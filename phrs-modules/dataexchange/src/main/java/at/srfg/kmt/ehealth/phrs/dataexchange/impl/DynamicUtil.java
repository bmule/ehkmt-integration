/*
 * Project :iCardea
 * File : DynamicUtil.java
 * Encoding : UTF-8
 * Date : Mar 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import static org.apache.commons.io.FileUtils.readFileToString;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicProperty;
import java.io.Serializable;
import org.apache.commons.beanutils.BasicDynaClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
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

    /**
     * Transforms a <code>DynamicClass</code> in to a <code>DynaClass</code>.
     * The  <code>DynamicClass</code> to transform must have a non-null and
     * non-empty name value otherwise an <code>IllegalArgumentException</code>
     * raises.
     * 
     * @param dynamicClass the <code>DynamicClass</code> instance to be
     * transformed, it can not be null.
     * @return a <code>DynaClass</code> for the given <code>DynamicClass</code>.
     * @throws NullPointerException if the <code>dynamicClass</code> argument is
     * null.
     * @throws IllegalArgumentException if the  <code>DynaClass</code> name
     * properties is null or empty.
     * @throws ClassNotFoundException by any kind of type matching.
     */
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

        final String uri = dynamicClass.getUri();
        if (uri == null || uri.isEmpty()) {
            String msg = String.format("The Dynamic Class to transform %s uri's attribute is null.", dynamicClass);
            throw new IllegalArgumentException(msg);
        }

        final BasicDynaClass result = new BasicDynaClass(uri, null, props);
        return result;
    }

    /**
     * Builds a <code>DynaProperty</code> based on a 
     * <code>DynamicPropertyType</code> instance.
     * 
     * @param type the involved <code>DynamicPropertyType</code>.
     * @return a <code>DynaProperty</code> based on a 
     * <code>DynamicPropertyType</code> instance.
     * @throws ClassNotFoundException if some classes (refered in the
     * DynaProperty) can not be located in to the classpath.
     */
    private static DynaProperty get(DynamicPropertyType type) throws ClassNotFoundException {
        final String name = type.getName();
        final String typeAsString = type.getType();
        final Class<?> classForName = Class.forName(typeAsString);
        final DynaProperty result = new DynaProperty(name, classForName);
        return result;
    }

    /**
     * Builds a <code>DynamicClass</code> based on a given bean class.</br>
     * The class must be for a bean-like class (get/set/is for for accessing the
     * properties). The new created <code>DynamicClass</code> will have also a 
     * specified uri.
     * 
     * @param clazz the class to be used like model.
     * @param className the name for the new created <code>DynamicClass</code>.
     * @param classUri the class  URI for the new created
     * <code>DynamicClass</code>, it can not be null.
     * @return a <code>DynamicClass</code> based on a given bean class.
     * @throws IntrospectionException 
     * @throws NullPointerException if the <code>clazz</code> or 
     * <code>classUri</code> arguments are null.
     * @throws IllegalArgumentException if the <code>clazz</code> contains no
     * access methods for its properties.
     */
    public static DynamicClass get(Class clazz, String className, String classUri) throws IntrospectionException {
        if (clazz == null) {
            throw new NullPointerException("The clazz argument can not be null.");
        }
        
        if (classUri == null || classUri.isEmpty()) {
            throw new NullPointerException("The classUri argument can not be null.");
        }
        final Method[] methods = clazz.getMethods();

        if (methods.length == 0) {
            throw new IllegalArgumentException("No methods found.");
        }

        final BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

        final DynamicClass dynamicClass = new DynamicClass(classUri, className);
        final Set<DynamicPropertyType> types =
                new HashSet<DynamicPropertyType>(methods.length);
        
        
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            final String name = propertyDescriptor.getName();
            if ("class".equals(name)) {
                continue;
            }
            Class<?> type = propertyDescriptor.getPropertyType();
            final boolean isPrimitive = type.isPrimitive();
            if (isPrimitive) {
                // care about the in /out boxing
                if ("boolean".equals(type.getName())) {
                    type = Boolean.class;
                } else if ("int".equals(type.getName())) {
                    type = Integer.class;
                } else if ("float".equals(type.getName())) {
                    type = Float.class;
                } else if ("double".equals(type.getName())) {
                    type = Double.class;
                } else if ("long".equals(type.getName())) {
                    type = Long.class;
                } else {
                    final String msg = 
                            String.format("The promitive type %s is not supported", type.getName());
                    throw new IllegalArgumentException(msg);
                }
            }
            final DynamicPropertyType dynamicPropertyType =
                    new DynamicPropertyType(name, type.getName(), dynamicClass);
            types.add(dynamicPropertyType);
        }

        if (types.isEmpty()) {
            throw new IllegalArgumentException("No accesors methods found methods found.");
        }
        dynamicClass.setPropertyTypes(types);

        return dynamicClass;
    }

    /**
     * Builds a <code>DynaBean</code> instance for a given 
     * <code>DynamicClass</code>. The bean instance can be used as it is.
     * 
     * @param dynamicClass the class for the bean instance, it can not be null.
     * @return a <code>DynaBean</code> instance for a given <code>DynamicClass</code>. 
     * @throws NullPointerException if the <code>dynamicClass</code> argument is
     * null.
     * @throws InstantiationException if some bean properties can not be
     * instantialised from any reasons.
     * @throws IllegalArgumentException if the  <code>DynaClass</code> name
     * properties is null or empty.
     * @throws ClassNotFoundException by any kind of type matching.
     */
    public static DynaBean getNewInstance(DynamicClass dynamicClass)
            throws IllegalAccessException, ClassNotFoundException, InstantiationException {

        if (dynamicClass == null) {
            throw new NullPointerException("The dynamicClass can not be null.");
        }

        final DynaClass dynaClass = get(dynamicClass);
        final DynaBean result = dynaClass.newInstance();
        return result;
    }

    /**
     * Builds a <code>DynaBean</code> for a given <code>DynamicBean</code>
     * instance.
     * 
     * @param dynamicBean the <code>DynamicBean</code> to be transformed, it can
     * not be null.
     * @return a <code>DynaBean</code> for a given <code>DynamicBean</code>
     * instance.
     * @throws NullPointerException if the <code>dynamicBean</code> argument is
     * null.
     * @throws InstantiationException if some bean properties can not be
     * instantialised from any reasons.
     * @throws IllegalArgumentException if the  <code>DynaClass</code> name
     * properties is null or empty.
     * @throws ClassNotFoundException by any kind of type matching.
     */
    public static DynaBean get(DynamicBean dynamicBean) throws
            IllegalAccessException, ClassNotFoundException, InstantiationException {

        if (dynamicBean == null) {
            throw new NullPointerException("The dynamicClass can not be null.");
        }

        final DynamicClass dynamicClass = dynamicBean.getDynamicClass();
        final DynaBean dynaBean = getNewInstance(dynamicClass);

        final Set<DynamicProperty> properties = dynamicBean.getProperties();
        for (DynamicProperty property : properties) {
            final String name = property.getName();
            final Serializable content = property.getContent();
            dynaBean.set(name, content);
        }

        return dynaBean;
    }
    
    public static Set<DynaBean> getDynaBeans(Set<DynamicBean> dynamicBeans) 
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        
        if (dynamicBeans == null) {
            throw new NullPointerException("The dynamicBeans argumetn can not e null.");
        }
        
        
        final Set<DynaBean> result = new HashSet<DynaBean>(dynamicBeans.size());
        if (result.isEmpty()) {
            return result;
        }
        
        for(DynamicBean dynamicBean : dynamicBeans) {
            final DynaBean dynaBean = get(dynamicBean);
            // mihai : I preffer to break the loop if any bean is inproper.
            result.add(dynaBean);
        }
        
        return result;
    }
    
    public static Set<DynaClass> getDynaClasses(Set<DynamicClass> dynamicClasses) 
            throws ClassNotFoundException {

        if (dynamicClasses == null) {
            throw new NullPointerException("The dynamicClasses argumetn can not e null.");
        }
        
        final Set<DynaClass> result = new HashSet<DynaClass>(dynamicClasses.size());
        if (result.isEmpty()) {
            return result;
        }
        
        for(DynamicClass dynamicClass : dynamicClasses) {
            final DynaClass dynaClass = get(dynamicClass);
            // mihai : I preffer to break the loop if any bean is inproper.
            result.add(dynaClass);
        }
        
        return result;
    }
    

    /**
     * Transforms a <code>DynaBean </code> in to a JSON string. </br>
     * The next snippet shows a json string for a bean with three properties
     * (one boolean, one string and one dateProperty) :
     * <pre>
     * {"booleanProperty":false,"stringProperty":"","dateProperty":null}
     * </pre>
     * 
     * @param bean the bean to transform, it can not be null.
     * @return a JSON representation for the given beam.
     */
    public static String toJSONString(DynaBean bean) {

        if (bean == null) {
            throw new NullPointerException("The bean argument can not be null.");
        }

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