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
        System.out.println("getPatientProtocolIdByCIED + makePixIdentifier. result=" + result + " on cied=" + cied);
        assertEquals("Expect PID=191 for ", expResult, result);

    }

    @Test
    public void testPIXQueryGetPatientProtocolIdByCIEDConstant() {
        System.out.println("testPIXQueryGetPatientProtocolIdByCIEDConstant");

        PixService instance = new PixService();
        String expResult = "191";

        //String expResult = PixService.TEST_CIED;//"model:Maximo/serial:12345";
        String cied = PixService.TEST_CIED;
        String result = instance.getPatientProtocolIdByCIED(cied);
        System.out.println("testPIXQueryGetPatientProtocolIdByCIEDConstant + PixService.TEST_CIED. result=" + result + " on cied=" + cied);
        assertEquals("Expect PID=191 for ", expResult, result);

    }

    /**
     * Test of makePixIdentifier method, of class PixService.
     * > Demo Patient: Suzie Mayr
     * > CIED ID: model:Maximo/serial:PZC123456S
     * > iCARDEA ID: 191
     */

    @Test
    public void testMakePixIdentifier() {
        System.out.println("testMakePixIdentifier");
        //cied:model:Maximo
        String pixQueryIdType = "cied:model:Maximo";
        String pixQueryIdUser = "PZC123456S";
        String expResult = PixService.TEST_CIED;//"model:Maximo/serial:12345";
        String result = PixService.makePixIdentifier(pixQueryIdType, pixQueryIdUser);
        System.out.println("makePixIdentifier CIED=" + result);
        assertEquals("Expect CIED = " + expResult, expResult, result);

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
        System.out.println("testGetCurrentDate result " + result);

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
        System.out.println("testParsePid id=" + id);
        assertEquals("Expect ID 102", expResult, result);

    }

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
