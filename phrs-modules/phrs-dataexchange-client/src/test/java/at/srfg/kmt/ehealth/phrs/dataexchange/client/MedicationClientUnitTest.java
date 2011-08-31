/*
 * Project :iCardea
 * File : MedicationClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 31, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mradules
 */
public class MedicationClientUnitTest {

    public static final String NOTE = "Free text note for medication.";
    private static final String USER = MedicationClientUnitTest.class.getName();
    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>TermClientUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VitalSignClientUnitTest.class);
    private GenericTriplestore triplestore;
    private MedicationClient medicationClient;

    public MedicationClientUnitTest() {
    }

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
        medicationClient = new MedicationClient(triplestore);
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
    public void testAddMedicationForOwner() throws TripleException {
        final String resourceURI =
                medicationClient.addMedicationSign(
                USER,
                NOTE,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                "MyFreqency",
                Constants.HL7V3_ORAL_ADMINISTRATION,
                "1",
                "pillURI",
                "MyDrug");
        assertNotNull(resourceURI);

        final Iterable<Triple> vitalSigns =
                medicationClient.getMedicationTriplesForUser(USER);
        int count = 0;
        Set<String> rootIds = new HashSet<String>();
        for (Triple vitalSign : vitalSigns) {

            final String predicate = vitalSign.getPredicate();
            final String value = vitalSign.getValue();
            if (predicate.equals(Constants.OWNER)) {
                assertEquals(USER, value);
            }

            if (predicate.equals(Constants.RDFS_TYPE)) {
                assertEquals(Constants.PHRS_MEDICATION_CLASS, value);
            }

            if (predicate.equals(Constants.CREATE_DATE)) {
                assertNotNull(value);
            }

            if (predicate.equals(Constants.HL7V3_TEMPLATE_ID_ROOT)) {
                rootIds.add(value);
            }



            if (predicate.equals(Constants.SKOS_NOTE)) {
                assertEquals(NOTE, value);
            }

            count++;
        }

        final Set<String> expectedRootId = new HashSet<String>();
        // all this three describes a medication
        expectedRootId.add(Constants.IMUNISATION);
        expectedRootId.add(Constants.MEDICATION);
        assertEquals(expectedRootId, rootIds);

        // the medication has 13 tripels, see the documentaion for VitalSignClient
        assertEquals(14, count);
        
                final boolean exists = triplestore.exists(resourceURI);
        assertTrue(exists);
        final Iterable<String> forOwner =
                triplestore.getForPredicateAndValue(Constants.OWNER, USER, ValueType.LITERAL);

        count = 0;
        for (String resource : forOwner) {
            count++;
        }

        // I expect only one vital sign for this owner
        assertEquals(1, count);

    }
}
