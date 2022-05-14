package int221.oasip.backendus3.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class EditDTO {
    private Instant eventStartTime;
    private String eventNotes;
}
