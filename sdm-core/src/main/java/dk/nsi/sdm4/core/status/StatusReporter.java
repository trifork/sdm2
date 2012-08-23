package dk.nsi.sdm4.core.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.nsi.sdm4.core.parser.Inbox;
import dk.nsi.sdm4.core.status.ImportStatus.Outcome;

@Controller
public class StatusReporter {
    
    @Autowired
    Inbox inbox;

    @Autowired
    ImportStatusRepository statusRepo;

    @RequestMapping(value = "/status")
    public ResponseEntity<String> reportStatus() {
        HttpHeaders headers = new HttpHeaders();
        String body = "OK";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        try {
            if(inbox.isLocked()) {
                body = "Inbox is locked: " + inbox;
            } else if(lastStatusIsFailure()) {
                // status applied later
            } else {
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            body = e.getMessage();
        }

        body = addLastRunInformation(body);
        
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(body, headers, status);
    }

    private boolean lastStatusIsFailure() {
        if (statusRepo != null && 
            statusRepo.getLatestStatus() != null && 
            statusRepo.getLatestStatus().getOutcome() != null && 
            statusRepo.getLatestStatus().getOutcome().equals(Outcome.FAILURE)) {
            return true;
        }
        return false;
    }

    private String addLastRunInformation(String body) {
        ImportStatus latestStatus = statusRepo.getLatestStatus();
        if (latestStatus == null) {
            return body += "\nLast import: Never run";
        } else {
            body += "\nLast import ended at: " + latestStatus.getEndTime();
            Outcome outcome = latestStatus.getOutcome();
            if(outcome != null && outcome.equals(Outcome.FAILURE)) {
                body += "with outcome" + Outcome.FAILURE;
            }
            return body;
        }
    }
}
