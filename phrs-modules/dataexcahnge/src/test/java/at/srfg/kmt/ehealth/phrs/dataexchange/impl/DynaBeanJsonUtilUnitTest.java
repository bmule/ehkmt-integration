/*
 * Project :iCardea
 * File : DynaBeanJsonUtilUnitTest.java
 * Encoding : UTF-8
 * Date : Mar 4, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static org.junit.Assert.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Used to prove the <code>DynaBeanJsonUtil</code> functionality.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see DynaBeanJsonUtil
 */
public class DynaBeanJsonUtilUnitTest {

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
     * Builds a dynamic bean serialized and prove is the result is a valid JSON
     * object. The JSON object validity is done with the 
     * <code>JSONObject.fromObject</code> method. <br>
     * This test proves the <code>DynaBeanJsonUtil.toJSONStrig()</code>
     * functionality.
     * 
     * @throws IllegalAccessException if any field from the new builded bean
     * can not be access from any reasons.
     * @throws InstantiationException if the new builded bean can not be
     * instantiate.
     * @throws InvocationTargetException  if any field from the new builded bean
     * can not be access (get/set method invoke) from any reasons.
     * @see DynaBeanJsonUtil#toJSONStrig(org.apache.commons.beanutils.DynaBean) 
     */
    @Test
    public void testToJSONStrig() throws IllegalAccessException, 
            InstantiationException, InvocationTargetException {
        
        final DynaBean bean = buildBean();
        toJSONStrig(bean);
    }

    private String toJSONStrig(DynaBean bean) {
        final String jsonStrig = DynaBeanJsonUtil.toJSONStrig(bean);
        assertNotNull(jsonStrig);
        assertFalse(jsonStrig.isEmpty());

        // builds a json from the stirng
        final JSONObject jsonObject = JSONObject.fromObject(jsonStrig);
        assertNotNull(jsonStrig);
        // porove one propety (names) from the new builded json Object
        final Object displayName = jsonObject.get("phrs__DisplayName");
        assertEquals("blablabla", displayName);
        
        return jsonStrig;
    }

    /**
     * Builds a dynamic bean from a given JSON string and proves if the new 
     * builded dynamic bean is ok. <br>
     * This test proves the <code>DynaBeanJsonUtil.toJSONStrig()</code>
     * functionality.
     * 

     * @throws IllegalAccessException if any field from the new builded bean
     * can not be access from any reasons.
     * @throws InstantiationException if the new builded bean can not be
     * instantiate.
     * @throws InvocationTargetException  if any field from the new builded bean
     * can not be access (get/set method invoke) from any reasons.
     * @see DynaBeanJsonUtil#fromJSONString(java.lang.String) 
     */
    @Test
    public void testFromJSONStrig() throws IllegalAccessException,
            InstantiationException, InvocationTargetException {

        final DynaBean bean = buildBean();
        final String jsonStrig = toJSONStrig(bean);
        
        final DynaBean newBean = DynaBeanJsonUtil.fromJSONString(jsonStrig);
        assertNotNull(newBean);
        
        // porove one propety (names) from the new builded json Object
        final Object displayName = newBean.get("phrs__DisplayName");
        assertEquals("blablabla", displayName);
    }

    private DynaBean buildBean() throws IllegalAccessException,
            InstantiationException, InvocationTargetException {
        // FIXME : move this method on a util class.
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


        return bean;
    }
}
