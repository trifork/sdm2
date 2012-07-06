package dk.nsi.sdm2.core.config;

import dk.nsi.sdm2.core.annotations.EnableStamdata;
import dk.nsi.sdm2.core.parser.Inbox;
import dk.nsi.sdm2.core.parser.Parser;
import dk.nsi.sdm2.core.persist.RecordPersister;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.activation.DataSource;
import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfigTest.TestConfiguration.class})
public class ApplicationConfigTest {
    @Inject
    Parser parser;

    @Inject
    RecordPersister recordPersister;

    @Inject
    Inbox inbox;

    @Inject
    DataSource dataSource;

    @EnableStamdata(home = "test_home")
    @Configuration
    @Import({ApplicationConfiguration.class})
    static class TestConfiguration {
        @Bean
        public Parser testParser() {
            return mock(Parser.class);
        }
    }

    @Configuration
    public static class ApplicationConfiguration extends StamdataConfiguration {
        //Make sure to override all methods on StamdataConfiguration with mock methods

        @Bean
        public DataSource dataSource() {
            return mock(DataSource.class);
        }

        @Bean
        public RecordPersister recordPersister() {
            return mock(RecordPersister.class);
        }

        @Bean
        public Inbox inbox() {
            return mock(Inbox.class);
        }
    }

    @Test
    public void canCreateStamdataConfiguration() throws Exception {
        assertNotNull(parser);
        assertNotNull(recordPersister);
        assertNotNull(inbox);
    }

    @Test @Ignore
    public void inboxIsPopulatedWithHome() throws Exception {
        assertEquals("/jbossHome/test_home", inbox.top().getAbsolutePath());
    }
}
