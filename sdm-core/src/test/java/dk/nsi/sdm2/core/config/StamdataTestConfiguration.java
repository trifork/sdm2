package dk.nsi.sdm2.core.config;

import com.avaje.ebean.springsupport.factory.EbeanServerFactoryBean;
import com.googlecode.flyway.core.Flyway;
import dk.nsi.sdm2.core.parser.Inbox;
import dk.nsi.sdm2.core.parser.ParserExecutor;
import dk.nsi.sdm2.core.persist.RecordPersisterEbean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

@Configuration
public class StamdataTestConfiguration extends StamdataConfiguration {
    //Make sure to override all methods on StamdataConfiguration with mock methods

    @Bean
    public DataSource dataSource() {
        return mock(DataSource.class);
    }

    @Bean
    public ParserExecutor parserExecutor() {
        //ParserExecutor currently doesn't have a interface, and cannot be mocked
        return null;
    }

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return null;
    }

    @Bean
    public EbeanServerFactoryBean ebeanServer() throws Exception {
        return null;
    }

    @Bean
    public RecordPersisterEbean recordPersister() {
        return mock(RecordPersisterEbean.class);
    }

    @Bean
    public Inbox inbox() {
        return mock(Inbox.class);
    }
}
