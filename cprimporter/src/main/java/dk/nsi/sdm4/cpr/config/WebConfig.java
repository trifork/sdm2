package dk.nsi.sdm4.cpr.config;

import dk.nsi.sdm4.core.status.TimeSource;
import dk.nsi.sdm4.core.status.TimeSourceRealTimeImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import dk.nsi.sdm4.core.status.StatusReporter;

@Configuration
@EnableWebMvc
public class WebConfig {

    @Bean
    public StatusReporter statusReporter() {
        return new StatusReporter();
    }
}
