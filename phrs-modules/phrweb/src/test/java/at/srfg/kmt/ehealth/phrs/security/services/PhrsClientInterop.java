/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.PHRSRequestClient;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatmentMatrix;
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBloodPressure;
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBodyWeight;
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileActivityDailyLiving;
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsProblem;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;
import java.util.Date;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients;
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;
import com.google.code.morphia.query.Query;
import java.util.Map;
import java.util.UUID;

public class PhrsClientInterop {

    public static final String NOTE = "to import";
    public static final String USER = "user_unittest_PhrsClientInterop";//MedicationClientUnitTest.class.getName();
    public static final String DOSE_INTERVAL = "http://www.icardea.at/phrs/instances/EveryHour";
    public static final String DOSE_TIME_OF_DAY = "http://www.icardea.at/phrs/instances/InTheMorning";
    public static final String DOSE_UNITS = "http://www.icardea.at/phrs/instances/pills";
    public static final String MED_REASON = "http://www.icardea.at/phrs/instances/Cholesterol";
    private static PhrsStoreClient phrsClient = null;
    private static GenericTriplestore triplestore;
    private static boolean cleanEnv = false;

    public PhrsClientInterop() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        phrsClient = PhrsStoreClient.getInstance();
        triplestore = phrsClient.getGenericTriplestore();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (phrsClient != null) {
            Query query = phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri =", USER);
            phrsClient.getPhrsDatastore().delete(query);

            phrsClient.shutdown();
            if (cleanEnv && triplestore != null) {
                ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
            }
            phrsClient = null;
        }
        triplestore = null;
    }
    /*
     * @Before public void setUp() { phrsClient = PhrsStoreClient.getInstance();
     * Query query =
     * phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri
     * =", USER); phrsClient.getPhrsDatastore().delete(query); triplestore =
     * phrsClient.getGenericTriplestore();
     *
     * }
     *
     * @After public void tearDown() throws GenericRepositoryException,
     * TripleException, IllegalAccessException, InstantiationException,
     * Exception { try { PhrsStoreClient phrsClient =
     * PhrsStoreClient.getInstance(); if (phrsClient != null) { Query query =
     * phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri
     * =", USER); phrsClient.getPhrsDatastore().delete(query); } } catch
     * (Exception e) { e.printStackTrace(); }
     *
     * try { //clean up if (triplestore != null) {
     * ((GenericTriplestoreLifecycle) triplestore).shutdown();
     * ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment(); }
     *
     * } catch (Exception e) { e.printStackTrace(); }
     *
     * try { if (phrsClient != null) { Query query =
     * phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri
     * =", USER); phrsClient.getPhrsDatastore().delete(query); } } catch
     * (Exception e) { // e.printStackTrace();shows a distracting error }
     *
     * try { if (triplestore != null) { ((GenericTriplestoreLifecycle)
     * triplestore).shutdown(); ((GenericTriplestoreLifecycle)
     * triplestore).cleanEnvironment();
     *
     * }
     *
     * // if (phrsClient != null) { // phrsClient.setTripleStore(null); // }
     *
     * } catch (Exception e) { //e.printStackTrace(); shows a distracting error
     * } triplestore = null; phrsClient = null;
    }
     */

    @Ignore
    @Test
    public void testSavePhrResourceAndFind() throws Exception {
        //PhrsStoreClient phrsClient = PhrsStoreClient.getInstance();
        System.out.println("testSavePhrResourceAndFind_Message");
        //  phrsClient = PhrsStoreClient.getInstance();
        assertNotNull("phrsClient NULL", phrsClient);
        InteropAccessService iaccess = phrsClient.getInteropService();
        assertNotNull("CommonDao null via InteropAccessService ", iaccess.getCommonDao());
        MedicationTreatment res = new MedicationTreatment();

        res.setOwnerUri(USER);
        res.setCreatorUri(USER);
        res.setBeginDate(new Date());
        //res.setEndDate(new Date());
        res.setLabel("phr user drug test 1 ");

        res.setStatus(Constants.STATUS_RUNNING);
        res.setReasonCode(MED_REASON);

        //res.setReasonCode(value);
        //res.setPrescribedByName("");
        MedicationTreatmentMatrix mtm = new MedicationTreatmentMatrix();

        mtm.setDosage(3.0d);//double

        mtm.setDosageQuantity("5");
        mtm.setDosageUnits(DOSE_UNITS);

        mtm.setDosageInterval(DOSE_INTERVAL);
        mtm.setDosageTimeOfDay(DOSE_TIME_OF_DAY);

        res.setTreatmentMatrix(mtm);

        assertNotNull(iaccess.getCommonDao());

        try {
            phrsClient.getCommonDao().crudSaveResource(res, USER, USER);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String resourceUri = res.getResourceUri();
        Date created = res.getCreateDate();
        //System.out.println("resourceUri = " + resourceUri + " created=" + created);
        assertNotNull("resourceUri is null, not saved" + resourceUri);
        assertNotNull("create date is null, not saved" + created);


        //List list1 =phrsClient.getPhrsRepositoryClient().crudReadResources(USER, MedicationTreatment.class);

        boolean found = false;
        List list = null;
        try {
            //list = phrsClient.getCommonDao().crudReadResources(USER, (Object) MedicationTreatment.class);
            list = phrsClient.getCommonDao().crudReadMedicationResources(USER);

            assertNotNull("Null, Expected search results for resource type and user ", list);
            assertTrue("Empty, Expected search results for resource type and user ", !list.isEmpty());

            for (Object itemObj : list) {
                MedicationTreatment item = (MedicationTreatment) itemObj;
                System.out.println("item resUri=" + item.getResourceUri());

                if (item.getResourceUri() != null && resourceUri.equals(item.getResourceUri())) {
                    found = true;
                } else {
                    if (item.getResourceUri() == null) {
                        System.out.println("item  resUri=NULL");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("Resource saved was NOT found again", found);

    }

    @Test
    public void testRegisterPid() {
        System.out.println("testRegisterPid");
        //register PID
        //In setup this was done actorClient.register(PROTOCOL_ID_NAMESPACE, USER, USER_PROTOCOL_ID);//unit test, use pr
        String pid = "PID123";
        //PhrsStoreClient phrsStoreClient = PhrsStoreClient.getInstance();
        phrsClient.getInteropClients().registerProtocolId(USER, pid, null);
        //commonDao.registerProtocolId(user.getOwnerUri(), protocolId, null);

        String actual = phrsClient.getInteropClients().getProtocolId(USER);  //default namespace
        System.out.println("testRegisterPid actual" + actual + " expected " + pid);
        assertNotNull("Protocol ID null ", actual);
        assertEquals("", pid, actual);


    }

    public void makePHRSRequest_PCC09(String protocolId, String careProvisioncode) {
        InteropClients interopClients = phrsClient.getInteropClients();
        PHRSRequestClient requestClient = interopClients.getPHRSRequestClient();
        String REPLY_URI = "http://localhost:8989/testws/pcc10";
        String CODE = careProvisioncode != null ? careProvisioncode : careProvisioncode;


        try {
            String stuff = requestClient.addPHRSRequest(REPLY_URI, protocolId, CODE);
        } catch (TripleException e) {
            e.printStackTrace();
            fail("Cannot add PHRS Request simulation");
        }
    }

    @Test
    public void testNotifySubscribers() {
        System.out.println("testNotifySubscribers");
        String protocolId = "PROTOCOLID_" + USER;
        makePHRSRequest_PCC09(protocolId, "COBSCAT");

        CommonDao commonDao = phrsClient.getCommonDao();
        commonDao.registerProtocolId(USER, protocolId, null);

        //CoreTestData.addTestBasicHealthVitalsData(USER);
        //Need to pass each throu
        ObsVitalsBloodPressure bp1 = new ObsVitalsBloodPressure();
        bp1.setSystolic(160);
        bp1.setDiastolic(90);
        bp1.setBeginDate(new Date());
        bp1.setEndDate(new Date());
        bp1.setNote("note id " + CoreTestData.makeSimpleId());
        bp1.setSystemNote(bp1.getNote());
        bp1.setOwnerUri(USER);
        //must look like saved, otherwise message fails
        bp1.setResourceUri(USER + "_" + UUID.randomUUID().toString());
        InteropAccessService iaccess = phrsClient.getInteropService();
        Object obj = (Object) bp1;
        Map uris = iaccess.sendMessages(obj);

        //commonDao.crudSaveResource(bp1, USER, USER);
        //saves and sends message...and notify...

        assertNotNull("uris null ", uris);
        assertFalse("uris empty ", uris.isEmpty());

        //interopClients.notifyInteropMessageSubscribersByPhrId(protocolId);

    }

    @Test
    public void testMessageVitalsBP() {
        System.out.println("testMessageVitalsBP");
        String theUser = USER + "_BP";
        String protocolId = "PROTOCOLID_" + theUser;
        makePHRSRequest_PCC09(protocolId, "COBSCAT");
        CommonDao commonDao = phrsClient.getCommonDao();
        commonDao.registerProtocolId(theUser, protocolId, null);

        ObsVitalsBloodPressure item = new ObsVitalsBloodPressure();
        item.setSystolic(160);
        item.setDiastolic(90);
        item.setBeginDate(new Date());
        item.setEndDate(new Date());
        item.setNote("note id " + CoreTestData.makeSimpleId());
        item.setSystemNote(item.getNote());
        item.setOwnerUri(theUser);
        //must look like saved, otherwise message fails
        item.setResourceUri(theUser + "_" + UUID.randomUUID().toString());

        InteropAccessService iaccess = phrsClient.getInteropService();
        //String testme=iaccess.testme();
        //assertEquals("","testme",testme);
        Object obj = (Object) item;
        Map uris = iaccess.sendMessages(obj);
        assertNotNull("uris null ", uris);
        assertFalse("uris empty ", uris.isEmpty());
    }

    @Test
    public void testMessageVitalsProblem() {
        System.out.println("testMessageVitalsProblem");
        String theUser = USER + "_Problem";
        String protocolId = "PROTOCOLID_" + theUser;
        makePHRSRequest_PCC09(protocolId, "COBSCAT");
        CommonDao commonDao = phrsClient.getCommonDao();
        commonDao.registerProtocolId(theUser, protocolId, null);

        ObsProblem item = new ObsProblem();

        item.setCode(Constants.HL7V3_FEVER);
        item.setStatus(Constants.STATUS_COMPELETE);

        item.setBeginDate(new Date());
        item.setEndDate(new Date());
        item.setNote("note id " + CoreTestData.makeSimpleId());
        item.setSystemNote(item.getNote());
        item.setOwnerUri(theUser);
        //must look like saved, otherwise message fails
        item.setResourceUri(theUser + "_" + UUID.randomUUID().toString());

        InteropAccessService iaccess = phrsClient.getInteropService();
        //String testme=iaccess.testme();
        //assertEquals("","testme",testme);
        Object obj = (Object) item;
        Map uris = iaccess.sendMessages(obj);
        assertNotNull("uris null ", uris);
        assertFalse("uris empty ", uris.isEmpty());
    }

    @Test
    public void testMessageVitalsBW() {
        System.out.println("testMessageVitalsBW");
        String theUser = USER + "_BW";
        String protocolId = "PROTOCOLID_" + theUser;
        makePHRSRequest_PCC09(protocolId, "COBSCAT");
        CommonDao commonDao = phrsClient.getCommonDao();
        commonDao.registerProtocolId(theUser, protocolId, null);


        ObsVitalsBodyWeight item = new ObsVitalsBodyWeight();
        item.setBodyHeight(171.2d);
        item.setBodyWeight(70.3d);
        item.setBeginDate(new Date());
        item.setEndDate(new Date());
        item.setNote("note id " + CoreTestData.makeSimpleId());
        item.setSystemNote(item.getNote());
        item.setOwnerUri(theUser);
        //must look like theUser, otherwise message fails
        item.setResourceUri(theUser + "_" + UUID.randomUUID().toString());

        InteropAccessService iaccess = phrsClient.getInteropService();
        //String testme=iaccess.testme();
        //assertEquals("","testme",testme);
        Object obj = (Object) item;
        Map uris = iaccess.sendMessages(obj);
        assertNotNull("uris null ", uris);
        assertFalse("uris empty ", uris.isEmpty());
    }

    @Test
    public void testMessageVitalsADL() {
        System.out.println("testMessageVitalsADL");
        String theUser = USER + "_ADL";
        String protocolId = "PROTOCOLID_" + theUser;
        makePHRSRequest_PCC09(protocolId, "COBSCAT");
        CommonDao commonDao = phrsClient.getCommonDao();
        commonDao.registerProtocolId(theUser, protocolId, null);

        ProfileActivityDailyLiving item = new ProfileActivityDailyLiving();

        item.setCode("http://www.icardea.at/phrs/instances/DifficultiesPerformingGardeningActivities");
        item.setStatus("http://www.icardea.at/phrs/instances/ICanDoPHRSCode");

        item.setBeginDate(new Date());
        item.setEndDate(new Date());
        item.setNote("note id " + CoreTestData.makeSimpleId());
        item.setSystemNote(item.getNote());
        item.setOwnerUri(theUser);
        //must look like theUser, otherwise message fails
        item.setResourceUri(theUser + "_" + UUID.randomUUID().toString());

        InteropAccessService iaccess = phrsClient.getInteropService();
        //String testme=iaccess.testme();
        //assertEquals("","testme",testme);
        Object obj = (Object) item;
        Map uris = iaccess.sendMessages(obj);
        assertNotNull("uris null ", uris);
        assertFalse("uris empty ", uris.isEmpty());
    }

    @Test
    public void testNotifySubscribersByResourceChange() {
        String theUser = USER + "_ByResourceChange";
        System.out.println("testNotifySubscribersByResourceChange");
        InteropClients interopClients = phrsClient.getInteropClients();

        String protocolId = "PROTOCOLID_" + theUser;

        CommonDao commonDao = phrsClient.getCommonDao();
        commonDao.registerProtocolId(theUser, protocolId, null);

        //CoreTestData.addTestBasicHealthVitalsData(USER);
        //Need to pass each throu
        ObsVitalsBloodPressure bp1 = new ObsVitalsBloodPressure();
        bp1.setSystolic(200);
        bp1.setDiastolic(65);
        bp1.setBeginDate(new Date());
        bp1.setEndDate(new Date());
        bp1.setNote("note id " + CoreTestData.makeSimpleId());
        bp1.setSystemNote(bp1.getNote());
        bp1.setOwnerUri(theUser);
        //bp1.setResourceUri(theUser+"_"+UUID.randomUUID().toString());

        //Map uris = phrsClient.getInteropService().sendMessages(bp1);

        commonDao.crudSaveResource(bp1, theUser, theUser);
        //saves and sends message...and notify...

        assertNotNull("Saved resource resourceUri is  null ", bp1.getResourceUri());


        //interopClients.notifyInteropMessageSubscribersByPhrId(protocolId);

    }
}
