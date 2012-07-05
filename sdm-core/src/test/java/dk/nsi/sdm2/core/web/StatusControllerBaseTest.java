package dk.nsi.sdm2.core.web;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class StatusControllerBaseTest {

    @Test
    public void willReturk200OK() throws Exception {
        StatusControllerBase controller = new StatusControllerBase() {
            @Override
            protected void doHealthCheck() { }
        };

        final ResponseEntity<String> response = controller.ping();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("OK", response.getBody());
    }

    @Test
    public void willReturk500ServerErrorOnError() throws Exception {
        StatusControllerBase controller = new StatusControllerBase() {
            @Override
            protected void doHealthCheck() {
                throw new RuntimeException("Error message");
            }
        };

        final ResponseEntity<String> response = controller.ping();

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error message", response.getBody());
        assertEquals("text/plain", response.getHeaders().getContentType().toString());
    }
}
