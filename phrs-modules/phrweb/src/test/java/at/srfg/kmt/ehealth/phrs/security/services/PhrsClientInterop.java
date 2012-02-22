/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.PHRSRequestClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import org.apache.commons.beanutils.DynaBean;
import org.junit.*;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients;
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;
import com.google.code.morphia.query.Query;

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
            //Query query = phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri =", USER);
            //phrsClient.getPhrsDatastore().delete(query);

           
            phrsClient.shutdown();
           // if (cleanEnv && triplestore != null) {
           //     ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
           // }
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
     * } triplestore = null; phrsClient = null; }
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
    public static final String DRUG_1_QUANTITY = "2";
    public static final String DRUG_2_QUANTITY = "12";
    public static final String DRUG_1_NAME = "Prednisone";
    public static final String DRUG_2_NAME = "Concor 2";
    public static final String DRUG_1_CODE = "C0032952";
    public static final String DRUG_2_CODE = "C0110591";
    //addNewMessagesEhr("xxxx", "zzzzz", "5", "14");

    public void addNewMessagesEhr(String ownerUri,String phrResourceUri, String phrResourceUri2, String drug1_quantity, String drug2_quantity) throws TripleException, IllegalAccessException, InstantiationException {
        InteropClients interopClients = phrsClient.getInteropClients();
        MedicationClient medicationClient = interopClients.getMedicationClient();
        medicationClient.setCreator(Constants.EXTERN);//simulate extern


        final String resourceURI_1 =
                medicationClient.addMedicationSign(
                ownerUri,
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
                ownerUri,
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
        /*
         * if (printDynabean) { for (DynaBean dynaBean : beans) { final String
         * toString = DynaBeanUtil.toString(dynaBean);
         * System.out.println(toString); } }
         */


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


        InteropClients interopClients = phrsClient.getInteropClients();
        interopClients.registerProtocolId(USER, protocolId, null);
        //CommonDao commonDao = phrsClient.getCommonDao();
        //commonDao.registerProtocolId(USER, protocolId, null);


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
        //sends messages and then notifies
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
        InteropClients interopClients = phrsClient.getInteropClients();
        interopClients.registerProtocolId(USER, protocolId, null);

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
        InteropClients interopClients = phrsClient.getInteropClients();
        interopClients.registerProtocolId(USER, protocolId, null);

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
        InteropClients interopClients = phrsClient.getInteropClients();
        interopClients.registerProtocolId(USER, protocolId, null);


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
        InteropClients interopClients = phrsClient.getInteropClients();
        interopClients.registerProtocolId(USER, protocolId, null);

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
    public void testCoreTestDataWithNotify(){
        makePHRSRequest_PCC09("191", "COBSCAT");
        
        CoreTestData.createTestUserData();
        CommonDao commonDao = phrsClient.getCommonDao();
        
        String greetName= commonDao.getUserGreetName("phrtest");
        System.out.println("greetName= "+greetName);
        assertNotNull("Greet name null",greetName);
        assertFalse("Greet name emtpy",greetName.isEmpty()); 
    }
        @Test
    public void testContactInfo(){
        CommonDao commonDao = phrsClient.getCommonDao();
      
        String greetName= commonDao.getUserGreetName("phrtest");
        System.out.println("greetName= "+greetName);
        assertNotNull("Greet name null",greetName);
        assertFalse("Greet name emtpy",greetName.isEmpty()); 
    }
    @Test
    public void testSingleResourceCRUD(){
            CommonDao commonDao = phrsClient.getCommonDao(); 
            String loginUserIdOwnerUri="user_testSingleResource";
            PhrFederatedUser user = commonDao.getPhrUser(loginUserIdOwnerUri, true);                     
            user.setOwnerUri(loginUserIdOwnerUri);
            user.setCreatorUri(user.getOwnerUri());
            user.setUserId(loginUserIdOwnerUri);
            user.setIdentifier(loginUserIdOwnerUri);//init to local identifier, but could later assign to an OpenId.         
            commonDao.crudSaveResource(user, user.getOwnerUri(), "CoreTestData createTestUserData");
            
            
            // 
            PhrFederatedUser user2 = commonDao.getPhrUser(loginUserIdOwnerUri, true);                     
            user2.setOwnerUri(loginUserIdOwnerUri);
            user2.setCreatorUri(loginUserIdOwnerUri);
            user2.setUserId(loginUserIdOwnerUri);
            user2.setIdentifier(loginUserIdOwnerUri);//init to local identifier, but could later assign to an OpenId.         
            commonDao.crudSaveResource(user2, user2.getOwnerUri(), "CoreTestData createTestUserData");
              List list= commonDao.getPhrsRepositoryClient().crudReadResources(loginUserIdOwnerUri, (Object)PhrFederatedUser.class);
            if(list!=null){
                for(Object obj:list){
                    PhrFederatedUser item=(PhrFederatedUser)obj;
                    System.out.println("item: "+item.getOwnerUri()+" dbId:" +item.getId());
                }
            }          
            List list2= commonDao.getPhrsRepositoryClient().crudReadResources(loginUserIdOwnerUri, (Object)PhrFederatedUser.class);
//            if(list!=null){
//                for(Object obj:list){
//                    PhrFederatedUser item=(PhrFederatedUser)obj;
//                    System.out.println("item: "+item.getOwnerUri()+" dbId:" +item.getId());
//                }
//            }
            
            System.out.println("DB IDs second:"+user2.getId().toString()+ " first:"+user.getId().toString());
            assertEquals("DB ID not same:",user2.getId().toString(),user.getId().toString());
            
            PhrFederatedUser user3 = commonDao.getPhrUser(loginUserIdOwnerUri, true);                     
            user3.setOwnerUri(loginUserIdOwnerUri);
            user3.setCreatorUri(loginUserIdOwnerUri);
            user3.setUserId(loginUserIdOwnerUri);
            user3.setIdentifier(loginUserIdOwnerUri);//init to local identifier, but could later assign to an OpenId.         
            commonDao.crudSaveResource(user3, user3.getOwnerUri(), "CoreTestData createTestUserData");
            
            list= commonDao.getPhrsRepositoryClient().crudReadResources(loginUserIdOwnerUri, (Object)PhrFederatedUser.class);
            if(list!=null){
                for(Object obj:list){
                    PhrFederatedUser item=(PhrFederatedUser)obj;
                     System.out.println("item: "+item.getOwnerUri()+" dbId:" +item.getId());
                }
            }
    }
    @Test
    public void testRegisterProtocolIDMultiple(){
        //
        
        InteropClients interopClients = phrsClient.getInteropClients();
        String phrId="testRegisterProtocolIDMultipleSameUser";//+//+UUID.randomUUID().toString();
        
        String pid_1="A1_1_PROTOCOLID_"+phrId;
        
        interopClients.registerProtocolId(phrId, pid_1, null);
        
        String pid_expected=interopClients.getProtocolId(phrId);
        System.out.println("pid_expected "+pid_expected+" phrId="+phrId);
        assertEquals("Found PID not equal to pid_1",pid_1,pid_expected);
        
        String pid_2="A1_2_PROTOCOLID_FINAL";//+phrId;
        
        interopClients.registerProtocolId(phrId, pid_2, null);
        pid_expected=interopClients.getProtocolId(phrId);
        System.out.println("pid_expected "+pid_expected+" phrId="+phrId);
        assertEquals("Found PID not equal to new pid_2",pid_2,pid_expected);
       
        
    }
     
        @Test
    public void testRegisterGetUser(){
        //
        
        InteropClients interopClients = phrsClient.getInteropClients();
        String phrId="testRegisterProtocolIDMultipleSameUser";//+//+UUID.randomUUID().toString();
       
        String expect="A1_2_PROTOCOLID_FINAL";
        
        String existingPid=interopClients.getProtocolId(phrId);
        System.out.println("existingPid "+existingPid+" FOR phrId="+phrId+" expectPID0"+expect);
       
        assertEquals("Found PID not equal to new pid_2",expect,existingPid);
       
        
    }
       @Test
    public void testRegisterProtocolIDMultipleTimes(){
        //
        
        InteropClients interopClients = phrsClient.getInteropClients();
        String phrId="A1_testRegisterProtocolIDMultipleTimes"+new Date().getTime();//+//+UUID.randomUUID().toString();
        
        String pid_1="A1_1_PROTOCOLID_"+phrId;
        
        interopClients.registerProtocolId(phrId, pid_1, null);
        
        String pid_expected=interopClients.getProtocolId(phrId);
        System.out.println("pid_expected "+pid_expected+" phrId="+phrId);
        assertEquals("Found PID not equal to pid_1",pid_1,pid_expected);
        
        String pid_2="A1_2_PROTOCOLID_"+phrId;
        
        interopClients.registerProtocolId(phrId, pid_2, null);
        pid_expected=interopClients.getProtocolId(phrId);
        System.out.println("pid_expected "+pid_expected+" phrId="+phrId);
        assertEquals("Found PID not equal to new pid_2",pid_2,pid_expected);
       
        
    }
        @Test
    public void testRegisterProtocolIDMultipleTimes_CommonDao(){
        //
        InteropClients interopClients = phrsClient.getInteropClients();
        CommonDao commonDao = phrsClient.getCommonDao();
       
        String phrId="1_testRegisterProtocolIDMultipleTimes_CommonDao"+new Date().getTime();
        String pid_1="1_1_PROTOCOLID_"+phrId;
        
        commonDao.registerProtocolId(phrId, pid_1, null);
        
        String pid_expected=interopClients.getProtocolId(phrId);
        System.out.println("pid_expected "+pid_expected+" phrId="+phrId);
        assertEquals("Found PID not equal to pid_1",pid_1,pid_expected);
        
        String pid_2="1_2_PROTOCOLID_"+phrId;
        
        commonDao.registerProtocolId(phrId, pid_2, null);
        pid_expected=interopClients.getProtocolId(phrId);
        
        System.out.println("pid_expected "+pid_expected+" phrId="+phrId);
        assertEquals("Found PID not equal to new pid_2",pid_2,pid_expected);
       
        
    }
    @Test
    public void testNotifySubscribersByResourceChange() {
        String theUser = USER + "_ByResourceChange";
        System.out.println("testNotifySubscribersByResourceChange");
        InteropClients interopClients = phrsClient.getInteropClients();

        String protocolId = "PROTOCOLID_" + theUser;

      
        interopClients.registerProtocolId(USER, protocolId, null);

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
        CommonDao commonDao = phrsClient.getCommonDao();
        commonDao.crudSaveResource(bp1, theUser, theUser);
        //saves and sends message...and notify...

        assertNotNull("Saved resource resourceUri is  null ", bp1.getResourceUri());


        //interopClients.notifyInteropMessageSubscribersByPhrId(protocolId);

    }

    @Test
    public void testNotifyDirectly() {
        try {
            String user=USER+"_"+UUID.randomUUID().toString();
            addNewMessagesEhr(user,"med1", "med2", "5", "14");

            String protocolId = "PROTOCOLID_" +user ;
            makePHRSRequest_PCC09(protocolId, "MEDLIST");

            InteropClients interopClients = phrsClient.getInteropClients();
            interopClients.registerProtocolId(user, protocolId, null);

            //InteropClients interopClients = phrsClient.getInteropClients();

            //interopClients.notifyInteropMessageSubscribersByPhrId(protocolId);
            String selectedCareProvisionCode = null;
            notifyInteropMessageSubscribers(selectedCareProvisionCode, protocolId);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public PHRSRequestClient getPHRSRequestClient() {
        return phrsClient.getInteropClients().getPHRSRequestClient();
    }

    public DynaBeanClient getDynaBeanClient() {
        return phrsClient.getInteropClients().getDynaBeanClient();
    }
    /*
     * Usually there is NO selectedCareProvisionCode, just fire on all Or we
     * pass the resourceType and select the prov code
     */

    public void notifyInteropMessageSubscribers(String selectedCareProvisionCode, String protocolId) throws Exception {
        PHRSRequestClient requestClient = getPHRSRequestClient();
        DynaBeanClient beanClient = getDynaBeanClient();
        final Iterable<String> resources = requestClient.getAllPHRSRequests();
        for (String resource : resources) {
            final DynaBean request = beanClient.getDynaBean(resource);

            boolean sendNotification = false;
            String careProvisionCode = (String) request.get(Constants.HL7V3_CARE_PROVISION_CODE);
            //filter on careProvisionCode  ?
            if (selectedCareProvisionCode == null) {
                sendNotification = true;
            } else {
                if (careProvisionCode == null) {
                    sendNotification = false;
                } else if (careProvisionCode.equalsIgnoreCase(selectedCareProvisionCode)) {
                    sendNotification = true;
                }

            }
            String id = (String) request.get("http://www.icardea.at/phrs/actor#protocolId");
            //filter on protocolId ?
            if (id == null) {
                sendNotification = false;
            } else if (protocolId != null) {
                if (id.equals(protocolId)) {
                    sendNotification = true;
                } else {
                    sendNotification = false;
                }
            }

            if (sendNotification) {
                final String wsAdress =
                        (String) request.get("http://www.icardea.at/phrs/hl7V3#wsReplyAddress");


                final Map<String, String> properties = new HashMap<String, String>();
                properties.put("patientId", id);
                properties.put("patientNames", "patientNames");

                //Care Provision Code
                properties.put("careProvisionCode", careProvisionCode);
                properties.put("responseEndpointURI", wsAdress);
                int port = ConfigurationService.getInstance().getSubscriberSocketListnerPort();

                notifyInteropMessageSubscribers("localhost", port, properties);


            }
            System.out.println("END notifyInteropMessageSubscribers notify=" + sendNotification + " protocolId" + protocolId + " selectedCareProvisionCode " + selectedCareProvisionCode);

        }
        System.out.println("Finished - Notified Core after Loading test data ");

    }

    public void notifyInteropMessageSubscribers(String host, int port, Map<String, String> params) {
        System.out.println("Tries to dispach this properties {}." + params);
        try {
            final Socket socket = new Socket(host, port);
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(params);
             System.out.println("Prameters : {}  dispathed." + params);
        } catch (Exception e) {
            System.out.println("Prameters : {} can not be dispathed." + params);
            System.out.println(e.getMessage() + e);
        }

        System.out.println("The  was distpatched" + params);
    }
  
}
