package dk.nsi.sdm4.sorrelation.config;

import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.sql.SQLException;

@Configuration
public class SorrelationConfig extends StamdataConfiguration {
	@Bean
	public Parser parser() {
		return null;
	}

	// this is not automatically registered, see https://jira.springsource.org/browse/SPR-8539
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return StamdataConfiguration.propertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Persister persister() throws SQLException {
        return new AuditingPersister();
    }

	@Bean
	public SLALogger slaLogger() {
		return new SLALogConfig("Stamdata SORrelation-importer", "sorrelationimporter").getSLALogger();
	}
}
