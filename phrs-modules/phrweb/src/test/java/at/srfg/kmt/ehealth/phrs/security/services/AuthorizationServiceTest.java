/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

@Ignore
public class AuthorizationServiceTest {
    
    public AuthorizationServiceTest() {
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

    /**
     * Test of getDefault method, of class AuthorizationService.
     */
    @Test
    public void testGetDefault() {
        System.out.println("getDefault");
        int level = 0;
        AuthorizationService expResult = null;
        AuthorizationService result = AuthorizationService.getDefault(level);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getDefaultByOwnerUri method, of class AuthorizationService.
     */
    @Test
    public void testGetDefaultByOwnerUri() {
        System.out.println("getDefaultByOwnerUri");
        String ownerUri = "";
        AuthorizationService expResult = null;
        AuthorizationService result = AuthorizationService.getDefaultByOwnerUri(ownerUri);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of auditGrantRequest method, of class AuthorizationService.
     */
    @Test
    public void testAuditGrantRequest_3args() {
        System.out.println("auditGrantRequest");
        String patientId = "";
        boolean isPhrId = false;
        String resourceCode = "";
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.auditGrantRequest(patientId, isPhrId, resourceCode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of auditGrantRequest method, of class AuthorizationService.
     */
    @Test
    public void testAuditGrantRequest_4args() {
        System.out.println("auditGrantRequest");
        String patientId = "";
        String idType = "";
        String requesterRole = "";
        String resourceCode = "";
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.auditGrantRequest(patientId, idType, requesterRole, resourceCode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of permitAccessOnPhrId method, of class AuthorizationService.
     */
    @Test
    public void testPermitAccessOnPhrId() {
        System.out.println("permitAccessOnPhrId");
        String targetUser = "";
        String resourceCode = "";
        String action = "";
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.permitAccessOnPhrId(targetUser, resourceCode, action);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of permitAccessOnProtocolId method, of class AuthorizationService.
     */
    @Test
    public void testPermitAccessOnProtocolId() {
        System.out.println("permitAccessOnProtocolId");
        String targetUser = "";
        String resourceCode = "";
        String action = "";
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.permitAccessOnProtocolId(targetUser, resourceCode, action);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of permitAccess method, of class AuthorizationService.
     */
    @Test
    public void testPermitAccess() {
        System.out.println("permitAccess");
        String targetUser = "";
        boolean isPhrId = false;
        String resourceCode = "";
        String action = "";
        String subjectRole = "";
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.permitAccess(targetUser, isPhrId, resourceCode, action, subjectRole);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isResourceOwnedBySessionUser method, of class AuthorizationService.
     */
    @Test
    public void testIsResourceOwnedBySessionUser() {
        System.out.println("isResourceOwnedBySessionUser");
        String targetUser = "";
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isResourceOwnedBySessionUser(targetUser);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getConsentSubjects method, of class AuthorizationService.
     */
    @Test
    public void testGetConsentSubjects() {
        System.out.println("getConsentSubjects");
        AuthorizationService instance = new AuthorizationService();
        List expResult = null;
        List result = instance.getConsentSubjects();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getConsentResources method, of class AuthorizationService.
     */
    @Test
    public void testGetConsentResources() {
        System.out.println("getConsentResources");
        AuthorizationService instance = new AuthorizationService();
        List expResult = null;
        List result = instance.getConsentResources();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isConsentMgrRole method, of class AuthorizationService.
     */
    @Test
    public void testIsConsentMgrRole() {
        System.out.println("isConsentMgrRole");
        String subjectCode = "";
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isConsentMgrRole(subjectCode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getSessionUserLoginId method, of class AuthorizationService.
     */
    @Test
    public void testGetSessionUserLoginId() {
        System.out.println("getSessionUserLoginId");
        AuthorizationService instance = new AuthorizationService();
        String expResult = "";
        String result = instance.getSessionUserLoginId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getSessionUserOwnerUri method, of class AuthorizationService.
     */
    @Test
    public void testGetSessionUserOwnerUri() {
        System.out.println("getSessionUserOwnerUri");
        AuthorizationService instance = new AuthorizationService();
        String expResult = "";
        String result = instance.getSessionUserOwnerUri();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getSessionUserRole method, of class AuthorizationService.
     */
    @Test
    public void testGetSessionUserRole() {
        System.out.println("getSessionUserRole");
        AuthorizationService instance = new AuthorizationService();
        String expResult = "";
        String result = instance.getSessionUserRole();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isMedicalRole method, of class AuthorizationService.
     */
    @Test
    public void testIsMedicalRole() {
        System.out.println("isMedicalRole");
        boolean expResult = false;
        boolean result = AuthorizationService.isMedicalRole();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of hasFilterProtocolId method, of class AuthorizationService.
     */
    @Test
    public void testHasFilterProtocolId() {
        System.out.println("hasFilterProtocolId");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.hasFilterProtocolId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getFilterProtocolId method, of class AuthorizationService.
     */
    @Test
    public void testGetFilterProtocolId() {
        System.out.println("getFilterProtocolId");
        AuthorizationService instance = new AuthorizationService();
        String expResult = "";
        String result = instance.getFilterProtocolId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getModify method, of class AuthorizationService.
     */
    @Test
    public void testGetModify() {
        System.out.println("getModify");
        AuthorizationService instance = new AuthorizationService();
        String expResult = "";
        String result = instance.getModify();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setModify method, of class AuthorizationService.
     */
    @Test
    public void testSetModify() {
        System.out.println("setModify");
        String modify = "";
        AuthorizationService instance = new AuthorizationService();
        instance.setModify(modify);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of makeModifyYes method, of class AuthorizationService.
     */
    @Test
    public void testMakeModifyYes() {
        System.out.println("makeModifyYes");
        AuthorizationService instance = new AuthorizationService();
        instance.makeModifyYes();
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of makeModifyNo method, of class AuthorizationService.
     */
    @Test
    public void testMakeModifyNo() {
        System.out.println("makeModifyNo");
        AuthorizationService instance = new AuthorizationService();
        instance.makeModifyNo();
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setAllowAllFalse method, of class AuthorizationService.
     */
    @Test
    public void testSetAllowAllFalse() {
        System.out.println("setAllowAllFalse");
        AuthorizationService instance = new AuthorizationService();
        instance.setAllowAllFalse();
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getAllowAll method, of class AuthorizationService.
     */
    @Test
    public void testGetAllowAll() {
        System.out.println("getAllowAll");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getAllowAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowAll method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowAll() {
        System.out.println("isAllowAll");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getAllowAny method, of class AuthorizationService.
     */
    @Test
    public void testGetAllowAny() {
        System.out.println("getAllowAny");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getAllowAny();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowAny method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowAny() {
        System.out.println("isAllowAny");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowAny();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setPermittedActions method, of class AuthorizationService.
     */
    @Test
    public void testSetPermittedActions() {
        System.out.println("setPermittedActions");
        AuthorizationService instance = new AuthorizationService();
        instance.setPermittedActions();
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getAllowTest method, of class AuthorizationService.
     */
    @Test
    public void testGetAllowTest() {
        System.out.println("getAllowTest");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getAllowTest();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowTest method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowTest() {
        System.out.println("isAllowTest");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowTest();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setAllowTest method, of class AuthorizationService.
     */
    @Test
    public void testSetAllowTest() {
        System.out.println("setAllowTest");
        boolean allowTest = false;
        AuthorizationService instance = new AuthorizationService();
        instance.setAllowTest(allowTest);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getAllowCreate method, of class AuthorizationService.
     */
    @Test
    public void testGetAllowCreate() {
        System.out.println("getAllowCreate");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getAllowCreate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowCreate method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowCreate() {
        System.out.println("isAllowCreate");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowCreate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setAllowCreate method, of class AuthorizationService.
     */
    @Test
    public void testSetAllowCreate() {
        System.out.println("setAllowCreate");
        boolean allowCreate = false;
        AuthorizationService instance = new AuthorizationService();
        instance.setAllowCreate(allowCreate);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowView method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowView() {
        System.out.println("isAllowView");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowView();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setAllowView method, of class AuthorizationService.
     */
    @Test
    public void testSetAllowView() {
        System.out.println("setAllowView");
        boolean allowView = false;
        AuthorizationService instance = new AuthorizationService();
        instance.setAllowView(allowView);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowEdit method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowEdit() {
        System.out.println("isAllowEdit");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowEdit();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getAllowEdit method, of class AuthorizationService.
     */
    @Test
    public void testGetAllowEdit() {
        System.out.println("getAllowEdit");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getAllowEdit();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setAllowEdit method, of class AuthorizationService.
     */
    @Test
    public void testSetAllowEdit() {
        System.out.println("setAllowEdit");
        boolean allowEdit = false;
        AuthorizationService instance = new AuthorizationService();
        instance.setAllowEdit(allowEdit);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getAllowDelete method, of class AuthorizationService.
     */
    @Test
    public void testGetAllowDelete() {
        System.out.println("getAllowDelete");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getAllowDelete();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowDelete method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowDelete() {
        System.out.println("isAllowDelete");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowDelete();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setAllowDelete method, of class AuthorizationService.
     */
    @Test
    public void testSetAllowDelete() {
        System.out.println("setAllowDelete");
        boolean allowDelete = false;
        AuthorizationService instance = new AuthorizationService();
        instance.setAllowDelete(allowDelete);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getAllowList method, of class AuthorizationService.
     */
    @Test
    public void testGetAllowList() {
        System.out.println("getAllowList");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getAllowList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowList method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowList() {
        System.out.println("isAllowList");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setAllowList method, of class AuthorizationService.
     */
    @Test
    public void testSetAllowList() {
        System.out.println("setAllowList");
        boolean allowList = false;
        AuthorizationService instance = new AuthorizationService();
        instance.setAllowList(allowList);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getAllowViewChart method, of class AuthorizationService.
     */
    @Test
    public void testGetAllowViewChart() {
        System.out.println("getAllowViewChart");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getAllowViewChart();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isAllowViewChart method, of class AuthorizationService.
     */
    @Test
    public void testIsAllowViewChart() {
        System.out.println("isAllowViewChart");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isAllowViewChart();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setAllowViewChart method, of class AuthorizationService.
     */
    @Test
    public void testSetAllowViewChart() {
        System.out.println("setAllowViewChart");
        boolean allowViewChart = false;
        AuthorizationService instance = new AuthorizationService();
        instance.setAllowViewChart(allowViewChart);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getEditMode method, of class AuthorizationService.
     */
    @Test
    public void testGetEditMode() {
        System.out.println("getEditMode");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.getEditMode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of isEditMode method, of class AuthorizationService.
     */
    @Test
    public void testIsEditMode() {
        System.out.println("isEditMode");
        AuthorizationService instance = new AuthorizationService();
        boolean expResult = false;
        boolean result = instance.isEditMode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setEditMode method, of class AuthorizationService.
     */
    @Test
    public void testSetEditMode() {
        System.out.println("setEditMode");
        boolean editMode = false;
        AuthorizationService instance = new AuthorizationService();
        instance.setEditMode(editMode);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getOwnerUri method, of class AuthorizationService.
     */
    @Test
    public void testGetOwnerUri() {
        System.out.println("getOwnerUri");
        AuthorizationService instance = new AuthorizationService();
        String expResult = "";
        String result = instance.getOwnerUri();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of setOwnerUri method, of class AuthorizationService.
     */
    @Test
    public void testSetOwnerUri() {
        System.out.println("setOwnerUri");
        String ownerUri = "";
        AuthorizationService instance = new AuthorizationService();
        instance.setOwnerUri(ownerUri);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }
}
