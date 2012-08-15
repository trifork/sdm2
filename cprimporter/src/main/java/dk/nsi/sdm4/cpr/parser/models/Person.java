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


package dk.nsi.sdm4.cpr.parser.models;

import org.joda.time.DateTime;

/**
 * This class is called Personoplysninger in SDM3
 */
public class Person extends CPREntity
{
	String cpr;
	String gaeldendeCpr;
	String status;
	DateTime statusDato;
	String statusMakering;
	String koen;
	DateTime foedselsdato;
	boolean foedselsdatoMarkering;
	DateTime startDato;
	String startDatoMarkering;
	DateTime slutDato;
	String slutDatoMarkering;
	String stilling;

	@Override
	public String getIdentifier() {
		return getCpr();
	}

	public String getCpr()
	{
		return cpr;
	}

	public void setCpr(String cpr)
	{
		this.cpr = cpr;
	}

	public String getGaeldendeCpr()
	{
		return gaeldendeCpr;
	}

	public void setGaeldendeCpr(String gaeldendeCpr)
	{
		this.gaeldendeCpr = gaeldendeCpr;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public DateTime getStatusDato()
	{
		return statusDato;
	}

	public void setStatusDato(DateTime statusDato)
	{
		this.statusDato = statusDato;
	}

	public String getStatusMakering()
	{
		return statusMakering;
	}

	public void setStatusMakering(String statusMakering)
	{
		this.statusMakering = statusMakering;
	}

	public String getKoen()
	{
		return koen;
	}

	public void setKoen(String koen)
	{
		this.koen = koen;
	}

	public DateTime getFoedselsdato()
	{
		return foedselsdato;
	}

	public void setFoedselsdato(DateTime foedselsdato)
	{
		this.foedselsdato = foedselsdato;
	}

	public boolean getFoedselsdatoMarkering()
	{
		return foedselsdatoMarkering;
	}

	public void setFoedselsdatoMarkering(boolean isUncertain)
	{
		this.foedselsdatoMarkering = isUncertain;
	}

	public DateTime getStartDato()
	{
		return startDato;
	}

	public void setStartDato(DateTime startDato)
	{
		this.startDato = startDato;
	}

	public String getStartDatoMarkering()
	{
		return startDatoMarkering;
	}

	public void setStartDatoMarkering(String startDatoMarkering)
	{
		this.startDatoMarkering = startDatoMarkering;
	}

	public DateTime getSlutDato()
	{
		return slutDato;
	}

	public void setSlutdato(DateTime slutDato)
	{
		this.slutDato = slutDato;
	}

	public String getSlutDatoMarkering()
	{
		return slutDatoMarkering;
	}

	public void setSlutDatoMarkering(String slutDatoMarkering)
	{
		this.slutDatoMarkering = slutDatoMarkering;
	}

	public String getStilling()
	{
		return stilling;
	}

	public void setStilling(String stilling)
	{
		this.stilling = stilling;
	}
}
