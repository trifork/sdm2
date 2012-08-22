package dk.nsi.sdm4.core.status;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import dk.nsi.sdm4.core.status.StatusReporter;

import static org.junit.Assert.assertEquals;

public class StatusReporterTest {

    @Test
    public void willReturk200OK() throws Exception {
        StatusReporter reporter = new StatusReporter(); 

        final ResponseEntity<String> response = reporter.reportStatus();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("OK", response.getBody());
    }

}
