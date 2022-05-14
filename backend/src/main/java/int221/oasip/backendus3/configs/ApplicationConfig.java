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
                    FieldError fieldError = exception.getFieldError();

                    if (fieldError != null) {
                        errorAttributes.put("message", fieldError.getDefaultMessage());
                    }
                }

                return errorAttributes;
            }
        };
    }
}
