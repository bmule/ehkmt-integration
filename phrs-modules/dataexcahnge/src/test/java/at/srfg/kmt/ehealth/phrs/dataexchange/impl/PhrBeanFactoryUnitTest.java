/*
 * Project :iCardea
 * File : ActorException.java
 * Date : Dec 15, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaProperty;
import static org.junit.Assert.*;
import org.apache.commons.beanutils.DynaBean;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the <code>PhrBeanFactory</code> functionality together with the 
 * <code>DefaultPhrBeanPropertiesFactory</code>.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see DefaultPhrBeanPropertiesFactory
 * @see PhrBeanFactory
 */
public class PhrBeanFactoryUnitTest {
    
    /**
     * The factory used to build beans instances.
     */
    private static PhrBeanFactory factory;
    
    /**
     * Used to provide the default properties array.
     */
    private static PhrBeanPropertiesFactory propertiesFactory;
    
    /**
     * Gets call before the test suite runs.
     */
    @BeforeClass
    public static void init() {
        factory = PhrBeanFactory.getInstance();
        propertiesFactory = DefaultPhrBeanPropertiesFactory.getInstance();
    }

    /**
     * Builds a bean with the default configuration and prove it if the new
     * builded bean has all the required properties.
     * 
     * @throws IllegalAccessException if the dynamic bean instance can not be created.
     * @throws InstantiationException  if the dynamic bean instance can not be created.
     */
    @Test
    public void testCreateDefaultBean() throws IllegalAccessException, InstantiationException {
        // builds an enpty bean, the bean is empty.
        final DynaBean bean = factory.buidBean();
        assertNotNull(bean);
        
        final DynaProperty[] properties =
                bean.getDynaClass().getDynaProperties();
        final DynaProperty[] defaultProperties = 
                propertiesFactory.getProperties();
        
        assertArrayEquals(defaultProperties, properties);
        // all the vaues are null, the bean is empty.
        for (DynaProperty property : defaultProperties) {
            final String propertyName = property.getName();
            final Object value = bean.get(propertyName);
            assertNull(value);
        }
    }
    
    /**
     * Builds a dynamic bean fills is with data and prove if this was correct.
     * The dynamic bean is filled with data by using the 
     * <code>BeanUtils.populate</code> method.
     * 
     * 
     * @throws IllegalAccessException if the dynamic bean instance can not be created.
     * @throws InstantiationException  if the dynamic bean instance can not be created.
     */
    @Test
    public void testUseDefaultBean() 
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final DynaBean bean = factory.buidBean();
        
        // this prepares the map with the values.
        final Map values = new HashMap();
        values.put("phrs__DisplayName", "blablabla");
        values.put("phrs__OwnerId", "mihai");
        values.put("phrs__CreatedBy", "mihai");
        final Date date = new Date();
        values.put("phrs__CreateDate", date);
        values.put("phrs__ModifyDate", date);
        
        final Set<String> types = new HashSet<String>();
        types.add("type1");
        types.add("type2");
        types.add("type3");
        values.put("phrs__Types", types);

        values.put("phrs__ResourceURI", "mihai.uri");
        values.put("phrs__Deleted", Boolean.TRUE);
        
        BeanUtils.populate(bean, values);
        
        final String getDysplayName = (String) bean.get("phrs__DisplayName");
        assertEquals("blablabla", getDysplayName);
    }
}
