package dk.nsi.sdm2.core.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import dk.nsi.sdm2.core.domain.AbstractRecord;
import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import com.google.common.collect.Lists;

import dk.nsi.sdm2.core.persist.RecordSpecification.FieldSpecification;
import dk.nsi.sdm2.core.util.Preconditions;

public class RecordPersisterImpl implements RecordPersister {
    
    private final Connection connection;
    private final Instant transactionTime;

    public RecordPersisterImpl(Connection connection, Instant transactionTime)
    {
        this.connection = connection;
        this.transactionTime = transactionTime;
    }

    @Override
    public void persist(AbstractRecord record) throws SQLException {
        throw new RuntimeException();
    }

    @Override
    public void persist(Collection<? extends AbstractRecord> record) throws SQLException {
        throw new RuntimeException();
    }

    @Override
    public void persist(Record record, RecordSpecification specification) throws SQLException {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(specification);
        Preconditions.checkArgument(specification.conformsToSpecifications(record));

        // Data dumps from Yderregister and "Sikrede" contains history information and are therefore handled
        // differently from all other register types. The data contained in each input record is appended directly
        // to the database instead of updating existing records.

        PreparedStatement insertRecordStatement = connection.prepareStatement(createInsertStatementSql(specification));

        populateInsertStatement(insertRecordStatement, record, specification);
        insertRecordStatement.execute();

        insertRecordStatement.close();
    }

    @Override
    public void populateInsertStatement(PreparedStatement preparedStatement, Record record,
            RecordSpecification recordSpec) throws SQLException {
        
        Preconditions.checkArgument(recordSpec.conformsToSpecifications(record), "The record does not conform to it's spec.");

        int index = 1;

        for (FieldSpecification fieldSpecification: recordSpec.getFieldSpecs())
        {
            if(fieldSpecification.persistField)
            {
                if (fieldSpecification.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL)
                {
                    preparedStatement.setString(index, (String) record.get(fieldSpecification.name));
                }
                else if (fieldSpecification.type == RecordSpecification.RecordFieldType.NUMERICAL)
                {
                    preparedStatement.setInt(index, (Integer) record.get(fieldSpecification.name));
                }
                else
                {
                    throw new AssertionError("RecordType was not set correctly in the specification");
                }

                index++;
            }
        }

        preparedStatement.setTimestamp(index++, new Timestamp(transactionTime.getMillis()));
        preparedStatement.setTimestamp(index++, new Timestamp(transactionTime.getMillis()));

    }

    @Override
    public String createInsertStatementSql(RecordSpecification specification) {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ").append(specification.getTable()).append(" (");

        List<String> fieldNames = Lists.newArrayList();
        List<String> questionMarks = Lists.newArrayList();

        for (FieldSpecification fieldSpecification: specification.getFieldSpecs())
        {
            if(fieldSpecification.persistField)
            {
                fieldNames.add(fieldSpecification.name);
                questionMarks.add("?");
            }
        }

        fieldNames.add("ValidFrom");
        questionMarks.add("?");

        fieldNames.add("ModifiedDate");
        questionMarks.add("?");

        builder.append(StringUtils.join(fieldNames, ", "));
        builder.append(") VALUES (");
        builder.append(StringUtils.join(questionMarks, ", "));
        builder.append(")");

        return builder.toString();
    }
}
