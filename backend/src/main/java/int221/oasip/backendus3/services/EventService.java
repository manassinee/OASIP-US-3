package int221.oasip.backendus3.services;

import int221.oasip.backendus3.dtos.CreateEventRequestDTO;
import int221.oasip.backendus3.dtos.EditEventRequestDTO;
import int221.oasip.backendus3.entities.Event;
import int221.oasip.backendus3.entities.EventCategory;
import int221.oasip.backendus3.exceptions.EntityNotFoundException;
import int221.oasip.backendus3.exceptions.EventOverlapException;
import int221.oasip.backendus3.repository.EventCategoryRepository;
import int221.oasip.backendus3.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
    private EventRepository repository;
    private ModelMapper modelMapper;
    private EventCategoryRepository categoryRepository;

    public List<Event> getAll() {
        return repository.findAll();
    }

    public Event getEvent(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<Event> getEventsOnDateStartAt(Instant startAt, Integer categoryId) {
        return repository.findByDateRangeOfOneDayStartAtAndCategoryId(startAt, categoryId);
    }

    public List<Event> getEventsOnDateStartAt(Instant startAt) {
        return repository.findByDateRangeOfOneDayStartAt(startAt);
    }

    public Event create(CreateEventRequestDTO newEvent) {
        Event e = modelMapper.map(newEvent, Event.class);
        EventCategory category = categoryRepository.findById(newEvent.getEventCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Event category with id " + newEvent.getEventCategoryId() + " not found"));

        e.setEventCategory(category);
        e.setEventDuration(category.getEventDuration());
        e.setEventStartTime(Instant.from(newEvent.getEventStartTime()));

        Instant startTime = e.getEventStartTime();
        Instant endTime = startTime.plus(e.getEventDuration(), ChronoUnit.MINUTES);

        List<Event> overlapEvents = repository.findOverlapEventsByCategoryId(startTime, endTime, e.getEventCategory().getId());

        if (overlapEvents.size() > 0) {
            System.out.println("Overlapping events: ");
            System.out.println(overlapEvents);
            throw new EventOverlapException();
        }

        e.setId(null);
        System.out.println(e);

        return repository.saveAndFlush(e);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Event update(Integer id, EditEventRequestDTO editEvent) {
        Event event = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));

        if (editEvent.getEventNotes() != null) {
            event.setEventNotes(editEvent.getEventNotes());
        }
        if (editEvent.getEventStartTime() != null) {
            event.setEventStartTime(Instant.from(editEvent.getEventStartTime()));
        }

        return repository.saveAndFlush(event);
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