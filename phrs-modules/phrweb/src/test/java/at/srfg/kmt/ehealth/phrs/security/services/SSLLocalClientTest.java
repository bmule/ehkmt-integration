/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import org.junit.*;
import static org.junit.Assert.*;

@Ignore
public class SSLLocalClientTest {
    
    public SSLLocalClientTest() {
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
     * Test of sslSetup method, of class SSLLocalClient.
     */
    @Test
    public void testSslSetup_String_String() {
        System.out.println("sslSetup");
        String certPath = "";
        String password = "";
        SSLLocalClient.sslSetup(certPath, password);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sslSetup method, of class SSLLocalClient.
     */
    @Test
    public void testSslSetup() {
        System.out.println("sslSetup");
        SSLLocalClient.sslSetup();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
