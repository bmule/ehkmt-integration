/*
 * Project :iCardea
 * File : Utils.java
 * Encoding : UTF-8
 * Date : Apr 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hl7.v3.*;


/**
 * Contain a set of common used (static) package scoped method(s).
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class Util {

    /**
     * Java Date pattern used to format data like : yyyyMMddHHmmss.
     * This is the date format used in the PCC10 messages.
     */
    public static final String FOR_PCC_MESSAGE_PATTERN = "yyyyMMddHHmmss";

    /**
     * Java Date pattern used to format data like : yyyy-MM-dd'T'HH:mm:ss'Z'.
     * This is the date format used in the PHRS storage.
     */
    private static final String FROM_DYNA_BEAN_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Used to format <code>java.util.Data</code> and <code>String</code> 
     * according with the yyyy-MM-dd'T'HH:mm:ss'Z' pattern.
     */
    private static final DateFormat DYNA_DATE_FORMAT = 
            new SimpleDateFormat(FROM_DYNA_BEAN_PATTERN);

    /**
     * Used to format <code>java.util.Data</code> and <code>String</code> 
     * according with the yyyyMMddHHmmss pattern.
     */
    private final static DateFormat PCC_DATE_FORMAT = 
            new SimpleDateFormat(FOR_PCC_MESSAGE_PATTERN);

    /**
     * JAX-B object factory used to build jax-b object 'hanged' in the
     * element(s) tree. This object factory is used to build HL7 objects
     * (name space "org.hl7.v3").
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    /**
     * Returns the JAX-B object factory used to build jax-b object 'hanged' in the
     * element(s) tree. This object factory is used to build HL7 objects
     * (name space "org.hl7.v3").
     * 
     * @return the JAX-B object factory used to build jax-b object 'hanged' in the
     * element(s) tree
     */
    static ObjectFactory getObjectFactory() {
        return OBJECT_FACTORY;
    }
    
    /**
     * Formats a <code>java.util.Date</code> in to a String according with the 
     * pattern : "yyyyMMddHHmmss".
     * This is the date format used in the PCC10 messages.
     * 
     * @param date the date to format.
     * @return a <code>java.util.Date</code> in to a String according with the 
     * pattern : "yyyyMMddHHmmss".
     */
    static String formatForPCCMessage(Date date) {
        
        if (date == null) {
            throw new NullPointerException("The date argument can not be null.");
        }
        
        final String result = PCC_DATE_FORMAT.format(date);
        return result;
    }
    
    static String formatFromDyne(Date date) {
        final String result = DYNA_DATE_FORMAT.format(date);
        return result;
    }
    
    
}
