package int221.oasip.backendus3.repository;

import int221.oasip.backendus3.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    // get all overlap events. there are two scenarios:
    // 1. events that started before the startTime and ended after the startTime
    // 2. events that started between the startTime (inclusive) and the endTime (exclusive)
    @Query(nativeQuery = true,
            value = "SELECT * FROM event e WHERE " +
                    "e.eventCategoryId = ?3 AND " +
                    "((e.eventStartTime < ?1 AND (e.eventStartTime + INTERVAL e.eventDuration MINUTE) > ?1) OR " +
                    "(e.eventStartTime >= ?1 AND e.eventStartTime < ?2))")
    List<Event> findOverlapEventsByCategoryId(Instant startTime, Instant endTime, Integer categoryId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM Event e WHERE e.eventCategoryId = ?1 AND DATE(e.eventStartTime) = ?2")
    List<Event> findByCategoryIdOnDate(Integer categoryId, LocalDate date);
}
