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
public class Udleveringsbestemmelser extends TakstEntity {

	private String kode; // Ref. t. LMS02, felt 10
	private String udleveringsgruppe; // Recept: A eller B, håndkøb: H
	private String kortTekst;
	private String tekst;

	@Override
	public String getKey() {
		return kode;
	}

	@Id
	@Column
	public String getKode() {
		return kode;
	}

	@Column
	public String getKortTekst() {
		return kortTekst;
	}

	@Column
	public String getTekst() {
		return tekst;
	}

	@Column
	public String getUdleveringsgruppe() {
		return udleveringsgruppe;
	}

	public void setKode(String kode) {
		this.kode = kode;
	}

	public void setKortTekst(String kortTekst) {
		this.kortTekst = kortTekst;
	}

	public void setTekst(String tekst) {
		this.tekst = tekst;
	}

	public void setUdleveringsgruppe(String udleveringsgruppe) {
		this.udleveringsgruppe = udleveringsgruppe;
	}
}
