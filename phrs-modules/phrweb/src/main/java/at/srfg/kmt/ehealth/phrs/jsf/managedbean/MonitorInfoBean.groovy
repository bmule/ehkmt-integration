package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped

import org.primefaces.model.StreamedContent

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.MonitorInfoItem
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService

import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.presentation.builder.BuilderUtil


@ManagedBean(name = "monitorinfoBean")
@RequestScoped
public class MonitorInfoBean extends FaceBaseBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(MonitorInfoBean.class);



    boolean handledReportRequestParams = false;

    public MonitorInfoBean() {
        super();//required!!
        // setPermittedActions performed by super class
        domainClazz = MonitorInfoItem.class
        setSelected(MonitorInfoItem.newInstance())

        initVocabularies(domainClazz, getLanguage())
        try {
            loadModelMain()
        } catch (Exception e) {
            println('ShareBean loadModelMain Exception ' + e)
        }

    }

    // AUTHORIZE_RESOURCE_CODE_PHRS_UNKNOWN
    private void buildView() {

        List<PhrFederatedUser> phrUsers = null
        //Either get a smaller list of subjects using the consent editor
        //or get all users in the PHRS system. More can be offered for sharing

        phrUsers = userService.getResources(null, PhrFederatedUser.class);

        if (!phrUsers) phrUsers = []
        internalModelList = []
        //which resources?
        List resourceCodes = config.getConsentSubjectCodes('phr')

        //resourceCodes.add(PhrsConstants.AUTHORIZE_RESOURCE_CODE_CONDITION)
        String action = PhrsConstants.AUTHORIZE_ACTION_CODE_READ
        //Sorting, leave room for higher priority records.
        int sortOrder = 5

        for (PhrFederatedUser ph: phrUsers) {
            sortOrder++
            //for each supported resourceCode, check READ permissions
            for (String resourceType: resourceCodes) {
                try {
                    boolean permitViewContent = permitUserOnPhrId(ph.getOwnerUri(), resourceType, action)

                    boolean permitViewRow = false
                    //check permit for current user, on phrUser with resource type
                    //show row for testing
                    if (config.isAppModeMonitorListAllUsers()) {
                        permitViewRow = true
                        //permitViewContent=true
                    }

                    if (permitViewRow || permitViewContent) {

                        MonitorInfoItem item = new MonitorInfoItem();
                        item.message = ''


                        item.ownerUri = ph.getOwnerUri()
                        item.currentUserId = getSessionUserOwnerUri()
                        item.currentUserRole = getCurrentUserRole()

                        item.setAllowedViewContent(permitViewContent)
                        item.setAllowedViewRow(permitViewRow)

                        item.setResourceType(resourceType)

                        ProfileContactInfo pci = userService.getProfileContactInfo(item.getOwnerUri())


                        if (pci) {
                            String name = pci.getLastName() + pci.getLastName() ? ',' : '' + pci.getFirstName()
                            item.setName(name);
                            String protocolId = null

                            if (pci.getPixIdentifier()) {

                                protocolId = pci.getPixIdentifier().getIdentifier()
                                item.setProtocolId(protocolId)
                            }

                            if (!protocolId) {
                                item.message += 'No Protocol ID found in the Contact Information'
                            }

                            internalModelList.add(item)
                        } else {
                            //item.messageCode=null
                            item.message += 'No contact info provided or protocolId'
                        }
                        item.setSortOrder(sortOrder)
                        if (UserSessionService.isSessionUser(ph.getOwnerUri())) {
                            //put name
                            item.setName('My Report. ' + item.name)
                            item.setSortOrder(1)
                        }

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

        if (getUserService()) {

            internalModelList = buildView()

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
        if (selected) {
            MonitorInfoItem item = (MonitorInfoItem) selected

            streamedContent = createPermittedReport(item.ownerUri, true, item.resourceType)
            LOGGER.debug("getDownLoadReport item.ownerUri="+item.ownerUri+" item.resourceType="+item.resourceType)
        } else if(UserSessionService.getRequestAttributeString('resourcecode')!=null){
            //JSF will invoke the getters while setting up, at least this request param should be valid
            streamedContent = handleReportFromExternalRequest()
            LOGGER.debug("getDownLoadReport external request")
        }
        return streamedContent

    }
    /**
     *
     * @return
     */
    public StreamedContent getDownLoadTestingReport() {
        StreamedContent streamedContent = null
        if (selected) {
            MonitorInfoItem item = (MonitorInfoItem) selected

            streamedContent = createTestingReport(item.ownerUri, true, item.resourceType)

        } else if(UserSessionService.getRequestAttributeString('resourcecode')!=null){
            //JSF will invoke the getters while setting up, at least this request param should be valid
            streamedContent = BuilderUtil.reportBuildDummy() ;
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

        try {
            if (resourceType && targetUserId) {
                //this will lookup the
                reportFile = createPermittedReport(targetUserId, isPhrId, resourceType)
                LOGGER.debug("creating report for: isPhrId= "+isPhrId+" id="+targetUserId+" resourceType="+resourceType)
            } else {
                LOGGER.debug("No params for resourcecode or (phrid or protocolid)")
            }
        } catch (Exception e) {
            LOGGER.error(" targetUserId=" + targetUserId + "idType=" + " resourceType=" + resourceType, e)
        }
        return reportFile
    }

    private String getProtocolIdFromPhrId(String phrId) {

        return userService.getProtocolId(phrId)

    }


    protected boolean isContentViewPermitted(String targetUserId, boolean isPhrId, String resourceType) {
        boolean isContentViewPermitted = false
        //either one
        String idType = null

        if (isPhrId) idType = 'phrid'

        try {

            if (targetUserId && resourceType) {


                if (isPhrId) {
                    isContentViewPermitted = permitUserOnPhrId(targetUserId, resourceType, PhrsConstants.AUTHORIZE_ACTION_CODE_READ)

                    LOGGER.debug(" request phrId=" + targetUserId + " on resourceType=" + resourceType + " permitted?")
                } else {

                    isContentViewPermitted = permitUserOnProtocolId(targetUserId, resourceType, PhrsConstants.AUTHORIZE_ACTION_CODE_READ)

                    LOGGER.debug(" request phrId=" + targetUserId + " resourceType=" + resourceType + " permitted?")
                }

            } else {

                LOGGER.debug(" request error null found : protocolId or phrId=" + isPhrId + " id=" + targetUserId + " resourceType=" + resourceType + " permitted? false")
            }
        } catch (Exception e) {
            LOGGER.error(" targetUserId=" + targetUserId + "idType=" + " resourceType=" + resourceType + " idType=" + idType, e)
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
    protected StreamedContent createPermittedReport(String targetUserId, boolean isPhrId, String resourceType) {

        StreamedContent reportFile = null

        //either one
        String idType = null

        if (isPhrId) idType = 'phrid'

        boolean isPermitted = this.isContentViewPermitted(targetUserId, isPhrId, resourceType)

        reportFile = BuilderUtil.handlePermittedReport(targetUserId, isPhrId, resourceType, isPermitted)
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

        StreamedContent reportFile = null

        //either one
        String idType = null

        if (isPhrId) idType = 'phrid'

        boolean isPermitted = true;
        //this.isContentViewPermitted(targetUserId, isPhrId, resourceType)

        reportFile = BuilderUtil.handlePermittedReport(targetUserId, isPhrId, resourceType, isPermitted)
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