<script setup>
import { computed } from "@vue/reactivity";
import { onBeforeMount, ref } from "vue";
import { createEvent, getCategories, getEventsByCategoryIdOnDate } from "../service/api";
import { findOverlap, formatDateTimeLocal } from "../utils";

// init inputs with default values
const inputs = ref(makeDefaultValues());
const categories = ref([]);

function makeDefaultValues() {
  return {
    bookingName: '',
    bookingEmail: '',
    eventStartTime: '',
    eventCategoryId: '',
    eventNotes: ''
  };
}

onBeforeMount(async () => {
  categories.value = await getCategories();
});


// format: 2022-02-02T02:02
const minDateTImeLocal = formatDateTimeLocal(new Date());
const eventsForSelectedCategoryAndDate = ref([]);

const canSubmit = computed(() => {
  const noErrors = Object.values(errors.value).every((error) => error === false || error.length === 0);
  const inputsWithoutNotes = { ...inputs.value };
  delete inputsWithoutNotes.eventNotes;

  const noEmptyFields = Object.values(inputsWithoutNotes).every((value) => value !== '');

  return noErrors && noEmptyFields;
});

async function handleSubmit() {
  const event = {
    ...inputs.value,

    // convert local time to UTC in ISO-8601 format
    eventStartTime: new Date(inputs.value.eventStartTime).toISOString(),
  };

  try {
    const createdEvent = await createEvent(event);

    if (createdEvent) {
      // clear inputs
      inputs.value = makeDefaultValues();
      alert('Successfully created the event');
    } else {
      alert('Sorry, something went wrong');
    }
  } catch (errorResponse) {
    if (errorResponse.status !== 400) {
      return;
    }

    Object.assign(errors.value, errorResponse.errors);
  }
}


const errors = ref({
  bookingName: [],
  bookingEmail: [],
  eventStartTime: [],
  eventNotes: [],
  hasOverlappingEvents: false
});

function validateBookingName(e) {
  const bookingName = e.target.value;
  errors.value.bookingName = [];

  if (bookingName.length > 100) {
    errors.value.bookingName.push("Booking name must be less than 100 characters");
  }

  if (bookingName.trim().length === 0) {
    errors.value.bookingName.push("Booking name must not be blank");
  }
}

function validateBookingEmail(e) {
  const bookingEmail = e.target.value;
  errors.value.bookingEmail = [];

  if (bookingEmail.length > 50) {
    errors.value.bookingEmail.push("Booking email must be less than 50 characters");
  }

  if (bookingEmail.trim().length === 0) {
    errors.value.bookingEmail.push("Booking email must not be blank");
  }

  // RFC2822 https://regexr.com/2rhq7
  const emailRegex = /^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/;
  if (!emailRegex.test(bookingEmail)) {
    errors.value.bookingEmail.push("Booking email is invalid");
  }
}

function validateEventNotes(e) {
  const eventNotes = e.target.value;
  errors.value.eventNotes = [];

  if (eventNotes.length > 500) {
    errors.value.eventNotes.push("Booking note must be less than 500 characters");
  }
}


const previousDate = ref(null);

async function validateStartTime() {
  const eventStartTime = inputs.value.eventStartTime;
  const eventCategoryId = inputs.value.eventCategoryId;

  if (!eventStartTime) {
    return;
  }

  const now = new Date();
  const startTime = new Date(eventStartTime);

  errors.value.eventStartTime = [];
  errors.value.hasOverlappingEvents = false;

  if (startTime.getTime() <= now.getTime()) {
    errors.value.eventStartTime.push("Start time must be in the future")
  }

  const date = eventStartTime.split('T')[0];
  if (date !== previousDate.value) {
    console.log('date changed', date);

    if (eventCategoryId) {
      const dateMidnight = new Date(eventStartTime);
      dateMidnight.setHours(0, 0, 0, 0);

      eventsForSelectedCategoryAndDate.value = await getEventsByCategoryIdOnDate(eventCategoryId, dateMidnight.toISOString());
      console.log('fetched events (start time changed)', eventsForSelectedCategoryAndDate.value);
    }
  }

  previousDate.value = date;

  if (eventCategoryId) {
    const selectedCategory = categories.value.find((category) => category.id === eventCategoryId);
    const overlapEvents = findOverlap(eventStartTime, selectedCategory.eventDuration, eventsForSelectedCategoryAndDate.value);
    const hasOverlap = overlapEvents.length > 0;

    if (hasOverlap) {
      errors.value.hasOverlappingEvents = true;
    }
  }
}

async function validateCategoryId() {
  const eventStartTime = inputs.value.eventStartTime;
  const eventCategoryId = inputs.value.eventCategoryId;

  if (!eventStartTime || !eventCategoryId) {
    return;
  }

  const dateMidnight = new Date(eventStartTime);
  dateMidnight.setHours(0, 0, 0, 0);

  eventsForSelectedCategoryAndDate.value = await getEventsByCategoryIdOnDate(eventCategoryId, dateMidnight.toISOString());
  console.log('fetched events (category id changed)', eventsForSelectedCategoryAndDate.value);

  const selectedCategory = categories.value.find((category) => category.id === eventCategoryId);
  const overlapEvents = findOverlap(eventStartTime, selectedCategory.eventDuration, eventsForSelectedCategoryAndDate.value);
  const hasOverlap = overlapEvents.length > 0;

  if (hasOverlap) {
    errors.value.hasOverlappingEvents = true;
  } else {
    errors.value.hasOverlappingEvents = false;
  }
}
</script>
 
<template>
  <div class="max-w-md mx-auto mt-8">
    <form @submit.prevent="handleSubmit"
      class="flex flex-col gap-4 bg-white py-10 px-8 border border-gray-100 rounded-xl shadow-xl shadow-black/5">

      <div class="flex flex-col text-center mb-4 text-gray-700">
        <h1 class="font-medium text-2xl">Create Event</h1>
      </div>

      <div class="flex flex-col gap-2">
        <label for="name" class="required text-sm font-medium text-gray-700">Booking Name</label>
        <input id="name" type="text" v-model="inputs.bookingName" required class="bg-gray-100 p-2 rounded"
          @input="validateBookingName" placeholder="What's your booking name?">
        <div v-if="errors.bookingName.length > 0"
          class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
          <span v-for="error in errors.bookingName">{{ error }}</span>
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <label for="email" class="required text-sm font-medium text-gray-700">Booking Email</label>
        <input id="email" type="email" v-model="inputs.bookingEmail" required class="bg-gray-100 p-2 rounded"
          @input="validateBookingEmail" placeholder="What's your email?">
        <div v-if="errors.bookingEmail.length > 0"
          class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
          <span v-for="error in errors.bookingEmail">{{ error }}</span>
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <label for="startTime" class="required text-sm font-medium text-gray-700">Start Time</label>
        <input id="startTime" type="datetime-local" :min="minDateTImeLocal" v-model="inputs.eventStartTime" required
          class="bg-gray-100 p-2 rounded" @input="validateStartTime">
        <div v-if="errors.eventStartTime.length > 0 || errors.hasOverlappingEvents"
          class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
          <span v-for="error in errors.eventStartTime">{{ error }}</span>
          <span v-if="errors.hasOverlappingEvents">Start time overlaps with other event(s)</span>
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <label for="category" class="required text-sm font-medium text-gray-700">Category</label>
        <select v-model="inputs.eventCategoryId" required class="bg-gray-100 p-2 rounded" @change="validateCategoryId"
          id="category">
          <option disabled selected value="">Select event category</option>
          <option v-for="category in categories" :value="category.id">{{ category.eventCategoryName }} ({{
              category.eventDuration
          }} min.)</option>
        </select>
      </div>

      <div class="flex flex-col gap-2">
        <label for="notes" class="text-sm font-medium text-gray-700">Notes <span
            class="text-gray-400 font-normal">(optional)</span></label>
        <textarea id="notes" v-model="inputs.eventNotes" class="bg-gray-100 p-2 rounded" @input="validateEventNotes"
          placeholder="What's your event about?"></textarea>
        <div v-if="errors.eventNotes.length > 0"
          class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
          <span v-for="error in errors.eventNotes">{{ error }}</span>
        </div>
      </div>

      <button type="submit"
        class="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded disabled:opacity-60 disabled:cursor-not-allowed mt-2"
        :disabled="!canSubmit">Create
        Event</button>
    </form>
  </div>
</template>
 
<style scoped>
.required::after {
  content: '*';
  @apply text-red-500 pl-1
}
</style>