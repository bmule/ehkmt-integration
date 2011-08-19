/*
 * Project :iCardea
 * File : SesameTriplestoreUnitTest.java
 * Encoding : UTF-8
 * Date : Jun 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.impl.sesame;


import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test suite is used to test the <code>SesameTriplestore</code> 
 * functionality.
 * 
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 * @see SesameTriplestore
 */
public class SesameTriplestoreInitUnitTest {
    
    
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>SesameTriplestoreInitUnitTest</code>..
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SesameTriplestoreInitUnitTest.class);

    private GenericTriplestore triplestore;

    /**
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     * 
     * @throws GenericRepositoryException if this occurs then the test 
     * environment may be wrong set.
     */
    @Before
    public void initSiute() throws GenericRepositoryException {
        triplestore = new SesameTriplestore();
        final String loadFile = "startup.test.rdf";
        final LoadRdfPostConstruct loadRdfPostConstruct = 
                new LoadRdfPostConstruct(loadFile);
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();
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

    @Test
    public void testInit() throws TripleException, GenericRepositoryException {
        final String subject = "http://www.icardea.at/phrs/instances/codeSystem/snomed";
        // The loaded file startup.test.rdf contains only two triples with the 
        // given subject.
        final Iterable<Triple> forSubject = 
                triplestore.getForSubject(subject);
        
        int count = 0;
        for (Triple triple : forSubject) {
            count++;
        }
        assertEquals(2, count);
        
        final boolean exists = triplestore.exists(subject);
        assertTrue(exists);
    }
}
