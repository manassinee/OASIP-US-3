<script setup>
</script>
 
<template>
  <nav class="bg-white px-12 py-4 flex border-b border-gray-200">
    <div class="flex items-center gap-2 text-sm font-medium">
      <router-link to="/" class="py-2 px-4 rounded-md text-gray-700 hover:text-sky-600">Events</router-link>
      <router-link :to="{ name: 'createEvent' }" class="py-2 px-4 rounded-md text-gray-700 hover:text-sky-600">Create
        Event
      </router-link>
    </div>
  </nav>

  <router-view></router-view>
</template>
 
<style scoped>
.router-link-active {
  @apply text-sky-600 bg-sky-50;
}
</style>