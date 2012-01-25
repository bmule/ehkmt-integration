/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * Test of the AuditAtna The
 * <code>icardea.properties</code> file provides information about the ATNA
 * host, secure or not. The test depends on the icardea.properties settings.
 * These can be in the java source or in the test resource area and might not
 * reflect the actual hospital conditions when testing... please change this
 * file where appropriate for the environment.
 */
public class AuditAtnaServiceTest {

    public AuditAtnaServiceTest() {
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
    public void testSetupSSL() {
        System.out.println("setupSSL");
        AuditAtnaService instance = null;
        boolean flag = false;
        try {
            instance = new AuditAtnaService();
            instance.setupSSL();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("setupSSL has exception", flag);
        assertTrue("setupSSL is not configured for SSL, see icardea.properties", instance.isSecure());
    }

    @Test
    public void testGetHost() {
        System.out.println("getHost");
        AuditAtnaService instance = new AuditAtnaService();
        String expResult = "";
        String result = instance.getHost();
        assertTrue("ATNA host name malformed ", result != null && result.length() > 5);

    }

    @Test
    public void testGetPort() {
        System.out.println("getPort");
        AuditAtnaService instance = new AuditAtnaService();
        int expResult = -1;
        int result = instance.getPort();
        assertTrue("ATNA port is not assigned", result > expResult);

    }

    @Test
    public void testIsAtnalogRequired() {
        System.out.println("isAtnalogRequired");
        AuditAtnaService instance = new AuditAtnaService();
        boolean expResult = true;
        boolean result = instance.isAtnalogRequired();
        assertEquals("ATNA loggin is not enabled, see icardea.properties", expResult, result);
    }

    @Test
    public void testSendAuditMessageGrantForRole() {
        //cant test threaded testSendAuditMessageGrantForRole ?
        System.out.println("sendAuditMessageGrantForRole");
        String patientId = "";
        String resource = "";
        String requestorRole = "";
        AuditAtnaService instance = new AuditAtnaService();
        //how to test, a new thread is created....
        boolean success = instance.doAuditMessageGrantForRole(patientId, resource, requestorRole);
        assertTrue("ATNA not successful doAuditMessageGrantForRole", success);
    }

    @Test
    public void testSendAuditMessageForPatientRegistration() {
        //cant test threaded testSendAuditMessageGrantForRole ?
        System.out.println("sendAuditMessageGrantForRole");
        String patientId = "";

        AuditAtnaService instance = new AuditAtnaService();
        //how to test, a new thread is created....
        boolean success = instance.doAuditMessageForPatientRegistration(patientId);
        assertTrue("ATNA not successful doAuditMessageForPatientRegistration", success);

    }
}
