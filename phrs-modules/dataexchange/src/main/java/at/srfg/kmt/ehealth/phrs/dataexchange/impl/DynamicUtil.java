/*
 * Project :iCardea
 * File : DynamicUtil.java
 * Encoding : UTF-8
 * Date : Mar 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static org.apache.commons.io.FileUtils.readFileToString;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

/**
 * Contains a common set of common used methods related with the 
 * DynaBeans 
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class DynamicUtil {

    public static DynaClass get(DynamicClass dynamicClass) throws ClassNotFoundException {

        if (dynamicClass == null) {
            throw new NullPointerException("The dynamicClass can not be null.");
        }

        final Set<DynamicPropertyType> propertyTypes = dynamicClass.getPropertyTypes();
        final DynaProperty[] props = new DynaProperty[propertyTypes.size()];
        int dynaPropertyIndex = 0;
        for (DynamicPropertyType propertyType : propertyTypes) {
            props[dynaPropertyIndex++] = get(propertyType);
        }

        return null;
    }

    private static DynaProperty get(DynamicPropertyType type) throws ClassNotFoundException {
        final String name = type.getName();
        final String typeAsString = type.getType();
        final Class<?> classForName = Class.forName(typeAsString);
        final DynaProperty result = new DynaProperty(name, classForName);
        return result;

    }

    public static DynaBean getNewInstance(DynamicClass dynamicClass)
            throws IllegalAccessException, ClassNotFoundException, InstantiationException {

        if (dynamicClass == null) {
            throw new NullPointerException("The dynamicClass can not be null.");
        }

        final DynaClass dynaClass = get(dynamicClass);
        return dynaClass.newInstance();
    }
    
    public static String toJSONString(DynaBean bean) {
        final JSONObject jsonObject = JSONObject.fromObject(bean);
        return jsonObject.toString();
    }

    public static void toJSONFile(DynaBean bean, File file) throws IOException {
        final JSONObject jsonObject = JSONObject.fromObject(bean);
        final FileWriter writer = new FileWriter(file);
        final String toWrite = jsonObject.toString();
        writer.write(toWrite);
    }

    public static void toJSONFile(DynaBean bean, String file) throws IOException {
        toJSONFile(bean, new File(file));
    }

    public static DynaBean fromJSONString(String jsonString) {
        final JSONObject jsonObject = JSONObject.fromObject(jsonString);
        final DynaBean result = (DynaBean) JSONObject.toBean(jsonObject);
        return result;
    }

    public static DynaBean fromJSONFile(DynaBean bean, File file)
            throws IOException {
        final String toString = readFileToString(file);
        final DynaBean result = fromJSONString(toString);
        return result;
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
        // remove the last " ,"
        msg.delete(msg.length() - 2, msg.length());
        msg.append("}");

        return msg.toString();
    }
}
