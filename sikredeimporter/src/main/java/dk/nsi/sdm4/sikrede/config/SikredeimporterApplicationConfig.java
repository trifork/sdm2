package dk.nsi.sdm4.sikrede.config;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.sikrede.brs.BrsUpdater;
import dk.nsi.sdm4.sikrede.parser.SikredeParser;
import org.joda.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SikredeimporterApplicationConfig {
	@Bean
	public Parser parser() {
		return new SikredeParser();
	}

	@Bean
	public BrsUpdater brsUpdater() {
		return new BrsUpdater();
	}

	@Bean
	public RecordPersister persister() {
		return new RecordPersister(Instant.now());
	}
}
