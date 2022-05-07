package int221.oasip.backendus3.services;

import int221.oasip.backendus3.entities.EventCategory;
import int221.oasip.backendus3.repository.EventCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventCategoryService {
    private EventCategoryRepository repository;

    public List<EventCategory> getAll(){
        List<EventCategory> categories = repository.findAll();
        return categories;
    }
}
