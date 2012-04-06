/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;

import at.srfg.kmt.ehealth.phrs.Constants;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.hl7.v3.COCTMT050000UVPatient;

import org.hl7.v3.II;
import org.hl7.v3.REPCMT004000UV01CareProvisionEvent;
import org.hl7.v3.REPCMT004000UV01RecordTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bmulrenin
 */
public class TaskUtil {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(TaskUtil.class);

    /**
     * Modifies this element and Adds patient ID to PCC-10 Message
     * It clears other identifiers
     * final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
     * subject2.getCareProvisionEvent();
     * https://svn.connectopensource.org/svn/CONNECT_public/branches/3.1/hotfix1/Product/Production/Adapters/Framework/AdapterCommonDataLayerEJB/src/main/java/gov/hhs/fha/nhinc/adapter/commondatalayer/mappers/StaticTestResultsQuery.java
     * REPCMT004000UV01CareProvisionEvent careProvisionEvent
     *
     * @param domainRoot         default is "1"
     * @param extentionPatientId "patientID"
     *                           boolean clear other entries
     * @return
     */
    public static void createPatientIdNode(REPCMT004000UV01CareProvisionEvent careProvisionEvent, String domainRoot, String extentionPatientId, boolean clear) {
        final JAXBElement<REPCMT004000UV01RecordTarget> recordTarget_JAXB = 
                careProvisionEvent.getRecordTarget();
        final REPCMT004000UV01RecordTarget recordTarget = recordTarget_JAXB.getValue();
        
        final JAXBElement<COCTMT050000UVPatient> patient_JAXB = recordTarget.getPatient();
        final COCTMT050000UVPatient patient = patient_JAXB.getValue();
        
        final II newId = new II();
        newId.setRoot(domainRoot);
        newId.setExtension(extentionPatientId);
        patient.getId().add(newId);
    }

    public static void createPatientIdNode(REPCMT004000UV01CareProvisionEvent careProvisionEvent, String extentionPatientId) {
        final JAXBElement<REPCMT004000UV01RecordTarget> recordTarget_JAXB = 
                careProvisionEvent.getRecordTarget();
        final REPCMT004000UV01RecordTarget recordTarget = recordTarget_JAXB.getValue();
        
        final JAXBElement<COCTMT050000UVPatient> patient_JAXB = recordTarget.getPatient();
        final COCTMT050000UVPatient patient = patient_JAXB.getValue();
        
        final II newId = new II();
        newId.setRoot(Constants.ICARDEA_DOMAIN_PIX_OID);
        newId.setExtension(extentionPatientId);
        final List<II> ids = patient.getId();
        ids.clear();
        ids.add(newId);
        LOGGER.debug("createPatientIdNode check patientID careProvisionEvent {}",careProvisionEvent);
        LOGGER.debug("createPatientIdNode check patientID careProvisionEvent ids {}",ids);
    }

    /**
     * @param toParse
     * @return
     */
    public static II getII_IdentifierNode(REPCMT004000UV01CareProvisionEvent toParse) {
        II ii_node = null;
        try {
            List<II> ids = toParse.getRecordTarget().getValue().getPatient().getValue().getPatientPerson().getValue().getId();

            if (ids.size() > 0) ii_node = ids.get(0);
        } catch (Exception e) {
            LOGGER.error("getPatientID error from REPCMT004000UV01CareProvisionEvent, Message is missing required structures", e);
        }
        return ii_node;

    }

    public static String getPatientId(REPCMT004000UV01CareProvisionEvent toParse) {
        return getPatientId(getII_IdentifierNode(toParse));
    }

    /**
     * @param node
     * @return returns null if null or blank
     */
    public static String getPatientId(II node) {
        if (node == null) return null;

        String patientId = node.getExtension();
        if (patientId != null) {
            patientId = patientId.trim();
            if (patientId.isEmpty()) patientId = null;
        }


        return patientId;
    }
    public static boolean hasPatientId(REPCMT004000UV01CareProvisionEvent toParse){
           return getPatientId(toParse) != null;
    }
    public static String getdefaultRoot() {
        return "1";
    }
}
