package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
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
        //changed ProfileUserContactInfo
        domainClazz = ProfileContactInfo.class
        setSelected(domainClazz.newInstance())

        contactType =PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER

        setAllowCreate(false);
        setAllowDelete(false);
        setModify(AuthorizationService.MODIFY_YES)
        initVocabularies(domainClazz,getLanguage())


        if(vocabMap.containsKey('TAG_ROLES_MEDICAL_PROFESSIONAL') && vocabMap.containsKey('TAG_ROLES_NON_MEDICAL_USER')) {
           vocabMap.put('TAG_ROLES_MEDICAL_PROFESSIONAL',this.vocabMap.get('TAG_ROLES_NON_MEDICAL_USER'))
        }

        loadModelMain()

    }
     @Override
     void  loadModelMain(){
        try {
            if(userService){
                internalModelList = []
                //add single record
                // ProfileUserContactInfo contactInfo = userService.getResourceSingle(ProfileUserContactInfo, true) //create if needed
                ProfileContactInfo contactInfo = (ProfileContactInfo)userService.getResourceSingle(ProfileContactInfo, true) //create if needed
                //only one, "selected"  can be referenced by the jsf page without using the list modelMain
                selected=contactInfo
                getModelMain().add(contactInfo)
            }
        } catch (Exception e){
            LOGGER.error('ProfileContactInfoBean loadModelMain Exception '+e)
        }
    }

        
            
    public boolean getContactTypeMedical(){
        return false
    }
    public boolean isContactTypeMedical(){
        return false
    }
    public String findTypeContact(){
        return findRequestParam('typecontact')
    }

//    public String doTypeContact(){
//        Map paramMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
//
//        String temp = paramMap.containsKey('typecontact') ? paramMap.get('typecontact') : null
//
//        if(temp) {
//            println('typecontact parm found in paramMap! = '+temp)
//        } else {
//            temp = paramMap.containsKey('infotype') ? paramMap.get('infotype') : null
//            if(temp) println('typecontact as infotype parm found! = '+temp)
//        }
//
//        return temp
//    }

    @Override
    public void setPermittedActions(){
        super.setPermittedActions();
        setAllowCreate(false)
        setAllowDelete(false)
        setModify(AuthorizationService.MODIFY_YES)
    }


    /**
     * Called for a new form, local changes. Also for testing
     * Under risks, there are no new risks that a user can add, we provide all known risks and the user should update
     */


    @Override
    public void modifyNewResource(){
        super.modifyNewResource();

            if(selected) {
                selected.healthcareRole = PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER
                //selected.pixQueryIdType=PixService.PIX_QUERY_TYPE_DEFAULT
            }

    }


}