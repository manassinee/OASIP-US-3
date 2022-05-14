package int221.oasip.backendus3.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
public class EventDTO {
    @NotNull
    private Integer eventCategoryId;

    @NotBlank
    @Size(max = 100, message = "Booking name must be less than {max} characters")
    private String bookingName;

    @NotBlank
    @Size(max = 50, message = "Booking email must be less than {max} characters")
    private String bookingEmail;

    private Instant eventStartTime;

    @Size(max = 500, message = "Event notes must be less than {max} characters")
    private String eventNotes;
}

