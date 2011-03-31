/*
 * Project :iCardea
 * File : DymanicBeanRepositoryBeanUnitTest.java
 * Encoding : UTF-8
 * Date : Mar 29, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DymanicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DymanicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicProperty;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.io.Serializable;
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
public class DymanicBeanRepositoryBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.DymanicBeanRepositoryBeanUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DymanicBeanRepositoryBeanUnitTest.class);
    /**
     * The <code>DymanicBeanRepository</code> instance to test.
     */
    @EJB
    private DymanicBeanRepository beanRepository;
    /**
     * The <code>DymanicClassRepository</code> instance to test.
     */
    @EJB
    private DymanicClassRepository classRepository;

    /**
     * Builds a <code>DymanicBeanRepositoryBeanUnitTest</code> instance.
     */
    public DymanicBeanRepositoryBeanUnitTest() {
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
    public static JavaArchive createDeployment() throws MalformedURLException {
        final JavaArchive ejbJar =
                ShrinkWrap.create(JavaArchive.class, "phrs.dataexchage.test.jar");

        // all the classes from the :
        // at.srfg.kmt.ehealth.phrs.dataexchange.impl and
        // at.srfg.kmt.ehealth.phrs.dataexchange.api and
        // at.srfg.kmt.ehealth.phrs.dataexchange.model are
        // added to the ejb jar (and to the classpath).
        // see the log for the ejb jar structure.
        ejbJar.addPackage(DymanicBeanRepositoryBeanUnitTest.class.getPackage());

        final Package apiPackage = DymanicBeanRepository.class.getPackage();
        ejbJar.addPackage(apiPackage);

        final Package modelPackage = DynamicBean.class.getPackage();
        ejbJar.addPackage(modelPackage);


        // the test-persistence.xml file is in the classpath, it is added
        // to the deployed under the name persistence.xml.
        // I preffer to keep the test related JPA configuration separate
        // from the production (JPA) configuration.
        ejbJar.addManifestResource("test-persistence.xml", "persistence.xml");

        final String ejbStructure = ejbJar.toString(true);
        LOGGER.debug("EJB jar structure on deploy is :");
        LOGGER.debug(ejbStructure);
        return ejbJar;
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
    public void testPrersistAndQuery() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> defaultModelMap =
                DummyModelFactory.createDefaultModelMap();

        // here I build an class instance.
        final String name = DummyModelFactory.createUniqueString("myName");
        final String classURI = DummyModelFactory.createUniqueString("myURI");
        final DynamicClass dynamicClass =
                at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelFactory.buildDynamicClass(name, classURI, defaultModelMap);
        
        // here I add it.
        classRepository.persist(dynamicClass);
        
        // here I obtain the laready persisted class.
        final DynamicClass getClass = classRepository.get(classURI);
        
        // here I build the dynaimc bean
        final DynamicBean dynamicBean = 
                DummyModelFactory.buildDefaultDynamicBean(getClass);
        
        // and here I persist the bean
        beanRepository.add(dynamicBean);
        
        
        // I prove if the repository contains a bean with the same class
        final boolean contains = beanRepository.contains(getClass);
        assertTrue(contains);
        
        // here I get all the registered beans for the given class (type)
        final Set<DynamicBean> allForClass = beanRepository.getAllForClass(getClass);
        assertTrue(!allForClass.isEmpty());
        
        // here I get the last version for this bean
        final DynamicBean beanForClass = beanRepository.getForClass(getClass);
        assertNotNull(beanForClass);
        
        // the bean must have 3 properties (see the build bean sequence).
        final Set<DynamicProperty> properties = beanForClass.getProperties();
        assertEquals(3, properties.size());
        
        // here I obtain the bean type.
        final DynamicClass beanClass = beanForClass.getDynamicClass();
        assertEquals(classURI, beanClass.getUri());
        
        // here I iterate over all (three) properties and prove them value
        for (DynamicProperty property : properties) {
            final String type = property.getType();
            final Serializable content = property.getContent();
            assertNotNull(content);
            final Serializable valueForType = DummyModelFactory.getValueForType(type);
            assertEquals(valueForType, content);
        }
    }
    
}
