package int221.oasip.backendus3.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
public class EditEventRequestDTO {
    @Future(message = "Start time must be in the future")
    private Instant eventStartTime;

    @Size(max = 500, message = "Event notes must be less than 500 characters")
    private String eventNotes;
}
