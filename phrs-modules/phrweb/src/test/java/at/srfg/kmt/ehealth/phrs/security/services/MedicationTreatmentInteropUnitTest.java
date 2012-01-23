/*
 * Project :iCardea
 * File : MedicationClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 31, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.DynaProperty;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DynaBeanUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import org.apache.commons.beanutils.DynaBean;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatmentMatrix;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;
import java.util.*;

/**
 * Used to prove the functionality for the
 * <code>MedicationClient</code>.
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 * @see MedicationClient
 */
public class MedicationTreatmentInteropUnitTest {

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

    public MedicationTreatmentInteropUnitTest() {
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
        System.out.println("--------------initSuite------------------");
        phrsClient = PhrsStoreClient.getInstance(true);
        triplestore = phrsClient.getGenericTriplestore();

        //ds = phrsClient.getPhrsDatastore();
        //triplestore = PhrsStoreClient.getInstance().getTripleStore();
        iaccess = phrsClient.getInteropService();
        medicationClient = new MedicationClient(triplestore);


        //addNewMessagesEhr("1", "2");

        //addNewMessagesEhr();
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
        //System.out.println("shutdownSuite");

        try {
            //clean up 

            Query query = phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri =", USER);
            phrsClient.getPhrsDatastore().delete(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //ds=null;
        try {
            //System.out.println("shutdown triplestore");
            ((GenericTriplestoreLifecycle) phrsClient.getGenericTriplestore()).shutdown();
            ((GenericTriplestoreLifecycle) phrsClient.getGenericTriplestore()).cleanEnvironment();
            triplestore = null;
            phrsClient.setTripleStore(null);
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
        //System.out.println("end    shutdownSuite");
    }
    /*
     * final String toString = DynaBeanUtil.toString(dynaBean);
     * System.out.println(toString);
     */

    /**
     * Add 2 medications not yet known by PHR portal
     *
     * @throws TripleException
     */// GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, JAXBException
    public void addNewMessagesEhr(String label1, String label2, String phrResourceUri) throws TripleException, IllegalAccessException, InstantiationException {

        final String resourceURI_1 =
                medicationClient.addMedicationSign(
                USER,
                NOTE,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                DOSE_INTERVAL,//MyFreqency",
                Constants.HL7V3_ORAL_ADMINISTRATION,
                "1",
                DOSE_UNITS,//"pillURI",
                phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX+phrResourceUri : "EHRDrug_" + label1);

        final String resourceURI_2 =
                medicationClient.addMedicationSign(
                USER,
                NOTE,
                Constants.STATUS_RUNNING,
                "201006010000",
                "201006010000",
                DOSE_INTERVAL,//MyFreqency",
                Constants.HL7V3_ORAL_ADMINISTRATION,
                "2",
                DOSE_UNITS,//"pillURI",
                phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX+phrResourceUri : "EHRDrug " + label2);

        final Iterable<String> uris = medicationClient.getMedicationURIsForUser(USER);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }

        for (DynaBean dynaBean : beans) {
            final String toString = DynaBeanUtil.toString(dynaBean);
            System.out.println(toString);
        }
        //assertNotNull(resourceURI_1);
        //assertNotNull(resourceURI_2);
        //return resourceURI;

    }

    @Test
    public void testAddNewMessagesEhr() throws TripleException, IllegalAccessException, InstantiationException {

        final String resourceURI_1 =
                medicationClient.addMedicationSign(
                USER,
                NOTE,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                "DOSE_INTERVAL",//MyFreqency",
                Constants.HL7V3_ORAL_ADMINISTRATION,
                "1",
                "pillURI",
                "testaddDrug_1");

        final String resourceURI_2 =
                medicationClient.addMedicationSign(
                USER,
                NOTE,
                Constants.STATUS_RUNNING,
                "201006010000",
                "201006010000",
                "DOSE_INTERVAL",//MyFreqency",
                Constants.HL7V3_ORAL_ADMINISTRATION,
                "2",
                "pillURI",
                "testaddDrug 2");

        final Iterable<String> uris = medicationClient.getMedicationURIsForUser(USER);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }

        for (DynaBean dynaBean : beans) {
            final String toString = DynaBeanUtil.toString(dynaBean);
            System.out.println(toString);
        }
        //assertNotNull(resourceURI_1);
        //assertNotNull(resourceURI_2);
        //return resourceURI;

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

        mtm.setDosageInterval(DOSE_INTERVAL);
        mtm.setDosageTimeOfDay(DOSE_TIME_OF_DAY);

        res.setTreatmentMatrix(mtm);




        //save simulation
        res.setResourceUri("theResourceUri_" + UUID.randomUUID().toString());
        res.setCreateDate(new Date());
        res.setModifyDate(res.getCreateDate());


        return res;

    }

//	@Test
//	public void testTransformStatus() {
//		
//	}
//
//	@Test
//	public void testTransformCode() {
//		
//	}
//
//	@Test
//	public void testTransformCategory() {
//		
//	}
//
//	@Test
//	public void testTranformMedicationAdminRoute() {
//		
//	}
//
//
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
     * " /parsed" + result); assertEquals("reference note", expect, result);
    }
     */
    @Test
    public void testParseReferenceNote() {
        System.out.println("testParseReferenceNote");

        String expect = "1234";
        String note = InteropAccessService.REFERENCE_NOTE_PREFIX + expect;

        String result = InteropAccessService.parseReferenceNote(note);
        System.out.println(note + " /parsed" + result);
        assertEquals("reference note", expect, result);
    }

    @Test
    public void testFindInteropMessagesForUser() throws Exception {

        addNewMessagesEhr("1", "2", "123-resuri-567");
        String phrClass = PHRS_RESOURCE_CLASS;
        String user = USER;
        System.out.println("testFindInteropMessagesForUser");
        Iterable<String> results = iaccess.findInteropMessagesForUser(user, phrClass);
        int count = 0;
        for (String result : results) {
            count++;
            System.out.println("all found messages count" + count + " uri=" + result);

        }
        assertTrue("No messages found by interop for user and resource type, expect two ", count > 1);
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

    public static void showDynaProperties(DynaBean dynaBean) {
        if (dynaBean != null) {
            DynaProperty[] props = dynaBean.getDynaClass().getDynaProperties();
            System.out.println("------------------");
            System.out.println("showDynaProperties:");
            System.out.println("dynaBean.getClass()=" + dynaBean.getClass());

            for (DynaProperty prop : props) {
                System.out.println(" dyna propName=" + prop.getName() + " toString=" + prop.toString());
            }
            System.out.println("------------------");
        }

    }

    @Test
    public void testSendInteropMessages() {
        System.out.println("testSendInteropMessages");

        Map<String, String> uris = iaccess.sendMessages(phrMed_1);
        assertNotNull("Messages sent but no message URIs returned!", uris);

        showMap("message uris by type", uris);
        //showList("results",list);

        assertTrue(!uris.isEmpty());
        assertTrue(uris.size() > 0);


        String user = USER;
        /*
         * Iterable<String> results = iaccess.findInteropMessagesForUser(user,
         * phrClass); int count = 0; for (String result : results) { count++;
         * System.out.println("all found messages count" + count + " uri=" +
         * result);
         *
         * }
         * assertTrue("No messages found by interop for user and resource type",
         * count > 0);
         */

        String interopRef = iaccess.findMessageWithReference(
                user, phrMed_1.getOwnerUri(), PHRS_RESOURCE_CLASS, null);

        //assertNotNull("cannot find message with note reference to resource Uri ="+phrMed_1.getResourceUri(),interopRef);
        //send message
        //know resourceUri
        //find it


    }
//    @Test 
//    public void testSendInteropMessagesDirectCode(){
//        
//        List<MedicationTreatment> list = doInteropMessage(phrMed_1);
//        
//    }

    @Test
    public void testImportMessagesDirectCode() throws Exception {
        System.out.println("testImportMessagesDirectCode");
        addNewMessagesEhr("1", "2", null);//an EHR record
        List<MedicationTreatment> imported = importNewMessages(USER, PHRS_RESOURCE_CLASS, true);

        assertNotNull(imported);
        assertTrue(imported.size() > 0);
        assertTrue(imported.size() > 2);


    }
    public static final String MED_DRUG_NAME_URI = "http://www.icardea.at/phrs/hl7V3#drugName";

    public List importNewMessages(String ownerUri, String phrsClass, boolean importMessage) throws Exception {
        addNewMessagesEhr("1", "2", null);
        List list = new ArrayList();
        if (ownerUri != null && phrsClass != null) {

            final MultiIterable result = new MultiIterable();
            final Map<String, String> queryMap = new HashMap<String, String>();
            try {
                Iterable<String> results = iaccess.findInteropMessagesForUser(ownerUri, phrsClass);
                //check each results, has it been tagged?

                //import the message, and also save it back to the Interop Service to tag it and make other listeners aware of it.
                if (results != null) {
                    //transform message to

                    DynaBeanClient dynaBeanClient = iaccess.getInteropClients().getDynaBeanClient();

                    for (String messageResourceUri : results) {
                        try {
                            DynaBean dynaBean = dynaBeanClient.getDynaBean(messageResourceUri);

                            showDynaProperties(dynaBean);
                            System.out.println("importNewMessages getDynaClass()= " + dynaBean.getDynaClass().getName());

                            // DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
                            //http://www.icardea.at/phrs#owner
                            Object owner = dynaBean.get(Constants.OWNER);
                            System.out.println("importNewMessages owner= " + dynaBean.get(Constants.OWNER));
                            System.out.println("importNewMessages drug name= " + dynaBean.get(MED_DRUG_NAME_URI));

                            System.out.println("importNewMessages dynabean= " + dynaBean.toString());
                            //String referenceNote= bean.get()
                            boolean isNewMessage = false;
                            Object creator = dynaBean.get(Constants.CREATOR);
                            System.out.println("importNewMessages creator= " + dynaBean.get(Constants.CREATOR));
                            Object referenceNote = null;
                            try {
                                referenceNote = dynaBean.get(Constants.SKOS_NOTE);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // LOGGER.error(' message error, interop ownerUri= '+ownerUri+" messageResourceUri="+messageResourceUri, e)
                            }
                            if (referenceNote != null) {
                                String aboutResourceUri = iaccess.parseReferenceNote((String) referenceNote);

                                if (aboutResourceUri != null && aboutResourceUri.length() != 0) {
                                    isNewMessage = false;
                                } else {
                                    isNewMessage = true;
                                }
                            }

                            if (isNewMessage) {
                                Object repositoryObject = iaccess.transformInteropMessage(ownerUri, phrsClass, dynaBean, messageResourceUri);
                                if (repositoryObject != null) {
                                    list.add(messageResourceUri);
                                }
                                if (importMessage) {
                                    list.add(messageResourceUri);
                                    //save transformed resource to local store
                                    //the resourceUri issue
                                    iaccess.getCommonDao().crudSaveResource(repositoryObject, ownerUri, "interopservice");
                                    //send message to interop service
                                    //Map map=
                                    iaccess.sendMessages(repositoryObject);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            // LOGGER.error(' message error, interop ownerUri= '+ownerUri+" messageResourceUri="+messageResourceUri, e)
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (list != null) {
            System.out.println("importNewMessages list size= " + list.size());
        } else {
            System.out.println("importNewMessages list=NULL " + list);
        }


        return list;

    }

    /*
     * @Test public void testFindMessageWithReference() {
     * System.out.println("testFindMessageWithReference"); //add message
     * manually, note with reference //know resourceUri //find it
     *
     * }
     *
     * @Test public void testFindInteropMessageWithReferenceTag() {
     * System.out.println("testImportNewMessages"); }
     */
    @Test
    public void testImportNewMessages() throws Exception {

        String phrClass = PHRS_RESOURCE_CLASS;
        String user = USER;
        System.out.println("testImportNewMessages");

        //add fresh, there might be others but there should be at least 2
        addNewMessagesEhr("1", "2", null);
        List imported = iaccess.importNewMessages(USER, phrClass, false);//
        assertNotNull(imported);
        assertTrue(imported.size() > 0);
        assertTrue(imported.size() > 2);


    }

    /**
     * Adds a single medication and prove if this was done properly.
     *
     * @throws TripleException if this exception occurs then this test fails.
     */
    @Test
    public void testAddMedicationForOwner() throws TripleException {
        System.out.println("testAddMedicationForOwner");
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
                "testaddDrug");
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

    @Test
    public void testEmptyMedicationsForOwner() throws TripleException {
        System.out.println("testEmptyMedicationsForOwner");
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
}
