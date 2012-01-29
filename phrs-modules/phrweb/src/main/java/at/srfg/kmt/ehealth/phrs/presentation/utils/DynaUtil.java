/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.presentation.utils;

import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;

public class DynaUtil {

    public static String toString(DynaBean bean) {
        if (bean != null) {
            return DynaBeanUtil.toString(bean);
        }
        return null;

    }

    public static String getStringProperty(DynaBean bean, String property) {
        return DynaUtil.getStringProperty(bean, property, null);
    }

    public static Object getProperty(DynaBean bean, String property) {
        return DynaUtil.getProperty(bean, property, null);
    }

    /**
     * Get property value as String, if it is not a String, then it will return
     * a null
     *
     * @param bean
     * @param property
     * @param defaultValue
     * @return
     */
    public static String getStringProperty(DynaBean bean, String property, String defaultValue) {
        String value = null;
        try {
            Object obj = DynaUtil.getProperty(bean, property, defaultValue);
            if (obj != null && obj instanceof String) {
                value = (String) obj;
            }
        } catch (Exception e) {
            System.out.println("No dyna String (check Object property) property for: " + property);
        }

        if (value == null) {
            value = defaultValue;
        }

        return value;

    }

    /**
     *
     * @param bean
     * @param property
     * @param defaultValue
     * @return
     */
    public static Object getProperty(DynaBean bean, String property, String defaultValue) {
        Object value = null;
        if (bean != null && property != null && property.length() > 0) {
            try {
                value = bean.get(property);
            } catch (Exception e) {
                System.out.println("No dyna Object property for: " + property);
            }
        }

        if (value == null) {
            value = defaultValue;
        }

        return value;

    }

    /**
     * Get as DynaBean, other if it is not a DynaBean, return null
     *
     * @param bean
     * @param property
     * @return
     */
    public static DynaBean getDynaBeanProperty(DynaBean bean, String property) {
        DynaBean value = null;
        if (bean != null && property != null && property.length() > 0) {
            try {
                Object obj = bean.get(property);
                //if (obj != null && (obj instanceof BasicDynaBean) ) {
                //    System.out.println("BasicDynaBean ");
                //}
                if (obj != null && !(obj instanceof String) ) {
                    value = (DynaBean) obj;
                }
            } catch (Exception e) {
                System.out.println("No dyna Object property for: " + property);
            }
        }

        return value;

    }

    /**
     * To get the value URI Example: DynaBean has "status" property which refers
     * to a resource with a URI Often only for instance data
     *
     * @param bean
     * @param property
     * @param defaultValue
     * @return
     */
    public static String getValueResourceUri(DynaBean bean, String property, String defaultValue) {
        String value = null;
        if (bean != null && property != null && property.length() > 0) {
            try {
                Object obj = bean.get(property);
                if (obj != null && obj instanceof DynaBean) {
                    DynaBean db = (DynaBean) obj;
                    //resourceURI
                    value = db.getDynaClass().getName();
                }
            } catch (Exception e) {
                System.out.println("No dyna Object property for: " + property);
            }
        }

        if (value == null) {
            value = defaultValue;
        }

        return value;

    }

    public static String getValueResourceUri(DynaBean bean, String property) {
        return getValueResourceUri(bean, property, null);
    }
}
