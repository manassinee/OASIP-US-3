package int221.oasip.backendus3.configs;

import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new ExtendedErrorAttributes();
    }
}
