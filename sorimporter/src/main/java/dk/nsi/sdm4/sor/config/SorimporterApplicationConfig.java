package dk.nsi.sdm4.sor.config;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.sor.SORImporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SorimporterApplicationConfig {
	@Bean
	public Parser parser() {
		return new SORImporter();
	}

	@Bean
	public Persister persister() {
		return new AuditingPersister();
	}
}
