package dk.nsi.sdm2.sample.parser;

import dk.nsi.sdm2.core.parser.Parser;
import dk.nsi.sdm2.core.parser.ParserException;
import dk.nsi.sdm2.core.persist.Record;
import dk.nsi.sdm2.core.persist.RecordBuilder;
import dk.nsi.sdm2.core.persist.RecordPersister;
import dk.nsi.sdm2.core.persist.RecordSpecification;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class SampleParser implements Parser {

    RecordSpecification recordSpecification;

    public SampleParser(RecordSpecification recordSpecification) {
        this.recordSpecification = recordSpecification;
    }

    /*
     * @see dk.nsi.sdm2.core.parser.Parser#process(java.io.File, dk.nsi.sdm2.core.persist.RecordPersister)
     * 
     * This is an example of how to read from a dataset (file) and persist the
     * data to the Stamdata database.
     */
    @Override
    public void process(File dataSet, RecordPersister persister) throws ParserException {

        try {
            List<Sample> samples = unmarshallFile(dataSet);
            for (Sample sample : samples) {
                Record record = buildRecord(sample);
                persister.persist(record, recordSpecification);
            }
        } catch (SQLException e) {
            throw new ParserException(e);
        }
    }

    private Record buildRecord(Sample sample) {
        RecordBuilder builder = new RecordBuilder(recordSpecification);
        
        builder.field("id", sample.getId());
        builder.field("name", sample.getName());
        builder.field("sample", sample.getSample());
        
        return builder.build();
    }

    /*
     * An Example of how to unmarshal an XML file using JAXB 
     */
    private List<Sample> unmarshallFile(File dataSet) {
        List<Sample> sampleList = new ArrayList<Sample>();
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Sample.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            File[] input = null;
            if(dataSet.isDirectory()) {
                 input = dataSet.listFiles();
            } else {
                input = new File[] {dataSet};
            }

            for (int i = 0; i < input.length; i++) {
                Sample sample = (Sample) jaxbUnmarshaller.unmarshal(input[i]);
                sampleList.add(sample);
            }
        } catch (JAXBException e) {
            throw new ParserException(e);
        }
        return sampleList;
    }
}
