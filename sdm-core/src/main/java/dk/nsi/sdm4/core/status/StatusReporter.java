package dk.nsi.sdm4.core.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.nsi.sdm4.core.parser.Inbox;

@Controller
public class StatusReporter {
    
    @Autowired
    Inbox inbox;

    @RequestMapping(value = "/status")
    public ResponseEntity<String> reportStatus() {
        HttpHeaders headers = new HttpHeaders();
        String body = "OK";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        try {
            if(inbox.isLocked()) {
                body = "Inbox is locked: " + inbox;
            } else {
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            body = e.getMessage();
        }

        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(body, headers, status);
    }
}
