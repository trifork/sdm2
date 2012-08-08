package dk.nsi.sdm4.cpr.parser;

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.parser.SimpleParser;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Collection;

@Repository
public class CprParser implements Parser {
	@Override
	public void process(File dataSet) throws ParserException {
		throw new UnsupportedOperationException("process");
	}
}
