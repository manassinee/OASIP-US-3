package int221.oasip.backendus3.services;

import int221.oasip.backendus3.controllers.OverlapEventException;
import int221.oasip.backendus3.dtos.CreateEventRequestDTO;
import int221.oasip.backendus3.dtos.EditEventRequestDTO;
import int221.oasip.backendus3.entities.Event;
import int221.oasip.backendus3.entities.EventCategory;
import int221.oasip.backendus3.repository.EventCategoryRepository;
import int221.oasip.backendus3.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventService {
    private EventRepository repository;
    private ModelMapper modelMapper;
    private EventCategoryRepository categoryRepository;

    public List<Event> getAll() {
        return repository.findAll();
    }

    public Event getEvent(Integer id){
        Optional<Event> event = repository.findById(id);

        return event.orElse(null);
    }

    public List<Event> getEventsInCategoryOnDateStartAt(Integer categoryId, Instant startAt) {
        return repository.findByCategoryIdWithDateRangeOfOneDayStartAt(categoryId, startAt);
    }

    public Event save(CreateEventRequestDTO newEvent) {
        Event e = modelMapper.map(newEvent, Event.class);
        Optional<EventCategory> ec = categoryRepository.findById(newEvent.getEventCategoryId());

        if (ec.isPresent()) {
            EventCategory category = ec.get();
            e.setEventCategory(category);
            e.setEventDuration(category.getEventDuration());
            e.setEventStartTime(Instant.from(newEvent.getEventStartTime()));

            Instant startTime = e.getEventStartTime();
            Instant endTime = startTime.plus(e.getEventDuration(), ChronoUnit.MINUTES);

            List<Event> overlapEvents = repository.findOverlapEventsByCategoryId(startTime, endTime, e.getEventCategory().getId());

            // check if there is overlap
            if (overlapEvents.size() > 0) {
                System.out.println("Overlapping events: ");
                System.out.println(overlapEvents);
                throw new OverlapEventException("eventStartTime", "Start time overlaps with another event(s)");
            }

            e.setId(null);
            System.out.println(e);
        } else {
            return null;
        }

        return repository.saveAndFlush(e);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Event update(Integer id, EditEventRequestDTO editEvent) {
        // get event
        Optional<Event> e = repository.findById(id);

        // set event fields
        if (e.isPresent()) {
            Event event = e.get();

            if (editEvent.getEventNotes() != null) {
                event.setEventNotes(editEvent.getEventNotes());
            }
            if (editEvent.getEventStartTime() != null) {
                event.setEventStartTime(Instant.from(editEvent.getEventStartTime()));
            }

            return repository.saveAndFlush(event);
        }

        return null;
    }

    public List<Event> getEventsInCategory(Integer categoryId) {
        return repository.findByEventCategory_Id(categoryId);
    }

    public List<Event> getUpcomingAndOngoingEvents(Integer categoryId) {
        Instant now = Instant.now();
        return repository.findUpcomingAndOngoingEvents(now, categoryId);
    }

    public List<Event> getUpcomingAndOngoingEvents() {
        return getUpcomingAndOngoingEvents(null);
    }

    public List<Event> getPastEvents(Integer categoryId) {
        Instant now = Instant.now();
        return repository.findPastEvents(now, categoryId);
    }

    public List<Event> getPastEvents() {
        return getPastEvents(null);
    }
}