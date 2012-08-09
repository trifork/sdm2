package dk.nsi.sdm4.cpr.config;

import dk.nsi.sdm4.core.annotations.EnableStamdata;
import dk.nsi.sdm4.core.config.StamdataConfigurationSupport;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.cpr.parser.CPRParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableStamdata(home = "cpr")
public class CprparserConfig implements StamdataConfigurationSupport {

    @Bean
    public Parser parser() {
        return new CPRParser();
    }
}
