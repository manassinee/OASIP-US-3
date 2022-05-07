<script setup>
const props = defineProps({
  name: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true
  },
  startTime: {
    type: String,
    required: true
  },
  duration: {
    type: Number,
    required: true
  },
  category: {
    type: String,
    required: true
  },
  notes: {
    type: String,
    required: false,
  }
})

const emits = defineEmits([
  'close'
]);

function formatDateAndFromToTime(date, durationMinute) {
  const from = new Date(date);
  const to = new Date(date);
  to.setMinutes(to.getMinutes() + durationMinute);
  return `${from.toLocaleDateString()} ${formatTime(from)} - ${formatTime(to)}`;
}

function formatTime(date) {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}
</script>
 
<template>
  <div class="absolute inset-0 flex pointer-events-none">
    <div
      class="z-50 m-auto pointer-events-auto bg-white -translate-y-2/4 p-8 rounded-2xl flex flex-col gap-3 shadow-xl border-b-2 border-white/50 shadow-black/5 min-w-[420px]">
      <!-- close button -->
      <div
        class="absolute top-1 right-1 mt-1 mr-1 cursor-pointer text-gray-500 hover:bg-gray-50 rounded-full w-10 h-10 transition flex justify-center items-center font-bold"
        @click="$emit('close')">
        ⨉
      </div>
      <div>
        <p class="text-xl">{{ props.name }}</p>
        <p class="text-gray-500">{{ props.email }}</p>
      </div>

      <div class="text-sm text-pink-600 font-semibold rounded-lg bg-pink-50 px-3 py self-baseline mb-2">{{
          props.category
      }}
      </div>

      <div>
        <p>{{ formatDateAndFromToTime(props.startTime, props.duration) }}</p>
        <p class="text-gray-500 text-sm">{{ props.duration }} {{ props.duration > 1 ? 'minutes' : 'minute' }}</p>
      </div>

      <div>
        <p class="text-gray-500 text-sm">Notes</p>
        <p>{{ props.notes || '–' }}</p>
      </div>
    </div>
  </div>
</template>
 
<style scoped>
</style>