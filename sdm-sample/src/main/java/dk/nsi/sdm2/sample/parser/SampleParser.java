package dk.nsi.sdm2.sample.parser;

import dk.nsi.sdm2.core.parser.Parser;
import dk.nsi.sdm2.core.parser.ParserException;
import dk.nsi.sdm2.core.persist.RecordPersister;

import java.io.File;

//TODO: @Persister(name="sample", importFrom="sample")
public class SampleParser implements Parser {
    @Override
    public void process(File dataSet, RecordPersister persister) throws ParserException {
        //todo reads from dataSet and puts to persister
    }
}
