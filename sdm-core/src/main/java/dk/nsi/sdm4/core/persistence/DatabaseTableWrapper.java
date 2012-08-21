/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */

package dk.nsi.sdm4.core.persistence;

import com.google.common.base.Preconditions;
import dk.nsi.sdm4.core.domain.Entities;
import dk.nsi.sdm4.core.domain.TemporalEntity;
import dk.nsi.sdm4.core.util.Dates;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static dk.nsi.sdm4.core.util.Dates.toSqlDate;

/**
 * @author Rune Skou Larsen <rsj@trifork.com>
 */
@Deprecated
public class DatabaseTableWrapper<T extends TemporalEntity>
{
    private final PreparedStatement insertRecordStmt;
    private final PreparedStatement insertAndUpdateRecordStmt;
    private final PreparedStatement updateRecordStmt;
    private final PreparedStatement selectConflictsStmt;
    private final PreparedStatement updateValidToStmt;
    private final PreparedStatement updateValidFromStmt;

    private final Connection connection;
	private final DataSource datasource;

	private ResultSet currentRS;
    private Class<T> type;
    private String tablename;
    private Method idMethod;

    private List<Method> outputMethods;
    private List<String> notUpdatedColumns;

    public DatabaseTableWrapper(DataSource datasource, Class<T> type) throws SQLException
    {
        this.tablename = Entities.getEntityTypeDisplayName(type);
        this.type = type;
	    this.datasource = datasource;
	    this.connection = DataSourceUtils.getConnection(datasource); // gets the transactionmanager's connection, not just a new one

	    this.idMethod = Entities.getIdMethod(type);
        this.outputMethods = Entities.getOutputMethods(type);
        
        this.notUpdatedColumns = findNotUpdatedColumns();
        this.insertRecordStmt = prepareInsertStatement();
        this.insertAndUpdateRecordStmt = prepareInsertAndUpdateStatement();
        this.updateRecordStmt = prepareUpdateStatement();
        this.selectConflictsStmt = prepareSelectConflictsStatement();
        this.updateValidToStmt = prepareUpdateValidToStatement();
        this.updateValidFromStmt = prepareUpdateValidFromStatement();
    }
    
    private PreparedStatement prepareInsertStatement() throws SQLException
    {
        String sql = "INSERT INTO " + tablename + " (ModifiedDate, CreatedDate, ValidFrom, ValidTo";
        for (Method method : outputMethods)
        {
            sql += ", ";
            sql += Entities.getColumnName(method);
        }
        sql += ") VALUES (";
        sql += "?,"; // modifieddate
        sql += "?,"; // createddate
        sql += "?,"; // validfrom
        sql += "?"; // validto

        for (int i = 0; i < outputMethods.size(); i++)
        {
            sql += ",?";
        }

        sql += ")";

        return connection.prepareStatement(sql);
    }

    private PreparedStatement prepareInsertAndUpdateStatement() throws SQLException
    {
        String sql = "INSERT INTO " + tablename + " (ModifiedDate, CreatedDate, ValidFrom, ValidTo";
        
        for (Method method : outputMethods)
        {
            sql += ", ";
            String name = Entities.getOutputFieldName(method);
            sql += name;
        }

        for (String notUpdateName : notUpdatedColumns)
        {
            sql += ", " + notUpdateName;
        }

        sql += ") VALUES (?, ?, ?, ?";

        for (int i = 0; i < outputMethods.size() + notUpdatedColumns.size(); i++)
        {
            sql += ",?";
        }

        sql += ")";

        return connection.prepareStatement(sql);
    }

    private PreparedStatement prepareUpdateStatement() throws SQLException
    {
        String sql = "UPDATE " + tablename + " SET ModifiedDate = ?, ValidFrom = ?, ValidTo = ?";

        for (Method method : outputMethods)
        {
            sql += ", " + Entities.getOutputFieldName(method) + " = ?";
        }

        sql += " WHERE " + Entities.getIdColumnName(type) + " = ? AND ValidFrom = ? AND ValidTo = ?";

        return connection.prepareStatement(sql);
    }


    private PreparedStatement prepareSelectConflictsStatement() throws SQLException
    {
        // Select where IDs match and validity intervals overlap.

        String keyColumn = Entities.getIdColumnName(type);
        String sql = "SELECT * FROM " + tablename + " WHERE " + keyColumn + " = ? AND NOT (ValidTo < ? OR ValidFrom > ?) ORDER BY ValidTo";
        return connection.prepareStatement(sql);
    }

    public void insertEntity(TemporalEntity entity, Date transactionTime) throws Exception
    {
        applyParamsToInsertStatement(insertRecordStmt, entity, transactionTime, transactionTime);
        insertRecordStmt.execute();
    }

    public void insertAndUpdateRow(TemporalEntity entity, Date transactionTime) throws Exception
    {
        applyParamsToInsertAndUpdateStatement(insertAndUpdateRecordStmt, entity, transactionTime, transactionTime);
        insertAndUpdateRecordStmt.execute();
    }

    public void updateRow(T entity, Date transactionTime, Date existingValidFrom, Date existingValidTo) throws Exception
    {
        applyParamsToUpdateStatement(updateRecordStmt, entity, transactionTime, transactionTime, existingValidFrom, existingValidTo);
        updateRecordStmt.execute();
    }

    private PreparedStatement prepareUpdateValidToStatement() throws SQLException
    {
        String sql = "UPDATE " + tablename + " SET ValidTo = ?, ModifiedDate = ? WHERE " + Entities.getIdColumnName(type) + " = ? AND ValidFrom = ?";

        return connection.prepareStatement(sql);
    }

    private PreparedStatement prepareUpdateValidFromStatement() throws SQLException
    {
        String sql = "UPDATE " + tablename + " SET ValidFrom = ?, ModifiedDate = ? WHERE " + Entities.getIdColumnName(type) + " = ? AND ValidFrom = ?";

        return connection.prepareStatement(sql);
    }

    public int applyParamsToInsertStatement(PreparedStatement statement, TemporalEntity entity, Date transactionTime, Date createdTime) throws Exception
    {
        int idx = 1;

        statement.setTimestamp(idx++, new Timestamp(transactionTime.getTime()));
        statement.setTimestamp(idx++, new Timestamp(createdTime.getTime()));
        statement.setTimestamp(idx++, new Timestamp(entity.getValidFrom().getTime()));
        statement.setTimestamp(idx++, new Timestamp(entity.getValidTo().getTime()));

        for (Method method : outputMethods)
        {
            Object o = method.invoke(entity);
            statement.setObject(idx++, o);
        }

        return idx;
    }
    
    public int applyParamsToInsertAndUpdateStatement(PreparedStatement pstmt, TemporalEntity sde, Date transactionTime, Date createdTime) throws Exception
    {
        int idx = applyParamsToInsertStatement(pstmt, sde, transactionTime, createdTime);
        
        currentRS.last();
        
        for (String notUpdateName : notUpdatedColumns)
        {
            Object o = currentRS.getObject(notUpdateName);
            pstmt.setObject(idx++, o);
        }
        
        return idx;
    }

    public void applyParamsToUpdateStatement(PreparedStatement statement, TemporalEntity entity, Date transactionTime, Date createdTime, Date existingValidFrom, Date existingValidTo) throws Exception
    {
        int idx = 1;
        Object key = Entities.getEntityID(entity);

        statement.setObject(idx++, transactionTime);
        statement.setObject(idx++, entity.getValidFrom());
        statement.setObject(idx++, entity.getValidTo());

        for (Method method : outputMethods)
        {
            Object o = method.invoke(entity);
            statement.setObject(idx++, o);
        }

        updateValidToStmt.setObject(3, getCurrentRS().getObject(Entities.getIdColumnName(type)));
        statement.setObject(idx++, key);
        statement.setObject(idx++, existingValidFrom);
        statement.setObject(idx++, existingValidTo);
    }


    public boolean fetchEntityConflicts(Object id, Date validFrom, Date validTo) throws SQLException
    {
        if (getCurrentRS() != null) getCurrentRS().close();

        // If at least one version of the entity was found with the
        // specified id in the specified [validFrom;validTo[ range.

        selectConflictsStmt.setObject(1, id);
        selectConflictsStmt.setObject(2, validFrom);
        selectConflictsStmt.setObject(3, validTo);
        currentRS = selectConflictsStmt.executeQuery();

        return getCurrentRS().next();
    }


    public boolean fetchEntitiesInRange(Date validFrom, Date validTo) throws SQLException
    {
        // FIXME: If we don't clean up the memory requirement is enormous 5GB+.

        String sql = "SELECT * FROM " + tablename + " WHERE NOT (ValidTo < '" + Dates.toSqlDate(validFrom) + "' OR ValidFrom > '" + Dates.toSqlDate(validTo) + "')";
        currentRS = connection.createStatement().executeQuery(sql);
        
        return getCurrentRS().next();
    }


    public Date getCurrentRowValidFrom() throws SQLException
    {
        // We do the conversion from time stamp to date because
        // the two types cannot be compared easily otherwise.

        return new Date(getCurrentRS().getTimestamp("ValidFrom").getTime());
    }


    public Date getCurrentRowValidTo() throws SQLException
    {
        return new Date(getCurrentRS().getTimestamp("ValidTo").getTime());
    }


    public boolean moveToNextRow() throws SQLException
    {
        return getCurrentRS().next();
    }

    public void copyCurrentRowButWithChangedValidFrom(Date validFrom, Date transactionTime) throws SQLException
    {
        // FIXME: This should be done with a INSERT .. SELECT statement to avoid transporting the
        // entire record.

        String sql = "INSERT INTO " + tablename + " (ModifiedDate, CreatedDate, ValidFrom, ValidTo";

        for (Method method : outputMethods)
        {
            sql += ", ";
            String name = Entities.getOutputFieldName(method);
            sql += name;
        }

        for (String notUpdateName : notUpdatedColumns)
        {
            sql += ", " + notUpdateName;
        }

        sql += ") VALUES (";
        sql += "'" + toSqlDate(transactionTime) + "',"; // modifieddate
        sql += "'" + toSqlDate(transactionTime) + "',"; // createddate
        sql += "'" + toSqlDate(validFrom) + "',"; // validfrom
        sql += "'" + toSqlDate(getCurrentRS().getTimestamp("ValidTo")) + "'"; // validTo

        for (int i = 0; i < outputMethods.size(); i++)
        {
            sql += ",?";
        }

        for (int i = 0; i < notUpdatedColumns.size(); i++)
        {
            sql += ",?";
        }

        sql += ")";

        PreparedStatement stmt = connection.prepareStatement(sql);
        int idx = 1;

        for (Method method : outputMethods)
        {
            stmt.setObject(idx++, getCurrentRS().getObject(Entities.getOutputFieldName(method)));
        }

        for (String notUpdateName : notUpdatedColumns)
        {
            stmt.setObject(idx++, getCurrentRS().getObject(notUpdateName));
        }

        stmt.executeUpdate();
    }

    public void updateValidToOnCurrentRow(Date validTo, Date transactionTime) throws SQLException
    {
        updateValidToStmt.setObject(1, validTo);
        updateValidToStmt.setObject(2, transactionTime);
        updateValidToStmt.setObject(3, getCurrentRS().getObject(Entities.getIdColumnName(type)));
        updateValidToStmt.setObject(4, getCurrentRS().getTimestamp("ValidFrom"));

        // This can potentially hit several rows when validFrom equals validTo
        // when a record is "closed".
        
        updateValidToStmt.executeUpdate();
    }

    public void updateValidFromOnCurrentRow(Date validFrom, Date transactionTime) throws SQLException
    {
        updateValidFromStmt.setObject(1, validFrom);
        updateValidFromStmt.setObject(2, transactionTime);
        updateValidFromStmt.setObject(3, getCurrentRS().getObject(Entities.getIdColumnName(type)));
        updateValidFromStmt.setObject(4, getCurrentRS().getTimestamp("ValidFrom"));

        // This can potentially hit several rows when validFrom equals validTo
        // when a record is "closed".
        
        updateValidFromStmt.executeUpdate();
    }

    public boolean currentRowEquals(TemporalEntity entity) throws Exception
    {
        for (Method method : outputMethods)
        {
            if (!fieldEqualsCurrentRow(method, entity)) return false;
        }

        return true;
    }

    private boolean fieldEqualsCurrentRow(Method method, TemporalEntity entity) throws Exception
    {
        String fieldname = Entities.getOutputFieldName(method);

        Object o = method.invoke(entity);

        if (o instanceof String)
        {
            String value = getCurrentRS().getString(fieldname);
            // Null strings and empty strings are the same
            if (value == null && ((String) o).trim().isEmpty())
                return true;
            if (!o.equals(value))
                return false;
        }
        else if (o instanceof Integer)
        {
            Integer value = getCurrentRS().getInt(fieldname);
            if (!o.equals(value))
                return false;
        }
        else if (o instanceof Long)
        {
            Long value = getCurrentRS().getLong(fieldname);
            if (!o.equals(value))
                return false;
        }
        else if (o instanceof Double)
        {
            Double value = getCurrentRS().getDouble(fieldname);
            if (!o.equals(value))
                return false;
        }
        else if (o instanceof Boolean)
        {
            Boolean value = getCurrentRS().getInt(fieldname) != 0;
            if (!o.equals(value))
                return false;
        }
        else if (o instanceof Date)
        {
            Timestamp ts = getCurrentRS().getTimestamp(fieldname);
            if (ts == null)
                return false;
            long millis = ts.getTime();
            if (millis != ((Date) o).getTime())
                return false;
        }
        else if (o == null)
        {
            Object value = getCurrentRS().getObject(fieldname);
            if (value != null)
                return false;
        }
        else
        {
            String message = "method " + Entities.getEntityTypeDisplayName(type) + "." + method.getName() + " has unsupported return type: " + o + ". DB mapping unknown.";
            throw new Exception(message);
        }

        return true;
    }

    public List<StamdataEntityVersion> findEntitiesInRange(Date validFrom, Date validTo) throws SQLException
    {
        String sql = "SELECT " + Entities.getOutputFieldName(idMethod) + ", validFrom FROM " + tablename + " WHERE NOT (validTo < '" + toSqlDate(validFrom) + "' OR validFrom > '" + toSqlDate(validTo) + "')";
        currentRS = connection.createStatement().executeQuery(sql);

        List<StamdataEntityVersion> versions = new ArrayList<StamdataEntityVersion>();

        while (getCurrentRS().next())
        {
            StamdataEntityVersion version = new StamdataEntityVersion();
            version.id = getCurrentRS().getObject(1);
            version.validFrom = getCurrentRS().getTimestamp(2);
            versions.add(version);
        }

        return versions;
    }

    public static class StamdataEntityVersion
    {
        public Object id;
        public Date validFrom;
    }

    public void updateValidToOnEntityVersion(Date validTo, StamdataEntityVersion versions, Date transactionTime) throws SQLException
    {
        updateValidToStmt.setTimestamp(1, new Timestamp(validTo.getTime()));
        updateValidToStmt.setTimestamp(2, new Timestamp(transactionTime.getTime()));
        updateValidToStmt.setObject(3, versions.id);
        updateValidToStmt.setTimestamp(4, new Timestamp(versions.validFrom.getTime()));

        int rowsAffected = updateValidToStmt.executeUpdate();

        Preconditions.checkState(rowsAffected == 1, "updateValidToStmt completeXml number of rows updated - expected=1, actual=" + rowsAffected);
    }

    /**
     * Get a list with all columns that will not be updated by the Entity Entities don't have to be
     * complete. They can update only parts of a table and then the rest have to be copied as not
     * changed.
     */
    public List<String> findNotUpdatedColumns() throws SQLException
    {
        // NOTE: The only reason why this method exists is because
        // the Person table spans several entities.

        ArrayList<String> results = new ArrayList<String>();
        Statement stm = connection.createStatement();
        ResultSet rs = stm.executeQuery("desc " + tablename);

        while (rs.next())
        {
            String colName = rs.getString(1);

            // Ignore all system columns

            if (colName.toUpperCase().indexOf("PID") > 0)
                continue;
            if (colName.equalsIgnoreCase("ModifiedDate"))
                continue;
            if (colName.equalsIgnoreCase("CreatedDate"))
                continue;
            if (colName.equalsIgnoreCase("ValidFrom"))
                continue;
            if (colName.equalsIgnoreCase("ValidTo"))
                continue;

            boolean found = false;
            for (Method method : outputMethods)
            {
                // Ignore the columns that are updated by the entity

                String name = Entities.getOutputFieldName(method);
                if (colName.equalsIgnoreCase(name))
                {
                    found = true;
                    continue;
                }
            }

            if (!found)
            {
                results.add(colName);
            }
        }

        rs.close();
        stm.close();

        return results;
    }
    
    public void close() throws SQLException
    {
        insertAndUpdateRecordStmt.close();
        updateRecordStmt.close();
        selectConflictsStmt.close();
        updateValidToStmt.close();
        updateValidFromStmt.close();
	    DataSourceUtils.releaseConnection(connection, datasource);
        
        System.gc();
    }

    public ResultSet getCurrentRS()
    {
        return currentRS;
    }
}
