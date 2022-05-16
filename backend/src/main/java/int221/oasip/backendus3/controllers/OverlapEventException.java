package int221.oasip.backendus3.controllers;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class OverlapEventException extends RuntimeException {
    private final String field;

    public OverlapEventException(String field, String message) {
        super(message);
        this.field = field;
    }
}
