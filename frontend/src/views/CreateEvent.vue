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
  const event = {
    ...inputs.value,

    // convert local time to UTC in ISO-8601 format
    eventStartTime: new Date(inputs.value.eventStartTime).toISOString(),
  };

  const createdEvent = await createEvent(event);
  if (createdEvent) {
    // clear inputs
    inputs.value = makeDefaultValues();
    alert('Successfully created the event');
  } else {
    alert('Sorry, something went wrong');
  }
}
</script>
 
<template>
  <div>
    <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
      <label for="name">Booking Name </label>
      <input id="name" type="text" v-model="inputs.bookingName" required class="bg-gray-100 p-2">

      <label for="email">Booking Email </label>
      <input id="email" type="email" v-model="inputs.bookingEmail" required class="bg-gray-100 p-2">

      <label for="startTime">Event Start Time </label>
      <input id="startTime" type="datetime-local" :min="minDateTImeLocal" v-model="inputs.eventStartTime" required
        class="bg-gray-100 p-2">

      <label for="category">Event Category </label>
      <select v-model="inputs.eventCategoryId" required class="bg-gray-100 p-2">
        <option v-for="category in categories" :value="category.id">{{ category.eventCategoryName }} - ({{
            category.eventDuration
        }} minutes)</option>
      </select>

      <label for="notes">Event Notes (optional)</label>
      <textarea id="notes" v-model="inputs.eventNotes" class="bg-gray-100 p-2"></textarea>

      <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">submit</button>
    </form>
  </div>
</template>
 
<style scoped>
</style>