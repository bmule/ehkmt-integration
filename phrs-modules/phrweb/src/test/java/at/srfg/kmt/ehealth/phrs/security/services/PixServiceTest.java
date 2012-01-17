/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.model.baseform.PixIdentifier;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.segment.PID;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;

@Ignore
public class PixServiceTest {
    
    public PixServiceTest() {
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
     * Test of getAuditService method, of class PixService.
     */
    @Test
    public void testGetAuditService() {
        System.out.println("getAuditService");
        PixService instance = new PixService();
        AuditAtnaService expResult = null;
        AuditAtnaService result = instance.getAuditService();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of initSSl method, of class PixService.
     */
    @Test
    public void testInitSSl() {
        System.out.println("initSSl");
        PixService instance = new PixService();
        instance.initSSl();
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of sendAuditMessage method, of class PixService.
     */
    @Test
    public void testSendAuditMessage() {
        System.out.println("sendAuditMessage");
        String protocolId = "";
        Map<String, String> attrs = null;
        PixService instance = new PixService();
        boolean expResult = false;
        boolean result = instance.sendAuditMessage(protocolId, attrs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of sendAndRecvPixMessage method, of class PixService.
     */
    @Test
    public void testSendAndRecvPixMessage_Message() throws Exception {
        System.out.println("sendAndRecvPixMessage");
        Message message = null;
        PixService instance = new PixService();
        Message expResult = null;
        Message result = instance.sendAndRecvPixMessage(message);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of sendAndRecvPixMessage method, of class PixService.
     */
    @Test
    public void testSendAndRecvPixMessage_Message_boolean() throws Exception {
        System.out.println("sendAndRecvPixMessage");
        Message message = null;
        boolean reponseInXML = false;
        PixService instance = new PixService();
        Message expResult = null;
        Message result = instance.sendAndRecvPixMessage(message, reponseInXML);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of sendAndRecvPixMessage method, of class PixService.
     */
    @Test
    public void testSendAndRecvPixMessage_4args() throws Exception {
        System.out.println("sendAndRecvPixMessage");
        Message message = null;
        boolean inXML = false;
        String host = "";
        int port = 0;
        PixService instance = new PixService();
        Message expResult = null;
        Message result = instance.sendAndRecvPixMessage(message, inXML, host, port);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of validatePatientProtocolId method, of class PixService.
     */
    @Test
    public void testValidatePatientProtocolId_3args() {
        System.out.println("validatePatientProtocolId");
        String phrId = "";
        PixIdentifier pixIdentifier = null;
        Map<String, String> resultsMap = null;
        PixService instance = new PixService();
        String expResult = "";
        String result = instance.validatePatientProtocolId(phrId, pixIdentifier, resultsMap);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of validatePatientProtocolId method, of class PixService.
     */
    @Test
    public void testValidatePatientProtocolId_4args() {
        System.out.println("validatePatientProtocolId");
        String phrId = "";
        PixIdentifier pixIdentifier = null;
        Map<String, String> resultsMap = null;
        boolean useProtocolId = false;
        PixService instance = new PixService();
        String expResult = "";
        String result = instance.validatePatientProtocolId(phrId, pixIdentifier, resultsMap, useProtocolId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of validProtocolId method, of class PixService.
     */
    @Test
    public void testValidProtocolId() {
        System.out.println("validProtocolId");
        String protocolId = "";
        Map<String, String> pidResults = null;
        PixService instance = new PixService();
        boolean expResult = false;
        boolean result = instance.validProtocolId(protocolId, pidResults);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getPatientProtocolIdByPhrId method, of class PixService.
     */
    @Test
    public void testGetPatientProtocolIdByPhrId() {
        System.out.println("getPatientProtocolIdByPhrId");
        String phrId = "";
        Map<String, String> pidResult = null;
        PixService instance = new PixService();
        String expResult = "";
        String result = instance.getPatientProtocolIdByPhrId(phrId, pidResult);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getPatientByPhrId method, of class PixService.
     */
    @Test
    public void testGetPatientByPhrId() {
        System.out.println("getPatientByPhrId");
        String phrId = "";
        PixService instance = new PixService();
        Map expResult = null;
        Map result = instance.getPatientByPhrId(phrId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getPatientByProtocolId method, of class PixService.
     */
    @Test
    public void testGetPatientByProtocolId() throws Exception {
        System.out.println("getPatientByProtocolId");
        String protocolId = "";
        PixService instance = new PixService();
        Map expResult = null;
        Map result = instance.getPatientByProtocolId(protocolId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getPatientPIDById method, of class PixService.
     */
    @Test
    public void testGetPatientPIDById() throws Exception {
        System.out.println("getPatientPIDById");
        String identifierValue = "";
        String namespace = "";
        PixService instance = new PixService();
        Map expResult = null;
        Map result = instance.getPatientPIDById(identifierValue, namespace);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of getPatientMessage method, of class PixService.
     */
    @Test
    public void testGetPatientMessage() {
        System.out.println("getPatientMessage");
        String fam_name = "";
        String giv_name = "";
        String sex = "";
        String dob = "";
        PixService instance = new PixService();
        PID expResult = null;
        PID result = instance.getPatientMessage(fam_name, giv_name, sex, dob);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }

    /**
     * Test of pdq method, of class PixService.
     */
    @Test
    public void testPdq() throws Exception {
        System.out.println("pdq");
        String fam_name = "";
        String giv_name = "";
        String sex = "";
        String dob = "";
        PixService instance = new PixService();
        Message expResult = null;
        Message result = instance.pdq(fam_name, giv_name, sex, dob);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("Test fails.");
    }
}
