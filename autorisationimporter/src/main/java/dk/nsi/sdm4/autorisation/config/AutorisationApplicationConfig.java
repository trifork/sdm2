package dk.nsi.sdm4.autorisation.config;

import dk.nsi.sdm4.autorisation.parser.AutorisationParser;
import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class AutorisationApplicationConfig {
	@Bean
	public Parser parser() {
		return new AutorisationParser();
	}

    @Bean
    public Persister persister() {
        return new AuditingPersister();
    }

	// this is not automatically registered, see https://jira.springsource.org/browse/SPR-8539
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	    return StamdataConfiguration.propertySourcesPlaceholderConfigurer();
    }
}
