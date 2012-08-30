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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
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

    }

    @Autowired
    YderregisterParser parser;
    
    @Autowired
    RecordPersister persister;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test(expected = OutOfSequenceException.class)
    public void testImportingOldVersionWillResultInAnOutOfSequenceException() {
        String sql = "insert into YderregisterKeyValue (`key`, value) values ('Yderregister_version', '20120104')";
        jdbcTemplate.update(sql);
        
        File fileSet = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/csc"));
        parser.process(fileSet);
        
    }

    @Test(expected = OutOfSequenceException.class)
    public void testImportingCurrentVersionResultInAnOutOfSequenceException() throws Exception {
        String sql = "insert into YderregisterKeyValue (`key`, value) values ('Yderregister_version', '20120103')";
        jdbcTemplate.update(sql);
        
        File fileSet = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/csc"));
        parser.process(fileSet);
    }

    @Test
    public void testThatFileContentAreParsed() throws Exception {
        File fileSet = FileUtils.toFile(getClass().getClassLoader().getResource("data/yderregister/csc"));
        parser.process(fileSet);
        
        assertEquals(58, jdbcTemplate.queryForInt("select count(*) from Yderregister"));
        assertEquals(54, jdbcTemplate.queryForInt("select count(*) from YderregisterPerson"));
    }
}
