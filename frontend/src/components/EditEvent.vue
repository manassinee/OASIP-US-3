<script setup>
import { computed } from '@vue/reactivity';
import { ref } from 'vue';
import { getEventsByCategoryIdOnDate } from "../service/api";
import { findOverlap, formatDateTimeLocal } from '../utils';
import Badge from './Badge.vue';

const props = defineProps({
  currentEvent: {
    type: Object,
    default: {},
  }
});

const inputs = ref({
  eventStartTime: formatDateTimeLocal(new Date(props.currentEvent.eventStartTime)),
  eventNotes: props.currentEvent.eventNotes,
});

const minDateTImeLocal = formatDateTimeLocal(new Date());

const emits = defineEmits([
  'save',
  'cancel'
]);

function makeDefaultValues() {
  return {
    eventStartTime: '',
    eventNotes: ''
  };
}

const errors = ref({
  eventStartTime: [],
  eventNotes: [],
  hasOverlappingEvents: false
});

const canSubmit = computed(() => {
  const noErrors = Object.values(errors.value).every((error) => error === false || error.length === 0);
  const inputsWithoutNotes = { ...inputs.value };
  delete inputsWithoutNotes.eventNotes;

  const noEmptyFields = Object.values(inputsWithoutNotes).every((value) => value !== '');

  return noErrors && noEmptyFields;
});

function handleSaveClick() {
  if (!canSubmit.value) {
    return;
  }

  const eventStartTime = new Date(inputs.value.eventStartTime).toISOString();
  const eventNotes = inputs.value.eventNotes;

  emits('save', { eventStartTime, eventNotes });
}

function validateEventNotes(e) {
  const eventNotes = e.target.value;
  errors.value.eventNotes = [];

  if (eventNotes.length > 500) {
    errors.value.eventNotes.push("Booking note must be less than 500 characters");
  }
}

const previousDate = ref(null);
const eventsForSelectedCategoryAndDate = ref([]);

async function validateStartTime() {
  const eventStartTime = inputs.value.eventStartTime;
  const eventCategoryId = props.currentEvent.eventCategory.id;
  const eventId = props.currentEvent.id;

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
    const selectedCategory = props.currentEvent.eventCategory;
    const overlapEvents = findOverlap(eventStartTime, selectedCategory.eventDuration, eventsForSelectedCategoryAndDate.value, eventId);
    const hasOverlap = overlapEvents.length > 0;

    if (hasOverlap) {
      errors.value.hasOverlappingEvents = true;
    }
  }
}
</script>
 
<template>
  <div
    class=" bg-white p-6 rounded-2xl flex flex-col gap-3 shadow-xl border-b-2 border-white/50 shadow-black/5 break-words w-full">
    <div>
      <p class="text-xl">{{ props.currentEvent.bookingName }}</p>
      <p class="text-gray-500">{{ props.currentEvent.bookingEmail }}</p>
    </div>

    <Badge :text="props.currentEvent.eventCategory.eventCategoryName" class="self-baseline mb-2" />

    <p class="text-gray-500 text-sm">{{ props.currentEvent.eventDuration }} {{ props.currentEvent.eventDuration > 1 ?
        'minutes' : 'minute'
    }}</p>

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
      <label for="notes" class="text-sm font-medium text-gray-700">Notes <span
          class="text-gray-400 font-normal">(optional)</span></label>
      <textarea id="notes" v-model="inputs.eventNotes" class="bg-gray-100 p-2 rounded" @input="validateEventNotes"
        placeholder="What's your event about?"></textarea>
      <div v-if="errors.eventNotes.length > 0"
        class="text-red-500 text-sm bg-red-50 py-1 px-2 mx-1 rounded-md flex flex-col">
        <span v-for="error in errors.eventNotes">{{ error }}</span>
      </div>
    </div>

    <div class="flex gap-2">
      <button class="bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded mt-2 flex-1"
        @click="$emit('cancel')">Cancel</button>

      <button type="submit"
        class="bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded disabled:opacity-60 disabled:cursor-not-allowed mt-2 flex-1"
        @click="handleSaveClick" :disabled="!canSubmit">Save</button>

    </div>
  </div>
</template>
 
<style>
.required::after {
  content: '*';
  @apply text-red-500 pl-1
}
</style>