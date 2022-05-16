<script setup>
import { formatDateAndFromToTime, formatDateTimeLocal } from '../utils';
import Badge from './Badge.vue';
import { ref } from 'vue';

const props = defineProps({
  currentEvent: {
    type: Object,
    default: {},
  }
});

const event = ref({
  eventStartTime: formatDateTimeLocal(new Date(props.currentEvent.eventStartTime)),
  eventNotes: props.currentEvent.eventNotes,
});

const minDateTImeLocal = formatDateTimeLocal(new Date());

console.log(event.value);


const emits = defineEmits([
  'close',
  'save'
]);

function makeDefaultValues() {
  return {
    eventStartTime: '',
    eventNotes: ''
  };
}
</script>
 
<template>

  <div
    class=" bg-white p-6 rounded-2xl flex flex-col gap-3 shadow-xl border-b-2 border-white/50 shadow-black/5 break-words w-full">
    <!-- close button -->
    <div
      class="absolute top-1 right-1 mt-1 mr-1 cursor-pointer text-gray-500 hover:bg-gray-50 rounded-full w-10 h-10 transition flex justify-center items-center font-bold"
      @click="$emit('close')">
      ⨉
    </div>
    <div>
      <p class="text-xl">{{ props.currentEvent.bookingName }}</p>
      <p class="text-gray-500">{{ props.currentEvent.bookingEmail }}</p>
    </div>

    <Badge :text="props.currentEvent.eventCategory.eventCategoryName" class="self-baseline mb-2" />

    <div>
      <input id="startTime" type="datetime-local" :min="minDateTImeLocal" v-model="event.eventStartTime" required
        class="bg-gray-100 p-2">
      <p class="text-gray-500 text-sm">{{ props.currentEvent.eventDuration }} {{ props.currentEvent.eventDuration > 1 ?
          'minutes' : 'minute'
      }}</p>
    </div>

    <div>
      <p class="text-gray-500 text-sm">Notes</p>
      <textarea id="notes" v-model="event.eventNotes" class="bg-gray-100 p-2"></textarea>
    </div>

    <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" @click="$emit('save', {
      ...event,
      eventStartTime: new Date(event.eventStartTime).toISOString()
    })">submit</button>
  </div>
</template>
 
<style>
</style>