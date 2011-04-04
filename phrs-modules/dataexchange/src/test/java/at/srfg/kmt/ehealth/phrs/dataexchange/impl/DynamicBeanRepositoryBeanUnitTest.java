/*
 * Project :iCardea
 * File : DymanicBeanRepositoryBeanUnitTest.java
 * Encoding : UTF-8
 * Date : Mar 29, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;


import java.io.Serializable;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelFactory;
import java.io.File;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynaClassException;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicPropertyTypeException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides test for the <code>DymanicBeanRepository</code> implementation.
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see DymanicBeanRepository
 * @see DymanicBeanRepositoryBean
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class DynamicBeanRepositoryBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.DynamicBeanRepositoryBeanUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DynamicBeanRepositoryBeanUnitTest.class);

    /**
     * The <code>DymanicBeanRepository</code> instance to test.
     */
    @EJB
    private DynamicBeanRepository beanRepository;

    /**
     * The <code>DymanicClassRepository</code> instance to test.
     */
    @EJB
    private DynamicClassRepository classRepository;

    /**
     * Builds a <code>DymanicBeanRepositoryBeanUnitTest</code> instance.
     */
    public DynamicBeanRepositoryBeanUnitTest() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a <code>JavaArchive</code> that contains
     * all the required beans and libraries;
     * the Arquillian deploy it in to the running container
     * under the name test.ebj when the test starts.
     *
     * @return a <code>JavaArchive</code> which contains all the needed calsses.
     * @throws MalformedURLException if the test ejb jar can not be created
     * from any reasons.
     */
    @Deployment
    public static Archive<?> createDeployment() throws MalformedURLException {
        final JavaArchive ejbJar =
                ShrinkWrap.create(JavaArchive.class, "phrs.dataexchage.test.jar");

        // all the classes from the :
        // at.srfg.kmt.ehealth.phrs.dataexchange.impl and
        // at.srfg.kmt.ehealth.phrs.dataexchange.api and
        // at.srfg.kmt.ehealth.phrs.dataexchange.model are
        // added to the ejb jar (and to the classpath).
        // see the log for the ejb jar structure.
        ejbJar.addPackage(DynamicBeanRepositoryBeanUnitTest.class.getPackage());

        final Package apiPackage = DynamicBeanRepository.class.getPackage();
        ejbJar.addPackage(apiPackage);

        final Package modelPackage = DynamicBean.class.getPackage();
        ejbJar.addPackage(modelPackage);

        // the test-persistence.xml file is in the classpath, it is added
        // to the deployed under the name persistence.xml.
        // I preffer to keep the test related JPA configuration separate
        // from the production (JPA) configuration.
        ejbJar.addManifestResource("test-persistence.xml", "persistence.xml");

        final EnterpriseArchive ear =
                ShrinkWrap.create(EnterpriseArchive.class, "test.ear");
        ear.addModule(ejbJar);
        final File lib = ArchiveHelper.getCommonsBeanUtils();
        ear.addLibraries(lib);

        final String earStructure = ear.toString(true);
        LOGGER.debug("EAR jar structure on deploy is :");
        LOGGER.debug(earStructure);

        final String ejbStructure = ejbJar.toString(true);
        LOGGER.debug("EJB jar structure on deploy is :");
        LOGGER.debug(ejbStructure);

        return ear;
    }

    /**
     * Create a new DynamicBean instance, add it and prove if the operation
     * was successfully. </br>
     * More precisely this tests does :
     * <ul>
     * <li> it builds a <code>DynamicClass</code> based on a map of properties.
     * This map (of properties) contains contains three properties(a string, 
     * a boolean and a date).
     * <li> it persist the class.
     * <li> It builds a <code>DynamicBean</code> for the upper defined class.
     * <li> It sets values for the bean properties 
     * <li> It persist the bean (with the properties). 
     * <li> It proves if the bean was persisted - by querying the repository for
     * beans for a given class.
     * <li> It obtains the last bean instance by querying the repository for
     * the last version for a bean with given class.
     * <li> It iterates over all the properties and proves if the properties are
     * conform with the original one.
     * </ul>
     * Note : This test emphasis search for the  <b>last</b> bean version, 
     * it does not test the bean history.
     * 
     * @see DymanicBeanRepository#add(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean) 
     * @see DymanicBeanRepository#contains(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     * @see DymanicBeanRepository#getForClass(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass)
     */
    @Test
    public void testPrersistAndQuery() throws DynamicPropertyTypeException, DynaClassException {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> defaultModelMap =
                DummyModelFactory.createDefaultModelMap();

        // here I build an class instance.
        final String name = ModelFactory.buildUniqueString("myName");
        final String classURI = ModelFactory.buildUniqueString("myURI");
        final DynamicClass dynamicClass =
                ModelFactory.buildDynamicClass(name, classURI, defaultModelMap);

        // here I add it.
        classRepository.persist(dynamicClass);

        // here I obtain the laready persisted class.
        final DynamicClass getClass = classRepository.get(classURI);

        // here I build the dynaimc bean
        final DynaBean dynaBean = DynamicUtil.getNewInstance(getClass);
        populateBean(dynaBean);

        // and here I persist the bean
        beanRepository.add(dynaBean);

        // I prove if the repository contains a bean with the same class
        final boolean contains = beanRepository.contains(getClass);
        assertTrue(contains);

        // here I get all the registered beans for the given class (type)
        final Set<DynaBean> allForClass = beanRepository.getAllForClass(getClass);
        assertTrue(!allForClass.isEmpty());

        // here I get the last version for this bean (the bean is still empty 
        // no properties was set).
        final DynaBean beanForClass = beanRepository.getForClass(getClass);
        assertNotNull(beanForClass);

        // I have three proepties + the default properties
        final DynaProperty[] dynaProperties =
                beanForClass.getDynaClass().getDynaProperties();
        final int propertiesCount = 3 + DynamicUtil.getDefaultPropertiesCount();
        assertEquals(propertiesCount, dynaProperties.length);

        // proves of the bean's class has the right uri.
        final DynaClass dynaClass = beanForClass.getDynaClass();
        assertEquals(classURI, dynaClass.getName());

        // here I iterate over all the propeteries and prove them values
        DynamicUtilUnitTest.proveDefaultProperties(dynaClass);

        // prove the non-default values
        provePropertiesValues(beanForClass);
    }

    private void populateBean(final DynaBean dynaBean) {
        // here I set the properties for the bean
        dynaBean.set(DummyModelFactory.STRING_PROPERTY_NAME,
                DummyModelFactory.STRING_PROPERTY_VALUE);
        dynaBean.set(DummyModelFactory.BOOLEAN_PROPERTY_NAME,
                DummyModelFactory.BOOLEAN_PROPERTY_VALUE);
        dynaBean.set(DummyModelFactory.DATE_PROPERTY_NAME,
                DummyModelFactory.DATE_PROPERTY_VALUE);
    }

    /**
     * Proves the non-dynamic properties values for a given bean.
     * 
     * @param dynaBean the bean to prove.
     */
    static void provePropertiesValues(DynaBean dynaBean) {

        for (String propName : DummyModelFactory.PROPERTY_NAMES) {
            final Object value = dynaBean.get(propName);
            final Serializable expectValue =
                    DummyModelFactory.getPropertyValueForName(propName);
            final String msg = String.format("No value for the property %s", propName);
            assertEquals(msg, expectValue, value);
        }
    }

    /**
     * This test proves the versioning feature for the 
     * <code>DynamicBeanRepository</code>. More precisely this test does :
     * <ul>
     * <li> it builds a <code>DynamicClass</code> based on a map of properties.
     * This map (of properties) contains contains three properties(a string, 
     * a boolean and a date).
     * <li> it persist the class.
     * <li> It builds a <code>DynamicBean</code> for the upper defined class.
     * <li> It persists the a <code>DynamicBean</code> for the upper defined
     * class for a number if times, each persist generates a new version.
     * <li> proves if the number of versions are correct.
     * </ul>
     * 
     * @see DymanicBeanRepository#add(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean) 
     * @see DymanicBeanRepository#contains(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     * @see DymanicBeanRepository#getForClass(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass)
     */
    @Test
    public void testVersioning() throws DynamicPropertyTypeException, DynaClassException {

        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> defaultModelMap =
                DummyModelFactory.createDefaultModelMap();

        // here I build an class instance.
        final String name = ModelFactory.buildUniqueString("myName");
        final String classURI = ModelFactory.buildUniqueString("myURI");
        final DynamicClass dynamicClass =
                ModelFactory.buildDynamicClass(name, classURI, defaultModelMap);

        // here I add it.
        classRepository.persist(dynamicClass);

        // here I obtain the laready persisted class.
        final DynamicClass getClass = classRepository.get(classURI);

        // here I build the dynaimc bean
        final DynaBean dynaBean = DynamicUtil.getNewInstance(getClass);
        
        populateBean(dynaBean);
        
        // here I add the bean 10 times, after this the bean repository will 
        // contain 10 verions of the given bean, ten verison for the same bean
        // type (class)
        final int amount = 10;
        for (int i = 0; i < amount; i++) {
            beanRepository.add(dynaBean);
        }
        
        // get all the beans fot the given class, the repositry muat contin 10 verions.
        final Set<DynaBean> allForClass = beanRepository.getAllForClass(getClass);
        assertEquals(amount, allForClass.size());
    }
}
