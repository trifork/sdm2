package dk.nsi.sdm2.core.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public abstract class StatusControllerBase {

    @RequestMapping("/status")
    public ResponseEntity<String> ping() {
        HttpHeaders headers = new HttpHeaders();
        String body = "OK";
        HttpStatus status = HttpStatus.OK;

        try {
            doHealthCheck();
        } catch (Exception e) {
            //todo: logger.warn("Health check failed", e)
            body = e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(body, headers, status);
    }

    protected abstract void doHealthCheck();
}
