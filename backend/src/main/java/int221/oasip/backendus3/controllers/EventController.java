package int221.oasip.backendus3.controllers;

import int221.oasip.backendus3.dtos.EditDTO;
import int221.oasip.backendus3.dtos.EventDTO;
import int221.oasip.backendus3.entities.Event;
import int221.oasip.backendus3.services.EventService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {
    private EventService service;

    @GetMapping("")
    public List<Event> getEvents(){ return service.getAll(); }

    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Integer id){
        return service.getEvent(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@RequestBody EventDTO newEvent){
        return service.save(newEvent);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id){ service.delete(id); }

    @PutMapping("/{id}")
    public Event update(@PathVariable Integer id, @RequestBody EditDTO editEvent) {
        return service.update(id, editEvent);
    }
}
