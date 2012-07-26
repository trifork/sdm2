package dk.nsi.sdm4.core.config;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean;
import com.avaje.ebean.springsupport.txn.SpringAwareJdbcTransactionManager;
import com.googlecode.flyway.core.Flyway;
import dk.nsi.sdm4.core.domain.AbstractRecord;
import dk.nsi.sdm4.core.parser.DirectoryInbox;
import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.parser.ParserExecutor;
import dk.nsi.sdm4.core.persist.RecordPersisterEbean;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.ArrayList;

@Configuration
@EnableScheduling
//@EnableTransactionManagement
public class StamdataConfiguration {
    @Bean
    public Inbox inbox() throws Exception {
        return new DirectoryInbox(
                "/pack/jboss/server/default/data/sdm4", //TODO: property
                "TODO:::sample", //TODO: Read from @EnableStamdata.home
                10); //TODO: Property
    }

    @Bean
    public RecordPersisterEbean recordPersister() {
        return new RecordPersisterEbean();
    }

    @Bean
    public ParserExecutor parserExecutor() {
        return new ParserExecutor();
    }

    @Bean
    public JndiObjectFactoryBean dataSource() throws Exception{
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        // TODO: property
        factory.setJndiName("java:jdbc/MySQLDS");
        return factory;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }

    @Bean
    public EbeanServerFactoryBean ebeanServer(DataSource dataSource) throws Exception {
        final EbeanServerFactoryBean factoryBean = new EbeanServerFactoryBean();
        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName("localhostConfig");
        serverConfig.setClasses(new ArrayList<Class<?>>(new Reflections("dk.nsi").getSubTypesOf(AbstractRecord.class)));
        serverConfig.setDataSource(dataSource);
        serverConfig.setExternalTransactionManager(new SpringAwareJdbcTransactionManager());
        factoryBean.setServerConfig(serverConfig);
        return factoryBean;
    }

}
