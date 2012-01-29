/*
 * Project :iCardea
 * File : TermClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.*;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class PHRSRequestClientUnitTest {

    private static final String REPLY_URI = "myURI";

    private static final String ID = "myID";

    private static final String CODE = "myCode";

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>TermClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PHRSRequestClientUnitTest.class);

    private GenericTriplestore triplestore;

    private PHRSRequestClient requestClient;

    /**
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     *
     * @throws GenericRepositoryException if this occurs then the test
     * environment may be wrong set.
     */
    @Before
    public void initSuite() throws GenericRepositoryException {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        triplestore = connectionFactory.getTriplestore();
        requestClient = new PHRSRequestClient(triplestore);
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
    public void addPHRSRequest() throws TripleException {
        final String stuff = requestClient.addPHRSRequest(REPLY_URI, ID, CODE);
        assertNotNull(stuff);

        final Iterable<Triple> forSubject = triplestore.getForSubject(stuff);
        int tripleCount = 0;
        for (Triple triple : forSubject) {
            proveTriples(triple);

            tripleCount++;
        }
        // I expect only 5 tripeles for this uri
        assertEquals(5, tripleCount);
    }

    @Test
    public void addPHRSRequestTriples() throws TripleException {
        final String stuff = requestClient.addPHRSRequest(REPLY_URI, ID, CODE);
        assertNotNull(stuff);

        final Iterable<Triple> triples = requestClient.getAllPHRSRequestsTriples();
        int count = 0;
        for (Triple triple : triples) {
            proveTriples(triple);
            count++;
        }
        assertEquals(5, count);
    }

    @Test
    public void addRemovePHRSRequestWithArguments() throws TripleException {
        final String stuff = requestClient.addPHRSRequest(REPLY_URI, ID, CODE);
        assertNotNull(stuff);

        requestClient.removeAllPHRSRequests(REPLY_URI, ID, CODE);
        final Iterable<Triple> triples = requestClient.getAllPHRSRequestsTriples();
        int count = 0;
        for (Triple triple : triples) {
            count++;
        }
        assertEquals(0, count);
    }

    @Test
    public void addRemovePHRSRequest() throws TripleException {
        final String stuff = requestClient.addPHRSRequest(REPLY_URI, ID, CODE);
        assertNotNull(stuff);

        requestClient.removeAllPHRSRequests();
        final Iterable<Triple> triples = requestClient.getAllPHRSRequestsTriples();
        int count = 0;
        for (Triple triple : triples) {
            count++;
        }
        assertEquals(0, count);
    }

    private void proveTriples(Triple triple) {
        final String subject = triple.getSubject();
        final String predicate = triple.getPredicate();
        final String value = triple.getValue();

        if (Constants.RDFS_TYPE.equals(predicate)) {
            assertEquals(Constants.PHRS_REQUEST_CLASS, value);
        }

        if (Constants.HL7V3_REPLY_ADRESS.equals(predicate)) {
            assertEquals(REPLY_URI, value);
        }

        if (Constants.HL7V3_CARE_PROVISION_CODE.equals(predicate)) {
            assertEquals(CODE, value);
        }

        if (Constants.PHRS_ACTOR_PROTOCOL_ID.equals(predicate)) {
            assertEquals(ID, value);
        }
    }
}
