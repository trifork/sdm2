package dk.nsi.sdm4.core.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean;
import com.avaje.ebean.springsupport.txn.SpringAwareJdbcTransactionManager;
import com.googlecode.flyway.core.Flyway;

import dk.nsi.sdm4.core.annotations.EnableStamdata;
import dk.nsi.sdm4.core.parser.ParserExecutor;
import dk.nsi.sdm4.core.persist.RecordPersisterEbean;
import dk.nsi.sdm4.core.util.Preconditions;

@Configuration
@EnableScheduling
//@EnableTransactionManagement
public class StamdataConfiguration {
    

    @Bean
    public RecordPersisterEbean recordPersister() {
        return new RecordPersisterEbean();
    }

    @Bean
    public ParserExecutor parserExecutor() {
        return new ParserExecutor();
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

    @SuppressWarnings("unchecked")
    public static String getHome(Class clazz) {
        Preconditions.checkNotNull(clazz, "Class");
        Preconditions.checkArgument(clazz.isAnnotationPresent(EnableStamdata.class), "Parsers must be annotated with @EnableStamdata.");

        EnableStamdata es = (EnableStamdata) clazz.getAnnotation(EnableStamdata.class);
        return es.home();
    }

    // this is not automatically registered, see https://jira.springsource.org/browse/SPR-8539
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
