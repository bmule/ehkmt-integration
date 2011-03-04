/*
 * Project :iCardea
 * File : ActorException.java
 * Date : Dec 15, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import org.apache.commons.beanutils.DynaProperty;

/**
 * Define a ways to builds an arrays of properties. 
 * This properties can be use to define a dynamic bean.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface PhrBeanPropertiesFactory {
    
    /**
     * An arrays of properties.
     * 
     * @return an arrays of properties.
     */
    DynaProperty[] getProperties();
}
