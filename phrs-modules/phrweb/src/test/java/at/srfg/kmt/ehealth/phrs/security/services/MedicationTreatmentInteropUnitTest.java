/*
 * Project :iCardea
 * File : MedicationClientUnitTest.java
 * Encoding : UTF-8
 * Date : Aug 31, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.presentation.utils.DynaUtil;
import com.google.code.morphia.query.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
/*
 The protocol id for the test patient is 191. 
 * It is available both the in the SALK installation 
 * and in the "public" test server  
 * 
 */

public class MedicationTreatmentInteropUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicationTreatmentInteropUnitTest.class.getName());
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
    //public static final String PILL_URI="http://www.icardea.at/phrs/instances/pills";
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

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException {
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

    @After
    public void tearDown() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, Exception {
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
 
    /**
     * Add 2 medications not yet known by PHR portal
     *
     * @throws TripleException
     */// GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, JAXBException

    public void addNewMessagesEhr(String label1, String label2, String phrResourceUri) throws TripleException, IllegalAccessException, InstantiationException {
        medicationClient.setCreator(Constants.EXTERN);//simulate extern
 
        final String resourceURI_1 =
                medicationClient.addMedicationSign(
                USER,
                NOTE,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                DOSE_INTERVAL,//FIXXME must use the buildFrequency
                Constants.HL7V3_ORAL_ADMINISTRATION,
                "1",
                DOSE_UNITS,//"pillURI",
                phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri : "EHRDrug_" + label1
                 //FIXXME does not create a name code node
                );

        final String resourceURI_2 =
                medicationClient.addMedicationSign(
                USER,
                NOTE,
                Constants.STATUS_RUNNING,
                "201006010000",
                "201006010000",
                DOSE_INTERVAL,//FIXXME use buildFrequency NONE
                Constants.HL7V3_ORAL_ADMINISTRATION,
                "2",
                DOSE_UNITS,//"pillURI",
                phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri : "EHRDrug " + label2
                //FIXXME does not create a name code node
                );

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
    

    }

    @Test
    public void testAddNewMessagesEhr() throws TripleException, IllegalAccessException, InstantiationException {

        try {
            final String resourceURI_1 =
                    medicationClient.addMedicationSign(
                    USER,
                    NOTE,
                    Constants.STATUS_COMPELETE,
                    "201006010000",
                    "201006010000",
                    DOSE_INTERVAL,//FIXXME build
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    "1",
                    DOSE_UNITS,
                    "testaddDrug_1");//FIXXME does not create a code node!

            assertNotNull(resourceURI_1);

            final String resourceURI_2 =
                    medicationClient.addMedicationSign(
                    USER,
                    NOTE,
                    Constants.STATUS_RUNNING,
                    "201006010000",
                    "201006010000",
                    DOSE_INTERVAL,//FIXXME Build
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    "12",
                    DOSE_UNITS,
                    "testaddDrug 2");//FIXXME does not create a code node!

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

            for (DynaBean dynaBean : beans) {
                final String toString = DynaBeanUtil.toString(dynaBean);
                System.out.println(toString);
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

        mtm.setDosageInterval(DOSE_INTERVAL);
        mtm.setDosageTimeOfDay(DOSE_TIME_OF_DAY);

        res.setTreatmentMatrix(mtm);

        //save simulation
        res.setResourceUri("theResourceUri_" + UUID.randomUUID().toString());
        res.setCreateDate(new Date());
        res.setModifyDate(res.getCreateDate());


        return res;

    }
    @Test
    public void testClientCreator(){
        String creator= this.medicationClient.getCreator();
        assertEquals("Error Expect PHR creator! ",PhrsConstants.INTEROP_CREATOR_DEFAULT_PHR,creator);
        
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
     * " /parsed" + result); assertEquals("reference note", expect, result); }
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


    @Test
    public void testSendInteropMessageMedicationClient() {
        System.out.println("testSendInteropMessages");

        Map<String, String> uris = iaccess.sendMessages(phrMed_1);
        
        assertNotNull("Messages sent but no message URIs returned!", uris);

        showMap("message uris by type", uris);
        //showList("results",list);

        assertTrue(!uris.isEmpty());
        assertTrue(uris.size() > 0);


        String user = USER;
   
        String interopRef = iaccess.findMessageWithReference(
                user, phrMed_1.getOwnerUri(), PHRS_RESOURCE_CLASS, null);

        assertNotNull("cannot find message with note reference to resource Uri ="+phrMed_1.getResourceUri(),interopRef);

    }
    
    @Test
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

            mtm.setDosageInterval(DOSE_INTERVAL);
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
            //note cannot be null
            String referenceNote = null;
            if (resource.getResourceUri() != null) {
                referenceNote = InteropAccessService.REFERENCE_NOTE_PREFIX + res.getResourceUri(); //'' //res.note is by default not sharable
            } else {
                referenceNote = "error";
            }

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
            assertNotNull(PhrsStoreClient.getInstance().getInteropClients().getMedicationClient());

            MedicationClient medicationclient = PhrsStoreClient.getInstance().getInteropClients().getMedicationClient();

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

                            DynaBeanUtil.toString(dynaBean);
                            System.out.println("importNewMessages getDynaClass()= " + dynaBean.getDynaClass().getName());

                            // DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
                            //http://www.icardea.at/phrs#owner
                            Object owner = DynaUtil.getStringProperty(dynaBean, Constants.OWNER);
                            System.out.println("importNewMessages owner= " + DynaUtil.getStringProperty(dynaBean, Constants.OWNER));
                            //System.out.println("importNewMessages drug name= " + DynaUtil.getStringProperty(dynaBean,MED_DRUG_NAME_URI));
//transformInteropMessage(ownerUri, phrsClass, dynaBean, messageResourceUri);
//DynaUtil.getStringProperty(dynaBean, Constants.CREATOR);

                            //String referenceNote= bean.get()
                            boolean isNewMessage = false;
                            Object creator = DynaUtil.getStringProperty(dynaBean, Constants.CREATOR);
                            System.out.println("importNewMessages creator= " + dynaBean.get(Constants.CREATOR));
                            Object referenceNote = null;
                            try {
                                referenceNote = DynaUtil.getStringProperty(dynaBean, Constants.SKOS_NOTE);
                            } catch (Exception e) {
                                System.out.println("referenceNote");
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
                                Object repositoryObject = transformInteropMessage(ownerUri, phrsClass, dynaBean, messageResourceUri);
                                if (repositoryObject != null) {
                                    list.add(messageResourceUri);
                                }
                                if (importMessage && repositoryObject != null) {
                                    //list.add(messageResourceUri);
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

            } catch (java.lang.Error e) {
                System.out.println("java lang error sesame error");
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

    public Object transformInteropMessage(String givenOwnerUri, String phrsClass, DynaBean dynabean, String messageResourceUri) {
        Object theObject = null;
        try {

            if (dynabean != null && phrsClass != null) {
                LOGGER.debug("phrsClass=" + phrsClass + " bean properties =");

                //switch (phrsClass) {
                //   case Constants.PHRS_MEDICATION_CLASS:
                String messageOwner = DynaUtil.getStringProperty(dynabean, Constants.OWNER, null);
                //Map props = bean.getProperties() //check ownerUri of message
                if (messageOwner != null && (messageOwner != givenOwnerUri)) {
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
                    String medName = this.getMapValue(attrMedName, Constants.HL7V3_DRUG_NAME, "Drug");
                    med.setTitle(medName);
                    String productCode = this.getMapValue(attrMedName, Constants.HL7V3_VALUE, null);
                    med.setProductCode(productCode);

                    //FIXXME 
                    med.getTreatmentMatrix().setAdminRoute(DynaUtil.getValueResourceUri(dynabean, Constants.HL7V3_ADMIN_ROUTE, null));
                    //this is a uri to another dynabean with dosage and units
                    //Constants.HL7V3_DOSAGE PHRS_MEDICATION_DOSAGE

                    //FIXXME DOSAGE is QUANTITY integer
                    //FIXXME dosage per unit not received from EHR, but web form no longer asks.

                    //Constants.PHRS_MEDICATION_DOSAGE

                    med.getTreatmentMatrix().setDosage(0d);

                    Map<String, String> doseAttrs = getMedicationDosageAttributes(dynabean);
                    String doseQuantity = this.getMapValue(doseAttrs, Constants.HL7V3_DOSAGE_VALUE, "0");
                    med.getTreatmentMatrix().setDosageQuantity(doseQuantity);

                    String doseUnits = this.getMapValue(doseAttrs, Constants.HL7V3_DOSAGE_UNIT, "http://www.icardea.at/phrs/instances/pills");
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
                        System.out.println("medication imported " + med.toString());
                        LOGGER.debug("medication imported " + med.toString());
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

  
//     public String buildDosage(String value, String unitURI) throws TripleException {
//
//        final String subject =
//                triplestore.persist(Constants.HL7V3_DOSAGE_VALUE, value, LITERAL);
//
//        // this can help to find a medication, there are alos other way 
//        // to do this (e.g. using the know templateRootID, for more )
//        // information about this please consult the documentation)
//        triplestore.persist(subject,
//                Constants.RDFS_TYPE,
//                Constants.PHRS_MEDICATION_DOSAGE_CLASS,
//                RESOURCE);
//
//        triplestore.persist(subject,
//                Constants.HL7V3_DOSAGE_UNIT,
//                unitURI,
//                RESOURCE);
//
//        return subject;
//
//    }
    /**
     * Get the dosage details and put them into a map
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
     * Get name and product code attributes. These might be stored simply as a name  or as complex subnode
     * @param medicationDynabean
     * @return 
     */
//Simple has HL7V3_DRUG_NAME string
//dyn MANUFACTURED_PRODUCT_CLASS http://www.icardea.at/phrs/hl7V3#manufacturedProduct 
//dyn ... MANUFACTURED_LABEL_DRUG_CLASS http://www.icardea.at/phrs/hl7V3#manufacturedLabeledDrug
//String ......HL7V3_DRUG_NAME http://www.icardea.at/phrs/hl7V3#drugName
//
//Complex with drug code node dynabean HL7V3_CODE
//dyn MANUFACTURED_PRODUCT_CLASS
//dyn ... MANUFACTURED_LABEL_DRUG_CLASS
//dyn ... HL7V3_CODE
//String ...... HL7V3_VALUE ..drug code
//String ...... SKOS_PREFLABEL
//
//    MANUFACTURED_PRODUCT_CLASS
//            "http://www.icardea.at/phrs/types/1.0/ManufacturedProduct";
//    
//    HL7_CLASS_CODE 
//            ICARDEA_HL7V3_NS + "#classCode";
//    
//    MANUFACTURED_LABEL_DRUG = 
//            ICARDEA_HL7V3_NS + "#manufacturedLabeledDrug";
//    
//    MANUFACTURED_LABEL_DRUG_CLASS 
//            "http://www.icardea.at/phrs/types/1.0/ManufacturedLabeledDrug";
    
    public Map getMedicationNameAttributes(DynaBean medicationDynabean) {
        Map map = null;
        try {       
            DynaBean dynaBeanMfgProduct =  DynaUtil.getDynaBeanProperty(
                    medicationDynabean, 
                    Constants.MANUFACTURED_PRODUCT_CLASS); 

            if (dynaBeanMfgProduct != null) {
                DynaBean dynaBean= DynaUtil.getDynaBeanProperty(
                        dynaBeanMfgProduct, 
                        Constants.MANUFACTURED_LABEL_DRUG_CLASS);
                        
                //Name provided either complex or simple
                //
                //simple:
                //HL7V3_DRUG_NAME literal
                //
                //or complex:                 
                //HL7V3_CODE dyna
                //....SKOS_PREFLABEL literal
                //....HL7V3_VALUE literal

                if (dynaBean != null) {
                    String name, drugId = null;
                    //simple name usually from PHR add  FIXXME WRONG!
                    name = DynaUtil.getStringProperty(dynaBean, Constants.HL7V3_DRUG_NAME); //Constants.HL7V3_DOSAGE
                    //or complex name and code from EHR FIXXME WRONG!
                    DynaBean obj = (DynaBean) DynaUtil.getDynaBeanProperty(dynaBean, Constants.HL7V3_CODE);

                    //Try to get name and code from a complex representation
                    if (obj != null) {
                        String name2 = DynaUtil.getStringProperty(dynaBean, Constants.SKOS_PREFLABEL);
                        name = name2 != null && name2.length() > 0 ? name2 : name;

                        drugId = DynaUtil.getStringProperty(dynaBean, Constants.HL7V3_VALUE);
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
            LOGGER.error("transforming date", e);
        }
        return theDate;
    }

    public String buildMedicationDosage(String dosageValue, String dosageUnits) {
        String newDosageURI = null;
        try {
            newDosageURI = medicationClient.buildDosage(dosageValue, dosageUnits);
            //newDosageURI = getInteropClients().getMedicationClient().buildDosage(dosageValue, dosageUnits);
        } catch (Exception e) {
            LOGGER.error("buildMedicationDosage dosageValue=" + dosageValue + " dosageUnits=" + dosageUnits, e);
        }
        return newDosageURI;
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
    @Ignore
    public void testAddMedicationForOwner() throws TripleException {
        System.out.println("testAddMedicationForOwner");
        String resourceURI = null;
        try {
            resourceURI =
                    medicationClient.addMedicationSign(
                    USER,
                    NOTE,
                    Constants.STATUS_COMPELETE,
                    "201006010000",
                    "201006010000",
                    DOSE_INTERVAL,//FIXXME build
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    "1",
                    DOSE_UNITS,
                    "testaddDrug",
                    InteropAccessService.DRUG_CODE_DEFAULT_PHR);
        } catch (TripleException e) {
            e.printStackTrace();
        } catch (java.lang.Error e) {
            System.out.println("java lang error sesame error");
            e.printStackTrace();
        }
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
