/*
 * Project :iCardea
 * File : MapPhrBeanPropertiesFactoryTest.java
 * Date : Dec 15, 2010
 * User : mradules
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.DynaBean;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the <code>PhrBeanFactory</code> functionality together with the 
 * <code>PhrBeanMapPropertiesFactory</code>.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see PhrBeanMapPropertiesFactory
 * @see PhrBeanFactory
 */
public class PhrBeanMapPropertiesFactoryTest {

    /**
     * The factory used to build beans instances.
     */
    private static PhrBeanFactory factory;

    /**
     * Gets call before the test suite runs.
     */
    @BeforeClass
    public static void init() {
        factory = PhrBeanFactory.getInstance();
    }

    /**
     * Builds a dynamic bean using a map of that contains only one property and
     * proves if the bean contains this property.
     * 
     * @throws IllegalAccessException if the dynamic bean instance can not be created.
     * @throws InstantiationException  if the dynamic bean instance can not be created.
     * @see PhrBeanMapPropertiesFactory
     * @see PhrBeanFactory
     */
    @Test
    public void testSimpleMap() throws IllegalAccessException, InstantiationException {
        final String ns = "myNS";
        final String name = "myNewOne";
        final Map<String, Class<?>> types = buildTypesMap(name, Long.class);
        final PhrBeanMapPropertiesFactory beanPropertiesFactory =
                new PhrBeanMapPropertiesFactory(ns, types);
        
        // this bean contains only one property because is builded according with
        // the types map.
        final DynaBean bean = factory.buidBean(beanPropertiesFactory);
        assertNotNull(bean);

        final DynaProperty[] properties = bean.getDynaClass().getDynaProperties();

        // I expect only one property, the types map cotains only one property
        assertEquals(properties.length, 1);

        final String exprctName = PhrPropertyUtil.buildName(ns, name);
        assertEquals(exprctName, properties[0].getName());
        assertEquals(Long.class, properties[0].getType());
    }

    /**
     * Builds a dynamic bean using a map of that contains only one property and
     * an array that contains a set of other properties. The created bean must 
     * contain all properties (the one from the map and the one from the array).
     * 
     * @throws IllegalAccessException if the dynamic bean instance can not be created.
     * @throws InstantiationException  if the dynamic bean instance can not be created.
     */
    @Test
    public void testInheritMap() throws IllegalAccessException, InstantiationException {
        final String name = "myNewOne";
        final Map<String, Class<?>> types = buildTypesMap(name, Long.class);
        final String ns = "myNS";

        final PhrBeanPropertiesFactory defPropFactory =
                DefaultPhrBeanPropertiesFactory.getInstance();
        final DynaProperty[] defProperties = defPropFactory.getProperties();

        final PhrBeanMapPropertiesFactory propFactory =
                new PhrBeanMapPropertiesFactory(ns, types);
        propFactory.addProperties(defProperties);

        final DynaBean bean = factory.buidBean(propFactory);
        assertNotNull(bean);

        final DynaProperty[] dynaProperties =
                bean.getDynaClass().getDynaProperties();
        
        // the length is the size of the types map + the size of the default 
        // property array (builded with the PhrBeanPropertiesFactory)
        final int expectedLenght = defProperties.length + types.size();
        assertEquals(expectedLenght, dynaProperties.length);
        
        final DynaClass dynaClass = bean.getDynaClass();
        
        final DynaProperty[] properties = dynaClass.getDynaProperties();
        
        final List<DynaProperty> asList = Arrays.asList(properties);
        // proves if all the def properties are part of the generated bean
        for (DynaProperty dynaProperty : defProperties) {
            final boolean contains = asList.contains(dynaProperty);
            assertTrue(contains);
        }
    }

    private Map<String, Class<?>> buildTypesMap(String name, Class<?> clazz) {
        final Map<String, Class<?>> types = new HashMap<String, Class<?>>();
        types.put(name, clazz);
        return types;
    }
}
