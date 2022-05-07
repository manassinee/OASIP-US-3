const baseUrl = import.meta.env.PROD ? import.meta.env.VITE_API_URL : '/api';

function makeUrl(path) {
  return `${baseUrl}${path}`;
}

export async function getEvents() {
  const response = await fetch(makeUrl('/events'));
  if (response.status === 200) {
    return response.json()
  } else {
    console.log('Cannot fetch events');
  }
}