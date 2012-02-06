package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import javax.annotation.PostConstruct
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped

import at.srfg.kmt.ehealth.phrs.model.baseform.ActionPlanEvent
import at.srfg.kmt.ehealth.phrs.presentation.utils.EventDuration
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ManagedBean(name="actionmgtBean")
@RequestScoped
public class ActionActivityMgtBean extends FaceBaseBean  {
    private final static Logger LOGGER = LoggerFactory.getLogger(ActionActivityMgtBean.class);

	
	/**
	 * Sets the domainClass from the subclass
	 * Creates a new "selected" emtpy resource
	 * TODO handle this in PostConstruct but depends on whether we trust this. Some issues with older Tomcats and other containers
	 * if they run the super class Postconstruct method at all...
	 */
	public ActionActivityMgtBean() {
		super();//required!!
		
		// setPermittedActions performed by super class
		
		domainClazz = ActionPlanEvent.class
		setSelected(domainClazz.newInstance())
		
		initVocabularies(domainClazz,getLanguage())
		
		try {
			loadModelMain()

		} catch (Exception e){
			println('ActionActivityMgtBean loadModelMain Exception '+e)
		}
				
		setModify(AuthorizationService.MODIFY_YES)
		
	}
	public String durationTimeString(){
		String temp=''
		if(selected){
			EventDuration ed = new EventDuration(selected.beginDate,selected.endDate)
			temp = ed.toString()
			
		}
		return temp
	}
	
	/**
	 * Might this be called after the subclass's PostContruct or before ??
	 */
	@PostConstruct
	public void init(){	

		//System.out.println('Postcontruct  subclass'+this.getClass().toString()+' uidThisView='+uidThisView+' locale='+FacesContext.getCurrentInstance().getViewRoot().getLocale());

	}
	
	@Override
	public void setPermittedActions(){
		super.setPermittedActions();
		setModify(AuthorizationService.MODIFY_YES)
		
		setAllowCreate(true)
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
		
		//BasePhrsModel temp= (ProfileRisk)getSelected();
		//action_isActiveStatusTrue action_isActiveStatusFalse 
		selected.status='action_isActiveStatusTrue'
		
		selected.beginDate=new Date() //not display date
		
	}
	public void setEvent(ActionPlanEvent event){
		selected = event
	}
	
	public ActionPlanEvent getEvent(){
		return selected
	}


}
                    
