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
package dk.nsi.sdm4.sikrede.parser;

import static org.junit.Assert.assertTrue;
import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification.FieldSpecification;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification.RecordFieldType;

public class RecordGenerator {
    private RecordSpecification recordSpecification;

    public RecordGenerator(RecordSpecification recordSpecification) {
        this.recordSpecification = recordSpecification;
    }

    public String stringFromRecords(Record record) {
        if (!recordSpecification.conformsToSpecifications(record)) {
            throw new IllegalArgumentException("Sikrede record does not conform to specification");
        }

        StringBuilder builder = new StringBuilder();
        for (FieldSpecification fieldSpecification : recordSpecification.getFieldSpecs()) {
            if (fieldSpecification.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL) {
                String value = (String) record.get(fieldSpecification.name);
                builder.append(prefixPadding(' ', fieldSpecification.length - value.length()));
                builder.append(value);
            } else if (fieldSpecification.type == RecordSpecification.RecordFieldType.NUMERICAL) {
                String value = Integer.toString((Integer) record.get(fieldSpecification.name));
                builder.append(prefixPadding('0', fieldSpecification.length - value.length()));
                builder.append(value);
            } else {
                throw new AssertionError("Missing implementation for type " + fieldSpecification.type);
            }
        }
        return builder.toString();
    }

    public String stringFromIncompleteRecord(Record record) {
        for (FieldSpecification fieldSpecification : recordSpecification.getFieldSpecs()) {
            if (!record.containsKey(fieldSpecification.name)) {
                if (fieldSpecification.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL) {
                    record = record.put(fieldSpecification.name, "");
                } else if (fieldSpecification.type == RecordFieldType.NUMERICAL) {
                    record = record.put(fieldSpecification.name, 0);
                } else {
                    throw new AssertionError("Missing implementation for type " + fieldSpecification.type);
                }
            }
        }

        return stringFromRecords(record);
    }

    public String stringRecordFromIncompleteSetOfFields(Object... keysAndValues) {
        return stringFromIncompleteRecord(createRecord(keysAndValues));
    }

    public static Record createRecord(Object... keysAndValues) {
        Record record = new Record();

        assertTrue(keysAndValues.length % 2 == 0);

        for (int i = 0; i < keysAndValues.length; i += 2) {
            String key = (String) keysAndValues[i];
            Object value = keysAndValues[i + 1];
            record = record.put(key, value);
        }

        return record;
    }

    private String prefixPadding(char paddingElement, int n) {
        String padding = "";
        for (int i = 0; i < n; i++) {
            padding += paddingElement;
        }
        return padding;
    }
}
