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
public class Firma extends TakstEntity {
	private Long firmanummer; // Ref. t. LMS01, felt 13 - 14
	private String firmamaerkeKort; // P.t. tomt
	private String firmamaerkeLangtNavn;
	private String parallelimportoerKode;

	@Column
	public String getFirmamaerkeKort() {
		return firmamaerkeKort;
	}

	@Column
	public String getFirmamaerkeLangtNavn() {
		return firmamaerkeLangtNavn;
	}

	@Id
	@Column
	public Long getFirmanummer() {
		return firmanummer;
	}

	@Override
	public Long getKey() {
		return firmanummer;
	}

	@Column
	public String getParallelimportoerKode() {
		return parallelimportoerKode;
	}

	public void setFirmamaerkeKort(String firmamaerkeKort) {
		this.firmamaerkeKort = firmamaerkeKort;
	}

	public void setFirmamaerkeLangtNavn(String firmamaerkeLangtNavn) {
		this.firmamaerkeLangtNavn = firmamaerkeLangtNavn;
	}

	public void setFirmanummer(Long firmanummer) {
		this.firmanummer = firmanummer;
	}

	public void setParallelimportoerKode(String parallelimportoerKode) {
		this.parallelimportoerKode = parallelimportoerKode;
	}
}
