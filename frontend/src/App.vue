<script setup>
import {onBeforeMount, ref} from 'vue';
const events = ref([])

//GET 
async function getEvents() {
  const response = await fetch('/api/events');
  if (response.status === 200) {
    const data = await response.json() 
    console.log(data);
    return data;
  } else {
    console.log('Cannot fetch events');
  }
}

onBeforeMount(async () => {
  events.value = await getEvents();
});


</script>

<template>
All Events: {{ events.length }} events
<!-- {{ events }} -->
<div v-if=" events.length > 0"
>
  <ul>
    <li v-for="event in events">
      {{ event.id }}
      {{ event.eventStartTime }}  
      {{ event.eventDuration }}
      {{ event.eventCategory.eventCategoryName }}
      {{ event.bookingName }}
      {{ event.bookingEmail }}
    </li>
  </ul>
</div>
<div v-else>No Scheduled Events</div >

</template>

<style>

</style>
