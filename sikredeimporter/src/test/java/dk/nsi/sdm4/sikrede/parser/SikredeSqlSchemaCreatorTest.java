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

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import dk.nsi.sdm4.core.persistence.recordpersister.RecordMySQLTableGenerator;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sikrede.recordspecs.SikredeRecordSpecs;
import static dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification.field;

public class SikredeSqlSchemaCreatorTest {
	private static final Logger log = Logger.getLogger(SikredeSqlSchemaCreatorTest.class);
    private RecordSpecification exampleRecordSpecification;

    @Before
    public void createExampleSikredeFields() {
        exampleRecordSpecification = RecordSpecification.createSpecification("SikredeGenerated", "Foo",
                field("Foo", 10), field("Bar", 5).numerical(), field("Baz", 42));
    }

    @Test
    public void testExampleSikredeFields() {
        String expected = "CREATE TABLE SikredeGenerated (" + "PID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY,"
                + "Foo VARCHAR(10)," + "Bar BIGINT," + "Baz VARCHAR(42)," + "ValidFrom DateTime NOT NULL,"
                + "ValidTo DateTime," + "ModifiedDate DateTime NOT NULL" + ") ENGINE=InnoDB COLLATE=utf8_bin;";

        String result = RecordMySQLTableGenerator.createSqlSchema(exampleRecordSpecification);

        assertEquals(expected, result.replaceAll("\n", "").replaceAll("\t", ""));
    }

    @Test
    public void testPrintOfActualSchema() {
        log.debug(RecordMySQLTableGenerator.createSqlSchema(SikredeRecordSpecs.ENTRY_RECORD_SPEC));
    }
}
