package int221.oasip.backendus3.controllers;

import int221.oasip.backendus3.dtos.CreateEventRequestDTO;
import int221.oasip.backendus3.dtos.EditEventRequestDTO;
import int221.oasip.backendus3.entities.Event;
import int221.oasip.backendus3.services.EventService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {
    private EventService service;

    @GetMapping("")
    public List<Event> getEvents(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (categoryId != null && date != null) {
           return service.getEventsByCategoryIdOnDate(categoryId, date);
        }

        return service.getAll();
    }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Integer id) {
        return service.getEvent(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Validated @RequestBody CreateEventRequestDTO newEvent) {
        return service.save(newEvent);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){ service.delete(id); }

    @PatchMapping("/{id}")
    public Event update(@PathVariable Integer id, @Validated @RequestBody EditEventRequestDTO editEvent) {
        return service.update(id, editEvent);
    }
}
