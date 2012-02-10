package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.*;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.ArrayOf_xsd_anyType;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecision;

import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecisionResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResources;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResourcesResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjects;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjectsResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub;

import static org.junit.Assert.*;

public class ConsentMgrServiceTest {

    String ROLE_CODE_DOC = "ROLECODE:DOCTOR";
    String ROLE_CODE_NURSE = "ROLECODE:DOCTOR";
    String ACTION_ID_READ = "READ";
    String PROTOCOL_ID = "191";
    String PHR_ID = "1";
    String RESOURCE_CODE_CONDITION = "CONDITION";
    String ISSUER_NAME = "PHR";

    public ConsentMgrServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
//    @Test
//    public void testPingLocator(){
//        org.apache.axis.utilsPingServiceLocator service = new org.apache.axis.utils.PingServiceLocator();
//        try {
//            endpoint = new java.net.URL(opts.getURL());
//        } catch (java.net.MalformedURLException e) {
//            throw new javax.xml.rpc.ServiceException(e);
//        }
//
//        PingPort port = (PingPort) service.getPing5(endpoint);
//
//        /*
//        *     At this point all preparations are done. Using the port we can
//        *     now perform as many calls as necessary.
//        */
//
//        // perform call
//        StringHolder text =
//                new StringHolder("WSS4J - Scenario 5 - text");
//        TicketType type =
//                new TicketType("WSS4J - Scenario 5 - TicketType");
//
//        port.ping(type, text);
//        System.out.println(text.value);
//
//        if (opts.isFlagSet('t') > 0) {
//            long startTime = System.currentTimeMillis();
//
//            for (int i = 0; i < 20; i++) {
//                port.ping(type, text);
//            }
//    }
    @Test
    public void testDecisionRequestString() {
        ConsentMgrService cms = new ConsentMgrService();
        assertNotNull(cms);
        //apply substring(39) to remove <?xml version="1.0" encoding="UTF-16"?> 
        String requestString = getTestRequestString(true);
        assertNotNull("requestString  SUBSTRING null" + requestString);

        //System.out.println(" WHOLE message requestString" + requestString);

    }

    public String getTestRequestString(boolean makeSubString) {
        return this.getTestRequestString(
                makeSubString,
                "patientid" + UUID.randomUUID().toString(),
                "issuerName11111111111111111111111111111111111111",
                PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DOCTOR,
                PhrsConstants.AUTHORIZE_RESOURCE_CODE_CONDITION,
                PhrsConstants.AUTHORIZE_ACTION_CODE_READ);
    }

    public String getTestRequestString(boolean makeSubString, String patientId, String issuerName,
            String subjectCode, String resourceCode, String action) {
        ConsentMgrService cms = new ConsentMgrService();
        String requestString = cms.generateRequestAsString(cms.generateSAMLRequest(
                "1", issuerName, subjectCode, resourceCode, action));


        System.out.println(" requestString=" + requestString);
        String shortRequest = requestString;
        System.out.println(" requestString.substring(39)=" + shortRequest.substring(39));

        if (makeSubString) {
            return requestString.substring(39);
        }
        return requestString;
    }
/*
    @Test
    public void testBasicStubConnectionUsingiCardeaProperties() {
        String endpoint = null;

        //ResourceBundle properties = ResourceBundle.getBundle("icardea");
        //endpoint = properties.getString("consent.ws.endpoint");

        // 1. Create the Stub to access the service (proxy if you like) (defaults to URL in the WSDL)
        //ConsentManagerImplServiceStub stub = new ConsentManagerImplServiceStub(endpoint + "?wsdl");
        //String url = "http://localhost:8080/consenteditor/services/ConsentManagerImplService?wsdl";
        String endPoint = ConsentMgrService.getServiceEndpoint();
        System.out.println("endpoint =" + endpoint);
        ConsentManagerImplServiceStub stub = null;

        try {
            stub = new ConsentManagerImplServiceStub(endPoint);

            assertNotNull("ConsentManagerImplServiceStub is null " + stub);
        } catch (Exception e) {
            fail("ConsentManagerImplServiceStub exception endpoint=" + endpoint);
        }
        // 2. Create a request object (here a nested class named after the matching method)
        //     ConsentManagerImplServiceStub.GetSubjects req = new ConsentManagerImplServiceStub.GetSubjects();
        ConsentManagerImplServiceStub.GetSubjects req = null;
        try {
            req = new ConsentManagerImplServiceStub.GetSubjects();
            assertNotNull("GetSubjects is null " + req);
        } catch (Exception e) {
            fail("ConsentManagerImplServiceStub.GetSubjects exception endpoint=" + endpoint);

        }
        // 3. Populate the request object with all the necessary information
        //Do elsewhere, this is beyond the scope of this test.

        // 4. Access the actual web method which returns a response object
        //ConsentManagerImplServiceStub.GetSubjectsResponse res = stub.getSubjects(req);
        try {
            ConsentManagerImplServiceStub.GetSubjectsResponse res = stub.getSubjects(req);
            assertNotNull("GetSubjectsResponse is null " + res);
        } catch (RemoteException remoteException) {
            fail("ConsentManagerImplServiceStub.GetSubjectsResponse exception endpoint=" + endpoint);

        }
        // 5. Extract the detail information from the response object
        //System.out.println(res.getGetSubjectsReturn());
    }
  */
    public void testConsentServiceStub() {
        ConsentMgrService cms = new ConsentMgrService();

        assertNotNull(cms);
        ConsentManagerImplServiceStub stub = cms.getConsentServiceStub();
        assertNotNull(stub);
    }
    /*
     * @Test public void testGrantAuthorityRequest() { ConsentMgrService cms =
     * new ConsentMgrService(); String res= cms.grantRightsRequest("191",
     * "doctor", "condition"); assertNotNull(res);
    }
     */

    /**
     * Test of sslSetup method, of class ConsentMgrService.
     */
    @Test
    public void testSslSetup() throws Exception {
        System.out.println("sslSetup");
        boolean flag = false;
        int sslConfig = 2;
        try {
            SSLLocalClient.sslSetup(sslConfig);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            fail("exception occured with sslSetup");
        }
        assertTrue("Failed ssl setup ", flag);

    }

    /**
     * Test of isMedicalRole method, of class ConsentMgrService.
     *
     * @Ignore requires session @Test public void testIsMedicalRole() {
     * System.out.println("isMedicalRole"); boolean expResult = true; boolean
     * result = ConsentMgrService.isMedicalRole(); assertEquals(expResult,
     * result);
     *
     * }
     */
    /**
     * Test of isConsentMgrRole method, of class ConsentMgrService.
     */
    @Test
    public void testIsConsentMgrRole() {
        System.out.println("isConsentMgrRole");
        String subjectCode = ROLE_CODE_DOC;
        boolean expResult = true;
        boolean result = ConsentMgrService.isConsentMgrRole(subjectCode);
        assertEquals(expResult, result);

    }

    @Test
    public void testConfigPropertyConfig() {
        System.out.println("testConfigPropertyConfig");
        boolean flag = false;
        PropertiesConfiguration config = ConfigurationService.getInstance().getPropertiesConfiguration();
        // System.out.println("testisHealthInfoAccessibleRoleWithCode prop="+config);

        assertNotNull("PropertiesConfiguration null", config);

    }

    @Test
    public void testConfig() {
        System.out.println("testConfig");
        boolean flag = false;
        ConfigurationService config = ConfigurationService.getInstance();
        // System.out.println("testisHealthInfoAccessibleRoleWithCode prop="+config);

        assertNotNull("ConfigurationService null", config);

    }

    @Test
    public void testisHealthInfoAccessibleRoleWithCode() {
        System.out.println("testisHealthInfoAccessibleRoleWithCode");
        boolean flag = false;
        String value = ConfigurationService.getInstance().getProperty("isAllHealthinfoAccessibleByRole");
        System.out.println("testisHealthInfoAccessibleRoleWithCode prop=" + value);
        if (value != null) {
            value = value.trim();
        }

        if (value != null && value.equalsIgnoreCase("true")) {
            flag = true;
        }
        assertEquals(true, flag);

    }

    @Test
    public void testisHealthInfoAccessibleByThisRole() {
        System.out.println("isConsentMgrRole");
        String subjectCode = ROLE_CODE_DOC;
        boolean expResult = true;
        boolean result = ConfigurationService.getInstance().isHealthInfoAccessibleByThisRole(subjectCode);
        assertEquals(expResult, result);

    }

    @Test
    public void testisHealthInfoAccessibleRole() {
        System.out.println("isConsentMgrRole");
        String subjectCode = ROLE_CODE_DOC;
        boolean expResult = true;
        boolean result = ConfigurationService.getInstance().isHealthInfoAccessibleByRole();
        assertEquals(expResult, result);
    }

    //String SUBJECT_MEDICATIONS="";
    /**
     * Test of isConsentMgrAction method, of class ConsentMgrService.
     */
    @Test
    public void testIsConsentMgrAction() {
        System.out.println("isConsentMgrAction");
        String actionCode = ACTION_ID_READ;
        boolean expResult = true;
        boolean result = ConsentMgrService.isConsentMgrAction(actionCode);
        assertEquals(expResult, result);

    }

    /**
     * Test of isPermitted method, of class ConsentMgrService.
     */
    /*
     * "1","phrs","ROLECODE:DOCTOR","RESOURCECODE:CONDITION","READ"
     */
    @Ignore
    @Test
    public void testIsPermitted_5args() {
        System.out.println("isPermitted");
        String targetUser = PROTOCOL_ID; // PHR_ID
        String subjectCode = ROLE_CODE_DOC;
        String idType = PhrsConstants.PROTOCOL_ID_NAME;
        String resourceCode = RESOURCE_CODE_CONDITION;
        String action = ACTION_ID_READ;
        ConsentMgrService instance = new ConsentMgrService();

        boolean expResult = false;
        boolean result = instance.isPermitted(targetUser, subjectCode, idType, resourceCode, action);

        assertEquals(expResult, result);
        System.out.println("is permitted = " + result);

    }

    /**
     * Test of auditGrantRequest method, of class ConsentMgrService.
     
    @Ignore
    @Test
    public void testAuditGrantRequest() {
        System.out.println("auditGrantRequest");

        String patientId = PROTOCOL_ID; // PHR_ID
        String requesterRole = ROLE_CODE_DOC;
        String idType = PhrsConstants.PROTOCOL_ID_NAME;
        String resourceCode = RESOURCE_CODE_CONDITION;
        String action = ACTION_ID_READ;
        ConsentMgrService instance = new ConsentMgrService();

        boolean expResult = false;
        boolean result = instance.auditGrantRequest(patientId, idType, requesterRole, resourceCode);

        assertEquals(expResult, result);
        System.out.println("is Audit sent? " + result);

    }*/

    /**
     * Test of callGetDecision method, of class ConsentMgrService.
     */
    @Test
    public void testCallGetDecision() {
        System.out.println("callGetDecision");
        String patientId = PROTOCOL_ID;// or 
        String issuerName = ISSUER_NAME;
        String subjectCode = ROLE_CODE_DOC;

        String targetUser = PROTOCOL_ID;///PHR_ID    
        String resourceCode = RESOURCE_CODE_CONDITION;
        String action = ACTION_ID_READ;

        ConsentMgrService instance = new ConsentMgrService();
        //String expResult = "";
        String result = instance.callGetDecision(patientId, issuerName, subjectCode, resourceCode, action);
        assertNotNull("decision xml null ", result);
        System.out.println("decision xml= " + result);

        // TODO review the generated test code and remove the default call to fail.

    }

    /**
     * Test of callGetSubjects method, of class ConsentMgrService.
     */
    @Test
    public void testCallGetSubjects() {
        System.out.println("callGetSubjects");
        ConsentMgrService instance = new ConsentMgrService();

        List<String> result = instance.callGetSubjects();

        assertNotNull("callGetSubjects result null", result);

        for (String res : result) {
           System.out.println("subject code= " + res);
         }


    }

    /**
     * Test of callGetResources method, of class ConsentMgrService.
     */
    @Test
    public void testCallGetResources() {
        System.out.println("callGetResources");
        ConsentMgrService instance = new ConsentMgrService();
        //List expResult = null;
        List<String> result = instance.callGetResources();
        assertNotNull("callGetResources result null", result);

        for (String res : result) {
          System.out.println("res code= " + res);
        }

    }
    /**
     * Test of hasUserByPhrId method, of class ConsentMgrService.
     *
     * @Test public void testHasUserByPhrId() {
     * System.out.println("hasUserByPhrId"); String phrId = "";
     * ConsentMgrService instance = new ConsentMgrService(); boolean expResult =
     * false; boolean result = instance.hasUserByPhrId(phrId);
     * assertEquals(expResult, result);
     *
     * }
     *
     *
     * @Test public void testHasUserByProtocolId() {
     * System.out.println("hasUserByProtocolId"); String phrId = "";
     * ConsentMgrService instance = new ConsentMgrService(); boolean expResult =
     * false; boolean result = instance.hasUserByProtocolId(phrId);
     * assertEquals(expResult, result);
     *
     * }
     *
     *
     * @Test public void testIsPermitted_String() {
     * System.out.println("isPermitted"); String XACMLAuthzDecisionStatement =
     * ""; ConsentMgrService instance = new ConsentMgrService(); boolean
     * expResult = false; boolean result =
     * instance.isPermitted(XACMLAuthzDecisionStatement);
     * assertEquals(expResult, result);
     *
     * }
     */
}
