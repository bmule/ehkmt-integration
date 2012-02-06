package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped

import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@ManagedBean(name="proids")
@RequestScoped
public class ProfileUserIdentifiers extends FaceBaseBean  {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProfileUserIdentifiers.class);

	public ProfileUserIdentifiers() {
		super();
		// setPermittedActions performed by super class
		domainClazz = ProfileUserIdentifiers.class
		setSelected(domainClazz.newInstance())

		initVocabularies(domainClazz,getLanguage())

		try {
			loadModelMain()
		} catch (Exception e){
			println('ProfileUserIdentifiers loadModelMain Exception '+e)
		}
	}

	@Override
	public void setPermittedActions(){
		super.setPermittedActions();
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
			//selected.status='xxxx'		
		}
	}

	@Override
	public void storeModifyFirst(){
		super.storeModifyFirst()
		if(selected){

		}
	}
}
