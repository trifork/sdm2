package dk.nsi.sdm4.sample.dao;

import dk.nsi.sdm4.sample.parser.SampleRecord;

import java.util.List;

public interface SampleDao {
	public void createSamples(List<SampleRecord> samples);
}
