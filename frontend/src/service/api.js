const baseUrl = import.meta.env.PROD ? import.meta.env.VITE_API_URL : "/api";

function makeUrl(path) {
  return `${baseUrl}${path}`;
}

//GET
export async function getEvents() {
  const response = await fetch(makeUrl("/events"));
  if (response.status === 200) {
    const events = response.json();
    return events;
  } else {
    console.log("Cannot fetch events");
  }
}

export async function getCategories() {
  const response = await fetch(makeUrl("/categories"));
  if (response.status === 200) {
    const categories = response.json();
    return categories;
  } else {
    console.log("Cannot fetch events");
  }
}

//CREATE
export async function createEvent(newEvent) {
  const response = await fetch(makeUrl("/events"), {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(newEvent),
  });
  if (response.status === 201) {
    const addedEvent = await response.json();
    return addedEvent;
  } else {
    console.log("Cannot create event");
  }
}

//DELETE
export async function deleteEvent(id) {
  const response = await fetch(makeUrl(`/events/${id}`), {
    method: "DELETE"
  });

  if (response.status === 200) {
    return true;
  } else {
    console.log("Cannot delete event");
    return false;
  }
}

//UPDATE
export async function updateEvent(id, editEvent) {
  const response = await fetch(makeUrl(`/events/${id}`), {
    method: "PUT",
    headers: {
      "content-type": "application/json"
    },
    body: JSON.stringify(editEvent)
  });
  if (response.status === 200) {
    const updatedEvent = await response.json();
    return updatedEvent;
  } else {
    console.log("Cannot edit event");
  }
}