/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.support.test;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.Date;

import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import java.util.HashSet;
import java.util.Set;
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

    public CoreTestData(InteropClients interopClients) {
        this.interopClients = interopClients;

        this.client = interopClients.getMedicationClient();

    }

    public CoreTestData(PhrsStoreClient storeClient) {
        this.interopClients = storeClient.getInteropClients();

        this.client = interopClients.getMedicationClient();

    }
     public CoreTestData() {
        this.interopClients = PhrsStoreClient.getInstance().getInteropClients();

        this.client = interopClients.getMedicationClient();

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
     *
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
        } catch (TripleException e) {
            System.out.println("" + e);
            LOGGER.error("", e);
        } catch (Exception e) {
            System.out.println("" + e);
            LOGGER.error("", e);

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
  * 
  * @param owner
  * @return int expected count of records, not the actual. We test the expected vs found
  */
    public int addTestMedications_2_forPortalTestForOwnerUri(String owner) {
        int countAdded=8;//update this if added more manually...needed by tests
        try {
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

        } catch (TripleException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        } catch (Exception e) {
            LOGGER.error("", e);
            e.printStackTrace();
        }
        
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
}
