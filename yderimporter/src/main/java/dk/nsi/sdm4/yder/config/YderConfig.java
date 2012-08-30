package dk.nsi.sdm4.yder.config;

import java.sql.SQLException;

import org.joda.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.yder.parser.YderregisterParser;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class YderConfig extends StamdataConfiguration {
	@Bean
	public Parser parser() {
		return new YderregisterParser();
	}

    @Bean
    public RecordPersister persister() {
        return new RecordPersister(Instant.now());
    }

	// this is not automatically registered, see https://jira.springsource.org/browse/SPR-8539
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return StamdataConfiguration.propertySourcesPlaceholderConfigurer();
    }

	@Bean
	public SLALogger slaLogger() {
		return new SLALogConfig("Stamdata Yder-importer", "yderimporter").getSLALogger();
	}
}
