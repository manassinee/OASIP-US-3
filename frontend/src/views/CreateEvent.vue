<script setup>
import { onBeforeMount, ref, watch } from "vue";
import { getCategories, createEvent } from "../service/api";
import { formatDateTimeLocal } from "../utils";

const bookingName = ref('');
const bookingEmail = ref('')
const eventStartTime = ref('')
const eventCategory = ref('');
const eventNotes = ref('');

const categories = ref([]);

onBeforeMount(async () => {
  categories.value = await getCategories();
});

// format: 2022-02-02T02:02
const minDateTImeLocal = formatDateTimeLocal(new Date());

async function handleSubmit() {
  const event = {
    bookingName: bookingName.value,
    bookingEmail: bookingEmail.value,
    // from 2022-02-02T02:02 to 2022-02-02T02:02:00Z (append the ':00Z')
    eventStartTime: new Date(eventStartTime.value).toISOString(),
    eventCategoryId: eventCategory.value.id,
    eventNotes: eventNotes.value
  }

  const createdEvent = await createEvent(event);
  if (createdEvent) {
      bookingName = '';
      bookingEmail = '';
      eventStartTime = '';
      eventCategoryId = '';
      eventNotes = '';
  } else {
    alert('Sorry, something went wrong');
  }
}

watch(eventStartTime, (d) => {
  console.log(d);
});
</script>
 
<template>
<div>
<form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
  <label for="name">Booking Name </label>
  <input id="name" type="text" v-model="bookingName" required class="bg-gray-100 p-2">
  
  <label for="email">Booking Email </label>
  <input id="email" type="email" v-model="bookingEmail" required class="bg-gray-100 p-2">
  
  <label for="startTime">Event Start Time </label>
  <input id="startTime" type="datetime-local" :min="minDateTImeLocal" v-model="eventStartTime" required class="bg-gray-100 p-2">
      
  <label for="category">Event Category </label>
  <select v-model="eventCategory" class="bg-gray-100 p-2">
    <option v-for="category in categories" :value="category">{{ category.eventCategoryName }} - ({{ category.eventDuration }} minutes)</option>
  </select>

  <label for="notes">Event Notes (optional)</label>
  <textarea id="notes" v-model="eventNotes" class="bg-gray-100 p-2"></textarea>
  
  <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">submit</button>
</form>
</div>
</template>
 
<style scoped>

</style>