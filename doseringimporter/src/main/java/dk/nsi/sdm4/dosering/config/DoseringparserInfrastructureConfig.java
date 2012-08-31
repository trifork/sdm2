package dk.nsi.sdm4.dosering.config;

import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableScheduling
@EnableTransactionManagement
//The Spring Java Configuration annotations above needs to be on this class, not on the abstract superclass to
// make Spring stop complaining about weird things
public class DoseringparserInfrastructureConfig extends StamdataConfiguration {
	@Bean
	public SLALogger slaLogger() {
		return new SLALogConfig("Stamdata Dosering-importer", "doseringimporter").getSLALogger();
	}
}
