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

import dk.nsi.sdm4.takst.FixedLengthParserConfiguration;

public class ATCKoderOgTekstFactory implements FixedLengthParserConfiguration<ATCKoderOgTekst> {
	@Override
	public String getFilename() {
		return "lms12.txt";
	}

	@Override
	public int getLength(int fieldNo) {
		switch (fieldNo) {
			case 0:
				return 8;
			case 1:
				return 72;
			default:
				return -1;
		}
	}

	@Override
	public int getNumberOfFields() {
		return 2;
	}

	@Override
	public int getOffset(int fieldNo) {
		switch (fieldNo) {
			case 0:
				return 0;
			case 1:
				return 8;
			default:
				return -1;
		}
	}

	@Override
	public void setFieldValue(ATCKoderOgTekst obj, int fieldNo, String value) {
		switch (fieldNo) {
			case 0:
				obj.setATC(value);
				break;
			case 1:
				obj.setTekst(value);
				break;
			default:
				break;
		}
	}
}
