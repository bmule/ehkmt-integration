/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.util;



import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mradules
 */
public final class DateUtil {
    
    private static final DateFormat OUTPUT_FORMAT = new SimpleDateFormat("yyyyMMddHm");
    
    private static final List<DateFormat> formats;
    static {
        formats = new ArrayList<DateFormat>();
        formats.add(new SimpleDateFormat("yyyyMMddHms"));
        formats.add(OUTPUT_FORMAT);
        formats.add(new SimpleDateFormat("yyyyMMdd"));
        formats.add(new SimpleDateFormat("yyyyMM"));
        formats.add(new SimpleDateFormat("yyyy"));
        formats.add(new SimpleDateFormat("yyyy-MM-dd"));
    }
    
    
    /**
     * Don't let anyone to instantiate this class.
     */
    private DateUtil() {
        // UNIMPLEMENTED
    }
    
    public static Date getFormatedDate(String dateStr) {
        for (DateFormat format : formats) {
            try {
                return  format.parse(dateStr);
            } catch (ParseException ex) {
                // I don't care about this exception.
            }
        }
        
        final String msg = String.format("This date %s has an unsupported format", dateStr);
        throw new IllegalArgumentException(msg);
    }
    
    public static String getFormatedDate(Date date) {
        return OUTPUT_FORMAT.format(date);
    }
}
