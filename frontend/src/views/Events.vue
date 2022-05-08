<script setup>
import { onBeforeMount, ref } from "vue";
import EventPopup from "../components/EventPopup.vue";
import { getEvents, deleteEvent } from "../service/api";
import { sortDescendingByDateInPlace } from "../utils";

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
</script>

<template>
  <div class="p-10 max-w-[1024px] mx-auto">
    <div class="mb-4 font-semibold">All Events: {{ events.length }} events</div>
    <table
      class="table-auto w-full text-md text-left text-gray-500 relative z-0"
    >
      <thead class="text-xs text-sky-500 uppercase bg-sky-50">
        <tr class="text-sky-600">
          <th class="px-6 py-3">Name</th>
          <th class="px-6 py-3">Start Time</th>
          <th class="px-6 py-3">Duration (mins)</th>
          <th class="px-6 py-3">Category</th>
          <th class="px-6 py-3">Action</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-if="events.length > 0"
          v-for="event in events"
          @click="currentEvent = event"
          class="my-10 blockbg-white rounded-lg shadow-md relative cursor-pointer hover:bg-gray-50 transition box-border"
          :class="[
            {
              'z-10 bg-blue-200/10 hover:bg-blue-200/20 ring-2 ring-blue-400/50 ':
                currentEvent.id === event.id
            }
          ]"
        >
          <td class="py-2 px-2">{{ event.bookingName }}</td>
          <td class="py-2 px-2">
            {{ new Date(event.eventStartTime).toLocaleString() }}
          </td>
          <td class="py-2 px-2">{{ event.eventDuration }}</td>
          <td class="py-2 px-2">{{ event.eventCategory.eventCategoryName }}</td>
          <td class="py-2 px-2">
            <button class="relative text-red-400 z-50" @click="cancelEvent(event)">Delete</button>
          </td>
        </tr>
        <tr v-else>
          <td colspan="5" class="p-6 text-center">No Scheduled Event</td>
        </tr>
      </tbody>
    </table>

    <EventPopup
      v-if="currentEvent.id"
      :name="currentEvent.bookingName"
      :email="currentEvent.bookingEmail"
      :startTime="currentEvent.eventStartTime"
      :duration="currentEvent.eventDuration"
      :category="currentEvent.eventCategory.eventCategoryName"
      :notes="currentEvent.eventNotes"
      @close="currentEvent = {}"
    />
  </div>
</template>

<style scoped></style>
