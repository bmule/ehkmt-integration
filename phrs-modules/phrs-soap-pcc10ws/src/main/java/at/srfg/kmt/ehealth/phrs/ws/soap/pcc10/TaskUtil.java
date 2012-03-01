/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;

import java.util.ArrayList;
import java.util.List;
import org.hl7.v3.II;
import org.hl7.v3.REPCMT004000UV01CareProvisionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bmulrenin
 */
public class TaskUtil {
        private static final Logger LOGGER =
            LoggerFactory.getLogger(TaskUtil.class);
    /**
     * Modifies this element and Adds patient ID to PCC-10 Message
     * It clears other identifiers
     *  final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();  
     * https://svn.connectopensource.org/svn/CONNECT_public/branches/3.1/hotfix1/Product/Production/Adapters/Framework/AdapterCommonDataLayerEJB/src/main/java/gov/hhs/fha/nhinc/adapter/commondatalayer/mappers/StaticTestResultsQuery.java
     * REPCMT004000UV01CareProvisionEvent careProvisionEvent
     * @param domainRoot  default is "1"
     * @param extentionPatientId "patientID"
     * boolean clear other entries
     * @return 
     */
    public static void createPatientIdNode(REPCMT004000UV01CareProvisionEvent careProvisionEvent,String domainRoot, String extentionPatientId,boolean clear){
        II personIdNode=new II();
        try {

            List<II> ids=careProvisionEvent.getRecordTarget().getValue().getPatient().getValue().getPatientPerson().getValue().getId();
            if(ids!=null){
                //there is already in the template an existing demo id
                ids.clear();
            }           
            
            ids.add(personIdNode);
            
            personIdNode.setExtension(extentionPatientId);
            personIdNode.setRoot(domainRoot);//or "1" Constants.ICARDEA_DOMAIN_PIX_OID

            
        } catch (Exception e) {
            LOGGER.error(" createPatientIdNode ",e);
            
        }
    
    }
    public static String getdefaultRoot(){
     return "1";
    }
}
