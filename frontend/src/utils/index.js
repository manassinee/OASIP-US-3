export function sortDescendingByDateInPlace(arr, keyExtractor) {
  return arr.sort((a, b) => {
    return new Date(keyExtractor(b)).getTime() - new Date(keyExtractor(a)).getTime();
  });
}

export function formatDateTimeLocal(date) {
  const copiedDate = new Date(date);
  const offset = copiedDate.getTimezoneOffset();
  // add the offset before converting to UTC, result in local time
  // offset is -420 min. for UTC+7, thus --420 = +420
  copiedDate.setMinutes(copiedDate.getMinutes() - offset, 0, 0);
  return copiedDate.toISOString().slice(0, -1);
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