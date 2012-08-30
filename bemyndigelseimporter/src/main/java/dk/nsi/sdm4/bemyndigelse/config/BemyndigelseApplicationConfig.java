package dk.nsi.sdm4.bemyndigelse.config;

import org.joda.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import dk.nsi.sdm4.bemyndigelse.parser.BemyndigelseParser;
import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;

@Configuration
public class BemyndigelseApplicationConfig {
	@Bean
	public Parser parser() {
		return new BemyndigelseParser();
	}

	@Bean
	public RecordPersister persister() {
		return new RecordPersister(Instant.now());
	}
}
