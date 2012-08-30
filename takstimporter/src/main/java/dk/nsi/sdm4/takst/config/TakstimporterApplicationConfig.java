package dk.nsi.sdm4.takst.config;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.takst.TakstImporter;
import dk.nsi.sdm4.takst.TakstParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TakstimporterApplicationConfig {
	@Bean
	public Parser parser() {
		return new TakstImporter();
	}

	@Bean
	public Persister persister() {
		return new AuditingPersister();
	}

	@Bean
	public TakstParser takstParser() {
		return new TakstParser();
	}
}