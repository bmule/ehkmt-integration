package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ActorClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsModel;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatmentMatrix;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropTermTransformer;
import at.srfg.kmt.ehealth.phrs.presentation.utils.DynaUtil;
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;
import com.google.code.morphia.query.Query;
import java.util.*;
import org.apache.commons.beanutils.DynaBean;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * The protocol id for the test patient is 191. It is available both the in the
 * SALK installation and in the "public" test server RUN these TESTS with a
 * local file based sesame store not the REMOTE triplestore
 */

public class PhrsClientPhrResourceUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhrsClientPhrResourceUnitTest.class.getName());
    String PHRS_RESOURCE_CLASS = Constants.PHRS_MEDICATION_CLASS;
    /**
     * The note used in this test
     */
    public static final String NOTE = "to import";
    //user owner uri
    public static final String USER = "MedicationTreatmentInteropUnitTest_OwnerUri";
    public static final String USER_PROTOCOL_ID = "PROTOCOL_TEST_"+USER;
    public static final String PROTOCOL_ID_NAMESPACE = Constants.ICARDEA_DOMAIN_PIX_OID;
    public static final String doseFrequenceUriDefault = "http://www.icardea.at/phrs/instances/PerDay";
    public static final String DOSE_TIME_OF_DAY = "http://www.icardea.at/phrs/instances/InTheMorning";
    public static final String DOSE_UNITS = Constants.PILL;//Constants.MILLIGRAM
    public static final String MED_REASON = "http://www.icardea.at/phrs/instances/Cholesterol";
    //public static final String MED_DRUG_NAME_URI = "http://www.icardea.at/phrs/hl7V3#drugName";
    //public static final String PILL_URI="http://www.icardea.at/phrs/instances/pills";
    private static GenericTriplestore triplestore;
    private static MedicationClient medicationClient;

    private static PhrsStoreClient phrsClient;
    private static InteropAccessService iaccess;
    private static InteropProcessor iprocess;
    public static final String DRUG_1_QUANTITY = "2";
    public static final String DRUG_2_QUANTITY = "12";
    public static final String DRUG_1_NAME = "Prednisone";
    public static final String DRUG_2_NAME = "Concor 2";
    public static final String DRUG_1_CODE = "C0032952";
    public static final String DRUG_2_CODE = "C0110591";
    private boolean printDynabean = false;
    private static boolean cleanEnv=false;
    

    @BeforeClass
    public static void setUpClass() throws Exception {
                //phrsClient = PhrsStoreClient.getInstance(true); problem with lock and triplestore connection and static init
        phrsClient = PhrsStoreClient.getInstance(true);
        CommonDao commonDao = phrsClient.getCommonDao();

        //this need for test to send messages given a known protocolId
        PhrFederatedUser pfu = commonDao.getPhrUser(USER, true);
        pfu.setProtocolIdPix(USER_PROTOCOL_ID);
        pfu.setOwnerUri(USER);
        pfu.setCreatorUri(USER);
        commonDao.crudSaveResource((Object) pfu, USER, USER);


        triplestore = phrsClient.getGenericTriplestore();

        iaccess = phrsClient.getInteropService();
        iprocess = new InteropProcessor();//phrsClient);

        //get this one, we set the creator differently
        medicationClient = phrsClient.getInteropClients().getMedicationClient();

        //assign actor

        ActorClient actorClient = phrsClient.getInteropClients().getActorClient();//new ActorClient(triplestore);
        actorClient.register(PROTOCOL_ID_NAMESPACE, USER, USER_PROTOCOL_ID);//unit test, use pr
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if(phrsClient!=null){
            Query query = phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri =", USER);
            phrsClient.getPhrsDatastore().delete(query);
            phrsClient.shutdown();
            if(cleanEnv && triplestore!=null){
                ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
            }
            
            
            phrsClient=null;
        }
        triplestore=null;
    }
/*
    @Before
    public void setUp() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException {

        //get fresh instance using "true"
        //phrsClient = PhrsStoreClient.getInstance(true); problem with lock and triplestore connection and static init
        phrsClient = PhrsStoreClient.getInstance(true);
        triplestore = phrsClient.getGenericTriplestore();

        iaccess = phrsClient.getInteropService();
        iprocess = new InteropProcessor(phrsClient);

        //get this one, we set the creator differently
        medicationClient = phrsClient.getInteropClients().getMedicationClient();

        //assign actor

        ActorClient actorClient = phrsClient.getInteropClients().getActorClient();//new ActorClient(triplestore);
        actorClient.register(PROTOCOL_ID_NAMESPACE, USER, USER_PROTOCOL_ID);//unit test, use pr

        //actorClient.register(PROTOCOL_ID_NAMESPACE, Constants.OWNER_URI_CORE_PORTAL_TEST_USER, Constants.PROTOCOL_ID_UNIT_TEST);
        //actorClient.register(Constants.ICARDEA_DOMAIN_PIX_OID, USER, USER_PROTOCOL_ID);

    }

    @After
    public void tearDown() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, Exception {


        try {
            if (phrsClient != null) {
                Query query = phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri =", USER);
                phrsClient.getPhrsDatastore().delete(query);
            }
        } catch (Exception e) {
            // e.printStackTrace();shows a distracting error
        }

        try {
            if (triplestore != null) {
                ((GenericTriplestoreLifecycle) triplestore).shutdown();
                ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();

            }


        } catch (Exception e) {
            //e.printStackTrace(); shows a distracting error
        }
        triplestore = null;
        phrsClient = null;


    }*/
     //addNewMessagesEhr("xxxx", "zzzzz", "5", "14");
    public void addNewMessagesEhr(String phrResourceUri, String phrResourceUri2, String drug1_quantity, String drug2_quantity) throws TripleException, IllegalAccessException, InstantiationException {
        medicationClient.setCreator(Constants.EXTERN);//simulate extern


        final String resourceURI_1 =
                medicationClient.addMedicationSign(
                USER,
                //NOTE,
                phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri : NOTE,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                medicationClient.buildNullFrequency(),
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
                medicationClient.buildNullFrequency(),
                Constants.HL7V3_ORAL_ADMINISTRATION,
                drug2_quantity,
                DOSE_UNITS,
                DRUG_2_NAME,
                DRUG_2_CODE);

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

    /**
     * Assigns DRUG_1_NAME,DRUG_1_CODE
     *
     * @param phrResourceUri
     * @param drug_quantity
     * @return
     * @throws TripleException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public String addNewMessageEhr(String phrResourceUri, String drug_quantity) throws TripleException, IllegalAccessException, InstantiationException {
        return addNewMessageEhr(phrResourceUri, drug_quantity, DRUG_1_NAME, DRUG_1_CODE);
    }

    public String addNewMessageEhr(String phrResourceUri, String drug1_quantity, String drugName, String drugCode) throws TripleException, IllegalAccessException, InstantiationException {

        medicationClient.setCreator(Constants.EXTERN);//simulate extern

        final String resourceURI_1 =
                medicationClient.addMedicationSign(
                USER_PROTOCOL_ID,
                phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri : NOTE,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                medicationClient.buildNullFrequency(),//FIXXME must use the buildFrequency
                Constants.HL7V3_ORAL_ADMINISTRATION,
                drug1_quantity,
                DOSE_UNITS,
                //label1 != null ? label1 : "EHRDrug1"
                drugName,
                drugCode);

        return resourceURI_1;

    }

    @Test
    public void testAddNewMessagesEhr() throws TripleException, IllegalAccessException, InstantiationException {

        try {
            final String resourceURI_1 =
                    medicationClient.addMedicationSign(
                    USER_PROTOCOL_ID,
                    NOTE,
                    Constants.STATUS_COMPELETE,
                    "201006010000",
                    "201006010000",
                    medicationClient.buildNullFrequency(),//FIXXME build
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    DRUG_1_QUANTITY,
                    DOSE_UNITS,
                    DRUG_1_NAME,
                    DRUG_1_CODE);//FIXXME does not create a code node!

            assertNotNull(resourceURI_1);

            final String resourceURI_2 =
                    medicationClient.addMedicationSign(
                    USER_PROTOCOL_ID,
                    NOTE,
                    Constants.STATUS_RUNNING,
                    "201006010000",
                    "201006010000",
                    medicationClient.buildNullFrequency(),//FIXXME Build
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
    public void testPersistSampleResource() {
        MedicationTreatment res = new MedicationTreatment();

        String user = "user123";
        res.setOwnerUri(user);
        res.setCreatorUri(user);
        res.setBeginDate(new Date());
        //res.setEndDate(new Date());
        res.setLabel("phr user drug " + DRUG_1_NAME);

        res.setStatus(Constants.STATUS_RUNNING);
        res.setReasonCode(MED_REASON);

        //res.setReasonCode(value);
        //res.setPrescribedByName("");
        MedicationTreatmentMatrix mtm = new MedicationTreatmentMatrix();

        mtm.setDosage(30d);//double

        mtm.setDosageQuantity("99");
        mtm.setDosageUnits(DOSE_UNITS);

        mtm.setDosageInterval(doseFrequenceUriDefault);
        mtm.setDosageTimeOfDay(DOSE_TIME_OF_DAY);

        res.setTreatmentMatrix(mtm);

        //save simulation
        //res.setResourceUri("theResourceUri_" + UUID.randomUUID().toString());
        //res.setCreateDate(new Date());
        //res.setModifyDate(res.getCreateDate());*
        getCommonDao().crudSaveResource(res, user, user);

        assertNotNull("resourceUri is null", res.getResourceUri());

    }

    @Test
    public void testPersistSampleResourceAltSave() {
        MedicationTreatment res = new MedicationTreatment();

        String user = "user123";
        res.setOwnerUri(user);
        res.setCreatorUri(user);
        res.setBeginDate(new Date());
        //res.setEndDate(new Date());
        res.setLabel("phr user drug " + DRUG_1_NAME);

        res.setStatus(Constants.STATUS_RUNNING);
        res.setReasonCode(MED_REASON);

        //res.setReasonCode(value);
        //res.setPrescribedByName("");
        MedicationTreatmentMatrix mtm = new MedicationTreatmentMatrix();

        mtm.setDosage(30d);//double

        mtm.setDosageQuantity("99");
        mtm.setDosageUnits(DOSE_UNITS);

        mtm.setDosageInterval(doseFrequenceUriDefault);
        mtm.setDosageTimeOfDay(DOSE_TIME_OF_DAY);

        res.setTreatmentMatrix(mtm);

        //save simulation
        //res.setResourceUri("theResourceUri_" + UUID.randomUUID().toString());
        //res.setCreateDate(new Date());
        //res.setModifyDate(res.getCreateDate());*
        getCommonDao().crudSaveResource(res, user, user);

        assertNotNull("resourceUri is null", res.getResourceUri());

    }

    @Test
    public void testClientCreatorSetting() {

        String creator = medicationClient.getCreator();
        assertEquals("Error Expect PHR creator! ", PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR, creator);

    }

    @Test
    public void testClientCreatorReset() {
        medicationClient.setCreator("XYZ");
        String creator = medicationClient.getCreator();
        assertEquals("Error Expect PHR creator! ", "XYZ", creator);

    }

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

    public Set<DynaBean> getInteropMessagesForUser(String user, String phrClass, boolean onlyNewMessages) {
        System.out.println("testFindAllInteropMessagesForUser");
        Set<DynaBean> beans = findInteropMessagesForUser(user, phrClass, onlyNewMessages);
        int count = 0;
        for (DynaBean result : beans) {
            count++;
            if (printDynabean) {
                System.out.println("--------");
                System.out.println(DynaUtil.toString(result));
            }

        }
        if (printDynabean) {
            System.out.println("--------");
        }
        for (DynaBean result : beans) {
            count++;

            System.out.println("getInteropMessagesForUser found messages count" + count + " uri=" + result);


        }
        return beans;
    }

    @Test
    public void testFindNoNewInteropMessagesForUser() throws Exception {

        //simulate  EHR records with  phrweb references note
        addNewMessagesEhr("xxxx", "zzzzz", "5", "14");

        String phrClass = PHRS_RESOURCE_CLASS;
        String user = USER;
        System.out.println("testFindNoNewInteropMessagesForUser");
        Set<DynaBean> beans = findInteropMessagesForUser(user, phrClass, true);
        int count = 0;
        for (DynaBean result : beans) {
            count++;
            if (printDynabean) {
                System.out.println("all found messages count" + count + " uri=" + result);
            }

        }
        assertTrue("Known messages found in interop, expect zero ", count == 0);

    }

    @Test
    public void testLoadSampleTestDataSet() {
        System.out.println("testLoadSampleTestDataSet");
        CoreTestData core = new CoreTestData();
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
        iaccess.sendMessages(repositoryObject);
    }

    public int loadSampleTestDataSet(String owner) {

        CoreTestData core = new CoreTestData();
        //return count
        return core.addTestMedications_2_forPortalTestForOwnerUri(owner);

    }

    public PhrsStoreClient getPhrsStoreClient() {
        return phrsClient;
    }

    public static String parseReferenceNote(String note) {
        String out = null;
        if (note != null) {
            note = note.trim();
        }
        if (note != null) {
            if (note.contains(InteropAccessService.REFERENCE_NOTE_PREFIX)) {
                //or def parts, then use parts.size()
                String[] parts = note.split(InteropAccessService.REFERENCE_NOTE_PREFIX);

                if (parts != null && parts.length > 0) {
                    //split on whitespace, take [0]
                    out = parts[1];
                    if (out != null) {
                        out = out.trim();
                        String[] parts2 = out.split(" ");
                        if (parts2 != null && parts2.length > 0) {
                            out = parts2[0];
                        }
                    }
                }
            } else {
                //no parsing
                //out = note    
            }
        }
        return out;
    }
// +++++++++++++++ end helpers
    public String getProtocolId(String ownerUri){
        return  getCommonDao().getProtocolId(ownerUri);
    }
    /**
     *
     * @param ownerUri
     * @param phrsClass
     * @param onlyNewMessages - true collect only new messages; false - all
     * message, known and unknown to phrweb portal
     * @return
     */
    public Set<DynaBean> findInteropMessagesForUser(String ownerUri, String phrsClass, boolean onlyNewMessages) {
        Set<DynaBean> beans = new HashSet<DynaBean>();

        if (ownerUri != null && phrsClass != null) {
            try {
                //FIXID
                String protocolId= getProtocolId(ownerUri);
                if(protocolId !=null && ! protocolId.isEmpty()){
                    // ok
                } else {
                    LOGGER.error("No protocolID yet for User, no send messages for ownerUri="+ownerUri+" phrClass="+phrsClass);
                    return beans;
                }

                final Map<String, String> queryMap = new HashMap<String, String>();

                queryMap.put(Constants.RDFS_TYPE, phrsClass);
                //FIXID
                queryMap.put(Constants.OWNER, protocolId);
                //queryMap.put(Constants.OWNER, ownerUri);

                Iterable<String> resources = getPhrsStoreClient().getGenericTriplestore().getForPredicatesAndValues(queryMap);

                //check each results, has it been tagged?

                //import the message, and also save it back to the Interop Service to tag it and make other listeners aware of it.
                if (resources != null) {
                    //transform message to


                    DynaBeanClient dynaBeanClient = getInteropClients().getDynaBeanClient();

                    for (String messageResourceUri : resources) {
                        try {
                            String referenceNote = null;
                            DynaBean dynaBean = dynaBeanClient.getDynaBean(messageResourceUri);
                            try {
                                referenceNote = DynaUtil.getStringProperty(dynaBean, Constants.SKOS_NOTE);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(),e);

                            }
                            if (referenceNote != null) {
                                String aboutResourceUri = parseReferenceNote((String) referenceNote);

                                if (aboutResourceUri != null && aboutResourceUri.length() != 0) {
                                    //existing, already imported
                                    if (!onlyNewMessages) {
                                        beans.add(dynaBean);
                                    }
                                } else {
                                    //new
                                    beans.add(dynaBean);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(),e);

                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
        return beans;

    }
    public static int showList(String title, List<String> results) {
        int count = 0;
        System.out.println("Title:" + title);
        for (String result : results) {
            count++;
            System.out.println("item (" + count + ") uri=" + result);
        }
        return count;
    }

    public static int showMap(String title, Map<String, String> map) {
        int count = 0;
        System.out.println("Title:" + title);
        for (String key : map.keySet()) {
            count++;
            System.out.println("item (" + count + ") thing=" + key + " val=" + map.get(key));
        }
        return count;
    }

    @Test
    public void testFindReferenceTagExisting() throws Exception {
        String theParentId = "resUri1234";

        String message_1 = addNewMessageEhr(theParentId, "4");
        String message_2 = addNewMessageEhr(null, "44");
        String interopRef = iprocess.findMessageWithReference(USER, theParentId, Constants.PHRS_MEDICATION_CLASS, null);

        assertNotNull("Message not found", interopRef);
        assertEquals("Did not find resource reference ", message_1, interopRef);
    }

    @Test
    public void testFindReferenceTagNotExisting() throws Exception {
        String theParentId = "resUri1234";
        //put message with a different reference and one with null
        String message_1 = addNewMessageEhr("xxxxxx", "111");
        String message_2 = addNewMessageEhr(null, "222");

        String interopRef = iprocess.findMessageWithReference(USER, theParentId, Constants.PHRS_MEDICATION_CLASS, null);
        assertNull("expected no message result, found: ", interopRef);

    }

    @Test
    public void testFindReferenceTagNull() throws Exception {
        String theParentId = "resUri1234";
        String message_1 = addNewMessageEhr(null, "777");
        String message_2 = addNewMessageEhr(null, "888");

        String interopRef = iprocess.findMessageWithReference(USER, theParentId, Constants.PHRS_MEDICATION_CLASS, null);
        assertNull("expected null interopRef, found: ", interopRef);
        //assertEquals("Did not find resource reference ",theParentId,interopRef);
    }

    @Test
    public void testSendInteropMessageMedicationClient() {
        System.out.println("testSendInteropMessages");
        MedicationTreatment phrMed_1 = createPhrResource("test in phr_ drug_1", 2.0d, "3");

        Map<String, String> uris = iaccess.sendMessages(phrMed_1);

        assertNotNull("Messages sent but no message URIs returned!", uris);

        //showMap("message uris by type", uris);
        //showList("results",list);

        assertTrue(!uris.isEmpty());
        assertTrue(uris.size() > 0);
        for (String uri : uris.keySet()) {
            System.out.println("uri=" + uri);
        }


        assertNotNull("resourceUri is null of sample obj ", phrMed_1.getOwnerUri());
        //this is the message uri, not the resource uri
        String interopRef = iaccess.findMessageWithReference(
                USER, phrMed_1.getResourceUri(), PHRS_RESOURCE_CLASS, null);

        System.out.println("interopRef: " + interopRef);
        assertNotNull("interopRef is null, cannot find message for " + phrMed_1.getOwnerUri(), interopRef);

        //Not needed: get dynabean for this message uri
        //assertEquals("cannot find message with note reference to resource Uri =" + phrMed_1.getResourceUri(), phrMed_1.getResourceUri(), dynbeanNoteReference);

    }

    @Test
    public void testImportNewMessages() throws Exception {
        System.out.println("testImportNewMessages");

        String message_1 = addNewMessageEhr(null, "4", DRUG_1_NAME, DRUG_1_CODE);
        String message_2 = addNewMessageEhr(null, "8", DRUG_2_NAME, DRUG_2_CODE);

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
        String resUri2 = null;
        
        for (Object obj : phrResources) {
            MedicationTreatment mt = (MedicationTreatment) obj;
            String title = mt.getTitle();
            String drugCode = mt.getProductCode();
            Double drugDosage = mt.getTreatmentMatrix().getDosage();
            
            String drugQuantity = mt.getTreatmentMatrix().getDosageQuantity();
            System.out.println("title "+title+ " drugCode "+drugCode
                    +" drugDosage "+drugDosage+" drugDosage string "+drugDosage.intValue()
                    +" drugQuantity "+drugQuantity);
            if (DRUG_1_NAME.equals(title)) {
                assertEquals("Bad drug code     message 1", DRUG_1_CODE, drugCode);
                assertEquals("Bad drug quantity message 1", 4, drugDosage.intValue());
                
                //assertEquals("Bad drug quantity message 1", "4", drugQuantity);
                matchName1 = DRUG_1_NAME;
                resUri1 = mt.getResourceUri();
            } else if (DRUG_2_NAME.equals(title)) {
                assertEquals("Bad drug code     message 2", DRUG_2_CODE, drugCode);
                assertEquals("Bad drug quantity message 2", 8, drugDosage.intValue());
                
                //assertEquals("Bad drug quantity message 2", "8", drugQuantity);
                matchName2 = DRUG_2_NAME;
                resUri2 = mt.getResourceUri();
            }
            assertEquals("Expected HL7V3_ORAL_ADMINISTRATION", mt.getTreatmentMatrix().getAdminRoute(),
                    Constants.HL7V3_ORAL_ADMINISTRATION);

        }

        assertEquals("Bad drug name message 1", DRUG_1_NAME, matchName1);
        assertEquals("Bad drug name message 2", DRUG_2_NAME, matchName2);
        //find marked messages
        boolean onlyNewMessages = false;
        String phrClass = PHRS_RESOURCE_CLASS;
        Set<DynaBean> beans = this.getInteropMessagesForUser(USER, phrClass, onlyNewMessages);

        String interopRef1 = iprocess.findMessageWithReference(
                USER, resUri1, Constants.PHRS_MEDICATION_CLASS, null);
        assertNotNull("Message not found", interopRef1);
        assertEquals("Did not find resource reference for message_1 ", message_1, interopRef1);


        String interopRef2 = iprocess.findMessageWithReference(USER, resUri2, Constants.PHRS_MEDICATION_CLASS, null);
        assertNotNull("Message not found", interopRef2);
        assertEquals("Did not find resource reference for message_2 ", message_2, interopRef2);


        // DRUG_1_NAME = "Prednisone";
        // DRUG_2_NAME = "Concor 2";
    }

    @Test
    @Ignore
    public void testSendInteropMessageMedicationClientViaInteropDirectCode() {
        String messageResourceUri = null;
        try {
            MedicationTreatment resource = new MedicationTreatment();
            String resourceType = resource.getClass().getCanonicalName();
            resource.setOwnerUri(USER);
            resource.setCreatorUri(USER);
            resource.setBeginDate(new Date());
            //res.setEndDate(new Date());
            resource.setLabel("phr user drug test 1 ");

            resource.setStatus(Constants.STATUS_RUNNING);
            resource.setReasonCode(MED_REASON);

            //res.setReasonCode(value);
            //res.setPrescribedByName("");
            MedicationTreatmentMatrix mtm = new MedicationTreatmentMatrix();

            mtm.setDosage(3.0d);//double

            mtm.setDosageQuantity("5");
            mtm.setDosageUnits(DOSE_UNITS);


            mtm.setDosageInterval(doseFrequenceUriDefault);

            mtm.setDosageTimeOfDay(DOSE_TIME_OF_DAY);

            resource.setTreatmentMatrix(mtm);
            //save simulation
            resource.setResourceUri("theResourceUri_" + UUID.randomUUID().toString());
            resource.setCreateDate(new Date());
            resource.setModifyDate(resource.getCreateDate());
//------------------------------
//common metadata
//
            BasePhrsModel res = (BasePhrsModel) resource;
            String owner = res.getOwnerUri();

            String status = this.transformStatus(res.getStatus());
            status = status != null ? status : Constants.STATUS_RUNNING;

            String categoryCode = this.transformCategory(res.getCategory(), resourceType);
            categoryCode = categoryCode != null ? categoryCode : null;

            String valueCode = this.transformCode(res.getCode());
            valueCode = valueCode != null ? valueCode : null;

            String dateStringStart = transformDate(res.getBeginDate(), res.getEndDate());
            dateStringStart = dateStringStart != null ? dateStringStart : null;

            String dateStringEnd = transformDate(res.getEndDate(), (Date) null);
            dateStringEnd = dateStringEnd != null ? dateStringEnd : null;

// -----------------------------
//domain data
//           
            MedicationTreatment domain = (MedicationTreatment) resource;

            //res.note is by default not sharable
            /**
             * Use the note to tag this record. Be sure to write note to
             * multiple messages such as Vital signs with separate messages for
             * Body weight, height, sys, diastolic
             *
             */
            //create reference to phrweb object for note
            String referenceNote = createReferenceNote(domain.getResourceUri());

            String name = domain.getLabel() != null ? domain.getLabel() : domain.getCode();

            String freqCode = domain.getFrequencyCode();
            //String dosageValue = domain.getTreatmentMatrix().getDosage() != null ? domain.getTreatmentMatrix().getDosage().toString() : "0";
            String dosageQuantity = domain.getTreatmentMatrix().getDosageQuantity();
            String dosageValue = domain.getTreatmentMatrix().getDosage() != null ? domain.getTreatmentMatrix().getDosage().toString() : "0";

            String doseUnits = domain.getTreatmentMatrix().getDosageUnits();

            String doseInterval = domain.getTreatmentMatrix().getDosageInterval();
            String doseTimeOfDay = domain.getTreatmentMatrix().getDosageTimeOfDay();

            String adminRoute = domain.getTreatmentMatrix().getAdminRoute();
            adminRoute = tranformMedicationAdminRoute(adminRoute, doseUnits);
            //junit
            assertNotNull(PhrsStoreClient.getInstance().getInteropClients().getMedicationClient());
            //PhrsStoreClient.getInstance().
            MedicationClient medicationclient = getInteropClients().getMedicationClient();

            String messageId = medicationclient.addMedicationSign(
                    owner,
                    referenceNote,
                    status,
                    dateStringStart,
                    dateStringEnd,//dateStringEnd,
                    medicationclient.buildNullFrequency(),//doseInterval, frequency,
                    adminRoute,
                    dosageQuantity,//dosageValue,
                    doseUnits,
                    name);
            //InteropAccessService.DRUG_CODE_DEFAULT_PHR);
            messageResourceUri = messageId;
        } catch (RuntimeException e) {
            e.printStackTrace();

        } catch (TripleException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull("messageResourceUri is null,interop message failed", messageResourceUri);
    }

    public String createReferenceNote(String resourceUri) {
        String referenceNote = null;
        if (resourceUri != null) {
            referenceNote = InteropAccessService.REFERENCE_NOTE_PREFIX + resourceUri; //'' //res.note is by default not sharable
        } else {
            referenceNote = "error";
        }
        return referenceNote;
    }

    public void createManufactureName(String subjectUri, String drugName, String drugCode) {
        MedicationClient medicationClient = PhrsStoreClient.getInstance().getInteropClients().getMedicationClient();
        /*
         * triplestore.persist(subject,
         * "http://www.icardea.at/phrs/hl7V3#manufacturedProduct",
         * buildManufacturedProduct(drugName, drugCode), RESOURCE);
         */
        String newDrugProductUri = null;
        try {
            newDrugProductUri = medicationClient.buildManufacturedProduct(drugName, drugCode);
        } catch (TripleException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        assertNotNull(newDrugProductUri);

        try {
            // update 
            medicationClient.updateMedication(
                    subjectUri,
                    //Constants.MANUFACTURED_PRODUCT,
                    PhrsConstants.MEDICATION_PROPERTY_MANUFACTURED_PRODUCT_URI,
                    newDrugProductUri);
        } catch (TripleException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
     * medicationClient.updateMedication(resourceURI,
     * Constants.PHRS_MEDICATION_DOSAGE, newDosageURI);
     */

    public String transformStatus(String status) {
        String out = status;

        return out;
    }

    public String transformCode(String code) {
        String out = code;

        return out;
    }

    public String transformCategory(String category, String type) {
        String out = category;

        return out;
    }

    public String transformDate(Date date, Date defaultDate) {
        Date theDate = date;
        if (theDate == null) {
            if (defaultDate != null) {
                theDate = defaultDate;
            }
        }
        if (theDate != null) {

            return HealthyUtils.formatDate(theDate, (String) null, InteropAccessService.DATE_PATTERN_INTEROP_DATE_TIME);
        }
        return null;
    }

    public String tranformMedicationAdminRoute(String adminRoute, String doseUnits) {
        String out = adminRoute;
        out = Constants.HL7V3_ORAL_ADMINISTRATION;


        return out;
    }

    @Test
    @Ignore
    public void testImportMessagesDirectCode() throws Exception {
        System.out.println("testImportMessagesDirectCode");
        addNewMessagesEhr(null, null, "4", "8");//an EHR record
        boolean importMessage = false;
        List phrResources = new ArrayList();
        List<String> imported = importNewMessages(USER, PHRS_RESOURCE_CLASS, importMessage, phrResources);
        assertNotNull(imported);
        assertTrue("Found 0, expect more", imported.size() > 1);

        assertNotNull("No phrResources created ", phrResources);
        assertTrue("No phrResources created ", phrResources.size() > 0);



        String matchName1 = null;
        String matchName2 = null;
        for (Object obj : phrResources) {
            MedicationTreatment mt = (MedicationTreatment) obj;
            String title = mt.getTitle();
            String drugCode = mt.getProductCode();
            String drugQuantity = mt.getTreatmentMatrix().getDosageQuantity();

            if (DRUG_1_NAME.equals(title)) {
                assertEquals("Bad drug code     message 1", DRUG_1_CODE, drugCode);
                assertEquals("Bad drug quantity message 1", "4", drugQuantity);
                matchName1 = DRUG_1_NAME;
            } else if (DRUG_2_NAME.equals(title)) {
                assertEquals("Bad drug code     message 2", DRUG_2_CODE, drugCode);
                assertEquals("Bad drug quantity message 2", "8", drugQuantity);
                matchName2 = DRUG_2_NAME;
            }
            assertEquals("Expected HL7V3_ORAL_ADMINISTRATION",
                    mt.getTreatmentMatrix().getAdminRoute(),
                    Constants.HL7V3_ORAL_ADMINISTRATION);

        }

        assertEquals("Bad drug name message 1", DRUG_1_NAME, matchName1);
        assertEquals("Bad drug name message 2", DRUG_2_NAME, matchName2);

    }

    public List<String> importNewMessages(String ownerUri, String phrsClass, boolean importMessage) throws Exception {
        return this.importNewMessages(ownerUri, phrsClass, importMessage, null);
    }

    public List<String> importNewMessages(String ownerUri, String phrsClass, boolean importMessage, List phrResources) throws Exception {


        List<String> list = new ArrayList();
        if (ownerUri != null && phrsClass != null) {

            final MultiIterable result = new MultiIterable();
            final Map<String, String> queryMap = new HashMap<String, String>();
            try {
                //Find new messages (true),ignore known messages that have been imported
                Set<DynaBean> results = findInteropMessagesForUser(ownerUri, phrsClass, true);
                //check each results, has it been tagged?

                //import the message, and also save it back to the Interop Service to tag it and make other listeners aware of it.
                if (results != null) {
                    if (phrResources == null) {
                        phrResources = new ArrayList();
                    }
                    int resSize = results.size();
                    System.out.println("findInteropMessagesForUser, count messages= " + results.size());
                    LOGGER.debug("findInteropMessagesForUser, count messages= " + results.size());

                    for (DynaBean dynaBean : results) {
                        try {
                            //DynaBean dynaBean = dynaBeanClient.getDynaBean(messageResourceUri);
                            String messageUri = dynaBean.getDynaClass().getName();
                            DynaBeanUtil.toString(dynaBean);
                            System.out.println("importNewMessages getDynaClass()= " + messageUri);

                            // DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
                            //http://www.icardea.at/phrs#owner
                            Object owner = DynaUtil.getStringProperty(dynaBean, Constants.OWNER);
                            System.out.println("importNewMessages owner= " + DynaUtil.getStringProperty(dynaBean, Constants.OWNER));


                            Object creator = DynaUtil.getStringProperty(dynaBean, Constants.CREATOR);

                            Object repositoryObject = transformInteropMessage(ownerUri, phrsClass, dynaBean, messageUri);

                            if (repositoryObject != null) {

                                if (dynaBean.getDynaClass().getName() != null) {
                                    //list.add(repositoryObject);
                                    //add only the URI
                                    list.add(dynaBean.getDynaClass().getName());

                                }
                                phrResources.add(repositoryObject);
                            }
                            if (importMessage && repositoryObject != null) {
                                //save transformed resource to local store
                                //the resourceUri issue
                                getCommonDao().crudSaveResource(repositoryObject, ownerUri, "interopservice");
                                //send message to interop service
                                //Map map=
                                sendMessages(repositoryObject);
                            }

                            //}

                        } catch (Exception e) {
                            e.printStackTrace();
                            // LOGGER.error(' message error, interop ownerUri= '+ownerUri+" messageResourceUri="+messageResourceUri, e)
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            } catch (java.lang.Error e) {
                System.out.println("java lang error sesame error");
                e.printStackTrace();
            }
        }
        //   MedicationTreatment uunitTestMedCheck = (MedicationTreatment) unitTestObjectCheck;
        //   assertNotNull(unitTestObjectCheck);

        // assertEquals("bad admin route ", uunitTestMedCheck.getTreatmentMatrix().getAdminRoute(), Constants.HL7V3_ORAL_ADMINISTRATION);

        if (list != null) {
            System.out.println("importNewMessages list size= " + list.size());
        } else {
            System.out.println("importNewMessages list=NULL " + list);
        }

        return list;

    }

    public boolean compareOwners(String givenOwnerUri, String messageOwner) {
        boolean valid = false;
        //should be ok, check again
        if (messageOwner != null && messageOwner.length() > 0 && givenOwnerUri != null) {
            if (messageOwner.equals(givenOwnerUri)) {
                valid = true;
            }

        }
        return valid;
    }

    public Object transformInteropMessage(String givenOwnerUri, String phrsClass, DynaBean dynabean, String messageResourceUri) {
        Object theObject = null;
        try {

            if (dynabean != null && phrsClass != null) {
                LOGGER.debug("phrsClass=" + phrsClass + " bean properties =");

                //switch (phrsClass) {
                //   case Constants.PHRS_MEDICATION_CLASS:
                String messageOwner = DynaUtil.getStringProperty(dynabean, Constants.OWNER, null);
                if (messageOwner != null) {
                    messageOwner = messageOwner.trim();
                }
                //Map props = bean.getProperties() //check ownerUri of message


                if (!compareOwners(givenOwnerUri, messageOwner)) {
                    LOGGER.error("Message ownerUri does not match given givenOwnerUri=" + givenOwnerUri + " med.OwnerUri=");

                } else {

                    // Constants.HL7V3_DATE_START  Constants.HL7V3_DATE_END
                    // Constants.HL7V3_STATUS Constants.HL7V3_FREQUENCY Constants.HL7V3_ADMIN_ROUTE Constants.HL7V3_DOSAGE
                    // Constants.HL7V3_DRUG_NAME Constants.HL7V3_CODE

                    MedicationTreatment med = new MedicationTreatment();
                    med.setOwnerUri(messageOwner);

                    med.setCreatorUri(DynaUtil.getStringProperty(dynabean, Constants.CREATOR, "EHR"));//FIXXME 

                    String stdStatus = DynaUtil.getValueResourceUri(dynabean, Constants.HL7V3_STATUS, null);//TODO default

                    med.setStatusStandard(stdStatus); //med.status = 'medicationSummary_medicationStatus_true'
                    //InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_STATUS, null)

                    med.setStatus(InteropTermTransformer.transformStandardStatusToLocal(stdStatus, Constants.PHRS_MEDICATION_CLASS));
                    //there is no code here, but it is likely a dynabean
                    //deprecated med.setCode(DynaUtil.getStringProperty(dynabean, Constants.HL7V3_CODE, null));//TODO is this drug code?
                    //check the origin of this message....
                    med.setOrigin(DynaUtil.getStringProperty(dynabean, Constants.ORIGIN, PhrsConstants.INTEROP_ORIGIN_DEFAULT_EHR));

                    med.setOriginStatus(PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED);

                    med.setExternalReference(messageResourceUri);


                    //do not set resourceUri, but origin should be checked during udpates to interop messages
/*
                     * FIXXME
                     * med.setReasonCode("http://www.icardea.at/phrs/instances/NoSpecialTreatment");
                     * //med.prescribedByName=
                     * //Constants.MANUFACTURED_LABEL_DRUG //String property
                     *
                     */
                    Map<String, String> attrMedName = getMedicationNameAttributes(dynabean);
                    String medName = getMapValue(attrMedName, Constants.HL7V3_DRUG_NAME, "Drug");
                    med.setTitle(medName);
                    String productCode = getMapValue(attrMedName, Constants.HL7V3_VALUE, null);
                    med.setProductCode(productCode);

                    //Adminroute FIXXME 
                    // med.getTreatmentMatrix().setAdminRoute(Constants.HL7V3_ORAL_ADMINISTRATION);
                    med.getTreatmentMatrix().setAdminRoute(DynaUtil.getValueResourceUri(dynabean, Constants.HL7V3_ADMIN_ROUTE, Constants.HL7V3_ORAL_ADMINISTRATION));
                    //this is a uri to another dynabean with dosage and units
                    //Constants.HL7V3_DOSAGE PHRS_MEDICATION_DOSAGE

                    //FIXXME DOSAGE is QUANTITY integer
                    //FIXXME dosage per unit not received from EHR, but web form no longer asks.

                    //Constants.PHRS_MEDICATION_DOSAGE

                    med.getTreatmentMatrix().setDosage(0d);

                    Map<String, String> doseAttrs = getMedicationDosageAttributes(dynabean);
                    String doseQuantity = getMapValue(doseAttrs, Constants.HL7V3_DOSAGE_VALUE, "0");
                    med.getTreatmentMatrix().setDosageQuantity(doseQuantity);

                    String doseUnits = getMapValue(doseAttrs, Constants.HL7V3_DOSAGE_UNIT, "http://www.icardea.at/phrs/instances/pills");
                    med.getTreatmentMatrix().setDosageUnits(doseUnits);

                    //FIXXME - TOD and interval are not expected from EHR.  this create the frequency node
                    String interval = "http://www.icardea.at/phrs/instances/other";
                    med.getTreatmentMatrix().setDosageInterval(interval);

                    String tod = "http://www.icardea.at/phrs/instances/NotSpecified";
                    med.getTreatmentMatrix().setDosageTimeOfDay(tod);


                    //dates. always need a start date
                    String dateBegin = DynaUtil.getStringProperty(dynabean, Constants.HL7V3_DATE_START);
                    //set new date if not found
                    Date beginDate = transformDateFromMessage(dateBegin, new Date());	//HealthyUtils.formatDate( dateBegin, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)

                    String dateEnd = DynaUtil.getStringProperty(dynabean, Constants.HL7V3_DATE_END, null);
                    Date endDate = transformDateFromMessage(dateEnd, (Date) null);		//HealthyUtils.formatDate( dateEnd, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)//transformDate(dateEnd)

                    med.setBeginDate(beginDate);
                    med.setEndDate(endDate);


                    med.setCreateDate(new Date());
                    med.setModifyDate(med.getCreateDate());
                    med.setType(MedicationTreatment.class.toString());


                    theObject = med;

                    if (med != null) {
                        System.out.println("medication imported resourceUri=" + med.getResourceUri() + " name=" + med.getTitle() + " code=" + med.getProductCode());
                        LOGGER.debug("medication imported resourceUri" + med.getResourceUri() + " name=" + med.getTitle() + " code=" + med.getProductCode());
                    }
                }

                //Date formatedDate = DateUtil.getFormatedDate(dateStr)

                //  break

                // default:
                //     break
                //}
            }
        } catch (Exception e) {

            if (dynabean != null) {
                LOGGER.error(
                        "Interop Message transformation failed, ownerUri=" + givenOwnerUri + " phrsClass=" + phrsClass, e);
            } else {
                LOGGER.error("Interop Message transformation failed, dynabean NULL, ownerUri="
                        + givenOwnerUri + " phrsClass=" + phrsClass, e);
            }
        }
        return theObject;
    }

    //HL7V3_DRUG_NAME
    public static String getMapValue(Map<String, String> map, String key) {
        return getMapValue(map, key, null);
    }

    public static String getMapValue(Map<String, String> map, String key, String defaultValue) {
        String value = null;
        if (map != null && key != null && map.containsKey(key)) {
            value = map.get(key);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public DynaBean getByDynabeanOrUri(Object obj) {
        try {
            if (obj != null) {
                if (obj instanceof DynaBean) {
                    return (DynaBean) obj;
                }
            } else if (obj instanceof String) {
                DynaBeanClient dbc = PhrsStoreClient.getInstance().getInteropClients().getDynaBeanClient();
                DynaBean dynaBean = dbc.getDynaBean((String) obj);

            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return null;

    }

    /**
     * Get the dosage details and put them into a map
     *
     * @param medicationDynabean
     * @return
     */
    public Map getMedicationDosageAttributes(DynaBean medicationDynabean) {
        Map map = null;
        try {
            DynaBean dynaBean = (DynaBean) DynaUtil.getDynaBeanProperty(medicationDynabean, Constants.HL7V3_DOSAGE); //http://www.icardea.at/phrs/hl7V3#dosage  (medicationDynabean);

            if (dynaBean != null) {



                //DynaBeanClient dbc = PhrsStoreClient.getInstance().getInteropClients().getDynaBeanClient();
                //DynaBean dynaBean = dbc.getDynaBean(uri);

                if (dynaBean != null) {
                    String dosage = DynaUtil.getStringProperty(dynaBean, Constants.HL7V3_DOSAGE_VALUE); //Constants.HL7V3_DOSAGE
                    String units = DynaUtil.getValueResourceUri(dynaBean, Constants.HL7V3_DOSAGE_UNIT);
                    map = new HashMap();

                    if (dosage != null) {
                        dosage = dosage.trim();
                    }
                    if (dosage != null && dosage.length() > 0) {
                        map.put(Constants.HL7V3_DOSAGE_VALUE, dosage);
                    }
                    if (units != null) {
                        units = units.trim();
                    }
                    if (units != null && units.length() > 0) {
                        map.put(Constants.HL7V3_DOSAGE_UNIT, units);
                    }
                }


            }

        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return map;
    }

    /**
     * Get name and product code attributes. These might be stored simply as a
     * name or as complex subnode
     *
     * @param medicationDynabean
     * @return
     */
    public Map getMedicationNameAttributes(DynaBean medicationDynabean) {
        Map map = null;
        try {
            DynaBean dynaBeanMfgProduct = DynaUtil.getDynaBeanProperty(
                    medicationDynabean,
                    PhrsConstants.MEDICATION_PROPERTY_MANUFACTURED_PRODUCT_URI);//TODO replace with Constants.MANUFACTURED_PRODUCT, not MANUFACTURED_PRODUCT_CLASS

            if (dynaBeanMfgProduct != null) {
                DynaBean mfgLabelDrugBean = DynaUtil.getDynaBeanProperty(
                        dynaBeanMfgProduct,
                        Constants.MANUFACTURED_LABEL_DRUG);//not MANUFACTURED_LABEL_DRUG_CLASS



                if (mfgLabelDrugBean != null) {
                    String name, drugId = null;
                    //simple name usually from PHR add  FIXXME WRONG!
                    name = DynaUtil.getStringProperty(mfgLabelDrugBean, Constants.HL7V3_DRUG_NAME); //Constants.HL7V3_DOSAGE
                    //or complex name and code from EHR FIXXME WRONG!
                    DynaBean codeBean = (DynaBean) DynaUtil.getDynaBeanProperty(mfgLabelDrugBean, Constants.HL7V3_CODE);

                    //Try to get name and code from a complex representation
                    if (codeBean != null) {
                        String name2 = DynaUtil.getStringProperty(codeBean, Constants.SKOS_PREFLABEL);
                        name = name2 != null && name2.length() > 0 ? name2 : name;

                        drugId = DynaUtil.getStringProperty(codeBean, Constants.HL7V3_VALUE);
                    }

                    map = new HashMap();

                    if (name != null) {
                        name = name.trim();
                    }
                    if (name != null && name.length() > 0) {
                        map.put(Constants.HL7V3_DRUG_NAME, name);
                        //extra 
                        map.put(Constants.SKOS_PREFLABEL, name);
                    }
                    if (drugId != null) {
                        drugId = drugId.trim();
                    }
                    //this is a resource...another dynabean!
                    if (drugId != null && drugId.length() > 0) {
                        map.put(Constants.HL7V3_VALUE, drugId);
                    }
                }


            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return map;
    }

    /**
     * Transform to date string yyyyMMddHHmm
     *
     * @param date
     * @param dateTime
     * @return
     *
     */
    public static Date transformDateFromMessage(String dateMessage, Date defaultDate) {
        Date theDate = null;
        try {
            if (dateMessage != null) {
                theDate = DateUtil.getFormatedDate(dateMessage);
            }
            if (theDate != null) {
                theDate = defaultDate != null ? defaultDate : new Date();
            }
        } catch (Exception e) {

            LOGGER.error("transforming date dateMessage="+dateMessage, e);
        }
        return theDate;
    }

    public String buildMedicationDosage(String dosageValue, String dosageUnits) {
        String newDosageURI = null;
        try {
            newDosageURI = medicationClient.buildDosage(dosageValue, dosageUnits);

        } catch (Exception e) {
            LOGGER.error("buildMedicationDosage dosageValue=" + dosageValue + " dosageUnits=" + dosageUnits, e);
        }
        return newDosageURI;
    }
    /*

    public void registerProtocolId(String owneruri, String protocolId, String namespace){

        getPhrsStoreClient().getInteropProcessor().registerProtocolId( owneruri,protocolId,namespace)

    }
     */
    @Test
    public void testNotify_Med(){
        System.out.println("testNotify Med");
        InteropClients interopClients=phrsClient.getInteropClients();

        String protocolId= "PROTOCOLID_"+USER;

        CommonDao commonDao=phrsClient.getCommonDao();
        commonDao.registerProtocolId(USER,protocolId,null);


        MedicationTreatment phrMed_1 = createPhrResource("test in phr_ drug_1", 2.0d, "3");

        Map<String, String> uris = iaccess.sendMessages(phrMed_1);

        assertNotNull("Messages sent but no message URIs returned!", uris);

        //showMap("message uris by type", uris);
        //showList("results",list);

        assertTrue(!uris.isEmpty());
        assertTrue(uris.size() > 0);
        for (String uri : uris.keySet()) {
            System.out.println("uri=" + uri);
        }


        assertNotNull("resourceUri is null of sample obj ", phrMed_1.getOwnerUri());
        //this is the message uri, not the resource uri
        String interopRef = iaccess.findMessageWithReference(
                USER, phrMed_1.getResourceUri(), PHRS_RESOURCE_CLASS, null);

        System.out.println("interopRef: " + interopRef);
        assertNotNull("interopRef is null, cannot find message for " + phrMed_1.getOwnerUri(), interopRef);


       //CoreTestData.addTestBasicHealthVitalsData(USER);

       //interopClients.notifyInteropMessageSubscribersByPhrId(protocolId);

    }
   
 
}
