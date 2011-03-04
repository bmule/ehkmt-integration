/*
 * Project :iCardea
 * File : PhrBeanConstants.java
 * Encoding : UTF-8
 * Date : Mar 4, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

/**
 * All the constants used in the data exchange related classes.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public final class PhrBeanConstants {

    public static final String NAME_TOKEN = "__";
    public static final String DISPLAY_NAME = "DisplayName";
    public static final String OWNER_ID = "OwnerId";
    public static final String CREATED_BY = "CreatedBy";
    public static final String CREATE_DATE = "CreateDate";
    public static final String MODIFY_DATE = "ModifyDate";
    public static final String TYPES = "Types";
    public static final String RESOURCE_URI = "ResourceURI";
    public static final String DELETED = "Deleted";
    public static final String CAN_READ = "CanRead";
    public static final String CAN_WRITE = "CanWrite";
    public static final String DEFAULT_NS = "phrs";

    /**
     * Don't let anybody to instantiate this class.
     */
    private PhrBeanConstants() {
        // UNIMPLEMENTED
    }
}
