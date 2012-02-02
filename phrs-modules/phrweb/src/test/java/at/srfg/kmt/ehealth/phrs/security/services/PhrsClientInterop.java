/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatmentMatrix;
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
import com.google.code.morphia.query.Query;

public class PhrsClientInterop {

    public static final String NOTE = "to import";
    public static final String USER = "unittest123";//MedicationClientUnitTest.class.getName();
    public static final String DOSE_INTERVAL = "http://www.icardea.at/phrs/instances/EveryHour";
    public static final String DOSE_TIME_OF_DAY = "http://www.icardea.at/phrs/instances/InTheMorning";
    public static final String DOSE_UNITS = "http://www.icardea.at/phrs/instances/pills";
    public static final String MED_REASON = "http://www.icardea.at/phrs/instances/Cholesterol";
    private PhrsStoreClient phrsClient = null;
    private GenericTriplestore triplestore;

    public PhrsClientInterop() {
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
            if (phrsClient != null) {
                phrsClient.setTripleStore(null);
            }

        } catch (Exception e) {
            //e.printStackTrace(); shows a distracting error
        }
        triplestore = null;
        phrsClient = null;
    }


    @Test
    public void testSavePhrResourceAndFind() throws Exception {
        System.out.println("testSavePhrResourceAndFind_Message");
        phrsClient = PhrsStoreClient.getInstance();
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
  


}
