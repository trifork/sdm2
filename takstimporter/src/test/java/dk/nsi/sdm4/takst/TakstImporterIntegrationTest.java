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

import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.util.Dates;
import dk.nsi.sdm4.takst.config.TakstimporterApplicationConfig;
import dk.nsi.sdm4.takst.model.Doseringskode;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {TakstimporterApplicationConfig.class, TestDbConfiguration.class})
public class TakstImporterIntegrationTest
{
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TakstImporter importer;

	@Autowired
	private AuditingPersister persister;

	@Test
	public void testAreRequiredInputFilesPresent() throws Exception
	{
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/initial/"));

		assertTrue(importer.validateInputStructure(dir.listFiles()));
	}

	@Test
	public void testAreRequiredInputFilesPresent2() throws Exception
	{
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/incomplete/"));

		assertFalse(importer.validateInputStructure(dir.listFiles()));
	}

	@Test
	public void testLaegemiddelDoseringRef() throws Exception
	{
		Date from = Dates.toDate(2008, 01, 01);
		Date to = Dates.toDate(2009, 01, 01);

		Takst takst = new Takst(from, to);

		Doseringskode d = new Doseringskode();
		d.setDoseringskode(1l);
		d.setDrugid(2l);

		List<Doseringskode> dk = new ArrayList<Doseringskode>();
		dk.add(d);

		TakstDataset<Doseringskode> dataset = new TakstDataset<Doseringskode>(takst, dk, Doseringskode.class);
		takst.addDataset(dataset, Doseringskode.class);

		persister.persistCompleteDataset(dataset);
		assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM LaegemiddelDoseringRef"));

		persister.persistCompleteDataset(dataset);
		assertEquals(1, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM LaegemiddelDoseringRef"));
	}
}
