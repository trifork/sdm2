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


package dk.nsi.sdm4.dosering.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collections;

@Entity
public class DosageStructure extends DosageRecord
{
	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	private long releaseNumber;

	// Unik kode for doseringstrukturen. Obligatorisk. Heltal, 11 cifre.
	private long code;

	// Typen af dosering, enten "M+M+A+N", "PN", "N daglig",
	// "Fritekst" eller "Kompleks". Obligatorisk. Streng, 100 tegn.
	private String type;

	// For simple typer (dvs. alt andet end "Kompleks")
	// indeholder feltet doseringen på simpel form. Optionelt. Streng, 100 tegn.
	private String simpleString;

	// For simple typer en eventuel supplerende tekst.
	// Optionelt. Streng, 200 tegn.
	private String supplementaryText;

	// FMKs strukturerede dosering i XML format. Bemærk at enkelte
	// værdier vil være escaped. Obligatorisk. Streng, 10000 tegn.
	private String xml;

	// Såfremt det er muligt at lave en kort
	// doseringstekst på baggrund af xml og lægemidlets doseringsenhed vil
	// dette felt indeholde denne. Optionelt. Streng, 70 tegn.
	private String shortTranslation;

	// En lang doseringstekst baggrund af xml og
	// lægemidlets doseringsenhed. Obligatorisk. Strengm 10000 tegn.
	// TODO: While this is marked as (Mandatory) in practice it is
	// sometimes null. This will change in future. (Tom K)
	private String longTranslation;

	@Column
	public long getReleaseNumber()
	{
		return releaseNumber;
	}

	@Id
	@Column
	public long getCode()
	{
		return code;
	}

	@Column
	public String getType()
	{
		return type;
	}

	@Column
	public String getSimpleString()
	{
		return simpleString;
	}

	@Column
	public String getSupplementaryText()
	{
		return supplementaryText;
	}

	@Column
	public String getXml()
	{
		return xml;
	}

	@Column
	public String getShortTranslation()
	{
		return shortTranslation;
	}

	@Column
	public String getLongTranslation()
	{
		return longTranslation;
	}

	public String toString() {
		return new ReflectionToStringBuilder(this).setExcludeFieldNames(new String[] {"xml"}).toString();
	}
}
