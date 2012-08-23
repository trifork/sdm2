package dk.nsi.sdm4.cpr.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import dk.nsi.sdm4.core.config.StamdataConfigurationSupport;
import dk.nsi.sdm4.core.parser.DirectoryInbox;
import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.core.status.ImportStatusRepository;
import dk.nsi.sdm4.core.status.ImportStatusRepositoryJdbcImpl;
import dk.nsi.sdm4.cpr.parser.CPRParser;
import dk.nsi.sdm4.cpr.parser.CprSingleFileImporter;
import dk.sdsd.nsp.slalog.api.SLALogConfig;
import dk.sdsd.nsp.slalog.api.SLALogger;

@Configuration
@Import({dk.nsi.sdm4.core.config.StamdataConfiguration.class})
public class CprparserConfig implements StamdataConfigurationSupport {

    @Value("${jdbc.JNDIName}") private String jdbcJNDIName;
    @Value("${sdm.dataDir}") private String dataDir;

    @Bean
    public DataSource dataSource() throws Exception{
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        factory.setJndiName(jdbcJNDIName);
        factory.setExpectedType(DataSource.class);
        factory.afterPropertiesSet();
        return (DataSource) factory.getObject();
    }

	@Bean
	public PlatformTransactionManager transactionManager(DataSource ds) {
		return new DataSourceTransactionManager(ds);
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
    public Inbox inbox() throws Exception {
        return new DirectoryInbox(
                dataDir,
                parser().getHome()); 
    }

    @Bean
    public Parser parser() {
        return new CPRParser();
    }

	@Bean
	public ImportStatusRepository importStatusRepository() {
		return new ImportStatusRepositoryJdbcImpl();
	}

	// this is not automatically registered, see https://jira.springsource.org/browse/SPR-8539
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(true);
        propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(false);

        propertySourcesPlaceholderConfigurer.setLocations(new Resource[]{new ClassPathResource("default-config.properties"),new ClassPathResource("config.properties")});
        
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public CprSingleFileImporter singleFileImporter() {
        return new CprSingleFileImporter();
    }    
    
    @Bean
    public Persister persister() throws SQLException {
        return new AuditingPersister();
    }


	@Bean
	public SLALogger slaLogger() {
		return new SLALogConfig("Stamdata CPR-importer", "cprimporter").getSLALogger();
	}
}
