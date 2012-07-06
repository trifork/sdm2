package dk.nsi.sdm2.core.parser;

import dk.nsi.sdm2.core.persist.RecordPersister;
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

    @Scheduled(fixedDelay = 10)
    public void run() throws IOException {
        //TODO: exception håndtering?
        inbox.update();
        parser.process(inbox.top(), recordPersister);
    }
}
