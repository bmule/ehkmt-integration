package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ActorClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.SchemeClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService;
import at.srfg.kmt.ehealth.phrs.presentation.utils.DynaUtil;
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;
import com.google.code.morphia.query.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Ignore;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsModel;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import org.junit.Before;
import org.junit.Test;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import org.apache.commons.beanutils.DynaBean;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatmentMatrix;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropTermTransformer;
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import org.junit.*;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients;
/*
 * The protocol id for the test patient is 191. It is available both the in the
 * SALK installation and in the "public" test server
 *
 * NOTE: run with sesame REMOTE triple store that is loaded with data or run
 * loadEHR test data
 */
//Ingore - use only for testing against a triple store with data
@Ignore
public class LiveInteropUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveInteropUnitTest.class.getName());
    String PHRS_RESOURCE_CLASS = Constants.PHRS_MEDICATION_CLASS;
    /**
     * The default note used in this test
     */
    public static final String NOTE = "to import";
    //user owner uri
    public static final String SCENARIO_USER = Constants.OWNER_URI_CORE_PORTAL_TEST_USER;
    public static final String SCENARIO_USER_PROTOCOL_ID = Constants.PROTOCOL_ID_UNIT_TEST;
    public static final String USER = SCENARIO_USER;//"MedicationTreatmentInteropUnitTest";
    public static final String USER_PROTOCOL_ID = SCENARIO_USER_PROTOCOL_ID;//"unitTest_MedicationTreatmentInteropUnitTest";
    public static final String PROTOCOL_ID_NAMESPACE = Constants.ICARDEA_DOMAIN_PIX_OID;
    public String doseFrequency;
    public static final String doseFrequenceUriDefault = "http://www.icardea.at/phrs/instances/PerDay";
    public static final String DOSE_TIME_OF_DAY = "http://www.icardea.at/phrs/instances/InTheMorning";
    public static final String DOSE_UNITS = Constants.PILL;//Constants.MILLIGRAM
    public static final String MED_REASON = "http://www.icardea.at/phrs/instances/Cholesterol";
    private GenericTriplestore triplestore;
    private MedicationClient medicationClient;
    private ActorClient actorClient;
    private PhrsStoreClient phrsClient;
    private List<MedicationTreatment> phrResources;
    private InteropAccessService iaccess;
    private InteropProcessor iprocess;
    public static final String DRUG_1_QUANTITY = "2";
    public static final String DRUG_2_QUANTITY = "12";
    public static final String DRUG_1_NAME = "Prednisone";
    public static final String DRUG_2_NAME = "Concor 2";
    public static final String DRUG_1_CODE = "C0032952";
    public static final String DRUG_2_CODE = "C0110591";
    private boolean printDynabean = false;

    public LiveInteropUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException {
        phrResources = new ArrayList<MedicationTreatment>();
    
        //get fresh instance using "true"
        phrsClient = PhrsStoreClient.getInstance(true);
        triplestore = phrsClient.getGenericTriplestore();

     
        iaccess = phrsClient.getInteropService();
        iprocess = new InteropProcessor(phrsClient);

        //get this one, we set the creator differently
        medicationClient = phrsClient.getInteropClients().getMedicationClient();
        doseFrequency = medicationClient.buildNullFrequency();

        //assign actor
        actorClient = phrsClient.getInteropClients().getActorClient();//new ActorClient(triplestore);

        actorClient.register(PROTOCOL_ID_NAMESPACE, Constants.OWNER_URI_CORE_PORTAL_TEST_USER, Constants.PROTOCOL_ID_UNIT_TEST);
        //actorClient.register(Constants.ICARDEA_DOMAIN_PIX_OID, USER, USER_PROTOCOL_ID);

    }

    @After
    public void tearDown() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, Exception {

        /*
         * try { if (phrsClient != null) { Query query =
         * phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri
         * =", USER); phrsClient.getPhrsDatastore().delete(query); } } catch
         * (Exception e) { e.printStackTrace(); }
         *
         */
        //ds=null;
        /*
         * try { if (triplestore != null) { ((GenericTriplestoreLifecycle)
         * triplestore).shutdown(); ((GenericTriplestoreLifecycle)
         * triplestore).cleanEnvironment(); triplestore = null; } if (phrsClient
         * != null) { phrsClient.setTripleStore(null); }
         *
         * } catch (Exception e) { e.printStackTrace();
         *
         * }
         */
    }
    private boolean useLiveData = true;

    public void addNewMessagesEhr(String phrResourceUri, String phrResourceUri2, String drug1_quantity, String drug2_quantity) throws TripleException, IllegalAccessException, InstantiationException {
        if (!useLiveData) {
            medicationClient.setCreator(Constants.EXTERN);//simulate extern


            final String resourceURI_1 =
                    medicationClient.addMedicationSign(
                    USER,
                    //NOTE,
                    phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri : NOTE,
                    Constants.STATUS_COMPELETE,
                    "201006010000",
                    "201006010000",
                    medicationClient.buildNullFrequency(),//FIXXME must use the buildFrequency
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    drug1_quantity,
                    DOSE_UNITS,
                    //label1 != null ? label1 : "EHRDrug1"
                    DRUG_1_NAME,
                    DRUG_1_CODE);

            final String resourceURI_2 =
                    medicationClient.addMedicationSign(
                    USER,
                    phrResourceUri2 != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri2 : NOTE,
                    Constants.STATUS_RUNNING,
                    "201006010000",
                    "201006010000",
                    medicationClient.buildNullFrequency(),//FIXXME use buildFrequency NONE
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    drug2_quantity,
                    DOSE_UNITS,
                    DRUG_2_NAME,
                    DRUG_2_CODE);
        }

        //label2 != null ? label2 : "EHRDrug2" //FIXXME does not create a name code node
        //medicationClient.buildManufacturedProduct(label2 != null ? label2 : "EHRDrug2", DRUG_CODE_2)

        final Iterable<String> uris = medicationClient.getMedicationURIsForUser(USER);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }
        if (printDynabean) {
            for (DynaBean dynaBean : beans) {
                final String toString = DynaBeanUtil.toString(dynaBean);
                System.out.println(toString);
            }
        }


    }

    public String addNewMessageEhr(String phrResourceUri, String drug1_quantity) throws TripleException, IllegalAccessException, InstantiationException {
        String resourceURI_1 = null;
        if (!useLiveData) {
            medicationClient.setCreator(Constants.EXTERN);//simulate extern

            resourceURI_1 =
                    medicationClient.addMedicationSign(
                    USER,
                    phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri : NOTE,
                    Constants.STATUS_COMPELETE,
                    "201006010000",
                    "201006010000",
                    medicationClient.buildNullFrequency(),//FIXXME must use the buildFrequency
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    drug1_quantity,
                    DOSE_UNITS,
                    //label1 != null ? label1 : "EHRDrug1"
                    DRUG_1_NAME,
                    DRUG_1_CODE);
        }
        return resourceURI_1;

    }

    @Test
    @Ignore
    public void testAddNewMessagesEhr() throws TripleException, IllegalAccessException, InstantiationException {

        try {
            final String resourceURI_1 =
                    medicationClient.addMedicationSign(
                    USER,
                    NOTE,
                    Constants.STATUS_COMPELETE,
                    "201006010000",
                    "201006010000",
                    doseFrequency,//FIXXME build
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    DRUG_1_QUANTITY,
                    DOSE_UNITS,
                    DRUG_1_NAME,
                    DRUG_1_CODE);//FIXXME does not create a code node!

            assertNotNull(resourceURI_1);

            final String resourceURI_2 =
                    medicationClient.addMedicationSign(
                    USER,
                    NOTE,
                    Constants.STATUS_RUNNING,
                    "201006010000",
                    "201006010000",
                    doseFrequency,//FIXXME Build
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    DRUG_2_QUANTITY,
                    DOSE_UNITS,
                    DRUG_2_NAME,
                    DRUG_2_CODE);//FIXXME does not create a code node!

            assertNotNull(resourceURI_2);

            final Iterable<String> uris = medicationClient.getMedicationURIsForUser(USER);

            final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
            final Set<DynaBean> beans = new HashSet<DynaBean>();
            int count = 0;
            for (String uri : uris) {
                count++;
                final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
                beans.add(dynaBean);
            }

            assertTrue(count > 1);
            if (printDynabean) {
                for (DynaBean dynaBean : beans) {
                    final String toString = DynaBeanUtil.toString(dynaBean);
                    // System.out.println(toString);
                }
            }

        } catch (TripleException e) {
            fail("TripleException");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            fail("IllegalAccessException");
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
            fail("InstantiationException");
        } catch (java.lang.Error e) {
            System.out.println("testAddNewMessagesEhr java.lang.Error");
            e.printStackTrace();
            fail("java.lang.Error");
        }

        //assertNotNull(resourceURI_1);
        //assertNotNull(resourceURI_2);

    }

    public static MedicationTreatment createPhrResource(String drugLabel,
            Double dosage,
            String doseQuantity) {
        MedicationTreatment res = new MedicationTreatment();

        res.setOwnerUri(USER);
        res.setCreatorUri(USER);
        res.setBeginDate(new Date());
        //res.setEndDate(new Date());
        res.setLabel("phr user drug value is " + drugLabel);

        res.setStatus(Constants.STATUS_RUNNING);
        res.setReasonCode(MED_REASON);

        //res.setReasonCode(value);
        //res.setPrescribedByName("");
        MedicationTreatmentMatrix mtm = new MedicationTreatmentMatrix();

        mtm.setDosage(dosage);//double

        mtm.setDosageQuantity(doseQuantity);
        mtm.setDosageUnits(DOSE_UNITS);

        mtm.setDosageInterval(doseFrequenceUriDefault);
        mtm.setDosageTimeOfDay(DOSE_TIME_OF_DAY);

        res.setTreatmentMatrix(mtm);

        //save simulation
        res.setResourceUri("theResourceUri_" + UUID.randomUUID().toString());
        res.setCreateDate(new Date());
        res.setModifyDate(res.getCreateDate());


        return res;

    }

    @Test
    public void testClientCreatorSetting() {
        String creator = this.medicationClient.getCreator();
        assertEquals("Error Expect PHR creator! ", PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR, creator);

    }

    @Test
    public void testClientCreatorReset() {
        this.medicationClient.setCreator("XYZ");
        String creator = this.medicationClient.getCreator();
        assertEquals("Error Expect PHR creator! ", "XYZ", creator);

    }

//	@Test
//	public void testGetInteropResourceDynabeanByExternalReferenceId() {
//		
//	}
//
    /*
     * @Test public void testParseReferenceNote() {
     * System.out.println("testParseReferenceNote"); String expect = "1234";
     * String x = expect; String result =
     * InteropAccessService.parseReferenceNote(InteropAccessService.REFERENCE_NOTE_PREFIX
     * + x); System.out.println(InteropAccessService.REFERENCE_NOTE_PREFIX + x +
     * " /parsed" + result); assertEquals("reference note", expect, result); }
     */
    @Test
    public void testParseReferenceNote() {
        System.out.println("testParseReferenceNote");

        String expect = "1234";
        String note = InteropProcessor.REFERENCE_NOTE_PREFIX + expect;

        String result = InteropProcessor.parseReferenceNote(note);
        System.out.println(note + " /parsed" + result);
        assertEquals("reference note", expect, result);
    }

    @Test
    public void testFindAllInteropMessagesForUser() throws Exception {

        //addNewMessagesEhr( "123-resuri-567", "5", "14");

        //create EHR records without any reference note for a user
        addNewMessagesEhr(null, null, "5", "14");
        addNewMessagesEhr("xxxxx", null, "11", "22");
        String phrClass = PHRS_RESOURCE_CLASS;
        String user = USER;
        System.out.println("testFindAllInteropMessagesForUser");
        Set<DynaBean> beans = findInteropMessagesForUser(user, phrClass, false);
        int count = 0;
        for (DynaBean result : beans) {
            count++;
            if (printDynabean) {
                System.out.println("all found messages count" + count + " uri=" + result);
            }

        }
        assertTrue("No Interop messages, expect four ", count == 4);


    }

    /**
     * Run this on existing store for the test scenario user For normal junit
     * testing, @Ignore should be applied to this test
     *
     * @throws Exception
     */
    @Test
    public void testFindAllInteropMessagesForUserFromPreload() throws Exception {

        //addNewMessagesEhr( "123-resuri-567", "5", "14");

        //create EHR records without any reference note for a user
        //addNewMessagesEhr(null, null, "5", "14");
        //addNewMessagesEhr("xxxxx", null, "11", "22");
        String phrClass = PHRS_RESOURCE_CLASS;
        String user = SCENARIO_USER;
        System.out.println("testFindAllInteropMessagesForUser");
        Set<DynaBean> beans = findInteropMessagesForUser(user, phrClass, false);
        int count = 0;
        for (DynaBean dynaBean : beans) {
            count++;
            System.out.println(DynaBeanUtil.toString(dynaBean));
        }
        System.out.println("Interop messages found for test user, Total count" + count);
        assertTrue("No Interop messages found, ok or not? ", count > 0);


    }

    @Ignore
    @Test
    public void testIopSetupAndImport() {
        String user = Constants.OWNER_URI_CORE_PORTAL_TEST_USER;
        String phrsClass = Constants.PHRS_MEDICATION_CLASS;
        //private InteropProcessor iprocess;
        //InteropProcessor
        //InteropProcessor
        UserService userService = new UserService(this.phrsClient.getPhrsRepositoryClient());


        InteropProcessor ip = userService.getPhrsStoreClient().getInteropProcessor();

        List<String> list = ip.importNewMessages(user, phrsClass);


    }

    /**
     * Run this on existing store for the test scenario user For normal junit
     * testing, @Ignore should be applied to this test
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void testFindProtocolIdFromPreload() {
        boolean hasTest = false;
        String p1 = null;
        //String user = SCENARIO_USER;
        try {
            //Constants.PHRS_NAMESPACE
            p1 = actorClient.getProtocolId(PROTOCOL_ID_NAMESPACE, Constants.OWNER_URI_CORE_PORTAL_TEST_USER);
            if (p1 != null) {
                hasTest = true;
                System.out.println("Found protocolId for user in icardea namespace protocolId=" + p1 + " namespace=" + PROTOCOL_ID_NAMESPACE);

            }
        } catch (Exception e) {
            //System.out.println("1. Failed to find protocolId for user=" + Constants.OWNER_URI_CORE_PORTAL_TEST_USER + " for namespace=" + PROTOCOL_ID_NAMESPACE);
        }
        System.out.println("Found protocolId for user in icardea namespace protocolId=" + p1 + " namespace=" + PROTOCOL_ID_NAMESPACE);

        if (!hasTest) {
            System.out.println("Failed to find protocolId for user in icardea namespace" + PROTOCOL_ID_NAMESPACE);
        }
        try {
            //Constants.PHRS_NAMESPACE
            p1 = actorClient.getProtocolId(Constants.OWNER_URI_CORE_PORTAL_TEST_USER);
            if (p1 != null) {
                hasTest = true;
                System.out.println("Found protocolId for user in icardea namespace protocolId=" + p1 + " namespace=" + Constants.PHRS_NAMESPACE);

            }
        } catch (Exception e) {
            //System.out.println("2. Failed to find protocolId for user=" + Constants.OWNER_URI_CORE_PORTAL_TEST_USER + " for namespace=" + PROTOCOL_ID_NAMESPACE);
        }
        if (!hasTest) {
            System.out.println("Failed to find protocolId for user in default namespace" + Constants.PHRS_NAMESPACE);
        }
        assertTrue("Did not find protocol ID for user", hasTest);

    }

    public Set findInteropMessagesForUser(String user, String phrsClass, boolean onlyNewMessages) {
        return iprocess.findInteropMessagesForUser(user, phrsClass, onlyNewMessages);
    }

    @Ignore
    @Test
    public void testLoadSampleTestDataSet() {
        System.out.println("testLoadSampleTestDataSet");
        CoreTestData core = new CoreTestData(this.phrsClient);
        String dateStr = CoreTestData.makeDateLabelForTitle("dummy_unittest_");


        String dateLabel = "dummy_unittest_" + dateStr;
        String owner = dateLabel;

        int count = loadSampleTestDataSet(owner);
        Set<DynaBean> beans = core.getDynaBeans(owner);
        assertNotNull("EHR samples failed null  query on user= " + owner + "", beans);
        assertEquals("expected list size", 8, beans.size());


    }
// +++++++++++++++ start helpers

    public CommonDao getCommonDao() {
        return getPhrsStoreClient().getCommonDao();
    }

    public InteropClients getInteropClients() {
        return getPhrsStoreClient().getInteropClients();
    }

    public void sendMessages(Object repositoryObject) {
        this.iaccess.sendMessages(repositoryObject);
    }

    public int loadSampleTestDataSet(String owner) {

        CoreTestData core = new CoreTestData(this.phrsClient);
        //return count
        return core.addTestMedications_2_forPortalTestForOwnerUri(owner);

    }

    public PhrsStoreClient getPhrsStoreClient() {
        return phrsClient;
    }

    @Ignore
    @Test
    public void testFindReferenceTagExisting() throws Exception {
        String theParentId = "resUri1234";

        //String message_1 = addNewMessageEhr(theParentId, "55");
        //String message_2 = addNewMessageEhr(null, "44");
        String interopRef = iprocess.findMessageWithReference(USER, theParentId, Constants.PHRS_MEDICATION_CLASS, null);

        assertNotNull("Message not found", interopRef);
        //assertEquals("Did not find resource reference ", message_1, interopRef);
    }

    @Ignore
    @Test
    public void testFindReferenceTagNotExisting() throws Exception {
        String theParentId = "resUri1234";
        //put message with a different reference and one with null
        //String message_1 = addNewMessageEhr("xxxxxx", "111");
        //String message_2 = addNewMessageEhr(null, "222");

        String interopRef = iprocess.findMessageWithReference(USER, theParentId, Constants.PHRS_MEDICATION_CLASS, null);
        assertNull("expected no message result, found: ", interopRef);

    }

    @Ignore
    @Test
    public void testFindReferenceTagNull() throws Exception {
        String theParentId = "resUri1234";
        //String message_1 = addNewMessageEhr(null, "777");
        //String message_2 = addNewMessageEhr(null, "888");

        String interopRef = iprocess.findMessageWithReference(USER, theParentId, Constants.PHRS_MEDICATION_CLASS, null);
        assertNull("expected null interopRef, found: ", interopRef);
        //assertEquals("Did not find resource reference ",theParentId,interopRef);
    }

    @Test
    public void testFindAllInteropMessagesForUserFromLiveData() throws Exception {

        //addNewMessagesEhr( "123-resuri-567", "5", "14");

        //create EHR records without any reference note for a user

        String phrClass = PHRS_RESOURCE_CLASS;
        String user = USER;
        System.out.println("testFindAllInteropMessagesForUserFromLiveData");
        Set<DynaBean> beans = findInteropMessagesForUser(user, phrClass, false);
        int count = 0;
        for (DynaBean result : beans) {
            count++;
            if (printDynabean) {
                System.out.println("all found messages count" + count + " uri=" + result);
            }

        }
        System.out.println("testFindAllInteropMessagesForUserFromLiveData found messages count=" + count);
        assertTrue("No Interop messages, expect many ", count > 0);


    }

    @Test
    public void testFindNewInteropMessagesForUserFromLiveData() throws Exception {

        //addNewMessagesEhr( "123-resuri-567", "5", "14");

        //create EHR records without any reference note for a user

        String phrClass = PHRS_RESOURCE_CLASS;
        String user = USER;
        System.out.println("testFindNewInteropMessagesForUserFromLiveData");
        Set<DynaBean> beans = findInteropMessagesForUser(user, phrClass, true);
        int count = 0;
        for (DynaBean result : beans) {
            count++;
            if (printDynabean) {
                System.out.println("all found " + count + " uri=" + result);
            }

        }
        System.out.println("testFindNewInteropMessagesForUserFromLiveData found messages count=" + count);
        assertTrue("No Interop messages, expect many ", count > 0);


    }

    @Test
    public void testSchemaClient() {
        //schemeClient
        SchemeClient schemeClient = this.getInteropClients().getSchemeClient();
        Set<String> properties = null;
        System.out.println("part1");
        try {
            properties = schemeClient.getAllPropertiesForClass(PHRS_RESOURCE_CLASS);
            for (String property : properties) {
                System.out.println("found property=" + property);
            }
        } catch (Exception e) {
            System.out.println("part1 " + e);
        }
        System.out.println("part2");
        try {
            for (String property : properties) {
                boolean exist = schemeClient.propertyExists(property);
                System.out.println("exists=" + exist + " :property= " + property);
            }
        } catch (Exception e) {
            System.out.println("part2 " + e);
        }

        boolean noteExists = false;
        try {
            noteExists = schemeClient.propertyExists(Constants.SKOS_NOTE);
        } catch (Exception e) {
            System.out.println("noteExists error " + e);
        }
        assertTrue("skos note property does not exist! ", noteExists);
//"http://www.icardea.at/phrs/hl7V3#note"

    }

    @Test
    public void testImportNewMessagesFromLiveDate() throws Exception {
        System.out.println("testImportNewMessagesFromLiveDate");
        // addNewMessagesEhr(null, null, "4", "8");//an EHR record
        //String message_1 = addNewMessageEhr(null, "4");
        //String message_2 = addNewMessageEhr(null, "8");

        boolean importMessage = true;//false;
        List phrResources = new ArrayList();
//import is FALSE
        List<String> imported = iprocess.importNewMessages(USER, PHRS_RESOURCE_CLASS, importMessage, phrResources);
        //List<String> imported = importNewMessages(USER, PHRS_RESOURCE_CLASS, importMessage, phrResources);
        assertNotNull(imported);
        assertTrue("Found 0, expect more", imported.size() > 1);

        assertNotNull("No phrResources created ", phrResources);
        assertTrue("No phrResources created ", phrResources.size() > 0);

        String matchName1 = null;
        String matchName2 = null;
        String resUri1 = null;
        //String resUri2 = null;
        for (Object obj : phrResources) {
            MedicationTreatment mt = (MedicationTreatment) obj;
            String title = mt.getTitle();
            String drugCode = mt.getProductCode();
            String drugQuantity = mt.getTreatmentMatrix().getDosageQuantity();
            // if (resUri2 == null && resUri1 != null) resUri2 = mt.getResourceUri();


            //set one for a test
            if (resUri1 == null) {
                resUri1 = mt.getResourceUri();
                System.out.println("created in memory resUri 1=" + resUri1);
            }

        }

        //assertEquals("Bad drug name message 1", DRUG_1_NAME, matchName1);
        //assertEquals("Bad drug name message 2", DRUG_2_NAME, matchName2);

        assertNotNull("created PHR object resUri1 uri is null", resUri1);
        //assertNotNull("created PHR object resUri1 uri is null", resUri2);
        //find marked messages
        String interopRef1 = iprocess.findMessageWithReference(USER, resUri1, Constants.PHRS_MEDICATION_CLASS, null);
        assertNotNull("Message not found", interopRef1);
        //assertEquals("Did not find resource reference for message_1 ", message_1, interopRef1);


        //       String interopRef2 = iprocess.findMessageWithReference(USER, resUri2, Constants.PHRS_MEDICATION_CLASS, null);
        //       assertNotNull("Message not found", interopRef2);
        //assertEquals("Did not find resource reference for message_2 ", message_2, interopRef2);

        List phrObs = getCommonDao().getPhrsRepositoryClient().crudReadResources(USER, MedicationTreatment.class);
        assertNotNull(phrObs);
        assertTrue("Empty PHR object results from DB ", phrObs.size() > 0);

        for (Object obj : phrObs) {
            MedicationTreatment mt = (MedicationTreatment) obj;
            String title = mt.getTitle();
            String drugCode = mt.getProductCode();
            String drugQuantity = mt.getTreatmentMatrix().getDosageQuantity();
            System.out.println("title=" + title + " drugCode=" + drugCode + " drugQuantity=" + drugQuantity);
        }

        // DRUG_1_NAME = "Prednisone";
        // DRUG_2_NAME = "Concor 2";
    }
}
