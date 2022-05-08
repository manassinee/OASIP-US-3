const baseUrl = import.meta.env.PROD ? import.meta.env.VITE_API_URL : "/api";

function makeUrl(path) {
  return `${baseUrl}${path}`;
}

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
