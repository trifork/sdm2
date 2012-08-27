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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import dk.nsi.sdm4.bemyndigelse.model.Bemyndigelse;
import dk.nsi.sdm4.bemyndigelse.model.Bemyndigelser;
import dk.nsi.sdm4.bemyndigelse.recordspecs.BemyndigelseRecordSpecs;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordBuilder;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;

public class BemyndigelseParser implements Parser {

    private final RecordSpecification recordSpecification;
    private static Logger logger = Logger.getLogger(BemyndigelseParser.class); 

    @Autowired
    private SLALogger slaLogger;
    
    @Autowired
    private RecordPersister persister;
    
    
    public BemyndigelseParser() {
        this.recordSpecification = BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC;
    }
    
    @Override
    public void process(File dataSet) throws ParserException {

        SLALogItem slaLogItem = slaLogger.createLogItem("BemyndigelseParser", "dataSet");
        try {
            logger.debug("Starting Bemyndigelse parser");
            File files = checkRequiredFiles(dataSet);
            
            List<Bemyndigelser> bemyndigelsesList = unmarshallFile(files);
            for (Bemyndigelser bemyndigelser : bemyndigelsesList) {
                for(Bemyndigelse bemyndigelse: bemyndigelser.getBemyndigelseList()) {
                    Record record = buildRecord(bemyndigelse);
                    persister.persist(record, recordSpecification);
                }
            }
            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("BemyndigelseParser Failed - Cause: " + e.getMessage());
            slaLogItem.store();
            throw new ParserException(e);
        } finally {
            logger.debug("Ending Bemyndigelse parser");
        }
        
    }
    
    private Record buildRecord(Bemyndigelse bemyndigelse) {
        RecordBuilder builder = new RecordBuilder(recordSpecification);

        builder.field("kode", bemyndigelse.getKode());
        builder.field("bemyndigende_cpr", bemyndigelse.getBemyndigendeCPR());
        builder.field("bemyndigede_cpr", bemyndigelse.getBemyndigedeCPR());
        builder.field("bemyndigede_cvr", bemyndigelse.getBemyndigedeCVR());
        builder.field("system", bemyndigelse.getSystem());
        builder.field("arbejdsfunktion", bemyndigelse.getArbejdsfunktion());
        builder.field("rettighed", bemyndigelse.getRettighed());
        builder.field("status", bemyndigelse.getStatus());
        builder.field("godkendelses_dato", bemyndigelse.getGodkendelsesDato());
        builder.field("oprettelses_dato", bemyndigelse.getOprettelsesDato());
        builder.field("modificeret_dato", bemyndigelse.getModificeretDato());
        builder.field("gyldig_fra_dato", bemyndigelse.getGyldigFraDato());
        builder.field("gyldig_til_dato", bemyndigelse.getGyldigTilDato());
        
        return builder.build();
    }

    private List<Bemyndigelser> unmarshallFile(File dataSet) {
        List<Bemyndigelser> bemyndigelsesList = new ArrayList<Bemyndigelser>();
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Bemyndigelser.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            File[] input = null;
            if(dataSet.isDirectory()) {
                 input = dataSet.listFiles();
            } else {
                input = new File[] {dataSet};
            }

            for (int i = 0; i < input.length; i++) {
                Bemyndigelser bemyndigelser = (Bemyndigelser) jaxbUnmarshaller.unmarshal(input[i]);
                bemyndigelsesList.add(bemyndigelser);
            }
        } catch (JAXBException e) {
            logger.error("", e);
        }
        return bemyndigelsesList;
    }

    private File checkRequiredFiles(File dataSet) {
        
       Preconditions.checkNotNull(dataSet);
       
       File[] input = null;
       if(dataSet.isDirectory()) {
            input = dataSet.listFiles();
       } else {
           input = new File[] {dataSet};
       }

       for (int i = 0; i < input.length; i++) {
           String fileName = input[i].getName();
           MDC.put("filename", fileName);
       }
       
       return dataSet;
    }

    @Override
    public String getHome() {
        return "bemyndigelseimporter";
    }

}
