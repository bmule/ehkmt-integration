package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;
import static org.junit.Assert.*;
import org.junit.*;

public class ConfigurationServiceTest {

    String ROLE_CODE_DOC = "ROLECODE:DOCTOR";
    String ROLE_CODE_NURSE = "ROLECODE:DOCTOR";
    String ACTION_ID_READ = "READ";

    public ConfigurationServiceTest() {
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
    public void testConfigProperties() {
        System.out.println("testConfigProperties");

        try {
            boolean result = false;
            //only to test the parsing of the property: test result,result
            result = ConfigurationService.isAppModeTest();
            assertEquals(result, result);

            result = ConfigurationService.isAppModeSingleUserTest();
            assertEquals(result, result);

            result = ConfigurationService.isAppModePixTest();
            assertEquals(result, result);

            result = ConfigurationService.isAppModeRoleTest();
            assertEquals(result, result);

            result = ConfigurationService.isAppModeMonitorListAllUsers();
            assertEquals(result, result);

            String testValue = ConfigurationService.getInstance().getProperty("application.testmode").trim();
            assertNotNull(testValue);
            testValue = ConfigurationService.getInstance().getProperty("user.mode.singleuser").trim();
            assertNotNull(testValue);
            testValue = ConfigurationService.getInstance().getProperty("pix.mode.test").trim();
            assertNotNull(testValue);
            testValue = ConfigurationService.getInstance().getProperty("consent.mode.roletest").trim();
            assertNotNull(testValue);
            testValue = ConfigurationService.getInstance().getProperty("consultation.reports.listall").trim();
            assertNotNull(testValue);
        } catch (Exception e) {
            fail("error parsing or phrs.properties property is not boolean");
        }

    }

    @Test
    public void testisHealthInfoAccessibleRole() {
        System.out.println("testisHealthInfoAccessibleRole");
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
        System.out.println("testIsConsentMgrAction");
        String actionCode = ACTION_ID_READ;
        boolean expResult = true;
        boolean result = ConsentMgrService.isConsentMgrAction(actionCode);
        assertEquals(expResult, result);

    }

    @Test
    public void testAllConsentMgrActions() {
        System.out.println("testAllConsentMgrActions");
        assertNotNull("Config instance is null", ConfigurationService.getInstance());
        List<String> list = ConfigurationService.getInstance().getConsentAllActions();
        assertNotNull("list null", list);
        assertTrue("expect 3 or more", list.size() > 2);

    }
    //getConsentSubjectCodes
}
