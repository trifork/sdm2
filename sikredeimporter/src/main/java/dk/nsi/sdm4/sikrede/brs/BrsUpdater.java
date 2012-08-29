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
package dk.nsi.sdm4.sikrede.brs;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.google.common.base.Preconditions;

import dk.nsi.sdm4.core.persistence.recordpersister.Record;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class BrsUpdater {
    static final long NO_EXISTING_RELATIONSHIP = -1;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public BrsUpdater() {
    }

    public void updateRecord(Record record) throws SQLException {
        String hashedCpr = hashCpr((String) record.get("CPRnr"));

        updateExistingRelationship(hashedCpr, (String) record.get("SYdernrGl"), parseSikredeRecordDate((String) record
                .get("SIkraftDatoYderGl")), parseSikredeRecordDate((String) record.get("SIkraftDatoYder")));
        insertRelationship(hashedCpr, (String) record.get("SYdernr"), parseSikredeRecordDate((String) record
                .get("SIkraftDatoYder")), null);
    }

    void updateExistingRelationship(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom,
            DateTime assignedTo) throws SQLException {
        long primaryKeyFromExistingRelationship = openRelationshipExists(patientCpr, doctorOrganisationIdentifier);
        if (primaryKeyFromExistingRelationship == NO_EXISTING_RELATIONSHIP) {
            insertRelationship(patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo);
        } else {
            closeRelationship(primaryKeyFromExistingRelationship, assignedTo);
        }
    }

    long openRelationshipExists(String patientCpr, String doctorOrganisationIdentifier) throws SQLException {
        String querySql = "SELECT pk FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedTo IS NULL";
        Long result;
        try {
            result = jdbcTemplate.queryForLong(querySql, patientCpr, doctorOrganisationIdentifier);
        } catch (EmptyResultDataAccessException norelation) {
            result = NO_EXISTING_RELATIONSHIP;
        }

        return result;
    }

    void closeRelationship(long primaryKey, DateTime assignedTo) throws SQLException {
        String updateSql = "UPDATE AssignedDoctor SET assignedTo = ? WHERE pk = ?";
        jdbcTemplate.update(updateSql, new Date(assignedTo.getMillis()), primaryKey);
    }

    void insertRelationship(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom,
            DateTime assignedTo) throws SQLException {
        String insertSql = "INSERT INTO AssignedDoctor (patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo, reference) VALUES (?, ?, ?, ?, ?)";

        Date assignedToDate = null;
        if (assignedTo != null) {
            assignedToDate = new Date(assignedTo.getMillis());
        }

        jdbcTemplate.update(insertSql, patientCpr, doctorOrganisationIdentifier, new Date(assignedFrom.getMillis()),
                assignedToDate, new DateTime().toString());
    }

    static String hashCpr(String cpr) {
        Preconditions.checkArgument(cpr.length() == 10);
        try {
            return hash(cpr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    static DateTime parseSikredeRecordDate(String date) {
        Preconditions.checkArgument(date.length() == 8);
        return new DateTime(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer
                .parseInt(date.substring(6, 8)), 0, 0, 0);
    }

    private static final String SHA_1 = "SHA-1";

    private static String hash(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_1);
        digest.reset();
        byte[] bytes = digest.digest(string.getBytes());
        return getHex(bytes);
    }

    private static final String HEXES = "0123456789ABCDEF";

    private static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

}
