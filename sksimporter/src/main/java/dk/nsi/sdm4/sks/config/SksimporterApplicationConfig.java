package dk.nsi.sdm4.sks.config;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.sks.SKSParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SksimporterApplicationConfig {
    @Bean
    public Parser parser() {
		return new SKSParser();
	}

    @Bean
    public Persister persister() {
        return new AuditingPersister();
    }
}
