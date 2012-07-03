package dk.nsi.sdm2.core.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public abstract class StatusControllerBase {

    @RequestMapping("/status")
    public ResponseEntity<String> ping() {
        String body = "OK";
        HttpStatus status = HttpStatus.OK;

        try {
            doHealthCheck();
        } catch (Exception e) {
            //todo: logger.warn("Health check failed", e)
            body = e.getMessage();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<String>(body, status);
    }

    protected abstract void doHealthCheck();
}
