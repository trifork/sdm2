package dk.nsi.sdm4.cpr.config;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.cpr.parser.CPRParser;
import dk.nsi.sdm4.cpr.parser.CprSingleFileImporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CprparserApplicationConfig {
    @Bean
    public Parser parser() {
        return new CPRParser();
    }

    @Bean
    public CprSingleFileImporter singleFileImporter() {
        return new CprSingleFileImporter();
    }    

    @Bean
    public Persister persister() {
        return new AuditingPersister();
    }
}
