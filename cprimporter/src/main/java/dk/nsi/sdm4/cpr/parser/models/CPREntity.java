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

import dk.nsi.sdm4.core.domain.AbstractRecord;
import dk.nsi.sdm4.cpr.parser.CPRDataset;
import org.joda.time.DateTime;

public abstract class CPREntity extends AbstractRecord
{
	CPRDataset dataset;
	String cpr;

	public String getCpr()
	{
		return cpr;
	}

	public void setCpr(String cpr)
	{
		this.cpr = cpr;
	}

	public CPRDataset getDataset()
	{
		return dataset;
	}

	public void setDataset(CPRDataset dataset)
	{
		this.dataset = dataset;
	}

	@Override
	public DateTime getValidFrom()
	{
		return dataset.getValidFrom();
	}

	// temporary method to make sure we don't lose the string-identifier from the sdm3-migrated CprEntities
	public abstract String getIdentifier();
}
