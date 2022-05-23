package int221.oasip.backendus3.services;

import int221.oasip.backendus3.dtos.CreateEventRequestDTO;
import int221.oasip.backendus3.dtos.EditEventRequestDTO;
import int221.oasip.backendus3.dtos.EventResponseDTO;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    private EventRepository repository;
    private ModelMapper modelMapper;
    private EventCategoryRepository categoryRepository;

    private List<EventResponseDTO> mapListToDTO(List<Event> events) {
        return events.stream().map(event -> modelMapper.map(event, EventResponseDTO.class)).collect(Collectors.toList());
    }

    public List<EventResponseDTO> getAll() {
        List<Event> events = repository.findAll();
        return mapListToDTO(events);
    }

    public EventResponseDTO getEvent(Integer id) {
        Event event = repository.findById(id).orElse(null);

        if (event == null) {
            return null;
        }

        return modelMapper.map(event, EventResponseDTO.class);
    }

    public List<EventResponseDTO> getEventsOnDateStartAt(Instant startAt, Integer categoryId) {
        List<Event> events = repository.findByDateRangeOfOneDayStartAtAndCategoryId(startAt, categoryId);
        return mapListToDTO(events);
    }

    public List<EventResponseDTO> getEventsOnDateStartAt(Instant startAt) {
        List<Event> events = repository.findByDateRangeOfOneDayStartAt(startAt);
        return mapListToDTO(events);
    }

    public EventResponseDTO create(CreateEventRequestDTO newEvent) {
        Event e = new Event();
        EventCategory category = categoryRepository.findById(newEvent.getEventCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Event category with id " + newEvent.getEventCategoryId() + " not found"));

        e.setBookingName(newEvent.getBookingName().strip());
        e.setBookingEmail(newEvent.getBookingEmail().strip());
        e.setEventStartTime(Instant.from(newEvent.getEventStartTime()));
        e.setEventNotes(newEvent.getEventNotes().strip());
        e.setEventCategory(category);
        e.setEventDuration(category.getEventDuration());

        Instant startTime = e.getEventStartTime();
        Instant endTime = startTime.plus(e.getEventDuration(), ChronoUnit.MINUTES);

        List<Event> overlapEvents = repository.findOverlapEventsByCategoryId(startTime, endTime, e.getEventCategory().getId());

        if (overlapEvents.size() > 0) {
            throw new EventOverlapException();
        }

        e.setId(null);

        return modelMapper.map(repository.saveAndFlush(e), EventResponseDTO.class);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public EventResponseDTO update(Integer id, EditEventRequestDTO editEvent) {
        Event event = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));

        if (editEvent.getEventNotes() != null) {
            event.setEventNotes(editEvent.getEventNotes().strip());
        }

        if (editEvent.getEventStartTime() != null) {
            Instant startTime = Instant.from(editEvent.getEventStartTime());
            Instant endTime = startTime.plus(event.getEventDuration(), ChronoUnit.MINUTES);
            Integer categoryId = event.getEventCategory().getId();
            Integer eventId = event.getId();

            List<Event> overlapEvents = repository.findOverlapEventsByCategoryId(startTime, endTime, categoryId, eventId);

            if (overlapEvents.size() > 0) {
                throw new EventOverlapException();
            } else {
                event.setEventStartTime(startTime);
            }
        }

        return modelMapper.map(repository.saveAndFlush(event), EventResponseDTO.class);
    }

    public List<EventResponseDTO> getEventsInCategory(Integer categoryId) {
        List<Event> events = repository.findByEventCategory_Id(categoryId);
        return mapListToDTO(events);
    }

    public List<EventResponseDTO> getUpcomingAndOngoingEvents(Integer categoryId) {
        Instant now = Instant.now();
        List<Event> events = repository.findUpcomingAndOngoingEvents(now, categoryId);
        return mapListToDTO(events);
    }

    public List<EventResponseDTO> getUpcomingAndOngoingEvents() {
        return getUpcomingAndOngoingEvents(null);
    }

    public List<EventResponseDTO> getPastEvents(Integer categoryId) {
        Instant now = Instant.now();
        List<Event> events = repository.findPastEvents(now, categoryId);
        return mapListToDTO(events);
    }

    public List<EventResponseDTO> getPastEvents() {
        return getPastEvents(null);
    }
}