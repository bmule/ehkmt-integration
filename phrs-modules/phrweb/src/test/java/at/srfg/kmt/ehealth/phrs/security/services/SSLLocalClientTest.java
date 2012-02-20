/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import org.junit.*;
import static org.junit.Assert.*;


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

  
    @Test
    public void testSslSetupLocal() {
        System.out.println("testSslSetupLocal");

        try {
            SSLLocalClient.sslSetupLocal();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception"+e);
        }

    }


    @Test
    public void testSslSetupSetting_1() {
        System.out.println("sslSetup");
        int configSettings=1;
        try{
            SSLLocalClient.sslSetup(configSettings);
   
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception"+e);
        }
        
    }
    @Test
    public void testSslSetupSetting_2() {
        System.out.println("sslSetup");
        int configSettings=2;
        try{
            SSLLocalClient.sslSetup(configSettings);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception"+e);
        }
    }
}
