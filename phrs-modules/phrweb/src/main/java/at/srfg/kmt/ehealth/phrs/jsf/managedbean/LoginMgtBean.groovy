package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import java.io.Serializable

import javax.faces.bean.ManagedBean
import javax.faces.bean.SessionScoped
import javax.faces.context.FacesContext
import javax.faces.event.ActionEvent

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData
import at.srfg.kmt.ehealth.phrs.security.services.login.LoginServiceImpl
import at.srfg.kmt.ehealth.phrs.security.services.login.LoginService
import javax.servlet.http.HttpServletRequest
import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil
import javax.faces.application.FacesMessage
/**
 * 
 * Used primarily for logging out
 * Login and initial session initialization is handled by the OpenId related listener and service
 *  
 * Uses UserSessionService to manage session attributes because session
 * attributes can be set via servlets especially for the handling OpenId
 * authentication and User registration via the OpenId 
 */
@ManagedBean(name="loginBean")
@SessionScoped
public class LoginMgtBean extends FaceCommon implements Serializable{
	private final static Logger LOGGER = LoggerFactory.getLogger(LoginMgtBean.class);

    String username
    String loginType
	String password

	boolean loggedIn=false;

	//boolean enableOpenId=false


	public LoginMgtBean(){

		def theSession= FacesContext.getCurrentInstance().getExternalContext().getSession(true)


		/**
		 * Test setup, single user mode
		 */
		if(ConfigurationService.isAppModeSingleUserTest()){
			makeTestLogin()
		}
	}
/**
 * Test login when AppModeTest is true. Default ownerUri and user name
 * The system runs effectively in a single user mode. No login, no logout
 */
	private void makeTestLogin(){
		Map map = getSessionMap()
		map.put(PhrsConstants.SESSION_USER_PHR_OWNER_URI, PhrsConstants.USER_TEST_HEALTH_PROFILE_ID)
		map.put(PhrsConstants.SESSION_USER_LOGIN_ID, PhrsConstants.USER_TEST_HEALTH_PROFILE_ID)
		map.put(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME, PhrsConstants.USER_TEST_HEALTH_PROFILE_ID)

	}
	
	public Map getSessionMap(){
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
	}



	public void logout(ActionEvent actionEvent) {
		logout()

	}
	public void logout() {

		loggedIn=false

		// invalidate OpenId RelyingParty and Http session, must redirect
		// afterwards. Response OK, but this session scoped bean is ending
		//public static final String forwardRedirectIndexPage = "/index.xhtml";
		try {
			//don't logout the single test user phrtest
			if( ! ConfigurationService.isAppModeSingleUserTest()){
				UserSessionService.invalidateSession();
			}
			String contextName=FacesContext.getCurrentInstance().getExternalContext().getContextName()
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect('/'+contextName+'/index.xhtml?faces-redirect=true'); //?faces-redirect=true
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getRequestUserName(){
		String userName
		userName= UserSessionService.getRequestParameter('username')
		if(!userName) userName = UserSessionService.getRequestParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN)
		return userName
	}

	public boolean isloggedIn() {

		return UserSessionService.loggedIn();

	}

	public String getStatus() {

		if( UserSessionService.loggedIn()) return 'ok'
		else return 'no';
	}

	public PhrFederatedUser sessionPhrUser() {
		return UserSessionService.getSessionAttributePhrUser();
	}
	public String sessionOwnerUri() {

		return UserSessionService.getSessionAttributePhrId();
	}
	public String getOwnerUri() {
		return UserSessionService.getSessionAttributePhrId();
	}
	public String getPhrId() {
		return UserSessionService.getSessionAttributePhrId();
	}

	public String getUsername() {
        String greetName = UserSessionService.getSessionUserGreetName();
		String userName= greetName ? greetName : userAuthenticatedName()
		return userName ? userName : ''
	}
	//for long user ids, show last
	public String getShortname(){
		String shortname=getUsername()
		if(shortname){
			try{
				String[] temp = shortname.split('/')
				if(temp && temp.length >0){
					shortname=temp[temp.length-1]
				}
			} catch(Exception e){
                //
            }
		}
		shortname = ' ...'+HealthyUtils.lastChars(shortname,10)
		return shortname ?: ''

	}
	public String getRole() {

		String role= UserSessionService.getSessionAttributeRole()

		if(role && role.contains(PhrsConstants.AUTHORIZE_ROLE_CONSENT_MGR_PREFIX)){
            //remove prefix:, but TODO make ref to i18 and replace with '_'
			role=role.replace(':'+PhrsConstants.AUTHORIZE_ROLE_CONSENT_MGR_PREFIX, '')
		} else {
			role=''
		}
		return role // ?: ''
	}
	public boolean getRoleShow() {
		String role= UserSessionService.getSessionAttributeRole()
		if(role && role.contains(PhrsConstants.AUTHORIZE_ROLE_CONSENT_MGR_PREFIX)){
			return true
		}
		return false
	}
	
	public boolean getMedicalRole(){
		
		return ConfigurationService.getInstance().isMedicalCareRole(UserSessionService.getSessionAttributeRole())
	}
	
	public String userAuthenticatedName(){
		return UserSessionService.getSessionAttributeUserAuthenticatedName()
	}
	public String requestFilterOwner() {
		return UserSessionService.getRequestParameterFilterOwnerUri();
	}
	public String requestFilterProtocolId() {
		return UserSessionService.getRequestParameterFilterProtocolId();
	}
	public String sessionFilterProtocolId() {
		return UserSessionService.getSessionAttributeFilterProtocolId()
	}

	public void redirect(String uri) {

		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public boolean getTestMode() {
        return ConfigurationService.isAppModeTest() || ConfigurationService.isAppModeSingleUserTest();
    }
	public boolean testMode() {
		return ConfigurationService.isAppModeTest() || ConfigurationService.isAppModeSingleUserTest();
	}
    public void getLoadTestData(){
        LOGGER.error("web form got: getLoadTestData ")
        try {
          CoreTestData.addTestBasicHealthVitalsData(getOwnerUri())
        } catch (Exception e) {
            LOGGER.error("getLoadTestData failed",e)
        }
    }
    public void getLoadInterop(){
        LOGGER.error("web form got: getLoadInterop ")
        try {
            CoreTestData test= new CoreTestData()
            test.addTestMedications_2_forPortalTestForOwnerUri(getOwnerUri())
        } catch (Exception e) {
            LOGGER.error("getLoadInterop failed",e)
        }
    }

    public void loadTestData(){
        LOGGER.error("web form got: loadTestData ")
        try {
            CoreTestData.addTestBasicHealthVitalsData(getOwnerUri())
        } catch (Exception e) {
            LOGGER.error("loadTestData failed",e)
        }
    }
    public void loadInterop(){
        LOGGER.error("web form got: loadInterop ")
        try {
            CoreTestData test= new CoreTestData()
            test.addTestMedications_2_forPortalTestForOwnerUri(getOwnerUri())
        } catch (Exception e) {
            LOGGER.error("loadInterop failed",e)
        }
    }

    public void getProcessLogin(){
        processLogin()
    }

    public void processLogin(){
       LoginService ls= new LoginServiceImpl();

       try {
           if(ls.isLocalLogin(username,loginType)){
               LOGGER.debug("processLogin isLocalLogin: "+username+" username="+username)

               handleLocalLogin()
           } else {
               FacesContext context= FacesContext.getCurrentInstance()
               // request.contextPath}#{'/login'}"

               //pass to OpenID
               //String loginServletUrl="/login"
               String appReturnUrl=WebUtil.getUrl(context,"/openid");
               LOGGER.debug("processLogin OpenId will return locally to: "+appReturnUrl+" username="+username)

               //Discover endpoint OpenID
               String providerEndpointDiscovered=ls.createRedirect(username,appReturnUrl);
               LOGGER.debug("processLogin OpenId redirect to providerEndpointDiscovered: "+providerEndpointDiscovered+" , OpenId OP will return locally to openId servlet at: "+appReturnUrl+" username="+username)
               if(providerEndpointDiscovered){
                  redirect(providerEndpointDiscovered)
               }  else {
                   //TODO check session for
                   WebUtil.addFacesMessageSeverityError("Login Status", "Open ID login failed.");
                   // <p:growl id="loginMsgs" showDetail="true" />

               }

           }
       } catch (Exception e) {
           LOGGER.error("processLogin failed processLogin openid ");
       }

    }
    public void handleLocalLogin(){
        FacesContext context= FacesContext.getCurrentInstance()
        if(context){
            HttpServletRequest req =(HttpServletRequest)context.getExternalContext().getRequest();

            PhrFederatedUser pfu = UserSessionService.managePhrUserSessionLocalLoginScenario(
                    username, req)

            String userMessageCode=null
            if (pfu != null) {

                LOGGER.debug('success local login, redirect  user handleLocalLogin= '+username)
               //TODO  userMessageCode success to flash message

            } else {
                LOGGER.debug('error handleLocalLogin creating local user null');
                if (userMessageCode == null) {
                    userMessageCode = PhrsConstants.DEFAULT_ERROR_MSG_OPEN_ID
                }
                WebUtil.addFacesMessageSeverityError("Login Status", "Local login failed, use a login ID prefixed with phr e.g. phrtest, or loginId: nurse");
                //TODO userMessageCode error to flash message

            }
        } else {
            LOGGER.debug('error handleLocalLogin FacesContext null');
        }
        
    }





}

