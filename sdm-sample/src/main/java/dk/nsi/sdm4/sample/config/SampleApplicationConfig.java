package dk.nsi.sdm4.sample.config;

import javax.sql.DataSource;

import dk.nsi.sdm4.core.annotations.EnableStamdata;
import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.config.StamdataConfigurationSupport;
import dk.nsi.sdm4.core.parser.DirectoryInbox;
import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.sample.dao.SampleDao;
import dk.nsi.sdm4.sample.dao.SampleDaoJdbcImpl;
import dk.nsi.sdm4.sample.parser.SampleParser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@PropertySource("classpath:/sdm-sample.properties")
@EnableStamdata(home = "sample")
public class SampleApplicationConfig implements StamdataConfigurationSupport {

    @Value("${jdbc.JNDIName}") private String jdbcJNDIName;
    @Value("${sdm.dataDir}") private String dataDir;
    @Value("${sdm.stabilizationPeriod}") private int stabilizationPeriod;

    @Bean
    public DataSource dataSource() throws Exception{
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        factory.setJndiName(jdbcJNDIName); // "java:/MySQLDS"
        factory.setExpectedType(DataSource.class);
        factory.afterPropertiesSet();
        return (DataSource) factory.getObject();
    }

    @Bean
    public Inbox inbox() throws Exception {
        
        return new DirectoryInbox(
                dataDir,
                StamdataConfiguration.getHome(SampleApplicationConfig.class),
                stabilizationPeriod); 
    }

    @Bean
    public Parser parser() {
        return new SampleParser();
    }
    
    @Bean
    public Unmarshaller jaxbMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPaths(
                "dk.nsi.sdm4.sample.parser"
        );
        return marshaller;
    }

	@Bean
	public SampleDao sampleDao() {
		return new SampleDaoJdbcImpl();
	}
}
