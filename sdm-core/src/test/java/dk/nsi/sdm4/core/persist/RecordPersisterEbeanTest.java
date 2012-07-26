package dk.nsi.sdm4.core.persist;

import com.avaje.ebean.EbeanServer;
import dk.nsi.sdm4.core.domain.AbstractRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RecordPersisterEbeanTest {

    @Mock
    EbeanServer ebeanServer;

    @InjectMocks
    RecordPersisterEbean recordPersister;

    @Test
    public void willStoreRecord() throws Exception {
        AbstractRecord record = new AbstractRecord() { };
        recordPersister.persist(record);
        verify(ebeanServer).save(record);
    }
}
