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
package dk.nsi.sdm4.bemyndigelse.parser;

import dk.nsi.sdm4.bemyndigelse.config.BemyndigelseApplicationConfig;
import dk.nsi.sdm4.bemyndigelse.model.Bemyndigelser;
import dk.nsi.sdm4.bemyndigelse.recordspecs.BemyndigelseRecordSpecs;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {BemyndigelseApplicationConfig.class, TestDbConfiguration.class})
public class BemyndigelseParserTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BemyndigelseParser parser;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parseXML() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/20120329_102310000_v1.bemyndigelse.xml"));
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Bemyndigelser.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Bemyndigelser bemyndigelser = (Bemyndigelser) jaxbUnmarshaller.unmarshal(file);

            assertEquals("v00001", bemyndigelser.getVersion());
            assertEquals("075052201", bemyndigelser.getTimestamp());

        } catch (JAXBException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testParseFile() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/20120329_102310000_v1.bemyndigelse.xml"));

        parser.process(file);
        
        assertEquals("2 bemyndigelser expected",2, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));
    }

    @Test
    public void testParseFiles() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/"));

        parser.process(file);

        assertEquals("3 bemyndigelser expected",3, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));
    }

}
