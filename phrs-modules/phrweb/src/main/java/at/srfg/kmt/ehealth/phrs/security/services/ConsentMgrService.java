package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.ArrayOf_xsd_anyType;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecision;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecisionResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResources;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResourcesResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjects;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjectsResponse;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.*;
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
/**
 * 
 * Includes consent manager code from SRDC
 */
@SuppressWarnings("serial")
public class ConsentMgrService implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConsentMgrService.class);
    private ConsentManagerImplServiceStub stub = null;
    private String ISSUERNAME = "PHR";
    //not used, except to check for the https. The stub contains the 
    //
    //private String endpointConsentMgr;
    private int sslConfigSetting = 0;
    private AuditAtnaService auditService;
    private  int timeout=8000;
    private boolean isSSLConsent=false;
    public ConsentMgrService() {
        //1000 * 5
        //endpointConsentMgr = getServiceEndpoint();
        stub = getConsentServiceStub();
        //get this services
        auditService = new AuditAtnaService();

        String endpoint = ConfigurationService.getInstance().getProperty("consent.web.endpoint","http");
        sslConfigSetting = endpoint.contains("https") ? 2 : 0;
        String sslStr = ConfigurationService.getInstance().getProperty("consent.ssl","false");
        sslStr=sslStr.trim();
        isSSLConsent = Boolean.parseBoolean(sslStr);
    }

    public AuditAtnaService getAuditAtnaService() {
        return auditService;
    }
    public ConsentManagerImplServiceStub getConsentServiceStub() {

        if (stub != null) {
            return stub;
        }
        try {

            stub = new ConsentManagerImplServiceStub();
            //No timeout timeoutSetup(stub._getServiceClient().getOptions());

        } catch (AxisFault e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return stub;
    }
    public static void timeoutSetup(Options options) {
        
        if(options!=null){
            try {

                //int timeOutInMilliSeconds = 10000;
                //options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(timeOutInMilliSeconds));
                //options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, new Integer(timeOutInMilliSeconds));
                String timeoutStr = ConfigurationService.getInstance().getProperty("consent.ws.timeout","20000");
                int timeout=Integer.parseInt(timeoutStr.trim());
                options.setProperty(HTTPConstants.SO_TIMEOUT, timeout);
                options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, timeout);
                // or
                //options.setTimeOutInMilliSeconds(timeOutInMilliSeconds);
                //options.setProperty(HTTPConstants.CHUNKED, "false");

            } catch (Exception e) {
                LOGGER.error("timeoutSetup ", e);
            }
        }
    }

    public void sslSetup() throws Exception {

        //timeoutSetup();
        if(isSSLConsent)
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
     *
     * @param subjectCode
     * @return
     */
    public static boolean isAccessibleByThisRole(String subjectCode) {
        return ConfigurationService.getInstance().isHealthInfoAccessibleByThisRole(subjectCode);
    }

    public static boolean isConsentMgrAction(String actionCode) {

        return ConfigurationService.getInstance().isConsentAction(actionCode);
    }

    public String getProtocolId(String ownerUri) {
        CommonDao dao = PhrsStoreClient.getInstance().getCommonDao();
        return dao.getProtocolId(ownerUri);
    }

    /**
     * @param targetUser (user identifier: can send two queries for phr or
     * protocol ID)
     * @param subjectCode (role code)
     * @param idType
     * @param resourceCode (mediation, basic health, condition, etc)
     * @param action (READ,WRITE...)
     * @return
     */
    public boolean isPermitted(String targetUser, String subjectCode,
            String resourceCode, String action) {
        return this.isPermitted(targetUser, subjectCode, "", resourceCode, action);
    }

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
//                if (!idType.equals(PhrsConstants.PROTOCOL_ID_NAME)) {
//                    // try on phrId
//                    userIdentifier = getProtocolId(targetUser);
//                    if (!flag && userIdentifier != null && !userIdentifier.isEmpty()) {
//                        result = callGetDecision(userIdentifier, ISSUERNAME,
//                                subjectCode, resourceCode, action);
//                        flag = isPermitted(result);
//                    } else {
//                    }
//                }
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
     * Permit or Deny
     *
     * @param decsionResponse is the String XACMLAuthzDecisionStatement
     * @return
     */
    protected boolean isPermitted(String xacmlAuthzDecisionStatement) {
        // <xacml-context:Decision>Deny</xacml-context:Decision>

        if (xacmlAuthzDecisionStatement != null) {
            return (xacmlAuthzDecisionStatement.indexOf("Permit") != -1);
        }
        return false;
    }

    public static String getServiceEndpoint() {
        String endpoint = null;

        //ResourceBundle properties = ResourceBundle.getBundle("icardea");
        //endpoint = properties.getString("consent.ws.endpoint");
        endpoint = ConfigurationService.getInstance().getConsentServiceEndpoint();

        if (endpoint != null) {
            endpoint = endpoint.trim();
        }
        return endpoint;

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
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?><xacml-saml:XACMLAuthzDecisionStatement xmlns:xacml-saml=\"urn:oasis:names:tc:xacml:2.0:profile:saml2.0:v2:schema:assertion\"><xacml-context:Response xmlns:xacml-context=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\"><xacml-context:Result><xacml-context:Decision>Deny Deny</xacml-context:Decision></xacml-context:Result></xacml-context:Response></xacml-saml:XACMLAuthzDecisionStatement>";
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
            if (response != null) {
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
      public static List<String> callGetSubjects(ConsentManagerImplServiceStub stub) {

        List<String> list = new ArrayList<String>();
        try {

            
            GetSubjects request = new GetSubjects();
            GetSubjectsResponse response = stub.getSubjects(request);
            if (response != null) {
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


            if (response != null) {
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


            String response = callGetDecision(patientId, "phr", "ROLECODE:"
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
//            PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHYSICIAN,
//            PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE,
//            PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_FAMILY_MEMBER,
//            PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PSYCHIATRIST,
//            PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHARMACIST,
//            PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DENTIST,
//            PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_ADMIN,
//            PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_TEST
//RZ-iCARDEA-Admin
//RZ-iCARDEA-Doctor-Cardiologist
//RZ-iCARDEA-Doctor-Electrophysiologist
//RZ-iCARDEA-Doctor-Physician
//RZ-iCARDEA-Nurse
// String mappedRole= ConsentMgrService.getMedicalRoleMapping(list);

    public static String extractMappedRole(Map inputMap) {
        String mappedRole = null;
        if (inputMap != null && inputMap.containsKey(PhrsConstants.OPEN_ID_PARAM_ROLE)) {
            Object obj = inputMap.get(PhrsConstants.OPEN_ID_PARAM_ROLE);
            if (obj != null) {
                if (obj instanceof Collection) {
                    mappedRole = ConsentMgrService.getMedicalRoleMapping((Collection) obj);
                } else if (obj instanceof String) {
                    mappedRole = ConsentMgrService.getMedicalRoleMapping((String) obj);
                }
            }
        }
        return mappedRole;

    }

    /**
     *
     * @param roles
     * @return
     */
    public static String getMedicalRoleMapping(Collection<String> roles) {
        String selectedRole = null;

        if (roles != null) {
            for (String role : roles) {

                selectedRole = getMedicalRoleMapping(role);
                if (selectedRole != null) {
                    break;
                }
            }

        }

        return selectedRole;
    }

    /**
     * Primarily for mapping OpenId roles to consent mgt roles that are used by
     * the PHR and Consent mgr
     *
     * @param role
     * @return null if there is no medical mapping
     */
    public static String getMedicalRoleMapping(String role) {
        String selectedRole = null;

        if (role != null) {
            String lower = role.toLowerCase();

            if (lower.contains("doctor")) {
                selectedRole = PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHYSICIAN;
            } else if (lower.contains("nurse")) {
                selectedRole = PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE;
            }
        }

        return selectedRole;
    }
}
