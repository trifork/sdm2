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

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import dk.nsi.sdm4.core.parser.OutOfSequenceException;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;

public class YderregisterParser implements Parser {
    
    private final static String VERSION_KEY = "Yderregister_version";
    
    @Autowired
    private SLALogger slaLogger;
    
    @Autowired
    private RecordPersister persister;

    YderregisterSaxEventHandler eventHandler;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    
    public YderregisterParser() {
    }
    
    @Override
    public void process(File dataSet) throws ParserException {
        eventHandler = new YderregisterSaxEventHandler(persister);
    
        SLALogItem slaLogItem = slaLogger.createLogItem("YderregisterParser", dataSet != null ? dataSet.getName() : "no input file");
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();

            // Make sure that all the required file are there.
            //
            if (!areRequiredFilesPresent(dataSet)) throw new ParserException("Not all required files were present.");

            // Do the actual importing (there is only one file).
            //
            File file = dataSet.listFiles()[0];

            saxParser.parse(file, eventHandler);

            String newVersion = eventHandler.GetVersionFromFileSet();

            if (newVersion == null) throw new ParserException("No version string was extracted from the file set.");

            // Ensure the import sequence.
            //
            // Currently we can ensure that we don't import an old version, by looking at the previous
            // version and ensuring that the version number is larger.
            //
            
            
            String prevVersion;
            try {
                prevVersion = jdbcTemplate.queryForObject("select value from YderregisterKeyValue where `key` = '"+VERSION_KEY+"'",String.class);
            } catch(EmptyResultDataAccessException e) {
                prevVersion = null;
            }

            if (prevVersion != null && newVersion.compareTo(prevVersion) <= 0) {
                throw new OutOfSequenceException(prevVersion, newVersion);
            }

            String sql;
            if(prevVersion == null) {
                sql = "insert into YderregisterKeyValue (`key`, value) values ('"+VERSION_KEY+"', "+newVersion+")";
            } else {
                sql = "update YderregisterKeyValue set value="+newVersion+" where `key`='"+VERSION_KEY+"'";
            }
            jdbcTemplate.update(sql);

            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (OutOfSequenceException e) {
            slaLogItem.setCallResultError("YderregisterParser failed - Cause: " + e.getMessage());
            slaLogItem.store();
            throw e;

        } catch (Exception e) {
            slaLogItem.setCallResultError("YderregisterParser failed - Cause: " + e.getMessage());
            slaLogItem.store();
            throw new ParserException(e);
        }
    }

    public boolean areRequiredFilesPresent(File input) {
        return input.listFiles().length == 1;
    }

    @Override
    public String getHome() {
        return "yderimporter";
    }

}
