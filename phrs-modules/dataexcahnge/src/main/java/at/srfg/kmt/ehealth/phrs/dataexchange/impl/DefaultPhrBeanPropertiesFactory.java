/*
 * Project :iCardea
 * File : DefaultPhrBeanPropertiesFactory.java
 * Date : Dec 15, 2010
 * User : mradules
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static at.srfg.kmt.ehealth.phrs.dataexchange.impl.PhrBeanConstants.*;
import static at.srfg.kmt.ehealth.phrs.dataexchange.impl.PhrPropertyUtil.buildName;
import java.util.Date;
import java.util.Set;
import org.apache.commons.beanutils.DynaProperty;


/**
 * Used to build dynamic beans that contains the default set properties.
 * This properties are :
 * <ul>
 * <li> name : phrs__DisplayName, type : String
 * <li> name : phrs__OwnerId, type : String
 * <li> name : phrs__CreatedBy, type : String
 * <li> name : phrs__CreateDate, type : Date
 * <li> name : phrs__ModifyDate, type : Date
 * <li> name : phrs__Types, type : Set
 * <li> name : phrs__ResourceURI, type : String
 * <li> name : phrs__Deleted, type : Boolean
 * <li> name : _id, type : String
 * <li> name : _rev, type : String
 * </ul>
 * The _id and _rev are required by the persistence layer.
 * 
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public final class DefaultPhrBeanPropertiesFactory
        implements PhrBeanPropertiesFactory {
    
    /**
     * The default name space used. 
     * All the (dynamic) bean properties will be prefixed with this name space.
     */
    public final static String NS = "phrs";
    
    /**
     * The unique instance for this factory.
     */
    private final static PhrBeanPropertiesFactory THIS =
            new DefaultPhrBeanPropertiesFactory();
    
    /**
     * The default list of property.
     */
    private final static DynaProperty[] PROPERTIES = {
        
        new DynaProperty(buildName(NS, DISPLAY_NAME), String.class),
        new DynaProperty(buildName(NS, OWNER_ID), String.class),
        new DynaProperty(buildName(NS, CREATED_BY), String.class),
        new DynaProperty(buildName(NS, CREATE_DATE), Date.class),
        new DynaProperty(buildName(NS, MODIFY_DATE), Date.class),
        new DynaProperty(buildName(NS, TYPES), Set.class),
        new DynaProperty(buildName(NS, RESOURCE_URI), String.class),
        new DynaProperty(buildName(NS, DELETED), Boolean.class),
        new DynaProperty(buildName(NS, CAN_READ), Boolean.class),
        new DynaProperty(buildName(NS, CAN_WRITE), Boolean.class),

        new DynaProperty("_id", String.class),
        new DynaProperty("_rev", String.class),
    };

    /**
     * Don't let anybody to instantiate this class.
     */
    private DefaultPhrBeanPropertiesFactory() {
        // UNIMPLEMENTED
    }

    /**
     * The only one instance for this factory.
     * 
     * @return 
     */
    static PhrBeanPropertiesFactory getInstance() {
        return THIS;
    }

    /**
     * The properties used to build dynamic beans.
     * 
     * @return the properties used to build dynamic beans.
     */
    @Override
    public DynaProperty[] getProperties() {
        return PROPERTIES;
    }
}
