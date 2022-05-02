package int221.oasip.backendus3.controllers;

import int221.oasip.backendus3.entities.Event;
import int221.oasip.backendus3.services.EventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}


