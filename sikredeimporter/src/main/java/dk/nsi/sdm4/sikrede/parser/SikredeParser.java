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
package dk.nsi.sdm4.sikrede.parser;

import static java.lang.String.format;

import java.io.File;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Preconditions;


import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sikrede.brs.BrsUpdater;
import dk.nsi.sdm4.sikrede.recordspecs.SikredeRecordSpecs;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;

public class SikredeParser implements Parser {
	// default settings, is overwritten by configuration
    
    @Value("${spooler.sikrede.recipientid}")
    private String acceptedRecipientId = "F053";
    @Value("${spooler.sikrede.interfaceid}")
    private String acceptedInterfaceId = "S1061023";
    
    public static final String FILE_ENCODING = "ISO-8859-1";

    private static final String RECORD_TYPE_START = "00";
    private static final String RECORD_TYPE_ENTRY = "10";
    private static final String RECORD_TYPE_END = "99";

    private final SingleLineRecordParser recordParser;
    private final RecordSpecification recordSpecification;

    @Autowired
    private SLALogger slaLogger;
    
    @Autowired
    private RecordPersister persister;

    @Autowired
    private BrsUpdater brsUpdater;

    public SikredeParser() {
        recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;
        recordParser = new SingleLineRecordParser(recordSpecification);
    }

    @Override
    public void process(File dataSet) throws ParserException {
        SLALogItem slaLogItem = slaLogger.createLogItem("SikredeParser", "dataSet");
        try {
            File[] input = dataSet.listFiles();
            Preconditions.checkArgument(input.length == 1, "Only one file is expected at this point.");
            File file = input[0];

            MDC.put("filename", file.getName());

            // FIXME: Check that files are imported in the right order.
            // We can not do this yet as we do not know what the files are named.

            LineIterator lines = null;

            try {
                lines = FileUtils.lineIterator(file, FILE_ENCODING);
                importFile(lines, persister);
            } finally {
                LineIterator.closeQuietly(lines);
            }

            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("SikredeParser Failed - Cause: " + e.getMessage());
            slaLogItem.store();

            throw new ParserException(e);
        }
    }

    private void importFile(Iterator<String> lines, RecordPersister persister) throws Exception {
        // A set containing all CPR numbers that have changed.
        //
        Record startRecord = null;
        Record endRecord = null;

        int numRecords = 0;

        SingleLineRecordParser startRecordParser = new SingleLineRecordParser(SikredeRecordSpecs.START_RECORD_SPEC);
        SingleLineRecordParser endRecordParser = new SingleLineRecordParser(SikredeRecordSpecs.END_RECORD_SPEC);

        for (String line; lines.hasNext(); ) {
            if (endRecord != null) {
                throw new ParserException("Lines found after End Record.");
            }

            line = lines.next();

            if (line.startsWith(RECORD_TYPE_START)) {
                if (startRecord != null) {
                    throw new ParserException("Several Start Records were found.");
                }

                startRecord = startRecordParser.parseLine(line);

                // FIXME: Verificer Modt og SnitfladeId jf. dokumentation.
                //
                if (!acceptedRecipientId.equals(startRecord.get("Modt"))) {
                    throw new ParserException(format("The receiver id '%s' did not match the expected '%s'.", startRecord.get("Modt"), acceptedRecipientId));
                }

                if (!acceptedInterfaceId.equals(startRecord.get("SnitfladeId"))) {
                    throw new ParserException(format("The interface id did not match the expected '%s'.", startRecord.get("SnitfladeId"), acceptedInterfaceId));
                }
            } else if (line.startsWith(RECORD_TYPE_END)) {
                if (startRecord == null) throw new ParserException("Start record was not found before end record.");

                endRecord = endRecordParser.parseLine(line);
            } else if (line.startsWith(RECORD_TYPE_ENTRY)) {
                if (startRecord == null) throw new ParserException("Start record was not found before first entry.");

                Record record = recordParser.parseLine(line);

                brsUpdater.updateRecord(record);

                persister.persist(record, recordSpecification);

                numRecords++;
            } else {
                throw new ParserException("Unknown record type. line_content='" + line + "'");
            }
        }

        if (!endRecord.get("AntPost").equals(numRecords)) {
            throw new ParserException("The number of records that were parsed did not match the total from the end record.");
        }
    }

    @Override
    public String getHome() {
        return "sikredeimporter";
    }

}
