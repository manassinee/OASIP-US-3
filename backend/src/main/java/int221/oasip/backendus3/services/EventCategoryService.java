package int221.oasip.backendus3.services;

import int221.oasip.backendus3.dtos.EditCategoryRequestDTO;
import int221.oasip.backendus3.dtos.CategoryResponseDTO;
import int221.oasip.backendus3.entities.EventCategory;
import int221.oasip.backendus3.exceptions.NotUniqueException;
import int221.oasip.backendus3.exceptions.EntityNotFoundException;
import int221.oasip.backendus3.repository.EventCategoryRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventCategoryService {
    private EventCategoryRepository repository;
    private ModelMapper modelMapper;

    public List<EventCategory> getAll() {
        List<EventCategory> categories = repository.findAll();
        return categories;
    }

    public CategoryResponseDTO update(Integer id, EditCategoryRequestDTO editCategory) {
        EventCategory category = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category with id" + id + " not found"));

        if (editCategory.getEventCategoryName() != null) {
            String strippedName = editCategory.getEventCategoryName().strip();
            EventCategory existingCategory = repository.findByEventCategoryNameIgnoreCase(strippedName);

            if (existingCategory != null && !existingCategory.getId().equals(category.getId())) {
                throw new NotUniqueException("Event category name is not unique");
            }

            category.setEventCategoryName(strippedName);
        }

        if (editCategory.getEventCategoryDescription() != null) {
            category.setEventCategoryDescription(editCategory.getEventCategoryDescription().strip());
        }

        if (editCategory.getEventDuration() != null) {
            category.setEventDuration(editCategory.getEventDuration());
        }

        return modelMapper.map(repository.saveAndFlush(category), CategoryResponseDTO.class);
    }

}