package dk.nsi.sdm2.core.persist;

import com.avaje.ebean.EbeanServer;
import dk.nsi.sdm2.core.domain.AbstractRecord;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class RecordPersisterEbean implements RecordPersister {
    @Inject
    EbeanServer ebeanServer;

    public void persist(AbstractRecord record) throws SQLException {
        ebeanServer.save(record);
    }

    @Override
    public void persist(Collection<? extends AbstractRecord> records) throws SQLException {
        ebeanServer.save(records);
    }

    @Override
    public void persist(Record record, RecordSpecification specification) throws SQLException {
        throw new RuntimeException();
    }

    @Override
    public void populateInsertStatement(PreparedStatement preparedStatement, Record record, RecordSpecification recordSpec) throws SQLException {
        throw new RuntimeException();
    }

    @Override
    public String createInsertStatementSql(RecordSpecification specification) {
        throw new RuntimeException();
    }
}
