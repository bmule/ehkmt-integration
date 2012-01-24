/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.presentation.utils;

import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
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

    public static String getProperty(DynaBean bean, String property) {
        return DynaUtil.getStringProperty(bean, property, null);
    }

    public static String getStringProperty(DynaBean bean, String property, String defaultValue) {
        String value = null;
        try {
            Object obj = DynaUtil.getProperty(bean, property, defaultValue);
            if (obj != null && obj instanceof String) {
                value = (String) obj;
            }
        } catch (Exception e) {
            System.out.println("common error object does not have property=" + property);
        }

        if (value == null) {
            value = defaultValue;
        }

        return value;

    }

    public static Object getProperty(DynaBean bean, String property, String defaultValue) {
        Object value = null;
        if (bean != null && property != null && property.length() > 0) {
            try {
                Object obj = bean.get(property);
                if (obj != null) {
                    value = (String) obj;
                }
            } catch (Exception e) {
                System.out.println("common error object does not have property=" + property);
            }
        }

        if (value == null) {
            value = defaultValue;
        }

        return value;

    }
}
