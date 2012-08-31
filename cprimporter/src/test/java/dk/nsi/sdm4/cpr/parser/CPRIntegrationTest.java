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


package dk.nsi.sdm4.cpr.parser;

import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.util.Dates;
import dk.nsi.sdm4.cpr.config.CprparserApplicationConfig;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import static dk.nsi.sdm4.core.util.Dates.yyyy_MM_dd;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {CprparserApplicationConfig.class, TestDbConfiguration.class})
public class CPRIntegrationTest {
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	@Autowired
	private CPRParser parser;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void canEstablishData() throws Exception {
		Date latestVersionInitial = parser.getLatestVersion();
		assertNull(latestVersionInitial);

		importFile("data/testEtablering/D100313.L431102");

		// When running a full load (file doesn't ends on 01) of CPR no
		// LatestIkraft should be written to the dao.

		Date latestIkraft = parser.getLatestVersion();
		assertNull(latestIkraft);
	}

	@Test
	public void canImportAnUpdate() throws Exception {
		assertEquals("Nothing in database initially", 0, jdbcTemplate.queryForInt("SELECT Count(*) from Person WHERE cpr='1312095098'"));

		importFile("data/D100315.L431101");

		Map<String, Object> rs = jdbcTemplate.queryForMap("SELECT Fornavn, validFrom, validTo from Person WHERE cpr='1312095098'");
		assertEquals("Hjalte", rs.get("Fornavn"));
		assertEquals("2010-03-15 00:00:00.0", rs.get("validFrom").toString());
		assertEquals("2999-12-31 00:00:00.0", rs.get("validTo").toString());

		importFile("data/D100317.L431101");

		SqlRowSet updatedRows = jdbcTemplate.queryForRowSet("SELECT Fornavn, validFrom, validTo from Person WHERE cpr='1312095098' ORDER BY validFrom");
		assertTrue(updatedRows.next());
		assertEquals("Hjalte", updatedRows.getString("Fornavn"));
		assertEquals("2010-03-15 00:00:00.0", updatedRows.getString("validFrom"));
		assertEquals("2010-03-17 00:00:00.0", updatedRows.getString("validTo"));
		assertTrue(updatedRows.next());
		assertEquals("Hjalts", updatedRows.getString("Fornavn"));
		assertEquals("2010-03-17 00:00:00.0", updatedRows.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", updatedRows.getString("validTo"));
		assertFalse(updatedRows.next());
	}


	@Test
	public void jiraNSPSUPPORT_53_ErrorInUpdate() throws Exception {
		importFile("data/D120127.L431101");

		SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * from Person WHERE cpr='0101595072'");
		assertTrue(rs.next());
		assertEquals("K", rs.getString("Koen"));
		assertEquals("0101595072", rs.getString("CPR"));
		assertEquals("Yyyyyyyyyy", rs.getString("Efternavn"));
		assertEquals("Yyyyyy Yyyyy Yyyyyy", rs.getString("Fornavn"));
		assertEquals("2012-01-27 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", rs.getString("validTo"));
		assertFalse(rs.next());
	}

	@Test
	public void jiraNSPSUPPORT_53_ErrorInUpdate2() throws Exception {
		importFile("data/D120128.L431101");

		SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * from Person WHERE cpr='1005400925'");
		assertTrue(rs.next());
		assertEquals("M", rs.getString("Koen"));
		assertEquals("1005400925", rs.getString("CPR"));
		assertEquals("Jensen", rs.getString("Efternavn"));
		assertEquals("Pål", rs.getString("Fornavn"));
		assertEquals("2012-01-27 00:00:00.0", rs.getString("validFrom"));
		assertEquals("2999-12-31 00:00:00.0", rs.getString("validTo"));
		assertFalse(rs.next());
	}

	@Test(expected = ParserException.class)
	public void failsWhenEndRecordAppearsBeforeEndOfFile() throws Exception {
		importFile("data/endRecords/D100314.L431101");
	}


	@Test(expected = ParserException.class)
	public void failsWhenNoEndRecordExists() throws Exception {
		importFile("data/endRecords/D100315.L431101");
	}

	@Test
	public void canImportPersonNavnebeskyttelse() throws Exception {
		importFile("data/testCPR1/D100314.L431101");

		SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM Person WHERE CPR = '0101965058'");
		rs.next();

		assertEquals("K", rs.getString("Koen"));
		assertEquals("Ude Ulrike", rs.getString("Fornavn"));
		assertEquals("", rs.getString("Mellemnavn"));
		assertEquals("Udtzen", rs.getString("Efternavn"));
		assertEquals("", rs.getString("CoNavn"));
		assertEquals("", rs.getString("Lokalitet"));
		assertEquals("Søgade", rs.getString("Vejnavn"));
		assertEquals("", rs.getString("Bygningsnummer"));
		assertEquals("16", rs.getString("Husnummer"));
		assertEquals("1", rs.getString("Etage"));
		assertEquals("", rs.getString("SideDoerNummer"));
		assertEquals("Vodskov", rs.getString("Bynavn"));
		assertEquals("9000", rs.getString("Postnummer"));
		assertEquals("Aalborg", rs.getString("PostDistrikt"));
		assertEquals("01", rs.getString("Status"));
		assertEquals(yyyy_MM_dd.parseDateTime("1997-09-09").toDate(), rs.getDate("NavneBeskyttelseStartDato"));
		assertEquals(yyyy_MM_dd.parseDateTime("2001-02-20").toDate(), rs.getDate("NavneBeskyttelseSletteDato"));
		assertEquals("", rs.getString("GaeldendeCPR"));
		assertEquals(yyyy_MM_dd.parseDateTime("1896-01-01").toDate(), rs.getDate("Foedselsdato"));
		assertEquals("Pensionist", rs.getString("Stilling"));
		assertEquals("8511", rs.getString("VejKode"));
		assertEquals("851", rs.getString("KommuneKode"));
		assertEquals(yyyy_MM_dd.parseDateTime("2001-11-16").toDate(), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parseDateTime("2999-12-31").toDate(), rs.getDate("ValidTo"));
		assertTrue(rs.last());
	}

	@Test
	public void canImportForaeldreMyndighedBarn() throws Exception {
		importFile("data/testForaeldremyndighed/D100314.L431101");

		SqlRowSet rs = jdbcTemplate.queryForRowSet("Select * from Person where CPR='3112970028'");

		rs.next();

		assertTrue(rs.last());

		rs = jdbcTemplate.queryForRowSet("SELECT * FROM BarnRelation WHERE BarnCPR='3112970028'");

		rs.next();

		assertEquals("0702614082", rs.getString("CPR"));
		assertTrue(rs.last());

		rs = jdbcTemplate.queryForRowSet("SELECT * FROM ForaeldreMyndighedRelation WHERE CPR='3112970028' ORDER BY TypeKode");

		rs.next();

		assertEquals("0003", rs.getString("TypeKode"));
		assertEquals("Mor", rs.getString("TypeTekst"));
		assertEquals("", rs.getString("RelationCpr"));
		assertEquals(yyyy_MM_dd.parseDateTime("2008-01-01").toDate(), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parseDateTime("2999-12-31").toDate(), rs.getDate("ValidTo"));

		rs.next();

		assertEquals("0004", rs.getString("TypeKode"));
		assertEquals("Far", rs.getString("TypeTekst"));
		assertEquals("", rs.getString("RelationCpr"));
		assertEquals(yyyy_MM_dd.parseDateTime("2008-01-01").toDate(), rs.getDate("ValidFrom"));
		assertEquals(yyyy_MM_dd.parseDateTime("2999-12-31").toDate(), rs.getDate("ValidTo"));

		assertTrue(rs.last());
	}


	@Test
	public void canImportUmyndighedVaerge() throws Exception {
		importFile("data/testUmyndigVaerge/D100314.L431101");

		SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM UmyndiggoerelseVaergeRelation WHERE CPR='0709614126'");

		rs.next();

		assertEquals("0001", rs.getString("TypeKode"));
		assertEquals("Værges CPR findes", rs.getString("TypeTekst"));
		assertEquals("0904414131", rs.getString("RelationCpr"));
		assertEquals(yyyy_MM_dd.parseDateTime("2000-02-28").toDate(), rs.getDate("RelationCprStartDato"));
		assertEquals("", rs.getString("VaergesNavn"));
		assertEquals(null, rs.getDate("VaergesNavnStartDato"));
		assertEquals("", rs.getString("RelationsTekst1"));
		assertEquals("", rs.getString("RelationsTekst2"));
		assertEquals("", rs.getString("RelationsTekst3"));
		assertEquals("", rs.getString("RelationsTekst4"));
		assertEquals("", rs.getString("RelationsTekst5"));
		assertEquals(yyyy_MM_dd.parseDateTime("2000-02-28").toDate(), rs.getDate("ValidFrom"));
		assertEquals(Dates.THE_END_OF_TIME, rs.getDate("ValidTo"));
		assertTrue(rs.last());
	}


	@Test
	public void ImportU12160Test() throws Exception {
		importFile("data/D100312.L431101");

		SqlRowSet rs = jdbcTemplate.queryForRowSet("Select COUNT(*) from Person");
		rs.next();
		assertEquals(100, rs.getInt(1));

		rs = jdbcTemplate.queryForRowSet("Select COUNT(*) from BarnRelation");
		rs.next();
		assertEquals(30, rs.getInt(1));

		rs = jdbcTemplate.queryForRowSet("Select COUNT(*) from ForaeldreMyndighedRelation");
		rs.next();
		assertEquals(4, rs.getInt(1));

		rs = jdbcTemplate.queryForRowSet("Select COUNT(*) from UmyndiggoerelseVaergeRelation");
		rs.next();
		assertEquals(1, rs.getInt(1));

		// Check Address protection

		rs = jdbcTemplate.queryForRowSet("SELECT COUNT(*) FROM Person WHERE NavneBeskyttelseStartDato < NOW() AND NavneBeskyttelseSletteDato > NOW()");
		rs.next();
		assertEquals(1, rs.getInt(1));
	}


	@Test
	public void ImportU12170Test() throws Exception {
		importFile("data/D100313.L431101");

		SqlRowSet rs = jdbcTemplate.queryForRowSet("Select COUNT(*) from Person");
		rs.next();
		assertEquals(80, rs.getInt(1));

		rs = jdbcTemplate.queryForRowSet("SELECT COUNT(*) FROM BarnRelation");
		rs.next();
		assertEquals(29, rs.getInt(1));

		rs = jdbcTemplate.queryForRowSet("SELECT COUNT(*) FROM ForaeldreMyndighedRelation");
		rs.next();
		assertEquals(5, rs.getInt(1));

		rs = jdbcTemplate.queryForRowSet("SELECT COUNT(*) FROM UmyndiggoerelseVaergeRelation");
		rs.next();
		assertEquals(2, rs.getInt(1));

		// Check Address protection
		rs = jdbcTemplate.queryForRowSet("SELECT COUNT(*) FROM Person WHERE NavneBeskyttelseStartDato < NOW() AND NavneBeskyttelseSletteDato > NOW()");
		rs.next();
		assertEquals(1, rs.getInt(1));
	}


	@Test
	public void shouldUpdateTheCPRChangesTableForTheCPRGOSService() throws Exception {
		importFile("data/PVIT/D100314.L431101");

		SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM ChangesToCPR ORDER BY CPR");
		rs.next();

		assertThat(rs.getString("CPR"), is("0705314543"));
		assertThat(rs.getTimestamp("ModifiedDate"), is(notNullValue()));
		Timestamp t1 = rs.getTimestamp("ModifiedDate");

		rs.next();

		assertThat(rs.getString("CPR"), is("0705314545"));
		assertThat(rs.getTimestamp("ModifiedDate"), is(notNullValue()));
		Timestamp t2 = rs.getTimestamp("ModifiedDate");

		// To make sure the timestamp is updated we wait a second
		// since the granularity is 1 sec.

		Thread.sleep(1000);

		importFile("data/PVIT/D100315.L431101");

		rs = jdbcTemplate.queryForRowSet("SELECT * FROM ChangesToCPR ORDER BY CPR");
		rs.next();

		assertThat(rs.getString("CPR"), is("0705314543"));
		assertThat(rs.getTimestamp("ModifiedDate"), is(t1));

		rs.next();

		assertThat(rs.getString("CPR"), is("0705314545"));
		assertTrue(rs.getTimestamp("ModifiedDate").after(t2));

		rs.next();

		assertThat(rs.getString("CPR"), is("0705314547"));
		assertThat(rs.getTimestamp("ModifiedDate"), is(notNullValue()));

		assertFalse(rs.next());
	}

	private void importFile(String filePath) throws Exception {
		URL resource = getClass().getClassLoader().getResource(filePath);
		File datasetDir = tmpDir.newFolder();
		FileUtils.copyURLToFile(resource, new File(datasetDir, lastPathSegment(filePath)));

		parser.process(datasetDir);
	}

	private String lastPathSegment(String filePath) {
		String[] segments = filePath.split("/");

		return segments[segments.length - 1];
	}
}
