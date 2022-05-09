<script setup>
import { onBeforeMount, ref } from "vue";
import EventDetails from "../components/EventDetails.vue";
import { deleteEvent, getEvents } from "../service/api";
import { sortDescendingByDateInPlace } from "../utils";
import { formatDate, formatTime } from '../utils';


const events = ref([]);

onBeforeMount(async () => {
  const e = await getEvents();
  sortDescendingByDateInPlace(e, (e) => e.eventStartTime);

  events.value = e;
});

const currentEvent = ref({});

async function cancelEvent(event) {
  if (!confirm(`Cancel event #${event.id} ${event.bookingName}?`)) {
    return;
  }

  const isSuccess = await deleteEvent(event.id);
  if (isSuccess) {
    alert(`Delete successfully`);
    events.value = events.value.filter((e) => e.id !== event.id);
  } else {
    alert('Sorry, something went wrong');
  }

}

function formatDateTime(date) {
  return `${formatDate(date)} ${formatTime(date)} `;
}
</script>

<template>
  <div class="py-8 px-12 max-w-[1440px] flex mx-auto">

    <div class="flex flex-col">
      <div class="mb-4 font-semibold">All Events: {{ events.length }} events</div>
      <div class="flex">
        <table class="table-fixed text-md text-left w-8/12 flex-1">

          <thead class="text-xs text-sky-500 uppercase bg-sky-50">
            <tr class="text-sky-600">
              <th class="px-6 py-3">Name</th>
              <th class="px-6 py-3">Schedule</th>
              <th class="px-6 py-3">Category</th>
              <th class="px-6 py-3">Action</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="events.length > 0" v-for="event in events" @click="currentEvent = event"
              class="my-10 blockbg-white rounded-lg shadow-md relative cursor-pointer hover:bg-gray-50 transition box-border"
              :class="[
                {
                  'z-10 bg-blue-200/10 hover:bg-blue-200/20 ring-2 ring-blue-400/50 ':
                    currentEvent.id === event.id,
                }
              ]">

              <td class="py-2 px-2">
                <span class="font-medium text-gray-800">{{ event.bookingName }}</span>
              </td>

              <td class="py-2 px-2">
                <div class="flex flex-col">
                  <span class="">{{ formatDateTime(new Date(event.eventStartTime)) }}</span>
                  <span class="text-sm text-gray-500">{{ event.eventDuration }} minutes</span>
                </div>
              </td>

              <td class="py-2 px-2">
                <div class="flex">
                  <div class="text-sm text-sky-600 font-semibold rounded-lg bg-sky-50 px-3">
                    {{ event.eventCategory.eventCategoryName }}
                  </div>
                </div>
              </td>

              <td class="py-2 px-2">
                <button class="text-red-400 z-50" @click.stop="cancelEvent(event)">Delete</button>
              </td>

            </tr>
            <tr v-else>
              <td colspan="5" class="p-6 text-center">No Scheduled Event</td>
            </tr>
          </tbody>

        </table>

        <div class="p-4 bg-gray-100 relative w-4/12" v-if="currentEvent.id">
          <EventDetails class="sticky top-24" :currentEvent="currentEvent" @close="currentEvent = {}" />
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
</style>
