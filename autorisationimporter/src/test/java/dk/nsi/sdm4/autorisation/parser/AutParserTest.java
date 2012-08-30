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

package dk.nsi.sdm4.autorisation.parser;

import dk.nsi.sdm4.autorisation.config.AutorisationApplicationConfig;
import dk.nsi.sdm4.autorisation.model.Autorisation;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {AutorisationApplicationConfig.class, TestDbConfiguration.class})
public class AutParserTest {
    @Autowired
    AutorisationParser parser;
    
    public static File valid;
    public static File invalid;

    @Before
    public void setUp() {
        valid = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/valid/20090915AutDK.csv"));
        invalid = FileUtils.toFile(getClass().getClassLoader().getResource("data/aut/invalid/20090915AutDK.csv"));
    }

    @Test
    public void testParse() throws IOException {
        Autorisationsregisterudtraek auts = parser.parse(valid, new DateTime());
        assertEquals(4, auts.getEntities().size());
        Autorisation a = auts.getEntityById("0013H");
        assertNotNull(a);
        assertEquals("0101280063", a.getCpr());
        assertEquals("Tage Søgaard", a.getFornavn());
    }
    
    @Test(expected = Exception.class)
    public void testInvalid() throws IOException {
        new AutorisationParser().parse(invalid, new DateTime());
    }
}
