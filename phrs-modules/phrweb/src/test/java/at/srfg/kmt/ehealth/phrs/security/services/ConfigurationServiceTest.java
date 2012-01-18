/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import static org.junit.Assert.*;
import org.junit.*;

import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import java.util.List;
import org.apache.commons.configuration.PropertiesConfiguration;

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
    public void testisHealthInfoAccessibleByThisRole() {
        System.out.println("testisHealthInfoAccessibleByThisRole");
        String subjectCode = ROLE_CODE_DOC;
        boolean expResult = true;
        boolean result = ConfigurationService.getInstance().isHealthInfoAccessibleByThisRole(subjectCode);
        assertEquals(expResult, result);

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
