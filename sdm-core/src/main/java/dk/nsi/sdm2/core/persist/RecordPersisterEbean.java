package dk.nsi.sdm2.core.persist;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RecordPersisterEbean implements RecordPersister {

    //@Inject
    DataSource dataSource;


    @Override
    public void persist(Record record, RecordSpecification specification) throws SQLException {
    }

    @Override
    public void populateInsertStatement(PreparedStatement preparedStatement, Record record, RecordSpecification recordSpec) throws SQLException {
    }

    @Override
    public String createInsertStatementSql(RecordSpecification specification) {
        return null;
    }
}
