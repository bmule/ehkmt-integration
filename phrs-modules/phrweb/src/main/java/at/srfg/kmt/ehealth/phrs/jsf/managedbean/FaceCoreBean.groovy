package at.srfg.kmt.ehealth.phrs.jsf.managedbean

// collector observable lists or maps http://mrhaki.blogspot.com/2009/09/groovy-goodness-observable-map-and-list.html

import java.util.Collection

import javax.faces.application.Application
import javax.faces.application.ViewHandler
import javax.faces.component.UIViewRoot
import javax.faces.context.FacesContext
import javax.faces.event.ActionEvent

import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient
import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyEnhancer
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyService
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService

/**
 * 
 * Includes UserServices and vocabulary helper methods to support child beans
 * Vocabulary lookup for labels can check vocabulary services or I18 files.
 * 
 * This class is the base and no JSF scope is asigned, the children classes will providing scoping
 * 
 * Some variables are prefied with internal* to avoid some cases of cycling in groovy. Also
 * it appears JSF can call getters multiple times, so some properties should be loaded or init.
 * The getModelMain - need to modify however the JSF pages needs to reload new values and it uses that getter
 * to reload... 
 * 
 *
 */
class FaceCoreBean implements Serializable{

	boolean msgDebug=true
	public final String OUTCOME_SUCCESS = 'success'
	public String uidThisView=null

	UserService userService
	Locale locale
	String language
	String modify
	AuthorizationService permit

    List internalModelList

	Map<String,Collection<ModelLabelValue>> internalVocabMap

	/*
	 @ManagedProperty(value='#{message}')
	 private MessageBean messageBean;
	 //must povide the setter method
	 public void setMessageBean(MessageBean messageBean) {
	 this.messageBean = messageBean;
	 }
	 */

	public FaceCoreBean(){

		if(!userService) userService = PhrsStoreClient.getInstance().getPhrsRepositoryClient().getUserService()

		//permit = new AuthorizationService()
		initPermit()
		getPermit().setEditMode(false)
		modify='no'
		getPermit().makeModifyNo()
		checkSession()

		if(uidThisView==null) uidThisView=UUID.randomUUID().toString();

		initLanguage()
	}
	public void initPermit(){
		if(userService) permit= userService.getAuthorizationService()
		permit= UserService.getAuthorizationServiceDefault()
	}
	public boolean editMode() {
		return getPermit().isEditMode()
	}
	public String getModify(){
		return getPermit().getModify()
	}
	public void makeModifyYes(){
		getPermit().makeModifyYes()
	}
	public void makeModifyNo(){
		getPermit().makeModifyNo()
	}
	/**
	 * Subclass should perform initialization in Postcontruct method
	 * @param filterClass
	 * @param language
	 */
	protected  void initVocabularies(Class filterClass){

		if( ! internalVocabMap) {
			internalVocabMap = VocabularyEnhancer.localVocabularies(filterClass,getLanguage())
		}
	}

	public void initLanguage(){

		locale= locale ? locale : FacesContext.getCurrentInstance().getViewRoot().getLocale();
		language=locale ? locale.getLanguage() :'en'
	}

	public Map<String,Collection<ModelLabelValue>> getVocabMap(Class filterClass){
		if( ! internalVocabMap) {
			initVocabularies(filterClass)
		}
		return internalVocabMap
	}

	public void setVocabMap(Map<String,Collection<ModelLabelValue>> map){
		internalVocabMap = map
	}
	protected boolean checkSession(){
		return UserSessionService.hasSession();
	}


	/**
	 * Should get language from locale
	 * @param String
	 * @return
	 */
	public String getTermLabel(String termId){
		return VocabularyService.getTermLabel(termId, language)
	}

	public Collection<String> getTermLabels(Collection termIds){

		return VocabularyService.getTermLabelsById(termIds, language)
	}


	public ModelLabelValue getTerm(String termId){
		ModelLabelValue lv = VocabularyService.getTerm(termId, language)
		return lv
	}


	public Collection<String> getTermIds(String tag){
		return VocabularyService.getTermValuesByTag(tag, language)
	}


	//groovy make the get/set model

	public void crudReadModel(Class domainClazz){
		internalModelList = userService.getResources(domainClazz);

		//fillModelUnselectedData(model,domainClazz, PhrsConstants.TAG_RISK_FACTORS);
	}

	public boolean store(def selected){

		//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Changes save action event ..override '));
		return crudSaveResource(selected);

	}


	/**
	 * Save the selected resource but does not*reload the model list for the UI
	 * returns true if saved. The UI model can be reloaded if desired later
	 */
	public boolean crudSaveResource(def selected){
		println('crudSaveResource uidThisView='+uidThisView);
		if(selected){
			println('crudSaveResource selected.code='+selected.code);
			if(userService) {
				println('crudSaveResource userService');

				userService.crudSaveResource(selected)
				return true
				//loadModelMain()
			} else println('crudSaveResource userService NULL');
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Saved'));

		} else {
			println('crudSaveResource selected NULL=');
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'save error: selected NULL'));
		}
		return false
	}
	/**
	 * Delete the selected resource and reload the model list for the UI
	 */
	public boolean crudDeleteResource(def selected){
		println('crudDeleteResource ')
		if(selected!=null){
			if(userService) {
				userService.crudDeleteResource(selected)
				return true
			}

			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Deleted'));
		}
		return false
	}

	/**
	 * might kill whole view
	 * @param renderRepsonse
	 */
	public void refresh(boolean renderRepsonse) {
		println('refresh')
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ViewHandler viewHandler = application.getViewHandler();
		UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());
		context.setViewRoot(viewRoot);
		if(renderRepsonse) context.renderResponse(); //Optional
		println('end')
	}
	public void refresh() {
		refresh(true)

	}
	public void refreshParent(ActionEvent event ){
		//https://cwiki.apache.org/confluence/display/MYFACES/Clear+Input+Components
		println('event.getComponent().getParent().getId() '+event.getComponent().getParent().getId())
		//println('event.getComponent().getParent().getId() ')
		// parentComponent.getChildren().clear();

	}

	/**
	 * Create a new instance using the domainClazz, easier to use from Java subclasses 
	 * @param createNewInstance
	 */
	public def createSelected(Class domainClazz){
		return  domainClazz ?  domainClazz.newInstance() : new Object();
	}



}
