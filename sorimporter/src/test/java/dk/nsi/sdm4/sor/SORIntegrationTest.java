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
package dk.nsi.sdm4.sor;

import dk.nsi.sdm4.core.persistence.AuditingPersister;
import dk.nsi.sdm4.core.persistence.Persister;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
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
import java.net.URL;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class SORIntegrationTest {
	@Configuration
	@PropertySource({"classpath:test.properties", "classpath:default-config.properties"})
	@Import(SorImporterTestConfiguration.class)
	static class ContextConfiguration {
		@Bean
		public SORImporter parser() {
			return new SORImporter();
		}

		@Bean
		public Persister persister() {
			return new AuditingPersister();
		}
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	SORImporter importer;

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		jdbcTemplate.update("truncate table Praksis");
		jdbcTemplate.update("truncate table Yder");
		jdbcTemplate.update("truncate table Sygehus");
		jdbcTemplate.update("truncate table SygehusAfdeling");
		jdbcTemplate.update("truncate table Apotek");
	}

	@Test
	public void testImport() throws Exception {
		importFile("data/sor/SOR_FULL.xml");

		assertDbCount(3148, "Praksis");
		assertDbCount(5434, "Yder");
		assertDbCount(469, "Sygehus");
		assertDbCount(2890, "SygehusAfdeling");
		assertDbCount(328, "Apotek");
		assertDbCount(49, "Praksis where ValidTo < now()");
		assertDbCount(451, "Yder where ValidTo < now()");
		assertDbCount(20, "Sygehus where ValidTo < now()");
		assertDbCount(255, "SygehusAfdeling where ValidTo < now()");
		assertDbCount(2, "Apotek where ValidTo < now()");
		assertDbCount(3148 - 49, "Praksis where ValidTo > now()");
		assertDbCount(5434 - 451, "Yder where ValidTo > now()");
		assertDbCount(469 - 20, "Sygehus where ValidTo > now()");
		assertDbCount(2890 - 255, "SygehusAfdeling where ValidTo > now()");
		assertDbCount(328 - 2, "Apotek where ValidTo > now()");
	}

	private void assertDbCount(int expected, String table) {
		assertEquals(expected, jdbcTemplate.queryForInt("Select count(*) FROM "+ table));
	}

	private void importFile(String filePath) throws Exception {
		URL resource = getClass().getClassLoader().getResource(filePath);
		File datasetDir = tmpDir.newFolder();
		FileUtils.copyURLToFile(resource, new File(datasetDir, lastPathSegment(filePath)));

		importer.process(datasetDir);
	}

	private String lastPathSegment(String filePath) {
		String[] segments = filePath.split("/");

		return segments[segments.length - 1];
	}
}
