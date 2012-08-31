package dk.nsi.sdm4.dosering.config;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.dosering.parser.DoseringParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// The Spring Java Configuration annotations above needs to be on this class, not on the abstract superclass to
// make Spring stop complaining about weird things
public class DoseringparserApplicationConfig {
    @Bean
    public Parser parser() {
        return new DoseringParser();
    }

    @Bean
    public Persister persister() {
        return new AuditingPersister();
    }
}
