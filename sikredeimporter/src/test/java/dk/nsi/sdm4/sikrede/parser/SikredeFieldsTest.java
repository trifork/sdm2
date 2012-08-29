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

import static dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification.field;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Iterables;

import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification.FieldSpecification;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification.RecordFieldType;
import dk.nsi.sdm4.sikrede.recordspecs.SikredeRecordSpecs;

public class SikredeFieldsTest {
    @Test
    public void testCorrectNumberOfFields() {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();
        assertEquals(Iterables.size(fieldSpecs), 48);
    }

    @Test
    public void testCorrectNumberOfAlphanumericalFields() {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();

        int alphanumericalFields = 0;

        for (FieldSpecification spec : fieldSpecs) {
            if (spec.type == RecordFieldType.ALPHANUMERICAL) {
                alphanumericalFields++;
            }
        }

        assertEquals(47, alphanumericalFields);
    }

    @Test
    public void testCorrectNumberOfNumericalFields() {
        Iterable<FieldSpecification> fieldSpecs = SikredeRecordSpecs.ENTRY_RECORD_SPEC.getFieldSpecs();

        int numericalFields = 0;
        for (FieldSpecification spec : fieldSpecs) {
            if (spec.type == RecordFieldType.NUMERICAL) {
                numericalFields++;
            }
        }

        assertEquals(1, numericalFields);
    }

    @Test
    public void testCorrectAcceptedTotalLineLength() {
        RecordSpecification exampleRecordSpecification = RecordSpecification.createSpecification("SikredeGenerated",
                "Foo", field("Foo", 10).numerical(), field("Bar", 32));
        assertEquals(42, exampleRecordSpecification.acceptedTotalLineLength());
    }

    @Test
    public void testCorrectAcceptedTotalLineLengthForSingleton() {
        assertEquals(634, SikredeRecordSpecs.ENTRY_RECORD_SPEC.acceptedTotalLineLength());
    }

    @Test
    public void testConformsToSchemaSpecification() {
        RecordSpecification exampleRecordSpecification = RecordSpecification.createSpecification("SikredeGenerated",
                "Foo", field("Foo", 10).numerical(), field("Bar", 32));

        Record correctValues = RecordGenerator.createRecord("Foo", 42, "Bar", "12345678901234567890123456789012");
        Record correctValuesWhereBarIsShorter = RecordGenerator.createRecord("Foo", 42, "Bar",
                "123456789012345678901234567890");
        Record missingFoo = RecordGenerator.createRecord("Bar", "12345678901234567890123456789012");
        Record fooIsNotNumerical = RecordGenerator
                .createRecord("Foo", "Baz", "Bar", "12345678901234567890123456789012");
        Record barIsTooLong = RecordGenerator
                .createRecord("Foo", 42, "Bar", "1234567890123456789012345678901234567890");

        assertTrue(exampleRecordSpecification.conformsToSpecifications(correctValues));
        assertTrue(exampleRecordSpecification.conformsToSpecifications(correctValuesWhereBarIsShorter));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(missingFoo));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(fooIsNotNumerical));
        assertFalse(exampleRecordSpecification.conformsToSpecifications(barIsTooLong));
    }
}
