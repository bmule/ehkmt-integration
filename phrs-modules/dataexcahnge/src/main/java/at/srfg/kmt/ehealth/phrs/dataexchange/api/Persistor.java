/*
 * Project :iCardea
 * File : Persistor.java
 * Date : Mar 3, 2011
 * User : Mihai Radulescu
 */

package at.srfg.kmt.ehealth.phrs.dataexchange.api;

import org.apache.commons.beanutils.DynaBean;

/**
 * Used to persists a given bean.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface Persistor {

    /**
     * Persists a given bean.
     * 
     * @param bean the bean to persist.
     */
    void persist(DynaBean bean) throws PersistorException;
}
