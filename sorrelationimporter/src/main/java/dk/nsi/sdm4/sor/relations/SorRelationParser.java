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

import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordBuilder;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordSpecification;
import dk.nsi.sdm4.sor.recordspecs.SorRelationsRecordSpecs;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;
import oio.sundhedsstyrelsen.organisation._1_0.*;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class SorRelationParser implements Parser {
    private final RecordSpecification recordSpecification;
    private final RecordSpecification shakYderSpecification;
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SorRelationParser.class);
    private Map<String, String> selfRelationsMap;
    private Map<String, HashSet<String>> shakYderMap;
    private HashSet<String> parentChildIds;
    private DateTime snapshotDate;

	@Autowired
	private SLALogger slaLogger;

	@Autowired
	private RecordPersister persister;

    public SorRelationParser() {
        this.recordSpecification = SorRelationsRecordSpecs.RELATIONS_RECORD_SPEC;
        this.shakYderSpecification = SorRelationsRecordSpecs.SHAK_YDER_RECORD_SPEC;
    }
    
    @Override
    public void process(File dataSet) throws ParserException {
        SLALogItem slaLogItem = slaLogger.createLogItem("SorRelationParser", "dataSet");
        try {
            logger.debug("Starting SOR NPI relation parser");
            File files = checkRequiredFiles(dataSet);
            
            List<InstitutionOwnerEntityType> list = unmarshallFile(files);
            processSorTree(list);
            
            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("SorRelationParser Failed - Cause: " + e.getMessage());
            slaLogItem.store();
            throw new ParserException(e);
        } finally {
            logger.debug("Ending SOR NPI relation parser");
        }
        
    }

	@Override
	public String getHome() {
		return "sorrelationimporter";
	}

	void processSorTree(List<InstitutionOwnerEntityType> list) throws SQLException {
        try {
            selfRelationsMap = new HashMap<String, String>();
            shakYderMap = new HashMap<String, HashSet<String>>();
            parentChildIds = new HashSet<String>();

            for (InstitutionOwnerEntityType institutions : list) {
                persistInstitutionRelations(institutions, persister);
            }
            
            // persist institutions relations with self
            Set<String> keys = selfRelationsMap.keySet();
            for (String sorSelfRelationId : keys) {
                persistNode(sorSelfRelationId, sorSelfRelationId, persister);
            }
            
            // persist collected shak/yder numbers
            Set<String> syKeys = shakYderMap.keySet();
            for (String shakYderKey : syKeys) {
                persistShakYder(shakYderKey, shakYderMap.get(shakYderKey), persister);
            }
        } finally {
            selfRelationsMap = null;
            shakYderMap = null;
            parentChildIds = null;
        }
    }

    private void persistInstitutionRelations(InstitutionOwnerEntityType institution, RecordPersister persister) throws SQLException {
        
        InstitutionOwnerType owner = institution.getInstitutionOwner();
        String ownerId = ""+owner.getSorIdentifier();
        if(!hasValidPeriod(owner.getSorStatus())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Institution with SOR id:" +ownerId+"  is is no longer valid, toDate: "+owner.getSorStatus().getToDate());
            }
            return;
        }
        
        List<HealthInstitutionEntityType> childInstitutions = institution.getHealthInstitutionEntity();
        
        selfRelationsMap.put(ownerId, "");

        for (HealthInstitutionEntityType hiChild : childInstitutions) {
            String childId = ""+hiChild.getHealthInstitution().getSorIdentifier();
            persistNode(ownerId, childId, persister);

            List<OrganizationalUnitEntityType> organizationalUnitEntity = hiChild.getOrganizationalUnitEntity();
            for (OrganizationalUnitEntityType ouChild : organizationalUnitEntity) {
                traverseOrganizationalUnitEntity(ownerId, ouChild, persister);
            }
            traverseHealthInstitutionChild(hiChild, persister);
        }
    }

    private void traverseHealthInstitutionChild(HealthInstitutionEntityType child, RecordPersister persister) throws SQLException {
        HealthInstitutionType healthInstitution = child.getHealthInstitution();
        
        String hiChildId = ""+healthInstitution.getSorIdentifier();
        if(!hasValidPeriod(healthInstitution.getSorStatus())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Institution with SOR id:" +hiChildId+" is no longer valid, toDate: "+healthInstitution.getSorStatus().getToDate());
            }
            return;
        }
        
        selfRelationsMap.put(hiChildId, "");
        
        String shakIdentifier = healthInstitution.getShakIdentifier();
        if(shakIdentifier != null && shakIdentifier.trim().length() > 0) {
            putValueInShakYderMap("SHAK="+shakIdentifier, hiChildId);
        }

        List<OrganizationalUnitEntityType> organizationalUnitEntity = child.getOrganizationalUnitEntity();
        for (OrganizationalUnitEntityType ouChild : organizationalUnitEntity) {
            traverseOrganizationalUnitEntity(hiChildId, ouChild, persister);
        }
    }

    private void traverseOrganizationalUnitEntity(String parentId, OrganizationalUnitEntityType ouChild, RecordPersister persister) throws SQLException {
        OrganizationalUnitType ou = ouChild.getOrganizationalUnit();
        String childId = ""+ou.getSorIdentifier();

        // persist relation with owner and with self
        String parentChildId = parentId + "_" + childId;
        if(parentChildIds.contains(parentChildId)) {
            // ou is persisted already - this check is needed due to tree parsing algorithm where a parent must map directly to all childs 
            return;
        }
        parentChildIds.add(parentChildId);
        
        if(!hasValidPeriod(ou.getSorStatus())) {
            if(logger.isDebugEnabled()) {
                logger.debug("Institution with SOR id:" +ou+" is no longer valid, toDate: "+ou.getSorStatus().getToDate());
            }
            return;
        }
        
        persistNode(parentId, childId, persister);
        selfRelationsMap.put(childId, "");
        
        String shakIdentifier = ou.getShakIdentifier();
        if(shakIdentifier != null && shakIdentifier.trim().length() > 0) {
            putValueInShakYderMap("SHAK="+shakIdentifier, childId);
        }
        String yderIdentifier = ou.getProviderIdentifier();
        if(yderIdentifier != null && yderIdentifier.trim().length() > 0) {
            putValueInShakYderMap("Yder="+yderIdentifier, childId);
        }
        
        if(ouChild.getOrganizationalUnitEntity() != null) {
            // first persist any existing childs with parent relation
            List<OrganizationalUnitEntityType> subChilds = ouChild.getOrganizationalUnitEntity();
            for (OrganizationalUnitEntityType subChild : subChilds) {
                traverseOrganizationalUnitEntity(parentId, subChild, persister);
            }
            // then traverse recursively through childs;
            List<OrganizationalUnitEntityType> subChilds2 = ouChild.getOrganizationalUnitEntity();
            for (OrganizationalUnitEntityType subChild : subChilds2) {
                String ouChildId = ""+ouChild.getOrganizationalUnit().getSorIdentifier();
                traverseOrganizationalUnitEntity(ouChildId, subChild, persister);
            }
        }
    }

    private void persistNode(String parent, String child, RecordPersister persister) throws SQLException {
        RecordBuilder OwnerRecord = new RecordBuilder(recordSpecification);
        OwnerRecord.field("sor_parent", parent);
        OwnerRecord.field("sor_child", child);
        persister.persist(OwnerRecord.build(), recordSpecification);
    }
    
    private void persistShakYder(String shakYder, HashSet<String> sorSet, RecordPersister persister) throws SQLException {
        
        if(shakYder == null) {
            throw new IllegalArgumentException("SHAK or YDER must be set");
        }
        
        for (String sor : sorSet) {
            RecordBuilder OwnerRecord = new RecordBuilder(shakYderSpecification);
            OwnerRecord.field("shak_yder", shakYder);
            OwnerRecord.field("sor", sor);
            persister.persist(OwnerRecord.build(), shakYderSpecification);
        }
    }
    
    private void putValueInShakYderMap(String shakYder, String sor) {

        HashSet<String> sorSet = shakYderMap.get(shakYder);
        if(sorSet == null) {
            sorSet = new HashSet<String>();
        }
        sorSet.add(sor);
        shakYderMap.put(shakYder, sorSet);
    }
    
    
    private boolean hasValidPeriod(SorStatusType sorStatus) {
        if(sorStatus != null) {
            XMLGregorianCalendar toDate = sorStatus.getToDate();
            if(toDate != null) {
                DateTime toDateTime = new DateTime(toDate.toGregorianCalendar().getTime());
                if(snapshotDate.isAfter(toDateTime)) {
                    return false;
                }
            }
        }
        // if toDate of sorStatus cannot be found assume entity is valid.
        return true;
    }

    private List<InstitutionOwnerEntityType> unmarshallFile(File dataSet) {
        List<InstitutionOwnerEntityType> list = new ArrayList<InstitutionOwnerEntityType>();
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SorTreeType.class.getPackage().getName());
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            File[] input = null;
            if(dataSet.isDirectory()) {
                 input = dataSet.listFiles();
            } else {
                input = new File[] {dataSet};
            }

            for (int i = 0; i < input.length; i++) {
                JAXBElement<SorTreeType> jaxbSOR = (JAXBElement<SorTreeType>) jaxbUnmarshaller.unmarshal(input[i]);
                SorTreeType sor = jaxbSOR.getValue();
                setSnapshotDate(new DateTime(sor.getSnapshotDate().toGregorianCalendar().getTime()));
                List<InstitutionOwnerEntityType> institutionOwnerEntity = sor.getInstitutionOwnerEntity();
                list.addAll(institutionOwnerEntity);
            }
        } catch (JAXBException e) {
            logger.error("", e);
        }
        return list;
    }

    private File checkRequiredFiles(File dataSet) {
       checkNotNull(dataSet);
       
       File[] input;
       if(dataSet.isDirectory()) {
            input = dataSet.listFiles();
       } else {
           input = new File[] {dataSet};
       }

       for (int i = 0; i < input.length; i++) {
           String fileName = input[i].getName();
           MDC.put("filename", fileName);
       }
       
       return dataSet;
    }

    void setSnapshotDate(DateTime snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

}
