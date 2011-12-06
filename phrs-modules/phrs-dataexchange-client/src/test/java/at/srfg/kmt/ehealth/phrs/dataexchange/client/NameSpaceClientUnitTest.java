/*
 * Project  :iCardea
 * File     : NameSpaceClientUnitTest.java
 * Encoding : UTF-8
 * Date     : Dec 6, 2011
 * User     : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test suite does some basic  tests (persist and retrieve) 
 * with the <code>NameSpaceClient</code>.
 * 
 * @author m1s
 * @version 0.1
 * @since 0.1
 * @see NameSpaceClient
 */
public class NameSpaceClientUnitTest {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.NameSpaceClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(NameSpaceClientUnitTest.class);
    /**
     * The unique name space identifier used in this test.
     */
    private static final String NAME_SPACE = "MY NAME SPACE";
    /**
     * The unique PHR System identifier used in this test.
     */
    private static final String PHRS_ID = "MY PHRS_ID";
    /**
     * The unique Protocol Id identifier used in this test.
     */
    private static final String PROTOCOL_ID = "MY PROTOCOL_ID";
    /**
     * The connection to the underlying persistence layer used in this test.
     */
    private GenericTriplestore triplestore;
    /**
     * The
     * <code>NameSpaceClient</code> to be tested.
     */
    private NameSpaceClient nameSpaceClient;

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
        nameSpaceClient = new NameSpaceClient(triplestore);
    }

    /**
     * Register a relation between : a name space, a PHRS id and a Protocol id
     * and proves if the result was persisted properly.
     *
     * @see NameSpaceClient#register(java.lang.String, java.lang.String,
     * java.lang.String)
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testRegisters() throws TripleException {

        // I register the relation between the name space, phrs id and 
        // protocol id.
        final String register =
                nameSpaceClient.register(NAME_SPACE, PHRS_ID, PROTOCOL_ID);
        // the result can not be null
        Assert.assertNotNull(register);

        final Iterable<Triple> forSubject = triplestore.getForSubject(register);
        for (Triple triple : forSubject) {
            LOGGER.debug("Test triple {}", triple);
            final String predicate = triple.getPredicate();
            final String value = triple.getValue();
            if (Constants.RDFS_TYPE.equals(predicate)) {
                Assert.assertEquals(Constants.PHRS_ACTOR_CLASS, value);
            }

            if (Constants.OWNER.equals(predicate)) {
                Assert.assertEquals(PHRS_ID, value);
            }

            if (Constants.CREATOR.equals(predicate)) {
                final String creator = NameSpaceClient.class.getName();
                Assert.assertEquals(creator, value);
            }

            if (Constants.CREATE_DATE.equals(predicate)) {
                // I only care that the date is not null.
                Assert.assertNotNull(value);
            }

            if (Constants.PHRS_ACTOR_PROTOCOL_ID.equals(predicate)) {
                Assert.assertEquals(PROTOCOL_ID, value);
            }

            if (Constants.PHRS_ACTOR_NAMESPACE.equals(predicate)) {
                Assert.assertEquals(NAME_SPACE, value);
            }
        }
    }
    
    /**
     * Creates a relation between a given : Namespace, PHRS Id and Protocol Id
     * and retreats only the Protocol Id for the given  Namespace and PHRS Id.
     * 
     * @see NameSpaceClient#register(java.lang.String, java.lang.String,
     * java.lang.String)
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testProtocolId() throws TripleException {
         // I register the relation between the name space, phrs id and 
        // protocol id.
       nameSpaceClient.register(NAME_SPACE, PHRS_ID, PROTOCOL_ID);
        
        // I register the relation between the name space, phrs id and 
        // protocol id.
        final String protolId =
                nameSpaceClient.getProtocolId(NAME_SPACE, PHRS_ID);
        Assert.assertEquals(PROTOCOL_ID, protolId);
    }
}
