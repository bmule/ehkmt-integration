package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.jsf.support.DocumentReference
import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import javax.faces.application.FacesMessage
import javax.faces.bean.ManagedBean
import javax.faces.bean.SessionScoped
import javax.faces.context.FacesContext
import javax.faces.event.ActionEvent
import org.primefaces.event.NodeExpandEvent
import org.primefaces.event.NodeSelectEvent
import org.primefaces.model.DefaultTreeNode
import org.primefaces.model.TreeNode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import at.srfg.kmt.ehealth.phrs.presentation.builder.ReportTool

/**
 * This controller is SessionScoped and supports either the basic tree or table based tree. The Table based tree cannot yet support expanding 
 * the selected node. However, the simple tree UI widget supports using a selection widget and remembers the previously selected node because of the session scope.
 * This controller also supports the table based tree, however, the widget does not support needed events for 
 * expanding the sidebar correctly upon page changes. Therefore we do extra work here until a new widget library
 * is provided.
 *
 * TODO: refactor and load tree from a configuration tool perhaps using an spreadsheet tool rather than a complex XML structure.
 *
 */
@ManagedBean(name = "menuBean")
@SessionScoped
public class MenuController extends FaceCommon {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class.getName());

    //PhrsConstants.SESSION_MENU_CURRENT_NODE or this bean if SessionScoped

    String defaultNodeType = 'default'
    TreeNode root, home, sectionPatientInformation, sectionGroupInfoPeople, sectionPrivacy
    TreeNode dashboard, dashBoardMonitorPhrImports
    TreeNode sectionMeds, sectionActionPlan, sectionProfile, sectionMonitoring, sectionContacts, sectionMedicalDocs
    TreeNode sectionEdu, sectionCommunity
    TreeNode test
    //sectionObservations,

    String iconLinkType = 'default'//'document'
    String iconChartType = 'default'

    DocumentReference selectedDocument;

    //TODO From selectedDocument, get tree node to open
    //create listener so that menu node stays open after new page
    //Issue: parentNode reference into each Doc reference?
    TreeNode __selectedNode

    TreeNode[] selectedNodes;

    TreeNode selectedRequestNode
    private static ReportTool  reportTool;
    String menuType = 'table'

    public MenuController() {
        reportTool = new ReportTool();
        menuType= ConfigurationService.getInstance().getProperty('menu.type','table');
        init(this.getLocale())

        //previous if using request or view scope
        //String requestNodeTag=UserSessionService.getRequestParameter(PhrsConstants.MENU_CONTROL_REQUEST_PARAMETER_SELECTED_NODE)
        //String requestNodeTag=UserSessionService.getSessionAttribute(PhrsConstants.MENU_CONTROL_REQUEST_PARAMETER_SELECTED_NODE)

    }


//menu.profile=all
//menu.profile=none
//menu.profile=monitor,consult,login,logout,education,social,privacy

    /**
     *
     * @param locale
     */
    public void init(Locale locale) {
        //DefaultTreeNode.DEFAULT_TYPE, Folder


        boolean codedLabel = false

        root = new DefaultTreeNode(new DocumentReference('Main Menu', 'root', PhrsConstants.TYPE_ITEM_NODE_ROOT, codedLabel, null).setRoot(true), null)
        root.setExpanded(true) //address any UI bug

        if ( ! UserSessionService.getSystemStatus()) { //storage problem
            home = new DefaultTreeNode(new DocumentReference('Error', '/jsf/home.xhtml', PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK, codedLabel, root), root)//TYPE_ITEM_NODE_HOME
            WebUtil.addFacesMessageSeverityWarn('Status', 'System failed to start')

        } else if ( ! UserSessionService.loggedIn()) {
            //no side bar menu
            //home = new DefaultTreeNode(new DocumentReference('Home','/jsf/home.xhtml', PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK,codedLabel,root), root)//TYPE_ITEM_NODE_HOME

        } else if (UserSessionService.sessionUserHasMedicalRole()) {
            LOGGER.debug('menuController session user has medical role')
            // one level, no tree nesting
            home = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('default.home.label','Home'), '/jsf/home.xhtml', PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK, codedLabel, root), root)//TYPE_ITEM_NODE_HOME
            //consultation reports
            Map monitoringMap = [
                    '/jsf/monitor_info_dash.xhtml': 'Health Reports']
            addMenuItems(monitoringMap, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, root, codedLabel)

            //sectionMonitoring   = new DefaultTreeNode(new DocumentReference('Monitoring','', PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,root), root)
            //addMenuItems(monitoringMap, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionMonitoring,codedLabel)

        } else {
            String menuProfile= ConfigurationService.getInstance().getProperty('menu.profile','1');
            boolean showAll=false;
            boolean showEdu=true;
            boolean showSocial=true;
            boolean showLogout=true;
            //boolean showLogin=true;
            boolean showConsult=true;
            boolean showMonitor=true;
            boolean showPrivacy=true;

            if( ! menuProfile){
               showAll=true
            } else if(menuProfile.contains('default')){
                showAll=true
            } else if(menuProfile.contains('none')) {
                showAll=false
            }  else {
                showAll=false
            }

            home = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('default.home.label','Home'), '/jsf/home.xhtml', PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK, codedLabel, root), root)//TYPE_ITEM_NODE_HOME

            //
            String menuImport= ConfigurationService.getInstance().getProperty('import.ehr','1');
            if(menuImport == '0'){

            } else {  //
                dashBoardMonitorPhrImports = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.import_health_data','Import Health Data'), '/jsf/monitor_interop.xhtml', PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK, codedLabel, root), root)
                //'/jsf/iframe_social_community_links.xhtml': 'Community Links'
            }
        
            sectionMonitoring = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.monitoring','Monitoring'),'', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, root), root)

            sectionPatientInformation = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.patientinfo','Patient Information'), '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, root), root)

            //sectionObservations = new DefaultTreeNode(new DocumentReference('Health Observations', '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, sectionPatientInformation), sectionPatientInformation)

            //sectionMeds = new DefaultTreeNode(new DocumentReference('Medications','', PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,sectionObservations), sectionObservations)
            
            if(menuProfile == '0'){

            } else {
                sectionProfile = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.profile.label','Profile'), '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, sectionPatientInformation), sectionPatientInformation)
            }
            //sectionMedicalDocs  = new DefaultTreeNode(new DocumentReference('Imported Medical Data','', PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,sectionPatientInformation), sectionPatientInformation)
             //Information & People

            //Put health topics as (0) top level or nested  under Information
            if(menuProfile == '0'){
                sectionEdu = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.education.health.label','Health Topics'), '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, root), root)
                //Remove Contacts in case of privacy issues
            } else {
                sectionGroupInfoPeople = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.info_and_people','Information & People'), '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, root), root)

                sectionEdu = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.education.health.label','Health Topics'), '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, sectionGroupInfoPeople), sectionGroupInfoPeople)
                //contactInfo.contacts.menu.label
                sectionContacts = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('contactInfo.contacts.menu.label','Contacts'), '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, sectionGroupInfoPeople), sectionGroupInfoPeople)
                //contact info of healthcare persons,other
                Map mapContacts = ['/jsf/profile_allcontacts_mgt.xhtml?typecontact=healthcare_provider': reportTool.getLabel('contactInfo.personal.label','My Contacts')]

                addMenuItems(mapContacts, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionContacts, codedLabel)

            }

            Map mapEdu = [
                        '/jsf/iframe_education_info.xhtml': reportTool.getLabel('menu.education.basic.info.label','Basic Information'),
                        '/jsf/iframe_education_habits.xhtml':  reportTool.getLabel('menu.education.newhabits.label','New Habits'),
                        '/jsf/iframe_education_precautions.xhtml':  reportTool.getLabel('menu.education.precautions','Precautions'),
                        '/jsf/iframe_education_warnings.xhtml':  reportTool.getLabel('menu.education.warningsigns','Warning Signs'),
                        '/jsf/iframe_education_decisionaids.xhtml':  reportTool.getLabel('menu.Decision_Aids','Decision Aids'),
                        '/jsf/iframe_education_resources.xhtml': reportTool.getLabel('menu.links','Links'),
                        '/jsf/iframe_education_glossary.xhtml': reportTool.getLabel('menu.glossary','Glossary')

                ]

            if(menuProfile == '0'){
                mapEdu.put('/jsf/iframe_social_community_links.xhtml', reportTool.getLabel('menu.info_comunity_links','Community Links'))
            }

            addMenuItems(mapEdu, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionEdu, codedLabel)
            //  dashBoardMonitorPhrImports = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.info_comunity_links','Community Links'), '/jsf/iframe_social_community_links.xhtml', PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK, codedLabel, root), root)

            if(menuProfile == '0'){

            } else {
                sectionCommunity = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.info_comunity','Community'), '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, root), root)
                //Community links could go to Health Topics instead for menuProfile > 0
                Map mapCommunity = [
                        '/jsf/iframe_social_community_links.xhtml': reportTool.getLabel('menu.info_comunity_links','Community Links'),
                        '/jsf/iframe_social_forums.xhtml': reportTool.getLabel('menu.education.patientforums','Forums')
                        //'/jsf/iframe_social_blogs.xhtml': 'Blogs',
                        //'/jsf/iframe_social_bookmarks.xhtml': 'Community Bookmarks',
                        //'/jsf/iframe_social_tags.xhtml': 'Find by Keywords'
                ]
                addMenuItems(mapCommunity, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionCommunity, codedLabel)
            }



            Map healthObs = [
                    '/jsf/obs_bp_mgt.xhtml': reportTool.getLabel('obsBloodPressureA01.label','Blood Pressure'),
                    '/jsf/obs_bw_mgt.xhtml': reportTool.getLabel('obsBodyWeightBMW01.label','Body Weight'),
                    '/jsf/obs_problem_mgt.xhtml': reportTool.getLabel('menu.problems.label','Problems'),
                    '/jsf/obs_medication_mgt.xhtml': reportTool.getLabel('medicationSummary.label','Medications')
            ]

            //privacy.level
            String privacy= ConfigurationService.getInstance().getProperty('privacy.level','1');
            Map profile

            if(privacy == '0'){
                //remove personal contact info
                //activityItem.label
                profile = [
                        '/jsf/profile_padl_mgt.xhtml': reportTool.getLabel('menu.activitiesofdailyliving.label','Activities of Daily Living'),
                        '/jsf/obs_activity_mgt.xhtml': reportTool.getLabel('menu.activityItem.label','Physical Activities')]
            } else {
                //contactInfo.label
                profile = [
                        '/jsf/profile_contact_mgt.xhtml?typecontact=healthcare_user': reportTool.getLabel('contactInfo.personal.label','My Contact Info'),
                        '/jsf/profile_padl_mgt.xhtml': reportTool.getLabel('menu.activitiesofdailyliving.label','Activities of Daily Living'),
                        '/jsf/obs_activity_mgt.xhtml': reportTool.getLabel('menu.activityItem.label','Physical Activities')]
            }


            //simpler, remove extra
            addMenuItems(healthObs, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionPatientInformation, codedLabel)

            if(menuProfile == '0'){
                addMenuItems(profile, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionPatientInformation, codedLabel)
            } else {
                addMenuItems(profile, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionProfile, codedLabel)
            }
           // addMenuItems(healthObs, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionObservations, codedLabel)
            sectionActionPlan = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.actionplan.label','Action Plan'), '/jsf/action_schedule_dash.xhtml', PhrsConstants.TYPE_ITEM_LINK, codedLabel, sectionPatientInformation), sectionPatientInformation)

            Map monitoringMap = [
                    '/jsf/monitor_info_dash.xhtml': reportTool.getLabel('menu.reports_health','Health Reports'),
                    '/jsf/monitor_vitals.xhtml': reportTool.getLabel('overviewCurrentVitalSigns.vitalsign.label','Vital Signs')]
            addMenuItems(monitoringMap, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionMonitoring, codedLabel)

            //monitor_vitals.xhtml
            //'/jsf/riskfactor_mgt.xhtml': 'Risk Factors',
            if(menuProfile == '0'){
                sectionPrivacy = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.consent_editor','Consent Editor'), '/jsf/iframe_privacy_consent_editor.xhtml', PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK, codedLabel, root), root)

            } else{
                sectionPrivacy = new DefaultTreeNode(new DocumentReference(reportTool.getLabel('menu.consent_editor','Privacy & Admin'), '', PhrsConstants.TYPE_ITEM_NODE_HEADER, codedLabel, root), root)

                Map privacyMap = [
                        '/jsf/iframe_privacy_consent_editor.xhtml': reportTool.getLabel('menu.privacy','Consent Editor')]

                addMenuItems(privacyMap, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionPrivacy, codedLabel)
            }
        }

    }

   // public TreeNode getSectionOdls() {
   //     return sectionObservations
   // }
    /**
     * Add items to a tree node
     * @param map
     * @param iconType
     * @param phrsMenuLinkType
     * @param parentNode
     */
    public static void addMenuItems(Map theMap, String iconLinkType, String phrsMenuLinkType, TreeNode parentNode, boolean codedLabel) {

        theMap.entrySet().each { entry ->
            def temp = new DefaultTreeNode(iconLinkType, new DocumentReference(
                    entry.value, entry.key, phrsMenuLinkType, codedLabel, parentNode), parentNode)
            //temp.setType(defaultNodeType)
        }

    }


    public void displaySelectedSingle(ActionEvent event) {
        if (selectedNode != null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, 'Selected', selectedNode.getData().toString());

            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
    /**
     * UI invokes this when
     * Ajax event must be checked for time out....
     * @param event
     */
    public void onNodeSelect(NodeSelectEvent event) {
        if(!UserSessionService.loggedIn()){
           LOGGER.debug('Session time out , user not logged in redirect')
           // redirectTimeOut();
        }
        TreeNode selectedNode
        selectedNode = event.getTreeNode();

        if (selectedNode) {

            def data = selectedNode.getData();
            if (data && data instanceof DocumentReference) {
                DocumentReference ref = (DocumentReference) data

                if (ref && ref.type) {
                    if (ref.type == PhrsConstants.TYPE_ITEM_LINK ||
                            ref.type == PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK) {
                        __selectedNode = selectedNode
                        if (ref.fileName) {
                            String context = '/' + FacesContext.getCurrentInstance().getExternalContext().getContextName()
                            if (!ref.fileName.startsWith('/')) context = context + '/'
                            //println('context+ref.fileName='+context+ref.fileName)
                            this.redirect(context + ref.fileName)
                        }

                    }
                }
            }
        }

    }

    public void onNodeExpand(NodeExpandEvent event) {

        //selectedNode = event.getTreeNode()
    }

    public void nodeExpandListener(ActionEvent event) {

        //selectedNode = event.getComponent()
    }

    public String getStyleClass() {
        // http://stackoverflow.com/questions/5576418/how-to-highlight-a-primefaces-tree-node-from-backing-bean
        return 'highlight-node';
    }

    public TreeNode getSelectedNode() {

        if (__selectedNode) __selectedNode.setExpanded(true)
        return __selectedNode
    }

    public void setSelectedNode(TreeNode selectedNode) {

        __selectedNode = selectedNode
    }

    /**
     * Find the node based on the resourceUri tag
     * @param requestNodeId is set in the request as 'requestnode= the document reference resource uri'
     * @param node
     * @return
     */
    public TreeNode findNode(String requestNodeId, TreeNode node) {
        TreeNode found=null

        if (node && requestNodeId) {
            def data = node.getData();
            if (data && data instanceof DocumentReference) {
                DocumentReference ref = (DocumentReference) data
                String tag = ref.getResourceUri()
                if (tag && (tag == requestNodeId)) {
                    found = node
                    //println('FOUND tag='+tag)
                    return found
                } //else{

            }
            List nodes = node.getChildren();
            if (nodes) {
                for (TreeNode aNode: nodes) {
                    found = findNode(requestNodeId, aNode)
                    if (found) return found
                }
            }
        }

        return found

    }


}

