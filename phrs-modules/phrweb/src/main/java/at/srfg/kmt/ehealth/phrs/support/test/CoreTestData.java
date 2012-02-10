/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.support.test;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.PHRSRequestClient;
import at.srfg.kmt.ehealth.phrs.jsf.managedbean.ObsBloodPressureBean;
import at.srfg.kmt.ehealth.phrs.model.baseform.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;

import java.util.Date;

import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import java.awt.event.ActionEvent;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.util.*;

import org.apache.commons.beanutils.DynaBean;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    //coreTestData.addTestMedication_1_forAnyUnitTest(owner,"10","to import");
    private CommonDao commonDao;

    public CoreTestData(InteropClients interopClients) {
        this.interopClients = interopClients;

        this.client = interopClients.getMedicationClient();

        commonDao = PhrsStoreClient.getInstance().getCommonDao();
    }

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
        String formattedTime = dateTime.toString(dtf);
        return formattedTime;
    }

    public String makeDateLabelForTitle() {
        String dateStr = formatTimeDate(new DateTime(), DATE_PATTERN_DATE_TIME);
        return makeDateLabelForTitle(" test:");
    }

    public static String makeDateLabelForTitle(String prefix) {
        String dateStr = formatTimeDate(new DateTime(), DATE_PATTERN_DATE_TIME);
        return prefix + new Date().toString();
    }

    /**
     * @param owner
     * @param quantity DRUG_CODE_1 Drug name and code "Drug-Eluting Stents",
     * "C1322815"
     */
    public void addMedication_forAnyUnitTest(String owner, String quantity, String note) {
        this.addTestMedication_1_forAnyUnitTest(owner, quantity, note,
                DRUG_NAME_1,
                DRUG_CODE_1);
    }

    /**
     * Dates 201011030000,201105051010 ... status = Completed,
     * Constants.MILLIGRAM
     *
     * @param owner
     * @param quantity
     * @param note - can be null, but defaults to blank, put the resourceUri
     * string pattern here when needed for testing
     * @param drugName
     * @param drugCode
     */
    public void addTestMedication_1_forAnyUnitTest(String owner, String quantity, String note, String drugName, String drugCode) {
        String theNote = note != null ? note : "";
        try {
            client.addMedicationSign(
                    owner,
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
            LOGGER.debug("END addTestMedication_1_forAnyUnitTest  preparing test data for owner= " + owner);
        } catch (TripleException e) {
            System.out.println("" + e);
            LOGGER.debug("ERROR addTestMedication_1_forAnyUnitTest  preparing test data for owner= " + owner, e);
        } catch (Exception e) {
            System.out.println("" + e);
            LOGGER.debug("ERROR addTestMedication_1_forAnyUnitTest  preparing test data for owner= " + owner, e);
            ;

        }
    }

    /**
     * Bilateral test data owner = Constants.OWNER_URI_CORE_PORTAL_TEST_USER
     */
    public void addTestMedications_2_forPortalTestUser() {
        final String owner = Constants.OWNER_URI_CORE_PORTAL_TEST_USER;
        this.addTestMedications_2_forPortalTestForOwnerUri(owner);
    }

    /**
     * @param owner
     * @return int expected count of records, not the actual. We test the
     * expected vs found
     */
    public int addTestMedications_2_forPortalTestForOwnerUri(String owner) {
        int countAdded = 8;//update this if added more manually...needed by tests

        if (owner != null) {

            try {
                LOGGER.debug("START addTestMedications_2_forPortalTestForOwnerUri Start preparing test data for owner= " + owner);

                final TriplestoreConnectionFactory connectionFactory = TriplestoreConnectionFactory.getInstance();
                final GenericTriplestore triplestore = connectionFactory.getTriplestore();

                final MedicationClient client = new MedicationClient(triplestore);

                client.addMedicationSign(owner, "Free text note for the medication 1.",
                        Constants.STATUS_COMPELETE, "200812010000", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "25", Constants.MILLIGRAM,
                        "Prednisone", "C0032952");

                client.addMedicationSign(owner, "Free text note for the medication 2.",
                        Constants.STATUS_COMPELETE, "200812010000", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "40", Constants.MILLIGRAM,
                        "Pantoprazole (Pantoloc)", "C0081876");

                client.addMedicationSign(owner, "Free text note for the medication 3.",
                        Constants.STATUS_COMPELETE, "199910101010", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "5", Constants.MILLIGRAM,
                        "Concor", "C0110591");

                client.addMedicationSign(owner, "Free text note for the medication 4.",
                        Constants.STATUS_COMPELETE, "199910101010", "201010101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.DROPS,
                        "Psychopax (Diazepam)", "C0012010");

                client.addMedicationSign(owner, "Free text note for the medication 5.",
                        Constants.STATUS_COMPELETE, "198010101010", "20110601010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "300", Constants.MILLIGRAM,
                        "Convulex", "C0591288");

                client.addMedicationSign(owner, "Free text note for the medication 6.",
                        Constants.STATUS_COMPELETE, "20090101010", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "20", Constants.MILLIGRAM,
                        "Ebetrexat(Methotrexate)", "C0025677");

                client.addMedicationSign(owner, "Free text note for the medication 7.",
                        Constants.STATUS_COMPELETE, "20090101010", "201106101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "10", Constants.MILLIGRAM,
                        "Folsan(Folic Acid)", "C0016410");

                client.addMedicationSign(owner, "Free text note for the medication 8.",
                        Constants.STATUS_COMPELETE, "199910101010", "201010101010",
                        client.buildNullFrequency(),
                        Constants.HL7V3_ORAL_ADMINISTRATION, "1", Constants.TABLET,
                        "Magnosolv(Magnesium)", "C0024467");
                LOGGER.debug("END addTestMedications_2_forPortalTestForOwnerUri  preparing test data for owner= " + owner);
            } catch (TripleException e) {
                e.printStackTrace();
                LOGGER.debug("ERROR addTestMedications_2_forPortalTestForOwnerUri  preparing test data for owner= " + owner, e);
            } catch (Exception e) {
                LOGGER.debug("ERROR addTestMedications_2_forPortalTestForOwnerUri  preparing test data for owner= " + owner, e);
                e.printStackTrace();
            }
        } else {
            LOGGER.error("Error creating user test data, ownerUri=null");
        }
        
        notifySubscribers("MEDLIST");
        
        return countAdded;

    }

    public Set getDynaBeans(String owner) {
        Set<DynaBean> beans = null;
        try {
            final Iterable<String> uris = client.getMedicationURIsForUser(owner);
            final DynaBeanClient dynaBeanClient = new DynaBeanClient(interopClients.getTriplestore());
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
     *
     */
    public static void createTestUserData() {
        try {
            LOGGER.debug("Start preparing test data for phrtest191, phrtest190, phrtestm and " + PhrsConstants.AUTHORIZE_USER_PREFIX_TEST);
            boolean createUser = true;
            PhrsStoreClient sc = PhrsStoreClient.getInstance();
            createTestUserData("phrtest" + Constants.PROTOCOL_ID_PIX_TEST_PATIENT, Constants.PROTOCOL_ID_PIX_TEST_PATIENT, sc, createUser);
            createTestUserData("phrtest" + PROTOCOL_ID_190, PROTOCOL_ID_190, sc, createUser);
            createTestUserData(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST, Constants.PROTOCOL_ID_PIX_TEST_PATIENT, sc, createUser);
            createTestUserData("phrtestm", Constants.PROTOCOL_ID_UNIT_TEST, sc, createUser);
            LOGGER.debug("Created test data for phrtest191, phrtest190, phrtestm and " + PhrsConstants.AUTHORIZE_USER_PREFIX_TEST);
        } catch (Exception e) {
            LOGGER.error("Error creating user test data, creating PhrsStoreClient ? ", e);
        }
    }

    /**
     * Create user test data with login identifiers: phrtest190 and phrtest191
     *
     * @param nickname - one word, beginnning with phrtest
     *
     * @param protocolId
     * @param sc
     * @param sc
     */
    public static void createTestUserData(String nickname, String protocolId, PhrsStoreClient sc, boolean createUser) {

        try {
            CommonDao commonDao = PhrsStoreClient.getInstance().getCommonDao();
            String ownerUri = PhrFederatedUser.makeOwnerUri();
            PhrFederatedUser user = commonDao.getPhrUser(ownerUri, true);
            user.setOwnerUri(ownerUri);
            user.setNickname(nickname);
            user.setIdentifier(nickname);

            commonDao.crudSaveResource(user, ownerUri, "CoreTestData createTestUserData");

            ProfileContactInfo info = commonDao.getProfileContactInfo(ownerUri);
            PixIdentifier pixIdentifier = new PixIdentifier(protocolId, true, true, PhrsConstants.IDENTIFIER_TYPE_PIX_PROTOCOL_ID);
            pixIdentifier.setDomain(Constants.ICARDEA_DOMAIN_PIX_OID);
            commonDao.crudSaveResource(info, ownerUri, "CoreTestData createTestUserData");

            commonDao.registerProtocolId(ownerUri, protocolId, Constants.ICARDEA_DOMAIN_PIX_OID);
            info.setPixIdentifier(pixIdentifier);

            ObsVitalsBloodPressure bp1 = new ObsVitalsBloodPressure();
            bp1.setSystolic(110);
            bp1.setDiastolic(70);
            bp1.setBeginDate(new Date());
            bp1.setEndDate(new Date());
            bp1.setNote("note id " + makeSimpleId());
            bp1.setSystemNote(bp1.getNote());
            commonDao.crudSaveResource(bp1, ownerUri, "CoreTestData createTestUserData");

            ObsVitalsBloodPressure bp2 = new ObsVitalsBloodPressure();
            bp2.setSystolic(125);
            bp2.setDiastolic(84);
            bp2.setBeginDate(new Date());
            bp2.setEndDate(new Date());
            bp2.setNote("note id " + makeSimpleId());
            bp2.setSystemNote(bp2.getNote());
            commonDao.crudSaveResource(bp2, ownerUri, "CoreTestData createTestUserData");

            ObsVitalsBodyWeight bw1 = new ObsVitalsBodyWeight();
            bw1.setBodyWeight(75d);
            bw1.setBodyHeight(173d);
            bw1.setBeginDate(new Date());
            bw1.setEndDate(new Date());
            bw1.setNote("note id " + makeSimpleId());
            bw1.setSystemNote(bw1.getNote());
            commonDao.crudSaveResource(bw1, ownerUri, "CoreTestData createTestUserData");

            ObsVitalsBodyWeight bw2 = new ObsVitalsBodyWeight();
            bw2.setBodyWeight(75d);
            bw2.setBodyHeight(173d);
            bw2.setBeginDate(new Date());
            bw2.setEndDate(new Date());
            bw2.setNote("note id " + makeSimpleId());
            bw2.setSystemNote(bw2.getNote());
            commonDao.crudSaveResource(bw2, ownerUri, "CoreTestData createTestUserData");
            LOGGER.debug("Created test data for  nickname " + nickname + " ownerUri " + ownerUri + " protocolId " + protocolId);

        } catch (Exception e) {
            LOGGER.error("Error creating user test data", e);
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
                commonDao.crudSaveResource(bw2, ownerUri, "CoreTestData addTestBasicHealthVitalsData");
                LOGGER.debug("Created test data BP and BW for ownerUri " + ownerUri);

            } catch (Exception e) {
                LOGGER.error("Error creating user test data owner=" + ownerUri, e);
            }
        } else {
            LOGGER.error("Error creating user test data, ownerUri=null");
        }

    }

    public void notifySubscribers(String careProvisionCode) {
        try {
            PHRSRequestClient requestClient = this.interopClients.getPHRSRequestClient();
            DynaBeanClient beanClient = this.interopClients.getDynaBeanClient();
            final Iterable<String> resources = requestClient.getAllPHRSRequests();
            for (String resource : resources) {
                final DynaBean request = beanClient.getDynaBean(resource);
//                    final String code =
//                            (String) request.get("http://www.icardea.at/phrs/hl7V3#careProcisionCode");
                final String wsAdress =
                        (String) request.get("http://www.icardea.at/phrs/hl7V3#wsReplyAddress");
                final String id =
                        (String) request.get("http://www.icardea.at/phrs/actor#protocolId");

               

                final Map<String, String> properties = new HashMap<String, String>();
                properties.put("patientId", id);
                properties.put("patientNames", "patientNames");

                //Care Provision Code
                properties.put("careProvisionCode", careProvisionCode);
                properties.put("responseEndpointURI", wsAdress);

                notify("localhost", 5578, properties);
                LOGGER.error("Finished - Notified Core after Loading test data ");

            }
            LOGGER.error("Finished - Notified Core after Loading test data ");
        } catch (Exception e) {
            LOGGER.error("Failed to Notify Core after Loading test data "+e.getMessage(), e);
        }
    }

    private void notify(String host, int port, Map<String, String> params) {
        LOGGER.debug("Tries to dispach this properties {}.", params);
        try {
            final Socket socket = new Socket(host, port);
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(params);
        } catch (Exception e) {
            LOGGER.error("Prameters : {} can not be dispathed.", params);
            LOGGER.error(e.getMessage(), e);
        }

        LOGGER.debug("The {} was distpatched", params);
    }

    public static void main(String[] args) {

        System.out.println("x=" + makeSimpleId());

        System.out.println("y=" + makeSimpleId());

        System.out.println("z=" + makeSimpleId());

    }
}