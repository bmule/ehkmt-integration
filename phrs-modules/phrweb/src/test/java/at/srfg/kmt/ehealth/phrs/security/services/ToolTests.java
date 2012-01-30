/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;
import java.util.Date;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
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
        System.out.println("testDateFormat dateStr=" + dateStr);
       
        return dateStr;
    }
}
