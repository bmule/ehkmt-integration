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
import java.util.Date;
import java.util.UUID;
import org.junit.*;
import static org.junit.Assert.*;
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsModel;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;

public class PhrsClientUnitTest {

    public static final String NOTE = "to import";
    public static final String USER = "unittest123";//MedicationClientUnitTest.class.getName();
    public static final String DOSE_INTERVAL = "http://www.icardea.at/phrs/instances/EveryHour";
    public static final String DOSE_TIME_OF_DAY = "http://www.icardea.at/phrs/instances/InTheMorning";
    public static final String DOSE_UNITS = "http://www.icardea.at/phrs/instances/pills";
    public static final String MED_REASON = "http://www.icardea.at/phrs/instances/Cholesterol";

    public PhrsClientUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void testInstanceDefault() {
        System.out.println("testInstanceDefault");
        PhrsStoreClient sc = PhrsStoreClient.getInstance();
        assertNotNull(sc);
    }

    @Test
    public void testMedicationClientViaInteropDirectCode() {
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

            //quick test
                String phrResourceUri = null;
//             String resourceURI_1 =
//                    medicationclient.addMedicationSign(
//                    USER,
//                    NOTE,
//                    Constants.STATUS_COMPELETE,
//                    "201006010000",
//                    "201206010000",
//                    DOSE_INTERVAL,//MyFreqency",
//                    Constants.HL7V3_ORAL_ADMINISTRATION,
//                    "1",
//                    DOSE_UNITS,//"pillURI",
//                    phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri : "EHRDrug_test111"
//                    );//InteropAccessService.DRUG_CODE_DEFAULT_PHR);
            String resourceURI_1 =
                    medicationclient.addMedicationSign(
                    owner,
                    referenceNote,
                    Constants.STATUS_INCOMPELETE,
                    dateStringStart,
                    "201206010000",
                    DOSE_INTERVAL,//MyFreqency",
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    "1",
                    DOSE_UNITS,//"pillURI",
                    phrResourceUri != null ? InteropAccessService.REFERENCE_NOTE_PREFIX + phrResourceUri : "EHRDrug_test111"
                    );//InteropAccessService.DRUG_CODE_DEFAULT_PHR);

            assertNotNull("resourceURI_1 no-fail sample is null,interop sample message failed", resourceURI_1);

            String messageId = medicationclient.addMedicationSign(
                    owner,
                    referenceNote,
                    status,
                    dateStringStart,
                    dateStringEnd,//dateStringEnd,
                    doseInterval,//"MyFreqency",
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

    @Test
    public void testSavePhrResourceViaInterop() throws Exception {
        System.out.println("testSavePhrResourceViaInterop");
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
            iaccess.getCommonDao().crudSaveResource(res, "unittest", "interopservice");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resourceUri = res.getResourceUri();
        Date created = res.getCreateDate();
        System.out.println("resourceUri = " + resourceUri + " created=" + created);
        assertNotNull("resourceUri is null, not saved" + resourceUri);
        assertNotNull("create date is null, not saved" + created);
        System.out.println(" ");
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

    @Test
    public void testInstanceInteropAccessOverrideTripleStore() {
        System.out.println("testInstanceInteropAccessOverrideTripleStore");
        PhrsStoreClient sc = PhrsStoreClient.getInstance(getTripleStore());
        assertNotNull(sc);
        at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService ias = sc.getInteropService();

        assertNotNull(ias);
    }

    @Test
    public void testInstanceOverrideTripleStore() {
        System.out.println("testInstanceOverrideTripleStore");
        GenericTriplestore triplestore = getTripleStore();
        assertNotNull("triplestore null", triplestore);
        PhrsStoreClient sc = PhrsStoreClient.getInstance(triplestore);
        assertNotNull("PhrsStoreClient nulll", sc);
    }

    public GenericTriplestore getTripleStore() {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        GenericTriplestore triplestore = connectionFactory.getTriplestore();

        return triplestore;
    }

    @Test
    public void testSimpleMethodCall() {
        System.out.println("testSimpleMethodCall");
        String expect = "1234";
        //String x = "res=" + expect;

        String result = getInteropAccessService().getTest();
        System.out.println("result=" + result);
        assertNotNull("reference note null", result);
        assertEquals("reference note expect: " + expect + " found: " + result, expect, result);



        //assertEquals("xxx",result);
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

    @Test
    public void testParseReferenceNote() {
        System.out.println("phrsclient testParseReferenceNote");
        String expect = "1234";
        String x = expect;
        String result = InteropAccessService.parseReferenceNote(InteropAccessService.REFERENCE_NOTE_PREFIX + x);
        System.out.println(InteropAccessService.REFERENCE_NOTE_PREFIX + x + " /parsed" + result);
        assertEquals("reference note", expect, result);
    }

    @Test
    public void testReferenceNoteCode() {
        System.out.println("testReferenceNoteCode");
        String out = null;
        String expect = "1234";
        String note = InteropAccessService.REFERENCE_NOTE_PREFIX + expect;
        /*
         * if (note != null) { if (note.contains("=")) { //or def parts, then
         * use parts.size() String[] parts = note.split("=");
         *
         * if (parts != null && parts.length > 0) { out = parts[1]; } } else {
         * //no parsing out = note; } }
         */

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

        assertNotNull("reference note", out);
        assertEquals("reference note", expect, out);

    }
}
