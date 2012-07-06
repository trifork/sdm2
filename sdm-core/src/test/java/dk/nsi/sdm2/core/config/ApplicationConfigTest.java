package dk.nsi.sdm2.core.config;

import dk.nsi.sdm2.core.annotations.EnableStamdata;
import dk.nsi.sdm2.core.importer.parser.Inbox;
import dk.nsi.sdm2.core.importer.parser.Parser;
import dk.nsi.sdm2.core.persist.RecordPersister;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfigTest.ApplicationConfiguration.class, ApplicationConfigTest.TestConfiguration.class})
public class ApplicationConfigTest {
    @Inject
    Parser parser;

    @Inject
    RecordPersister recordPersister;

    @Inject
    Inbox inbox;

    @EnableStamdata(home = "test_home")
    public static class TestConfiguration {
        @Bean
        public Parser testParser() {
            return mock(Parser.class);
        }
    }

    public static class ApplicationConfiguration {
        //todo: datasource, recordPesister, etc.
    }

    @Test
    public void canCreateStamdataConfiguration() throws Exception {
        assertNotNull(parser);
        assertNotNull(recordPersister);
        assertNotNull(inbox);
    }

    @Test
    public void inboxIsPopulated() throws Exception {
        assertEquals("/jbossHome/test_home", inbox.top().getAbsolutePath());
    }
}
