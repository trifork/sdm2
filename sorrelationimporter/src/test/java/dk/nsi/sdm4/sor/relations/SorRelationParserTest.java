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
package dk.nsi.sdm4.sor.relations;

import dk.nsi.sdm4.core.persistence.recordpersister.RecordMySQLTableGenerator;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sor.config.SorrelationApplicationConfig;
import dk.nsi.sdm4.sor.recordspecs.SorRelationsRecordSpecs;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import oio.sundhedsstyrelsen.organisation._1_0.*;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {SorrelationApplicationConfig.class, TestDbConfiguration.class})
public class SorRelationParserTest {
	private RecordSpecification recordSpecification = SorRelationsRecordSpecs.RELATIONS_RECORD_SPEC;
    private RecordSpecification shakYderSpecification = SorRelationsRecordSpecs.SHAK_YDER_RECORD_SPEC;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private SorRelationParser parser;

    @Before
    public void setUp() throws Exception {
        setupDatabaseConnection(recordSpecification, shakYderSpecification);
    }
    
    @Test
    public void parseXML() throws Exception {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/SOR_FULL.xml"));

        JAXBContext jaxbContext;
        System.setProperty("jaxb.debug", "true");

        jaxbContext = JAXBContext.newInstance(SorTreeType.class.getPackage().getName());
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        jaxbUnmarshaller.setEventHandler(new XMLValidationEventHandler());
        JAXBElement<SorTreeType> jaxbSOR = (JAXBElement<SorTreeType>) jaxbUnmarshaller.unmarshal(file);

        SorTreeType sor = jaxbSOR.getValue();

        assertNotNull(sor);
        assertNotNull(sor.getInstitutionOwnerEntity());
        assertEquals(6934, sor.getInstitutionOwnerEntity().size());
    }

    @Test
    public void testSORRelationTablesAreCleanedUpBeforeEachRun() throws Exception {

        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/ONE_SYGEHUS.xml"));
        
        parser.process(file);
        assertEquals(10, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + recordSpecification.getTable()));
        
        //run again and see data is the same
        parser.process(file);
        assertEquals(10, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + recordSpecification.getTable()));
        
    }
 
    
    @Test
    public void testParseFile() throws Exception {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/SOR_FULL.xml"));

        parser.process(file);

	    assertEquals(22, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + recordSpecification.getTable() + " WHERE sor_parent = '21000016006'"));
        assertEquals("278591000016002", jdbcTemplate.queryForObject("SELECT sor FROM " + shakYderSpecification.getTable() + " WHERE shak_yder = 'SHAK=804301'", String.class));
        assertEquals(2, jdbcTemplate.queryForInt("SELECT count(sor) FROM " + shakYderSpecification.getTable() + " WHERE shak_yder = 'Yder=045756' AND (sor ='8301000016000' OR sor='8311000016003')"));
    }

    @Test
    public void testSimpleTree() throws Exception {
        List<InstitutionOwnerEntityType> institutions = new ArrayList<InstitutionOwnerEntityType>();
        InstitutionOwnerEntityType ioe = new InstitutionOwnerEntityType();
        institutions.add(ioe);
        InstitutionOwnerType io = new InstitutionOwnerType();
        io.setSorIdentifier(1234);
        ioe.setInstitutionOwner(io);
        List<HealthInstitutionEntityType> hieList = ioe.getHealthInstitutionEntity();
        HealthInstitutionEntityType hie = new HealthInstitutionEntityType();
        hieList.add(hie);
        HealthInstitutionType hi = new HealthInstitutionType();
        hi.setSorIdentifier(2345678);
        hie.setHealthInstitution(hi);
        List<OrganizationalUnitEntityType> ouList = hie.getOrganizationalUnitEntity();
        OrganizationalUnitEntityType oue = new OrganizationalUnitEntityType();
        ouList.add(oue);
        OrganizationalUnitType ou = new OrganizationalUnitType();
        ou.setSorIdentifier(9876);
        oue.setOrganizationalUnit(ou);
        List<OrganizationalUnitEntityType> oueChildList = oue.getOrganizationalUnitEntity();
        OrganizationalUnitEntityType oueChild = new OrganizationalUnitEntityType();
        oueChildList.add(oueChild);
        OrganizationalUnitType ouChild = new OrganizationalUnitType();
        ouChild.setSorIdentifier(98765);
        oueChild.setOrganizationalUnit(ouChild);
        
        parser.processSorTree(institutions);
        
        assertEquals(4, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + recordSpecification.getTable() + " WHERE sor_parent = '1234'"));

        assertEquals(4, jdbcTemplate.queryForInt("SELECT count(sor_parent) FROM " + recordSpecification.getTable() + " WHERE " +
		        "sor_child = '98765' AND (sor_parent = '1234' or sor_parent='2345678' or sor_parent='9876' or sor_parent='98765')"));
    }

    @Test
    public void testValidToDates() throws Exception {
        parser.setSnapshotDate(new DateTime());

        DateTime now = new DateTime();
        DateTime past = now.minusWeeks(1);
        DateTime future = now.plusWeeks(1);
        
        SorStatusType validValidTo = new SorStatusType();
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(future.getMillis());
        XMLGregorianCalendar xmlFutureDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        validValidTo.setToDate(xmlFutureDate);

        SorStatusType invalidValidTo = new SorStatusType();
        GregorianCalendar c2 = new GregorianCalendar();
        c2.setTimeInMillis(past.getMillis());
        XMLGregorianCalendar xmlPastDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c2);
        invalidValidTo.setToDate(xmlPastDate);
        
        List<InstitutionOwnerEntityType> institutions = new ArrayList<InstitutionOwnerEntityType>();
        InstitutionOwnerEntityType ioe = new InstitutionOwnerEntityType();
        institutions.add(ioe);
        InstitutionOwnerType io = new InstitutionOwnerType();
        io.setSorIdentifier(1234);
        io.setSorStatus(validValidTo);
        ioe.setInstitutionOwner(io);
        List<HealthInstitutionEntityType> hieList = ioe.getHealthInstitutionEntity();
        HealthInstitutionEntityType hie = new HealthInstitutionEntityType();
        hieList.add(hie);
        HealthInstitutionType hi = new HealthInstitutionType();
        hi.setSorIdentifier(2345678);
        hi.setSorStatus(validValidTo);
        hie.setHealthInstitution(hi);
        List<OrganizationalUnitEntityType> ouList = hie.getOrganizationalUnitEntity();
        OrganizationalUnitEntityType oue = new OrganizationalUnitEntityType();
        ouList.add(oue);
        OrganizationalUnitType ou = new OrganizationalUnitType();
        ou.setSorIdentifier(9876);
        ou.setSorStatus(validValidTo);
        oue.setOrganizationalUnit(ou);
        List<OrganizationalUnitEntityType> oueChildList = oue.getOrganizationalUnitEntity();
        OrganizationalUnitEntityType oueChild = new OrganizationalUnitEntityType();
        oueChildList.add(oueChild);
        OrganizationalUnitType ouChild = new OrganizationalUnitType();
        ouChild.setSorIdentifier(98765);
        ouChild.setSorStatus(invalidValidTo);
        oueChild.setOrganizationalUnit(ouChild);
        
        parser.processSorTree(institutions);
        
        assertEquals(3, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + recordSpecification.getTable() + " WHERE sor_parent = '1234'"));
    }

    private void setupDatabaseConnection(RecordSpecification sorRelations, RecordSpecification shakYder) throws SQLException {
        jdbcTemplate.update("DROP TABLE IF EXISTS " + sorRelations.getTable());
	    jdbcTemplate.update(RecordMySQLTableGenerator.createSqlSchema(sorRelations));

	    jdbcTemplate.update("DROP TABLE IF EXISTS " + shakYder.getTable());
	    jdbcTemplate.update(RecordMySQLTableGenerator.createSqlSchema(shakYder));
    }

	/**
	 * The SOR Schemas does not parse when using a validating parser, so we'll ignore all errors while parsing
	 */
    class XMLValidationEventHandler extends DefaultValidationEventHandler {
        @Override
        public boolean handleEvent(ValidationEvent event) {
            return true;
        }

    }
}
