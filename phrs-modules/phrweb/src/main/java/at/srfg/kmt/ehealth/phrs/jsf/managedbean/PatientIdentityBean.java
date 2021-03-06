package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.builder.ReportToolTransformer;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService;
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
        return pixQueryIdUser;

        //else if (phrUser != null) {
        //    return phrUser.getPixQueryIdUser() != null ? phrUser.getPixQueryIdUser() : null;
        //}

    }

    public void setPixQueryIdUser(String pixQueryIdUser) {
        this.pixQueryIdUser = pixQueryIdUser;
    }

    /**
     * @return when unassigned, PixService.PIX_QUERY_TYPE_DEFAULT
     */
    public String getPixQueryIdType() {
        String temp= pixQueryIdType != null ? pixQueryIdType : PixService.PIX_QUERY_TYPE_DEFAULT;
        return temp;
        //if (phrUser != null) {
        ///    return phrUser.getPixQueryIdType() != null ? phrUser.getPixQueryIdType() : PixService.PIX_QUERY_TYPE_DEFAULT;
        //}
        //return PixService.PIX_QUERY_TYPE_DEFAULT;
    }

    public void setPixQueryIdType(String pixQueryIdType) {
        this.pixQueryIdType = pixQueryIdType;
    }

    public String findUserPixPid() {
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
        ownerUri=userService.getOwnerUri();
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
                + " pixPid" + findUserPixPid());
        //
        boolean outcome = updateIdentifiers();
        String msg= outcome ? "Your patient identifier was found: "+ findUserPixPid() :"Your patient identifier was not found" ;
        // " Your patient identifier " + outcome  ? "was not found using this identifier":"was found: "+findUserPixPid());
        this.setStatusMessagePid(msg);

    }

    public boolean updateIdentifiers() {
        boolean outcome = false;
        try {

            LOGGER.debug("PatientIdentityBean COPY from VT  updateIdentifiers(disabled) Start. do query.   "
                    + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());

            if (getOwnerUri() != null && !getOwnerUri().isEmpty()
                    && getPixQueryIdType() != null && ! getPixQueryIdType().isEmpty()
                    && getPixQueryIdUser() != null && ! getPixQueryIdUser().isEmpty()) {

                ownerUri=userService.getOwnerUri();
                phrUser = userService.getPhrUser(ownerUri);

                PixService pixService = new PixService();
                String returnPid = null;
                String ciedIdentifier =null;
                //perform PIX query and update user account
                //String returnPid = pixService.updateProtocolIdFromUserProvidedCiedId(getOwnerUri(), getPixQueryIdUser(), getPixQueryIdType());

                //To find the PID (protocolId), the UI provides "type of query" and the identifier to query
                //a prefix pid: or cied: indicates the query namespace. The type is from a CIED models.
                //Direct entry of Protocolid (pid)
                if(getPixQueryIdType().startsWith("pid")){
                    //assign returnPid directly from UI
                    returnPid= getPixQueryIdUser();
                    ciedIdentifier=getPixQueryIdType()+":"+returnPid;
                } else {
                    //The PIX query requires a specific format, see the PIX query for that
                    //Query for PID using CIED implant identifier. Perform PIX query on CIED : 2 parts model (getPixQueryIdType) and serial (getPixQueryIdUser).
                    ciedIdentifier = PixService.makePixIdentifier(getPixQueryIdType(), getPixQueryIdUser());
                    LOGGER.debug("PatientIdentityBean VT  ciedIdentifier=" + ciedIdentifier + " pixQueryIdUser=" + getPixQueryIdUser() + " pixQueryIdType= " + getPixQueryIdType());
                    returnPid = pixService.getPatientProtocolIdByCIED(ciedIdentifier);

                    //phrUser.setProtocolIdPix(returnPid);
                }
                if(phrUser!=null && UserSessionService.isSpecialUser(phrUser.getIdentifier())){
                    if(returnPid != null && "191".equals(returnPid)){
                        LOGGER.debug("PIX returned 191 successfully");
                    }  else {
                        LOGGER.debug("PIX RESET for special user, PIX returned returnPid= "+returnPid);
                        returnPid="191";
                    }
                }
                
                if (returnPid != null && ! returnPid.isEmpty()) {
                    outcome = true;

                    try {
                        //update the session protocolId for the consent mananger!
                        UserSessionService.updateSessionProtocolId(returnPid);
                        LOGGER.debug("Updated session ProtocolId for consentmgr"+returnPid);
                        UserSessionService.logSessionMap();
                    } catch (Exception e) {
                        LOGGER.error("error updating session ProtocolId="+returnPid);
                    }
                }
                if (outcome) {
             
                    if(phrUser != null){
                        phrUser.setProtocolIdPix(returnPid);
                        //This might reset the returnPid in case PIX server is done

                        phrUser.setPixQueryIdType(getPixQueryIdType());
                        phrUser.setPixQueryIdUser(getPixQueryIdUser());
                        userService.crudSaveResource(phrUser,phrUser.getOwnerUri(),phrUser.getOwnerUri());

                        LOGGER.debug("Saved  PHR user with pid="+phrUser.getProtocolIdPix()+" PixQueryIdUser "+phrUser.getPixQueryIdUser()+" PixQueryIdUser "+phrUser.getPixQueryIdUser());
                    } else {
                        LOGGER.error("Error  PHR user is NULL, but pixQuery returned  pid="+returnPid);

                    }

                    addStatusMessagePID("Patient ID found, ID is: " + returnPid + " for ciedIdentifier =" + ciedIdentifier + " for owner=" + getOwnerUri());
                } else {
                     
                    addStatusMessagePID("Patient ID NOT FOUND for owner=" + getOwnerUri() + " for ciedIdentifier =" + ciedIdentifier);
                }

                //updateProtocolIdFromUserProvidedCiedId(getOwnerUri(), getPixQueryIdUser(), getPixQueryIdType());
                LOGGER.debug("PatientIdentityBean VT updateIdentifiers  returnPid status from getPatientProtocolIdByCIED found and saved="+ outcome+ " returnPid= " + returnPid
                        + " ciedIdentifier=" + ciedIdentifier + " returnPid=" + returnPid
                        + " ownerUri=" + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());

 
                determineStatusPID(phrUser);

                //determine status and refresh new user account
                //determineStatusPID();
            } else {
                LOGGER.error("PatientIdentityBean VT  updateIdentifiers Null value found: updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "
                        + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());
            }
        } catch (Exception e) {
            LOGGER.error("PatientIdentityBean VT  Error updateIdentifiers  updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "
                    + getOwnerUri() + " PixQueryIdType " + getPixQueryIdType() + " PixQueryIdUser" + getPixQueryIdUser());
        }
        return outcome;
    }

}
