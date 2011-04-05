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
     * The phrsBean (instance) Uri. 
     * This is a default phrs bean property, all the phrs bean will have this 
     * property.
     * 
     */
    public static final String PHRS_BEAN_URI = "_phrsBeanUri";

    /**
     * The phrsBean class (type for the instance) Uri.
     * This is a default phrs bean property, all the phrs bean will have this 
     * property.
     * 
     */
    public static final String PHRS_BEAN_CLASS_URI = "_phrsBeanClassURI";

    /**
     * The phrsBean (instance) name. 
     * This is a default phrs bean property, all the phrs bean will have this 
     * property.
     * 
     */
    public static final String PHRS_BEAN_NAME = "_phrsBeanName";

    /**
     * The phrsBean (instance) version (a bean can have more than one version). 
     * This is a default phrs bean property, all the phrs bean will have this 
     * property.
     * 
     */
    public static final String PHRS_BEAN_VERSION = "_phrsBeanVersion";

    /**
     * The phrsBean (instance) create date.
     * This is a default phrs bean property, all the phrs bean will have this 
     * property.
     * 
     */
    public static final String PHRS_BEAN_CREATE_DATE = "_phrsBeanCreateDate";

    /**
     * The phrsBean (instance) owner. This is a default phrs bean properties, 
     * all the phrs bean will have this property.
     * 
     */
    public static final String PHRS_BEAN_OWNER = "_phrsBeanOwner";

    /**
     * The phrsBean (instance) creator. This is a default phrs bean properties,
     * all the phrs bean will have this property.
     * 
     */
    public static final String PHRS_BEAN_CREATOR = "_phrsBeanOwner";

    /**
     * The phrsBean (instance) can read flag. This is a default phrs bean 
     * properties, all the phrs bean will have this property.
     * 
     */
    public static final String PHRS_BEAN_CANREAD = "_phrsBeanCanRead";

    /**
     * The phrsBean (instance) can write flag. This is a default phrs bean 
     * properties, all the phrs bean will have this property.
     * 
     */
    public static final String PHRS_BEAN_CANWRITE = "_phrsBeanCanWrite";

    /**
     * The phrsBean (instance) can use flag. This is a default phrs bean
     * properties, all the phrs bean will have this property.
     * 
     */
    public static final String PHRS_BEAN_CANUSE = "_phrsBeanCanUse";

    /**
     * Don't let anyone to instantiate this class.
     */
    private Constants() {
        // UNIMPLEMENTED
    }
}
