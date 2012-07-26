package dk.nsi.sdm2.core.config;

import dk.nsi.sdm2.core.parser.Parser;
import org.springframework.context.annotation.Bean;

public interface StamdataConfigurationSupport {
    @Bean
    public Parser parser();
}
