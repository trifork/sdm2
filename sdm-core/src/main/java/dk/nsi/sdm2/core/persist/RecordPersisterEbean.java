package dk.nsi.sdm2.core.persist;

import com.avaje.ebean.EbeanServer;
import dk.nsi.sdm2.core.domain.AbstractRecord;

import javax.inject.Inject;
import java.sql.SQLException;

public class RecordPersisterEbean {
    @Inject
    EbeanServer ebeanServer;

    public void persist(AbstractRecord record) throws SQLException {
        ebeanServer.save(record);
    }
}
