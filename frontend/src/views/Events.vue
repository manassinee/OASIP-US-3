<script setup>
import { onBeforeMount, ref } from 'vue';
import EventPopup from '../components/EventPopup.vue';
import { getEvents } from '../service/api';
import { sortDescendingByDateInPlace } from '../utils';

const events = ref([])

onBeforeMount(async () => {
  const e = await getEvents();
  sortDescendingByDateInPlace(e, (e) => e.eventStartTime);

  events.value = e;
});

const currentEvent = ref({});
</script>

<template>
  <div class="p-10 max-w-[1024px] mx-auto">
    <div class="mb-4">All Events: {{ events.length }} events</div>
    <table class="table-auto w-full text-md text-left text-gray-500 relative z-0">
      <thead class="text-xs text-gray-700 uppercase bg-gray-50">
        <tr>
          <th class="px-6 py-3">Name</th>
          <th class="px-6 py-3">Start Time</th>
          <th class="px-6 py-3">Duration (mins)</th>
          <th class="px-6 py-3">Category</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="events.length > 0" v-for="event in events" @click="currentEvent = event"
          class="relative cursor-pointer hover:bg-gray-50 transition box-border" :class="[{
            'z-10 bg-pink-200/10 hover:bg-pink-200/20 ring-2 ring-pink-400/50': currentEvent.id === event.id
          }]">
          <td class="py-2 px-2">{{ event.bookingName }}</td>
          <td class="py-2 px-2">{{ new Date(event.eventStartTime).toLocaleString() }}</td>
          <td class="py-2 px-2">{{ event.eventDuration }}</td>
          <td class="py-2 px-2">{{ event.eventCategory.eventCategoryName }}</td>
        </tr>
        <tr v-else>
          <td colspan="5" class="p-6 text-center">No Scheduled Event</td>
        </tr>
      </tbody>
    </table>

    <EventPopup v-if="currentEvent.id" :name="currentEvent.bookingName" :email="currentEvent.bookingEmail"
      :startTime="currentEvent.eventStartTime" :duration="currentEvent.eventDuration"
      :category="currentEvent.eventCategory.eventCategoryName" :notes="currentEvent.eventNotes"
      @close="currentEvent = {}" />

  </div>

</template>

<style scoped>
</style>
