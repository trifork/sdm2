package dk.nsi.sdm4.cpr.config;

import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.cpr.parser.CPRParser;
import dk.nsi.sdm4.cpr.parser.CprSingleFileImporter;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;

@Configuration
@EnableScheduling
@EnableTransactionManagement
//The Spring Java Configuration annotations above needs to be on this class, not on the abstract superclass to
// make Spring stop complaining about weird things
public class CprparserInfrastructureConfig extends StamdataConfiguration {
	@Bean
	public SLALogger slaLogger() {
		return new SLALogConfig("Stamdata CPR-importer", "cprimporter").getSLALogger();
	}
}
