package int221.oasip.backendus3.repository;

import int221.oasip.backendus3.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCatagoryRepository extends JpaRepository<Event, Integer> {
}
