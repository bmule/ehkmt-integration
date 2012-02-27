package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileMedicalContactInfo
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import javax.faces.bean.ManagedBean
import javax.faces.bean.RequestScoped
import javax.faces.context.FacesContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Manage  contacts of a user, but not the user.
 */
@ManagedBean(name="allcontacts")
@RequestScoped
public class ProfileAllContactsBean extends FaceBaseBean  {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProfileAllContactsBean.class);


    String contactType
    boolean pixUpdateInteropActorRegistry=true
    boolean pixRevalidatePixId=true

    public ProfileAllContactsBean() {
        super();//required!!

        //initial setup. Load actual object later
        domainClazz = ProfileMedicalContactInfo.class
        setSelected(domainClazz.newInstance())

        initVocabularies(domainClazz,getLanguage())

        contactType =PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_PROVIDER

        setAllowCreate(true);
        setAllowDelete(false);

        loadModelMain()

    }

    public boolean getContactTypeMedical(){
        return true
    }
    public boolean isContactTypeMedical(){
        return true
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


        return temp
    }

    @Override
    public void setPermittedActions(){
        super.setPermittedActions();
        setAllowCreate(true);
        setAllowDelete(false);
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
            contactType =PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_PROVIDER
        }

    }

}