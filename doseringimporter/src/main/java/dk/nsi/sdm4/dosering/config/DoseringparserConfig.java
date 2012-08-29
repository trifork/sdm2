package dk.nsi.sdm4.dosering.config;

import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.dosering.parser.DoseringParser;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;

@Configuration
@EnableScheduling
@EnableTransactionManagement
//The Spring Java Configuration annotations above needs to be on this class, not on the abstract superclass to
// make Spring stop complaining about weird things
public class DoseringparserConfig extends StamdataConfiguration {
    @Bean
    public Parser parser() {
        return new DoseringParser();
    }

    
    @Bean
    public Persister persister() {
        return new AuditingPersister();
    }

	@Bean
	public SLALogger slaLogger() {
		return new SLALogConfig("Stamdata Dosering-importer", "doseringimporter").getSLALogger();
	}
}
