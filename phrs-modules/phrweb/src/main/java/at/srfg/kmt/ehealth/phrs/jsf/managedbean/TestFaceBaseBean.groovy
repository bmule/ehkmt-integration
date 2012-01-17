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
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService
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
// TODO Observable lists and maps http://mrhaki.blogspot.com/2009/09/groovy-goodness-observable-map-and-list.html
// use for collectors addTo removeFrom
class TestFaceBaseBean implements Serializable{
	private final static Logger LOGGER = LoggerFactory.getLogger(TestFaceBaseBean.class);

	double rating=0d
	boolean msgDebug=true
	public final String OUTCOME_SUCCESS = 'success'
	public String uidThisView=null

	List internalModelList

	UserService userService
	AuthorizationService permit
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
	
	 boolean allowTest=true
	 //refactoring not complete
	 boolean allowView=true
	 boolean allowEdit=true
	 boolean allowDelete=true
	 boolean allowList=true
	 boolean allowViewChart=true
	 boolean allowCreate=true
	 
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

	public TestFaceBaseBean(){
		super()
		internalModelList=new ArrayList()
		if(!userService) userService = PhrsStoreClient.getInstance().getPhrsRepositoryClient().getUserService()
		/*
		initPermit()
		getPermit().setEditMode(false)
		modify='no'
		getPermit().makeModifyNo()
		*/

		if(uidThisView==null) uidThisView=UUID.randomUUID().toString()

		checkSession()

		initLanguage()


		//  setPermittedActions()

		UserSessionService.updateRequestToSessionParameters()


	}
	public void initPermit(){
		if(userService) permit= userService.getAuthorizationService()
		permit= UserService.getAuthorizationServiceDefault()
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
			//println('exception'+e)
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

			//importInteropMessages()
			internalModelList = getUserService().getResources(getDomainClazz());
		}
		if( !internalModelList) internalModelList = []

	}
	public List listModel(Class filterClazz){
		def model
		if(getUserService()){
			model = getUserService().getResources(filterClazz);
		}
		if( !model) model = []
		return model

	}
	/**
	 * 
	 * @param phrsClass
	 */
	public void importInteropMessages(String phrsClass){

	}
	/**
	 * filter by status e.g. complete, incomplete, or other local completion code
	 * @param status
	 * @param include
	 * @param results
	 * @return
	 */
	public  List filterResultsByStatus(String status, boolean include, List results){
		List list
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
		List list
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
			

		} catch (Exception e){
			println('Exception '+e)
		}
	}
	public void formCreate(){


		modify='yes'
		
		//getPermit().setEditMode(true)
		createInitResource()

	}
	public void formEdit(){
		try{

			modify='yes'
			

		} catch (Exception e){
			LOGGER.error('formEdit permitBean.makeModify',e)
		}
	}


	//groovy make the get/set model

	public void crudReadModel(){
		internalModelList = userService.getResources(domainClazz);

		//fillModelUnselectedData(model,domainClazz, PhrsConstants.TAG_RISK_FACTORS);
	}

	public void store(){
		//println('store() uidThisView='+uidThisView);
		//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Changes save action event ..override '));
		crudSave();
		loadModelMain()
		//flush form with new instance
		createInitResource()
	}
	public void getStore(){
		//println('getStore() uidThisView='+uidThisView);
		store();
	}
	/*
	 if(this.keys != null){
	 this.siteSettings.getRestClient().update(this.unitInstance, this.keys);
	 }
	 else{
	 this.keys = this.siteSettings.getRestClient().create(unitInstance);
	 this.unitInstance = this.siteSettings.getRestClient().retrieve(this.unit.getPersistenceClass(), this.keys);
	 }*/
	/**
	 * Save the selected resource and reload the model list for the UI
	 */
	public void crudSave(){
		//LOGGER.debug('crudSave uidThisView='+uidThisView);
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
		//refresh model?
		//return OUTCOME_SUCCESS
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
	/*
	 public void create(ActionEvent actionEvent){
	 println('edit formCreate')
	 if(msgDebug) FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'create mode '));
	 this.formCreate();
	 }*/
	/*
	 public void delete(ActionEvent actionEvent){
	 crudDelete();
	 }
	 public void create(ActionEvent actionEvent){
	 println('edit formCreate')
	 if(actionEvent) println('edit formCreate'+actionEvent.getClass())
	 //if(msgDebug) FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'create mode '));
	 this.formCreate();
	 }
	 public void view(ActionEvent actionEvent){
	 println('view ActionEvent')
	 //if(msgDebug) FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'View mode '));
	 this.formView();
	 }
	 public void edit(ActionEvent actionEvent){
	 println('edit ActionEvent')
	 //if(msgDebug) FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Edit mode '));
	 this.formEdit();
	 }
	 */
	/*
	 public void edit(ActionEvent actionEvent){
	 println('edit ActionEvent')
	 if(msgDebug) FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 'Info', 'Edit mode '));
	 this.formEdit();
	 }
	 */
	/**
	 * UI view action, invokes formView() before showing the UI dialog
	 */
	public void view(){
		//println('view() uidThisView='+uidThisView);
		this.formView();
	}
	public void view(ActionEvent event){
		//println('view(ActionEvent) uidThisView='+uidThisView);

		this.formView();
	}
	/**
	 * UI create action, invokes formCreate() to prepare a new item before showing the UI dialog
	 * The UI has not yet stored the resource
	 */
	public void create(){
		//println('create() uidThisView='+uidThisView);
		this.formCreate();
	}
	/**
	 * UI edit action, invokes formEdit() to prepare this bean before showing the UI dialog
	 */
	public void edit(){
		//println('edit() uidThisView='+uidThisView);
		this.formEdit();
	}
	public void edit(ActionEvent event){
		//println('edit(ActionEvent) uidThisView='+uidThisView);
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
			//println('isNew()=false uidThisView='+uidThisView);
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
	/*
	 binding does not work -serialization, but not a good idea coz it copies to session
	 //binding="#{xxxBean.mainform}"
	 public boolean clearForm2(){
	 boolean clearedForm=false
	 try{
	 if (mainForm != null) {
	 mainForm.getChildren().clear();
	 clearedForm=true
	 }
	 } catch (Exception e){
	 println('exception'+e)
	 }
	 return clearedForm
	 }
	 public boolean clearForm(){
	 boolean clearedForm=false
	 try{
	 if (mainform != null) {
	 mainform.getChildren().clear();
	 clearedForm=true
	 }
	 } catch (Exception e){
	 println('exception'+e)
	 }
	 return clearedForm
	 }*/
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
				//println('exception'+e)
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
			//println('form clear exception'+e)
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
		//println('rowEdit')
		this.rowEditListener(event)
	}
	public void onEdit(org.primefaces.event.RowEditEvent event){
		//println('onEdit')
		this.rowEditListener(event)
	}
	public void rowEditListener(org.primefaces.event.RowEditEvent event){
		//println('rowEditListener')

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

				} else {
					//println('onEditRow getObject = NULL');
				}
			} else {
				//println('onEditRow event = NULL');
			}
			listRequestParams()
		} catch (Exception e) {
			//println('rowEditListener ERROR = ' + e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 'ERROR ', e.toString()));
		}

	}
	public void listRequestParams(){
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			Map reqParams = fc.getExternalContext().getRequestParameterMap();
			
		} catch (Exception e) {
			
		}
	}

	public String  findRequestParam(String name){
		Map paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()

		String temp = paramMap.containsKey(name) ? paramMap.get(name) : null

		if(temp) {
			//println(name+' param found in paramMap! = '+temp)
		} else {
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

}
