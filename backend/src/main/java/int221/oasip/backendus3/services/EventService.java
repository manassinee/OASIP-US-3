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
import int221.oasip.backendus3.utils.ModelMapperUtils;
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
    private ModelMapperUtils modelMapperUtils;
    private EventCategoryRepository categoryRepository;

    public List<EventResponseDTO> getAll() {
        List<Event> events = repository.findAll();
        return modelMapperUtils.mapList(events, EventResponseDTO.class);
    }

    public EventResponseDTO getEvent(Integer id) {
        Event event = repository.findById(id).orElse(null);

        if (event == null) {
            return null;
        }

        return modelMapper.map(event, EventResponseDTO.class);
    }

    public EventResponseDTO create(CreateEventRequestDTO newEvent) {
        Event e = new Event();
        EventCategory category = categoryRepository.findById(newEvent.getEventCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Event category with id " + newEvent.getEventCategoryId() + " not found"));

        e.setBookingName(newEvent.getBookingName().strip());
        e.setBookingEmail(newEvent.getBookingEmail().strip());
        e.setEventStartTime(Instant.from(newEvent.getEventStartTime()));
        e.setEventCategory(category);
        e.setEventDuration(category.getEventDuration());
        if (newEvent.getEventNotes() != null) {
            e.setEventNotes(newEvent.getEventNotes().strip());
        }

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
        return modelMapperUtils.mapList(events, EventResponseDTO.class);
    }

    public List<EventResponseDTO> getUpcomingAndOngoingEvents(Integer categoryId) {
        Instant now = Instant.now();
        List<Event> events;
        if (categoryId != null) {
            events = repository.findUpcomingAndOngoingEvents(now, categoryId);
        } else {
            events = repository.findUpcomingAndOngoingEvents(now);
        }
        return modelMapperUtils.mapList(events, EventResponseDTO.class);
    }

    public List<EventResponseDTO> getUpcomingAndOngoingEvents() {
        return getUpcomingAndOngoingEvents(null);
    }

    public List<EventResponseDTO> getPastEvents(Integer categoryId) {
        Instant now = Instant.now();
        List<Event> events;
        if (categoryId != null) {
            events = repository.findPastEvents(now, categoryId);
        } else {
            events = repository.findPastEvents(now);
        }
        return modelMapperUtils.mapList(events, EventResponseDTO.class);
    }

    public List<EventResponseDTO> getPastEvents() {
        return getPastEvents(null);
    }

    public List<EventResponseDTO> getEventsOnDate(Instant startAt, Integer categoryId) {
        List<Event> events;
        if (categoryId != null) {
            events = repository.findByDateRangeOfOneDay(startAt, categoryId);
        } else {
            events = repository.findByDateRangeOfOneDay(startAt);
        }
        return modelMapperUtils.mapList(events, EventResponseDTO.class);
    }

    public List<EventResponseDTO> getEventsOnDate(Instant startAt) {
        return getEventsOnDate(startAt, null);
    }
}