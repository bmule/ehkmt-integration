package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.builder.ReportToolTransformer;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService;
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService;
import at.srfg.kmt.ehealth.phrs.security.services.PixService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;

// View scope complains about Audit client not serializable
@ManagedBean(name = "pidBean")
@RequestScoped
public class PatientIdentityBean implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(PatientIdentityBean.class);
    protected UserService userService;
    protected InteropProcessor interopProcessor;
    protected AuthorizationService authorizationService;



    private String ownerUri;
    private PhrFederatedUser phrUser;
    //cied:model:xxxxxx or //pid:///
    private String pixQueryIdType;
    private String pixQueryIdUser;
    private String pidPix;

    private String pidUser;
    private ReportToolTransformer toolTransformer;
    private String statusMessagePid;
    private boolean pidPixFound;
    private boolean pidUserFound;
    private boolean allowCommandIdentify = true;
    private boolean allowCommandImport = true;
    // for dialogs or forms to control view or edit mode of fields
    public String modify = AuthorizationService.MODIFY_YES;

    public PatientIdentityBean() {
        userService = PhrsStoreClient.getInstance().getPhrsRepositoryClient().getUserService();
        ownerUri = userService.getOwnerUri();
        initTools();


    }

    public PatientIdentityBean(String theOwnerUri) {
        userService = PhrsStoreClient.getInstance().getPhrsRepositoryClient().getUserService();
        this.ownerUri = theOwnerUri;
        initTools();


    }

    private void initTools() {


        interopProcessor = PhrsStoreClient.getInstance().getInteropProcessor();
        authorizationService = new AuthorizationService();
        toolTransformer = new ReportToolTransformer();


        phrUser = userService.getPhrUser(ownerUri);
        determineStatusPID(phrUser);

        if (phrUser != null) {
            pixQueryIdUser = phrUser.getPixQueryIdUser();
            pixQueryIdType = phrUser.getPixQueryIdType();
        }
        //TODO set default   pixQueryIdType   getPixQueryDeviceModel PIX query see below
        //if()selected.pixQueryIdType= PixService.PIX_QUERY_TYPE_DEFAULT; //??where model

    }



    public String getOwnerUri() {
        return ownerUri;
    }

    public PhrFederatedUser getPhrUser() {
        return phrUser;
    }

    public String getProtocolId() {
        if (phrUser != null) {
            return phrUser.getProtocolId();
        }
        return null;
    }

    public String getPixQueryIdUser() {

        if (phrUser != null) {
            return phrUser.getPixQueryIdUser() != null ? phrUser.getPixQueryIdUser() : null;
        }
        return null;
    }

    public void setPixQueryIdUser(String pixQueryIdUser) {
        this.pixQueryIdUser = pixQueryIdUser;
    }

    /**
     * @return when unassigned, PixService.PIX_QUERY_TYPE_DEFAULT
     */
    public String getPixQueryIdType() {
        if (phrUser != null) {
            return phrUser.getPixQueryIdType() != null ? phrUser.getPixQueryIdType() : PixService.PIX_QUERY_TYPE_DEFAULT;
        }
        return PixService.PIX_QUERY_TYPE_DEFAULT;
    }

    public void setPixQueryIdType(String pixQueryIdType) {
        this.pixQueryIdType = pixQueryIdType;
    }

    public String getPidPix() {
        if (phrUser != null && phrUser.getProtocolIdPix() != null && !phrUser.getProtocolIdPix().isEmpty()) {
            return phrUser.getProtocolIdPix();
        }
        return null;
    }

    public void setPidPix(String pidPix) {
        this.pidPix = pidPix;
    }

    public String getPidUser() {
        return pidUser;
    }

    public void setPidUser(String pidUser) {
        this.pidUser = pidUser;
    }


    public boolean getPidPixFound() {
        return pidPixFound;
    }

    public boolean isPidPixFound() {
        return pidPixFound;
    }

    public void setPidPixFound(boolean pidPixFound) {
        this.pidPixFound = pidPixFound;
    }

    public boolean getPidUserFound() {
        return pidUserFound;
    }

    public boolean isPidUserFound() {
        return pidUserFound;
    }

    public void setPidUserFound(boolean pidUserFound) {
        this.pidUserFound = pidUserFound;
    }

    public String getStatusMessagePid() {
        return statusMessagePid;
    }

    public void setStatusMessagePid(String statusMessagePid) {
        this.statusMessagePid = statusMessagePid;
    }

    public String getModify() {
        return modify;
    }

    public boolean getAllowCommandIdentify() {
        return allowCommandIdentify;
    }

    public boolean isAllowCommandIdentify() {
        return allowCommandIdentify;
    }

    public void setAllowCommandIdentify(boolean allowCommandIdentify) {
        this.allowCommandIdentify = allowCommandIdentify;
    }

    public boolean getAllowCommandImport() {
        return allowCommandImport;
    }

    public boolean isAllowCommandImport() {
        return allowCommandImport;
    }

    public void setAllowCommandImport(boolean allowCommandImport) {
        this.allowCommandImport = allowCommandImport;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void addStatusMessagePID(String msg) {
        statusMessagePid = statusMessagePid == null ? "" : statusMessagePid;
    }

    /**
     * Perform new query on user, what identifiers are available?
     */
    public void determineStatusPID(PhrFederatedUser phrUser) {
        //in case it was not already done...refresh user
        this.phrUser = phrUser;

        this.setPidPixFound(false);
        this.setPidUserFound(false);


        if (phrUser != null) {
            //pixQueryIdUser = phrUser.getPixQueryIdUser();
            //pixQueryIdType = phrUser.getPixQueryIdType();

            if (phrUser.getProtocolIdPix() != null) {
                this.setPidPixFound(true);
                addStatusMessagePID("Patient ID found, ID is: " + phrUser.getProtocolIdPix());
            }

            if (phrUser.getProtocolIdUser() != null) {
                //no msg, but update flag
                this.setPidUserFound(true);
                if (!this.isPidPixFound()) {
                    addStatusMessagePID("Patient ID provided by User, ID is: " + phrUser.getProtocolIdPix());
                }

            }
        } else {
            addStatusMessagePID("Error could not find PHR user for account: " + getOwnerUri());
        }

    }

    /**
     * get the latest stored resource and check
     */
    public void determineStatusPID() {
        phrUser = userService.getPhrUser(ownerUri);
        determineStatusPID(phrUser);
    }

    public void commandTest() {
       
        LOGGER.debug("commandTest()");
        WebUtil.addFacesMessageSeverityInfo("commandTest", "commandTest()");

    }



    /**
     * getTransformedNewMessages
     */
    public void commandProcessIdentifier() {
      
        LOGGER.debug("Start commandProcessIdentifier for owner=" + this.getOwnerUri()
                + " pixQueryType" + this.getPixQueryIdType()
                + " pixQueryIdUser" + this.getPixQueryIdUser()
                + " phrUser pid=" + this.getProtocolId()
                + " pixPid" + getPidPix());
        //
        boolean outcome = updateIdentifiers();
        this.setStatusMessagePid(" Your patient identifier is ok " + getPidPix());
    }

    public boolean updateIdentifiers() {
        boolean outcome = false;
        try {

            LOGGER.debug("updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "
                    + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());

            if (getOwnerUri() != null && !getOwnerUri().isEmpty()
                    && getPixQueryIdType() != null && !getPixQueryIdType().isEmpty()
                    && getPixQueryIdUser() != null && !getPixQueryIdUser().isEmpty()) {
                // getPixQueryDeviceModel
                PixService pixService = new PixService();
                //perform PIX query and update user account
                String returnPid = pixService.updateProtocolIdFromUserProvidedCiedId(getOwnerUri(), getPixQueryIdUser(), getPixQueryIdType());
                LOGGER.error("updateIdentifiers returnPid value found from updateProtocolIdFromUserProvidedCiedId: returnPid= "+returnPid
                        + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());
                
                if (returnPid != null && !returnPid.isEmpty()) {
                    outcome = true;
                }

                //determine status and refresh new user account
                determineStatusPID();
            } else {
                LOGGER.error("updateIdentifiers Null value found: updateIdentifiers from updateProtocolIdFromUserProvidedCiedId "
                        + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());
            }
        } catch (Exception e) {
            LOGGER.error("Error updateIdentifiers  updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "
                    + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());
        }
        return outcome;
    }

}
