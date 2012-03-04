package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.MonitorInfoItem
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo
import at.srfg.kmt.ehealth.phrs.presentation.builder.ReportUtil
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
import org.primefaces.model.StreamedContent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData

@ManagedBean(name = "monitorinfoBean")
@RequestScoped
public class MonitorInfoBean extends FaceBaseBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(MonitorInfoBean.class);


    //ConfigurationService cfrom FaceBaseBean
    
    boolean handledReportRequestParams = false;

    public MonitorInfoBean() {
        super();//required!!
        // setPermittedActions performed by super class
        domainClazz = MonitorInfoItem.class
        setSelected(MonitorInfoItem.newInstance())

        initVocabularies(domainClazz, getLanguage())
        try {
            createModelTestUsers()
            loadModelMain()
        } catch (Exception e) {
            LOGGER.debug("MonitorInfoBean loadModelMain  "+ e);
        }

    }
    /**
     * Runs once unless data store is empty
     */
    public void createModelTestUsers(){
        //ConfigurationService.getInstance().
        CoreTestData.createTestUsersForMonitoring();
    }
    /**
     * Build the list view of reports available to this authenticated user
     * They see their own personal reports first, followed by a list of users and priviledges
     * TODO For medical roles, do not disply their personal reports
     */
    private List<MonitorInfoItem> buildView(List<PhrFederatedUser> phrUsers,AuthorizationService authorizationService, String viewingUser) {
        LOGGER.debug("buildView start");
        List<MonitorInfoItem>  infoList=new ArrayList<MonitorInfoItem>();
        if( viewingUser && phrUsers)  {
            //ok
        } else {
            LOGGER.error("missing view user")
            return infoList
        }
        //Either get a smaller list of subjects using the consent editor
        //or get all users in the PHRS system. More can be offered for sharing

        List resourceCodes = ConfigurationService.getInstance().getConsentSubjectCodes('phr')
        LOGGER.debug("buildView resourceCodes "+resourceCodes);
        //resourceCodes.add(PhrsConstants.AUTHORIZE_RESOURCE_CODE_CONDITION)
        String action = PhrsConstants.AUTHORIZE_ACTION_CODE_READ
        //Sorting, leave room for higher priority records.
        int sortOrder = 5

        for (PhrFederatedUser ph: phrUsers) {
            //skip reports for medical user
            if(UserSessionService.sessionUserHasMedicalRole())  {
                if(ph.ownerUri == viewingUser)
                continue
            }
            sortOrder++
            LOGGER.debug('buildView userid='+ph.getIdentifier()+' nickname:'+ ph.getNickname());
            //for each supported resourceCode, check READ permissions
            for (String resourceType: resourceCodes) {
                LOGGER.debug('buildView resourceType='+resourceType+' nickname:'+ ph.getNickname());
                try {
                    boolean permitViewContent = authorizationService.grantAccessByPhrId(ph.getOwnerUri(), resourceType, action)
                    LOGGER.debug('buildView permitViewContent resourceType='+resourceType+' permitViewContent:'+permitViewContent);
                    boolean permitViewRow = false
                    //check permit for current user, on phrUser with resource type
                    //show row for testing
                    if (ConfigurationService.isAppModeMonitorListAllUsers()) {
                        permitViewRow = true
                        LOGGER.debug('buildView isAppModeMonitorListAllUsers permitViewRow='+permitViewRow);

                        //permitViewContent=true
                    }

                    MonitorInfoItem item = new MonitorInfoItem();
                    item.message = ''

                    item.ownerUri = ph.getOwnerUri()
                    item.currentUserId = getSessionUserOwnerUri()
                    item.currentUserRole = getCurrentUserRole()

                    item.setAllowedViewContent(permitViewContent)
                    item.setAllowedViewRow(permitViewRow)

                    item.setResourceType(resourceType)

                    ProfileContactInfo pci = userService.getProfileContactInfo(item.getOwnerUri())

                    String theName=userService.getUserGreetName(ph.getOwnerUri())
                    item.setName(theName);
                    String protocolId = ph.getProtocolId();

                    item.setProtocolId(protocolId)

                    if (!protocolId) {
                        item.addMessages('No Protocol ID was found to connect health systems')
                        item.message += ' No Protocol ID was found to connect health systems. '
                    }

                    String pixQueryId= ph.getPixQueryIdUser();

                    item.setPixQueryIdUser(pixQueryId)
                    item.setPixQueryIdNamespace(ph.getPixQueryIdNamespace())

                    if (!pixQueryId) {
                        item.addMessages('No CIED Implant ID was found')
                        item.message += ' No CIED Implant ID was found'
                    }

                    item.setSortOrder(sortOrder)
                    if (UserSessionService.isSessionUser(ph.getOwnerUri())) {
                        //put name
                        item.setName('My Report: ' + theName)
                        item.setSortOrder(1)
                    }

                    if (permitViewRow || permitViewContent) {
                        internalModelList.add(item)
                    } else {  //no permission
                        item.name ='---------'
                        item.protocolId= '---------'
                        item.addMessages('test mode')
                        item.message ='test mode: ' + item.message
                        infoList.add(item)
                    }

                } catch (Exception e) {
                    LOGGER.error("", e)
                }
            }//resource type
        }

    }

    /**
     * Override default model, this model is not currently
     * cached
     */
    @Override
    public void loadModelMain() {
        LOGGER.debug("loadModelMain");
        if (getUserService()) {
            LOGGER.debug("loadModelMain to buildView");
            List<PhrFederatedUser>  list= userService.getResources(null, PhrFederatedUser.class);
            String requestorOwnerUri= UserSessionService.getSessionAttributePhrId()
            if(requestorOwnerUri) {
                this.internalModelList= buildView(list,this.permit,requestorOwnerUri);
            }  else {
                LOGGER.error("requestorOwnerUri is null, no user owner in session");
            }

            //getUserService().getResources(getDomainClazz());
        }
        if (!internalModelList) internalModelList = []

    }

    @Override
    public void setPermittedActions() {
        super.setPermittedActions();
        setAllowCreate(false);
        setAllowDelete(false);
        setModify(AuthorizationService.MODIFY_NO)
        setAllowView(true)

    }
    /**
     * Download report and look at  request parameters
     * request parameter resourcecode
     * request parameter phrid  or protocolid
     *
     * Only read action
     * Get role from session user
     *
     * @return
     */
    public StreamedContent getDownLoadReport() {
        StreamedContent streamedContent = null
        LOGGER.debug("Start getDownLoadReport  request")
        if (selected) {
            MonitorInfoItem item = (MonitorInfoItem) selected

            streamedContent = createPermittedReport(item.ownerUri, true, item.resourceType,this.permit)
            LOGGER.debug("getDownLoadReport item.ownerUri="+item.ownerUri+" item.resourceType="+item.resourceType)
        } else if(UserSessionService.getRequestAttributeString('resourcecode')!=null){
            //JSF will invoke the getters while setting up, at least this request param should be valid
            LOGGER.debug("START getDownLoadReport from external request")
            streamedContent = handleReportFromExternalRequest()

        }
        LOGGER.debug("Start getDownLoadReport  request")
        return streamedContent

    }
    /**
     * The link tries to get
     * @return
     */
    public void downLoadTestingReport() {
         this.getDownLoadReport()
    }
    public StreamedContent getDownLoadTestingReport() {
        StreamedContent streamedContent = null
        LOGGER.debug('getDownLoadTestingReport accessed')
        if (selected) {
            MonitorInfoItem item = (MonitorInfoItem) selected
            LOGGER.debug('getDownLoadTestingReport selection item ownerUri='+item.ownerUri+' '+item.resourceType)

            streamedContent = createTestingReport(item.ownerUri, true, item.resourceType)

        } else if(UserSessionService.getRequestAttributeString('resourcecode')!=null){
            //JSF will invoke the getters while setting up, at least this request param should be valid
            LOGGER.debug('getDownLoadTestingReport Selected item, other request? No ownerUri. Found resource code')
            streamedContent = ReportUtil.reportBuildDummy() ;
        } else {
            LOGGER.debug('getDownLoadTestingReport no ownerUri or resource code')
            streamedContent = ReportUtil.reportBuildDummy() ;
        }
        return streamedContent

    }

    /**
     * Alternative parameters are expected, not derived from the form
     * Request param: phrid or protocolid
     * Request param:resourcecode
     * @return
     */
    public StreamedContent handleReportFromExternalRequest() {
        //controller is invoked but not by ajax, check for these parameters and then it is a request
        StreamedContent reportFile = null
        String resourceType=null
        String targetUserId=null
        LOGGER.debug('handleReportFromExternalRequest accessed')
        //if not from a local form or ajax, check request for resourcecode  and phrid from another page request
        try {
            if (resourceType == null) {
                resourceType = UserSessionService.getRequestAttributeString('resourcecode')
            }
            if (targetUserId == null) {
                targetUserId = UserSessionService.getRequestAttributeString('phrid')
            }
        } catch (Exception e) {
            LOGGER.error(" targetUserId=" + targetUserId + "idType=" + " resourceType=" + resourceType, e)
        }

        boolean isPhrId = targetUserId ? true : false

        if (!isPhrId) {
            try {

                //passed protocol ID, so try to get the phrId
                targetUserId = UserSessionService.getRequestAttributeString('protocolid')


            } catch (Exception e) {
                LOGGER.error(" targetUserId=" + targetUserId + "idType=" + " resourceType=" + resourceType, e)
            }
        }
        LOGGER.debug('handleReportFromExternalRequest accessed targetUserId='+targetUserId+' resourceType'+resourceType)
        try {
            if (resourceType && targetUserId) {
                //this will lookup the
                reportFile = createPermittedReport(targetUserId, isPhrId, resourceType, this.permit)
                LOGGER.debug("creating report for: isPhrId= "+isPhrId+" id="+targetUserId+" resourceType="+resourceType)
            } else {
                LOGGER.debug("No params for resourcecode or (phrid or protocolid)")
            }
        } catch (Exception e) {
            LOGGER.error(" targetUserId=" + targetUserId + "idType=" + " resourceType=" + resourceType, e)
        }
        return reportFile
    }
    /**
     *
     * @param phrId
     * @return
     */
    private String getProtocolIdFromPhrId(String phrId) {

        return userService.getProtocolId(phrId)

    }

    /**
     *
     * @param targetUserId   (protocolID or phrs Id - issue another system could ask for PID, no phrId
     * @param isPhrId
     * @param resourceType
     * @return
     */
    protected boolean isContentViewPermitted(String targetUserId, boolean isPhrId, String resourceType,AuthorizationService authorizationService) {
        boolean isContentViewPermitted = false
        //either one
        String idType = null
        LOGGER.debug('isContentViewPermitted START  targetUserId='+targetUserId+' resourceType'+resourceType)

        if (isPhrId) idType = 'phrid'

        try {

            if (targetUserId && resourceType) {


                if (isPhrId) {
                    isContentViewPermitted = authorizationService.grantAccessByPhrId(targetUserId, resourceType, PhrsConstants.AUTHORIZE_ACTION_CODE_READ)

                    LOGGER.debug(" isContentViewPermitted request phrId=" + targetUserId + " on resourceType=" + resourceType + " permitted?")
                } else {

                    isContentViewPermitted = grantAccessByProtocolId(targetUserId, resourceType, PhrsConstants.AUTHORIZE_ACTION_CODE_READ)

                    LOGGER.debug(" isContentViewPermitted request pid=" + targetUserId + " resourceType=" + resourceType + " permitted?")
                }

            } else {

                LOGGER.debug(" isContentViewPermitted request error null found : protocolId or phrId=" + isPhrId + " id=" + targetUserId + " resourceType=" + resourceType + " permitted? false")
            }
        } catch (Exception e) {
            LOGGER.error(" isContentViewPermitted targetUserId=" + targetUserId + "idType=" + " resourceType=" + resourceType + " idType=" + idType, e)
        }
        return isContentViewPermitted

    }
    /**
     *  A permitted Report provides what the end user is allowed to see
     *
     *  Determines whether the user is permitted to view the report
     *  Ultimately, a report is generated, although it might indicate
     *  that the user has not rights.
     * @param targetUserId
     * @param isPhrId
     * @param resourceType
     * @return
     */
    protected StreamedContent createPermittedReport(String targetUserId, boolean isPhrId, String resourceType, AuthorizationService authorizationService) {
        LOGGER.debug('createPermittedReport START  targetUserId='+targetUserId+' resourceType'+resourceType)

        StreamedContent reportFile = null

        //either one
        String idType = null

        if (isPhrId) idType = 'phrid'

        boolean isPermitted = this.isContentViewPermitted(targetUserId, isPhrId, resourceType,authorizationService)
        LOGGER.debug('createPermittedReport isPermitted?  targetUserId='+targetUserId+' resourceType'+resourceType+' isPermitted='+isPermitted)

        reportFile = ReportUtil.handlePermittedReport(targetUserId, isPhrId, resourceType, isPermitted)

        if(reportFile==null)
            LOGGER.debug('createPermittedReport report file NULL  targetUserId='+targetUserId+' resourceType'+resourceType+' isPermitted='+isPermitted)
        else
            LOGGER.debug('createPermittedReport report file NULL  targetUserId='+targetUserId+' resourceType'+resourceType+' isPermitted='+isPermitted)

        //check null, exception?
        return reportFile

    }
    /**
     * This is for testing
     * @param targetUserId
     * @param isPhrId
     * @param resourceType
     * @return
     */
    public StreamedContent createTestingReport(String targetUserId, boolean isPhrId, String resourceType) {
        LOGGER.debug('createTestingReport START  targetUserId='+targetUserId+' resourceType='+resourceType+' isPhrid?'+isPhrId)

        StreamedContent reportFile = null

        //either one
        String idType = null

        if (isPhrId) idType = 'phrid'

        boolean isPermitted = true;
        //this.isContentViewPermitted(targetUserId, isPhrId, resourceType)

        reportFile = ReportUtil.handlePermittedReport(targetUserId, isPhrId, resourceType, isPermitted)

        if(reportFile==null)
            LOGGER.debug('createTestingReport report file NULL  targetUserId='+targetUserId+' resourceType'+resourceType+' isPermitted='+isPermitted)
        else
            LOGGER.debug('createTestingReport report file finished targetUserId='+targetUserId+' resourceType'+resourceType+' isPermitted='+isPermitted)

        //check null, exception?
        return reportFile

    }
    //for testing

/*
public FileDownloadController() {
InputStream stream = this.getClass().getResourceAsStream("monitor_info_intro.pdf");
reportFile = new DefaultStreamedContent(stream, "application/pdf",
"downloaded_file.pdf");

}*/

}