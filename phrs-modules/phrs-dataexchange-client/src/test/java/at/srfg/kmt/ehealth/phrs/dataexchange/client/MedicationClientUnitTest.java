/*
 * Project :iCardea
 * File : MedicationClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 31, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.*;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to prove the functionality for the
 * <code>MedicationClient</code>.
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 * @see MedicationClient
 */
public class MedicationClientUnitTest {

    /**
     * The note used in this test
     */
    public static final String NOTE = "Free text note for medication.";

    private static final String USER = MedicationClientUnitTest.class.getName();

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>TermClientUnitTest</code>.
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

    /**
     * Adds a single medication and prove if this was done properly.
     *
     * @throws TripleException if this exception occurs then this test fails.
     */
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
                "MyDrug",
                "MyDrugCode");
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
        expectedRootId.add(Constants.MEDICATION_NORMAL_DOSING);
        assertEquals(expectedRootId, rootIds);

        // the medication has 13 tripels, see the documentaion for Medication
        assertEquals(13, count);

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

    /**
     * Registers a medication and after this it updates the dosage for the
     * registered medication.
     *
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testUpdateMedicationDosageOwner() throws TripleException {
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
                Constants.PILL,
                "MyDrug",
                "MyDrugCode");
        assertNotNull(resourceURI);

        // create a new dosage
        final String newDossageValue = "1.2";
        final String newDosageUnit = Constants.PILL;
        final String newDosageURI = medicationClient.buildDosage(newDossageValue, newDosageUnit);
        assertNotNull(newDosageURI);

        // update the existentn dosage
        medicationClient.updateMedication(resourceURI,
                Constants.HL7V3_DOSAGE,
                newDosageURI);

        final Iterable<String> medicationURIForUser =
                medicationClient.getMedicationURIsForUser(USER);
        final Iterator<String> iterator = medicationURIForUser.iterator();
        // I only have one medication
        assertTrue(iterator.hasNext());
        final String medicationURI = iterator.next();
        final Iterable<String> dosageURIs =
                triplestore.getForSubjectAndPredicate(medicationURI, Constants.HL7V3_DOSAGE);
        
        proveDosage(dosageURIs.iterator().next(), newDossageValue, newDosageUnit);
    }

    /**
     * Proves id the resource identified after its URI is a PHR Medication
     * Dosage and it proves also if its arguments are the expected one.
     *
     * @param dossageURI the URI for the PHR Medication Dosage.
     * @param value the expected value for the PHR Medication Dosage.
     * @param unit the expected unit for the PHR Medication Dosage.
     * @throws TripleException if this exception occurs then this test fails.
     */
    private void proveDosage(String dossageURI, String value, String unit)
            throws TripleException {

        if (dossageURI == null || value == null || unit == null) {
            throw new NullPointerException("None of the proveDosage methods argumetns ca be null.");
        }

        final Iterable<Triple> dosageTriples = triplestore.getForSubject(dossageURI);
        for (Triple triple : dosageTriples) {
            final String predicate = triple.getPredicate();
            final String val = triple.getValue();

            if (Constants.RDFS_TYPE.equals(predicate)) {
                val.equals(Constants.PHRS_MEDICATION_DOSAGE_CLASS);
            }

            if (Constants.HL7V3_DOSAGE_VALUE.equals(predicate)) {
                assertEquals(value, val);
            }

            if (Constants.HL7V3_DOSAGE_UNIT.equals(predicate)) {
                assertEquals(unit, val);
            }
        }
    }

    /**
     * Deletes a medication and proves is the delete was done properly.
     *
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testDeleteMedication() throws TripleException {
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
                "MyDrug",
                "MyDrugCode");
        assertNotNull(resourceURI);

        medicationClient.deleteMedication(resourceURI);
        final Iterable<Triple> medicationTriples = medicationClient.getMedicationTriples();
        int counter = 0;
        for (Triple triple : medicationTriples) {
            counter++;
        }

        assertEquals(0, counter);

    }
}
