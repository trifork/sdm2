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


package dk.nsi.sdm4.takst;

import dk.nsi.sdm4.core.domain.CompleteDataset;
import dk.nsi.sdm4.core.domain.Dataset;
import dk.nsi.sdm4.core.domain.TemporalEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A version of the Takst
 */
@Entity(name = "TakstVersion")
public class Takst extends TakstEntity {
	private final Map<Class<?>, CompleteDataset<? extends TemporalEntity>> datasets = new HashMap<Class<?>, CompleteDataset<? extends TemporalEntity>>();

	// The week-number for which LMS guarantees some sort of stability/validity
	// for a subset of this takst. (The stable subset excludes pricing and
	// substitions and possibly more).
	private int validityYear, validityWeekNumber;

	private Date validFrom, validTo;

	public Takst(Date validFrom, Date validTo) {
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	public <T extends TakstEntity> void addDataset(TakstDataset<T> dataset, Class<T> type) {
		datasets.put(type, dataset);
	}

	/**
	 * @param clazz the Class that the returned entities should have
	 * @return All entities of the given type in this takst.
	 */
	@SuppressWarnings("unchecked")
	public <T extends TakstEntity> TakstDataset<T> getDatasetOfType(Class<T> clazz) {
		return (TakstDataset<T>) datasets.get(clazz);
	}

	public Collection<CompleteDataset<? extends TemporalEntity>> getDatasets() {
		return datasets.values();
	}

	public <T extends TakstEntity> T getEntity(Object entityId, Class<T> type) {
		Dataset<T> dataset = getDatasetOfType(type);
		return dataset == null ? null : dataset.getEntityById(entityId);
	}

	@Column(name = "TakstUge")
	public String getStableWeek() {
		return "" + validityYear + validityWeekNumber;
	}

	@Id
	@Override
	public Date getValidFrom() {
		return validFrom;
	}

	@Override
	public Date getValidTo() {
		return validTo;
	}

	public void setValidityWeekNumber(int validityWeekNumber) {
		this.validityWeekNumber = validityWeekNumber;
	}

	public void setValidityYear(int validityYear) {
		this.validityYear = validityYear;
	}
}
