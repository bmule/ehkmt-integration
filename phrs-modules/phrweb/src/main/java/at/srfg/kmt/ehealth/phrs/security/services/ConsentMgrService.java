package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.ArrayOf_xsd_anyType;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecision;

import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecisionResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResources;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResourcesResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjects;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjectsResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.joda.time.DateTime;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.xacml.ctx.*;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionQueryType;
import org.opensaml.xacml.profile.saml.impl.XACMLAuthzDecisionQueryTypeImpl;
import org.opensaml.xacml.profile.saml.impl.XACMLAuthzDecisionQueryTypeMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import tr.com.srdc.icardea.atnalog.client.Audit;
import tr.com.srdc.icardea.consenteditor.saml.SAML;


@SuppressWarnings("serial")
public class ConsentMgrService implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConsentMgrService.class);

    private ConsentManagerImplServiceStub stub = null;
    private String ISSUERNAME = "PHR";
    //not used, except to check for the https. The stub contains the 
    //
    private String endpointConsentMgr;
    private int sslConfigSetting = 2;
    private AuditAtnaService auditService;

    public ConsentMgrService() {

        endpointConsentMgr = getServiceEndpoint();
        stub = getConsentServiceStub();
        //get this services
        auditService = new AuditAtnaService();

    }

    public AuditAtnaService getAuditAtnaService() {
        return auditService;
    }

    public static void timeoutSetup() throws Exception {
        try {
            Options options = new Options();
            int timeOutInMilliSeconds = 10000;
            //options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(timeOutInMilliSeconds));
            //options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(timeOutInMilliSeconds));

            // or
            options.setTimeOutInMilliSeconds(timeOutInMilliSeconds);
            options.setProperty(HTTPConstants.CHUNKED, "false");

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
        }
    }

    public void sslSetup() throws Exception {

        timeoutSetup();
        SSLLocalClient.sslSetup(sslConfigSetting);

    }

    public static boolean isMedicalRole(String subjectCode) {

        return ConfigurationService.getInstance().isMedicalCareRole(subjectCode);
        //UserSessionService.getSessionAttributeRole());
    }

    public static boolean isConsentMgrRole(String subjectCode) {
        if (subjectCode != null
                && subjectCode.startsWith(PhrsConstants.AUTHORIZE_ROLE_CONSENT_MGR_PREFIX)) {
            return true;
        }
        return false;
    }

    /**
     * Local setting, used now for creating alternate test or demo view
     * @param subjectCode
     * @return
     */
    public static boolean isAccessibleByThisRole(String subjectCode) {
        return ConfigurationService.getInstance().isHealthInfoAccessibleByThisRole(subjectCode);
    }

    public static boolean isConsentMgrAction(String actionCode) {

        return ConfigurationService.getInstance().isConsentAction(actionCode);
    }
      public String getProtocolId(String ownerUri){
          CommonDao dao = PhrsStoreClient.getInstance().getCommonDao();
          return dao.getProtocolId(ownerUri);
      }
              
              
    /**
     * @param targetUser   (user identifier: can send two queries for phr or protocol ID)
     * @param subjectCode  (role code)
     * @param idType
     * @param resourceCode (mediation, basic health, condition, etc)
     * @param action       (READ,WRITE...)
     * @return
     */
    public boolean isPermitted(String targetUser, String subjectCode,
                               String idType, String resourceCode, String action) {
        boolean flag = false;

        try {
//This is the local setting
            if (isAccessibleByThisRole(subjectCode)) {

                flag = true;

            } else if (isConsentMgrRole(subjectCode)) {

                sslSetup();
                String userIdentifier = targetUser;
                //try this identifier, usually the phrId
                String result = callGetDecision(userIdentifier, ISSUERNAME,
                        subjectCode, resourceCode, action);
                // try on protocol ID
                flag = isPermitted(result);

                //usually the phrId is passed
                if (!idType.equals(PhrsConstants.PROTOCOL_ID_NAME)) {
                    // try on phrId
                    userIdentifier=getProtocolId(targetUser);
                    if (!flag && userIdentifier!=null && !userIdentifier.isEmpty() ) {
                        result = callGetDecision(userIdentifier, ISSUERNAME,
                                subjectCode, resourceCode, action);
                        flag = isPermitted(result);
                    } else {

                    }
                }
                if (result != null) {
                    LOGGER.debug("result " + " targetUser phrId" + targetUser
                            + "idType = " + idType + " user id="
                            + userIdentifier + " subjectCode=" + subjectCode
                            + " resourceCode=" + resourceCode + " resourceCode"
                            + "action=" + action + " decision allow?=" + flag);
                } else {
                    LOGGER.debug("result " + " targetUser phrId" + targetUser
                            + "idType = " + idType + " user id="
                            + userIdentifier + " subjectCode=" + subjectCode
                            + " resourceCode=" + resourceCode + " resourceCode"
                            + "action=" + action + " result=NULL");

                }

            }
        } catch (Exception e) {

            LOGGER.debug("error result " + " targetUser phrId" + targetUser
                    + " idType=" + idType + " subjectCode=" + subjectCode
                    + " resourceCode=" + resourceCode + " resourceCode"
                    + "action=" + action + " decision allow?=" + flag);

            e.printStackTrace();
        }
        this.sendAuditMessage(targetUser, subjectCode, idType, resourceCode, action);
        LOGGER.debug("permission result " + " targetUser phrId" + targetUser
                + " idType=" + idType + " subjectCode=" + subjectCode
                + " resourceCode=" + resourceCode + " resourceCode"
                + "action=" + action + " decision allow?=" + flag + " isAccessibleByThisRole=" + isAccessibleByThisRole(subjectCode));
        return flag;
    }

    public void sendAuditMessage(String targetUser, String subjectRoleCode,
                                 String idType, String resourceCode, String action) {
        //final String patientId, final String resource, final String requestorRole
        auditService.sendAuditMessageGrant(targetUser, ISSUERNAME, subjectRoleCode);
    }

    boolean hasUserByPhrId(String phrId) {
        boolean flag = false;
        return flag;
    }

    boolean hasUserByProtocolId(String phrId) {
        boolean flag = false;
        return flag;
    }

    /**
     * Test for "Permit" in
     * <code><xacml-context:Decision>Permit</xacml-context:Decision></code>
     *
     * @param XACMLAuthzDecisionStatement
     * @return
     */
    protected boolean isPermitted(String XACMLAuthzDecisionStatement) {
        // <xacml-context:Decision>Deny</xacml-context:Decision>

        if (XACMLAuthzDecisionStatement != null) {
            return (XACMLAuthzDecisionStatement.indexOf("Permit") != -1);
        }
        return false;
    }

    public static String getServiceEndpoint() {
        String endpoint = null;

        //ResourceBundle properties = ResourceBundle.getBundle("icardea");
        //endpoint = properties.getString("consent.ws.endpoint");
        endpoint = ConfigurationService.getInstance().getConsentServiceEndpoint();

        if (endpoint != null) endpoint = endpoint.trim();
        return endpoint;

    }


    public ConsentManagerImplServiceStub getConsentServiceStub() {
        //ConsentManagerSubInterface stub = null;
        String endPoint = null;
        if (stub != null) return stub;
        try {
            endPoint = getServiceEndpoint();
            if (endPoint == null) throw new Exception("Endpoint is null");

            //          if(endPoint.startsWith("https")){
            //              stub = new ConsentManagerImplServiceSecuredStub();
            //          } else {
            stub = new ConsentManagerImplServiceStub();

            //          }
        } catch (AxisFault e) {
            LOGGER.error("Error created stub with endpoint" + endPoint, e);
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return stub;
    }

    public String callGetDecision(String patientId, String issuerName,
                                  String subjectCode, String resourceCode, String action) {

        //ConsentManagerImplServiceStub stub;

        if (patientId != null && !patientId.isEmpty()
                && subjectCode != null && !subjectCode.isEmpty()
                && resourceCode != null && !resourceCode.isEmpty()
                && action != null && !action.isEmpty()) {

            try {
                stub = getConsentServiceStub();

                GetDecision request = new GetDecision();
                request.setPatientID(patientId);

                String requestString = generateRequestAsString(generateSAMLRequest(
                        "1", issuerName, subjectCode, resourceCode, action));

                //remove xml statement at beginning
                requestString = requestString.substring(39);
                LOGGER.debug(" requestString =", requestString.substring(39));

                request.setRequestString(requestString);
                              //stub.getDecision(request);
                GetDecisionResponse response = stub.getDecision(request);
                String resultString = response.getGetDecisionReturn();
                //System.out.println("callGetDecision result" + resultString);
                LOGGER.debug(" callGetDecision patientId=" + patientId
                        + "issuerName " + issuerName + " subjectCode" + subjectCode
                        + "resourceCode " + resourceCode + " action" + action
                        + " decision= " + resultString);
                return resultString;

//            } catch (AxisFault e) {
//                e.printStackTrace();
//                LOGGER.error(" patientId=" + patientId, e);
//
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                LOGGER.error(" patientId=" + patientId, e);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(" patientId=" + patientId, e);
            }
        } else {
            LOGGER.debug("callGetDecision received a NULL argument: patientId=" + patientId
                    + "issuerName " + issuerName + " subjectCode" + subjectCode
                    + "resourceCode " + resourceCode + " action" + action);
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?><xacml-saml:XACMLAuthzDecisionStatement xmlns:xacml-saml=\"urn:oasis:names:tc:xacml:2.0:profile:saml2.0:v2:schema:assertion\"><xacml-context:Response xmlns:xacml-context=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\"><xacml-context:Result><xacml-context:Decision>Deny</xacml-context:Decision></xacml-context:Result></xacml-context:Response></xacml-saml:XACMLAuthzDecisionStatement>";
    }

    /**
     * Roles See PhrsConstants.
     *
     * @return
     */
    public List<String> callGetSubjects() {

        List<String> list = new ArrayList<String>();
        try {

            stub = getConsentServiceStub();
            GetSubjects request = new GetSubjects();
            GetSubjectsResponse response = stub.getSubjects(request);
            if(response!=null){
                ArrayOf_xsd_anyType result = response.getGetSubjectsReturn();
                System.out.println("SUBJECTS");
                for (Object o : result.getItem()) {
                    System.out.println("Subject Code: " + o);
                    list.add(o.toString());
                }
            } else {
                LOGGER.error("GetResourcesResponse response is null");
            }


//        } catch (AxisFault e) {
//            e.printStackTrace();
//            LOGGER.error(" ", e);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//            LOGGER.error(" ", e);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(" ", e);
        }
        return list;
    }

    public List<String> callGetResources() {

        List<String> list = new ArrayList<String>();
        try {
            stub = getConsentServiceStub();
            GetResources request = new GetResources();
            GetResourcesResponse response = stub.getResources(request);
     
             
            if(response!=null){
                ArrayOf_xsd_anyType result = response.getGetResourcesReturn();
                System.out.println("RESOURCES");
    
                for (Object o : result.getItem()) {
                    System.out.println("Resource Code: " + o);
                    list.add(o.toString());
                }   
            } else {
                LOGGER.error("GetResourcesResponse response is null");
            }
//        } catch (AxisFault e) {
//            e.printStackTrace();
//             LOGGER.error("",e);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//             LOGGER.error("",e);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
        }
        return list;
    }

    /**
     * @param patientId
     * @param idType
     * @param requesterRole
     * @param resource
     * @return true if the auditing option is configured
     */
    public boolean auditGrantRequest(String patientId, String idType,
                                     String requesterRole, String resource) {
        boolean result = new Boolean(ResourceBundle.getBundle("icardea").getString("atna.log")).booleanValue();
      
        auditService.sendAuditMessageGrant(patientId, resource, requesterRole);

        return result;
    }

    public String generateRequestAsString(XACMLAuthzDecisionQueryType request) {
        XACMLAuthzDecisionQueryTypeMarshaller marshaller = new XACMLAuthzDecisionQueryTypeMarshaller();
        Element element = null;
        String result = "";
        try {
            element = marshaller.marshall(request);

            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");

            LSSerializer writer = impl.createLSSerializer();
            result = writer.writeToString(element);
        } catch (MarshallingException e) {
            e.printStackTrace();
            LOGGER.error(" ", e);
        } catch (ClassCastException e) {
            e.printStackTrace();
            LOGGER.error(" ", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LOGGER.error(" ", e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            LOGGER.error(" ", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LOGGER.error(" ", e);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(" ", e);
        }
        return result;

    }

    public XACMLAuthzDecisionQueryType generateSAMLRequest(String requestId,
                                                           String issuer, String subjectId, String resourceId,
                                                           String actionString) {
        SAML saml = new SAML();
        XACMLAuthzDecisionQueryType query = saml.create(
                XACMLAuthzDecisionQueryTypeImpl.class,
                XACMLAuthzDecisionQueryTypeImpl.DEFAULT_ELEMENT_NAME_XACML20);
        RequestType request = saml.create(RequestType.class,
                RequestType.DEFAULT_ELEMENT_NAME);

        DateTime now = new DateTime();

        if (!subjectId.equals("")) {
            AttributeType subjectAttr = saml.create(AttributeType.class,
                    AttributeType.DEFAULT_ELEMENT_NAME);
            subjectAttr.setAttributeID("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
            subjectAttr.setDataType("http://www.w3.org/2001/XMLSchema#string");

            AttributeValueType subjectAttrValue = saml.create(
                    AttributeValueType.class,
                    AttributeValueType.DEFAULT_ELEMENT_NAME);
            subjectAttrValue.setValue(subjectId);
            subjectAttr.getAttributeValues().add(subjectAttrValue);

            SubjectType subject = saml.create(SubjectType.class,
                    SubjectType.DEFAULT_ELEMENT_NAME);
            subject.getAttributes().add(subjectAttr);
            request.getSubjects().add(subject);
        }

        if (!resourceId.equals("")) {
            AttributeType resourceAttr = saml.create(AttributeType.class,
                    AttributeType.DEFAULT_ELEMENT_NAME);
            resourceAttr.setAttributeID("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
            resourceAttr.setDataType("http://www.w3.org/2001/XMLSchema#string");

            AttributeValueType resourceAttrValue = saml.create(
                    AttributeValueType.class,
                    AttributeValueType.DEFAULT_ELEMENT_NAME);
            resourceAttrValue.setValue(resourceId);
            resourceAttr.getAttributeValues().add(resourceAttrValue);

            ResourceType resource = saml.create(ResourceType.class,
                    ResourceType.DEFAULT_ELEMENT_NAME);
            resource.getAttributes().add(resourceAttr);
            request.getResources().add(resource);
        }

        if (!actionString.equals("")) {
            ActionType action = saml.create(ActionType.class,
                    ActionType.DEFAULT_ELEMENT_NAME);

            AttributeType actionAttr = saml.create(AttributeType.class,
                    AttributeType.DEFAULT_ELEMENT_NAME);
            actionAttr.setAttributeID("urn:oasis:names:tc:xacml:1.0:action:action-id");
            actionAttr.setDataType("http://www.w3.org/2001/XMLSchema#string");

            AttributeValueType actionAttrValue = saml.create(
                    AttributeValueType.class,
                    AttributeValueType.DEFAULT_ELEMENT_NAME);
            actionAttrValue.setValue(actionString);
            actionAttr.getAttributeValues().add(actionAttrValue);

            action.getAttributes().add(actionAttr);
            request.setAction(action);
        }
        Issuer issuer_ = saml.create(Issuer.class, Issuer.DEFAULT_ELEMENT_NAME);
        issuer_.setValue(issuer);
        query.setID(requestId);
        query.setIssueInstant(now);
        query.setIssuer(issuer_);
        query.setRequest(request);
        return query;
    }

//srdc code
    public boolean grantRequest(String patientId, String requesterRole,
                                String resource) {
        boolean result = false;
        try {
            sslSetup();


            String response = callGetDecision(patientId, "srdc", "ROLECODE:"
                    + requesterRole.toUpperCase(), "RESOURCECODE:"
                    + resource.toUpperCase().replaceAll(" ", ""), "READ");
            result = response.indexOf("Permit") != -1;
            System.out.println("Grant request for patient '" + patientId
                    + "' from requestor role '" + requesterRole
                    + "' for resource '" + resource + "' results in " + result);
            boolean atnalog = new Boolean(ResourceBundle.getBundle("icardea").getString("atna.log")).booleanValue();

            // TODO: ATNA
            // Send ATNA Message: Grant Request Message
            // +"resource"+ is requested from "+requesterRole+" for
            // "+patientID+" with result "+result.
            if (atnalog) {
                ResourceBundle properties = ResourceBundle.getBundle("icardea");
                String atnalogServer = properties.getString("atna.log.server");
                int port = 2861;

                String xml = Audit.createMessage("GRM", patientId, resource, requesterRole);//TODO: Grant Request Message
                Audit a = null;
                try {
                    a = new Audit(atnalogServer, port);
                } catch (UnknownHostException e) {

                    e.printStackTrace();
                }
                a.send_udp(a.create_syslog_xml("caremanager", xml));
            }
        } catch (Exception e) {
            LOGGER.error(" ", e);
        }

        return result;
    }

    public int getSslConfigSetting() {
        return sslConfigSetting;
    }

    public void setSslConfigSetting(int sslConfigSetting) {
        this.sslConfigSetting = sslConfigSetting;
    }
}
