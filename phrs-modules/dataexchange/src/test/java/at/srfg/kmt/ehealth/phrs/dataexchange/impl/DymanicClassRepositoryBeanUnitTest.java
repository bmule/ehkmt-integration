/*
 * Project :iCardea
 * File : DymanicClassRepositoryBeanUnitTest.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DymanicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import java.net.MalformedURLException;
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
 * @see DymanicClassRepository
 * @see DymanicClassRepositoryBean
 */
@RunWith(Arquillian.class)
@Run(RunModeType.IN_CONTAINER)
public class DymanicClassRepositoryBeanUnitTest {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.ActorManagerBeanUnitTest</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(DymanicClassRepositoryBeanUnitTest.class);
    /**
     * The <code>DymanicClassRepository</code> instance to test.
     */
    @EJB
    private DymanicClassRepository dynanicClassRepository;

    /**
     * Builds a <code>DymanicClassRepositoryBeanUnitTest</code> instance.
     */
    public DymanicClassRepositoryBeanUnitTest() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a <code>JavaArchive</code> that contains
     * all the required beans and libraries;
     * the Arquillian deploy it in to the running container
     * under the name test.ebj when the test starts.
     *
     * @return a <code>JavaArchive</code> which contains the <code>MyService</code>
     * interface and the <code>MyServiceBean</code> class.
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
        ejbJar.addPackage(DymanicClassRepositoryBeanUnitTest.class.getPackage());

        final Package apiPackage = DymanicClassRepository.class.getPackage();
        ejbJar.addPackage(apiPackage);

        final Package modelPackage = DynamicClass.class.getPackage();
        ejbJar.addPackage(modelPackage);


        // the test-persistence.xml file is in the classpath, it is added
        // to the deployed under the name persistence.xml.
        // I preffer to keep the test related JPA configuration separate
        // from the production (JPA) configuration.
        ejbJar.addManifestResource("test-persistence.xml", "persistence.xml");

        final String ejbStructure = ejbJar.toString(true);
        logger.debug("EJB jar structure on deploy is :");
        logger.debug(ejbStructure);
        return ejbJar;
    }

    /**
     * Builds and persists an empty DynamicClass and after this it proves if this 
     * was done properly.
     */
    @Test
    public void testCreate() {
        final DynamicClass create = dynanicClassRepository.create();
        assertNotNull(create);

        final boolean exits = dynanicClassRepository.exits(create);
        assertTrue(exits);
    }

    /**
     * Builds and persists a DynamicClass for a given URI and after this it
     * proves if this was done properly.
     */
    @Test
    public void testCreateWithUri() {
        final String uri = DummyModelFactory.createUniqueString("URI");
        final DynamicClass create = dynanicClassRepository.create(uri);
        assertNotNull(create);

        final String getURI = create.getUri();
        assertEquals(uri, getURI);

        final boolean exits = dynanicClassRepository.exits(create);
        assertTrue(exits);
    }

    /**
     * Builds and persists a DynamicClass for a given URI and name and after this it
     * proves if this was done properly.
     */
    @Test
    public void testCreateWithUriAndName() {
        final String uri = DummyModelFactory.createUniqueString("URI");
        final String name = DummyModelFactory.createUniqueString("name");

        final DynamicClass create = dynanicClassRepository.create(name, uri);
        assertNotNull(create);

        final String getURI = create.getUri();
        assertEquals(uri, getURI);

        final String getName = create.getName();
        assertEquals(name, getName);

        final boolean exits = dynanicClassRepository.exits(create);
        assertTrue(exits);
    }

    /**
     * It builds a DynamicClass based on a given map of properties, persist it
     * and proves if the persist was done properly.
     * The Map used to build the DynamicClass three properties named :
     * <ul>
     * <li> stringProperty - property type java.lang.String
     * <li> booleanProperty - property type java.lang.Boolean
     * <li> dateProperty - property type java.util.date
     * </ul>
     * each of this property has a metadata as follow : 
     * <ul>
     * <li> for the "stringProperty" the metadata name is "stringPropertyMetadata" and the metadata value is "stringPropertyMetadata.value"
     * <li> for the "dateProperty" the metadata name is "datePropertyMetadata" and the metadata value is "Mon Jan 01 00:00:00 CET 1900"
     * <li> for the "booleanProperty" the metadata name is "booleanPropertyMetadata" and the metadata value is "true"
     * </ul>
     * so the resulted class will contain three properties (with the name and 
     * type upper defined), each of this type will have only one metadata.
     */
    @Test
    public void testPersistDefaultModel() {

        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createDefaultModelMap =
                DummyModelFactory.createDefaultModelMap();

        final String name = DummyModelFactory.createUniqueString("myName");
        final String classURI = DummyModelFactory.createUniqueString("myURI");
        final DynamicClass dynamicClass =
                at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelFactory.buildDynamicClass(name, classURI, createDefaultModelMap);

        dynanicClassRepository.persist(dynamicClass);

        final boolean exists = dynanicClassRepository.exits(classURI);
        assertTrue(exists);

        // here I obtain the persisted DynamicClass
        final DynamicClass get = dynanicClassRepository.get(classURI);
        assertNotNull(get);


        final Set<DynamicPropertyType> propertyTypes = get.getPropertyTypes();
        // the dummy bean contains only three properties
        assertEquals(3, propertyTypes.size());


        for (DynamicPropertyType type : propertyTypes) {
            provePropertyType(type);
        }

    }

    private void provePropertyType(DynamicPropertyType type) {
        final String[] names = {"booleanProperty", "stringProperty", "dateProperty"};
        final String name = type.getName();
        assertTrue(Arrays.asList(names).contains(name));

        final String[] types = {"java.lang.Boolean", "java.lang.String", "java.util.Date"};
        final String clazz = type.getType();
        assertTrue(Arrays.asList(types).contains(clazz));

        // and the property has only one metadata.
        final Set<DynamicPropertyMetadata> metadatas = type.getMetadatas();
        assertEquals(1, metadatas.size());

        provePropertyTypeMetadata(type);
    }

    private void provePropertyTypeMetadata(DynamicPropertyType type) {
        final Set<DynamicPropertyMetadata> metadatas = type.getMetadatas();

        // this is the name defined in the default model
        // see the ModelFactory.createDefaultModelMap for more detals 
        final String expectName = getMetadataName(type);
        final Serializable expectValue = getMetadataValue(type).toString();
        for (DynamicPropertyMetadata metadata : metadatas) {
            final String name = metadata.getName();
            assertEquals(expectName, name);

            final Serializable value = metadata.getValue();
            assertEquals(expectValue, value.toString());
        }
    }

    private String getMetadataName(DynamicPropertyType type) {
        final String clazz = type.getType();
        if (Date.class.getName().equals(clazz)) {
            return "datePropertyMetadata";
        }

        if (Boolean.class.getName().equals(clazz)) {
            return "booleanPropertyMetadata";
        }

        if (String.class.getName().equals(clazz)) {
            return "stringPropertyMetadata";
        }

        throw new RuntimeException(clazz + " is not supported.");
    }

    private Object getMetadataValue(DynamicPropertyType type) {
        final String clazz = type.getType();
        if (Date.class.getName().equals(clazz)) {
            return DummyModelFactory.DATE;
        }

        if (Boolean.class.getName().equals(clazz)) {
            return Boolean.TRUE.booleanValue();
        }

        if (String.class.getName().equals(clazz)) {
            return "stringPropertyMetadata.value";
        }

        throw new RuntimeException(clazz + " is not supported.");
    }

    @Test
    public void testPersistDefaultModelWithoutMetadata() {
        final Set<DynamicPropertyType> typeSet = DummyModelFactory.createDefaultTypeSet();
        final String name = DummyModelFactory.createUniqueString("myName");
        final String classURI = DummyModelFactory.createUniqueString("myURI");


        final DynamicClass dynamicClass =
                at.srfg.kmt.ehealth.phrs.dataexchange.model.ModelFactory.buildDynamicClass(name, classURI, typeSet);
        dynanicClassRepository.persist(dynamicClass);

        final boolean exists = dynanicClassRepository.exits(classURI);
        assertTrue(exists);

        // here I obtain the persisted DynamicClass
        final DynamicClass get = dynanicClassRepository.get(classURI);
        assertNotNull(get);

        final Set<DynamicPropertyType> propertyTypes = get.getPropertyTypes();
        // the dummy bean contains only three properties
        assertEquals(3, propertyTypes.size());
    }

    /**
     * Creates a <code>DynamicClass</code> remove it and proves if the class was
     * properly removed. The <code>DynamicClass</code> to remove is located
     * after its URI.
     */
    @Test
    public void testRemoveByURI() {
        final String name = DummyModelFactory.createUniqueString("myName");
        final String uri = DummyModelFactory.createUniqueString("myURI");
        final DynamicClass create = dynanicClassRepository.create(name, uri);
        
        final DynamicClass remove = dynanicClassRepository.remove(create);
        assertNotNull(create);
        
        final String removeURI = remove.getUri();
        assertNotNull(removeURI);
        
        assertEquals(uri, removeURI);
        
        final boolean exits = dynanicClassRepository.exits(uri);
        assertFalse(exits);
    }
    
    /**
     * 
     */
    @Test
    public void testRemoveByInstance() {
        final String name = DummyModelFactory.createUniqueString("myName");
        final String uri = DummyModelFactory.createUniqueString("myURI");
        final DynamicClass create = dynanicClassRepository.create(name, uri);
        
        final DynamicClass get = dynanicClassRepository.get(uri);
        assertNotNull(create);
        
        final DynamicClass remove = dynanicClassRepository.remove(get);
        
        
        final String removeURI = remove.getUri();
        assertNotNull(removeURI);
        
        assertEquals(uri, removeURI);
        
        final boolean exits = dynanicClassRepository.exits(uri);
        assertFalse(exits);
    }
}
