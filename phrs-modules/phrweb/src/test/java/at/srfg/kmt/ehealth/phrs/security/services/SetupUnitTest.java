/*
 * Project :iCardea
 * File : MedicationClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 31, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import com.google.code.morphia.query.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import org.junit.Before;
import org.junit.Test;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatmentMatrix;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;


public class SetupUnitTest {

    String PHRS_RESOURCE_CLASS = Constants.PHRS_MEDICATION_CLASS;
    /**
     * The note used in this test
     */
    public static final String NOTE = "to import";
    public static final String USER = "unittest123";//MedicationClientUnitTest.class.getName();
    public static final String DOSE_INTERVAL = "http://www.icardea.at/phrs/instances/EveryHour";
    public static final String DOSE_TIME_OF_DAY = "http://www.icardea.at/phrs/instances/InTheMorning";
    public static final String DOSE_UNITS = "http://www.icardea.at/phrs/instances/pills";
    public static final String MED_REASON = "http://www.icardea.at/phrs/instances/Cholesterol";
    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>TermClientUnitTest</code>.
     *
     * private static final Logger LOGGER =
     * LoggerFactory.getLogger(MedicationClientUnitTest.class);
     */
    private GenericTriplestore triplestore;
    private MedicationClient medicationClient;
    //private  Datastore ds;
    PhrsStoreClient phrsClient;
    private List<MedicationTreatment> phrResources;
    private MedicationTreatment phrMed_1;
    private MedicationTreatment phrMed_2;
    private InteropAccessService iaccess;

    public SetupUnitTest() {
    }

    /*
     * if BeforeClass r required to be static
     */
    @Before
    public void initSuite() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException {
        phrResources = new ArrayList<MedicationTreatment>();
        phrMed_1 = createPhrResource("test in phr_ drug_1", 2.0d, "3");//"http://www.icardea.at/phrs/instances/medfreq_2_time");
        phrMed_2 = createPhrResource("test_in phr_ drug_2", 4.0d, "4");
        phrResources.add(phrMed_1);
        phrResources.add(phrMed_2);
        /*
         * TriplestoreConnectionFactory connectionFactory =
         * TriplestoreConnectionFactory.getInstance(); This is not closed, etc.
         * Use PhrsStoreClient triple store triplestore =
         * connectionFactory.getTriplestore();
         */
        //get fresh instance using "true"
        System.out.println("initSuite");
        phrsClient = PhrsStoreClient.getInstance(true);
        triplestore = phrsClient.getGenericTriplestore();

        //ds = phrsClient.getPhrsDatastore();
        //triplestore = PhrsStoreClient.getInstance().getTripleStore();
        iaccess = phrsClient.getInteropService();
        medicationClient = new MedicationClient(triplestore);


        
    }

    /**
     * Runs before any test from this suite and prepare the environment for the
     * next running test.
     *
     * @throws GenericRepositoryException if this occurs then the test
     * environment may be wrong set.
     *
     * @Before public void initSuite() throws GenericRepositoryException,
     * TripleException, IllegalAccessException, InstantiationException { //final
     * TriplestoreConnectionFactory connectionFactory = //
     * TriplestoreConnectionFactory.getInstance(); //triplestore =
     * connectionFactory.getTriplestore(); //triplestore =
     * PhrsStoreClient.getInstance(triplestore).getTripleStore(); //iaccess =
     * PhrsStoreClient.getInstance(triplestore).getInteropService();
     * //connectionFactory.getTriplestore() }
     */
//AfterClass
    @After
    public void shutdownSuite() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, Exception {
        System.out.println("shutdownSuite");

        try {
            //clean up 

            Query query = phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri =", USER);
            phrsClient.getPhrsDatastore().delete(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ds=null;
        try {
            System.out.println("shutdown triplestore");
            ((GenericTriplestoreLifecycle) phrsClient.getGenericTriplestore()).shutdown();
            ((GenericTriplestoreLifecycle) phrsClient.getGenericTriplestore()).cleanEnvironment();
            triplestore = null;

            /*
             * System.out.println("shutdown triplestore");
             * ((GenericTriplestoreLifecycle) triplestore).shutdown();
             * ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
             * triplestore = null;
             */
        } catch (Exception e) {
            e.printStackTrace();
            // throw new Exception();
        }
        System.out.println("end    shutdownSuite");
    }
 

    @Test
    public void testEmptyMedicationsForOwner() throws TripleException {
        System.out.println("SETUP testEmptyMedicationsForOwner");
        /*
         * final String resourceURI = medicationClient.addMedicationSign( USER,
         * NOTE, Constants.STATUS_COMPELETE, "201006010000", "201006010000",
         * "MyFreqency", Constants.HL7V3_ORAL_ADMINISTRATION, "1", "pillURI",
         * "testaddDrug"); assertNotNull(resourceURI);
         */

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


        // the medication has 13 tripels, see the documentaion for VitalSignClient
        assertEquals(0, count);

    }
        public static MedicationTreatment createPhrResource(String drugLabel,
            Double dosage,
            String doseQuantity) {
        MedicationTreatment res = new MedicationTreatment();

        res.setOwnerUri(USER);
        res.setCreatorUri(USER);
        res.setLabel("phr user drug value is " + drugLabel);

        res.setStatus(Constants.STATUS_RUNNING);
        res.setReasonCode(MED_REASON);

        //res.setReasonCode(value);
        //res.setPrescribedByName("");
        MedicationTreatmentMatrix mtm = new MedicationTreatmentMatrix();

        mtm.setDosage(dosage);//double

        mtm.setDosageQuantity(doseQuantity);
        mtm.setDosageUnits(DOSE_UNITS);

        mtm.setDosageInterval(DOSE_INTERVAL);
        mtm.setDosageTimeOfDay(DOSE_TIME_OF_DAY);

        res.setTreatmentMatrix(mtm);


        return res;

    }
}
