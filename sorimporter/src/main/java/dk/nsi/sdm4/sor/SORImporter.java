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

import com.google.common.base.Preconditions;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;


/**
 * Parser for the SOR register.
 * <p/>
 * SOR is an acronym for 'Sundhedsvæsenets Organisationsregister'.
 */
public class SORImporter implements Parser {
	private static final Logger logger = Logger.getLogger(SORImporter.class);

	@Autowired
	private SLALogger slaLogger;

	@Autowired
	Persister persister;

	@SuppressWarnings("unchecked")
	@Override
	public void process(File datadirectory) {
		Preconditions.checkNotNull(datadirectory);

		SLALogItem slaLogItem = slaLogger.createLogItem("SORImporter", "All files");
		try {
			for (File file : datadirectory.listFiles()) {
				MDC.put("filename", file.getName());

				SORDataSets dataSets = parse(file);
				persister.persistCompleteDataset(dataSets.getPraksisDS());
				persister.persistCompleteDataset(dataSets.getYderDS());
				persister.persistCompleteDataset(dataSets.getSygehusDS());
				persister.persistCompleteDataset(dataSets.getSygehusAfdelingDS());
				persister.persistCompleteDataset(dataSets.getApotekDS());

				MDC.remove("filename");
			}
			slaLogItem.setCallResultOk();
			slaLogItem.store();
		} catch (Exception e) {
			slaLogItem.setCallResultError("SORImporter failed - Cause: " + e.getMessage());
			slaLogItem.store();

			throw new ParserException(e);
		}
	}

	public static SORDataSets parse(File file) throws SAXException, ParserConfigurationException, IOException {
		SORDataSets dataSets = new SORDataSets();
		SOREventHandler handler = new SOREventHandler(dataSets);
		SAXParserFactory factory = SAXParserFactory.newInstance();

		SAXParser parser = factory.newSAXParser();

		if (file.getName().toLowerCase().endsWith("xml")) {
			parser.parse(file, handler);
		} else {
			logger.warn("Can only parse files with extension 'xml'! The file is ignored. file=" + file.getAbsolutePath());
		}

		return dataSets;
	}

	@Override
	public String getHome() {
		return "sorimporter";
	}
}
