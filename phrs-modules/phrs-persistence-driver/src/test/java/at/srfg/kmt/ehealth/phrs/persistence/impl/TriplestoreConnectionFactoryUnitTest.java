/*
 * Project :iCardea
 * File : TriplestoreConnectionFactoryUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl;

import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import org.junit.Test;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains test related with the lifecycle phases for a generic triplestore.
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class TriplestoreConnectionFactoryUnitTest {

    public static final String FIRST_VALID_RESOURCE =
            "http://www.icardea.at/phrs/instances/codeSystem/snomed";
    
    public static final String SECOND_VALID_RESOURCE =
            "http://www.icardea.at/phrs/instances/codeSystem/Loinc";
    
    
    public static final String INVALID_SUBJECT =
            "http://www.icardea.at/phrs/instances/codeSystem/other_snomed"
            + "";
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>TriplestoreConnectionFactoryUnitTest</code>..
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(TriplestoreConnectionFactoryUnitTest.class);
    private GenericTriplestore triplestore;

    /**
     * Initialize the genric triple store according with a given configuration.
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     * 
     * @throws GenericRepositoryException if this occurs then the test 
     * environment may be wrong set.
     */
    @Before
    public void initSiute() throws GenericRepositoryException {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();

        triplestore = connectionFactory.getTriplestore();
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
     * Initialize the triplestore according with a given configuration file.
     * The configuration file is named : "generic_triplestore.xml" and it must 
     * be placed in the classpath.
     * 
     * @throws TripleException
     * @throws GenericRepositoryException 
     */
    @Test
    public void testInit() throws TripleException, GenericRepositoryException {
        testContent(FIRST_VALID_RESOURCE, triplestore);
        testContent(SECOND_VALID_RESOURCE, triplestore);
    }

    /**
     * Proves the default content for a given triple store. <br/>
     * More precisely this methods knows what a given triplestore must contains;
     * this because the triplestore load during its init process a known 
     * rdf file.
     * 
     * @param triplestore the triplestore to prove.
     * @throws TripleException  by any triplestro related exception.
     */
    private void testContent(String resourceURI, GenericTriplestore triplestore) throws TripleException {
        // The loaded file startup.test.rdf contains only two triples with the 
        // given subject.
        final Iterable<Triple> forValidSubject =
                triplestore.getForSubject(resourceURI);

        int validCount = 0;
        for (Triple triple : forValidSubject) {
            validCount++;
        }

        // the loaded rdf file contains olny 2 triples
        assertEquals(2, validCount);

        final boolean validExists = triplestore.exists(resourceURI);
        assertTrue(validExists);

        final Iterable<Triple> forInvalidSubject =
                triplestore.getForSubject(INVALID_SUBJECT);
        int invalidCount = 0;
        for (Triple triple : forInvalidSubject) {
            invalidCount++;
        }
        assertEquals(0, invalidCount);

        final boolean invalidExists = triplestore.exists(INVALID_SUBJECT);
        assertFalse(invalidExists);
    }

    /**
     * Initialize and shutdown a triplestore a few times without to purge its 
     * local resources.
     * 
     * @throws TripleException
     * @throws GenericRepositoryException 
     */
    @Test
    public void testRepetativeInitWithoutCleanEnviroment() 
        throws TripleException, GenericRepositoryException {

        ((GenericTriplestoreLifecycle) triplestore).shutdown();

        // here I init and shutdown the triplestore connectionn times
        // the enviroment remains the same (the environment includes the 
        // file dump). 
        // The start up rdf files are loaded and loaded but the content 
        // reamains the same
        for (int i = 0; i < 9; i++) {
            final TriplestoreConnectionFactory connectionFactory =
                    TriplestoreConnectionFactory.getInstance();
            triplestore = connectionFactory.getTriplestore();
        }

        testContent(FIRST_VALID_RESOURCE, triplestore);
        testContent(SECOND_VALID_RESOURCE, triplestore);
    }
}
