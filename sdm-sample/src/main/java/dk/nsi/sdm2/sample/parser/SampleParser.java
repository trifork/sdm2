package dk.nsi.sdm2.sample.parser;

import dk.nsi.sdm2.core.parser.SimpleParser;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class SampleParser extends SimpleParser<SampleRecord, Samples, Sample> {
    @Override
    protected Collection<Sample> getContainedEntitiesFrom(Samples type) {
        return type.getSampleList();
    }

    @Override
    public SampleRecord transform(Sample sample) {
        return SampleRecord.createFrom(sample);
    }
}
