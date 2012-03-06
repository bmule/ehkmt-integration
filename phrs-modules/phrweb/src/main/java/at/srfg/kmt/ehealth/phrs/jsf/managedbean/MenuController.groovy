package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import java.util.Locale
import java.util.Map

import javax.faces.application.FacesMessage
import javax.faces.bean.ManagedBean
import javax.faces.bean.SessionScoped
import javax.faces.context.FacesContext
import javax.faces.event.ActionEvent
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.primefaces.event.NodeExpandEvent
import org.primefaces.event.NodeSelectEvent
import org.primefaces.model.DefaultTreeNode
import org.primefaces.model.TreeNode

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.jsf.support.DocumentReference
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil

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
@ManagedBean(name="menuBean")
@SessionScoped
public class MenuController extends FaceCommon{
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class.getName());

	//PhrsConstants.SESSION_MENU_CURRENT_NODE or this bean if SessionScoped


    String defaultNodeType='default'
	TreeNode root,home,sectionPatientInformation, sectionGroupInfoPeople,sectionPrivacy
    TreeNode dashboard,dashBoardMonitorPhrImports
	TreeNode sectionObservations,sectionMeds,sectionActionPlan, sectionProfile,sectionMonitoring,sectionContacts,sectionMedicalDocs
	TreeNode sectionEdu,sectionCommunity
	TreeNode test

	String iconLinkType='default'//'document'
	String iconChartType='default'

	DocumentReference selectedDocument;

	//TODO From selectedDocument, get tree node to open
	//create listener so that menu node stays open after new page
	//Issue: parentNode reference into each Doc reference?
	TreeNode __selectedNode

	TreeNode[] selectedNodes;

	TreeNode selectedRequestNode

	public MenuController() {

		    init(this.getLocale())

		
		//previous if using request or view scope
		//String requestNodeTag=UserSessionService.getRequestParameter(PhrsConstants.MENU_CONTROL_REQUEST_PARAMETER_SELECTED_NODE)
		//String requestNodeTag=UserSessionService.getSessionAttribute(PhrsConstants.MENU_CONTROL_REQUEST_PARAMETER_SELECTED_NODE)

	}

	//menu.activityItem.label contactInfo.personal.label
	/**
	 * 
	 * @param locale
	 */
	public void init(Locale locale) {
		//DefaultTreeNode.DEFAULT_TYPE, Folder

		boolean codedLabel=false

		root = new DefaultTreeNode(new DocumentReference("Main Menu","root", PhrsConstants.TYPE_ITEM_NODE_ROOT,codedLabel,null).setRoot(true), null)
		root.setExpanded(true) //address any UI bug

        if( ! UserSessionService.getSystemStatus()) { //storage problem
            home = new DefaultTreeNode(new DocumentReference("Error","/jsf/home.xhtml", PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK,codedLabel,root), root)//TYPE_ITEM_NODE_HOME
            WebUtil.addFacesMessageSeverityWarn("Status","System failed to start")
            LOGGER.debug("system failed to start msg to user")
        } else if( ! UserSessionService.loggedIn()){
            LOGGER.debug("user not logged in")
            //home = new DefaultTreeNode(new DocumentReference("Home","/jsf/home.xhtml", PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK,codedLabel,root), root)//TYPE_ITEM_NODE_HOME

        } else if(UserSessionService.sessionUserHasMedicalRole()){
           LOGGER.debug("Show medical role sidebar - user has medical role")
            home = new DefaultTreeNode(new DocumentReference("Home","/jsf/home.xhtml", PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK,codedLabel,root), root)//TYPE_ITEM_NODE_HOME
            //consultation reports
            sectionMonitoring   = new DefaultTreeNode(new DocumentReference("Monitoring","", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,root), root)
            Map monitoringMap=[
                    '/jsf/monitor_info_dash.xhtml':'Health Reports']
            addMenuItems(monitoringMap, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionMonitoring,codedLabel)

        }  else {

            home = new DefaultTreeNode(new DocumentReference("Home","/jsf/home.xhtml", PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK,codedLabel,root), root)//TYPE_ITEM_NODE_HOME

            dashBoardMonitorPhrImports   = new DefaultTreeNode(new DocumentReference("Import Health Data","/jsf/monitor_interop.xhtml", PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK,codedLabel,root), root)

            sectionMonitoring   = new DefaultTreeNode(new DocumentReference("Monitoring","", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,root), root)

            sectionPatientInformation = new DefaultTreeNode(new DocumentReference("Patient Information","", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,root), root)

            sectionObservations = new DefaultTreeNode(new DocumentReference("Health Observations","", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,sectionPatientInformation), sectionPatientInformation)

            //sectionMeds = new DefaultTreeNode(new DocumentReference("Medications","", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,sectionObservations), sectionObservations)

            sectionActionPlan   = new DefaultTreeNode(new DocumentReference("Action Plan","/jsf/action_schedule_dash.xhtml", PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK,codedLabel,sectionPatientInformation), sectionPatientInformation)

            sectionProfile  	= new DefaultTreeNode(new DocumentReference("Profile",			"", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,sectionPatientInformation), sectionPatientInformation)

            //sectionMedicalDocs  = new DefaultTreeNode(new DocumentReference("Imported Medical Data","", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,sectionPatientInformation), sectionPatientInformation)

            sectionGroupInfoPeople = new DefaultTreeNode(new DocumentReference("Information & People","", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,root), root)

            sectionEdu 			= new DefaultTreeNode(new DocumentReference("Health Topics","", 	PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,sectionGroupInfoPeople), sectionGroupInfoPeople)

            sectionCommunity 	=  new DefaultTreeNode(new DocumentReference("Community",		"", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,root), root)

            sectionContacts 	= new DefaultTreeNode(new DocumentReference("Contacts","", 			PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,sectionGroupInfoPeople), sectionGroupInfoPeople)

            sectionPrivacy      = new DefaultTreeNode(new DocumentReference("Privacy & Admin",				"", PhrsConstants.TYPE_ITEM_NODE_HEADER,codedLabel,root), root)

            Map healthObs=[
                        '/jsf/obs_bp_mgt.xhtml':'Blood Pressure',
                        '/jsf/obs_bw_mgt.xhtml':'Body Weight',
                        '/jsf/obs_problem_mgt.xhtml':'Problems',
                        '/jsf/obs_medication_mgt.xhtml':'Medications'
                    ]
            addMenuItems(healthObs, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionObservations,codedLabel)
            /* were children of sectionMeds
            Map healthMedsObs=[
                        '/jsf/obs_medication_mgt.xhtml'
                    ]

            Map healthMedsObs=[
                        '/jsf/obs_medication_mgt.xhtml?view=active':'Active',
                        '/jsf/obs_medication_mgt.xhtml?view=inactive':'Inactive',
                        '/jsf/obs_medication_mgt.xhtml?view=history':'History'
                    ]
            addMenuItems(healthMedsObs, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionMeds,codedLabel)

             */
            //	'obs_medications_iframe.xhtml':'Medications'
            //action_schedule_mgt_module.xhtml
            //action_schedule_dash.xhtml
            /*
             Map actions=['action_schedule_dash.xhtml':'Action Plan']
             addMenuItems(actions, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionActionPlan,codedLabel)
             */
            Map monitoringMap=[
                        '/jsf/monitor_info_dash.xhtml':'Health Reports',
                        '/jsf/monitor_vitals.xhtml':'Vital Signs']
            addMenuItems(monitoringMap, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionMonitoring,codedLabel)

            //monitor_vitals.xhtml
            Map profile=[
                        '/jsf/profile_contact_mgt.xhtml?typecontact=healthcare_user':'My Contact Info',
                        '/jsf/riskfactor_mgt.xhtml':'Risk Factors',
                        '/jsf/profile_padl_mgt.xhtml':'Activities of Daily Living',
                        '/jsf/obs_activity_mgt.xhtml':'Physical Activities']
            //'profile_contact_info_iframe.xhtml?typecontact=healthcare_user':'My Contact Info',

            addMenuItems(profile, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionProfile,codedLabel)

            //contact info of healthcare persons,other
            Map mapContacts=['/jsf/profile_allcontacts_mgt.xhtml?typecontact=healthcare_provider':'My Contacts']


            addMenuItems(mapContacts, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionContacts,codedLabel)


            Map mapEdu =[
                        '/jsf/iframe_education_info.xhtml'			:'Basic Information',
                        '/jsf/iframe_education_habits.xhtml'		:'New Habits',
                        '/jsf/iframe_education_precautions.xhtml'	:'Precautions',
                        '/jsf/iframe_education_warnings.xhtml'		:'Warning Signs',
                        '/jsf/iframe_education_decisionaids.xhtml'	:'Decision Aids',
                        '/jsf/iframe_education_resources.xhtml'		:'Links',
                        '/jsf/iframe_education_glossary.xhtml'		:'Glossary'

                    ]
            //		'iframe_education_pages.xhtml'			:'',
            addMenuItems(mapEdu, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionEdu,codedLabel)

            Map mapCommunity =[
                        '/jsf/iframe_social_community_links.xhtml'	:'Community Links',
                        '/jsf/iframe_social_forums.xhtml'			:'Forums',
                        '/jsf/iframe_social_blogs.xhtml'			:'Blogs',
                        '/jsf/iframe_social_bookmarks.xhtml'		:'Community Bookmarks',
                        '/jsf/iframe_social_tags.xhtml'				:'Find by Keywords'
                    ]
            //		'iframe_social_pages.xhtml'				:'Wiki',
            addMenuItems(mapCommunity, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionCommunity,codedLabel)

            /*
             Map medDocs=['#':'Overview']
             addMenuItems(medDocs, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionMedicalDocs,codedLabel)
             */


            Map privacyMap=[
                        '/jsf/iframe_privacy_consent_editor.xhtml':'Consent Editor']

            addMenuItems(privacyMap, iconLinkType, PhrsConstants.TYPE_ITEM_LINK, sectionPrivacy,codedLabel)

        }
		//add test options
//		if(ConfigurationService.isAppModeTest()){
//			test   = new DefaultTreeNode(new DocumentReference("____","/jsf/test1.xhtml",
//				PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK,codedLabel,root), root)
//			//option load,etc
//		}


	}
	public TreeNode getSectionOdls(){
		return sectionObservations
	}
	/**
	 * Add items to a tree node
	 * @param map
	 * @param iconType
	 * @param phrsMenuLinkType
	 * @param parentNode
	 */
	public static void addMenuItems(Map theMap, String iconLinkType,String phrsMenuLinkType, TreeNode parentNode,boolean codedLabel){

		theMap.entrySet().each { entry ->
			def temp= new DefaultTreeNode(iconLinkType, new DocumentReference(
					entry.value, entry.key, phrsMenuLinkType,codedLabel,parentNode), parentNode)
			//temp.setType(defaultNodeType)
		}

	}


	public void displaySelectedSingle(ActionEvent event) {
		if(selectedNode != null) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", selectedNode.getData().toString());

			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	public void onNodeSelect(NodeSelectEvent event) {
		TreeNode selectedNode
		selectedNode = event.getTreeNode();

		if(selectedNode){

			def data = selectedNode.getData();
			if(data && data instanceof DocumentReference){
				DocumentReference ref = (DocumentReference)data

				if(ref && ref.type){
					if(ref.type == PhrsConstants.TYPE_ITEM_LINK ||
					ref.type == PhrsConstants.TYPE_ITEM_NODE_HEADER_LINK){
						__selectedNode=selectedNode
						if(ref.fileName) {
							String context='/'+FacesContext.getCurrentInstance().getExternalContext().getContextName()
							if( ! ref.fileName.startsWith('/')) context= context+'/'
							//println('context+ref.fileName='+context+ref.fileName)
							this.redirect(context+ref.fileName)
						}

					}
				}
			}
		}
		//println('onNodeSelect selectedTreeNode='+selectedNode)
		/*
		 if(selectedDocument && selectedDocument.fileName){
		 println('selectedDocument.fileName filename='+selectedDocument.fileName)
		 this.redirect(selectedDocument.fileName)
		 }
		 */
	}

	public void onNodeExpand(NodeExpandEvent event){
		//println('onNodeExpand selectedTreeNode='+selectedNode)
		//selectedNode = event.getTreeNode()
	}
	public void nodeExpandListener(ActionEvent event){
		//println('nodeExpandListener selectedTreeNode='+selectedNode)
		//selectedNode = event.getComponent()
	}

	public String getStyleClass() {
		// http://stackoverflow.com/questions/5576418/how-to-highlight-a-primefaces-tree-node-from-backing-bean
		return 'highlight-node';
	}

	public TreeNode getSelectedNode(){
		//println('getSelectedNode'+__selectedNode)
		if(__selectedNode) __selectedNode.setExpanded(true)
		return __selectedNode
	}
	public void setSelectedNode(TreeNode selectedNode){
		//println('setSelectedNode'+selectedNode)
		__selectedNode=selectedNode
	}

	/**
	* Find the node based on the resourceUri tag
	* @param requestNodeId is set in the request as 'requestnode= the document reference resource uri'
	* @param node
	* @return
	*/
   public TreeNode findNode(String requestNodeId,TreeNode node){
	   TreeNode found

	   if(node && requestNodeId){
		   def data = node.getData();
		   if(data && data instanceof DocumentReference){
			   DocumentReference ref = (DocumentReference)data
			   String tag=ref.getResourceUri()
			   if(tag && (tag == requestNodeId)){
				   found = node
				   //println('FOUND tag='+tag)
				   return found
			   } //else{
			   //println('------tag='+tag+' ref'+ref.getFileName()+' label='+ref.getLabel()+' type='+ref.getType())
			   //}

		   }
		   List nodes = node.getChildren();
		   if(nodes){
			   for(TreeNode aNode:nodes){
				   found = findNode(requestNodeId,aNode)
				   if(found) return found
			   }
		   }
	   }

	   return found

   }


 



}

