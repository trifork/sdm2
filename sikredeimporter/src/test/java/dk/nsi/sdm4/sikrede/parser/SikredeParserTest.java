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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.joda.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sikrede.SikredeTestConfiguration;
import dk.nsi.sdm4.sikrede.brs.BrsUpdater;
import dk.nsi.sdm4.sikrede.recordspecs.SikredeRecordSpecs;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class SikredeParserTest {
    
    @Configuration
    @PropertySource({"classpath:test.properties", "default-config.properties"})
    @Import(SikredeTestConfiguration.class)
    static class ContextConfiguration {
        @Bean
        public SikredeParser parser() {
            return new SikredeParser();
        }

        @Bean
        public BrsUpdater brsUpdater() {
            return new BrsUpdater();
        }

        @Bean
        public RecordPersister persister() {
            return new RecordPersister(Instant.now());
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SikredeParser parser;
    

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testEmptyFile() throws Exception {
        RecordSpecification recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;
        File input = setupExampleFile(recordSpecification);

        parser.process(input);
        
        assertEquals(0, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + recordSpecification.getTable()));
    }
/*
    @Test
    public void testAbleToInsertFourRecords() throws Exception {
        Connection connection = null;

        try {
            RecordSpecification recordSpecification = RecordSpecification.createSpecification("TestTable", "Foo",
                    field("PostType", 2).numerical().doNotPersist(), field("CPRnr", 10), field("Foo", 10));

            SingleLineRecordParser entryParser = new SingleLineRecordParser(recordSpecification);
            BrsUpdater mockedBrsUpdater = Mockito.mock(BrsUpdater.class);
            SikredeParser sikredeParser = new SikredeParser(entryParser, recordSpecification, mockedBrsUpdater);

            connection = setupSikredeGeneratedDatabaseAndConnection(recordSpecification);

            File inbox = setupExampleFile(recordSpecification, RecordGenerator.createRecord("PostType", 10, "Foo",
                    "1234567890", "CPRnr", "1234567890"), RecordGenerator.createRecord("PostType", 10, "Foo",
                    "ABCDEFGHIJ", "CPRnr", "1234567890"), RecordGenerator.createRecord("PostType", 10, "Foo", "Bar",
                    "CPRnr", "1234567890"), RecordGenerator.createRecord("PostType", 10, "Foo", "BarBaz", "CPRnr",
                    "1234567890"));

            sikredeParser.process(inbox, new RecordPersister(connection, Instant.now()));

            connection.commit();

            assertNumberOfSikredeGeneratedRecordsInDatabaseIs(connection, 4, recordSpecification);

            // FIXME: Write verify test
        } finally {
            if (connection != null) {
                connection.rollback();
            }
        }
    }

    @Test(expected = ParserException.class)
    public void testIllegalStartRecord() throws Exception {
        Connection connection = null;

        try {
            RecordSpecification recordSpecification = RecordSpecification.createSpecification("TestTable", "Foo",
                    field("PostType", 2).numerical().doNotPersist(), field("CPRnr", 10), field("Foo", 10));

            SingleLineRecordParser entryParser = new SingleLineRecordParser(recordSpecification);
            BrsUpdater mockedBrsUpdater = Mockito.mock(BrsUpdater.class);
            SikredeParser sikredeParser = new SikredeParser(entryParser, recordSpecification, mockedBrsUpdater);

            connection = setupSikredeGeneratedDatabaseAndConnection(recordSpecification);

            File inbox = setupExampleFileWithIllegalReceiverId(recordSpecification, RecordGenerator.createRecord(
                    "PostType", 10, "Foo", "1234567890", "CPRnr", "1234567890"), RecordGenerator.createRecord(
                    "PostType", 10, "Foo", "ABCDEFGHIJ", "CPRnr", "1234567890"), RecordGenerator.createRecord(
                    "PostType", 10, "Foo", "Bar", "CPRnr", "1234567890"), RecordGenerator.createRecord("PostType", 10,
                    "Foo", "BarBaz", "CPRnr", "1234567890"));

            sikredeParser.process(inbox, new RecordPersister(connection, Instant.now()));
        } finally {
            if (connection != null) {
                connection.rollback();
            }
        }
    }
    private Connection setupSikredeGeneratedDatabaseAndConnection(RecordSpecification recordSpecification)
            throws SQLException {
        Connection connection = new ConnectionManager().getConnection();

        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS " + recordSpecification.getTable());
        setupStatements.executeUpdate(RecordMySQLTableGenerator.createSqlSchema(recordSpecification));

        return connection;
    }
*/

    private File setupExampleFile(RecordSpecification recordSpecification, Record... records) throws IOException {
        RecordGenerator startGenerator = new RecordGenerator(SikredeRecordSpecs.START_RECORD_SPEC);
        RecordGenerator endGenerator = new RecordGenerator(SikredeRecordSpecs.END_RECORD_SPEC);

        StringBuilder builder = new StringBuilder();

        builder.append(startGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0, "Modt", "F053",
                "SnitfladeId", "S1061023"));
        builder.append('\n');

        RecordGenerator entryGenerator = new RecordGenerator(recordSpecification);

        for (Record record : records) {
            builder.append(entryGenerator.stringFromIncompleteRecord(record));
            builder.append('\n');
        }

        builder.append(endGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99, "AntPost", records.length));
        builder.append('\n');

        File inbox = temporaryFolder.newFolder("foo");
        File file = temporaryFolder.newFile("foo/foo.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParser.FILE_ENCODING);
        outputStreamWriter.write(builder.toString());
        outputStreamWriter.flush();
        fileOutputStream.close();

        return inbox;
    }

    private File setupExampleFileWithIllegalReceiverId(RecordSpecification recordSpecification, Record... records)
            throws IOException {
        RecordGenerator startGenerator = new RecordGenerator(SikredeRecordSpecs.START_RECORD_SPEC);
        RecordGenerator endGenerator = new RecordGenerator(SikredeRecordSpecs.END_RECORD_SPEC);

        StringBuilder builder = new StringBuilder();

        builder.append(startGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0, "Modt", "F042",
                "SnitfladeId", "S1061023"));
        builder.append('\n');

        RecordGenerator entryGenerator = new RecordGenerator(recordSpecification);

        for (Record record : records) {
            builder.append(entryGenerator.stringFromIncompleteRecord(record));
            builder.append('\n');
        }

        builder.append(endGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99, "AntPost", records.length));
        builder.append('\n');

        File inbox = temporaryFolder.newFolder("foo");
        File file = temporaryFolder.newFile("foo/foo.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParser.FILE_ENCODING);
        outputStreamWriter.write(builder.toString());
        outputStreamWriter.flush();
        fileOutputStream.close();

        return inbox;
    }
}
