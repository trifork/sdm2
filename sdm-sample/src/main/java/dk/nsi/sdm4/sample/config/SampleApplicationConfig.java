package dk.nsi.sdm4.sample.config;

import dk.nsi.sdm4.core.annotations.EnableStamdata;
import dk.nsi.sdm4.core.config.StamdataConfigurationSupport;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.sample.parser.SampleParser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableStamdata(home = "sample")
public class SampleApplicationConfig implements StamdataConfigurationSupport {

    @Bean
    public Parser parser() {
        return new SampleParser();
    }
}
