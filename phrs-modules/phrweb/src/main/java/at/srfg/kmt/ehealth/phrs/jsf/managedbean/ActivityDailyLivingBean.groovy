package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import javax.annotation.PostConstruct
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
import javax.faces.event.ActionEvent

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileActivityDailyLiving
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@ManagedBean(name="padlBean")
@RequestScoped
public class ActivityDailyLivingBean extends FaceBaseBean  {
    private final static Logger LOGGER = LoggerFactory.getLogger(ActivityDailyLivingBean.class);

	
	public ActivityDailyLivingBean() {
		super();//required!!
		
		// setPermittedActions performed by super class

		domainClazz = ProfileActivityDailyLiving.class
		setSelected(domainClazz.newInstance())	
		initVocabularies(domainClazz,getLanguage())
		
		loadModelMain()

	}


	public ProfileActivityDailyLiving getSelectedItem(){

		return selected
	}
	/**
	 * Might this be called after the subclass's PostContruct or before ??
	 */
	//@PostConstruct
	//public void init(){
    //do here, because we do not trust the super class Postcontruct
	//}

	@Override
	public void loadModelMain(){
		if(getUserService()){
			internalModelList = getUserService().getResources(getDomainClazz());
			//this UI shows all possibilities from the vocabulary...including those objects the user actually saved.
			internalModelList = fillModelUnselectedData(internalModelList,getDomainClazz(), PhrsConstants.TAG_ACTIVITIES_OF_DAILY_LIVING)
		}
	}
	@Override
	public void setPermittedActions(){
		super.setPermittedActions();
		this.setAllowCreate(false);
		this.setAllowDelete(false);
		this.setModify(AuthorizationService.MODIFY_YES)
		//no need for row view button
		setAllowView(true)
	}

	/**
	 * Called for a new form, local changes. Also for testing
	 * Under risks, there are no new risks that a user can add, we provide all known risks and the user should update
	 */
	@Override
	public void modifyNewResource(){
		super.modifyNewResource();
			
	}



}
                    
