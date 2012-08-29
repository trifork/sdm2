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


package dk.nsi.sdm4.sks;

import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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
import java.io.IOException;
import java.sql.SQLException;

import static org.apache.commons.io.FileUtils.toFile;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class SksIntegrationTest
{
	@Configuration
	@PropertySource({"classpath:test.properties", "classpath:default-config.properties"})
	@Import(SksTestConfiguration.class)
	static class ContextConfiguration {
		@Bean
		public SKSParser parser() {
			return new SKSParser();
		}

		@Bean
		public Persister persister() {
			return new AuditingPersister();
		}
	}

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	SKSParser importer;

	@Test
	public void canImportTheCorrectNumberOfRecords() throws Throwable
	{
		importer.process(datasetDirWith("data/sks/SHAKCOMPLETE.TXT"));
		
		// FIXME: These record counts are only correct iff if duplicate keys are disregarted.
		// This is unfortunate. Keys are currently only considered based their SKSKode.
		// They should be a combination of type + kode + startdato based on the register doc.
		assertEquals(622, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM Organisation WHERE Organisationstype = 'Sygehus'"));
		assertEquals(8451, jdbcTemplate.queryForInt("SELECT COUNT(*) FROM Organisation WHERE Organisationstype = 'Afdeling'"));
	}

	private File datasetDirWith(String filename) throws IOException {
		File datasetDir = tmpDir.newFolder();
		FileUtils.copyFileToDirectory(getFile(filename), datasetDir);
		return datasetDir;
	}

	private File getFile(String filename) {
		return toFile(getClass().getClassLoader().getResource(filename));
	}
}
