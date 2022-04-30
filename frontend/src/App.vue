<script setup>
import {onBeforeMount, ref} from 'vue';
const events = ref([])

//GET 
async function getEvents() {
  const response = await fetch('/api/events');
  if (response.status === 200) {
    const data = await response.json() 
    return data;
  } else {
    console.log('Cannot fetch events');
  }
}

onBeforeMount(async () => {
  const e = await getEvents();
  e.sort((a, b) => { 
    return new Date(b.eventStartTime).getTime() - new Date(a.eventStartTime).getTime();
  });

  events.value = e;
  // console.log(e);
});

const currentEvent = ref({});
</script>

<template>
All Events: {{ events.length }} events
<!-- {{ events }} -->
<div v-if=" events.length > 0"
>
  <ul>
    <li v-for="event in events" @click="currentEvent = event.id">
      {{ event.id }}
      {{ event.eventStartTime }}
      {{ event.eventDuration }}
      {{ event.eventCategory.eventCategoryName }}
      {{ event.bookingName }}
      {{ event.bookingEmail }}
      <span v-if="currentEvent === event.id">{{ event.eventNotes }}</span>
    </li>
  </ul>
</div>
<div v-else>No Scheduled Events</div >

</template>

<style>

</style>
