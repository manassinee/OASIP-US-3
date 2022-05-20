<script setup>
import { onBeforeMount, ref } from "vue";
import Badge from "../components/Badge.vue";
import EditEvent from "../components/EditEvent.vue";
import EventDetails from "../components/EventDetails.vue";
import { deleteEvent, getCategories, getEvents, getEventsByFilter, updateEvent } from "../service/api";
import { formatDate, formatTime, sortDescendingByDateInPlace } from "../utils";


const events = ref([]);
const currentEvent = ref({});
const categories = ref([]);

const eventTypes = {
  UPCOMING: "upcoming",
  PAST: "past",
  ALL: null
};

const categoryTypes = {
  ALL: null,
}

const filter = ref({
  categoryId: categoryTypes.ALL,
  type: eventTypes.ALL,
  date: ''
});

onBeforeMount(async () => {
  const events = await getEvents();
  setEvents(events);

  categories.value = await getCategories();
});

function setEvents(_events) {
  sortDescendingByDateInPlace(_events, (e) => e.eventStartTime);
  events.value = _events;
}


async function cancelEvent(event) {
  if (isEditing.value) {
    return;
  }
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

function selectEvent(event) {
  if (isEditing.value) {
    return;
  }
  currentEvent.value = event;
}

const isEditing = ref(false)
function startEdit(event) {
  if (isEditing.value) {
    return;
  }
  currentEvent.value = event;
  isEditing.value = true;
}

function stopEdit() {
  currentEvent.value = {};
  isEditing.value = false;
}

function formatDateTime(date) {
  return `${formatDate(date)} ${formatTime(date)} `;
}

async function saveEvent(updates) {
  const selectedEventId = currentEvent.value.id;

  if (new Date(updates.eventStartTime).getTime() !== new Date(currentEvent.value.eventStartTime).getTime() ||
    updates.eventNotes !== currentEvent.value.eventNotes) {
    const updatedEvent = await updateEvent(selectedEventId, updates);
    if (updatedEvent) {
      const event = events.value.find((e) => e.id === selectedEventId);
      event.eventStartTime = updatedEvent.eventStartTime;
      event.eventNotes = updatedEvent.eventNotes;

    }
  }

  isEditing.value = false;
}

async function filterEvents() {
  const categoryId = filter.value.categoryId;
  const type = filter.value.type;
  const date = filter.value.date;
  const _filter = {
    categoryId,
    type,
  };

  // add startAt only if all type is selected
  if (date && type === eventTypes.ALL) {
    const localDate = `${filter.value.date}T00:00:00`;
    const startAt = new Date(localDate);
    _filter.startAt = startAt.toISOString();
  }

  const events = await getEventsByFilter(_filter);
  setEvents(events);
}
</script>

<template>
  <div class="py-8 px-12 max-w-[1440px] flex mx-auto">

    <div class="flex flex-col">
      <div class="flex justify-between mb-4">
        <div class="mb-4 font-semibold">All Events: {{ events.length }} events</div>
        <div class="flex gap-6 flex-wrap">

          <div class="flex flex-col gap-1">
            <label class="text-xs text-gray-600">Category</label>
            <select v-model="filter.categoryId" class="text-sm bg-gray-100 p-1 self-baseline" @change="filterEvents">
              <option :value="categoryTypes.ALL">All</option>
              <option v-for="category in categories" :value="category.id">{{ category.eventCategoryName }}</option>
            </select>
          </div>

          <div class="flex gap-2">
            <div class="flex flex-col gap-1">
              <label class="text-xs text-gray-600">Type</label>
              <select v-model="filter.type" class="text-sm bg-gray-100 p-1" @change="filterEvents">
                <option :value="eventTypes.ALL">All</option>
                <option :value="eventTypes.UPCOMING">Upcoming</option>
                <option :value="eventTypes.PAST">Past</option>
              </select>
            </div>

            <div class="flex flex-col gap-1">
              <label class="text-xs text-gray-600">Date</label>
              <input v-model="filter.date" class="text-sm bg-gray-100 p-1 disabled:bg-gray-200 disabled:text-gray-400"
                type="date" @change="filterEvents" :disabled="filter.type !== eventTypes.ALL">
            </div>
          </div>

        </div>
      </div>
      <div class="flex">
        <table class="table-fixed text-left w-8/12 flex-1 break-words">

          <thead class="text-xs text-sky-500 uppercase bg-sky-50 text-left">
            <tr class="text-sky-600">
              <th class="pl-2 py-3">Name</th>
              <th class="pl-2 py-3">Date & Time</th>
              <th class="pl-2 py-3">Category</th>
              <th class="pl-2 py-3">Action</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="events.length > 0" v-for="event in events" @click="selectEvent(event)"
              class="my-10 bg-white rounded-lg border-b border-gray-200 shadow-black/5 relative cursor-pointer hover:bg-gray-50 transition box-border"
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
                  <Badge :text="event.eventCategory.eventCategoryName" />
                </div>
              </td>

              <td class="py-2 px-2">
                <div class="flex space-x-4">
                  <button @click.stop="cancelEvent(event)"
                    class="bg-white text-red-500 text-xs flex items-center border border-rose-500 px-1 py-0.5 rounded-md hover:bg-rose-500 hover:text-white transition">
                    <span class="material-symbols-outlined">
                      delete
                    </span>
                    <span>Delete</span>
                  </button>

                  <button @click.stop="startEdit(event)"
                    class="bg-white text-amber-500 text-xs flex items-center border border-yellow-500 px-1 py-0.5 rounded-md hover:bg-yellow-500 hover:text-white transition">
                    <span class="material-symbols-outlined">
                      edit
                    </span>
                    <span>Edit</span>
                  </button>
                </div>
              </td>

            </tr>
            <tr v-else>
              <td colspan="4" class="p-6 text-center">
                <span v-if="filter.type === eventTypes.UPCOMING">No On-Going or Upcoming Events</span>
                <span v-else-if="filter.type === eventTypes.PAST">No Past Events</span>
                <span v-else>No Scheduled Event</span>
              </td>
            </tr>
          </tbody>

        </table>

        <div class="p-4 bg-gray-100 relative w-4/12" v-if="currentEvent.id">
          <EditEvent class="sticky top-24" :currentEvent="currentEvent" @cancel="stopEdit" v-if="isEditing"
            @save="saveEvent" />
          <EventDetails class="sticky top-24" :currentEvent="currentEvent" @close="currentEvent = {}" v-else />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
