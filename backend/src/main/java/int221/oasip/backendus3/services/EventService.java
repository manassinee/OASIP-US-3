package int221.oasip.backendus3.services;

import int221.oasip.backendus3.entities.Event;
import int221.oasip.backendus3.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventService {
    private EventRepository repository;

    public List<Event> getAll() {
        List<Event> events = repository.findAll();
        return events;
    }

    public Event getEvent(Integer id){
        Optional<Event> event = repository.findById(id);

        return event.orElse(null);
    }
}
