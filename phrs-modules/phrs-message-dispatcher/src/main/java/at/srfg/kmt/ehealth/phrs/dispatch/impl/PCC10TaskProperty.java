/*
 * Project :iCardea
 * File : PCC10TaskProperty.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dispatch.impl;

/**
 * Defines all the properties that are required to generate a PCC10 response.
 * 
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public enum PCC10TaskProperty {
    
    /**
     * The URI where the PCC10 message will be send.
     */
    END_POINT_URI,

    /**
     * Hols the all patient names. This property may be a String.
     */
    PATIENT_NAME,
    
    /**
     * The unique patient id.  This property may be a String.
     */
    PATIENT_ID,
    
    /**
     * The Care Provision Code.
     */
    CARE_PROVISION_CODE;
}
