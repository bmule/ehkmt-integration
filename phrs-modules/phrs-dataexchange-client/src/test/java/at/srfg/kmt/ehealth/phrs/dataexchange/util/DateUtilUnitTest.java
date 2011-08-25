/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.util;

import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mradules
 */
public class DateUtilUnitTest {

    @Test
    public void testFromString_yyyyMMddHms() {
        final String dateStr = "201006010000";
        final Date formatedDate = DateUtil.getFormatedDate(dateStr);
        assertNotNull(formatedDate);
    }
    
    @Test
    public void testToString_yyyyMMddHms() {
        final String formatedDate = DateUtil.getFormatedDate(new Date());
        assertNotNull(formatedDate);
        final int length = formatedDate.length();
        // the lenght for the date is always 12
        assertEquals(12, length);
    }
    
    @Test
    public void testFromString_yyyyMMdd() {
        final String dateStr = "20100601";
        final Date formatedDate = DateUtil.getFormatedDate(dateStr);
        assertNotNull(formatedDate);
    }
    
    @Test
    public void testFromString_yyyyMM() {
        final String dateStr = "201006";
        final Date formatedDate = DateUtil.getFormatedDate(dateStr);
        assertNotNull(formatedDate);
    }
    
    @Test
    public void testFromString_yyyy() {
        final String dateStr = "2010";
        final Date formatedDate = DateUtil.getFormatedDate(dateStr);
        assertNotNull(formatedDate);
    }
    
}
