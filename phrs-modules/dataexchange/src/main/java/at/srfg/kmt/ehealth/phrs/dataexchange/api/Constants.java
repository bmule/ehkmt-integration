/*
 * Project :iCardea
 * File : Constants.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;

/**
 * Contains a set of unique purposed constants using on the data exchange module.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public final class Constants {

    /**
     * The SNOMED Controlled Terminology codeSystem value.
     */
    public static final String SNOMED = "2.16.840.1.113883.6.96";
    
    /**
     * The Logical Observation Identifier Names and Codes(LOINC)  codeSystem value.
     */
    public static final String LOINC = "2.16.840.1.113883.6.1";
    
    /**
     * RxNorm codeSystem value.
     */
    public static final String RXNORM = "2.16.840.1.113883.6.88";
    
    /**
     * RxNorm codeSystem value.
     */
    public static final String PHRS_CODESYSTEM = "2.16.840.1.113883.6.96.1.1";

    /**
     * Don't let anyone to instantiate this class.
     */
    private Constants() {
        // UNIMPLEMENTED
    }
}
