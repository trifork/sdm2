package dk.nsi.sdm4.core.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.parser.Parser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfigTest.TestConfiguration.class})
public class ApplicationConfigTest {
    @Inject
    Parser parser;

    @Inject
    Inbox inbox;

    @Inject
    DataSource dataSource;

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
        assertNotNull(inbox);
    }

    @Test
    @Ignore
    public void inboxIsPopulatedWithHome() throws Exception {
        assertEquals("/jbossHome/test_home", inbox.top().getAbsolutePath());
    }
}
