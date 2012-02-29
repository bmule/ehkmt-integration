package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.security.services.PixService;
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * A means to test UI components and integration with PixService,
 * Interoperability clients, and ConsentManager
 */
@ManagedBean(name = "vtestBean")
@RequestScoped
public class Vtest extends MonitorInteropBean implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Vtest.class);


    /**
     * Uses only the test user, and methods from MonitorInteropBean
     */
    public Vtest() {
        super(ConfigurationService.getInstance().getProperty("test.user.1.login.id", "phrtest"));
        //ownerUri = ConfigurationService.getInstance().getProperty("test.user.1.login.id", "phrtest");

    }


    /**
     * UI button
     */
    public void loadTestData() {
        LOGGER.error("VT web form got: loadTestData ");
        try {
            CoreTestData.addTestBasicHealthVitalsData(getOwnerUri());
        } catch (Exception e) {
            LOGGER.error("VT loadTestData failed", e);
        }
    }
    /*
     * UI button
     */

    public void loadInterop() {
        LOGGER.error("VT web form got: loadInterop ownerUri "+getOwnerUri());
        try {
            CoreTestData test = new CoreTestData();
            test.addTestMedications_2_forPortalTestForOwnerUri(getOwnerUri());
        } catch (Exception e) {
            LOGGER.error("VT loadInterop failed"+getOwnerUri(), e);
        }
    }

    /**
     * load, not notified?
     */
    public void loadTestProblemsMedications() {
        LOGGER.error("VT web form got: loadTestProblemsMedications test user  ");
        try {

            CoreTestData.loadTestProblemsMedications(false);
        } catch (Exception e) {
            LOGGER.error("VT loadTestProblemsMedications failed test user ", e);
        }
    }

    public void loadTestProblem() {
        LOGGER.error("VT web form got: loadTestProblemNotify test user ");
        try {

            CoreTestData.loadTestProblem(getOwnerUri(),true);
        } catch (Exception e) {
            LOGGER.error("VT loadTestProblemNotify failed test user"+getOwnerUri(), e);
        }
    }
    public void loadTestMedicationNoDrugCodeNotify() {
        LOGGER.error("VT web form got: loadTestMedicationNoDrugCodeNotify test user ");
        try {

            CoreTestData.loadTestMedicationNoDrugCode(getOwnerUri(),true);
        } catch (Exception e) {
            LOGGER.error("VT loadTestMedicationNoDrugCodeNotify failed test user"+getOwnerUri(), e);
        }
    }

    public void loadTestMedicationWithDrugCodeNotify() {
        LOGGER.error("VT web form got: loadTestMedicationWithDrugCodeNotify test user ");
        try {

            CoreTestData.loadTestMedicationWithDrugCode(getOwnerUri(),true);
        } catch (Exception e) {
            LOGGER.error("VT loadTestMedicationWithDrugCodeNotify failed test user"+getOwnerUri(), e);
        }
    }
    public void loadTestProblemsMedicationsNotify() {
        LOGGER.error("VT web form got: loadTestProblemsMedicationsNotify test user ");
        try {

            CoreTestData.loadTestProblemsMedications(getOwnerUri(),true);
        } catch (Exception e) {
            LOGGER.error("VT loadTestProblemsMedicationsNotify failed test user"+getOwnerUri(), e);
        }
    }

    @Override
    public void commandTest() {

        LOGGER.debug("VT comVT mandTest()");
        WebUtil.addFacesMessageSeverityInfo("VT commandTest", "commandTest()");

    }

    /**
     * No saving performed of identifiers or user
     *
     * @return
     */
    @Override
    public boolean updateIdentifiers() {
        boolean outcome = false;
        try {

            LOGGER.debug("VT TEST updateIdentifiers(disabled) Start. do query.   "
                    + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());

            if (getOwnerUri() != null && !getOwnerUri().isEmpty()
                    && getPixQueryIdType() != null && !getPixQueryIdType().isEmpty()
                    && getPixQueryIdUser() != null && !getPixQueryIdUser().isEmpty()) {


                PixService pixService = new PixService();
                //perform PIX query and update user account
                //String returnPid = pixService.updateProtocolIdFromUserProvidedCiedId(getOwnerUri(), getPixQueryIdUser(), getPixQueryIdType());

                String ciedIdentifier = PixService.makePixIdentifier(getPixQueryIdType(), getPixQueryIdUser());
                LOGGER.debug("VT TEST ciedIdentifier="+ciedIdentifier+" pixQueryIdUser="+getPixQueryIdUser()+" pixQueryIdType= "+getPixQueryIdType());

                String returnPid = pixService.getPatientProtocolIdByCIED(ciedIdentifier);

                if (returnPid != null) {
                    addStatusMessagePID("Patient ID found, ID is: " + returnPid +" for ciedIdentifier ="+ciedIdentifier+ " for owner=" + getOwnerUri());
                } else {
                    addStatusMessagePID("Patient ID NOT FOUND for owner=" + getOwnerUri()+" for ciedIdentifier ="+ciedIdentifier);
                }

                //updateProtocolIdFromUserProvidedCiedId(getOwnerUri(), getPixQueryIdUser(), getPixQueryIdType());
                LOGGER.error("VT updateIdentifiers (not saved, only query). returnPid value found from getPatientProtocolIdByCIED: returnPid= " + returnPid
                        + " ciedIdentifier=" + ciedIdentifier+" returnPid="+returnPid
                        + " ownerUri="+getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());

                if (returnPid != null && !returnPid.isEmpty()) {
                    outcome = true;
                }


                //determine status and refresh new user account
                //determineStatusPID();
            } else {
                LOGGER.error("VT TEST updateIdentifiers Null value found: updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "
                        + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());
            }
        } catch (Exception e) {
            LOGGER.error("VT TEST Error updateIdentifiers  updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "
                    + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());
        }
        return outcome;
    }

    @Override
    public void determineStatusPID() {

        determineStatusPID(null);
    }

    @Override
    public void determineStatusPID(PhrFederatedUser phrUser) {
    }
}
