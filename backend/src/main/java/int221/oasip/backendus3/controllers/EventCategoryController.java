package int221.oasip.backendus3.controllers;

import int221.oasip.backendus3.entities.Event;
import int221.oasip.backendus3.entities.EventCategory;
import int221.oasip.backendus3.services.EventCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class EventCategoryController {
    private EventCategoryService service;

    @GetMapping("")
    public List<EventCategory> getCategories(){ return service.getAll(); }
}
