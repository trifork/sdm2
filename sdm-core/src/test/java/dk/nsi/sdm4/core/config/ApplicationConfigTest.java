package dk.nsi.sdm4.core.config;

import dk.nsi.sdm4.core.annotations.EnableStamdata;
import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persist.RecordPersisterEbean;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.sql.DataSource;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfigTest.TestConfiguration.class})
public class ApplicationConfigTest {
    @Inject
    Parser parser;

    @Inject
    RecordPersisterEbean recordPersister;

    @Inject
    Inbox inbox;

    @Inject
    DataSource dataSource;

    @EnableStamdata(home = "test_home")
    @Configuration
    @Import({StamdataTestConfiguration.class})
    static class TestConfiguration implements StamdataConfigurationSupport {
        @Bean
        public Parser parser() {
            return mock(Parser.class);
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
        final File top = inbox.top();
        assertNotNull(top);
        assertEquals("test_home", top.getAbsolutePath());
    }
}
