package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment;
import at.srfg.kmt.ehealth.phrs.model.baseform.MonitorPhrItem;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.builder.ReportToolTransformer;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService;
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService;
import at.srfg.kmt.ehealth.phrs.security.services.PixService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.List;

@ManagedBean(name = "interopBean")
@RequestScoped

public class MonitorInteropBean {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(MonitorInteropBean.class);

    protected UserService userService;
    protected InteropProcessor interopProcessor;
    protected AuthorizationService authorizationService;

    protected MonitorPhrItem selected = null;

    protected String selectedResourceType;
    protected List<MonitorPhrItem> modelMain;


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

    public MonitorInteropBean() {
        initTools();

        initModelMain();

    }

    private void initTools() {

        userService = PhrsStoreClient.getInstance().getPhrsRepositoryClient().getUserService();
        interopProcessor = PhrsStoreClient.getInstance().getInteropProcessor();
        authorizationService = new AuthorizationService();
        toolTransformer = new ReportToolTransformer();

        ownerUri = userService.getOwnerUri();
        phrUser = userService.getPhrUser(ownerUri);

        if (phrUser != null) {
            pixQueryIdUser = phrUser.getPixQueryIdUser();
            pixQueryIdType = phrUser.getPixQueryIdType();
        }
        //TODO set default   pixQueryIdType   getPixQueryDeviceModel PIX query see below
        //if()selected.pixQueryIdType= PixService.PIX_QUERY_TYPE_DEFAULT; //??where model

    }

    public void initModelMain() {
        LOGGER.debug("START initModelMain for ownerUri=" + getOwnerUri());
        List transformedMsgs = interopProcessor.importNewMessages(
                getOwnerUri(),
                Constants.PHRS_MEDICATION_CLASS,
                false); //false, do not import new Messages, only report
        LOGGER.debug("END initModelMain for ownerUri=" + getOwnerUri());


        //these are unchecked objects
        modelMain = toolTransformer.tranformResource(transformedMsgs);
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
    }

    public void setPixQueryIdUser(String pixQueryIdUser) {
        this.pixQueryIdUser = pixQueryIdUser;
    }

    public String getPixQueryIdType() {
        if (phrUser != null) {
            return phrUser.getPixQueryIdUser();
        }
        return null;
    }

    public void setPixQueryIdType(String pixQueryIdType) {
        this.pixQueryIdType = pixQueryIdType;
    }

    public String getPidPix() {
        return pidPix;
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

    public List<MonitorPhrItem> getModelMain() {
        return modelMain;
    }

    public void setModelMain(List<MonitorPhrItem> modelMain) {
        this.modelMain = modelMain;
    }

    public String getSelectedResourceType() {
        return selectedResourceType;
    }

    public void setSelectedResourceType(String selectedResourceType) {
        this.selectedResourceType = selectedResourceType;
    }

    public MonitorPhrItem getSelected() {
        return selected;
    }

    public void setSelected(MonitorPhrItem selected) {
        this.selected = selected;
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

    public String modify() {
        return "false";
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

    /**
     *  Import health records
     */
    public void commandImportMessages(ActionEvent actionEvent) {
        try {
            System.out.println("commandImportMessage");
            LOGGER.debug("Start MonitorPhrItem form action: commandImportMessages for ownerUri=" + getOwnerUri());
            List transformedMsgs = interopProcessor.importNewMessages(
                    getOwnerUri(),
                    Constants.PHRS_MEDICATION_CLASS,
                    true); //true import the records
            int count=0;
            if(transformedMsgs!=null && ! transformedMsgs.isEmpty()) count=transformedMsgs.size();

            if(count==0){
                WebUtil.addFacesMessageSeverityWarn("Import Status", "There are no Medication records to import");

            }  else {
                WebUtil.addFacesMessageSeverityInfo("Import Status", "Successfully imported " + count + " Medication records. Please check your Medications list");

            }
        } catch (Exception e) {
            LOGGER.error("Error with commandImportMessages",e);
        }

        LOGGER.debug("END MonitorPhrItem form action: commandImportMessages for ownerUri=" + getOwnerUri());
        initModelMain();

    }

    /**
     * getTransformedNewMessages
     */
    public void commandProcessIdentifier(ActionEvent actionEvent) {
        LOGGER.debug("Start commandProcessIdentifier for owner=" + this.getOwnerUri()
                + " pixQueryType" + this.getPixQueryIdType()
                + " pixQueryIdUser" + this.getPixQueryIdUser()
                + " phrUser pid=" + this.getProtocolId()
                + " pixPid" + getPidPix());
        //
        boolean outcome=updateIdentifiers();
        this.setStatusMessagePid("Your patient identifier is ok " + getPidPix() + " " + this.getProtocolId());
    }

    public boolean updateIdentifiers(){
        boolean outcome=false;
        try{

            LOGGER.debug("updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "+
                    getOwnerUri()+" PixQueryIdType "+ getPixQueryIdType()+ " PixQueryIdUser"+getPixQueryIdUser());

            if(getOwnerUri()!= null && !getOwnerUri().isEmpty()
                    && getPixQueryIdType()!=null && ! getPixQueryIdType().isEmpty()
                    && getPixQueryIdUser()!=null && ! getPixQueryIdUser().isEmpty()){
               // getPixQueryDeviceModel
                PixService pixService= new PixService();
                //String pid= pixService.updateProtocolIdFromUserProvidedCiedId( getOwnerUri(), getPixQueryIdUser(),getPixQueryIdType());
                outcome=true;
            } else {
                LOGGER.error("updateIdentifiers Null value found: updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "+
                        getOwnerUri()+" PixQueryIdType "+ getPixQueryIdType()+ " PixQueryIdUser"+getPixQueryIdUser());
            }
        } catch (Exception e){
            LOGGER.error("Error updateIdentifiers  updateIdentifiers Start updateProtocolIdFromUserProvidedCiedId "+
                    getOwnerUri()+" PixQueryIdType "+ getPixQueryIdType()+ " PixQueryIdUser"+getPixQueryIdUser());
        }
        return outcome;
    }


}

   
