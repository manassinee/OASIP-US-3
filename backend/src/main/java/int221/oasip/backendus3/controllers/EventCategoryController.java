package int221.oasip.backendus3.controllers;

import int221.oasip.backendus3.dtos.CategoryResponseDTO;
import int221.oasip.backendus3.dtos.EditCategoryRequestDTO;
import int221.oasip.backendus3.exceptions.FieldNotValidException;
import int221.oasip.backendus3.exceptions.NotUniqueException;
import int221.oasip.backendus3.services.EventCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class EventCategoryController {
    private EventCategoryService service;

    @GetMapping("")
    public List<CategoryResponseDTO> getCategories() {
        return service.getAll();
    }

    @PatchMapping("/{id}")
    public CategoryResponseDTO update(@PathVariable Integer id, @Valid @RequestBody EditCategoryRequestDTO editCategory) {
        if (editCategory.getEventCategoryName() == null &&
                editCategory.getEventCategoryDescription() == null &&
                editCategory.getEventDuration() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one of eventCategoryName, eventCategoryDescription or eventDuration must be provided");
        }

        try {
            return service.update(id, editCategory);
        } catch (NotUniqueException e) {
            throw new FieldNotValidException("eventCategoryName", e.getMessage());
        }
    }

}
