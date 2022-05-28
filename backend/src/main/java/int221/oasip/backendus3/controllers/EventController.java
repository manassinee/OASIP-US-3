package int221.oasip.backendus3.controllers;

import int221.oasip.backendus3.dtos.CreateEventRequestDTO;
import int221.oasip.backendus3.dtos.EditEventRequestDTO;
import int221.oasip.backendus3.dtos.EventResponseDTO;
import int221.oasip.backendus3.exceptions.EntityNotFoundException;
import int221.oasip.backendus3.exceptions.EventOverlapException;
import int221.oasip.backendus3.exceptions.FieldNotValidException;
import int221.oasip.backendus3.services.EventService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {
    private EventService service;

    @GetMapping("")
    public List<EventResponseDTO> getEvents(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startAt,
            @RequestParam(required = false) String type
    ) {
        if ("day".equalsIgnoreCase(type) && startAt != null) {
            if (categoryId != null) {
                return service.getEventsOnDate(startAt.toInstant(), categoryId);
            } else {
                return service.getEventsOnDate(startAt.toInstant());
            }
        }

        if ("upcoming".equalsIgnoreCase(type)) {
            if (categoryId != null) {
                return service.getUpcomingAndOngoingEvents(categoryId);
            } else {
                return service.getUpcomingAndOngoingEvents();
            }
        }

        if ("past".equalsIgnoreCase(type)) {
            if (categoryId != null) {
                return service.getPastEvents(categoryId);
            } else {
                return service.getPastEvents();
            }
        }

        if (categoryId != null) {
            return service.getEventsInCategory(categoryId);
        }

        return service.getAll();
    }

    @GetMapping("/{id}")
    public EventResponseDTO getEventById(@PathVariable Integer id) {
        EventResponseDTO event = service.getEvent(id);

        if (event == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event with id " + id + " not found");
        }

        return event;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO create(@Valid @RequestBody CreateEventRequestDTO newEvent) {
        try {
            return service.create(newEvent);
        } catch (EventOverlapException e) {
            throw new FieldNotValidException("eventStartTime", e.getMessage());
        } catch (EntityNotFoundException e) {
            // category not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @PatchMapping("/{id}")
    public EventResponseDTO update(@PathVariable Integer id, @Valid @RequestBody EditEventRequestDTO editEvent) {
        if (editEvent.getEventStartTime() == null && editEvent.getEventNotes() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one of eventStartTime or eventNotes must be provided");
        }

        try {
            return service.update(id, editEvent);
        } catch (EventOverlapException e) {
            throw new FieldNotValidException("eventStartTime", e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
