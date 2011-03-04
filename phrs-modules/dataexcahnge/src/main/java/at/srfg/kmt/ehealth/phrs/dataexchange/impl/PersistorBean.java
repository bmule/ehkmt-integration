/*
 * Project :iCardea
 * File : PersistorBean.java.
 * Date : Mar 3, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import at.srfg.kmt.ehealth.phrs.dataexchange.api.Persistor;
import javax.ejb.Local;
import javax.ejb.Stateless;
import org.apache.commons.beanutils.DynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Stateless
@Local(Persistor.class)
public class PersistorBean implements Persistor {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.dataexchange.impl.PersistorBean</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PersistorBean.class);

    @Override
    public void persist(DynaBean bean) {
        LOGGER.debug("Try to persist {}.", DynaBeanUtil.toString(bean));
        
        LOGGER.debug("The {} was succesfull presisted.", DynaBeanUtil.toString(bean));
    }
}
