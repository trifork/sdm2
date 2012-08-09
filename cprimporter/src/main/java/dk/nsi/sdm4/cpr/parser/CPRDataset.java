package dk.nsi.sdm4.cpr.parser;

import dk.nsi.sdm4.cpr.parser.models.CPREntity;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class CPRDataset {
	private DateTime validFrom;
	private List<CPREntity> entities = new ArrayList<CPREntity>();

	public void setValidFrom(DateTime validFrom) {
		this.validFrom = validFrom;
	}

	public DateTime getValidFrom() {
		return validFrom;
	}

	public void addEntity(CPREntity entity) {
		entities.add(entity);
	}
}
