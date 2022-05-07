package int221.oasip.backendus3.dtos;

import int221.oasip.backendus3.entities.EventCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class EventDTO {
    private Integer eventCategoryId;
    private String bookingName;
    private String bookingEmail;
    private Instant eventStartTime;
    private String eventNotes;
}
