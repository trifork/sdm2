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


package dk.nsi.sdm4.dosering.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.nsi.sdm4.core.domain.CompleteDataset;
import dk.nsi.sdm4.core.domain.TemporalEntity;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.Persister;
import dk.nsi.sdm4.core.util.Dates;
import dk.nsi.sdm4.dosering.model.DosageRecord;
import dk.nsi.sdm4.dosering.model.DosageStructure;
import dk.nsi.sdm4.dosering.model.DosageUnit;
import dk.nsi.sdm4.dosering.model.DosageVersion;
import dk.nsi.sdm4.dosering.model.Drug;
import dk.nsi.sdm4.dosering.model.DrugDosageStructureRelation;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;

/**
 * Importer for drug dosage suggestions data.
 */
public class DoseringParser implements Parser {
    private static final Logger logger = Logger.getLogger(DoseringParser.class);
    
    @Autowired
    SLALogger slaLogger;
    
    @Autowired
    Persister persister;
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @SuppressWarnings("unchecked")
    @Override
    public void process(File dataSet) throws ParserException {

        SLALogItem slaLogItem = slaLogger.createLogItem("DosageSuggestionImporter", "All Files");
        try {
            // First check to see if all expected files are present
            validateInputStructure(dataSet);
            
            // METADATA FILE
            //
            // The file contains information about the validity period
            // of the data.

            DosageVersion version = parseVersionFile(getFile(dataSet, "DosageVersion.json"));
            version.setVersion(version.getReleaseDate());
            CompleteDataset<DosageVersion> versionDataset = new CompleteDataset<DosageVersion>(DosageVersion.class, version.getValidFrom(), Dates.THE_END_OF_TIME);
            versionDataset.add(version);

            // CHECK PREVIOUS VERSION
            //
            // Check that the version in imported in
            // sequence and that we haven't missed one.
            int maxVersion = jdbcTemplate.queryForInt("SELECT MAX(releaseNumber) FROM DosageVersion");

            if (maxVersion == 0) {
                logger.warn("No previous version of Dosage Suggestion registry found, assuming initial import.");
            } else if (version.getReleaseNumber() != maxVersion + 1) {
                throw new Exception("The Dosage Suggestion files are out of sequence! Expected " + (maxVersion + 1) + ", but was " + version.getReleaseNumber() + ".");
            }


            // OTHER FILES
            //
            // There are data files and relation file.
            // Relation files act as one-to-one etc. relations.
            //
            // This data source represents the 'whole truth' for
            // the validity period. That means that complete
            // datasets will be used for persisting, and no existing
            // records will be valid in the period.
            //
            // We have to declare the <T> types explicitly since GSon
            // (Java is stupid) can't get the runtime types otherwise.

            Type type;

            type = new TypeToken<Map<String, Collection<Drug>>>() {
            }.getType();
            CompleteDataset<?> drugs = parseDataFile(getFile(dataSet, "Drugs.json"), "drugs", version, Drug.class, type);
            setValidityPeriod(drugs, version);

            type = new TypeToken<Map<String, Collection<DosageUnit>>>() {
            }.getType();
            CompleteDataset<?> units = parseDataFile(getFile(dataSet, "DosageUnits.json"), "dosageUnits", version, DosageUnit.class, type);
            setValidityPeriod(units, version);

            type = new TypeToken<Map<String, Collection<DosageStructure>>>() {
            }.getType();
            CompleteDataset<?> structures = parseDataFile(getFile(dataSet, "DosageStructures.json"), "dosageStructures", version, DosageStructure.class, type);
            setValidityPeriod(structures, version);

            type = new TypeToken<Map<String, Collection<DrugDosageStructureRelation>>>() {
            }.getType();
            CompleteDataset<?> relations = parseDataFile(getFile(dataSet, "DrugsDosageStructures.json"), "drugsDosageStructures", version, DrugDosageStructureRelation.class, type);
            setValidityPeriod(relations, version);

            // PERSIST THE DATA
            persister.persistCompleteDataset(versionDataset, drugs, structures, units, relations);

            logger.info("Dosage Suggestion Registry v" + version.getReleaseNumber() + " was successfully imported.");
            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("DosageSuggestionImporter failed - Cause: " + e.getMessage());
            slaLogItem.store();
            throw new ParserException(e);
        }
    }

    /**
     * Parses Version.json files.
     */
    private DosageVersion parseVersionFile(File file) throws FileNotFoundException {
        Reader reader = new InputStreamReader(new FileInputStream(file));

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        Type type = new TypeToken<Map<String, DosageVersion>>() {
        }.getType();

        Map<String, DosageVersion> versions = gson.fromJson(reader, type);

        return versions.get("version");
    }

    /**
     * Parses other data files.
     */
    private <T extends TemporalEntity> CompleteDataset<T> parseDataFile(File file, String root, DosageVersion version, Class<T> type, Type collectionType) throws FileNotFoundException {
        Reader reader = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));

        Map<String, List<T>> parsedData = new Gson().fromJson(reader, collectionType);

        CompleteDataset<T> dataset = new CompleteDataset<T>(type, version.getValidFrom(), version.getValidTo());

        for (T structure : parsedData.get(root)) {
            dataset.add(structure);
        }

        return dataset;
    }

    /**
     * HACK: These dates should be taken from the complete data set. There is no
     * reason why we set dates on each record.
     */
    @SuppressWarnings("unchecked")
    private void setValidityPeriod(CompleteDataset<?> dataset, DosageVersion version) {
        CompleteDataset<? extends DosageRecord> records = (CompleteDataset<? extends DosageRecord>) dataset;

        for (DosageRecord record : records.getEntities()) {
            record.setVersion(version.getValidFrom());
        }
    }

    public boolean validateInputStructure(File input) {
        boolean present = true;

        present &= getFile(input, "DosageStructures.json") != null;
        present &= getFile(input, "DosageUnits.json") != null;
        present &= getFile(input, "Drugs.json") != null;
        present &= getFile(input, "DrugsDosageStructures.json") != null;
        present &= getFile(input, "DosageVersion.json") != null;

        return present;
    }

    /**
     * Searches the provided file array for a specific file name.
     *
     * @param files the list of files to search.
     * @param name  the file name of the file to return.
     * @return the file with the specified name or null if no file is found.
     */
    private File getFile(File dataSet, String name) {
        
        File[] files = dataSet.listFiles();
        
        File result = null;

        for (File file : files) {

            if (file.getName().equals(name)) {
                result = file;
                break;
            }
        }

        return result;
    }

    @Override
    public String getHome() {
        return "doseringimporter";
    }

}
