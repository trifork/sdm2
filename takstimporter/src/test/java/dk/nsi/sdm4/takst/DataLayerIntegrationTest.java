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

package dk.nsi.sdm4.takst;

import dk.nsi.sdm4.core.domain.CompleteDataset;
import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import org.apache.commons.io.FileUtils;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.SQLException;

import static org.junit.Assert.assertThat;

/**
 * Integration test of the database access layer. Tests that a dataset can be written to the
 * database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class DataLayerIntegrationTest {
	@Configuration
	@PropertySource({"classpath:test.properties", "classpath:default-config.properties"})
	@Import(TakstTestConfiguration.class)
	static class ContextConfiguration {
		@Bean
		public Persister persister() {
			return new AuditingPersister();
		}

		@Bean
		public TakstParser takstParser() {
			return new TakstParser();
		}
	}

	@Autowired
	private Persister persister;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TakstParser parser;

	@Test
	public void simpleImportOfInitilDataset() throws Exception {
		Takst takst = parse("data/initial");

		persister.persistCompleteDataset(takst.getDatasets().toArray(new CompleteDataset[]{}));

		assertThat(count("Laegemiddel"), Is.is(100));
	}

/*
    @Test
    public void updateANameShouldResultInTheOldRecordBeingInvalidated() throws Exception
    {
        Takst initialDataset = parse("data/takst/initial");
        persister.persistCompleteDataset(initialDataset.getDatasets().toArray(new CompleteDataset[]{}));

        Takst updateDataset = parse("data/takst/update");
        persister.persistCompleteDataset(updateDataset.getDatasets().toArray(new CompleteDataset[]{}));

        int numOfRecords = 100;
        int numOfChangesToExisting = 1;

        // The overwritten record should be kept but have its validity period
        // set to validTo = validFrom.

        assertThat(count("Laegemiddel"), is(numOfRecords + numOfChangesToExisting));

        // The update changes the name 'Kemadrin' to 'Kemadron'.

        ResultSet rs = statement.executeQuery("SELECT * FROM Laegemiddel WHERE DrugName LIKE 'Kemadrin' AND DrugId = 28100009555");
        rs.next();
        assertThat(rs.getTimestamp("ValidTo").getTime(), is(new DateTime(2009, 7, 30, 0, 0).getMillis()));

        rs = statement.executeQuery("SELECT * FROM Laegemiddel WHERE DrugName LIKE 'Kemadron' AND DrugId = 28100009555");
        rs.next();
        assertThat(rs.getTimestamp("ValidTo").getTime(), is(Dates.THE_END_OF_TIME.getTime()));
    }

    @Test
    public void ifARecordIsMissingInANewDatasetTheCoresponsingRecordFromAnyExistingDatasetShouldBeInvalidated() throws Exception
    {
        Takst initDataset = parse("data/takst/initial");
        persister.persistCompleteDataset(initDataset.getDatasets());

        Takst updatedDataset = parse("data/takst/delete");
        persister.persistCompleteDataset(updatedDataset.getDatasets());

        assertThat(count("Laegemiddel"), is(100));

        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Laegemiddel WHERE DrugId = 28100009555");

        rs.next();

        assertThat(rs.getTimestamp("ValidTo").getTime(), is(new DateTime(2009, 7, 31, 0, 0).getMillis()));
    }

    @Test
    public void canImportUdgaaedeNavneSubsetWhereEntriesHaveDifferentLetterCase() throws Exception
    {
        Takst takst = parse("data/takst/udgaaedeNavneTakst");

        persister.persistCompleteDataset(takst.getDatasets());

        connection.commit();

        assertThat(count("UdgaaedeNavne"), is(3));
    }

    @Test
    public void canImportACompleteDatasetWithAllDataTypes() throws Exception
    {
        Takst takst = parse("data/takst/realtakst");

        persister.persistCompleteDataset(takst.getDatasets());

        connection.commit();

        // See these numbers in the system.txt file.

        assertThat(count("Laegemiddel"), is(5492));
        assertThat(count("Pakning"), is(8809));

        // Udgaaede navne is a bit special. Since the keys we are able to
        // construct from the line entries might create duplicates, we might
        // not persist all entries. This is a problem stamdata solves itself
        // (by keeping track of historical data). Removal of UdgaaedeNavne (LMS10)
        // should be considered.

        int totalUdgaaedeNavnRecords = 2547;
        int numDublicateEntriesOnSameDay = 7;

        assertThat(count("UdgaaedeNavne"), is(totalUdgaaedeNavnRecords - numDublicateEntriesOnSameDay));
    }
*/

	//
	// Helpers
	//

	public int count(String tableName) throws SQLException {
		return jdbcTemplate.queryForInt("SELECT COUNT(*) FROM " + tableName);
	}

	private Takst parse(String dir) throws Exception {
		File file = FileUtils.toFile(getClass().getClassLoader().getResource(dir));
		Takst takst = parser.parseFiles(file.listFiles());

		return takst;
	}
}
