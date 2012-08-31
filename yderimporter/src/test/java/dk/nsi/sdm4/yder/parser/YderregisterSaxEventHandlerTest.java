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
package dk.nsi.sdm4.yder.parser;

import static dk.nsi.sdm4.yder.recordspecs.YderregisterRecordSpecs.PERSON_RECORD_TYPE;
import static dk.nsi.sdm4.yder.recordspecs.YderregisterRecordSpecs.YDER_RECORD_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.recordpersister.Record;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordBuilder;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.yder.YderTestConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class YderregisterSaxEventHandlerTest
{
    public static final String RECIPIENT_ID = "F053";
    private final String INTERFACE_ID = "S1040025";
    private final String ROOT_TAG = "etds1040025XML";
    private final String END_TAG = "Slut";
    private final String START_TAG = "Start";
    private final String YDER_TAG = "Yder";
    private final String PERSON_TAG = "Person";

    @Configuration
    @PropertySource("classpath:test.properties")
    @Import(YderTestConfiguration.class)
    static class ContextConfiguration {

        @Bean
        public RecordPersister persister() {
            return new RecordPersister(Instant.now());
        }

    }

    @Autowired
    RecordPersister persister;
    
    private YderregisterSaxEventHandler eventHandler;

    @Before
    public void setUp() throws Exception
    {
        persister = mock(RecordPersister.class);
        eventHandler = new YderregisterSaxEventHandler(persister);
    }

    @Test(expected = ParserException.class)
    public void testThatIfTheNumberOfRecordsFromTheEndRecordDifferThenThrowAnException() throws SAXException
    {
        eventHandler.startElement(null, null, ROOT_TAG, null);

        writeStartElement(INTERFACE_ID, RECIPIENT_ID, "00001");

        writeRecordElement(createYderRecord("0433514B17DFC5BF"));

        writeEndElement("2");

        eventHandler.endElement(null, null, ROOT_TAG);
    }

    @Test
    public void testThatTheExpectedRecordsArePassedToThePersister() throws SAXException, SQLException
    {
        eventHandler.startElement(null, null, ROOT_TAG, null);

        writeStartElement(INTERFACE_ID, RECIPIENT_ID, "00001");

        Record yderRecord = createYderRecord("0433514B17DFC5BF");
        Record person1 = createPersonRecord("C31C77B63CDDC5BF");
        Record person2 = createPersonRecord("030182BC3CDDC5BF");

        writeRecordElement(yderRecord, person1, person2);

        writeEndElement("3");

        eventHandler.endElement(null, null, ROOT_TAG);

        verify(persister).persist(yderRecord, YDER_RECORD_TYPE);
        verify(persister).persist(person1, PERSON_RECORD_TYPE);
        verify(persister).persist(person2, PERSON_RECORD_TYPE);
    }

    @Test(expected = ParserException.class)
    public void testThrowsExceptionIfTheWrongReceiverId() throws SAXException
    {
        eventHandler.startElement(null, null, ROOT_TAG, null);

        writeStartElement(INTERFACE_ID, "B012", "00001");
    }

    @Test(expected = ParserException.class)
    public void testThrowsExceptionIfTheWrongInterfaceId() throws SAXException
    {
        eventHandler.startElement(null, null, ROOT_TAG, null);

        writeStartElement("S2140021", RECIPIENT_ID, "00001");
    }
    
    @Test
    public void testVersionNumberIsExtracted() throws SAXException
    {
        eventHandler.startElement(null, null, ROOT_TAG, null);

        writeStartElement(INTERFACE_ID, RECIPIENT_ID, "00032");
        
        writeEndElement("0");

        eventHandler.endElement(null, null, ROOT_TAG);
        
        assertEquals(eventHandler.GetVersionFromFileSet(), "00032");
    }

    //
    // Helpers
    //

    public void writeStartElement(String interfaceId, String recipientId, String version) throws SAXException
    {
        eventHandler.startElement(null, null, START_TAG, createAttributes("SnitfladeId", interfaceId, "Modt", recipientId, "OpgDato", version));
        eventHandler.endElement(null, null, START_TAG);
    }

    public void writeEndElement(String recordCount) throws SAXException
    {
        eventHandler.startElement(null, null, END_TAG, createAttributes("AntPost", recordCount));
        eventHandler.endElement(null, null, END_TAG);
    }

    public Record createYderRecord(String histId)
    {
        return new RecordBuilder(YDER_RECORD_TYPE).field("HistIdYder", histId).addDummyFieldsAndBuild();
    }

    public Record createPersonRecord(String histId)
    {
        return new RecordBuilder(PERSON_RECORD_TYPE).field("HistIdPerson", histId).addDummyFieldsAndBuild();
    }

    public void writeRecordElement(Record yderRecord, Record ... personRecords) throws SAXException
    {
        eventHandler.startElement(null, null, YDER_TAG, createAttributes(yderRecord));
        
        for(Record personRecord: personRecords)
        {
            eventHandler.startElement(null, null, PERSON_TAG, createAttributes(personRecord));
            eventHandler.endElement(null, null, PERSON_TAG);
        }
        
        eventHandler.endElement(null, null, YDER_TAG);
    }

    public Attributes createAttributes(Record record)
    {
        Map<String, String> map = new HashMap<String, String>();
        for(Map.Entry<String, Object> entry : record.fields())
        {
            map.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return createAttributes(map);
    }


    public Attributes createAttributes(String... namesAndValues)
    {
        return createAttributes(createMap(namesAndValues));
    }

    public Map<String, String> createMap(String... keysAndValues)
    {
        assertTrue(keysAndValues.length % 2 == 0);

        Map<String, String> map = Maps.newHashMap();
        for(int i = 0; i < keysAndValues.length; i += 2)
        {
            map.put(keysAndValues[i], keysAndValues[i+1]);
        }

        return map;
    }

    public Attributes createAttributes(final Map<String, String> values)
    {
        Attributes attributes = mock(Attributes.class);

        when(attributes.getValue(any(String.class))).thenAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return values.get(invocation.getArguments()[0]);
            }
        });

        return attributes;
    }
}
