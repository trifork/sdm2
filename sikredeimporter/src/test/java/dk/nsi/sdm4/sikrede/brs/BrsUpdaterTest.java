package dk.nsi.sdm4.sikrede.brs;

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

import dk.nsi.sdm4.sikrede.config.SikredeimporterApplicationConfig;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {SikredeimporterApplicationConfig.class, TestDbConfiguration.class})
public class BrsUpdaterTest {
    private static final DateTime ASSIGNED_FROM = new DateTime(2011, 10, 10, 0, 0);
    private static final DateTime ASSIGNED_TO = new DateTime(2011, 11, 10, 0, 0);

    private String examplePatientCpr;
    private String exampleDoctorOrganisationIdentifier;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BrsUpdater brsUpdater;

    @Before
    public void setup() {
        examplePatientCpr = "1234567890123456789012345678901234567890";
        exampleDoctorOrganisationIdentifier = "123456";

    }

    @Test
    public void testHashCpr() {
        String hashedCpr = BrsUpdater.hashCpr("1234567890");
        assertEquals(40, hashedCpr.length());
    }

    @Test
    public void testParseSikredeRecordDate() {
        DateTime parsedDate = BrsUpdater.parseSikredeRecordDate("20110507");
        DateTime expectedDate = new DateTime(2011, 5, 7, 0, 0, 0);
        assertEquals(expectedDate, parsedDate);
    }

    @Test
    public void testOpenRelationshipExistsWhenNoRecords() throws SQLException {
        assertEquals(BrsUpdater.NO_EXISTING_RELATIONSHIP, brsUpdater.openRelationshipExists(examplePatientCpr,
                exampleDoctorOrganisationIdentifier));
    }

    @Test
    public void testOpenRelationshipExistsWhenRecordWhichHasOtherDoctorOrganisationIdExists() throws SQLException {
        brsUpdater.insertRelationship(examplePatientCpr, "654321", ASSIGNED_FROM, null);
        assertEquals(BrsUpdater.NO_EXISTING_RELATIONSHIP, brsUpdater.openRelationshipExists(examplePatientCpr,
                exampleDoctorOrganisationIdentifier));
    }

    @Test
    public void testOpenRelationshipExistsWhenRecordThatIsClosedIsFoundInDatabase() throws SQLException {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM,
                ASSIGNED_TO);
        assertEquals(BrsUpdater.NO_EXISTING_RELATIONSHIP, brsUpdater.openRelationshipExists(examplePatientCpr,
                exampleDoctorOrganisationIdentifier));
    }

    @Test
    public void testOpenRelationshipExistsWhenMatchingRecordInDatabase() throws SQLException {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, null);
        assertTrue(BrsUpdater.NO_EXISTING_RELATIONSHIP != brsUpdater.openRelationshipExists(examplePatientCpr,
                exampleDoctorOrganisationIdentifier));
    }

    @Test
    public void testCloseRelationship() throws SQLException {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, null);
        long primaryKeyOfOpenRelationship = brsUpdater.openRelationshipExists(examplePatientCpr,
                exampleDoctorOrganisationIdentifier);
        brsUpdater.closeRelationship(primaryKeyOfOpenRelationship, ASSIGNED_TO);
        assertClosedRelationship(primaryKeyOfOpenRelationship);
    }

    @Test
    public void testUpdateExistingRelationshipWhenDoctorDoesNotExist() throws SQLException {
        brsUpdater.updateExistingRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM,
                ASSIGNED_TO);
        assertClosedRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier);
    }

    @Test
    public void testUpdateExistingRelationshipWhenDoctorExistWithOpenRelationship() throws SQLException {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, null);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM);
        brsUpdater.updateExistingRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM
                .minusDays(1), ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
    }

    @Test
    public void testUpdateExistingRelationshipWhenDoctorExistWithClosedRelationship() throws SQLException {
        brsUpdater.insertRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM,
                ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        brsUpdater.updateExistingRelationship(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_TO
                .plusYears(2), ASSIGNED_TO.plusYears(3));
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_FROM, ASSIGNED_TO);
        assertRecordExists(examplePatientCpr, exampleDoctorOrganisationIdentifier, ASSIGNED_TO.plusYears(2),
                ASSIGNED_TO.plusYears(3));
    }

    // //////////////////////

    private void assertRecordExists(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom, DateTime assignedTo) {

        String sql = "SELECT * FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedFrom = ? AND assignedTo = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, patientCpr, doctorOrganisationIdentifier, new Date(assignedFrom.getMillis()), new Date(assignedTo.getMillis()));
        assertTrue(rs.next());
        
    }

    private void assertRecordExists(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom) {
        
        String sql = "SELECT * FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedFrom = ? AND assignedTo IS NULL";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, patientCpr, doctorOrganisationIdentifier, new Date(assignedFrom.getMillis()));
        assertTrue(rs.next());
    }

    private void assertClosedRelationship(long pk) {
        
        String sql = "SELECT * FROM AssignedDoctor WHERE pk = ? AND assignedTo IS NOT NULL";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, pk);
        assertTrue(rs.next());
    }

    private void assertClosedRelationship(String patientCpr, String doctorOrganisationIdentifier) {

        String sql = "SELECT * FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedTo IS NOT NULL";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, patientCpr, doctorOrganisationIdentifier);
        assertTrue(rs.next());
    }
}
