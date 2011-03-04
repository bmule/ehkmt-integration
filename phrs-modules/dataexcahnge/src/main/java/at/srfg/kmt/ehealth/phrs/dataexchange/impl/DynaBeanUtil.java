/*
 * Project :iCardea
 * File : Expression file is undefined on line 6, column 13 in Templates/Classes/Class.java.
 * Date : Mar 3, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class DynaBeanUtil {

    
    private DynaBeanUtil() {
        // UNIMPLEMETNED
    }
    
    public static String toString(DynaBean bean) {
        final StringBuffer msg = new StringBuffer();
        final DynaClass dynaClass = bean.getDynaClass();
        msg.append(dynaClass.getName());
        msg.append(":{");
        final DynaProperty[] properties = dynaClass.getDynaProperties();
        for (DynaProperty property : properties) {
            final String name = property.getName();
            msg.append(name);
            msg.append(":");
            msg.append(bean.get(name));
            msg.append(" ,");
        }
        // remove the lasr " ,"
        msg.delete(msg.length() - 2, msg.length());
        msg.append("}");
        
        return msg.toString();
    }
}
