package dk.nsi.sdm2.core.config;

import dk.nsi.sdm2.core.importer.parser.DirectoryInbox;
import dk.nsi.sdm2.core.importer.parser.Inbox;
import dk.nsi.sdm2.core.importer.parser.ParserExecutor;
import dk.nsi.sdm2.core.persist.RecordPersister;
import dk.nsi.sdm2.core.persist.RecordPersisterEbean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class StamdataConfiguration {
    @Bean
    public Inbox inbox() throws Exception {
        return new DirectoryInbox(
                "/pack/jboss/server/default/data/sdm4", //TODO: property
                "TODO:::sample", //TODO: Read from @EnableStamdata.home
                10); //TODO: Property
    }

    @Bean
    public RecordPersister recordPersister() {
        return new RecordPersisterEbean();
    }

    @Bean
    public ParserExecutor parserExecutor() {
        return new ParserExecutor();
    }

}
