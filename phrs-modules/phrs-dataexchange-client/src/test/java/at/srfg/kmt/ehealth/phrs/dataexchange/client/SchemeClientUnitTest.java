/*
 * Project :iCardea
 * File : SchemeClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import static at.srfg.kmt.ehealth.phrs.Constants.RDF_LITERAL;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Test Suite proves the functionality for the <code>SchemeClient</code>. 
 * 
 * 
 * @author mihai
 * @see SchemeClient
 */
public class SchemeClientUnitTest {

    public static final String RIGHT_PROPERTY = "http://www.icardea.at/phrs/hl7V3#endDate";
    public static final String WRONG_PROPERTY = "http://www.icardea.at/phrs/hl7V3#XXXXXXXXXXXXXXXXXXX";
    public static Set<String> CLASS_PROPERTIES;
    /**
     * The URI for the RDF class used in this test suite. This (RDF) class is 
     * loaded during the initialization process from a rdf file. The name and 
     * location for this rdf file is configurated with a XML file 
     * (with a certain syntax).  
     */
    public static final String CLASS_URI = "http://www.icardea.at/phrs/types/1.0/VitalSign";

    /**
     * All this properties are according with the loaded rdf file. The name and 
     * location for this rdf file is configurated with a XML file 
     * (with a certain syntax).  
     */
    static {
        CLASS_PROPERTIES = new HashSet<String>();
        CLASS_PROPERTIES.add("http://www.icardea.at/phrs/hl7V3#status");
        CLASS_PROPERTIES.add("http://www.icardea.at/phrs/hl7V3#note");
        CLASS_PROPERTIES.add("http://www.icardea.at/phrs/hl7V3#unit");
        CLASS_PROPERTIES.add("http://www.icardea.at/phrs/hl7V3#code");
        CLASS_PROPERTIES.add("http://www.icardea.at/phrs/hl7V3#effectiveTime");
        CLASS_PROPERTIES.add("http://www.icardea.at/phrs/hl7V3#templIdRoot");
        CLASS_PROPERTIES.add("http://www.icardea.at/phrs/hl7V3#value");
        CLASS_PROPERTIES.add("http://www.icardea.at/phrs/hl7V3#dispathedTo");
    }
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>SchemeClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SchemeClientUnitTest.class);
    private GenericTriplestore triplestore;
    private SchemeClient schemeClient;

    /**
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     * 
     * @throws GenericRepositoryException if this occurs then the test 
     * environment may be wrong set.
     */
    @Before
    public void initSuite() throws GenericRepositoryException, TripleException {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        triplestore = connectionFactory.getTriplestore();
        schemeClient = new SchemeClient(triplestore);
    }

    /**
     * Runs all after any test from this suite and cleans the environment in 
     * order that the next test will get a 'clean' environment.
     * 
     * @throws GenericRepositoryException 
     */
    @After
    public void shutdownSuite() throws GenericRepositoryException {
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
        triplestore = null;
    }

    /**
     * It proves if a given (class) property exists or not. The property is 
     * loaded during the startup process.
     * 
     * @throws TripleException 
     */
    @Test
    public void testPropertyExists() throws TripleException {
        final boolean rightPropertyExists =
                schemeClient.propertyExists(RIGHT_PROPERTY);
        assertTrue(rightPropertyExists);

        final boolean wrongPropertyExists =
                schemeClient.propertyExists(WRONG_PROPERTY);
        assertFalse(wrongPropertyExists);
    }

    /**
     * Gets all the types (ranges) for a given property and prove is this are 
     * correct.
     * 
     * @throws TripleException 
     */
    @Test
    public void testGetPropertyType() throws TripleException {
        // all the ranges for this property
        final Set<String> ranges =
                schemeClient.getPropertyTypes(RIGHT_PROPERTY);
        assertFalse(ranges.isEmpty());
        assertTrue(ranges.contains(RDF_LITERAL));
        // I know thar the property is a literal
        assertTrue(schemeClient.isPropertyLiteral(ranges));
    }

    @Test
    public void testIsLiteralPropertyType() throws TripleException {
        // all the ranges for this property
        final Set<String> ranges =
                schemeClient.getPropertyTypes(RIGHT_PROPERTY);
        final boolean isLiteral = schemeClient.isPropertyLiteral(ranges);
        assertTrue(isLiteral);

        final boolean isResource = schemeClient.isPropertyResource(ranges);
        assertFalse(isResource);
    }

    @Test
    public void testAllClassPropertiesType() throws TripleException {
        final Set<String> allPropertiesForClass =
                schemeClient.getAllPropertiesForClass(CLASS_URI);
        assertFalse(allPropertiesForClass.isEmpty());

        final int propertiesCount = allPropertiesForClass.size();
        // I know that the this calss has 7 properties.
        assertEquals(CLASS_PROPERTIES.size(), propertiesCount);

        assertEquals(CLASS_PROPERTIES, allPropertiesForClass);
    }
}
