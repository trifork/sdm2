package dk.nsi.sdm4.core.parser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import dk.nsi.sdm4.core.config.StamdataConfiguration;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;

public class ParserExecutor {
    @Autowired
    Parser parser;
    @Autowired
    Inbox inbox;

    @Autowired
    private SLALogger slaLogger;

    private static final Logger logger = Logger.getLogger(ParserExecutor.class);
    
    
    @Scheduled(fixedDelay = 1000)
    public void run() {
        // TODO logging
        String parserIdentifier = parser.getHome();
        SLALogItem slaLogItem = slaLogger.createLogItem("ParserExecutor", "Executing parser " + parserIdentifier);
        
//        URL resource = this.getClass().getResource("/log4j.xml");
//        if(resource != null) {
//            System.out.println(resource.toExternalForm());
//            logger.fatal(resource.toExternalForm());
//        } else {
//            logger.fatal("log4j.properties not found");
//        }
        
        try {
            runParserOnInbox();
            
            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("Parser " + parserIdentifier + " failed - Cause: " + e.getMessage());
            slaLogItem.store();
            logger.error("runParserOnInbox on parser "+parserIdentifier+" failed", e);
        }
    }

    private void runParserOnInbox() throws IOException {
        if(logger.isDebugEnabled()) {
            logger.debug("Running parser " + parser.getHome());
        }
        
        inbox.update();
        File dataSet = inbox.top();

        if (dataSet != null && !inbox.isLocked()) {
            parser.process(dataSet);

            // Once the import is complete
            // we can remove of the data set
            // from the inbox.
            inbox.advance();
        }
    }
}
