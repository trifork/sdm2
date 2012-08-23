package dk.nsi.sdm4.sorrelation.config;

import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.sql.SQLException;

@Configuration
@EnableScheduling
@EnableTransactionManagement
public class SorrelationConfig extends StamdataConfiguration {
	@Bean
	public Parser parser() {
		return new Parser() {
			@Override
			public void process(File dataSet) throws ParserException {
				throw new UnsupportedOperationException("process");
			}

			@Override
			public String getHome() {
				return "sorrelationimporter";
			}
		};
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
