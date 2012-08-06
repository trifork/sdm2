package dk.nsi.sdm4.core.parser;

import dk.nsi.sdm4.core.persist.RecordPersister;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import java.io.IOException;

public class ParserExecutor {
    @Inject
    Parser parser;
    @Inject
    RecordPersister recordPersister;
    @Inject
    Inbox inbox;

    @Scheduled(fixedDelay = 1000)
    public void run() throws IOException {
        //TODO: exception håndtering?
        inbox.update();

        //TODO: parser.process(inbox.top());
        System.out.println("Parse processing is temporary disabled");
    }
}
