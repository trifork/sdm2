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
public class PakningskombinationerUdenPriser extends TakstEntity {
	private Long varenummerOrdineret; // Vnr. på pakningen anført på recepten
	private Long varenummerSubstitueret; // Vnr. på en pakning der evt. kan substitueres til.
	private Long varenummerAlternativt; // Vnr. for en mindre, billigere pakning
	private Long antalPakninger; // Antal af den alternative pakning
	private String informationspligtMarkering; // Markering (stjerne *) for informationspligt.

	@Column
	public Long getAntalPakninger() {
		return antalPakninger;
	}

	@Column
	public String getInformationspligtMarkering() {
		return informationspligtMarkering;
	}

	@Override
	public Long getKey() {
		return varenummerOrdineret;
	}

	@Column
	public Long getVarenummerAlternativt() {
		return varenummerAlternativt;
	}

	@Id
	@Column
	public Long getVarenummerOrdineret() {
		return varenummerOrdineret;
	}

	@Column
	public Long getVarenummerSubstitueret() {
		return varenummerSubstitueret;
	}

	public void setAntalPakninger(Long antalPakninger) {
		this.antalPakninger = antalPakninger;
	}

	public void setInformationspligtMarkering(String informationspligtMarkering) {
		this.informationspligtMarkering = informationspligtMarkering;
	}

	public void setVarenummerAlternativt(Long varenummerAlternativt) {
		this.varenummerAlternativt = varenummerAlternativt;
	}

	public void setVarenummerOrdineret(Long varenummerOrdineret) {
		this.varenummerOrdineret = varenummerOrdineret;
	}

	public void setVarenummerSubstitueret(Long varenummerSubstitueret) {
		this.varenummerSubstitueret = varenummerSubstitueret;
	}
}
