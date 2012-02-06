package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
import javax.faces.context.FacesContext

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileMedicalContactInfo
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileUserContactInfo
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@ManagedBean(name="procon")
@RequestScoped
public class ProfileContactInfoBean extends FaceBaseBean  {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProfileContactInfoBean.class);

	String contactType
	boolean pixUpdateInteropActorRegistry=true
	boolean pixRevalidatePixId=true

	public ProfileContactInfoBean() {
		super();//required!!

		//initial setup. Load actual object later
		domainClazz = ProfileUserContactInfo.class
		setSelected(domainClazz.newInstance())

		initVocabularies(domainClazz,getLanguage())
		/*
		 map.put('TAG_ROLES_MEDICAL_PROFESSIONAL', 	getTerms(PhrsConstants.TAG_ROLES_MEDICAL_PROFESSIONAL,language))
		 //TAG_ROLES_NON_MEDICAL_USER
		 map.put('TAG_ROLES_NON_MEDICAL_USER', 	getTerms(PhrsConstants.TAG_ROLES_NON_MEDICAL_USER,language))
		 */

		//contactType=PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_PROVIDER
		//contactType=PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER

		setup()

		if(contactType == PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER){
			if(vocabMap.containsKey('TAG_ROLES_MEDICAL_PROFESSIONAL') && vocabMap.containsKey('TAG_ROLES_NON_MEDICAL_USER'))
				vocabMap.put('TAG_ROLES_MEDICAL_PROFESSIONAL',this.vocabMap.get('TAG_ROLES_NON_MEDICAL_USER'))
		}

	}
	private void loadContactType(){
		if(!contactType){
			String temp =doTypeContact()

			switch(temp){

				case PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER:
					contactType =temp
					domainClazz = ProfileUserContactInfo.class
					selected = new ProfileUserContactInfo()
					setAllowCreate(false);
					setAllowDelete(false);
					break

				case PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_PROVIDER:
					contactType =temp
					domainClazz = ProfileMedicalContactInfo.class
					selected = new ProfileMedicalContactInfo()
					setAllowCreate(true);
					setAllowDelete(false);
					break

				default:
					println('ERROR Contact info typecontact='+temp ? temp : 'null')

					contactType=PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER
					domainClazz = ProfileUserContactInfo.class
					selected = new ProfileUserContactInfo()
					break
			}

			if(contactType) println('constructor contactType set to = '+temp)
		}
	}
	private void setup(){

		try {
			loadModelMain()
			if(!contactType) loadContactType()
			if(contactType == PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER){
				if(getModelMain()==null) internalModelList = []
				if(getModelMain().isEmpty()){
					if(userService){
						//add one record
						ProfileUserContactInfo info= userService.getResourceSingle(ProfileUserContactInfo, true)
						//info.setLastName('?')
						//info.setFirstName('???')

						getModelMain().add(info)
					}
				}
			}

		} catch (Exception e){
			println('ProfileContactInfoBean loadModelMain Exception '+e)
		}


	}
	public boolean getContactTypeMedical(){
		return contactType == PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER ? false : true
	}
	public boolean isContactTypeMedical(){
		return contactType == PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER ? false : true
	}
	public String findTypeContact(){
		return findRequestParam('typecontact')
	}

	public String doTypeContact(){
		Map paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()

		String temp = paramMap.containsKey('typecontact') ? paramMap.get('typecontact') : null

		if(temp) {
			println('typecontact parm found in paramMap! = '+temp)
		} else {
			temp = paramMap.containsKey('infotype') ? paramMap.get('infotype') : null
			if(temp) println('typecontact as infotype parm found! = '+temp)
		}
		/*
		 if(!temp && requestMap && requestMap.containsKey('typecontact') ){
		 if(requestMap.containsKey('typecontact'))
		 temp =requestMap.get('typecontact')
		 else
		 temp = null
		 }
		 */		
		//println('request map contactType='+temp)
		/*if(temp){
		 if(( temp == PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER) ||
		 (temp == PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_PROVIDER)){
		 //contactType = temp
		 } else {
		 temp = null// PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_PROVIDER
		 }
		 }*/


		return temp
	}

	@Override
	public void setPermittedActions(){
		super.setPermittedActions();

		setAllowDelete(false);
		//this is determined too late!
		//TODO Issue: fired by super contructor before known in domain controller!
		if(!contactType) loadContactType()
		if(contactType == PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER){
			setAllowCreate(false);
			setAllowDelete(false);
		} else {
			setAllowCreate(true);
			setAllowDelete(false);
		}

		setModify(AuthorizationService.MODIFY_YES)

	}

	/**
	 * Called for a new form, local changes. Also for testing
	 * Under risks, there are no new risks that a user can add, we provide all known risks and the user should update
	 */

	@Override
	public void modifyNewResource(){
		super.modifyNewResource();
		if(contactType == PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER){
			if(selected) selected.healthcareRole = PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER

		} else {

		}
		//Patient
	}


	@Override
	public void storeModifyFirst(){
		super.storeModifyFirst()
		try{
			if(selected){
				//validate pix identifier, and update the status of the identifier
				ProfileContactInfo contactInfo = selected
				if(userService && contactInfo && contactInfo.pixIdentifier ){
					boolean validated= sendPixValidationMessage(contactInfo)
				}
			}
		} catch (Exception e){
			LOGGER.error(' '+e)
		}

	}
	
	public boolean sendPixValidationMessage(ProfileContactInfo contactInfo ){
		boolean validated=false
		try{
			if(contactInfo){
				//validate pix identifier

				if(userService && contactInfo.pixIdentifier ){
					InteropAccessService ias = new InteropAccessService(userService.getPhrsRepositoryClient())
					//validate and update the pixIdentifier object status. Audit message is sent
					validated= ias.sendPixValidationMessage(contactInfo, true);//updateInteropActorRegistry, revalidate

				}
			}
		} catch (Exception e){
			LOGGER.error(' '+e)
		}
	return validated
	}
}

