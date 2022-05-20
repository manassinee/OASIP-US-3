<script setup>
import { onBeforeMount, ref } from "vue";
import { createEvent, getCategories, getEventsByCategoryIdOnDate } from "../service/api";
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
const eventsForSelectedCategoryAndDate = ref([]);

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
    const hasOverlap = doesEventOverlap(eventStartTime, selectedCategory.eventDuration, eventsForSelectedCategoryAndDate.value);

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
  const hasOverlap = doesEventOverlap(eventStartTime, selectedCategory.eventDuration, eventsForSelectedCategoryAndDate.value);

  if (hasOverlap) {
    errors.value.hasOverlappingEvents = true;
  } else {
    errors.value.hasOverlappingEvents = false;
  }
}


// fetch scheduled events when startTime or categoryId changes
function doesEventOverlap(eventStartTime, duration, existingEvents) {
  const startTime = new Date(eventStartTime);
  const endTime = new Date(startTime);
  endTime.setMinutes(startTime.getMinutes() + duration);
  const formatter = Intl.DateTimeFormat([], { dateStyle: 'medium', timeStyle: 'short' })

  console.log(`=== checking overlap for ${formatter.format(startTime)} | ${formatter.format(endTime)} ===`);

  const overlapEvents = existingEvents.filter(event => {
    const otherStartTime = new Date(event.eventStartTime);
    const otherEndTime = new Date(event.eventStartTime);
    otherEndTime.setMinutes(otherEndTime.getMinutes() + event.eventDuration);

    // all overlap events. there are two scenarios:
    // 1. events that started before the startTime and ended after the startTime
    // 2. events that started between the startTime (inclusive) and the endTime (exclusive)
    // @Query(nativeQuery = true,
    //     value = "SELECT * FROM Event e WHERE " +
    //             "(e.eventStartTime < ?1 AND (e.eventStartTime + INTERVAL e.eventDuration MINUTE) > ?1) OR " +
    //             "(e.eventStartTime >= ?1 AND e.eventStartTime < ?2)")

    const isPastOverlap = otherStartTime.getTime() < startTime.getTime() && otherEndTime.getTime() > startTime.getTime();
    const isFutureOverlap = otherStartTime.getTime() >= startTime.getTime() && otherStartTime.getTime() < endTime.getTime();

    if (isPastOverlap || isFutureOverlap) {
      if (isPastOverlap) {
        console.log('> type: past overlap');
      }
      if (isFutureOverlap) {
        console.log('> type: future overlap');
      }

      console.log(`startTime: ${formatter.format(startTime)} | endTime: ${formatter.format(endTime)}`);
      console.log(`otherStartTime: ${formatter.format(otherStartTime)} | otherEndTime: ${formatter.format(otherEndTime)}`);
      return true;
    }

    return false;
  });

  if (overlapEvents.length === 0) {
    console.log(`no overlap at ${formatter.format(startTime)}`);
  }

  return overlapEvents.length > 0;
}

</script>
 
<template>
  <div>
    <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
      <label for="name">Booking Name </label>
      <input id="name" type="text" v-model="inputs.bookingName" required class="bg-gray-100 p-2"
        @input="validateBookingName">
      <div v-if="errors.bookingName.length > 0"
        class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.bookingName">{{ error }}</span>
      </div>

      <label for="email">Booking Email </label>
      <input id="email" type="email" v-model="inputs.bookingEmail" required class="bg-gray-100 p-2"
        @input="validateBookingEmail">
      <div v-if="errors.bookingEmail.length > 0"
        class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.bookingEmail">{{ error }}</span>
      </div>

      <label for="startTime">Event Start Time </label>
      <input id="startTime" type="datetime-local" :min="minDateTImeLocal" v-model="inputs.eventStartTime" required
        class="bg-gray-100 p-2" @input="validateStartTime">
      <div v-if="errors.eventStartTime.length > 0 || errors.hasOverlappingEvents"
        class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.eventStartTime">{{ error }}</span>
        <span v-if="errors.hasOverlappingEvents">Start time overlaps with other event(s)</span>
      </div>


      <label for="category">Event Category </label>
      <select v-model="inputs.eventCategoryId" required class="bg-gray-100 p-2" @change="validateCategoryId">
        <option v-for="category in categories" :value="category.id">{{ category.eventCategoryName }} - ({{
            category.eventDuration
        }} minutes)</option>
      </select>

      <label for="notes">Event Notes (optional)</label>
      <textarea id="notes" v-model="inputs.eventNotes" class="bg-gray-100 p-2" @input="validateEventNotes"></textarea>
      <div v-if="errors.eventNotes.length > 0"
        class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.eventNotes">{{ error }}</span>
      </div>

      <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">submit</button>
    </form>
  </div>
</template>
 
<style scoped>
</style>