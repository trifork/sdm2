package dk.nsi.sdm4.sample.dao;

import dk.nsi.sdm4.sample.parser.SampleRecord;

import java.util.List;

public class SampleDaoJdbcImpl implements SampleDao {
	@Override
	public void createSamples(List<SampleRecord> sample) {
		throw new UnsupportedOperationException("createSample");
	}
}
