package dk.nsi.sdm4.core.config;

import dk.nsi.sdm4.core.parser.Parser;
import org.springframework.context.annotation.Bean;

public interface StamdataConfigurationSupport {
    @Bean
    public Parser parser();
}
