/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatmentMatrix;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.*;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsModel;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import com.google.code.morphia.query.Query;

public class PhrsClientUnitTest {

    public static final String NOTE = "to import";
    public static final String USER = "unittest123";//MedicationClientUnitTest.class.getName();
    public static final String DOSE_INTERVAL = "http://www.icardea.at/phrs/instances/EveryHour";
    public static final String DOSE_TIME_OF_DAY = "http://www.icardea.at/phrs/instances/InTheMorning";
    public static final String DOSE_UNITS = "http://www.icardea.at/phrs/instances/pills";
    public static final String MED_REASON = "http://www.icardea.at/phrs/instances/Cholesterol";
    private PhrsStoreClient phrsClient=null;
    private GenericTriplestore triplestore;

    public PhrsClientUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        //phrsClient)
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        phrsClient = PhrsStoreClient.getInstance();
        Query query = phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri =", USER);
        phrsClient.getPhrsDatastore().delete(query);
        triplestore = phrsClient.getGenericTriplestore();

    }

    @After
    public void tearDown() throws GenericRepositoryException, TripleException, IllegalAccessException, InstantiationException, Exception {
        try {
            PhrsStoreClient phrsClient = PhrsStoreClient.getInstance();
            if (phrsClient != null) {
                Query query = phrsClient.getPhrsDatastore().createQuery(MedicationTreatment.class).filter("ownerUri =", USER);
                phrsClient.getPhrsDatastore().delete(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //clean up 
            if (triplestore != null) {
                ((GenericTriplestoreLifecycle) triplestore).shutdown();
                ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInstanceDefault() {
        System.out.println("testInstanceDefault");
        PhrsStoreClient sc = PhrsStoreClient.getInstance();
        assertNotNull(sc);
    }
//crudSaveResource(def theObject,String action

    @Test
    public void testSavePhrResourceAndFind() throws Exception {
        System.out.println("testSavePhrResourceAndFind_");
        PhrsStoreClient sc = PhrsStoreClient.getInstance();

        InteropAccessService iaccess = sc.getInteropService();
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
            sc.getCommonDao().crudSaveResource(res, USER, USER);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String resourceUri = res.getResourceUri();
        Date created = res.getCreateDate();
        System.out.println("resourceUri = " + resourceUri + " created=" + created);
        assertNotNull("resourceUri is null, not saved" + resourceUri);
        assertNotNull("create date is null, not saved" + created);
        System.out.println(" ");

        boolean found = false;
        List<MedicationTreatment> list = null;
        try {
            //list = phrsClient.getCommonDao().crudReadResources(USER, (Object) MedicationTreatment.class);
            list = sc.getCommonDao().crudReadMedicationResources(USER);

            assertNotNull("Null, Expected search results for resource type and user ", list);
            assertTrue("Empty, Expected search results for resource type and user ", !list.isEmpty());

            for (MedicationTreatment item : list) {
                System.out.println("item resUri="+item.getResourceUri());
                
                if (item.getResourceUri()!=null && resourceUri.equals(item.getResourceUri())) {
                    found = true;
                }else {
                    System.out.println("item  resUri=NULL");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("Resource saved was NOT found again", found);

    }

    @Test
    public void testSavePhrResource2() throws Exception {
        System.out.println("testSavePhrResourceAndFind");
        PhrsStoreClient sc = PhrsStoreClient.getInstance();

        InteropAccessService iaccess = sc.getInteropService();
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



        res.setOwnerUri(USER);
	res.setCreatorUri(USER); //resourceUri is set before actually saving the the object
        res.setType(null);
        

        assertNotNull(iaccess.getCommonDao());
       if(phrsClient==null) phrsClient = PhrsStoreClient.getInstance();
        try {
            //iaccess.getCommonDao().crudSaveResource(res, USER, USER);
            phrsClient.getPhrsRepositoryClient().crudSaveResource(res, "action111");

        } catch (Exception e) {
            e.printStackTrace();
        }
        String resourceUri = res.getResourceUri();
        Date created = res.getCreateDate();
        System.out.println("resourceUri = " + resourceUri + " created=" + created);
        assertNotNull("resourceUri is null, not saved" + resourceUri);
        assertNotNull("create date is null, not saved" + created);
        System.out.println(" ");

        boolean found = false;
        List<MedicationTreatment> list = null;
        try {
            list = phrsClient.getCommonDao().crudReadResources(USER, (Object) MedicationTreatment.class);

            assertNotNull("Null, Expected search results for resource type and user ", list);
            assertTrue("Empty, Expected search results for resource type and user ", !list.isEmpty());

            for (MedicationTreatment item : list) {
                if (resourceUri.equals(item.getResourceUri())) {
                    found = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue("Resource saved was found again", found);

    }
    /*
     * To test: phrsStoreClient.updateTriple(messageResourceUri,
     * Constants.SKOS_NOTE,REFERENCE_NOTE_PREFIX+messageResourceUri, false)
     */

    @Test
    public void testInstanceInteropAccess() {
        System.out.println("testInstanceInteropAccess");
        PhrsStoreClient sc = PhrsStoreClient.getInstance();
        assertNotNull(sc);
        at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService ias = sc.getInteropService();

        assertNotNull(ias);
    }
    /*
     * @Test public void testInstanceInteropAccessOverrideTripleStore() {
     * System.out.println("testInstanceInteropAccessOverrideTripleStore");
     * PhrsStoreClient sc = PhrsStoreClient.getInstance(getTripleStore(), true);
     * assertNotNull(sc);
     * at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService ias =
     * sc.getInteropService();
     *
     * assertNotNull(ias); }
     *
     * @Test public void testInstanceOverrideTripleStore() {
     * System.out.println("testInstanceOverrideTripleStore"); GenericTriplestore
     * triplestore = getTripleStore(); assertNotNull("triplestore null",
     * triplestore); PhrsStoreClient sc =
     * PhrsStoreClient.getInstance(getTripleStore(), true);
     * assertNotNull("PhrsStoreClient nulll", sc); }
     */

    public GenericTriplestore getTripleStore() {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        GenericTriplestore triplestore = connectionFactory.getTriplestore();

        return triplestore;
    }

    /*
     * @Test public void testParseReferenceNote() {
     * System.out.println("testParseReferenceNote"); String expect = "1234";
     * //String x = "res=" + expect; String x = expect; String result =
     * at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService.parseReferenceNote(x);
     * System.out.println("parsed=" + result); assertNotNull("reference note
     * null", result); assertEquals("reference note expect: " + expect + "
     * found: " + result, expect, result); }
     */
    public at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService getInteropAccessService() {

        PhrsStoreClient sc = PhrsStoreClient.getInstance();
        assertNotNull(sc);
        at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService ias = sc.getInteropService();
        return ias;
    }
}
