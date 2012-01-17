package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import javax.annotation.PostConstruct
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped

import org.joda.time.DateTime
import org.primefaces.model.DefaultStreamedContent
import org.primefaces.model.StreamedContent

import sun.tools.tree.ThisExpression;

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.MonitorInfoItem
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService


@ManagedBean(name="monitorinfoBean")
@RequestScoped
public class MonitorInfoBean extends FaceBaseBean  {


	public MonitorInfoBean() {
		super();//required!!
		// setPermittedActions performed by super class
		domainClazz = MonitorInfoItem.class
		setSelected(MonitorInfoItem.newInstance())

		initVocabularies(domainClazz,getLanguage())
		try {
			loadModelMain()
		} catch (Exception e){
			println('ShareBean loadModelMain Exception '+e)
		}

	}


	// AUTHORIZE_RESOURCE_CODE_PHRS_UNKNOWN
	private void buildView(){

		List<PhrFederatedUser> phrUsers=null
		//Either get a smaller list of subjects using the consent editor
		//or get all users in the PHRS system. More can be offered for sharing

		phrUsers= this.userService.getResources(null, PhrFederatedUser.class);

		if( !phrUsers) phrUsers=[]
		internalModelList = []
		//which resources?
		List resourceCodes=config.getConsentSubjectCodes('phr')

		//resourceCodes.add(PhrsConstants.AUTHORIZE_RESOURCE_CODE_CONDITION)
		String action = PhrsConstants.AUTHORIZE_ACTION_CODE_READ
		//Sorting, leave room for higher priority records.
		int sortOrder=5
		
		for(PhrFederatedUser ph:phrUsers){
			sortOrder++
			//for each supported resourceCode, check READ permissions
			for(String resourceType:resourceCodes){
				try{
					boolean permitViewContent= permitUserOnPhrId(ph.getOwnerUri(), resourceType, action )

					boolean permitViewRow=false
					//check permit for current user, on phrUser with resource type
					//show row for testing
					if(config.isAppModeMonitorListAllUsers()){
						permitViewRow=true
						//permitViewContent=true
					}

					if(permitViewRow || permitViewContent){

						MonitorInfoItem item= new MonitorInfoItem();
						item.message=''
						permitViewContent= permitUserOnPhrId()

						item.ownerUri= ph.getOwnerUri()
						item.currentUserId = getSessionUserOwnerUri()
						item.currentUserRole = getCurrentUserRole()

						item.setAllowedViewContent(permitViewContent)
						item.setAllowedViewRow(permitViewRow)

						item.setResourceType(resourceType)

						ProfileContactInfo pci= userService.getResourceSingle(item.OwnerUri,ProfileContactInfo.class)

						if(pci){
							String name = pci.getLastName() + pci.getLastName() ? ',':'' +pci.getFirstName()	
							item.setName(name);
							String protocolId

							if( pci.getPixIdentifier() ){

								protocolId = pci.getPixIdentifier().getIdentifier()
								item.setProtocolId(protocolId)
							}

							if(!protocolId){
								item.message+='No Protocol ID found in Contact Information'
							}

							internalModelList.add(item)
						} else {
							//item.messageCode=null
							item.message+='No contact info or protocolId'
						}
						item.sortOrder=sortOrder
						if(UserSessionService.isSessionUser(ph.getOwnerUri()){
							//put name
							item.name='My Report. '+item.name
							item.sortOrder=1
						}

					}
				} catch(Exception e) {
					LOGGER.error("",e)
				}
			}
		}

	}
	// getUserName(ownerUri)   getLocalProtocolIdByOwnerUri(ownerUri)
	/*monitor_info_dash.xhtml
	 String currentUserId;
	 String currentUserOwnerUri;
	 String currentUserRole;
	 // boolean currentUserAllowedViewContent=false;
	 String ownerUri;
	 String protocolId;
	 String name;
	 String resourceType;// consent
	 // String resourceTypeLabelCode;//I18
	 // row and content display
	 boolean allowedViewRow = false;
	 // local permission result
	 boolean allowedViewContent = false;
	 // external tool has priority
	 boolean consentViewContent = false;
	 */

	/**
	 * Override default model, this model is not currently
	 * cached
	 */
	@Override
	public void loadModelMain(){

		if(getUserService()){

			// Import new interop messages as new domain objects

			//importInteropMessages()
			internalModelList = buildView()

			//getUserService().getResources(getDomainClazz());
		}
		if( !internalModelList) internalModelList = []

	}
	@Override
	public void setPermittedActions(){
		super.setPermittedActions();
		setAllowCreate(false);
		setAllowDelete(false);
		setModify(AuthorizationService.MODIFY_NO)
		setAllowView(true)

	}
	/**
	 * 
	 * request parameter resourcecode
	 * request parameter phrid
	 * 
	 * Only read action
	 * Get role from session user
	 * 
	 * @return
	 */
	public StreamedContent getDownLoadReport(){
		
		if(selected){
			MonitorInfoItem item = (MonitorInfoItem)selected
			reportValidateRequest(item.ownerUri,true,item.resourceType)
			
		}
		
	}
	@PostConstruct
	public void handleReportRequest(){
		//controller is invoked but not by ajax, check for these parameters and then it is a request
		String resourceType = UserSessionService.getRequestAttributeString('resourcecode')
		String targetUserId = UserSessionService.getRequestAttributeString('phrid')
		boolean hasPhrId=false
		if(targetUserId) hasPhrId = true
		
		if( !hasPhrId){
			targetUserId = UserSessionService.getRequestAttributeString('protocolid')
		}
		if(resourceType & targetUserId){
			reportValidateRequest(targetUserId,  hasPhrId,  resourceType)
		}
		//boolean hasPhrId = targetUserId ? true : false
	}
	protected StreamedContent reportValidateRequest(String targetUserId, boolean hasPhrId, String resourceType){
		boolean permitViewContent= false
		StreamedContent reportFile		
		
		//either one
		String idType
		
		if(hasPhrId) idType='phrid'	
		
		try {

			if(targetUserId && resourceType){				

				if(hasPhrId){
					permitViewContent = permitUserOnPhrId(targetUserId, resourceType, PhrsConstants.AUTHORIZE_ACTION_CODE_READ)

					LOGGER.debug(" request phrId="+targetUserId+" on resourceType="+resourceType+" permitted?")
				} else {

					permitViewContent = permitUserOnProtocolId(targetUserId, resourceType, PhrsConstants.AUTHORIZE_ACTION_CODE_READ)

					LOGGER.debug(" request phrId="+targetUserId+" resourceType="+resourceType+" permitted?")
				}
				reportFile= reportBuild(targetUserId, idType,resourceType, permitViewContent)
				if(permitViewContent){
				
					boolean req= permit.auditGrantRequest(targetUserId,  true,resourceType)
				}
			}else {

				LOGGER.debug(" request error null found : protocolId or phrId="+hasPhrId+" id="+targetUserId+" resourceType="+resourceType+" permitted? false")
			}
		} catch(Exception e){
			LOGGER.error(" targetUserId="+targetUserId+"idType="+" resourceType="+resourceType+" idType="+idType,e)
		}
		return  reportFile

	}

	protected StreamedContent reportBuild(String targetUserId, String idType, String resourceType, boolean permited){

		StreamedContent reportFile=null
		if(targetUserId && resourceType) {
			try {
				InputStream stream
				String downloadFilename
				if(permited){

					DateTime stamp=HealthyUtils.formatDate(new DateTime(), null);
					downloadFilename='monitor-phrinfo-'+stamp+'.pdf'


				} else {
					// Forward slash : in resource path
					downloadFilename='/monitor_info_intro.pdf'
				}

				if(stream==null){
					//get default file
					stream= this.getClass().getResourceAsStream(downloadFilename)
				}
				if(stream){
					reportFile = new DefaultStreamedContent(stream, 'application/pdf',downloadFilename)
					
				}
			} catch(Exception e){
				LOGGER.error(" targetUserId="+targetUserId+"idType="+" resourceType="+resourceType+" idType="+idType,e)
			}
		}
		return reportFile

	}

	/*
	 public FileDownloadController() { 
	 InputStream stream = this.getClass().getResourceAsStream("monitor_info_intro.pdf"); 
	 reportFile = new DefaultStreamedContent(stream, "application/pdf",
	 "downloaded_file.pdf");
	 }
	 protected StreamedContent getFile() { 
	 }*/
	/*
	 <dependency>
	 <groupId>net.sf.jasperreports</groupId>
	 <artifactId>jasperreports</artifactId>
	 <version>4.5.0</version>
	 </dependency>
	 4.5.0
	 4.1.3
	 */


	//	@Override
	//	public void modifyNewResource(){
	//		super.modifyNewResource();
	//		if(selected){
	//			//default_activeStatusFalse  default_activeStatusTrue
	//			//selected.status='default_activeStatusTrue'
	//			//selected.setDisplayDate(new Date()) issue:then this sets date in begin date?
	//		}
	//
	//	}
	//
	//	@Override
	//	public void storeModifyFirst(){
	//		super.storeModifyFirst()
	//
	//		if(selected){
	//
	//		}
	//	}
}