package dk.nsi.sdm4.core.parser;

import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import java.io.IOException;

public class ParserExecutor {
    @Inject
    Parser parser;
    @Inject
    Inbox inbox;

    @Scheduled(fixedDelay = 1000)
    public void run() throws IOException {
        //TODO: exception håndtering?
        inbox.update();

        parser.process(inbox.top());
    }
}
