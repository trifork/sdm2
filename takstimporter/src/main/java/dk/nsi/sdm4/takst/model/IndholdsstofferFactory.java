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
import org.apache.commons.lang.math.NumberUtils;


public class IndholdsstofferFactory implements FixedLengthParserConfiguration<Indholdsstoffer> {
	@Override
	public String getFilename() {
		return "lms30.txt";
	}

	@Override
	public int getLength(int fieldNo) {
		switch (fieldNo) {
			case 0:
				return 11;
			case 1:
				return 6;
			case 2:
				return 100;
			case 3:
				return 100;
			case 4:
				return 150;
			default:
				return -1;
		}
	}

	@Override
	public int getNumberOfFields() {
		return 5;
	}

	@Override
	public int getOffset(int fieldNo) {
		switch (fieldNo) {
			case 0:
				return 0;
			case 1:
				return 11;
			case 2:
				return 17;
			case 3:
				return 117;
			case 4:
				return 217;
			default:
				return -1;
		}
	}

	@Override
	public void setFieldValue(Indholdsstoffer obj, int fieldNo, String value) {
		switch (fieldNo) {
			case 0:
				obj.setDrugID(NumberUtils.createLong(value));
				break;
			case 1:
				obj.setVarenummer(NumberUtils.createLong(value));
				break;
			case 2:
				obj.setStofklasse(value);
				break;
			case 3:
				obj.setSubstansgruppe(value);
				break;
			case 4:
				obj.setSubstans(value);
				break;
		}
	}
}
