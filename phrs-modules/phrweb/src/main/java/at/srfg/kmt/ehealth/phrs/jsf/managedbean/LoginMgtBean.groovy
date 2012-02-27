package at.srfg.kmt.ehealth.phrs.jsf.managedbean;


import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils
import at.srfg.kmt.ehealth.phrs.security.services.login.LoginService
import at.srfg.kmt.ehealth.phrs.security.services.login.LoginServiceImpl
import at.srfg.kmt.ehealth.phrs.security.services.login.LoginUtils
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData
import javax.faces.bean.ManagedBean
import javax.faces.bean.SessionScoped
import javax.faces.context.FacesContext
import javax.faces.event.ActionEvent
import javax.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * Used primarily for logging out
 * Login and initial session initialization is handled by the OpenId related listener and service
 *
 * Uses UserSessionService to manage session attributes because session
 * attributes can be set via servlets especially for the handling OpenId
 * authentication and User registration via the OpenId 
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginMgtBean extends FaceCommon implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoginMgtBean.class);

    String username
    String loginType = 'local.provider.1'    // local_1, openid_icardea_1, null or blank
    String password

    boolean loggedIn = false;

    public LoginMgtBean() {

        def theSession = FacesContext.getCurrentInstance().getExternalContext().getSession(true)

        /**
         * Test setup, single user mode
         */
        if (ConfigurationService.isAppModeSingleUserTest()) {
            makeTestLogin()
        }
    }
/**
 * Test login when AppModeTest is true. Default ownerUri and user name
 * The system runs effectively in a single user mode. No login, no logout
 */
    private void makeTestLogin() {
        PhrFederatedUser theUser=null
        try {
            //CoreTestData test = new CoreTestData()
            theUser= CoreTestData.createTestUserData()
        } catch (Exception e) {
            LOGGER.error('makeTestLogin loadInterop failed', e)
        }
        if(theUser){
            Map map = getSessionMap()
            map.put(PhrsConstants.SESSION_USER_PHR_OWNER_URI, theUser.getOwnerUri())
            map.put(PhrsConstants.SESSION_USER_LOGIN_ID, theUser.getUserId())
            map.put(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME, theUser.getOwnerUri())
        }



    }

    public Map getSessionMap() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
    }



    public void logout(ActionEvent actionEvent) {
        logout()

    }

    public void logout() {

        loggedIn = false

        // invalidate OpenId RelyingParty and Http session, must redirect
        // afterwards. Response OK, but this session scoped bean is ending
        //public static final String forwardRedirectIndexPage = "/index.xhtml";
        try {
            //don't logout the single test user phrtest
            if (!ConfigurationService.isAppModeSingleUserTest()) {
                UserSessionService.invalidateSession();
            }
            String contextName = FacesContext.getCurrentInstance().getExternalContext().getContextName()
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect('/' + contextName + '/index.xhtml?faces-redirect=true'); //?faces-redirect=true
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //?? old
    public String getRequestUserName() {
        String userName
        userName = UserSessionService.getRequestParameter('username')
        if (!userName) userName = UserSessionService.getRequestParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN)
        return userName
    }

    public boolean isloggedIn() {
        boolean loginStatus = UserSessionService.loggedIn();

        if( ! loginStatus){
            //boolean isVerified = UserSessionService.getSessionAttributeOpenIdIsVerified()
            String msg = UserSessionService.getSessionAttribute(PhrsConstants.ERROR_MSG_ATTR)
            if(msg){
                //TODO 
                String msgLabel= LoginUtils.getMessageLabel(msg,null);//TODO locale
                WebUtil.addFacesMessageSeverityError('Login Status: OpenId login failed, error (',msgLabel+')');
            }
        }

        return loginStatus
    }

    public String getStatus() {

        if (UserSessionService.loggedIn()) return 'ok'
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

//    public String getUsername() {
//        String greetName = UserSessionService.getSessionUserGreetName();
//        String userName = greetName ? greetName : userAuthenticatedName()
//        return userName ? userName : ''
//    }

    public String getGreetname() {
        String greetName = UserSessionService.getSessionUserGreetName();
        greetName = greetName ? greetName : userAuthenticatedName()
        greetName = greetName ? greetName : username
        return greetName
    }
    //for long user ids, show last
    public String getShortname() {
        String shortname = getUsername()
        if (shortname) {
            try {
                String[] temp = shortname.split('/')
                if (temp && temp.length > 0) {
                    shortname = temp[temp.length - 1]
                }
            } catch (Exception e) {
                //
            }
        }
        shortname =  HealthyUtils.lastChars(shortname, 10)
        return shortname ?: ''

    }

    public String getRole() {

        String role = UserSessionService.getSessionAttributeRole()

        if (role && role.contains(PhrsConstants.AUTHORIZE_ROLE_CONSENT_MGR_PREFIX)) {
            //remove prefix:, but TODO make ref to i18 and replace with '_'
            role = role.replace(':' + PhrsConstants.AUTHORIZE_ROLE_CONSENT_MGR_PREFIX, '')
        } else {
            role = ''
        }
        return role // ?: ''
    }

    public boolean getRoleShow() {
        String role = UserSessionService.getSessionAttributeRole()
        if (role && role.contains(PhrsConstants.AUTHORIZE_ROLE_CONSENT_MGR_PREFIX)) {
            return true
        }
        return false
    }

    public boolean getMedicalRole() {

        return ConfigurationService.getInstance().isMedicalCareRole(UserSessionService.getSessionAttributeRole())
    }

    public String userAuthenticatedName() {
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
    /**
     * UI
     * @return
     */
    public boolean getTestMode() {
        return ConfigurationService.isAppModeTest() || ConfigurationService.isAppModeSingleUserTest();
    }
    /**
     * UI
     * @return
     */
    public boolean testMode() {
        return ConfigurationService.isAppModeTest() || ConfigurationService.isAppModeSingleUserTest();
    }
    /**
     * UI button
     */
    public void getLoadTestData() {
        LOGGER.error('web form got: getLoadTestData ')
        try {
            CoreTestData.addTestBasicHealthVitalsData(getOwnerUri())
        } catch (Exception e) {
            LOGGER.error('getLoadTestData failed', e)
        }
    }
    /**
     * UI button
     */
    public void getLoadInterop() {
        LOGGER.error('web form got: getLoadInterop ')
        try {
            CoreTestData test = new CoreTestData()
            test.addTestMedications_2_forPortalTestForOwnerUri(getOwnerUri())
        } catch (Exception e) {
            LOGGER.error('getLoadInterop failed', e)
        }
    }
    /**
     * UI button
     */
    public void loadTestData() {
        LOGGER.error('web form got: loadTestData ')
        try {
            CoreTestData.addTestBasicHealthVitalsData(getOwnerUri())
        } catch (Exception e) {
            LOGGER.error('loadTestData failed', e)
        }
    }
    /*
    UI button
     */
    public void loadInterop() {
        LOGGER.error('web form got: loadInterop ')
        try {
            CoreTestData test = new CoreTestData()
            test.addTestMedications_2_forPortalTestForOwnerUri(getOwnerUri())
        } catch (Exception e) {
            LOGGER.error('loadInterop failed', e)
        }
    }
    /**
     * keep public in case UI uses this one
     */
    public void getProcessLogin() {
        processLogin()
    }

    private boolean detectedLocalLogin() {
        if (loginType && loginType.contains('local')) {
            return true
        }
        return false
    }

    private boolean detectedOpenIdLogin() {
        if (loginType && loginType.contains('openid')) {
            return true
        }
        return false
    }

    private void processLocalLogin() {

         //Check if username conforms to phr prefix or nurse
         //This is done to prevent mix up with the short name allowed in by an OpenId provider

        if (LoginUtils.isLocalLogin(username, loginType)) {
            LOGGER.debug('processLogin isLocalLogin:  loginType=' + loginType + ' username=' + username)

            FacesContext context = FacesContext.getCurrentInstance()
            if (context) {
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

                PhrFederatedUser pfu = UserSessionService.managePhrUserSessionLocalLoginScenario(
                        username, request)

                String userMessageCode = null
                if (pfu != null) {

                    LOGGER.debug('success local login, redirect  user handleLocalLogin= ' + username)
                    //TODO  userMessageCode success to flash message

                } else {
                    LOGGER.debug('error handleLocalLogin creating local user null');
                    if (userMessageCode == null) {
                        userMessageCode = PhrsConstants.DEFAULT_ERROR_MSG_OPEN_ID
                    }
                    WebUtil.addFacesMessageSeverityError('Login Status', 'Local login failed, use a login ID prefixed with phr e.g. phrtest, or loginId: nurse');
                    //TODO userMessageCode error to flash message

                }
            } else {
                LOGGER.debug('error handleLocalLogin FacesContext null');
            }

        } else {
            LOGGER.debug('processLogin localLogin failed, username does not conform to prefix phr* or nurse ' + username + ' username=' + username)
            WebUtil.addFacesMessageSeverityError('Login Status', 'User name missing');
        }
    }

    private void processOpenIdLogin() {
        LoginService loginService = new LoginServiceImpl();

        FacesContext context = FacesContext.getCurrentInstance()
        // request.contextPath}#{'/login'}'

        //pass to OpenID

        String appReturnUrl = LoginUtils.getOpenIdReturnToUrl(context);




        LOGGER.debug('processLogin OpenId will return locally to: ' + appReturnUrl + ' username=' + username+ '  LoginType: '+loginType)

        if (appReturnUrl) {
            //Discover endpoint OpenID  where UI form field loginType is the property key of Open ID server
            String providerEndpointDiscovered = loginService.createRedirect(username, appReturnUrl,loginType);
            LOGGER.debug('processLogin OpenId redirect to providerEndpointDiscovered: ' + providerEndpointDiscovered + ' , OpenId OP will return locally to openId servlet at: ' + appReturnUrl + ' username=' + username)
            if (providerEndpointDiscovered) {
                redirect(providerEndpointDiscovered)
            } else {

                WebUtil.addFacesMessageSeverityError('Login Status', 'Open ID login failed. No provider endpoint was discovered ');
                // <p:growl id='loginMsgs' showDetail='true' />
                LOGGER.debug('processOpenIdLogin Open ID login failed. No provider endpoint was discovered: '

                        + ' appReturnUrl: ' + appReturnUrl + ' providerEndpointDiscovered: ' + providerEndpointDiscovered
                        + ' username: ' + username + ' loginType' + loginType)
            }
        } else { //no appReturnUrl
            LOGGER.debug('processOpenIdLogin failed no appReturnUrl: appReturnUrl:' + appReturnUrl + ' for user ' + username)
            WebUtil.addFacesMessageSeverityError('Login Status', 'Open ID login failed. Error with appReturnUrl ' + appReturnUrl);
        }
    }
    /**
     * user interfaces invokes this public method
     */
    public void processLogin() {
        try {
            LOGGER.debug('processLogin START user: ' + username + ' loginType: '+loginType)
            if (username && loginType) {
                //ok
               if (detectedLocalLogin()) {
                    processLocalLogin()
                } else if (detectedOpenIdLogin()) {
                    processOpenIdLogin()
                } else {
                    WebUtil.addFacesMessageSeverityError('Login Status', 'Unknown login type: ' + loginType);
                    LOGGER.debug('processLogin failed unknown loginType: ' + loginType)
                }
            } else {
                //fail,
                WebUtil.addFacesMessageSeverityError('Login Status', 'User name missing');
                WebUtil.addFacesMessageSeverityError('Login Status', 'Login type missing, local or iCardea?');
            }

        } catch (Exception e) {
            LOGGER.error('processLogin failed processLogin openid ');
        }
        LOGGER.debug('processLogin END user: ' + username + ' loginType: '+loginType)
    }




}

