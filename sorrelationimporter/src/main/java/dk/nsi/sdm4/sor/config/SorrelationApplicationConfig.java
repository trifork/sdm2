package dk.nsi.sdm4.sor.config;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.sor.relations.SorRelationParser;
import org.joda.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SorrelationApplicationConfig {
	@Bean
	public Parser parser() {
		return new SorRelationParser();
	}

	@Bean
	public RecordPersister persister() {
		return new RecordPersister(Instant.now());
	}
}
