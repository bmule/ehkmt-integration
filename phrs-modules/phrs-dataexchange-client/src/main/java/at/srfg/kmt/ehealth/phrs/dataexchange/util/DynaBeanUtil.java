/*
 * Project :iCardea
 * File : DynaBeanUtil.java
 * Encoding : UTF-8
 * Date : Aug 30, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.util;


import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;


/**
 *
 * @author Mihai
 */
public final class DynaBeanUtil {
    
    /**
     * Don't let anybody to instantiate this class.
     */
    private DynaBeanUtil() {
        // UNIMPLEMENTD
    }

    /**                                                                                                                                                                         
     * Returns a String representation for a given <code>DynaBean</code>.                                                                                                       
     *                                                                                                                                                                          
     * @param bean the involved bean.                                                                                                                                           
     * @return a String representation for a given <code>DynaBean</code>.                                                                                                       
     */
    public static String toString(DynaBean bean) {

        if (bean == null) {
            throw new NullPointerException("The bean argument can not be null.");
        }

        final StringBuffer msg = new StringBuffer();
        final DynaClass dynaClass = bean.getDynaClass();
        msg.append(dynaClass.getName());
        msg.append(":{");
        final DynaProperty[] properties = dynaClass.getDynaProperties();
        for (DynaProperty property : properties) {
            final String name = property.getName();
            msg.append("\r\n");
            msg.append(name);
            msg.append(":");
            msg.append(bean.get(name));
            msg.append(" ,");
        }
        // remove the last " ,"                                                                                                                                                 
        msg.delete(msg.length() - 2, msg.length());

        return msg.toString();
    }
}
