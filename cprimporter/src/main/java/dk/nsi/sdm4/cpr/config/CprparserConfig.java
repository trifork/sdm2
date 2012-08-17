package dk.nsi.sdm4.cpr.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jndi.JndiObjectFactoryBean;

import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.nsi.sdm4.core.config.StamdataConfigurationSupport;
import dk.nsi.sdm4.core.parser.DirectoryInbox;
import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.cpr.parser.CPRParser;

@Configuration
@PropertySource({"classpath:default-config.properties", "classpath:config.properties"})
public class CprparserConfig implements StamdataConfigurationSupport {

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
                StamdataConfiguration.getHome(CPRParser.class),
                stabilizationPeriod); 
    }

    @Bean
    public Parser parser() {
        return new CPRParser();
    }
}
