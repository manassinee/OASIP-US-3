package int221.oasip.backendus3.services;

import int221.oasip.backendus3.dtos.EditDTO;
import int221.oasip.backendus3.dtos.EventDTO;
import int221.oasip.backendus3.entities.Event;
import int221.oasip.backendus3.entities.EventCategory;
import int221.oasip.backendus3.repository.EventCategoryRepository;
import int221.oasip.backendus3.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    public Event save(EventDTO newEvent) {
        Event e = modelMapper.map(newEvent, Event.class);
        Optional<EventCategory> ec = categoryRepository.findById(newEvent.getEventCategoryId());

        if (ec.isPresent()) {
            EventCategory category = ec.get();
            e.setEventCategory(category);
            e.setEventDuration(category.getEventDuration());
            System.out.println(e);
            e.setId(null);
        } else {
            return null;
        }

        return repository.saveAndFlush(e);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Event update(Integer id, EditDTO editEvent) {
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