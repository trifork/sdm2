package dk.nsi.sdm4.core.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.status.StatusReporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class StatusReporterTest {
    
    @Configuration
    static class TestConfiguration {
        @Bean
        public Inbox inbox() {
            return mock(Inbox.class);
        }
        @Bean
        public StatusReporter statusReporter() {
            return new StatusReporter();
        }
    }

    @Autowired
    Inbox inbox;
    @Autowired
    StatusReporter reporter;

    @Test
    public void willReturk200OK() throws Exception {
        when(inbox.isLocked()).thenReturn(false);

        final ResponseEntity<String> response = reporter.reportStatus();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("OK", response.getBody());
    }

    @Test
    public void errorWithLockedInbox() throws Exception {
        when(inbox.isLocked()).thenReturn(true);
        
        final ResponseEntity<String> response = reporter.reportStatus();

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("Inbox is locked"));
    }
}
