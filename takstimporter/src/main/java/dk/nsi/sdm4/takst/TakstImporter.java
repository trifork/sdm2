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

import com.google.common.collect.Maps;
import dk.nsi.sdm4.core.domain.CompleteDataset;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Map;

/**
 * Parser for the DKMA register. Also known as 'Taksten'.
 * <p/>
 * DKMA is an acronym for 'Danish Medicines Agency'.
 */
public class TakstImporter implements Parser {
	private static final Logger log = Logger.getLogger(TakstImporter.class);

	@Autowired
	private SLALogger slaLogger;

	@Autowired
	private Persister persister;

	private boolean validateInputStructure(File[] input) {
		final String[] requiredFileNames = new String[]{"system.txt", "lms01.txt", "lms02.txt", "lms03.txt", "lms04.txt", "lms05.txt", "lms07.txt", "lms09.txt", "lms10.txt", "lms11.txt", "lms12.txt", "lms13.txt", "lms14.txt", "lms15.txt", "lms16.txt", "lms17.txt", "lms18.txt", "lms19.txt", "lms20.txt", "lms23.txt", "lms24.txt", "lms25.txt", "lms26.txt", "lms27.txt", "lms28.txt"};

		Map<String, File> fileMap = Maps.newHashMap();

		for (File f : input) {
			fileMap.put(f.getName(), f);
		}

		for (String reqFile : requiredFileNames) {
			if (!fileMap.containsKey(reqFile)) {
				log.error("Input structure for takstimporter is not valid, file " + reqFile + " is not present");
				return false;
			}
		}

		return true;
	}

	public void process(File datadir) throws ParserException {
		SLALogItem slaLogItem = slaLogger.createLogItem("TakstImporter", "All");

		File[] input = datadir.listFiles();
		if (!validateInputStructure(input)) {
			throw new ParserException("input files in dir " + datadir + " are not valid");
		}

		try {
			Takst takst = new TakstParser().parseFiles(input);
			persister.persistCompleteDataset(takst.getDatasets().toArray(new CompleteDataset[]{}));
			slaLogItem.setCallResultOk();
			slaLogItem.store();
		} catch (Exception e) {
			slaLogItem.setCallResultError("TakstImporter failed - Cause: " + e.getMessage());
			slaLogItem.store();

			throw new ParserException(e);
		}
	}

	@Override
	public String getHome() {
		return "takstimporter";
	}
}
