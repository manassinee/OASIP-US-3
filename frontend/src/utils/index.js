export function sortDescendingByDateInPlace(arr, keyExtractor) {
  return arr.sort((a, b) => {
    return new Date(keyExtractor(b)).getTime() - new Date(keyExtractor(a)).getTime();
  });
}

export function formatDateTimeLocal(date) {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes();

  const formatted = `${year}-${month < 10 ? '0' : ''}${month}-${day < 10 ? '0' : ''}${day}T${hours < 10 ? '0' : ''}${hours}:${minutes < 10 ? '0' : ''}${minutes}`;
  return formatted;
}

export function formatDateAndFromToTime(date, durationMinute) {
  const from = new Date(date);
  const to = new Date(date);
  to.setMinutes(to.getMinutes() + durationMinute);
  return `${formatDate(from)} ${formatTime(from)} - ${formatTime(to)}`;
}

export function formatTime(date) {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

export function formatDate(date) {
  return date.toLocaleDateString();
}