/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import at.srfg.kmt.ehealth.phrs.presentation.utils.TimeUtils;
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;
import java.util.Date;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;


public class ToolTests {

    public ToolTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    @Test
    public  void testConfigService(){
        //consent.service.endpoint
        ConfigurationService cs=ConfigurationService.getInstance();
        assertNotNull("testConfigService NULL", cs);
        String value= ConfigurationService.getInstance().getProperty("consent.service.endpoint");
        assertNotNull(value);
        System.out.println("value="+value);

    }
    @Test
    //@Ignore
    public void testDateFormat_1() {
        String dateStr = HealthyUtils.formatDate(new Date(), null,
                HealthyUtils.DATE_PATTERN_LABEL_YEAR_MONTH_DAY_TIME);

        System.out.println("testDateFormat_1 dateStr=" + dateStr);
        assertNotNull(dateStr);

    }

    @Test
    public void testDateFormat() {

        String dateStr = getDateFormat();
        System.out.println("testDateFormat dateStr=" + dateStr);
        assertNotNull(dateStr);
    }
    
    public String getDateFormat(){
                String pattern = "yyyy-MM-dd HH:mm";
        String dateStr = CoreTestData.formatTimeDate(new DateTime(), pattern);

       
        return dateStr;
    }
    @Test
    public void testTime(){
        long x=new Date().getTime();
        System.out.println("x="+x);
        Date y = new Date(1328713915937l);
        assertEquals("1. equal",0,TimeUtils.compare(new Date(), new Date()));

        assertEquals("2. 1",1,TimeUtils.compare(new Date(1428713915937l),new Date(1328713915937l)));

        assertEquals("3. -1 ", -1,TimeUtils.compare(new Date(1328713915937l),new Date(1428713915937l)));

        assertFalse("4. false",TimeUtils.isBeginLess(new Date(),new Date()));

        assertTrue("5. false",TimeUtils.isBeginLess(new Date(1428713915937l),new Date(1328713915937l)));

        assertTrue("6. true",TimeUtils.isBeginLess(new Date(1328713915937l),new Date(1428713915937l)));

    }
}
