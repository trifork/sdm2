package dk.nsi.sdm4.core.parser;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

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
        String parserIdentifier = parser.getHome();
        SLALogItem slaLogItem = slaLogger.createLogItem("ParserExecutor", "Executing parser " + parserIdentifier);

        try {
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

		        slaLogItem.setCallResultOk();
		        slaLogItem.store();
	        } // if there is no data and no error, we never call store on the log item, which is okay
        } catch (Exception e) {
	        try {
	            inbox.lock();
	        } catch (RuntimeException lockExc) {
		        logger.error("Unable to lock " + inbox, lockExc);
	        }

            slaLogItem.setCallResultError("Parser " + parserIdentifier + " failed - Cause: " + e.getMessage());
            slaLogItem.store();
            logger.error("runParserOnInbox on parser "+parserIdentifier+" failed", e);
        }
    }
}
