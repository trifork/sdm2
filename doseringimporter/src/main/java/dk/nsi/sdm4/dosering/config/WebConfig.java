package dk.nsi.sdm4.dosering.config;

import dk.nsi.sdm4.core.status.StatusReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class WebConfig {

    @Bean
    public StatusReporter statusReporter() {
        return new StatusReporter();
    }
}
