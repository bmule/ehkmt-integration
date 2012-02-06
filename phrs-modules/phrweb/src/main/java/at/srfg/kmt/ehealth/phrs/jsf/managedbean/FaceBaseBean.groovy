package at.srfg.kmt.ehealth.phrs.jsf.managedbean

import java.io.Serializable
import java.util.ArrayList
import java.util.Collection
import java.util.List
import java.util.Locale
import java.util.Map
import java.util.UUID

import javax.faces.application.Application
import javax.faces.application.FacesMessage
import javax.faces.application.ViewHandler
import javax.faces.component.UIComponent
import javax.faces.component.UIViewRoot
import javax.faces.context.FacesContext
import javax.faces.event.ActionEvent

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.jsf.utils.JsfFormUtil
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import at.srfg.kmt.ehealth.phrs.presentation.services.ModelLabelValue
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyEnhancer
import at.srfg.kmt.ehealth.phrs.presentation.services.VocabularyService
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropProcessor
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
// TODO Observable lists and maps http://mrhaki.blogspot.com/2009/09/groovy-goodness-observable-map-and-list.html
// use for collectors addTo removeFrom
class FaceBaseBean implements Serializable{
	private final static Logger LOGGER = LoggerFactory.getLogger(FaceBaseBean.class);

	double rating=0d
	boolean msgDebug=true
	public final String OUTCOME_SUCCESS = 'success'
	public String uidThisView=null

	List internalModelList

	UserService userService
	AuthorizationService permit
	ConfigurationService config
	Locale locale
	String language
	//refactoring not complete
	String modify


	/**
	 * Issue whether JSF accepts object, the child objects can
	 * declase the class and get/set the selected item
	 */
	def selected
	//must be def and not any Class
	Class domainClazz

	Map<String,Collection<ModelLabelValue>> internalVocabMap
	/*
	 boolean allowTest=true
	 //refactoring not complete
	 boolean allowView=true
	 boolean allowEdit=true
	 boolean allowDelete=true
	 boolean allowList=true
	 boolean allowViewChart=true
	 boolean allowCreate=true
	 */
	// for row editing, whether to reload the model
	boolean rowEditReloadModel=true

	/*
	 @ManagedProperty(value="#{message}")
	 private MessageBean messageBean;
	 //must povide the setter method
	 public void setMessageBean(MessageBean messageBean) {
	 this.messageBean = messageBean;
	 }
	 */

	public FaceBaseBean(){
		super()
		config = ConfigurationService.getInstance();
		
		internalModelList=new ArrayList()
		if(!userService) userService = PhrsStoreClient.getInstance().getPhrsRepositoryClient().getUserService()
		initPermit()
		getPermit().setEditMode(false)
		modify='no'
		getPermit().makeModifyNo()
		//internal_editMode = false;
		//
		if(uidThisView==null) uidThisView=UUID.randomUUID().toString()

		checkSession()

		initLanguage()

		// domainClazz will be null need subclass initVocabularies(domainClazz,language)

		setPermittedActions()

		UserSessionService.updateRequestToSessionParameters()

	}
	public void initPermit(){
		if(userService) permit= userService.getAuthorizationService()
		permit= UserService.getAuthorizationServiceDefault()
	}
	public String getSessionUserLoginId(){
		String value = UserSessionService.getSessionAttributeUserLoginId()
		
		return value
	}

	public String getSessionUserOwnerUri(){
		String value = UserSessionService.getSessionAttributePhrId()
		
		return value
	}
	public String getCurrentUserRole(){

		String value = UserSessionService.getSessionAttributeRole()

		return value
	}
	/**
	* Based on ownerUris' for current user and target user
	* The permission logic should lookup up alternative identifiers when needed e.g. protocol ID or Open ID
	* Current User
	* @param targetUser
	* @param resource
	* @param action
	* @return
	*/
   public boolean permitUserOnPhrId(String targetUser, String resourceCode, String action){
	   boolean result=false
	   
	   //String currentOwnerUri = getCurrentUserOwnerUri()
	   if(permit){
		   result = permit.permitAccessOnPhrId( targetUser,  resourceCode, action)
	   }
	   return result
   }
   
   public boolean permitUserOnProtocolId(String targetUser, String resourceCode, String action){
	   boolean result=false
	   
	   //String currentOwnerUri = getCurrentUserOwnerUri()
	   if(permit){
		   result = permit.permitAccessOnProtocolId( targetUser,  resourceCode, action)
	   }
	   return result
   }
   

	/**
	 * 
	 * @return
	 */
	public String findFilterOwnerUri(){
		return UserSessionService.getRequestParameterFilterOwnerUri();

	}

	/*
	 public void refreshRequestMap(){
	 try {
	 if(FacesContext.getCurrentInstance()
	 && FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()){
	 //.getRequestMap()) of application :(
	 if(!this.getRequestMap()) requestMap=[:]
	 requestMap.putAll(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap());
	 }
	 } catch (Exception e){
	 println('exception'+e)
	 }
	 if( ! requestMap) requestMap = [:]
	 }*/

	public Map retrieveParameters(){
		try {
			if(FacesContext.getCurrentInstance()
			&& FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()){
				return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
			}
		} catch (Exception e){
			LOGGER.error('',e)
		}
		return  [:]
	}


	public void  setPermittedActions(){
	}


	/**
	 * Subclass should perform initialization in Postcontruct method
	 * @param filterClass
	 * @param language
	 */
	protected  void initVocabularies(Class filterClass,String language){

		if( ! internalVocabMap) {
			internalVocabMap = VocabularyEnhancer.localVocabularies(filterClass,language)
		}
	}
	/**
	 * Subclass should perform initialization in Postcontruct method
	 */
	protected synchronized void initVocabularies(){

		if( ! internalVocabMap) {
			internalVocabMap = VocabularyEnhancer.localVocabularies(getDomainClazz(),getLanguage())
		}
	}

	public void initLanguage(){

		locale= locale ? locale : FacesContext.getCurrentInstance().getViewRoot().getLocale();

		println('default req locale='+locale)
		language=locale ? locale.getLanguage() :'en'

	}

	public Map<String,Collection<ModelLabelValue>> getVocabMap(){
		if( ! internalVocabMap) {
			initVocabularies()
		}
		return internalVocabMap
	}

	public void setVocabMap(Map<String,Collection<ModelLabelValue>> map){
		internalVocabMap = map
	}
	protected boolean checkSession(){
		return UserSessionService.hasSession();
	}
	/*
	 * 
	 protected void setDomainClazz(Class clazz, boolean created selected item){
	 domainClazz = clazz.getClass()
	 //setSelected(domainClazz.);
	 println('setDomainClazz'+domainClazz.class)
	 setSelected(domainClazz.newInstance())
	 }*/
	/**
	 * This method does not perform a query,  the model must be loaded in either the Postcontruct or constructor
	 * of the subclass
	 * a query, just a getter. If internalModelList is null, it will be assign a new ArrayList() so that UIs are not broken
	 * 
	 * @return
	 */
	public List getModelMain(){
		//if(internalModelList) println('internal_model size '+internalModelList.size())
		//if( ! internalModelList) {
		//	loadModelMain()
		//}
		return internalModelList
	}
	public void setModelMain(List theModelList){
		//if(internalModelList) println('internal_model size '+internalModelList.size())
		this.internalModelList = theModelList
	}

	/**
	 * This is the default load for the model. 
	 */
	public void loadModelMain(){
		if(getUserService()){

			// Import new interop messages as new domain objects

			//do import manually by subclasses not automatically 
			internalModelList = getUserService().getResources(getDomainClazz());
		}
		if( !internalModelList) internalModelList = []

	}
	public List listModel(Class filterClazz){
		def model =null
		if(getUserService()){
			model = getUserService().getResources(filterClazz);
		}
		if( !model) model = []
		return model

	}
	/**
	 * 
	 * @param phrsClass - Interop classes e.g. Constants.PHRS_MEDICATION_CLASS
	 */
	public void importInteropMessages(String phrsClass){
		try{
			if(selected && userService){		
                            InteropProcessor ip= userService.getPhrsStoreClient().getInteropProcessor()
                            List list = ip.importNewMessages(selected.ownerUri, phrsClass);
                                        
                            //validate and update the pci.pixIdentifier object status
			} else {
                            
                            LOGGER.error('importInteropMessages selected = null, no ownerUri ');
                        }
		} catch (Exception e){
			LOGGER.error(' Controller importInteropMessages owner='+selected.ownerUri+' resourceType='+phrsClass+e)
		}
	}
	/**
	 * filter by status e.g. complete, incomplete, or other local completion code
	 * @param status
	 * @param include
	 * @param results
	 * @return
	 */
	public  List filterResultsByStatus(String status, boolean include, List results){
		List list =null
		if(getUserService()){
			list = getUserService().filterResultsByStatus(status,include,results);
		}
		if(!list) list = []
		return list

	}
	public List fillModelUnselectedData(Collection modelList, Class theDomainClazz, String tag){
		return VocabularyService.extendModelWithUnselectedData(
		tag, modelList,  domainClazz);
	}

	public List crudReadHistory(){
		return crudReadHistory(domainClazz);
	}

	public List crudReadHistory( Class entityClazz){
		List list  =null
		if(getUserService()) list = getUserService().crudReadHistory(entityClazz);
		if(!list) list =[]
		return list
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


	/**
	 * @deprecated
	 * @param tag
	 * @return
	 */
	public Collection<ModelLabelValue> getTerms(String tag){
		return VocabularyService.getTermsByTag(tag, language)
	}

	public Collection<String> getTermIds(String tag){
		return VocabularyService.getTermValuesByTag(tag, language)
	}
	/*
	 public boolean getEditMode() {
	 //println('isEditMode='+internal_editMode)
	 return internalEditMode;
	 }
	 public boolean isEditMode() {
	 //println('isEditMode='+internal_editMode)
	 return internalEditMode;
	 }
	 public void setEditMode(boolean editMode) {
	 println('setEditMode='+editMode)
	 this.internalEditMode = editMode
	 }*/

	// ******* Lookup helpers, eventually create converter, but here we get locale. TODO We must use tomcat 7 to make parameter method calls from JSF or add EL 2.2 to Tomcat 6??

	public String resetAction(){
		//println('resetAction start')
		this.selected = domainClazz.newInstance()
		//println('resetAction finish')
	}
	public String closeAction(javax.faces.event.AjaxBehaviorEvent event){
		//println('closeAction start')
		this.selected = domainClazz.newInstance()
		//println('closeAction finish')
	}
	/*
	 closeAction(javax.faces.event.AjaxBehaviorEvent)
	 */
	public void resetAction(ActionEvent event){
		//println('resetAction start ActionEvent')
		this.selected = domainClazz.newInstance()
		//println('resetAction finish ActionEvent')
	}
	protected void createInitResource(){
		this.selected = domainClazz.newInstance()
		//this.selected = domainClazz.n
		//Constructor<domainClazz> temp = new Constructor<domainClazz>()

		//((BasePhrsModel)selected).atest='test note 2'
		modifyInitBaseResource()
		//init when subclass overrides modifyInitDomainResource
		modifyNewResource()
		//return res
	}
	/**
	 * subclass overrides modifyInitDomainResource to initialized
	 * 
	 * 	BasePhrsModel temp= (BasePhrsModel)getSelected();
	 *  or cast  to domain class
	 * @param res
	 * @return
	 */
	public void modifyNewResource(){

	}
	private void modifyInitBaseResource(){

	}
	//only for setting editMode, an error occurs when trying to use setPropertyActionListenr to  set editMode to true e.g. to change value
	//ok for setting editMode=false 'view'... only for Groovy objects????
	public void formView(){
		try{
			modify='no'
			getPermit().makeModifyNo()

		} catch (Exception e){
			println('Exception '+e)
		}
	}
	public void formCreate(){


		modify='yes'
		getPermit().makeModifyYes()
		//getPermit().setEditMode(true)
		createInitResource()

	}

	public void formEdit(){
		try{

			modify='yes'
			getPermit().makeModifyYes()

		} catch (Exception e){
			LOGGER.error('formEdit permitBean.makeModify',e)
		}
	}

	public void crudReadModel(){
		internalModelList = userService.getResources(domainClazz);

		//fillModelUnselectedData(model,domainClazz, PhrsConstants.TAG_RISK_FACTORS);
	}

	public void store(){

		//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Changes save action event ..override '));
		crudSave();
		loadModelMain()
		//flush form with new instance
		createInitResource()
	}
	public void getStore(){

		store();
	}

	/**
	 * Save the selected resource and reload the model list for the UI
	 */
	public void crudSave(){

		if(selected){

			if(userService) {

				storeModifyFirst()
				userService.crudSaveResource(selected)
				loadModelMain()
			} else{
				LOGGER.error('crudSave userService NULL')
			}
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Saved'));
		} else {
			LOGGER.error('crudSave selected resource NULL')

			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'save error: selected NULL'));
		}

	}
	public void storeModifyFirst(){

	}
	/**
	 * Delete the selected resource and reload the model list for the UI
	 */
	public void crudDelete(){
		//println('delete ')
		if(selected!=null){
			if(internalModelList && userService) {
				userService.crudDeleteResource(selected)
				loadModelMain()
				//internalModelList.remove(selected);
			}
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Deleted'));
		}

	}

	/**
	 * UI view action, invokes formView() before showing the UI dialog
	 */
	public void view(){

		this.formView();
	}
	public void view(ActionEvent event){


		this.formView();
	}
	/**
	 * UI create action, invokes formCreate() to prepare a new item before showing the UI dialog
	 * The UI has not yet stored the resource
	 */
	public void create(){

		this.formCreate();
	}
	/**
	 * UI edit action, invokes formEdit() to prepare this bean before showing the UI dialog
	 */
	public void edit(){

		this.formEdit();
	}
	public void edit(ActionEvent event){

		this.formEdit();
	}
	/**
	 * UI delete action, invokes crudDelete()
	 */
	public void delete(){
		//getPermit().setEditMode(false)
		crudDelete()
	}
	/**
	 * test action
	 */
	public void printme(){
		System.out.println('printme() uidThisView='+uidThisView);
	}
	/**
	 * Is new resource
	 * @return
	 */
	public boolean isNew(){

		if(selected && selected.resourceUri){

			return false
		}

		return true
	}


	public Collection<ModelLabelValue> getRiskfactorTreatmentsList(){

		String theCode = selected && selected.code ? selected.code : ''
		return VocabularyEnhancer.getRiskfactorTreatmentsList(theCode,vocabMap)
	}
	public void testme(){
		System.out.println('testme writes this message, no action uidThisView='+uidThisView);

	}

	public String outcome(){
		System.out.println('outcome');
		return 'success';
	}
	public String getOutcome(){
		System.out.println('getOutcome');
		return 'success';
	}
	/**
	 * Use immediate=true on command button and actionListener="#{...refresh}" kill whole view
	 * https://cwiki.apache.org/confluence/display/MYFACES/Clear+Input+Components
	 * "... causes the current View tree to be discarded and a fresh one created. The new components of course then have no submitted values,
	 and so fetch their displayed values via their value-bindings." http://www.google.at/url?sa=t&rct=j&q=jsf++parentComponent.getChildren%28%29.clear%28%29%3B&source=web&cd=1&ved=0CCQQFjAA&url=http%3A%2F%2Fjava-server-faces.blogspot.com%2F2006%2F04%2Fcancel-buttons-easy-way.html&ei=c3XHTrWpHMX1sgaFo5zuBg&usg=AFQjCNFW1nBTJT-SXjMOG8qI0kY5pfjeHQ
	 http://www.logikdev.com/2011/06/13/delete-the-components-holding-unwanted-state/
	 * @param renderRepsonse
	 */

	/**
	 * 
	 * @param renderRepsonse
	 * @param event
	 * 
	 * reset button should have reset prefix and form Id 'FormId'
	 */
	public UIComponent findFormComponent(UIComponent component){
		if(component){

			boolean found=false
			UIComponent theComponent = component
			String className
			try{
				int count=0
				while(!found){
					count++
					if(theComponent){
						className = theComponent.getClass().getSimpleName()
						if(className && className.toLowerCase().contains('form')){
							found=true
							return theComponent
						}
					} else {
						found=true
						continue
					}
					if(count > 20){
						found=true
						continue
					}
					theComponent = theComponent.getParent()
				}
			} catch (Exception e){
				LOGGER.error('error find form in JSF tree', e)

			}
		}
		return null
	}
	public UIComponent clearForm(UIComponent child){
		boolean clearedForm=false
		UIComponent form= findFormComponent(child)

		if(form){
			JsfFormUtil.clearSubmittedValues(form)
			//List list = form.getChildren()
			//if(list) list.clear()
			clearedForm=true
		}
		return form

	}


	//do nothing, but close, no refresh of form
	public void close(ActionEvent event){
		//println('close')
	}

	public void refresh(boolean renderRepsonse,ActionEvent event) {
		//println('refresh event renderRepsonse='+renderRepsonse)
		boolean clearedForm=false
		//clearedForm=clearForm()
		// this wipes it out... need to create the dialog view again!

		UIComponent form
		try{
			if(event){
				UIComponent comp=event.getComponent()
				//TODO don't clear if "close!"
				form = clearForm(comp)
				if(form){
					/*FacesContext context = FacesContext.getCurrentInstance();
					 Application application = context.getApplication();
					 ViewHandler viewHandler = application.getViewHandler();
					 UIViewRoot viewRoot = viewHandler.createView(context, form.getId())
					 context.setViewRoot(viewRoot);
					 if(renderRepsonse) {
					 context.renderResponse(); //Optional entire view issue
					 }*/	
					clearedForm=true
				}
			}

		} catch (Exception e){
			clearedForm=false
			LOGGER.error('form clear exception', e)

		}

		//this wipes everything, restarts the view mode reinit construtor etc
		try{
			if( ! clearedForm){
				FacesContext context = FacesContext.getCurrentInstance();
				Application application = context.getApplication();
				ViewHandler viewHandler = application.getViewHandler();
				UIViewRoot viewRoot
				//if(!form){
				viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());
				//} else{
				//viewHandler.createView(context, form.getId())
				//}
				context.setViewRoot(viewRoot);
				if(renderRepsonse) {
					context.renderResponse(); //Optional entire view issue
				}
			}
		} catch (Exception e){
			LOGGER.error('render view exception', e)

		}


	}
	public void refresh(ActionEvent event) {
		refresh(true,event)

	}
	public void refreshNoRender(ActionEvent event) {
		refresh(false,event)

	}
	public void refreshParent(ActionEvent event ){
		//https://cwiki.apache.org/confluence/display/MYFACES/Clear+Input+Components
		//println('event.getComponent().getParent().getId() '+event.getComponent().getParent().getId())
		//println('event.getComponent().getParent().getId() ')
		// parentComponent.getChildren().clear();

	}
	/**
	 * Set the domain instance and create a new instance
	 * @param clazz
	 * @param createNewInstance
	 */
	public void setDomainClazz(Class clazz,boolean createNewInstance){
		this.domainClazz = clazz
		makeSelected(true)
	}
	/**
	 * Create a new instance using the domainClazz, easier to use from Java subclasses 
	 * @param createNewInstance
	 */
	public void makeSelected(boolean createNewInstance){
		if(createNewInstance)  createInitResource()
		//if(createNewInstance) selected = domainClazz.newInstance()
	}

	/*
	 http://stackoverflow.com/questions/6942535/jsf-2-with-primefaces-incell-editing-no-updated-object-in-roweditevent-paramet
	 */
	public void rowEdit(org.primefaces.event.RowEditEvent event){

		this.rowEditListener(event)
	}
	public void onEdit(org.primefaces.event.RowEditEvent event){

		this.rowEditListener(event)
	}
	public void rowEditListener(org.primefaces.event.RowEditEvent event){


		try {
			if(event){
				if(event.getObject()){

					def rowItem =  event.getObject(); // selected object from event
					selected= rowItem
					//store  selected
					store()   // Write into the database

					if(rowEditReloadModel) {
						loadModelMain()
					}
					//remove reference, we are done with it
					selected=null

				}
			}
			//listRequestParams()
		} catch (Exception e) {

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 'ERROR ', e.toString()));
		}

	}
	public void listRequestParams(){
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			Map reqParams = fc.getExternalContext().getRequestParameterMap();
			
		} catch (Exception e) {
			//
		}
	}

	public String  findRequestParam(String name){
		Map paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()

		String temp = paramMap.containsKey(name) ? paramMap.get(name) : null

		if( ! temp) {
			temp = paramMap.containsKey(name) ? paramMap.get(name) : null

		}

		return temp
	}


	/**
	 *  Forward, but might have issue with relative CSS or JS and browser bookmarking because browser displays original URL
	 * @param uri
	 */
	public void dispatch(String uri){
		FacesContext.getCurrentInstance().getExternalContext().dispatch(uri);
	}
	/**
	 * Redirect
	 * @param uri
	 */
	public void redirect(String uri){
		//implicit call to FacesContext.getCurrentInstance().responseComplete(). If use response.sendRedirect then must call ..responseComplete afterwards
		//FacesContext.getCurrentInstance().getExternalContext().redirect(uri);// "article.jsp?article_id=" + articleId);
		UserSessionService.redirect(uri)
	}

	/**
	 * Helper functions
	 * @return
	 */
	public boolean isAllowTest() {
		return getPermit().isAllowTest();
	}
	public boolean getAllowTest() {
		return getPermit().isAllowTest();
	}
	public void setAllowTest(boolean allowTest) {
		getPermit().setAllowTest(allowTest)
	}
	// ...
	public boolean isAllowView() {
		return getPermit().isAllowView()
	}
	public boolean getAllowView() {
		return getPermit().isAllowView()
	}
	public void setAllowView(boolean allowView) {
		getPermit().setAllowView(allowView)
	}
	// ....
	public boolean getAllowEdit() {
		return getPermit().isAllowEdit()
	}
	public boolean isAllowEdit() {
		return getPermit().isAllowEdit()
	}

	public void setAllowEdit(boolean allowEdit) {
		getPermit().setAllowEdit(allowEdit)
	}
	// ...
	public boolean isAllowDelete() {
		return getPermit().isAllowDelete()
	}
	public boolean getAllowDelete() {
		return getPermit().isAllowDelete()
	}
	public void setAllowDelete(boolean allowDelete) {
		getPermit().setAllowDelete(allowDelete)
	}
	// ...
	public boolean isAllowList() {
		return getPermit().isAllowList()
	}
	public boolean getAllowList() {
		return getPermit().isAllowList()
	}
	public void setAllowList(boolean allowList) {
		getPermit().setAllowList(allowList)
	}
	// ...
	public boolean getAllowViewChart() {
		return getPermit().isAllowViewChart()
	}
	public boolean isAllowViewChart() {
		return getPermit().isAllowViewChart()
	}

	public void setAllowViewChart(boolean allowViewChart) {
		getPermit().setAllowViewChart(allowViewChart)
	}
	// <!-- 	allowCreate modify  -->

	public boolean isAllowCreate() {	
		return getPermit().isAllowCreate();
	}
	public boolean getAllowCreate() {
		return getPermit().isAllowCreate()
	}
	public void setAllowCreate(boolean allowCreate) {
		getPermit().setAllowCreate(allowCreate)
	}
	// ...
	boolean allowThis=false


}
