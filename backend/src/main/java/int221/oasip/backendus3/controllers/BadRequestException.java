package int221.oasip.backendus3.controllers;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

