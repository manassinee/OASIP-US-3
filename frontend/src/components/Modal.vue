<script setup>
import { computed } from '@vue/reactivity';

const props = defineProps({
  variant: {
    type: String,
    default: 'success',
  },
  title: {
    type: String,
    default: 'Title',
  },
  subtitle: {
    type: String,
    default: 'Subtitle',
  },
  buttonText: {
    type: String,
    default: 'OK',
  },
  isOpen: {
    type: Boolean,
    required: true,
  },
});

const iconClass = computed(() => {
  if (props.variant === 'success') {
    return 'text-green-400 border-green-400';
  }
  if (props.variant === 'error') {
    return 'text-rose-400 border-rose-400';
  }
});

const buttonClass = computed(() => {
  if (props.variant === 'success') {
    return 'bg-green-500 hover:bg-green-600';
  }
  if (props.variant === 'error') {
    return 'bg-rose-500 hover:bg-rose-600';
  }
});

const emits = defineEmits(['close'])
</script>
 
<template>
  <Teleport to="body">
    <div class="absolute inset-0 bg-black/50 flex text-slate-700 shadow-2xl" @click="$emit('close')"
      v-if="props.isOpen">
      <div class="m-auto -translate-y-8 pt-8 px-12 bg-white flex flex-col gap-4 max-w-lg rounded-lg overflow-hidden"
        @click.stop="">
        <div class="flex flex-col items-center gap-2">

          <div>
            <span class="material-symbols-outlined text-8xl border-4 rounded-full" :class="iconClass">
              {{ props.variant === 'success' ? 'done' : 'exclamation' }}
            </span>
          </div>

          <h1 class="font-semibold text-2xl">
            {{ props.title }}
          </h1>

          <p class="text-gray-500 text-center">
            {{ props.subtitle }}
          </p>

        </div>
        <div class="flex items-center justify-center p-4 border-t border-gray-200 w-full">
          <div class="flex gap-2">
            <button
              class="text-white font-medium py-2 px-12 rounded disabled:opacity-60 disabled:cursor-not-allowed flex-1"
              :class="buttonClass" @click="$emit('close')">
              {{ props.buttonText }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>
 
<style scoped>
</style>