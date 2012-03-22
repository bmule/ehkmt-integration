package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.MonitorPhrItem;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.builder.ReportToolTransformer;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService;
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// View scope complains about Audit client not serializable
@ManagedBean(name = "interopBean")
@RequestScoped
public class MonitorInteropBean implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(MonitorInteropBean.class);
    protected UserService userService;
    protected InteropProcessor interopProcessor;
    protected AuthorizationService authorizationService;
    protected MonitorPhrItem selected = null;
    protected String selectedResourceType;
    protected List<MonitorPhrItem> modelMain;
    private String ownerUri;
    private PhrFederatedUser phrUser;
    private Set<String> IMPORT_TYPES;
    
    private ReportToolTransformer toolTransformer;


    private boolean allowCommandIdentify = true;
    private boolean allowCommandImport = true;

    // for dialogs or forms to control view or edit mode of fields
    public String modify = AuthorizationService.MODIFY_YES;

    public MonitorInteropBean() {
        userService = PhrsStoreClient.getInstance().getPhrsRepositoryClient().getUserService();
        ownerUri = userService.getOwnerUri();
        initTools();

        initModelMain();

    }

    public MonitorInteropBean(String theOwnerUri) {
        userService = PhrsStoreClient.getInstance().getPhrsRepositoryClient().getUserService();
        this.ownerUri = theOwnerUri;
        initTools();

        initModelMain();

    }

    private void initTools() {
        IMPORT_TYPES = new HashSet<String>();

        IMPORT_TYPES.add(Constants.PHRS_MEDICATION_CLASS);
        IMPORT_TYPES.add(Constants.PHRS_VITAL_SIGN_CLASS);

        interopProcessor = PhrsStoreClient.getInstance().getInteropProcessor();
        authorizationService = new AuthorizationService();
        toolTransformer = new ReportToolTransformer();

        phrUser = userService.getPhrUser(ownerUri);

        if(findUserPixPid() == null) {
            this.allowCommandImport=false;
            LOGGER.debug("MonitorInteropBean - No PID available allowCommandImport=false");
            //WebUtil.addFacesMessageSeverityInfo("Patient Identifier Status","Importing data is not possible, please return to the home page to update your CIED info");
        } else {
            this.allowCommandImport=true;
            LOGGER.debug("MonitorInteropBean - PID available allowCommandImport=true");
        }

    }
    public String findUserPixPid() {
        if (phrUser != null && phrUser.getProtocolIdPix() != null && ! phrUser.getProtocolIdPix().isEmpty()) {
            return phrUser.getProtocolIdPix();
        }
        return null;
    }
    private List importSelectedTypes(Set<String> importTypes,boolean saveImports){
        List transformedMsgs = new ArrayList();
        
        for(String phrClass:importTypes){        
            List temp = interopProcessor.importNewMessages(
                    getOwnerUri(),
                    phrClass,
                    saveImports);
            if(temp!=null && !temp.isEmpty()){
                transformedMsgs.addAll(temp);
            }
        }
        return transformedMsgs;
    }
    /**
     * Show Messages available for import
     */

    private void initModelMain() {
        if(allowCommandImport){
            LOGGER.debug("START initModelMain for ownerUri=" + getOwnerUri());

            List transformedMsgs = importSelectedTypes(IMPORT_TYPES,false);
                    //interopProcessor.importNewMessages(
                    //getOwnerUri(),
                    //Constants.PHRS_MEDICATION_CLASS,
                    //false); //false, do not import new Messages, only report

            int count = transformedMsgs == null ? -1 : transformedMsgs.size();

            if (count > 0) {
                //ok
                LOGGER.debug("transformedMsgs. OK found INTEROP MEDS found count=" + count);
                modelMain = toolTransformer.tranformResource(transformedMsgs);
            } else {

                LOGGER.debug("transformedMsgs. No interop meds found, create test data");
                modelMain = CoreTestData.createMedicationMonitorPhrItems(getOwnerUri());

                int count2 = modelMain == null ? -1 : modelMain.size();
                LOGGER.debug("transformedMsgs from test data, modelMain count=" + count2);
            }
            LOGGER.debug("END initModelMain for ownerUri=" + getOwnerUri()+ " original message count="+count);
        } else {
            LOGGER.debug("START/END allowCommandImport=false No initModelMain for ownerUri=" + getOwnerUri());
        }
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

    public String getModify() {
        return modify;
    }

    public boolean getAllowCommandIdentify() {
        return allowCommandIdentify;
    }
    //needed by UI  "is"
    public boolean isAllowCommandIdentify() {
        return allowCommandIdentify;
    }

    public void setAllowCommandIdentify(boolean allowCommandIdentify) {
        this.allowCommandIdentify = allowCommandIdentify;
    }

    public boolean getAllowCommandImport() {
        return allowCommandImport;
    }
    //needed by UI  "is"
    public boolean isAllowCommandImport() {
        return allowCommandImport;
    }

    public void setAllowCommandImport(boolean allowCommandImport) {
        this.allowCommandImport = allowCommandImport;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void commandTest() {

        LOGGER.debug("commandTest()");
        WebUtil.addFacesMessageSeverityInfo("commandTest", "commandTest()");

    }

    /**
     * Import health records
     */
    public void commandImportMessages() {
        try {

            LOGGER.debug("Start MonitorPhrItem form action: commandImportMessages for ownerUri=" + getOwnerUri());


            List transformedMsgs = importSelectedTypes(IMPORT_TYPES,true); //true save imported data

            int count = 0;
            if (transformedMsgs != null && !transformedMsgs.isEmpty()) {
                count = transformedMsgs.size();
            }

            if (count > 0) {
                //reset model main, in request scope
                //if reshow....setModelMain(transformedMsgs);
                WebUtil.addFacesMessageSeverityInfo("Import Status", "Successfully imported " + count + " Medication records. Please check your Medications list");
                LOGGER.debug("import OK, records found=" + count);

            } else {
                if (ConfigurationService.isAppModeTest()) {
                    CoreTestData.addTestMedicationsPhr(getOwnerUri());
                    LOGGER.debug("addTestMedicationsPhr owner=" + getOwnerUri());
                } else {
                    LOGGER.debug("import no records found");
                    WebUtil.addFacesMessageSeverityWarn("Import Status", "There are no Medication records to import");
                }

                //WebUtil.addFacesMessageSeverityWarn("Import Status", "There are no Medication records to import");
            }
        } catch (Exception e) {
            LOGGER.error("Error with commandImportMessages", e);
        }

        LOGGER.debug("END MonitorPhrItem form action: commandImportMessages for ownerUri=" + getOwnerUri());
        initModelMain();
    }


}
