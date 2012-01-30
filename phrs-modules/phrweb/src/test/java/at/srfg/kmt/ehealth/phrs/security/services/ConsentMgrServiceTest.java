/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import java.util.List;
import java.util.ResourceBundle;
import static org.junit.Assert.*;
import org.junit.*;
import tr.com.srdc.icardea.consenteditor.webservice.client.ConsentManagerImplServiceStub;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import org.apache.commons.configuration.PropertiesConfiguration;

@Ignore
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

    @Ignore
    @Test
    public void testBasicStubConnection() throws Exception {
        String endpoint = null;

        ResourceBundle properties = ResourceBundle.getBundle("icardea");
        endpoint = properties.getString("consent.ws.endpoint");
        System.out.println("endpoint =" + endpoint);
        // 1. Create the Stub to access the service (proxy if you like) (defaults to URL in the WSDL)
        //        StockQuoteServiceStub stub = new StockQuoteServiceStub(
        //            "http://localhost:8080/axis2/services/StockQuoteService"
        //        );    
        //ConsentManagerImplServiceStub stub = new ConsentManagerImplServiceStub(endpoint + "?wsdl");
        String url = "http://localhost:8080/consenteditor/services/ConsentManagerImplService?wsdl";
        ConsentManagerImplServiceStub stub = new ConsentManagerImplServiceStub(url);

        // 2. Create a request object (here a nested class named after the matching method)
        //     ConsentManagerImplServiceStub.GetSubjects req = new ConsentManagerImplServiceStub.GetSubjects();
        ConsentManagerImplServiceStub.GetSubjects req = new ConsentManagerImplServiceStub.GetSubjects();

        // 3. Populate the request object with all the necessary information
        //req.set

        // 4. Access the actual web method which returns a response object
        //ConsentManagerImplServiceStub.GetSubjectsResponse res = stub.getSubjects(req);
        ConsentManagerImplServiceStub.GetSubjectsResponse res = stub.getSubjects(req);

        // 5. Extract the detail information from the response object
        System.out.println(res.getGetSubjectsReturn());
    }

    /**
     * Test of sslSetup method, of class ConsentMgrService.
     */
    @Ignore
    @Test
    public void testSslSetup() throws Exception {
        System.out.println("sslSetup");
        ConsentMgrService.sslSetup();

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
     */
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

    }

    /**
     * Test of callGetDecision method, of class ConsentMgrService.
     */
    @Ignore
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

        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("Test fails.");
    }

    /**
     * Test of callGetSubjects method, of class ConsentMgrService.
     */
    @Test
    public void testCallGetSubjects() {
        System.out.println("callGetSubjects");
        ConsentMgrService instance = new ConsentMgrService();
        //List expResult = null;
        List<String> result = instance.callGetSubjects();

        assertNotNull("callGetSubjects result null", result);
        if (result == null) {
        } else {
            for (String res : result) {
                System.out.println("subject code= " + result);
            }
        }
        //assertEquals(expResult, result);

    }

    /**
     * Test of callGetResources method, of class ConsentMgrService.
     */
    @Ignore
    @Test
    public void testCallGetResources() {
        System.out.println("callGetResources");
        ConsentMgrService instance = new ConsentMgrService();
        //List expResult = null;
        List<String> result = instance.callGetResources();
        assertNotNull("callGetResources result null", result);

        if (result == null) {
            System.out.println("testCallGetResources result null ");
        } else {
            for (String res : result) {
                System.out.println("res code= " + result);
            }
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
