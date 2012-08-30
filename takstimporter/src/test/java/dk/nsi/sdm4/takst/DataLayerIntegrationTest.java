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
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.core.util.Dates;
import dk.nsi.sdm4.takst.config.TakstimporterApplicationConfig;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Integration test of the database access layer. Tests that a dataset can be written to the
 * database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {TakstimporterApplicationConfig.class, TestDbConfiguration.class})
public class DataLayerIntegrationTest {
	@Autowired
	private Persister persister;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TakstParser parser;

	@Test
	public void simpleImportOfInitialDataset() throws Exception {
		Takst takst = parse("data/initial");

		persister.persistCompleteDataset(takst.getDatasets().toArray(new CompleteDataset[]{}));

		assertThat(count("Laegemiddel"), Is.is(100));
	}

	@Test
	public void updateANameShouldResultInTheOldRecordBeingInvalidated() throws Exception {
		Takst initialDataset = parse("data/initial");
		persister.persistCompleteDataset(initialDataset.getDatasets().toArray(new CompleteDataset[]{}));

		Takst updateDataset = parse("data/update");
		persister.persistCompleteDataset(updateDataset.getDatasets().toArray(new CompleteDataset[]{}));

		int numOfRecords = 100;
		int numOfChangesToExisting = 1;

		// The overwritten record should be kept but have its validity period
		// set to validTo = validFrom.

		assertThat(count("Laegemiddel"), is(numOfRecords + numOfChangesToExisting));

		// The update changes the name 'Kemadrin' to 'Kemadron'.

		Timestamp validTo = jdbcTemplate.queryForObject("SELECT ValidTo FROM Laegemiddel WHERE DrugName LIKE 'Kemadrin' AND DrugId = 28100009555", Timestamp.class);
		assertThat(validTo.getTime(), is(new DateTime(2009, 7, 30, 0, 0).getMillis()));

		Timestamp validToKemadron = jdbcTemplate.queryForObject("SELECT ValidTo FROM Laegemiddel WHERE DrugName LIKE 'Kemadron' AND DrugId = 28100009555", Timestamp.class);
		assertThat(validToKemadron.getTime(), is(Dates.THE_END_OF_TIME.getTime()));
	}

	@Test
	public void ifARecordIsMissingInANewDatasetTheCoresponsingRecordFromAnyExistingDatasetShouldBeInvalidated() throws Exception {
		Takst initDataset = parse("data/initial");
		persister.persistCompleteDataset(initDataset.getDatasets().toArray(new CompleteDataset[]{}));

		Takst updatedDataset = parse("data/delete");
		persister.persistCompleteDataset(updatedDataset.getDatasets().toArray(new CompleteDataset[]{}));

		assertThat(count("Laegemiddel"), is(100));

		Timestamp validTo = jdbcTemplate.queryForObject("SELECT ValidTo FROM Laegemiddel WHERE DrugId = 28100009555", Timestamp.class);
		assertThat(validTo.getTime(), is(new DateTime(2009, 7, 31, 0, 0).getMillis()));
	}

	@Test
	public void canImportUdgaaedeNavneSubsetWhereEntriesHaveDifferentLetterCase() throws Exception {
		Takst takst = parse("data/udgaaedeNavneTakst");

		persister.persistCompleteDataset(takst.getDatasets().toArray(new CompleteDataset[]{}));

		assertThat(count("UdgaaedeNavne"), is(3));
	}

	@Test
	public void canImportACompleteDatasetWithAllDataTypes() throws Exception {
		Takst takst = parse("data/realtakst");

		persister.persistCompleteDataset(takst.getDatasets().toArray(new CompleteDataset[]{}));

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
