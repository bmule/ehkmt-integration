/*
 * Project :iCardea
 * File : ProbmemClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 24, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import static at.srfg.kmt.ehealth.phrs.Constants.*;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;

import java.util.HashSet;
import java.util.Set;
import org.junit.After;
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
public class ProbmemClientUnitTest {

    public static final String NOTE = "Free text note for the problem.";
    public static final String START_DATE = "201006010000";
    public static final String END_DATE = "201007010000";
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>ProbmemClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProbmemClientUnitTest.class);
    private static final String USER = ProbmemClientUnitTest.class.getName();
    private GenericTriplestore triplestore;
    private ProblemEntryClient problemClient;

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
        problemClient = new ProblemEntryClient(triplestore);
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
     * Adds a problem entry  for an owner and tests if all the properties for the 
     * vial sign was properly set.
     * 
     * @throws TripleException by any triplestore related exeception. 
     * If this exception occurs then this test fails.
     * @see ProblemEntryClient#addProblemEntry(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String) 
     */
    @Test
    public void testAddProblemEntryForOwner() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                USER,
                HL7V3_SYMPTOM,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);

        final Iterable<Triple> observations = problemClient.getProblemEntryTriplesForUser(USER);
        int count = 0;
        Set<String> rootIds = new HashSet<String>();
        for (Triple problemEnrty : observations) {
            final String predicate = problemEnrty.getPredicate();
            final String value = problemEnrty.getValue();
            if (predicate.equals(OWNER)) {
                assertEquals(USER, value);
            }

            if (predicate.equals(RDFS_TYPE)) {
                assertEquals(PHRS_OBSERVATION_ENTRY_CLASS, value);
            }

            if (predicate.equals(CREATE_DATE)) {
                assertNotNull(value);
            }

            if (predicate.equals(HL7V3_TEMPLATE_ID_ROOT)) {
                rootIds.add(value);
            }

            if (predicate.equals(HL7V3_CODE)) {
                assertEquals(HL7V3_SYMPTOM, value);
            }

            if (predicate.equals(HL7V3_STATUS)) {
                assertEquals(STATUS_COMPELETE, value);
            }

            if (predicate.equals(HL7V3_START_DATE)) {
                assertEquals(START_DATE, value);
            }

            if (predicate.equals(HL7V3_END_DATE)) {
                assertEquals(END_DATE, value);
            }

            if (predicate.equals(SKOS_NOTE)) {
                assertEquals(NOTE, value);
            }

            if (predicate.equals(HL7V3_VALUE_CODE)) {
                assertEquals(HL7V3_FEVER, value);
            }

            count++;
        }

        final Set<String> expectedRootId = new HashSet<String>();
        // all this three describes an HL7 V3 problem entry
        expectedRootId.add(PROBLEM_ENTRY);
        expectedRootId.add(PROBLEM_OBSERVATION);
        assertEquals(expectedRootId, rootIds);

        // the vial sign has 12 tripels, see the documentaion for problemClient
        assertEquals(12, count);

        final boolean exists = triplestore.exists(resourceURI);
        assertTrue(exists);
        final Iterable<String> forOwner =
                triplestore.getForPredicateAndValue(OWNER, USER, ValueType.LITERAL);

        count = 0;
        for (String resource : forOwner) {
            count++;
        }

        // I expect only one problem entry  for this owner
        assertEquals(1, count);
    }

    /**
     * Updates a properties for a given problem entry and prove is this operation 
     * was successfully.
     * 
     * @throws TripleException by any triplestore related exeception. 
     * If this exception occurs then this test fails.
     * @see ProblemEntryClient#updateProblemEntry(java.lang.String, java.lang.String, java.lang.String) 
     */
    @Test
    public void testUpdateProblemEnrtyForOwner() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                USER,
                HL7V3_SYMPTOM,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);

        assertNotNull(resourceURI);
        // this update only two properties the HL7V3_VALUE_CODE and the UPDATE_DATE,
        // the problemClient set the UPDATE_DATE (to the cuurent date) efery 
        // time when a resource get updated.
        problemClient.updateProblemEntry(resourceURI, HL7V3_VALUE_CODE, HL7V3_PALPITATION);

        final Iterable<Triple> properties = problemClient.getProblemEntryTriplesForUser(USER);
        Set<String> rootIds = new HashSet<String>();
        for (Triple triple : properties) {

            final String predicate = triple.getPredicate();
            final String value = triple.getValue();

            if (predicate.equals(OWNER)) {
                assertEquals(USER, value);
            }

            if (predicate.equals(RDFS_TYPE)) {
                assertEquals(PHRS_OBSERVATION_ENTRY_CLASS, value);
            }

            if (predicate.equals(CREATE_DATE)) {
                assertNotNull(value);
            }

            if (predicate.equals(HL7V3_TEMPLATE_ID_ROOT)) {
                rootIds.add(value);
            }

            if (predicate.equals(HL7V3_CODE)) {
                assertEquals(HL7V3_SYMPTOM, value);
            }

            if (predicate.equals(HL7V3_STATUS)) {
                assertEquals(STATUS_COMPELETE, value);
            }

            if (predicate.equals(HL7V3_START_DATE)) {
                assertEquals(START_DATE, value);
            }

            if (predicate.equals(HL7V3_END_DATE)) {
                assertEquals(END_DATE, value);
            }

            if (predicate.equals(SKOS_NOTE)) {
                assertEquals(NOTE, value);
            }

            if (predicate.equals(HL7V3_VALUE_CODE)) {
                assertEquals(HL7V3_PALPITATION, value);
            }
        }

        final Set<String> expectedRootId = new HashSet<String>();
        // all this three describes an HL7 V3 problem entry
        expectedRootId.add(PROBLEM_ENTRY);
        expectedRootId.add(PROBLEM_OBSERVATION);
        assertEquals(expectedRootId, rootIds);
    }

    @Test
    public void testDeleteProblemEntry() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                OWNER,
                HL7V3_SYMPTOM,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);

        problemClient.deleteProblemEntry(resourceURI);
        int counter = 0;
        final Iterable<Triple> triples = problemClient.getProblemEntryTriples();
        for (Triple triple : triples) {
            counter++;
        }

        assertEquals(0, counter);
    }

    @Test
    public void testAddProblemEntry_Condition() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                OWNER,
                HL7V3_CONDITION,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);
    }

    @Test
    public void testAddProblemEntry_Symptom() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                OWNER,
                HL7V3_SYMPTOM,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);

    }

    @Test
    public void testAddProblemEntry_Finding() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                OWNER,
                HL7V3_FINDING,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);
    }

    @Test
    public void testAddProblemEntry_Complaint() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                OWNER,
                HL7V3_COMPILANT,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);

    }

    @Test
    public void testAddProblemEntry_FunctionalLimitation() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                OWNER,
                HL7V3_FUNCTIONAL_LIMITATION,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);

    }

    @Test
    public void testAddProblemEntry_Problem() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                OWNER,
                HL7V3_PROBLEM,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);

    }

    @Test
    public void testAddProblemEntry_Diagnosis() throws TripleException {
        final String resourceURI =
                problemClient.addProblemEntry(
                OWNER,
                HL7V3_DIAGNOSIS,
                STATUS_COMPELETE,
                START_DATE,
                END_DATE,
                NOTE,
                HL7V3_FEVER);
        assertNotNull(resourceURI);
    }

    @Test
    public void testAddProblemEntry_Risk() throws TripleException {
        // I am not sure about this
    }
}
