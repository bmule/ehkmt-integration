
package at.srfg.kmt.ehealth.phrs.security.services;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ActorClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test suite does some basic tests (persist and retrieve) with the
 * <code>ActorClient</code>.
 *
 * @author m1s
 * @version 0.1
 * @since 0.1
 * @see ActorClient
 */

public class ActorClientUnitTest {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.ActorClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ActorClientUnitTest.class);

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
     * <code>ActorClient</code> to be tested.
     */
    private ActorClient actorClient;

    /**
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     *
     * @throws GenericRepositoryException if this occurs then the test
     * environment may be wrong set.
     */
    @Before
    public void initSuite() throws GenericRepositoryException, TripleException {
        //final TriplestoreConnectionFactory connectionFactory =
        //        TriplestoreConnectionFactory.getInstance();
        triplestore = triplestore = PhrsStoreClient.getInstance().getTripleStore();//connectionFactory.getTriplestore();
        actorClient = new ActorClient(triplestore);
    }

    /**
     * Register a relation between : a name space, a PHRS id and a Protocol id
     * and proves if the result was persisted properly.
     *
     * @see ActorClient#register(java.lang.String, java.lang.String,
     * java.lang.String)
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testRegisters() throws TripleException {

        final String registerActor = registerActor();

        final Iterable<Triple> forSubject = triplestore.getForSubject(registerActor);
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
                final String creator = ActorClient.class.getName();
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
     * Creates a relation between a given : Name-Space, PHRS Id and Protocol Id
     * and retreats only the Protocol Id for the given Namespace and PHRS Id.
     *
     * @see ActorClient#register(java.lang.String, java.lang.String,
     * java.lang.String)
     * @see ActorClient#getProtocolId(java.lang.String, java.lang.String)
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testProtocolId() throws TripleException {
        registerActor();

        // I register the relation between the name space, phrs id and 
        // protocol id.
        final String protolId =
                actorClient.getProtocolId(NAME_SPACE, PHRS_ID);
        Assert.assertEquals(PROTOCOL_ID, protolId);
    }

    /**
     * Tries to obtain a protocol id for a wrong (unregister) Name-Space, PHRS
     * Id and Protocol Id.
     *
     * @see ActorClient#register(java.lang.String, java.lang.String,
     * java.lang.String)
     * @see ActorClient#getProtocolId(java.lang.String, java.lang.String)
     * @see ActorClient#getProtocolId(java.lang.String)
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testGetProtocolIdForWrongActors() throws TripleException {
        registerActor();

        final String subfix = getRandomString();

        final String protolIdForNsandPHRId =
                actorClient.getProtocolId(NAME_SPACE + subfix, PHRS_ID + subfix);
        // I am sure that there is no relation between the upper arguments
        // (I use random string)
        Assert.assertNull(protolIdForNsandPHRId);


        final String protolId =
                actorClient.getProtocolId(PHRS_ID + subfix);
        // I am sure that there is no relation between the upper arguments
        // (I use random string)
        Assert.assertNull(protolId);
    }

    /**
     * Creates a relation between a given : Namespace, PHRS Id and Protocol Id
     * and prove its validity using all the existing
     * <code>xxxExist</code> methods. This test is the antonym for the
     * <code>testProtocolIdIsNotExisting</code>.
     *
     * @throws TripleException if this exception occurs then this test fails.
     * @see ActorClient#register(java.lang.String, java.lang.String)
     * @see ActorClient#exist(java.lang.String, java.lang.String,
     * java.lang.String)
     * @see ActorClient#protocolIdExist(java.lang.String, java.lang.String)
     * @see ActorClient#protocolIdExist(java.lang.String)
     * @see #testProtocolIdIsNotExisting()
     */
    @Test
    public void testProtocolIdExists() throws TripleException {
        registerActor();

        final boolean existNameSpaceAndPHRSId =
                actorClient.protocolIdExist(NAME_SPACE, PHRS_ID);
        Assert.assertTrue(existNameSpaceAndPHRSId);

        final boolean exist =
                actorClient.protocolIdExist(PROTOCOL_ID);
        Assert.assertTrue(exist);
    }

    /**
     * Proves its validity for some not existent actors using all the existing
     * <code>xxxExist</code> methods. This test is the antonym for the
     * <code>testProtocolIdExists</code>.
     *
     * @throws TripleException if this exception occurs then this test fails.
     * @see ActorClient#register(java.lang.String, java.lang.String)
     * @see ActorClient#exist(java.lang.String, java.lang.String,
     * java.lang.String)
     * @see ActorClient#protocolIdExist(java.lang.String, java.lang.String)
     * @see ActorClient#protocolIdExist(java.lang.String)
     * @see #testProtocolIdExists()
     */
    @Test
    public void testProtocolIdIsNotExisting() throws TripleException {
        final String subfix = getRandomString();
        final String namespace = NAME_SPACE + subfix;
        final String phrsId = PHRS_ID + subfix;
        final String protocolId = PROTOCOL_ID + subfix;

        final boolean existAll =
                actorClient.exist(namespace, phrsId, protocolId);
        // this relation can not exist
        Assert.assertFalse(existAll);

        final boolean existNameSpaceAndPHRSId =
                actorClient.protocolIdExist(namespace, phrsId);
        Assert.assertFalse(existNameSpaceAndPHRSId);

        final boolean exist =
                actorClient.protocolIdExist(protocolId);
        Assert.assertFalse(exist);
    }

    /**
     * Generates a random (hex)string with at least 4 digits.
     *
     * @return a random (hex)string with at least 4 digits.
     */
    private String getRandomString() {
        final double random = Math.random() * 10000;
        final String subfix = Double.toHexString(random);
        return subfix;
    }

    /**
     * Registers a actor, remove it and prove if the remove action was
     * successfully. The remove is applied on all actors that have a given
     * Name-Space and PHR System id.
     *
     * @throws TripleException if this exception occurs then this test fails.
     * @see ActorClient#removeProtocolIds(java.lang.String, java.lang.String)
     */
    @Test
    public void testRemoveProtocolIdForNsAndPHRsID() throws TripleException {
        registerActor();

        final Set<String> removeProtoclIds =
                actorClient.removeProtocolIds(NAME_SPACE, PHRS_ID);
        // I register a singular actor, the result set has only one element.
        Assert.assertEquals(1, removeProtoclIds.size());

        // the result set must contain only one  protocl id (because I register
        // only one actor).
        Assert.assertTrue(removeProtoclIds.contains(PROTOCOL_ID));

        // this actor was removed so this must return false.
        final boolean existAll =
                actorClient.exist(NAME_SPACE, PHRS_ID, PROTOCOL_ID);
        Assert.assertFalse(existAll);
    }

    /**
     * Registers an actor (using the NAME_SPACE, PHRS_ID, PROTOCOL_ID constants)
     * and prove if the registration was done correct.
     *
     * @return the URI for the new actor.
     * @throws TripleException by any predicate calculus related errors.
     */
    private String registerActor() throws TripleException {
        final String register =
                actorClient.register(NAME_SPACE, PHRS_ID, PROTOCOL_ID);

        // the result can not be null
        Assert.assertNotNull(register);

        final boolean existAll =
                actorClient.exist(NAME_SPACE, PHRS_ID, PROTOCOL_ID);
        Assert.assertTrue(existAll);

        return register;
    }

    /**
     * Registers a actor, remove it and prove if the remove action was
     * successfully. The remove is applied on all actors that have a given PHR
     * System id.
     *
     * @throws TripleException if this exception occurs then this test fails.
     * @see ActorClient#removeProtocolIds(java.lang.String)
     */
    @Test
    public void testRemoveProtocolIdForNs() throws TripleException {
        registerActor();

        final Set<String> removeProtoclIds =
                actorClient.removeProtocolIds(PHRS_ID);
        // I register a singular actor, the result set has only one element.
        Assert.assertEquals(1, removeProtoclIds.size());

        // the result set must contain only one  protocl id (because I register
        // only one actor).
        Assert.assertTrue(removeProtoclIds.contains(PROTOCOL_ID));

        // this actor was removed so this must return false.
        final boolean existAll =
                actorClient.exist(NAME_SPACE, PHRS_ID, PROTOCOL_ID);
        Assert.assertFalse(existAll);
    }

    /**
     * Repetitive registers the same actor and it proves if only one instance
     * of the actor exists. Because the a
     * 
     * 
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testRepetativeAdd() throws TripleException {
        for (int i = 0; i < 10; i++) {
            registerActor();
        }
        
        // only one actor must be registered
        final Set<String> protocolId = 
                actorClient.getProtocolIdsInNamespace(NAME_SPACE);
        Assert.assertEquals(1, protocolId.size());
    }
}
