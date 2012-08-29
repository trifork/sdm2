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


package dk.nsi.sdm4.sks;

import dk.nsi.sdm4.core.domain.AbstractStamdataEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "Organisation")
public class Institution extends AbstractStamdataEntity
{
	private Date validFrom;
	private Date validTo;
	
	private String navn;
	private String nummer;

	private final InstitutionType type;

	public enum InstitutionType
	{
		HOSPITAL_DEPARTMENT, HOSPITAL
	}

	public Institution(InstitutionType organisationstype)
	{
		this.type = organisationstype;
	}

	public Date getValidTo()
	{
		return validTo;
	}

	public void setValidTo(Date validTo)
	{
		this.validTo = validTo;
	}

	@Column
	public String getNavn()
	{
		return navn;
	}

	public void setNavn(String navn)
	{
		this.navn = navn;
	}

	@Id
	@Column
	public String getNummer()
	{
		return nummer;
	}

	public void setNummer(String nummer)
	{
		this.nummer = nummer;
	}

	@Column
	public String getOrganisationstype()
	{
		if (type == InstitutionType.HOSPITAL_DEPARTMENT)
		{
			return "Afdeling";
		}
		else if (type == InstitutionType.HOSPITAL)
		{
			return "Sygehus";
		}

		return null;
	}

	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}

	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}
}
