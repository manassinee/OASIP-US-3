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
import java.time.LocalDate;
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
        List<Event> events = repository.findAll();
        return events;
    }

    public Event getEvent(Integer id){
        Optional<Event> event = repository.findById(id);

        return event.orElse(null);
    }

    public List<Event> getEventsByCategoryIdOnDate(Integer categoryId, LocalDate date) {
        return repository.findByCategoryIdOnDate(categoryId, date);
    }

    public Event save(CreateEventRequestDTO newEvent) {
        Event e = modelMapper.map(newEvent, Event.class);
        Optional<EventCategory> ec = categoryRepository.findById(newEvent.getEventCategoryId());

        if (ec.isPresent()) {
            EventCategory category = ec.get();
            e.setEventCategory(category);
            e.setEventDuration(category.getEventDuration());

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
                event.setEventStartTime(editEvent.getEventStartTime());
            }

            return repository.saveAndFlush(event);
        }

        return null;
    }
}