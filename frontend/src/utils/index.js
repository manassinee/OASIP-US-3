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
  return date.toLocaleDateString([], {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

export function findOverlap(eventStartTime, duration, existingEvents, eventId) {
  console.log("findOverlap", eventStartTime, duration, existingEvents, eventId);
  const startTime = new Date(eventStartTime);
  const endTime = new Date(startTime);
  endTime.setMinutes(startTime.getMinutes() + duration);
  const formatter = Intl.DateTimeFormat([], { dateStyle: 'medium', timeStyle: 'short' })

  console.log(`=== checking overlap for ${formatter.format(startTime)} | ${formatter.format(endTime)} ===`);

  const overlapEvents = existingEvents.filter(event => {
    const otherStartTime = new Date(event.eventStartTime);
    const otherEndTime = new Date(event.eventStartTime);
    otherEndTime.setMinutes(otherEndTime.getMinutes() + event.eventDuration);

    if (eventId && eventId === event.eventId) {
      return false;
    }

    // all overlap events. there are two scenarios:
    // 1. events that started before the startTime and ended after the startTime
    // 2. events that started between the startTime (inclusive) and the endTime (exclusive)
    const isPastOverlap = otherStartTime.getTime() < startTime.getTime() && otherEndTime.getTime() > startTime.getTime();
    const isFutureOverlap = otherStartTime.getTime() >= startTime.getTime() && otherStartTime.getTime() < endTime.getTime();

    if (isPastOverlap || isFutureOverlap) {
      if (isPastOverlap) {
        console.log('> type: past overlap');
      }
      if (isFutureOverlap) {
        console.log('> type: future overlap');
      }

      console.log(`startTime: ${formatter.format(startTime)} | endTime: ${formatter.format(endTime)}`);
      console.log(`otherStartTime: ${formatter.format(otherStartTime)} | otherEndTime: ${formatter.format(otherEndTime)}`);
      return true;
    }

    return false;
  });

  if (overlapEvents.length === 0) {
    console.log(`no overlap at ${formatter.format(startTime)}`);
  }

  return overlapEvents;
}