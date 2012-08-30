package dk.nsi.sdm4.autorisation.config;

import dk.nsi.sdm4.autorisation.parser.AutorisationParser;
import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class AutorisationInfrastructureConfig extends StamdataConfiguration {
	@Bean
	public SLALogger slaLogger() {
		return new SLALogConfig("Stamdata Autorisation-importer", "autorisationimporter").getSLALogger();
	}
}
