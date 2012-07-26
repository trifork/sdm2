package dk.nsi.sdm2.sample.parser;

import dk.nsi.sdm2.core.domain.AbstractRecord;

public class SampleRecord extends AbstractRecord {
    private String name;
    private String sample;

    public static SampleRecord createFrom(final Sample sample) {
        return new SampleRecord() {{
            setId(new Integer(sample.getId()).longValue());
            setName(sample.getName());
            setSample(sample.getSample());
        }};
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }
}
