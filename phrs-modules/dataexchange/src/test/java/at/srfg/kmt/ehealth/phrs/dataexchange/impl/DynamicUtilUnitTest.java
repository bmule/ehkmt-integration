/*
 * Project :iCardea
 * File : DymanicClassRepositoryBeanUnitTest.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.beanutils.DynaBean;
import static org.junit.Assert.*;
import static at.srfg.kmt.ehealth.phrs.dataexchange.impl.DummyModelFactory.*;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelFactory;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * Provides test for the <code>DynamicUtil</code> utility class methods.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see DynamicUtil
 */
public class DynamicUtilUnitTest {

    /**
     * Builds a <code>DynamicUtilUnitTest</code> instance.
     */
    public DynamicUtilUnitTest() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a <code>DynamicClass</code> based on a given map, transforms it
     * in a <code>DynaClass</code> using the <code>DynamicUtil</code> and prove
     * if the new builded class is has the proper properties. 
     * 
     * @see DynamicUtil#get(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     * @throws IllegalAccessException if it the DynaBeans to test can not be 
     * access from any reason. If this exception occurs the test fails.
     * @throws InstantiationException if it the DynaBeans to test can not be 
     * instantiated from any reason.  If this exception occurs the test fails.
     * @throws ClassNotFoundException by any kind of type matching. 
     * If this exception occurs the test fails.
     */
    @Test
    public void testGetDynaClassFromDymanicClass() throws ClassNotFoundException {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> dummyModelMap =
                DummyModelFactory.createDefaultModelMap();

        // here I build an class instance.
        final String name = DummyModelFactory.createUniqueString("myName");
        final String classURI = DummyModelFactory.createUniqueString("myURI");
        final DynamicClass dynamicClass =
                ModelFactory.buildDynamicClass(name, classURI, dummyModelMap);
        assertNotNull(dynamicClass);

        final DynaClass dynaClass = DynamicUtil.get(dynamicClass);
        assertNotNull(dynaClass);

        // the DynaClass must ave the same name wint the DynamicClass
        final String dynaClassName = dynaClass.getName();
        assertEquals(classURI, dynaClassName);
        
        proveDefaultProperties(dynaClass);
    }

    /**
     * Builds a new <code>DynaBean</code> instance based on the <code>DynaClass</code> 
     * and prove it the new created instance has the proper properties.
     * 
     * @see DynamicUtil#getNewInstance(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     * @throws IllegalAccessException if it the DynaBeans to test can not be 
     * access from any reason. If this exception occurs the test fails.
     * @throws InstantiationException if it the DynaBeans to test can not be 
     * instantiated from any reason.  If this exception occurs the test fails.
     * @throws ClassNotFoundException by any kind of type matching. 
     * If this exception occurs the test fails.
     */
    @Test
    public void testGetNewInstance() throws IllegalAccessException,
            ClassNotFoundException, InstantiationException {

        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> dummyModelMap =
                DummyModelFactory.createDefaultModelMap();

        // here I build an class instance.
        final String name = DummyModelFactory.createUniqueString("myName");
        final String classURI = DummyModelFactory.createUniqueString("myURI");
        final DynamicClass dynamicClass =
                ModelFactory.buildDynamicClass(name, classURI, dummyModelMap);

        // obtains a DynaBean instance for the DynamicClass
        final DynaBean newBean = DynamicUtil.getNewInstance(dynamicClass);
        assertNotNull(newBean);

        final DynaClass dynaClass = newBean.getDynaClass();
        final String dynaClassName = dynaClass.getName();
        assertEquals(classURI, dynaClassName);

        //proves the propeties
        proveDefaultProperties(dynaClass);

        // I use the bean here (set the values)
        final String stringPropertyValue = "XXXXXX";
        newBean.set("stringProperty", stringPropertyValue);

        final Date datePropertyValue = DummyModelFactory.DATE;
        newBean.set("dateProperty", datePropertyValue);

        final Boolean booleanPropertyValue = Boolean.TRUE;
        newBean.set("booleanProperty", booleanPropertyValue);

        // retreive the values from the bean instance.
        final Object newStringPropertyValue = newBean.get("stringProperty");
        assertEquals(stringPropertyValue, newStringPropertyValue);

        final Object newDatePropertyValue = newBean.get("dateProperty");
        assertEquals(datePropertyValue, newDatePropertyValue);

        final Object newBooleanPropertyValue = newBean.get("booleanProperty");
        assertEquals(booleanPropertyValue, newBooleanPropertyValue);
    }

    private void proveDefaultProperties(DynaClass dynaClass) {

        // the default model has only three properties.
        // see the DummyModelFactory.createDefaultModelMap for more informarions.
        final DynaProperty[] dynaProperties = dynaClass.getDynaProperties();
        assertEquals(3, dynaProperties.length);

        // here I iterate over all properties and prove if they are ok.
        for (DynaProperty property : dynaProperties) {
            final String propType = property.getType().getName();
            final String propertyNameForType = getPropertyNameForType(propType);
            assertNotNull(propertyNameForType);
            final String propName = property.getName();
            assertEquals(propertyNameForType, propName);
        }
    }

    /**
     * Builds a DynaBean based on a <code>DynamicClass</code> transform it in a 
     * string and prove if this was done properly.
     * 
     * @throws IllegalAccessException if it the DynaBeans to test can not be 
     * access from any reason. If this exception occurs the test fails.
     * @throws InstantiationException if it the DynaBeans to test can not be 
     * instantiated from any reason.  If this exception occurs the test fails.
     * @throws ClassNotFoundException by any kind of type matching. 
     * If this exception occurs the test fails.
     * @see DynamicUtil#getNewInstance(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     * @see DynamicUtil#toJSONString(org.apache.commons.beanutils.DynaBean) 
     */
    @Test
    public void testToJSONString() throws IllegalAccessException,
            ClassNotFoundException, InstantiationException {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> dummyModelMap =
                DummyModelFactory.createDefaultModelMap();

        // here I build an class instance.
        final String name = DummyModelFactory.createUniqueString("myName");
        final String classURI = DummyModelFactory.createUniqueString("myURI");
        final DynamicClass dynamicClass =
                ModelFactory.buildDynamicClass(name, classURI, dummyModelMap);

        // obtains a DynaBean instance for the DynamicClass
        final DynaBean newBean = DynamicUtil.getNewInstance(dynamicClass);

        final String jsonString = DynamicUtil.toJSONString(newBean);
        assertNotNull(jsonString);

        // I build a JSONObject just to validate the string
        final JSONObject jsonObject = JSONObject.fromObject(jsonString);
        assertNotNull(jsonObject);

        final Object stringProperty = jsonObject.get("stringProperty");
        // enpty string means null, the stringProperty has no value because the 
        // newBean was just created and not populated with data.
        assertTrue(stringProperty.toString().isEmpty());
    }

    /**
     * It builds an <code>DynaBean</code> instance from an existing
     * (and data populated) <code>DynamicBean</code>.
     * 
     * @throws IllegalAccessException if it the DynaBeans to test can not be 
     * access from any reason. If this exception occurs the test fails.
     * @throws InstantiationException if it the DynaBeans to test can not be 
     * instantiated from any reason.  If this exception occurs the test fails.
     * @throws ClassNotFoundException by any kind of type matching. 
     * If this exception occurs the test fails.
     * @see DynamicUtil#get(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean) 
     */
    @Test
    public void testGetDynaBeanFromDymanicBean() 
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> dummyModelMap =
                DummyModelFactory.createDefaultModelMap();

        // here I build an class instance.
        final String name = DummyModelFactory.createUniqueString("myName");
        final String classURI = DummyModelFactory.createUniqueString("myURI");
        final DynamicClass dynamicClass =
                ModelFactory.buildDynamicClass(name, classURI, dummyModelMap);

        // this bean has 3 values.
        final DynamicBean dynamicBean =
                DummyModelFactory.buildDefaultDynamicBean(dynamicClass);

        // here I transform the DynamicBean in to a DynaBean
        final DynaBean dynaBean = DynamicUtil.get(dynamicBean);
        final DynaClass dynaClass = dynaBean.getDynaClass();
        assertNotNull(dynaClass);

        // the bean has only three properties
        final DynaProperty[] dynaProperties = dynaClass.getDynaProperties();
        assertEquals(3, dynaProperties.length);

        // iterate over properties and prove the values
        for (DynaProperty dynaProperty : dynaProperties) {
            final Class type = dynaProperty.getType();
            final String dynaPropertyName = dynaProperty.getName();
            final Object dynaPropertyValue = dynaBean.get(dynaPropertyName);

            // extra chech for the property value type.
            // the value type must be the same like the type variable
            final Class dynaPropertyValueType = dynaPropertyValue.getClass();
            assertEquals(type, dynaPropertyValueType);

            final Serializable expectValue = getValueForType(type);
            assertEquals(expectValue, dynaPropertyValue);
        }
    }

    /**
     * Builds a <code>DynamicClass</code> based on a java class.
     * 
     * @throws IntrospectionException 
     * @see DynamicUtil#get(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     */
    @Test
    public void testGetForJavaClass() throws IntrospectionException {
        // here I build an class instance.
        final String name = DummyModelFactory.createUniqueString("myName");
        final String classURI = DummyModelFactory.createUniqueString("myURI");
        
        // here I build the class baed on the DynamicClass based on the 
        // java class DummyBean.
        final DynamicClass dynamicClass = DynamicUtil.get(DummyBean.class, name, classURI);
        assertNotNull(dynamicClass);
        
        // get the class properties, it only has 3 propeterties
        final Set<DynamicPropertyType> propertyTypes = dynamicClass.getPropertyTypes();
        assertEquals(3, propertyTypes.size());
        
        //iterates over the propeteirs and proves the name/type
        for (DynamicPropertyType propertyType : propertyTypes) {
            final String propertyTypeName = propertyType.getName();
            final String type = propertyType.getType();
            
            // for more information about the type and them names please consult
            // the DummyBean class.
            if (Date.class.getName().equals(type)) {
                assertEquals("firstApperance", propertyTypeName);
            } else if (Boolean.class.getName().equals(type)) {
                assertEquals("superHero", propertyTypeName);
            } else if (String.class.getName().equals(type)) {
                assertEquals("name", propertyTypeName);
            } else {
                throw new AssertionError("Type [" + type + "] is not supporteds. ");
            }
        }
    }
}
