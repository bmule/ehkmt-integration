/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.support.test;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemEntryClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropTermTransformer;
import at.srfg.kmt.ehealth.phrs.presentation.utils.DynaUtil;
import org.apache.commons.beanutils.DynaBean;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CoreTestData {

    private final static Logger LOGGER = LoggerFactory.getLogger(CoreTestData.class);
    public static final String DRUG_CODE_1 = "C1322815";
    public static final String DRUG_NAME_1 = "Drug-Eluting Stents";
    public static final String DRUG_QUANTITY_1 = "25";
    public static final String STATUS_1 = Constants.STATUS_COMPELETE;
    public static final String STATUS_2 = Constants.STATUS_COMPELETE;
    public static final String DRUG_CODE_2 = "C1322815";
    public static final String DRUG_NAME_2 = "Drug-Eluting Stents";
    public static final String DRUG_QUANTITY_2 = "3";
    public static final String ADMIN_ROUTE = Constants.HL7V3_ORAL_ADMINISTRATION;
    public static final String DATE_PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm";
    private MedicationClient client;
    private InteropClients interopClients;
    //coreTestData.addMedication_forAnyUnitTest(owner,"10","to import");
    //coreTestData.addTestMedication_1_forAnyUnitTest(protocolId,"10","to import");
    private CommonDao commonDao;

    /**
     * @param interopClients
     * @deprecated
     */
    public CoreTestData(InteropClients interopClients) {
        this.interopClients = interopClients;

        this.client = interopClients.getMedicationClient();

        commonDao = PhrsStoreClient.getInstance().getCommonDao();
    }

    /**
     * @param storeClient
     * @deprecated
     */
    public CoreTestData(PhrsStoreClient storeClient) {
        this.interopClients = storeClient.getInteropClients();

        this.client = interopClients.getMedicationClient();

        commonDao = storeClient.getCommonDao();

    }

    public CoreTestData() {
        this.interopClients = PhrsStoreClient.getInstance().getInteropClients();

        this.client = interopClients.getMedicationClient();
        commonDao = PhrsStoreClient.getInstance().getCommonDao();
    }

    public static String formatTimeDate(DateTime dateTime, String pattern) {

        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
        return dateTime.toString(dtf);
    }

    public String makeDateLabelForTitle() {

        return makeDateLabelForTitle(" test:");
    }

    public static String makeDateLabelForTitle(String prefix) {
        String dateStr = formatTimeDate(new DateTime(), DATE_PATTERN_DATE_TIME);
        return prefix + dateStr;
    }

    /**
     * @param protcolId
     * @param quantity  DRUG_CODE_1 Drug name and code "Drug-Eluting Stents",
     *                  "C1322815"
     */
    public void addMedication_forAnyUnitTest(String protcolId, String quantity, String note) {
        this.addTestMedication_1_forAnyUnitTest(protcolId, quantity, note,
                DRUG_NAME_1,
                DRUG_CODE_1);
    }

    /**
     * Dates 201011030000,201105051010 ... status = Completed,
     * Constants.MILLIGRAM
     *
     * @param protcolId
     * @param quantity
     * @param note      - can be null, but defaults to blank, put the resourceUri
     *                  string pattern here when needed for testing
     * @param drugName
     * @param drugCode
     */
    public void addTestMedication_1_forAnyUnitTest(String protcolId, String quantity, String note, String drugName, String drugCode) {
        String theNote = note != null ? note : "";

        try {
            client.addMedicationSign(
                    protcolId,
                    theNote,//"Free text note for the medication.",
                    STATUS_1,
                    "201011030000",
                    "201105051010",
                    client.buildFrequency("morgen", -1, 8, Constants.HOUR),
                    Constants.HL7V3_ORAL_ADMINISTRATION,
                    quantity,//"25",
                    Constants.MILLIGRAM,
                    drugName + makeDateLabelForTitle(),
                    drugCode);
            LOGGER.debug("END addTestMedication_1_forAnyUnitTest  preparing test data for owner= " + protcolId);
        } catch (TripleException e) {
            System.out.println("" + e);
            LOGGER.debug("ERROR addTestMedication_1_forAnyUnitTest  preparing test data for owner= " + protcolId, e);
        } catch (Exception e) {
            System.out.println("" + e);
            LOGGER.debug("ERROR addTestMedication_1_forAnyUnitTest  preparing test data for owner= " + protcolId, e);


        }
        interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDLIST, protcolId);  //"MEDLIST"
    }

    /**
     * Bilateral test data owner = Constants.OWNER_URI_CORE_PORTAL_TEST_USER
     */
    public void addTestMedications_2_forPortalTestUser() {

        //final String owner = Constants.OWNER_URI_CORE_PORTAL_TEST_USER;   x
        String owner = ConfigurationService.getInstance().getProperty("test.user.1.login.id", "phrtest");
        this.addTestMedications_2_forPortalTestForOwnerUri(owner);
    }

    /**
     * @param owner - the ProtocolId is determined.
     * @return int expected count of records, not the actual. We test the
     *         expected vs found
     */
    public int addTestMedications_2_forPortalTestForOwnerUri(String owner) {
        int countAdded = 8;//update this if added more manually...needed by tests

        String protocolId = this.interopClients.getProtocolId(owner);

        if (protocolId != null && !protocolId.isEmpty()) {
            // ok 
        } else {
            LOGGER.error("No protocolID yet for User, No TEST messages for ownerUri=" + owner);
            return 0;
        }
        if (owner != null) {

            try {
                LOGGER.debug("START addTestMedications_2_forPortalTestForOwnerUri Start preparing test data for owner= " + owner);

                final TriplestoreConnectionFactory connectionFactory = TriplestoreConnectionFactory.getInstance();
                final GenericTriplestore triplestore = connectionFactory.getTriplestore();

                final MedicationClient client = new MedicationClient(triplestore);

                client.addMedicationSign(protocolId, "Free text note for the medication 1.",
                        Constants.STATUS_ACTIVE, "200812010000", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "25", Constants.MILLIGRAM,
                        "Prednisone", "C0032952");

                client.addMedicationSign(protocolId, "Free text note for the medication 2.",
                        Constants.STATUS_ACTIVE, "200812010000", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "40", Constants.MILLIGRAM,
                        "Pantoprazole (Pantoloc)", "C0081876");

                client.addMedicationSign(protocolId, "Free text note for the medication 3.",
                        Constants.STATUS_ACTIVE, "199910101010", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "5", Constants.MILLIGRAM,
                        "Concor", "C0110591");

                client.addMedicationSign(protocolId, "Free text note for the medication 4.",
                        Constants.STATUS_ACTIVE, "199910101010", "201010101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.DROPS,
                        "Psychopax (Diazepam)", "C0012010");

                client.addMedicationSign(protocolId, "Free text note for the medication 5.",
                        Constants.STATUS_ACTIVE, "198010101010", "20110601010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "300", Constants.MILLIGRAM,
                        "Convulex", "C0591288");

                client.addMedicationSign(protocolId, "Free text note for the medication 6.",
                        Constants.STATUS_ACTIVE, "20090101010", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "20", Constants.MILLIGRAM,
                        "Ebetrexat(Methotrexate)", "C0025677");

                client.addMedicationSign(protocolId, "Free text note for the medication 7.",
                        Constants.STATUS_ACTIVE, "20090101010", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "10", Constants.MILLIGRAM,
                        "Folsan(Folic Acid)", "C0016410");

                client.addMedicationSign(protocolId, "Free text note for the medication 8.",
                        Constants.STATUS_ACTIVE, "199910101010", "201010101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.TABLET,
                        "Magnosolv(Magnesium)", "C0024467");

                LOGGER.debug("END addTestMedications_2_forPortalTestForOwnerUri  preparing test data for owner= " + owner + " pid=" + protocolId);
            } catch (TripleException e) {
                e.printStackTrace();
                LOGGER.debug("ERROR addTestMedications_2_forPortalTestForOwnerUri  preparing test data for owner= " + owner + " pid=" + protocolId, e);
            } catch (Exception e) {
                LOGGER.debug("ERROR addTestMedications_2_forPortalTestForOwnerUri  preparing test data for owner= " + owner + " pid=" + protocolId, e);
                e.printStackTrace();
            }
        } else {
            LOGGER.error("Error creating addTestMedications_2_forPortalTestForOwnerUri test data, ownerUri=null");
        }

       //No notify interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDLIST, protocolId);  //"MEDLIST"

        return countAdded;

    }
    
    public static MedicationTreatment createMedication(String ownerUri,String stdStatus,String datebeginStr,String dateEndStr, String dose,String doseUnit,String drugname, String drugcode){

        
        MedicationTreatment med= new MedicationTreatment();
        Date dateEnd= DateUtil.getFormatedDate(dateEndStr);
        Date dateBegin= DateUtil.getFormatedDate(datebeginStr);

   
        String localStatus= InteropTermTransformer.transformStandardStatusToLocal(stdStatus, Constants.PHRS_MEDICATION_CLASS) ;

        med.setStatus(localStatus);
        med.setStatusStandard(stdStatus);
        med.setOwnerUri(ownerUri);
        med.setCreatorUri("createMedication");
       
        med.setLabel(drugname);
        med.setProductCode(drugcode);
        med.getTreatmentMatrix().setDosageQuantity(dose);
        Double dosage=Double.parseDouble(dose);
        med.getTreatmentMatrix().setDosageUnits(doseUnit);
        med.getTreatmentMatrix().setDosage(dosage);
        med.getTreatmentMatrix().setAdminRoute(Constants.HL7V3_ORAL_ADMINISTRATION);
        med.setNote("");


        String interval = "http://www.icardea.at/phrs/instances/other";
        med.getTreatmentMatrix().setDosageInterval(interval);

        String tod = "http://www.icardea.at/phrs/instances/NotSpecified";
        med.getTreatmentMatrix().setDosageTimeOfDay(tod);


        return med;


    }
    public int addTestMedicationsPhr(String owner) {
        int countAdded = 8;//update this if added more manually...needed by tests

        String protocolId = this.interopClients.getProtocolId(owner);

        if (protocolId != null && !protocolId.isEmpty()) {
            // ok
        } else {
            LOGGER.error("No protocolID yet for User, No TEST messages for ownerUri=" + owner);
            return 0;
        }
        if (owner != null) {

            try {

                LOGGER.debug("START addTestMedicationsPhr for owner= " + owner);
                CommonDao commonDao = PhrsStoreClient.getInstance().getCommonDao();

                MedicationTreatment med_1= CoreTestData
                        .createMedication(owner,Constants.STATUS_ACTIVE,
                                "200812010000","201106101010","25",Constants.MILLIGRAM,"Prednisone", "C0032952");

                MedicationTreatment med_2= CoreTestData
                        .createMedication(owner,Constants.STATUS_ACTIVE,
                                "200812010000", "201106101010","40",Constants.MILLIGRAM,"Pantoprazole (Pantoloc)", "C0081876");

                MedicationTreatment med_3= CoreTestData
                        .createMedication(owner,Constants.STATUS_ACTIVE,
                                "199910101010", "201106101010","5",Constants.MILLIGRAM,"Concor", "C0110591");

                MedicationTreatment med_4= CoreTestData
                        .createMedication(owner,Constants.STATUS_ACTIVE,
                                "199910101010", "201010101010","1",Constants.DROPS,"Psychopax (Diazepam)", "C0012010");

                MedicationTreatment med_5= CoreTestData
                        .createMedication(owner,Constants.STATUS_ACTIVE,
                                "198010101010", "20110601010","300",Constants.MILLIGRAM,"Convulex", "C0591288");

                MedicationTreatment med_6= CoreTestData
                        .createMedication(owner,Constants.STATUS_ACTIVE,
                                "20090101010", "201106101010","20",Constants.MILLIGRAM,"Ebetrexat(Methotrexate)", "C0025677");

                MedicationTreatment med_7= CoreTestData
                        .createMedication(owner,Constants.STATUS_ACTIVE,
                                "20090101010", "201106101010","10",Constants.MILLIGRAM,"Folsan(Folic Acid)", "C0016410");

                MedicationTreatment med_8= CoreTestData
                        .createMedication(owner,Constants.STATUS_ACTIVE,
                                "199910101010", "201010101010","1",Constants.TABLET,"Magnosolv(Magnesium)", "C0024467");



                commonDao.crudSaveResource(med_1,owner,"addTestMedicationsPhr");
                commonDao.crudSaveResource(med_2,owner,"addTestMedicationsPhr");
                commonDao.crudSaveResource(med_3,owner,"addTestMedicationsPhr");
                commonDao.crudSaveResource(med_4,owner,"addTestMedicationsPhr");
                commonDao.crudSaveResource(med_5,owner,"addTestMedicationsPhr");
                commonDao.crudSaveResource(med_6,owner,"addTestMedicationsPhr");
                commonDao.crudSaveResource(med_7,owner,"addTestMedicationsPhr");
                commonDao.crudSaveResource(med_8,owner,"addTestMedicationsPhr");

                LOGGER.debug("END addTestMedicationsPhr  preparing test data for owner= " + owner + " pid=" + protocolId);

            } catch (Exception e) {
                LOGGER.debug("ERROR addTestMedicationsPhr  preparing test data for owner= " + owner + " pid=" + protocolId, e);
                e.printStackTrace();
            }
        } else {
            LOGGER.error("Error creating addTestMedicationsPhr, ownerUri=null");
        }


        return countAdded;

    }
    /**
     * runs once
     * @param number
     */
    public static void createTestUsersForMonitoring() {
        CommonDao commonDao = PhrsStoreClient.getInstance().getCommonDao();
        if(commonDao.getPhrUser("phr01", false) !=null) {
            PhrFederatedUser  u1= createTestUserForMonitoring("pid1","phr01","Marie","Arcand",null,null);

        }
        if(commonDao.getPhrUser("phr01", false) !=null) {
            PhrFederatedUser  u2= createTestUserForMonitoring("pid2","phr02","Ann","Kaine",null,null);

        }
        if(commonDao.getPhrUser("phr01", false) !=null) {
            PhrFederatedUser  u4= createTestUserForMonitoring("pid4","phr03","Thomas","Kane",null,null);

        }
        if(commonDao.getPhrUser("phr01", false) !=null) {
            PhrFederatedUser  u3= createTestUserForMonitoring("pid3","phr04","Dudley","Clougher",null,null);

        }

    }

    public static PhrFederatedUser createTestUserForMonitoring(String protocolId,String loginId, String firstname, String lastname, String pixQueryIdType, String pixQueryIdUser) {


        PhrFederatedUser user = null;
        try {
            CommonDao commonDao = PhrsStoreClient.getInstance().getCommonDao();

            //String loginUserIdOwnerUri = PhrFederatedUser.makeOwnerUri();
            user = commonDao.getPhrUser(loginId, true);

            user.setOwnerUri(loginId);
            user.setCreatorUri(user.getOwnerUri());
            user.setUserId(loginId);
            user.setIdentifier(loginId);//init to local identifier, but could later assign to an OpenId.

            user.setCanLocalLogin(true);

            user.setNickname(firstname);
            user.setCanLocalLogin(true);

            user.setProtocolIdUser(protocolId);//Constants.ICARDEA_DOMAIN_PIX_OID);
            user.setProtocolIdPix(protocolId);//Constants.ICARDEA_DOMAIN_PIX_OID);

            user.setPixQueryIdUser(pixQueryIdUser);
            user.setPixQueryIdType(pixQueryIdType);

            //user.setRole(PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER_LOCAL_LOGIN);

            commonDao.crudSaveResource(user, user.getOwnerUri(), "createTestUserForMonitoring");

            //register PID.
            //commonDao.getPhrsStoreClient().getInteropClients().registerProtocolId(user.getOwnerUri(), protocolId, null);


            ProfileContactInfo info = commonDao.getProfileContactInfo(user.getOwnerUri());
            if (info == null) {
                info = new ProfileContactInfo();
                info.setOwnerUri(user.getOwnerUri());
                info.setCreatorUri(user.getOwnerUri());
                info.setType(info.getClass().getCanonicalName());

            }
            info.setFirstName(firstname);
            info.setLastName(lastname);


            commonDao.crudSaveResource(info, user.getOwnerUri(), "createTestUserForMonitoring");


            ObsVitalsBloodPressure bp1 = new ObsVitalsBloodPressure();
            bp1.setSystolic(110);
            bp1.setDiastolic(55);
            bp1.setHeartRate(60);
            bp1.setBeginDate(new Date());
            bp1.setEndDate(new Date());
            bp1.setNote("note " + makeSimpleId());
            bp1.setSystemNote(bp1.getNote());
            commonDao.crudSaveResource(bp1, user.getOwnerUri(), "createTestUserForMonitoring");
            //does automatic notify upon save


            ObsVitalsBodyWeight bw1 = new ObsVitalsBodyWeight();
            bw1.setBodyWeight(71d);
            bw1.setBodyHeight(163d);
            bw1.setBeginDate(new Date());
            bw1.setEndDate(new Date());
            bw1.setNote("note " + makeSimpleId());
            bw1.setSystemNote(bw1.getNote());
            commonDao.crudSaveResource(bw1, user.getOwnerUri(), "createTestUserForMonitoring");

            //    "http://www.icardea.at/phrs/instances/BleedingGums");


            ObsProblem prob = new ObsProblem();
            prob.setBeginDate(new Date());
            prob.setStatus("default_activeStatusTrue");
            prob.setCode(Constants.HL7V3_FEVER);

            prob.setNote("note " + makeSimpleId());
            prob.setNote(prob.getSystemNote());
            commonDao.crudSaveResource(prob, user.getOwnerUri(), "createTestUserForMonitoring");


            //                Constants.STATUS_COMPELETE,
//                Constants.HL7V3_ORAL_ADMINISTRATION,
//                "20", Constants.MILLIGRAM,
//                "Ebetrexat(Methotrexate)"
//                        "C0025677"

            MedicationTreatment res = new MedicationTreatment();

            res.setBeginDate(new Date());
            //res.setEndDate(new Date());
            res.setLabel("Ebetrexat(Methotrexate)");
            res.setProductCode( "C0025677");

            res.setStatus(Constants.STATUS_RUNNING);
            res.setReasonCode("http://www.icardea.at/phrs/instances/Cholesterol");

            //res.setReasonCode(value);
            //res.setPrescribedByName("");
            MedicationTreatmentMatrix mtm = new MedicationTreatmentMatrix();
            mtm.setAdminRoute(Constants.HL7V3_ORAL_ADMINISTRATION);
            mtm.setDosage(2d);//double

            mtm.setDosageQuantity("100");
            mtm.setDosageUnits(Constants.MILLIGRAM);

            mtm.setDosageInterval("http://www.icardea.at/phrs/instances/EveryHour");
            mtm.setDosageTimeOfDay("http://www.icardea.at/phrs/instances/InTheMorning");

            res.setTreatmentMatrix(mtm);

            commonDao.crudSaveResource(res, user.getOwnerUri(), "createTestUserForMonitoring");
            LOGGER.debug("createTestUserForMonitoring for  fullname " +firstname+" "+ lastname + " ownerUri " + user.getOwnerUri() + " protocolId " + protocolId);


        } catch (Exception e) {
            LOGGER.error("Error creating user test data", e);
        }
        return user;
    }

    /**
     * @param protocolId
     * @return
     */
    public Set getDynaBeans(String protocolId) {
        Set<DynaBean> beans = null;
        try {
            final Iterable<String> uris = client.getMedicationURIsForUser(protocolId);
            final DynaBeanClient dynaBeanClient = new DynaBeanClient(PhrsStoreClient.getInstance().getTripleStore());
            beans = new HashSet<DynaBean>();

            for (String uri : uris) {
                final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
                beans.add(dynaBean);
            }
        } catch (TripleException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("", e);
        }
        return beans;
    }

    public static final String PROTOCOL_ID_190 = "190";

    /**
     * Create users and test data for 190 and 191
     */
    public static PhrFederatedUser createTestUserData() {
        PhrFederatedUser user = null;
        try {
            user = createTestUserData(true);
        } catch (Exception e) {
            LOGGER.error("Error creating user test user data", e);
        }
        return user;
    }

    public static PhrFederatedUser createTestUserData(boolean addObservations) {

//       test.user.1.cied.pixQueryIdType=cied:model:Maximo
//     test.user.1.cied.serial=PZC123456S
//     test.user.1.pid=191
//     test.user.1.name=Suzie Mayr
//     test.user.1.firstname=Suzie
//     test.user.1.lastname=Mayr

        String loginUserIdOwnerUri = ConfigurationService.getInstance().getProperty("test.user.1.login.id", PhrsConstants.AUTHORIZE_USER_PREFIX_TEST);
        LOGGER.debug("Creating test data for test user = " + loginUserIdOwnerUri);
        PhrFederatedUser user = null;
        try {
            String pixQueryIdUser = ConfigurationService.getInstance().getProperty("test.user.1.cied.serial", "PZC123456S");
            String pixQueryIdType = ConfigurationService.getInstance().getProperty("test.user.1.cied.pixQueryIdType", "cied:model:Maximo");
            String protocolId = ConfigurationService.getInstance().getProperty("test.user.1.pid", "191");
            //phrtest ..


            String firstname = ConfigurationService.getInstance().getProperty("test.user.1.firstname", "Suzie");
            String lastname = ConfigurationService.getInstance().getProperty("test.user.1.lastname", "Mayr");
            String fullname = ConfigurationService.getInstance().getProperty("test.user.1.name", "Suzie Mayr");


            CommonDao commonDao = PhrsStoreClient.getInstance().getCommonDao();
            // String ownerUri         = PhrFederatedUser.makeOwnerUri();
            user = commonDao.getPhrUser(loginUserIdOwnerUri, true);

            user.setOwnerUri(loginUserIdOwnerUri);
            user.setCreatorUri(user.getOwnerUri());
            user.setUserId(loginUserIdOwnerUri);
            user.setIdentifier(loginUserIdOwnerUri);//init to local identifier, but could later assign to an OpenId.

            user.setCanLocalLogin(true);


            user.setNickname(fullname);
            user.setCanLocalLogin(true);

            user.setProtocolIdUser(protocolId);//Constants.ICARDEA_DOMAIN_PIX_OID);
            user.setProtocolIdPix(protocolId);//Constants.ICARDEA_DOMAIN_PIX_OID);

            user.setPixQueryIdUser(pixQueryIdUser);
            user.setPixQueryIdType(pixQueryIdType);

            //user.setRole(PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER_LOCAL_LOGIN);

            commonDao.crudSaveResource(user, user.getOwnerUri(), "CoreTestData createTestUserData");

            //register PID.
            //commonDao.getPhrsStoreClient().getInteropClients().registerProtocolId(user.getOwnerUri(), protocolId, null);


            ProfileContactInfo info = commonDao.getProfileContactInfo(user.getOwnerUri());
            if (info == null) {
                info = new ProfileContactInfo();
                info.setOwnerUri(user.getOwnerUri());
                info.setCreatorUri(user.getOwnerUri());
                info.setType(info.getClass().getCanonicalName());

            }
            info.setFirstName(firstname);
            info.setLastName(lastname);


            commonDao.crudSaveResource(info, user.getOwnerUri(), "CoreTestData createTestUserData");


            if (addObservations) {

                ObsVitalsBloodPressure bp1 = new ObsVitalsBloodPressure();
                bp1.setSystolic(140);
                bp1.setDiastolic(85);
                bp1.setHeartRate(75);
                bp1.setBeginDate(new Date());
                bp1.setEndDate(new Date());
                bp1.setNote("note " + makeSimpleId());
                bp1.setSystemNote(bp1.getNote());
                commonDao.crudSaveResource(bp1, user.getOwnerUri(), "CoreTestData createTestUserData");
                //does automatic notify upon save
            }
            /*

          if (addObservations) {
              ObsVitalsBodyWeight bw1 = new ObsVitalsBodyWeight();
              bw1.setBodyWeight(75d);
              bw1.setBodyHeight(173d);
              bw1.setBeginDate(new Date());
              bw1.setEndDate(new Date());
              bw1.setNote("note " + makeSimpleId());
              bw1.setSystemNote(bw1.getNote());
              commonDao.crudSaveResource(bw1, user.getOwnerUri(), "CoreTestData createTestUserData");
              //does automatic notify upon save
          }  */
            /*
               try {
            client.addProblemEntry(
                    protocolId,
                    Constants.HL7V3_SYMPTOM,
                    Constants.STATUS_COMPELETE,
                    "201008200000",
                    "",
                    "loaded by core test data Free text note",
                    "http://www.icardea.at/phrs/instances/BleedingGums");
        } catch (TripleException e) {
            LOGGER.error("loadTestProblem Constants.HL7V3_UNABLE_TO_EAT", e);  //To change body of catch statement use File | Settings | File Templates.
        }
             */
            /*
            ObsProblem prob=new ObsProblem();
             prob.setBeginDate(new Date());
            prob.setStatus("default_activeStatusTrue");
            prob.setCode("http://www.icardea.at/phrs/instances/BleedingGums");

            prob.setNote("note " + makeSimpleId());
            prob.setSystemNote(prob.getNote());
            commonDao.crudSaveResource(prob, user.getOwnerUri(), "CoreTestData createTestUserData");
            LOGGER.debug("Created test data for  fullname " + fullname + " ownerUri " + user.getOwnerUri() + " protocolId " + protocolId);
              */

        } catch (Exception e) {
            LOGGER.error("Error creating user test data", e);
        }
        return user;
    }


    /**
     * String protocolId = ConfigurationService.getInstance().getProperty("test.user.1.pid", "191");
     */
    public static void loadTestProblemsMedications(boolean notify) {
        loadTestProblemsMedications(ConfigurationService.getInstance().getProperty("test.user.1.pid", "191"), notify);
    }

    public static void loadTestProblemsMedications(String protocolId, boolean notify) {
        InteropClients interopClients = PhrsStoreClient.getInstance().getInteropClients();
        ProblemEntryClient client = interopClients.getProblemEntryClient();
        MedicationClient medClient = interopClients.getMedicationClient();

        // this adds a problem-finding named ....
        try {
            client.addProblemEntry(
                    protocolId,
                    Constants.HL7V3_COMPILANT,
                    Constants.STATUS_COMPELETE,
                    "201008200000",
                    "",
                    "loaded by core test data Free text note.",
                    Constants.HL7V3_SICK_TO_STOMACH);
        } catch (TripleException e) {
            LOGGER.error("loadTestProblem Constants.HL7V3_SICK_TO_STOMACH", e);  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            client.addProblemEntry(
                    protocolId,
                    Constants.HL7V3_COMPILANT,
                    Constants.STATUS_COMPELETE,
                    "201008200000",
                    "",
                    "loaded by core test data Free text note",
                    Constants.HL7V3_UNABLE_TO_EAT);
        } catch (TripleException e) {
            LOGGER.error("loadTestProblem Constants.HL7V3_UNABLE_TO_EAT", e);  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            medClient.addMedicationSign(protocolId, "Free text note for the medication 8.",
                    Constants.STATUS_COMPELETE, "199910101010", "201010101010",
                    medClient.buildNullFrequency(),
                    Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.TABLET,
                    "Magnosolv(Magnesium)", "C0024467");
            //add without drug code
            medClient.addMedicationSign(protocolId, "Free text note for the medication 8.",
                    Constants.STATUS_COMPELETE, "199910101010", "201010101010",
                    medClient.buildNullFrequency(),
                    Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.TABLET,
                    "BOB Magnosolv(No CODE)");
        } catch (TripleException e) {
            LOGGER.error("loadTestProblem addMedicationSign  ", e);
        }
        try {
            interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDCCAT, protocolId);  //"MEDLIST"
        } catch (Exception e) {
            LOGGER.error("loadTestProblem notifyInteropMessageSubscribersByProtocolId  ", e);
        }
        try {
            interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDLIST, protocolId);  //"MEDLIST"
        } catch (Exception e) {
            LOGGER.error("loadTestProblem notifyInteropMessageSubscribersByProtocolId  ", e);
        }
    }

    public static void loadTestProblem(String protocolId, boolean notify) {
        InteropClients interopClients = PhrsStoreClient.getInstance().getInteropClients();
        ProblemEntryClient client = interopClients.getProblemEntryClient();


        // this adds a problem-finding named ....
        try {
            client.addProblemEntry(
                    protocolId,
                    Constants.HL7V3_COMPILANT,
                    Constants.STATUS_COMPELETE,
                    "201008200000",
                    "",
                    "loaded by core test data Free text note. " + new Date(),
                    Constants.HL7V3_SICK_TO_STOMACH);
        } catch (TripleException e) {
            LOGGER.error("loadTestProblem Constants.HL7V3_SICK_TO_STOMACH", e);  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            client.addProblemEntry(
                    protocolId,
                    Constants.HL7V3_SYMPTOM,
                    Constants.STATUS_COMPELETE,
                    "201008200000",
                    "",
                    "loaded by core test data Free text note",
                    "http://www.icardea.at/phrs/instances/BleedingGums");
        } catch (TripleException e) {
            LOGGER.error("loadTestProblem Constants.HL7V3_UNABLE_TO_EAT", e);  //To change body of catch statement use File | Settings | File Templates.
        }


        try {
            interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDCCAT, protocolId);  //"MEDLIST"
        } catch (Exception e) {
            LOGGER.error("loadTestProblem notifyInteropMessageSubscribersByProtocolId  ", e);
        }

    }

    public static void loadTestMedicationWithDrugCode(String protocolId, boolean notify) {
        InteropClients interopClients = PhrsStoreClient.getInstance().getInteropClients();

        MedicationClient medClient = interopClients.getMedicationClient();


        try {
            //add without drug code
            medClient.addMedicationSign(protocolId, "Free text note for the medication 8.",
                    Constants.STATUS_COMPELETE, "199910101010", "201010101010",
                    medClient.buildNullFrequency(),
                    Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.TABLET,
                    "BOB Magnosolv(No CODE)");
        } catch (TripleException e) {
            LOGGER.error("loadTestProblem addMedicationSign  ", e);
        }

        try {
            interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDCCAT, protocolId);  //"MEDLIST"
        } catch (Exception e) {
            LOGGER.error("loadTestProblem notifyInteropMessageSubscribersByProtocolId  ", e);
        }
        try {
            interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDLIST, protocolId);  //"MEDLIST"
        } catch (Exception e) {
            LOGGER.error("loadTestProblem notifyInteropMessageSubscribersByProtocolId  ", e);
        }
    }

    public static void loadTestMedicationNoDrugCode(String protocolId, boolean notify) {
        InteropClients interopClients = PhrsStoreClient.getInstance().getInteropClients();

        MedicationClient medClient = interopClients.getMedicationClient();

        try {

            //add without drug code
            medClient.addMedicationSign(protocolId, "Free text note for the medication 8.",
                    Constants.STATUS_COMPELETE, "199910101010", "201010101010",
                    medClient.buildNullFrequency(),
                    Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.TABLET,
                    "BOB Magnosolv(No CODE)");
        } catch (TripleException e) {
            LOGGER.error("loadTestProblem addMedicationSign  ", e);
        }

        try {
            interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDCCAT, protocolId);  //"MEDLIST"
        } catch (Exception e) {
            LOGGER.error("loadTestProblem notifyInteropMessageSubscribersByProtocolId  ", e);
        }
        try {
            interopClients.notifyInteropMessageSubscribersByProtocolId(InteropProcessor.CARE_PROVISION_CODE_MEDLIST, protocolId);  //"MEDLIST"
        } catch (Exception e) {
            LOGGER.error("loadTestProblem notifyInteropMessageSubscribersByProtocolId  ", e);
        }
    }

    /**
     * Makes a simple four digit id frorm a UUID
     *
     * @return
     */
    public static String makeSimpleId() {
        String id = UUID.randomUUID().toString();
        String[] parts = id.split("-");
        id = parts[1];

        return id;
    }

    /**
     * Load test blood pressure and body weight for this user
     *
     * @param ownerUri
     */
    public static void addTestBasicHealthVitalsData(String ownerUri) {
        if (ownerUri != null) {
            try {
                CommonDao commonDao = PhrsStoreClient.getInstance().getCommonDao();

                ObsVitalsBloodPressure bp1 = new ObsVitalsBloodPressure();

                bp1.setSystolic(160);
                bp1.setDiastolic(90);
                bp1.setBeginDate(new Date());
                bp1.setEndDate(new Date());
                bp1.setNote("note id " + makeSimpleId());
                bp1.setSystemNote(bp1.getNote());
                commonDao.crudSaveResource(bp1, ownerUri, "CoreTestData addTestBasicHealthVitalsData");

                ObsVitalsBloodPressure bp2 = new ObsVitalsBloodPressure();
                bp2.setSystolic(148);
                bp2.setDiastolic(83);
                bp2.setBeginDate(new Date());
                bp2.setEndDate(new Date());
                bp2.setNote("note id " + makeSimpleId());

                bp2.setSystemNote(bp2.getNote());
                commonDao.crudSaveResource(bp2, ownerUri, "CoreTestData addTestBasicHealthVitalsData");

                ObsVitalsBodyWeight bw1 = new ObsVitalsBodyWeight();
                bw1.setBodyWeight(67d);
                bw1.setBodyHeight(171d);
                bw1.setBeginDate(new Date());
                bw1.setEndDate(new Date());
                bw1.setMeasurementUnit(Constants.KILOGRAM);
                bw1.setNote("note id " + makeSimpleId());
                bw1.setSystemNote(bw1.getNote());
                commonDao.crudSaveResource(bw1, ownerUri, "CoreTestData addTestBasicHealthVitalsData");

                ObsVitalsBodyWeight bw2 = new ObsVitalsBodyWeight();
                bw2.setBodyWeight(70d);
                bw2.setBodyHeight(171d);
                bw2.setBeginDate(new Date());
                bw2.setEndDate(new Date());
                bw2.setNote("note id " + makeSimpleId());
                bw2.setSystemNote(bw2.getNote());
                bw2.setMeasurementUnit(Constants.KILOGRAM);
                commonDao.crudSaveResource(bw2, ownerUri, "CoreTestData addTestBasicHealthVitalsData");
                LOGGER.debug("Created test data BP and BW for ownerUri " + ownerUri);

            } catch (Exception e) {
                LOGGER.error("Error creating user test data owner=" + ownerUri, e);
            }
        } else {
            LOGGER.error("Error creating user test data, ownerUri=null");
        }

    }
}