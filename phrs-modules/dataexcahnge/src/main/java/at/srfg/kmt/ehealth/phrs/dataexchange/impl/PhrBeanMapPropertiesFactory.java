/*
 * Project :iCardea
 * File : ActorException.java
 * Date : Dec 15, 2010
 * User : mradules
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.DynaProperty;

/**
 * Used to build properties for dynamic bean according with a given Map.
 * The map contains like key the property name and the corresponding value
 * represents the type.<br>
 * The namespace can be used to group together properties, the name space
 * is reflected in the name of the property, e.g. if the namespace is "myNS" 
 * and the property name is "MyName" then the full name is "myNS__MyName"
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class PhrBeanMapPropertiesFactory implements PhrBeanPropertiesFactory {

    /**
     * All the dynamic properties.
     */
    private final List<DynaProperty> properties;

    /**
     * Builds a <code>PhrBeanMapPropertiesFactory</code> instance.
     */
    public PhrBeanMapPropertiesFactory() {
        properties = new ArrayList<DynaProperty>();
    }

    /**
     * Builds a <code>PhrBeanMapPropertiesFactory</code> instance for a given
     * namespace and properties map. The map contains like key the property 
     * name and the corresponding value represents the type.
     * All the properties are placed in the given namespace.
     * 
     * @param namespace the name spaces for all the properties specified with
     * the map.
     * @param map the properties map.
     */
    public PhrBeanMapPropertiesFactory(String namespace, Map<String, Class<?>> map) {
        properties = new ArrayList<DynaProperty>();
        addProperties(namespace, map);
    }

    /**
     * Adds an array of properties to the already existing properties.
     * 
     * @param properties the properties to add.
     */
    public final void addProperties(DynaProperty[] properties) {
        final List<DynaProperty> asList = Arrays.asList(properties);
        this.properties.addAll(asList);
    }

    /**
     * Adds a map of properties to the already existing properties.
     * The map contains like key the property name and the corresponding value
     * represents the type.
     * All the properties are added to the given name space.
     * 
     * @param namespace the name space.
     * @param map the properties map, it can not be null.
     * @throws NullPointerException if the <code>map</code> is null.
     */
    public final void addProperties(String namespace, Map<String, Class<?>> map) {
        if (map == null) {
            final NullPointerException nullException =
                    new NullPointerException("The map argument can not be null");
            throw nullException;
        }

        if (map.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Class<?>> entry : map.entrySet()) {
            final String key = entry.getKey();
            final Class<?> type = entry.getValue();
            final String propertyName =
                    PhrPropertyUtil.buildName(namespace, key);
            final DynaProperty dynaProperty = new DynaProperty(propertyName, type);
            properties.add(dynaProperty);
        }
    }

    @Override
    public DynaProperty[] getProperties() {
        final DynaProperty[] result = new DynaProperty[properties.size()];
        int index = 0;
        for (DynaProperty property : properties) {
            result[index++] = property;
        }

        return result;
    }
}
