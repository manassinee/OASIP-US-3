<script setup>
</script>
 
<template>
  <nav class="bg-white px-10 py-4 flex">
    <div class="flex gap-6 text-sm font-medium">
      <router-link to="/" class="text-gray-700 hover:text-pink-600">Home</router-link>
      <router-link :to="{ name: 'createEvent' }" class="text-gray-700 hover:text-pink-600">Create Event</router-link>
    </div>
  </nav>

  <router-view></router-view>
</template>
 
<style scoped>
.router-link-active {
  @apply text-pink-600;
}
</style>