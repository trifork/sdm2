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

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.toFile;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SksParserTest
{
    private static String completeTxt = "data/sks/SHAKCOMPLETE.TXT";
    private static String completeXml = "data/sks/SHAKCOMPLETE.XML";
    private static String delta = "data/sks/SHAKDELTA.TXT";

	private SKSParser importer;

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();

	@Before
	public void setup() {
		importer = new SKSParser();
	}

	@Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyInputFileSet() throws IOException {
        importer.validateInputStructure(tmpDir.newFolder());
    }
    
    @Test
    public void shouldAcceptSKSCompleteTxtFile() throws IOException {
        assertTrue(importer.validateInputStructure(datasetDirWith(completeTxt)));
    }

	@Test
    public void shouldNotAcceptSKSCompleteXmlFile() throws IOException {
        assertFalse(importer.validateInputStructure(datasetDirWith(completeXml)));
    }
    
    @Test
    public void shouldAcceptSKSDeltaFile() throws IOException {
        assertTrue(importer.validateInputStructure(datasetDirWith(delta)));
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
