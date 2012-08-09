package dk.nsi.sdm4.sample.config;

import dk.nsi.sdm4.core.annotations.EnableStamdata;
import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.config.StamdataConfigurationSupport;
import dk.nsi.sdm4.core.parser.DirectoryInbox;
import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.sample.parser.SampleParser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@EnableStamdata(home = "sample")
public class SampleApplicationConfig implements StamdataConfigurationSupport {

    @Bean
    public Inbox inbox() throws Exception {
        
        return new DirectoryInbox(
                "/tmp", //TODO: property
                StamdataConfiguration.getHome(SampleApplicationConfig.class),
                10); //TODO: Property
    }

    @Bean
    public Parser parser() {
        return new SampleParser();
    }
    
    @Bean
    public Unmarshaller jaxbMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPaths(
                "dk.nsi.sdm4.sample.parser"
        );
        return marshaller;
    }

}
