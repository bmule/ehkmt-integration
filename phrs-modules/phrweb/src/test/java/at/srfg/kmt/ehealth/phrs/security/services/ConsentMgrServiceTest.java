package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.*;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

//import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.ArrayOf_xsd_anyType;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecision;

import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetDecisionResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResources;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetResourcesResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjects;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub.GetSubjectsResponse;
import at.srfg.kmt.ehealth.phrs.services.ConsentManagerImplServiceStub;
import org.apache.axis2.AxisFault;

import static org.junit.Assert.*;

public class ConsentMgrServiceTest {

    String ROLE_CODE_DOC = "ROLECODE:DOCTOR";
    String ROLE_CODE_NURSE = "ROLECODE:DOCTOR";
    String ACTION_ID_READ = "READ";
    String PROTOCOL_ID = "191";
    String PHR_ID = "1";
    String RESOURCE_CODE_CONDITION = "RESOURCECODE:CONDITION";
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
        //ConsentMgrService cms = new ConsentMgrService();
        //assertNotNull(cms);
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
        boolean result = instance.isPermitted(
                targetUser, 
                subjectCode, 
                idType, 
                resourceCode, 
                action);

//         boolean permitted= cms.isPermitted(
//                patientId, 
//                subjectCode, 
//                "protocolId",               
//                resourceCode, 
//                action);

        assertEquals(expResult, result);
        System.out.println("is permitted = " + result);

    }



    /**
     * Test of callGetDecision method, of class ConsentMgrService.
     */
    @Test
    public void testCallGetDecision() {
        ConsentMgrService cms = new ConsentMgrService();
       // boolean permit = cms.grantRequest("191", "ROLECODE:DOCTOR", "RESOURCECODE:CONDITION");
        //cms.grantRequest("191", "doctor", "condition");
        
        System.out.println("callGetDecision");
        String patientId = "191";//PROTOCOL_ID;// or 
        String issuerName = ISSUER_NAME;
        String subjectCode = "ROLECODE:DOCTOR";//ROLE_CODE_DOC; ROLECODE:DOCTOR

        //String targetUser = PROTOCOL_ID;///PHR_ID    
        String resourceCode = "RESOURCECODE:CONDITION";//BASICHEALTH RESOURCECODE:CONDITION RESOURCE_CODE_CONDITION;
        String action = "READ";//ACTION_ID_READ;

        
        //String expResult = "";
        String result = cms.callGetDecision(patientId, issuerName, subjectCode, resourceCode, action);
        assertNotNull("decision xml null ", result);
        System.out.println("decision xml= " + result);

        // TODO review the generated test code and remove the default call to fail.

    }
    
 /**
  * Added test document for 191
  * The doctor is able to do alot... but not the nurse
  * 
  */
    @Test
    public void testIsPermittedDoctorRead191() {
        ConsentMgrService cms = new ConsentMgrService();
       // boolean permit = cms.grantRequest("191", "ROLECODE:DOCTOR", "RESOURCECODE:CONDITION");
        //cms.grantRequest("191", "doctor", "condition");
        
        System.out.println("callGetDecision");
        String idType=PhrsConstants.PROTOCOL_ID_NAME;
        String patientId = "191";//PROTOCOL_ID;// or 
        String issuerName = ISSUER_NAME;
        String subjectCode = "ROLECODE:DOCTOR";//ROLE_CODE_DOC; ROLECODE:DOCTOR

        //String targetUser = PROTOCOL_ID;///PHR_ID    
        String resourceCode = "RESOURCECODE:BASICHEALTH";//RESOURCECODE:CONDITION";//BASICHEALTH RESOURCECODE:CONDITION RESOURCE_CODE_CONDITION;
        String action = "READ";//ACTION_ID_READ;

        
        //String expResult = "";
        boolean permitted= cms.isPermitted(
                patientId, 
                subjectCode, 
                idType,               
                resourceCode, 
                action);
        //String result = cms.callGetDecision(patientId, issuerName, subjectCode, resourceCode, action);
        assertTrue("Doctor Expect patient 191 is permitted -check test doc in consent editor", permitted);
        System.out.println("doctor permitted  191= " + permitted);

        // TODO review the generated test code and remove the default call to fail.

    }
    /**
     * This should be a test for nurse, but there is no way to change 
     * the Consent editor test setup
     * 
     */
    @Test
    public void testIsPermittedNurseRead191() {
        ConsentMgrService cms = new ConsentMgrService();
        // boolean permit = cms.grantRequest("191", "ROLECODE:DOCTOR", "RESOURCECODE:CONDITION");
        //cms.grantRequest("191", "doctor", "condition");

        System.out.println("callGetDecision");
        String idType=PhrsConstants.PROTOCOL_ID_NAME;
        String patientId = "191";//PROTOCOL_ID;// or
        String issuerName = ISSUER_NAME;
        String subjectCode = "ROLECODE:NURSE";//ROLE_CODE_DOC; ROLECODE:DOCTOR

        //String targetUser = PROTOCOL_ID;///PHR_ID
        String resourceCode = "RESOURCECODE:IMMUNIZATION";//BASICHEALTH RESOURCECODE:CONDITION RESOURCE_CODE_CONDITION;
        String action = "READ";//ACTION_ID_READ;


        //String expResult = "";
        boolean permitted= cms.isPermitted(
                patientId,
                subjectCode,
                idType,
                resourceCode,
                action);
        //String result = cms.callGetDecision(patientId, issuerName, subjectCode, resourceCode, action);
        assertFalse("nurse Expect patient 191 not permitted" , permitted);
        System.out.println("nurse permitted  191= " + permitted);

        // TODO review the generated test code and remove the default call to fail.

    }

    /**
     * Test of callGetSubjects method, of class ConsentMgrService.
     */
    @Test
    public void testCallGetSubjects() {
        System.out.println("callGetSubjects");
        ConsentMgrService cms = new ConsentMgrService();

        List<String> result = cms.callGetSubjects();

        assertNotNull("callGetSubjects result null", result);

        for (String res : result) {
           System.out.println("subject code= " + res);
         }


    }
        @Test
    public void testCallGetSubjectsOverrideStub() {
        System.out.println("testCallGetSubjectsOverrideStub");
        

            try {
                List<String> result = ConsentMgrService.callGetSubjects(new ConsentManagerImplServiceStub());
                
                assertNotNull("callGetSubjects result null", result);
                
                for (String res : result) {
                    System.out.println("subject code= " + res);
                }
            } catch (AxisFault e) {
                fail("exception AxisFault");
                e.printStackTrace();
                
            } catch (Exception e) {
                e.printStackTrace();
                fail("exception");
            }


    }
    //stub = new ConsentManagerImplServiceStub();

    /**
     * Test of callGetResources method, of class ConsentMgrService.
     */
    @Test
    public void testCallGetResources() {
        System.out.println("callGetResources");
        ConsentMgrService cms = new ConsentMgrService();
        //List expResult = null;
        List<String> result = cms.callGetResources();
        assertNotNull("cms result null", result);

        for (String res : result) {
          System.out.println("res code= " + res);
        }

    }
    /**
     * Test of hasUserByPhrId method, of class ConsentMgrService.
     *
     * @Test public void testHasUserByPhrId() {
     * System.out.println("hasUserByPhrId"); String phrId = "";
     * ConsentMgrService cms = new ConsentMgrService(); boolean expResult =
     * false; boolean result = cms.hasUserByPhrId(phrId);
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
