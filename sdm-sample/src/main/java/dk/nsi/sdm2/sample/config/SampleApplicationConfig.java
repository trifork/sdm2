package dk.nsi.sdm2.sample.config;

import dk.nsi.sdm2.core.annotations.EnableStamdata;
import dk.nsi.sdm2.core.config.StamdataConfigurationSupport;
import dk.nsi.sdm2.core.parser.Parser;
import dk.nsi.sdm2.sample.parser.SampleParser;
import dk.nsi.sdm2.sample.specification.SampleRecordSpecs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableStamdata(home = "sample")
public class SampleApplicationConfig implements StamdataConfigurationSupport {

    @Bean
    public Parser parser() {
        return new SampleParser(SampleRecordSpecs.SAMPLE_RECORD_SPEC);
    }
}
