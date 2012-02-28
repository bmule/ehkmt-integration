/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;


import org.junit.*;
import static org.junit.Assert.*;


public class PixService0Test {
    
    public PixService0Test() {
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
    public void testParsePixResultId(){
        String response="MSH|^~ABC|OTHER_IBM_BRIDGE_TLS|IBM|PAT_IDENTITY_X_REF_MGR_MISYS|ALLSCRIPTS|20090224104204-0600||ADT^A08^ADT_A01|9241351356666182528|P|2.3.1||20090224104204-0600"
                +"PID|||102^^^IBOT&1.3.6.1.4.1.21367.2009.1.2.370&ISO||OTHER_IBM_BRIDGE^MARION||19661109|FPV1||O";
        String pid=PixService.parsePid( response,"102",PixService.ICARDEA_PIX_CIED_FULL_NAMESPACE);
        
        assertEquals(" Expect pid = 102","102",pid);
        
              String response2="MSH|^~ABC|OTHER_IBM_BRIDGE_TLS|IBM|PAT_IDENTITY_X_REF_MGR_MISYS|ALLSCRIPTS|20090224104204-0600||ADT^A08^ADT_A01|9241351356666182528|P|2.3.1||20090224104204-0600"
                +"PID|xx |yy|102^^^IBOT&1.3.6.1.4.1.21367.2009.1.2.370&ISO||OTHER_IBM_BRIDGE^MARION||19661109|FPV1||O";
        String pid2=PixService.parsePid( response2,"102",PixService.ICARDEA_PIX_CIED_FULL_NAMESPACE);
        
        assertEquals(" Expect pid = 102","102",pid2);
    }
    /**
     * Test of getAuditService method, of class PixService.

    @Test
    public void testInitSSl() {
        System.out.println("initSSl");
        PixService instance = new PixService();
        instance.initSSl();
        // TODO review the generated test code and remove the default call to fail.

    }


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

    }










     */

}
