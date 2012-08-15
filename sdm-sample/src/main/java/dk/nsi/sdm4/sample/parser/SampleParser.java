package dk.nsi.sdm4.sample.parser;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.sample.dao.SampleDao;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SampleParser implements Parser {
    
    @Inject
    Unmarshaller unmarshaller;

	@Inject
	SampleDao dao;

    @Override
    public void process(File dataSet) throws ParserException {
        Samples samples;
        try {
            samples = (Samples) unmarshaller.unmarshal(new StreamSource(dataSet));
        } catch (Exception e) {
            throw new ParserException("Could not unmarshall", e);
        }

        List<SampleRecord> records = new ArrayList<SampleRecord>();
        try {
            for (Sample sample : samples.getSampleList()) {
                records.add(SampleRecord.createFrom(sample));
            }
        } catch (Exception e) {
            throw new ParserException("Error creating persister records", e);
        }
        
        dao.createSamples(records);
    }
}
