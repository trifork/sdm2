package dk.nsi.sdm4.core.status;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.status.StatusReporter;
import dk.nsi.sdm4.core.status.ImportStatus.Outcome;

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
            Inbox inbox = mock(Inbox.class);
            return inbox;

        }
        @Bean
        public StatusReporter statusReporter() {
            return new StatusReporter();
        }
        @Bean ImportStatusRepository importStatusRepository() {
            ImportStatusRepository repo = mock(ImportStatusRepository.class);
            return repo;
        }
    }

    @Autowired
    Inbox inbox;
    @Autowired
    StatusReporter reporter;
    @Autowired
    ImportStatusRepository statusRepo;

    
    @Test
    public void willReturn200underNormalCircumstances() throws Exception {
        when(inbox.isLocked()).thenReturn(false);
        final ResponseEntity<String> response = reporter.reportStatus();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("OK"));
    }

    @Test
    public void errorWithLockedInbox() throws Exception {
        when(inbox.isLocked()).thenReturn(true);
        
        final ResponseEntity<String> response = reporter.reportStatus();

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().startsWith("Inbox is locked"));
    }    
    
    @Test
    public void reportsNeverRunWhenImportStatusDoesNotExist() {
        when(statusRepo.getLatestStatus()).thenReturn(null);
        when(inbox.isLocked()).thenReturn(false);
        
        final ResponseEntity<String> response = reporter.reportStatus();
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().toLowerCase().contains("never run"));
    }
    
    @Test
    public void reportsEndTimeOfLastSuccess() {
        ImportStatus status = new ImportStatus();
        DateTime endTime = new DateTime();
        status.setEndTime(endTime);
        status.setOutcome(Outcome.SUCCESS);
        
        when(statusRepo.getLatestStatus()).thenReturn(status);
        when(inbox.isLocked()).thenReturn(false);
        
        final ResponseEntity<String> response = reporter.reportStatus();
        assertEquals(200, response.getStatusCode().value());
        
        assertTrue(response.getBody().contains(endTime.toString()));
    }

    @Test
    public void reportsLastFailure() {
        ImportStatus status = new ImportStatus();
        DateTime endTime = new DateTime();
        status.setEndTime(endTime);
        status.setOutcome(Outcome.FAILURE);
        
        when(statusRepo.getLatestStatus()).thenReturn(status);
        when(inbox.isLocked()).thenReturn(false);
        
        final ResponseEntity<String> response = reporter.reportStatus();
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains(Outcome.FAILURE.toString()));
    }
}
