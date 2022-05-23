package int221.oasip.backendus3.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class CategoryNameNotUniqueException extends RuntimeException {
    public CategoryNameNotUniqueException() {
        super("Event category name is not unique");
    }

}
