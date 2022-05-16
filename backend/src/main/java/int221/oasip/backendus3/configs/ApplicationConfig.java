package int221.oasip.backendus3.configs;

import org.modelmapper.ModelMapper;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
                Throwable error = super.getError(webRequest);

                if (error instanceof MethodArgumentNotValidException) {
                    MethodArgumentNotValidException exception = (MethodArgumentNotValidException) error;

                    if (exception.hasFieldErrors()) {
                        // make a map of field errors. keyed by field name, with value of an array of error messages for that field
                        Map<String, List<String>> map = new HashMap<>();

                        for (FieldError fieldError : exception.getFieldErrors()) {
                            map.computeIfAbsent(fieldError.getField(), k -> new ArrayList<>()).add(fieldError.getDefaultMessage());
                        }

                        errorAttributes.put("message", "Validation failed");
                        errorAttributes.put("errors", map);
                    }
                }

                return errorAttributes;
            }
        };
    }
}
