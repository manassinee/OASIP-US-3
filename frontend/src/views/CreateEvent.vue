<script setup>
import { onBeforeMount, ref, watch } from "vue";
import { createEvent, getCategories } from "../service/api";
import { formatDateTimeLocal } from "../utils";

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

async function handleSubmit() {
  const canSubmit = Object.values(errors.value).every((error) => error.length === 0);

  if (!canSubmit) {
    return;
  }

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
  eventNotes: []
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

function validateStartTime(e) {
  const value = e.target.value;
  const now = new Date();
  const startTime = new Date(value);
  errors.value.eventStartTime = [];

  if (startTime.getTime() <= now.getTime()) {
    errors.value.eventStartTime.push("Start time must be in the future")
  }
}

function validateEventNotes(e) {
  const eventNotes = e.target.value;
  errors.value.eventNotes = [];

  if (eventNotes.length > 500) {
    errors.value.eventNotes.push("Booking note must be less than 500 characters");
  }
}

</script>
 
<template>
  <div>
    <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
      <label for="name">Booking Name </label>
      <input id="name" type="text" v-model="inputs.bookingName" required class="bg-gray-100 p-2"
      @input="validateBookingName">
      <div v-if="errors.bookingName.length > 0" class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.bookingName">{{ error }}</span>
      </div>

      <label for="email">Booking Email </label>
      <input id="email" type="email" v-model="inputs.bookingEmail" required class="bg-gray-100 p-2"
      @input="validateBookingEmail">
      <div v-if="errors.bookingEmail.length > 0" class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.bookingEmail">{{ error }}</span>
      </div>

      <label for="startTime">Event Start Time </label>
      <input id="startTime" type="datetime-local" :min="minDateTImeLocal" v-model="inputs.eventStartTime" required
        class="bg-gray-100 p-2" @input="validateStartTime">
      <div v-if="errors.eventStartTime.length > 0" class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.eventStartTime">{{ error }}</span>
      </div>


      <label for="category">Event Category </label>
      <select v-model="inputs.eventCategoryId" required class="bg-gray-100 p-2">
        <option v-for="category in categories" :value="category.id">{{ category.eventCategoryName }} - ({{
            category.eventDuration
        }} minutes)</option>
      </select>

      <label for="notes">Event Notes (optional)</label>
      <textarea id="notes" v-model="inputs.eventNotes" class="bg-gray-100 p-2"
       @input="validateEventNotes"></textarea>
      <div v-if="errors.eventNotes.length > 0" class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.eventNotes">{{ error }}</span>
      </div>

      <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">submit</button>
    </form>
  </div>
</template>
 
<style scoped>
</style>