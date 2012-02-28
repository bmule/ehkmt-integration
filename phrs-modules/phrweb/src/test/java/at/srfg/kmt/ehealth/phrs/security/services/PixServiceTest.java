/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.segment.PID;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;


public class PixServiceTest {
    
    public PixServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getPatientProtocolIdByCIED method, of class PixService.
     */
    @Test
    public void testGetPatientProtocolIdByCIED() {
        System.out.println("getPatientProtocolIdByCIED");
        
        PixService instance = new PixService();
        String expResult = "191";
        
        
        String pixQueryIdType = "cied:model:Maximo";
        String pixQueryIdUser = "PZC123456S";
        //String expResult = PixService.TEST_CIED;//"model:Maximo/serial:12345";
        String cied = PixService.makePixIdentifier(pixQueryIdType, pixQueryIdUser);
        String result = instance.getPatientProtocolIdByCIED(cied);
        assertEquals("Expect PID=191 for ",expResult, result);
      
    }
        @Test
    public void testPIXQueryGetPatientProtocolIdByCIEDConstant() {
        System.out.println("testPIXQueryGetPatientProtocolIdByCIEDConstant");
        
        PixService instance = new PixService();
        String expResult = "191";

        //String expResult = PixService.TEST_CIED;//"model:Maximo/serial:12345";
        String cied = PixService.TEST_CIED;
        String result = instance.getPatientProtocolIdByCIED(cied);
        assertEquals("Expect PID=191 for ",expResult, result);
      
    }
      /**
     * Test of makePixIdentifier method, of class PixService.
     *  > Demo Patient: Suzie Mayr
        > CIED ID: model:Maximo/serial:PZC123456S
        > iCARDEA ID: 191
     */
    
    @Test
    public void testMakePixIdentifier() {
        System.out.println("testMakePixIdentifier");
        //cied:model:Maximo
        String pixQueryIdType = "cied:model:Maximo";
        String pixQueryIdUser = "PZC123456S";
        String expResult = PixService.TEST_CIED;//"model:Maximo/serial:12345";
        String result = PixService.makePixIdentifier(pixQueryIdType, pixQueryIdUser);
        System.out.println("makePixIdentifier CIED="+result);
        assertEquals("Expect CIED = "+expResult,expResult, result);
        
    }
    /**
     * Test of getCurrentDate method, of class PixService.
     */
    @Test
    public void testGetCurrentDate() {
        
        String expResult = "";
        String result = PixService.getCurrentDate();
        //assertEquals(expResult, result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        System.out.println("testGetCurrentDate result "+result);
        
    }

  /**
     * Test of parsePid method, of class PixService.
     */
    @Test
    public void testParsePid() {
        System.out.println("testParsePid");
              String response = "MSH|^~ABC|OTHER_IBM_BRIDGE_TLS|IBM|PAT_IDENTITY_X_REF_MGR_MISYS|ALLSCRIPTS|20090224104204-0600||ADT^A08^ADT_A01|9241351356666182528|P|2.3.1||20090224104204-0600"
                + "PID|||102^^^IBOT&1.3.6.1.4.1.21367.2009.1.2.370&ISO||OTHER_IBM_BRIDGE^MARION||19661109|FPV1||O";
        
  
        String namespace = PixService.ICARDEA_PIX_CIED_FULL_NAMESPACE;
        String id = PixService.parsePid(response, "123", namespace);
        String expResult = "102";
        String result = PixService.parsePid(response, id, namespace);
        System.out.println("parsePid found id="+id);
        assertEquals("Expect ID 102",expResult, result);
        
    }
//    /**
//     * Test of queryProtocolIdById method, of class PixService.
//     */
//    @Test
//    public void testQueryProtocolIdById() throws Exception {
//        System.out.println("queryProtocolIdById");
//        String id = "";
//        String namespace = "";
//        PixService instance = new PixService();
//        String expResult = "";
//        String result = instance.queryProtocolIdById(id, namespace);
//        assertEquals(expResult, result);
//       
//    }
  
//    @Test
//    public void testSendMessage() {
//        System.out.println("sendMessage");
//        String msg = "";
//        PixService instance = new PixService();
//        String expResult = "";
//        String result = instance.sendMessage(msg);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAuditService method, of class PixService.
//     */
//    @Test
//    public void testGetAuditService() {
//        System.out.println("getAuditService");
//        PixService instance = new PixService();
//        AuditAtnaService expResult = null;
//        AuditAtnaService result = instance.getAuditService();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of initSSl method, of class PixService.
//     */
//    @Test
//    public void testInitSSl() {
//        System.out.println("initSSl");
//        PixService instance = new PixService();
//        instance.initSSl();
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of sendAuditMessage method, of class PixService.
//     */
//    @Test
//    public void testSendAuditMessage() {
//        System.out.println("sendAuditMessage");
//        String protocolId = "";
//        Map<String, String> attrs = null;
//        PixService instance = new PixService();
//        boolean expResult = false;
//        boolean result = instance.sendAuditMessage(protocolId, attrs);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of sendAndRecvPixMessage method, of class PixService.
//     */
//    @Test
//    public void testSendAndRecvPixMessage_Message() throws Exception {
//        System.out.println("sendAndRecvPixMessage");
//        Message message = null;
//        PixService instance = new PixService();
//        Message expResult = null;
//        Message result = instance.sendAndRecvPixMessage(message);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of sendAndRecvPixMessage method, of class PixService.
//     */
//    @Test
//    public void testSendAndRecvPixMessage_Message_boolean() throws Exception {
//        System.out.println("sendAndRecvPixMessage");
//        Message message = null;
//        boolean inXML = false;
//        PixService instance = new PixService();
//        Message expResult = null;
//        Message result = instance.sendAndRecvPixMessage(message, inXML);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPatientMessage method, of class PixService.
//     */
//    @Test
//    public void testGetPatientMessage() {
//        System.out.println("getPatientMessage");
//        String fam_name = "";
//        String giv_name = "";
//        String sex = "";
//        String dob = "";
//        PixService instance = new PixService();
//        PID expResult = null;
//        PID result = instance.getPatientMessage(fam_name, giv_name, sex, dob);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of pdq method, of class PixService.
//     */
//    @Test
//    public void testPdq() throws Exception {
//        System.out.println("pdq");
//        String fam_name = "";
//        String giv_name = "";
//        String sex = "";
//        String dob = "";
//        PixService instance = new PixService();
//        Message expResult = null;
//        Message result = instance.pdq(fam_name, giv_name, sex, dob);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isUseMessageDispatcher method, of class PixService.
//     */
//    @Test
//    public void testIsUseMessageDispatcher() {
//        System.out.println("isUseMessageDispatcher");
//        PixService instance = new PixService();
//        boolean expResult = false;
//        boolean result = instance.isUseMessageDispatcher();
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setUseMessageDispatcher method, of class PixService.
//     */
//    @Test
//    public void testSetUseMessageDispatcher() {
//        System.out.println("setUseMessageDispatcher");
//        boolean useMessageDispatcher = false;
//        PixService instance = new PixService();
//        instance.setUseMessageDispatcher(useMessageDispatcher);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updateProtocolIdFromUserProvidedCiedId method, of class PixService.
//     */
//    @Test
//    public void testUpdateProtocolIdFromUserProvidedCiedId() {
//        System.out.println("updateProtocolIdFromUserProvidedCiedId");
//        String ownerUri = "";
//        String pixQueryIdUser = "";
//        String pixQueryIdType = "";
//        PixService instance = new PixService();
//        String expResult = "";
//        String result = instance.updateProtocolIdFromUserProvidedCiedId(ownerUri, pixQueryIdUser, pixQueryIdType);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updateProtocolIdFromUserProvidedId method, of class PixService.
//     */
//    @Test
//    public void testUpdateProtocolIdFromUserProvidedId() {
//        System.out.println("updateProtocolIdFromUserProvidedId");
//        String ownerUri = "";
//        String pixQueryIdUser = "";
//        String pixQueryIdType = "";
//        PixService instance = new PixService();
//        String expResult = "";
//        String result = instance.updateProtocolIdFromUserProvidedId(ownerUri, pixQueryIdUser, pixQueryIdType);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updateIdentifierFromUser method, of class PixService.
//     */
//    @Test
//    public void testUpdateIdentifierFromUser_3args() {
//        System.out.println("updateIdentifierFromUser");
//        String ownerUri = "";
//        String pixQueryIdUser = "";
//        String pixQueryIdType = "";
//        PixService instance = new PixService();
//        String expResult = "";
//        String result = instance.updateIdentifierFromUser(ownerUri, pixQueryIdUser, pixQueryIdType);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updateIdentifierFromUser method, of class PixService.
//     */
//    @Test
//    public void testUpdateIdentifierFromUser_4args() {
//        System.out.println("updateIdentifierFromUser");
//        String ownerUri = "";
//        String pixQueryIdUser = "";
//        String pixQueryIdType = "";
//        boolean requeryPix = false;
//        PixService instance = new PixService();
//        String expResult = "";
//        String result = instance.updateIdentifierFromUser(ownerUri, pixQueryIdUser, pixQueryIdType, requeryPix);
//        assertEquals(expResult, result);
//        fail("The test case is a prototype.");
//    }


}
