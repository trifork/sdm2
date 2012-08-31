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

package dk.nsi.sdm4.dosering.parser;

import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.dosering.config.DoseringparserApplicationConfig;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.io.FileUtils.toFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {DoseringparserApplicationConfig.class, TestDbConfiguration.class})
public class DoseringParserIntegrationTest {
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Autowired
    DoseringParser parser;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    Persister persister;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private File versionFile;
    private File drugsFile;
    private File dosageStructureFile;
    private File unitsFile;
    private File relationFile;

    @Before
    public void setUp() throws SQLException {
        // The 'single' files only contain one record each.
        // This makes it easy to know that is imported and
        // it is a lot faster.
        //
        // The other files contain several records and are
        // used to count the number of records imported.

        versionFile = getFile("single/DosageVersion.json");
        drugsFile = getFile("single/Drugs.json");
        dosageStructureFile = getFile("single/DosageStructures.json");
        unitsFile = getFile("single/DosageUnits.json");
        relationFile = getFile("single/DrugsDosageStructures.json");
    }

    @Test
    public void importTheVersionFileCorrectly() throws Exception {
        runImporter();

        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from DosageVersion");
        rs.next();
        
        assertThat(rs.getLong("releaseNumber"), equalTo(125L));
        assertThat(rs.getDate("releaseDate"), equalTo(date("2011-02-15")));
        assertThat(rs.getDate("lmsDate"), equalTo(date("2011-02-02")));
        assertThat(rs.getDate("daDate"), equalTo(date("2011-01-24")));
        
        // expect only one row
        assertFalse(rs.next());
    }

    @Test
    public void importAllDosageStructures() throws Exception {
        dosageStructureFile = getFile("multiple/DosageStructures.json");

        runImporter();
        
        assertThat(jdbcTemplate.queryForInt("select count(*) from DosageStructure"), equalTo(587));
    }

    @Test
    public void importTheStructuresCorrectly() throws Exception {
        runImporter();

        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from DosageStructure");
        rs.next();

        assertThat(rs.getLong("releaseNumber"), equalTo(125L));
        assertThat(rs.getString("code"), equalTo("3"));
        assertThat(rs.getString("type"), equalTo("M+M+A+N"));
        assertThat(rs.getString("simpleString"), equalTo("0.5"));
        assertThat(rs.getString("shortTranslation"), equalTo("1/2 tablet morgen"));
        assertThat(rs.getString("xml"), equalTo("<b:DosageStructure\n   xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2009/01/01 DKMA_DosageStructure.xsd\"\n   xmlns:a=\"http://www.dkma.dk/medicinecard/xml.schema/2008/06/01\"\n   xmlns:b=\"http://www.dkma.dk/medicinecard/xml.schema/2009/01/01\"\n   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n   <b:DosageTimesStructure>\n      <a:DosageTimesIterationIntervalQuantity>1</a:DosageTimesIterationIntervalQuantity>\n      <a:DosageTimesStartDate>2000-01-01</a:DosageTimesStartDate>\n      <b:DosageQuantityUnitText>tablet</b:DosageQuantityUnitText>\n      <b:DosageDayElementStructure>\n         <a:DosageDayIdentifier>1</a:DosageDayIdentifier>\n         <b:MorningDosageTimeElementStructure>\n            <a:DosageQuantityValue>0.5</a:DosageQuantityValue>\n         </b:MorningDosageTimeElementStructure>\n      </b:DosageDayElementStructure>\n   </b:DosageTimesStructure>\n</b:DosageStructure>"));
        assertThat(rs.getString("longTranslation"), equalTo("Daglig 1/2 tablet morgen"));
        assertThat(rs.getString("supplementaryText"), nullValue());
        
        // expect only one row
        assertFalse(rs.next());
    }

    @Test
    public void Should_import_all_dosage_units() throws Exception {
        unitsFile = getFile("multiple/DosageUnits.json");

        runImporter();
        assertThat(jdbcTemplate.queryForInt("select count(*) from DosageUnit"), equalTo(21));
    }

    @Test
    public void Should_import_dosage_units_correctly() throws Exception {
        runImporter();

        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from DosageUnit");
        rs.next();

        assertThat(rs.getLong("releaseNumber"), equalTo(125L));
        assertThat(rs.getInt("code"), equalTo(8));
        assertThat(rs.getString("textSingular"), equalTo("brusetablet"));
        assertThat(rs.getString("textPlural"), equalTo("brusetabletter"));
        
        // expect only one row
        assertFalse(rs.next());

    }

    @Test
    public void Should_import_all_drugs() throws Exception {
        drugsFile = getFile("multiple/Drugs.json");

        runImporter();
        assertThat(jdbcTemplate.queryForInt("select count(*) from DosageDrug"), equalTo(3710));

    }

    // HELPER METHODS

    private Date date(String dateString) throws Exception {
        return dateFormat.parse(dateString);
    }

    public File getFile(String filename) {
        return toFile(getClass().getClassLoader().getResource("data/doseringsforslag/" + filename));
    }

    public void runImporter() throws Exception {
        File datasetDir = tmpDir.newFolder();
        FileUtils.copyFileToDirectory(versionFile, datasetDir);
        FileUtils.copyFileToDirectory(drugsFile, datasetDir);
        FileUtils.copyFileToDirectory(dosageStructureFile, datasetDir);
        FileUtils.copyFileToDirectory(unitsFile, datasetDir);
        FileUtils.copyFileToDirectory(relationFile, datasetDir);

        parser.process(datasetDir);
    }
}
