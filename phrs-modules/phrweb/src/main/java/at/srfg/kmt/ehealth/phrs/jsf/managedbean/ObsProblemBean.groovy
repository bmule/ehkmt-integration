package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped

import at.srfg.kmt.ehealth.phrs.model.baseform.ObsProblem
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService


@ManagedBean(name="odlprobBean")
@RequestScoped
public class ObsProblemBean extends FaceBaseBean  {
	
	
	public ObsProblemBean() {
		super();//required!!
		// setPermittedActions performed by super class
		domainClazz = ObsProblem.class
		setSelected(domainClazz.newInstance())

		initVocabularies(domainClazz,getLanguage())

		try {
			loadModelMain()

		} catch (Exception e){
			println('ObsActivityBean loadModelMain Exception '+e)
		}	
	}


	@Override
	public void setPermittedActions(){
		super.setPermittedActions();
		//this.setAllowCreate(false);
		this.setAllowDelete(false);
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
		if(selected){		
			//default_activeStatusFalse  default_activeStatusTrue
			selected.status='default_activeStatusTrue'
			//selected.setDisplayDate(new Date()) issue:then this sets date in begin date?	
		}

	}

	@Override
	public void storeModifyFirst(){
		super.storeModifyFirst()

		if(selected){

		}
	}
}

