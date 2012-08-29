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


package dk.nsi.sdm4.takst.model;

import dk.nsi.sdm4.core.domain.TemporalEntity;
import dk.nsi.sdm4.takst.Takst;
import dk.nsi.sdm4.takst.TakstEntity;

import javax.persistence.Column;


public class NumeriskMedEnhed extends TakstEntity implements TemporalEntity {
	private String klartekst;
	private double numerisk;
	private Object enhed;

	public NumeriskMedEnhed(Takst takst, String klartekst, double numerisk, Object enhed) {
		this.takst = takst;
		this.klartekst = klartekst;
		this.numerisk = numerisk;
		this.enhed = enhed;
	}

	@Column(name = "StyrkeEnhed")
	public String getEnhed() {
		if (enhed instanceof DivEnheder) return ((DivEnheder) enhed).getTekst();
		return null;
	}

	/**
	 * Only used when enhed is a String
	 */
	@Column(name = "StyrkeEnhed")
	public String getEnhedString() {
		if (enhed instanceof String) return (String) enhed;
		return null;
	}

	public String getEntityTypeDisplayName() {
		// Should probably never be used as objects of this class are always
		// nested.

		return getClass().getSimpleName();
	}

	@Override
	public String getKey() {
		return null;
	}

	@Column(name = "StyrkeTekst")
	public String getKlartekst() {
		return klartekst;
	}

	@Column(name = "StyrkeNumerisk")
	public double getNumerisk() {
		return numerisk;
	}
}
