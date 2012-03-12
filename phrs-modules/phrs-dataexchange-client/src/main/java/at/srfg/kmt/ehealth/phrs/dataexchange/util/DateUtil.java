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
 * Used to transform date to string and string to (java.util.)Date. 
 * The sting to date supports the following formats :
 * <ul>
 * <li> yyyyMMddHms
 * <li> yyyyMMddHm
 * <li> yyyyMMddH
 * <li> yyyyMMdd
 * <li> yyyyMM
 * <li> yyyy
 * <li> yyyyMMddHms
 * <li> yyyy-MM-dd
 * </ul>
 * This class is not designed to be extended.
 *
 * @author Mihai
 */
public final class DateUtil {

    /**
     * The formatter use th convert (java.util.)Date to String.
     */
    private static final DateFormat OUTPUT_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");//updated
    // private static final DateFormat OUTPUT_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");FIXXME
    /**
     * A list that contains all the supported (date) formats.
     */
    //From PCC09 QueryFactorUnitTest
    public final static DateFormat DATE_FORMAT_PCC09 =
            new SimpleDateFormat("yyyyMMddHHmmss");

    private static final List<DateFormat> FORMATS;

    static {
        FORMATS = new ArrayList<DateFormat>();
        FORMATS.add(OUTPUT_FORMAT);
        FORMATS.add(new SimpleDateFormat("yyyyMMddHms"));
        FORMATS.add(new SimpleDateFormat("yyyyMMdd"));
        FORMATS.add(new SimpleDateFormat("yyyyMM"));
        FORMATS.add(new SimpleDateFormat("yyyy"));
        FORMATS.add(new SimpleDateFormat("yyyy-MM-dd"));
        FORMATS.add(new SimpleDateFormat("yyyyMMddHHmmss"));
    }

    /**
     * Don't let anyone to instantiate this class.
     */
    private DateUtil() {
        // UNIMPLEMENTED
    }

    /**
     * Transforms a string in to a (java.util.)Date. The String must follow one
     * of the following formats:
     * <ul>
     * <li> yyyyMMddHms
     * <li> yyyyMMddHm
     * <li> yyyyMMddH
     * <li> yyyyMMdd
     * <li> yyyyMM
     * <li> yyyy
     * <li> yyyyMMddHms
     * <li> yyyy-MM-dd
     * </ul>
     * If the <code>dateStr</code> argument does not follow one of the 
     * previous listed formats then a <code>IllegalArgumentException</code> raises.
     * 
     * @param dateStr the date in string, it can not be null.
     * @return a date for a given string.
     * @throws NullPointerException if the <code>dateStr</code> argument is null.
     * @throws IllegalArgumentException if the <code>dateStr</code> argument does
     * not follows the upper listed formats.
     */
    public static Date getFormatedDate(String dateStr) {

        if (dateStr == null) {
            throw new NullPointerException("The dateStr argumetn can not be null.");
        }

        for (DateFormat format : FORMATS) {
            try {
                return format.parse(dateStr);
            } catch (ParseException ex) {
                // I don't care about this exception.
            }
        }

        final String msg = String.format("This date %s has an unsupported format", dateStr);
        throw new IllegalArgumentException(msg);
    }

    /**
     * Formats a (java.util.)Date in to a String with <i>yyyyMMddHm</i> format.
     * 
     * @param date the date to transfrom in String, it can not be null.
     * @return a String with <i>yyyyMMddHm</i> format for the given date.
     * @throws NullPointerException if the <code>dateStr</code> argument is null.
     */
    public static String getFormatedDate(Date date) {

        if (date == null) {
            throw new NullPointerException("The date argumetn can not be null.");
        }

        return OUTPUT_FORMAT.format(date);
    }

    /**
     * Builds a
     * <code>java.util.Date</code> instance located 31 days in the past and
     * returns it like String formated with the pattern : "yyyyMMddHHmmss".
     *
     * @return a
     * <code>java.util.Date</code> instance located 31 days in the past and
     * returns it like String formated with the pattern : "yyyyMMddHHmmss".
     */
    public static String getLastMonthDate() {
        final Date date = new Date();
        // one day has 86400 seconds
        final long oneMonth = 1000 * 86400 * 31;
        final long now = date.getTime();
        final long lastMonth = now + oneMonth;

        return DATE_FORMAT_PCC09.format(lastMonth);
    }

    /**
     * Builds a
     * <code>java.util.Date</code> that caries the current date and returns it
     * like String formated with the pattern : "yyyyMMddHHmmss".
     *
     * @return a
     * <code>java.util.Date</code> that caries the current date and returns it
     * like String formated with the pattern : "yyyyMMddHHmmss".
     */
    public static String getNowDate() {
        final Date date = new Date();
        return DATE_FORMAT_PCC09.format(date);
    }
}
