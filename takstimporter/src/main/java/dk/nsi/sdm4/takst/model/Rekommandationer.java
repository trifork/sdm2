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

import dk.nsi.sdm4.takst.TakstEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Rekommandationer extends TakstEntity {
	private Long rekommandationsgruppe;
	private Long drugID; // Ref. t. LMS01
	private Long varenummer; // Ref. t. LMS02

	// Værdier: Anbefales med forbehold / Anbefales ikke
	private String rekommandationsniveau;

	@Column
	public Long getDrugID() {
		return drugID;
	}

	@Override
	public Long getKey() {
		return varenummer;
	}

	@Column
	public Long getRekommandationsgruppe() {
		return rekommandationsgruppe;
	}

	@Column
	public String getRekommandationsniveau() {
		return rekommandationsniveau;
	}

	@Id
	@Column
	public Long getVarenummer() {
		return varenummer;
	}

	public void setDrugID(Long drugID) {
		this.drugID = drugID;
	}

	public void setRekommandationsgruppe(Long rekommandationsgruppe) {
		this.rekommandationsgruppe = rekommandationsgruppe;
	}

	public void setRekommandationsniveau(String rekommandationsniveau) {
		this.rekommandationsniveau = rekommandationsniveau;
	}

	public void setVarenummer(Long varenummer) {
		this.varenummer = varenummer;
	}
}
