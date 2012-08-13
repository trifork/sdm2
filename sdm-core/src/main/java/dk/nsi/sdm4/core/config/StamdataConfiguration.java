package dk.nsi.sdm4.core.config;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean;
import com.avaje.ebean.springsupport.txn.SpringAwareJdbcTransactionManager;
import com.googlecode.flyway.core.Flyway;
import dk.nsi.sdm4.core.parser.DirectoryInbox;
import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.parser.ParserExecutor;
import dk.nsi.sdm4.core.persist.RecordPersisterEbean;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.sql.DataSource;

@Configuration
@EnableScheduling
//@EnableTransactionManagement
public class StamdataConfiguration implements ImportAware {
    private static Logger logger = Logger.getLogger(StamdataConfiguration.class);
    String home;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        home = (String) importMetadata.getAnnotationAttributes("dk.nsi.sdm4.core.annotations.EnableStamdata").get("home");
        logger.debug("Using home=" + home + " from @EnableStamdata");
    }

    @Bean
    public Inbox inbox() throws Exception {
        return new DirectoryInbox(
                "/tmp", //TODO: property
                home,
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
    public DataSource dataSource() throws Exception {
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
        // TODO: property
        factory.setJndiName("java:/MySQLDS");
        factory.setExpectedType(DataSource.class);
        factory.afterPropertiesSet();
        return (DataSource) factory.getObject();
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
        //serverConfig.setClasses(new ArrayList<Class<?>>(new Reflections("dk.nsi").getTypesAnnotatedWith(Entity.class)));
        serverConfig.setDataSource(dataSource);
        serverConfig.setExternalTransactionManager(new SpringAwareJdbcTransactionManager());
        factoryBean.setServerConfig(serverConfig);
        return factoryBean;
    }
}
