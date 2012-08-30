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
package dk.nsi.sdm4.yder.parser;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.joda.time.Instant;
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

import dk.nsi.sdm4.core.parser.OutOfSequenceException;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.yder.YderTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class YderregisterParserTest {

    @Configuration
    @PropertySource("classpath:test.properties")
    @Import(YderTestConfiguration.class)
    static class ContextConfiguration {
        @Bean
        public YderregisterParser parser() {
            return new YderregisterParser();
        }

        @Bean
        public RecordPersister persister() {
            return new RecordPersister(Instant.now());
        }

        @Bean
        public YderregisterSaxEventHandler saxHandler() {
            return new YderregisterSaxEventHandler();
        }
    }

    @Autowired
    YderregisterParser parser;
    
    @Autowired
    RecordPersister persister;

    @Autowired
    YderregisterSaxEventHandler saxHandler;
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void dummy() {
        boolean dummy = true;
        assertTrue(dummy);
    }

//    @Test(expected = OutOfSequenceException.class)
//    public void testImportingOldVersionWillResultInAnOutOfSequenceException() {
//        String sql = "insert into YderregisterKeyValue (`key`, value) values ('Yderregister_version', '00002')";
//        jdbcTemplate.update(sql);
//        
//        File fileSet = null;
//        try {
//            fileSet = createFileSet("00001");
//        } catch (IOException e) {
//            fail(e.getMessage());
//        }
//
//        parser.process(fileSet);
//    }

    /*
     * @Test(expected = OutOfSequenceException.class) public void
     * testImportingCurrentVersionResultInAnOutOfSequenceException() throws
     * Exception { when(keyValueStore.get("version")).thenReturn("00002"); File
     * fileSet = createFileSet("00002");
     * 
     * parser.process(fileSet, persister); }
     * 
     * @Test(expected = ParserException.class) public void
     * testMissingFilesResultsInAParserException() throws Exception { File
     * fileSet = createFileSet("00001"); fileSet.listFiles()[0].delete();
     * 
     * parser.process(fileSet, persister); }
     * 
     * @Test public void testStoresTheVersionFromStartRecordCorrectly() throws
     * Exception { String version = "00031";
     * 
     * File fileSet = createFileSet(version);
     * 
     * parser.process(fileSet, persister);
     * 
     * verify(keyValueStore).put("version", version); }
     * 
     * @Test public void testThatAllFilesArePassedToTheParser() throws Exception
     * { File fileSet = createFileSet("00001");
     * 
     * parser.process(fileSet, persister);
     * 
     * for (File file : fileSet.listFiles()) { verify(saxParser).parse(file,
     * saxHandler); } }
     */

    //
    // Helpers
    //

    public File createFileSet(String filename, String version) throws IOException {
        File root = folder.newFolder("root");

        File file = new File(root, filename);
        file.createNewFile();

        return root;
    }

    public File createFileSet(String version) throws IOException {
        return createFileSet("M.S1040025.SB025.xml", version);
    }
}
