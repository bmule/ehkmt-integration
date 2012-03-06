package at.srfg.kmt.ehealth.phrs.presentation.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.security.services.login.RegistrationModel;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSessionService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserSessionService.class);
    // The configuration.xml will have these

//    public static final String forwardRedirectIsAuthenticatedToPage = "/jsf/home.xhtml";
//    public static final String formwardRedirectFilteredDirectory = "/jsf/";
//    public static final String forwardRedirectLoginPage = "/WEB-INF/views/jsp/login.jsp";
    // public static final String forwardRedirectLoginPageAlternate =
    // "/WEB-INF/views/jsp/login.jsp";
    public static CommonDao getCommonDao() {
        return PhrsStoreClient.getInstance().getCommonDao();
    }

    public static boolean isSessionUser(String targetUserOwnerUri) {

        if (targetUserOwnerUri != null) {
            String sessionUser = UserSessionService.getSessionAttributePhrId();
            if (sessionUser != null && targetUserOwnerUri.equals(sessionUser)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Use to redirect to login page When the session is active but no
     * Authentication or OwnerUri parameters found in session. Somehow the user
     * logged out, send them to the login page
     */
    public static void redirectAndLogin() {

        if (ConfigurationService.isAppModeSingleUserTest()) {
            LOGGER.debug("redirectAndLogin isAppModeTest="
                    + ConfigurationService.isAppModeSingleUserTest());
        } else {
            // TODO config page
            redirect(getConfigurationService().getProperty(
                    "forwardRedirectLoginPage"));
        }
    }

    /**
     * To help with future refactoring, use this reference in this class
     *
     * @return
     */
    public static ConfigurationService getConfigurationService() {
        return ConfigurationService.getInstance();
    }

    /**
     * @param uri
     */
    // implicit call to FacesContext.getCurrentInstance().responseComplete(). If
    // use response.sendRedirect then must call ..responseComplete afterwards
    public static void redirect(String uri) {
        // implicit call to
        // FacesContext.getCurrentInstance().responseComplete(). If use
        // response.sendRedirect then must call ..responseComplete afterwards
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(uri);// "article.jsp?article_id=" + articleId);
        } catch (IOException e) {
            LOGGER.error("uri=" + uri, e);
        } catch (Exception e) {
            LOGGER.error("uri=" + uri, e);
        }
    }

    /**
     * Handle PhrFederatedUser by OpenId
     *
     * @param localId
     * @param model
     * @param req
     * @return
     * @throws Exception
     */
    public static PhrFederatedUser managePhrUserSessionByOpenIdUserLoginScenario(
            String localId, RegistrationModel model, HttpServletRequest req) throws Exception {

        PhrFederatedUser phrUser = null;
        if (req != null) {
            String theUserId = localId;

            if (theUserId == null) {
                theUserId = req.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN);
            }
            if (theUserId == null) {
                theUserId = req.getParameter("username");
            }
            if (theUserId == null) {
                throw new Exception("Missing user name or invalid parameter");
            }

            theUserId = theUserId.trim();

            // assign admin regardless of case to a constant 'admin' name
            if (PhrsConstants.AUTHORIZE_USER_ADMIN.equalsIgnoreCase(theUserId)) {
                //changing the case
                theUserId = PhrsConstants.AUTHORIZE_USER_ADMIN;
            }

            HttpSession sess = req.getSession(true);// create session if needed

            if (sess != null) {
                //TODO model.getLocalShortId() update code update Federated User
                if (model.getLocalShortId() == null && theUserId != null) {

                    model.setLocalShortId(theUserId);
                }
                try {
//                    phrUser = getCommonDao().updatePhrUserByClaimedId(
//                            model.getOpenId(),
//                            registrationAttr, true, true);  //create, update
                    //model, create, update registration parameters except identifier
                    phrUser = getCommonDao().updatePhrUser(model, true, true);
                    //create, update
                } catch (Exception e1) {

                    LOGGER.error("error creating phrUser", e1);
                    throw new Exception("Could not create User from Open Id attributes ");
                }
                //before assigning any important session properties
                if (phrUser == null) {
                    LOGGER.error("User is null, error creating phrUser");
                    throw new Exception("User is null, error creating phrUser ");
                }


                sess.setAttribute(PhrsConstants.OPEN_ID_IS_VERIFIED, model.getIs_verified());


                sess.setAttribute("user_fullname", model.getFullName());
                sess.setAttribute("user_email", model.getEmailAddress());
                sess.setAttribute("user_openid", model.getOpenId());

                sess.setAttribute("user_fullname", model.getFullName());
                sess.setAttribute("user_email", model.getEmailAddress());
                sess.setAttribute("user_openid", model.getOpenId());

                //TODO remove session objects
                sess.setAttribute(PhrsConstants.SESSION_USER_OPENID_OBJECT,
                        model);


                // phrUser = processAuthenticatedUser(openIdUser);
                sess.setAttribute(PhrsConstants.SESSION_USER_LOGIN_ID,
                        phrUser.getUserId());

                sess.setAttribute(PhrsConstants.SESSION_USER_PHR_OWNER_URI,
                        phrUser.getOwnerUri());

                //sess.setAttribute(PhrsConstants.SESSION_USER_PHR_OBJECT,
                //        phrUser);

                sess.setAttribute(
                        PhrsConstants.SESSION_USER_AUTHENTICATION_NAME,
                        phrUser.getOwnerUri());

                String role = model.getRole() != null ? model.getRole() : phrUser.getRole();
                if (role != null && !role.isEmpty()) {
                    sess.setAttribute(PhrsConstants.SESSION_USER_AUTHORITY_ROLE,
                            role);

                }

                //FIXXME PID Consent editor test
                updateSessionProtocolIdTest(phrUser);

                //sess.setAttribute(PhrsConstants.SESSION_USER_AUTHORITY_ROLE, model.getRole());
                try {
                    String greetName = getCommonDao().getUserGreetName(phrUser.getOwnerUri());
                    if (greetName != null) {
                        sess.setAttribute(PhrsConstants.SESSION_USER_GREET_NAME, greetName);
                    }
                } catch (Exception e1) {
                    LOGGER.error("error creating greetname", e1);
                }
            }
        }
        return phrUser;
    }
    /**
     * Demo only no password
     * @param localId
     * @return
     * @throws Exception 
     */
    public static PhrFederatedUser managePhrUserSessionLocalLoginScenario(String localId) throws Exception {
        return managePhrUserSessionLocalLoginScenario(localId);
    }
    /**
     * Manage local login
     * @param localId
     * @param password
     * @return
     * @throws Exception
     */
    public static PhrFederatedUser managePhrUserSessionLocalLoginScenario(String localId,String password, Map<String,String> attrs) throws Exception {

        PhrFederatedUser phrUser = null;
        if (localId != null && !localId.isEmpty()) {
            String theUserId = localId;
     

            theUserId = theUserId.trim();

            // assign admin regardless of case to a constant 'admin' name
            if (PhrsConstants.AUTHORIZE_USER_ADMIN.equalsIgnoreCase(theUserId)) {
                theUserId = PhrsConstants.AUTHORIZE_USER_ADMIN;
            }

            // find it or create it....
            Map<String, String> map = null;// attributes, see parameter

            phrUser = getCommonDao().getPhrUserByLocalUserId(theUserId,
                    map, true);// create if not found


            putSessionAttributeString(PhrsConstants.SESSION_USER_LOGIN_ID,
                    phrUser.getUserId());

            putSessionAttributeString(PhrsConstants.SESSION_USER_PHR_OWNER_URI,
                    phrUser.getOwnerUri());

            //putSessionAttributeString(PhrsConstants.SESSION_USER_PHR_OBJECT,
            //        phrUser);

            putSessionAttributeString(
                    PhrsConstants.SESSION_USER_AUTHENTICATION_NAME,
                    phrUser.getOwnerUri());

            putSessionAttributeString(PhrsConstants.SESSION_USER_AUTHORITY_ROLE,
                    phrUser.getRole());

            String greetName = getCommonDao().getUserGreetName(phrUser.getOwnerUri());

            //FIXXME PID Consent editor test
            updateSessionProtocolIdTest(phrUser);

            if (greetName != null) {
                putSessionAttributeString(PhrsConstants.SESSION_USER_GREET_NAME, greetName);
            }

        } else {
            throw new Exception("UserId null or blank");
        }
        return phrUser;
    }
    public static String SESSION_ATTR_NAME_PROTOCOL_ID_CONSENT_MGR="protocolid";

    public static void updateSessionProtocolId(String pid){
        putSessionAttributeString(SESSION_ATTR_NAME_PROTOCOL_ID_CONSENT_MGR, pid);
    }
    public static void updateSessionProtocolIdTest(PhrFederatedUser phrUser){
        if(phrUser!=null){
            String protocolId=phrUser.getProtocolId(); //getCommonDao().getProtocolId(phrUser.getOwnerUri());
            if(protocolId !=null && ! protocolId.isEmpty()){
                //
            }  else {
                protocolId="191";
            }
            LOGGER.debug("updateSessionProtocolIdTest PID="+protocolId+" phruser.identifier="+phrUser.getIdentifier()+" owner="+phrUser.getOwnerUri());
            updateSessionProtocolId(protocolId);
        }
    }
    /**
    * @deprecated
    * @param localId
    * @param req
    * @return
    * @throws Exception 
    */
    public static PhrFederatedUser managePhrUserSessionLocalLoginScenario(
            String localId, HttpServletRequest req) throws Exception {

        PhrFederatedUser phrUser = null;
        if (req != null) {
            String theUserId = localId;

            if (theUserId == null) {
                theUserId = req.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN);
            }
            if (theUserId == null) {
                theUserId = req.getParameter("username");
            }
            if (theUserId == null) {
                throw new Exception("Missing user name or invalid parameter");
            }

            theUserId = theUserId.trim();

            // assign admin regardless of case to a constant 'admin' name
            if (PhrsConstants.AUTHORIZE_USER_ADMIN.equalsIgnoreCase(theUserId)) {
                theUserId = PhrsConstants.AUTHORIZE_USER_ADMIN;
            }
            HttpSession sess = req.getSession(true);// create session if needed
            if (sess != null) {
                // find it or create it....
                Map<String, String> map = null;// attributes, see parameter
                // naming
                phrUser = getCommonDao().getPhrUserByLocalUserId(theUserId,
                        map, true);// create if not found

                // phrUser = processAuthenticatedUser(openIdUser);
                sess.setAttribute(PhrsConstants.SESSION_USER_LOGIN_ID,
                        phrUser.getUserId());

                sess.setAttribute(PhrsConstants.SESSION_USER_PHR_OWNER_URI,
                        phrUser.getOwnerUri());

                //sess.setAttribute(PhrsConstants.SESSION_USER_PHR_OBJECT,
                //        phrUser);

                sess.setAttribute(
                        PhrsConstants.SESSION_USER_AUTHENTICATION_NAME,
                        phrUser.getOwnerUri());

                sess.setAttribute(PhrsConstants.SESSION_USER_AUTHORITY_ROLE,
                        phrUser.getRole());

                String greetName = getCommonDao().getUserGreetName(phrUser.getOwnerUri());
                if (greetName != null) {
                    sess.setAttribute(PhrsConstants.SESSION_USER_GREET_NAME, greetName);
                }
            }
        }
        return phrUser;
    }

    /**
     * Using FacesContext, when using JSF based pages
     *
     * @param localId
     * @return
     * @throws Exception
     *
     * public static PhrFederatedUser managePhrUserSessionLocalLoginScenario(
     * String localId) throws Exception {
     *
     * PhrFederatedUser phrUser = null; FacesContext context =
     * UserSessionService.getFacesContext(); if (context != null) {
     * HttpServletRequest req = (HttpServletRequest)
     * context.getExternalContext().getRequest(); phrUser =
     * managePhrUserSessionLocalLoginScenario(localId, req);
     *
     *
     * }
     * return phrUser;
    }
     */
    /**
     * To facilitate testing and demonstration
     * <p/>
     * Automatic user setup for local login without password for:
     * <p/>
     * 1. prefix: phruser* 2. prefix: phrtest
     *
     * <p/>
     * or Actual user names: 3. ellen role nurse 4. phruser role nurse 5.
     * phruser1 role doctor
     *
     * @param theUserName
     * @return
     */
    public static boolean loginAutomaticUser(String theUserName) {
        if (theUserName != null && theUserName.trim().length() > 1) {
            if (theUserName.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST)) {
                return true;
            }
            if (theUserName.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_AUTO_USER)) {
                return true;
            }
            if (theUserName.equals(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)) {
                return true;
            }
            //AUTHORIZE_USER_VT_SCENARIO_NURSE_

        }
        return false;
    }

    public static void logMap(Map<String, String> map) {
        LOGGER.debug("Logging map contents:");
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                LOGGER.debug(" key=" + key + " value=" + map.get(key));
            }
        } else {
            LOGGER.debug(" ....map is null or empty");

        }
    }

    public static Map<String, String> extractSingleValues(Map inputMap) {
        Map<String, String> extracted = new HashMap<String, String>();
        if (inputMap != null && !inputMap.isEmpty()) {

            for (Object key : inputMap.keySet()) {

                try {
                    Object obj = inputMap.get((String) key);

                    if (obj instanceof String) {
                        if (obj != null && ((String) obj).isEmpty()) {
                            extracted.put((String) key, (String) obj);
                        }
                        //In case non-standard e.g. role
                    } else if (obj instanceof List) {
                        List col = (List) obj;
                        if (obj != null && col.size() > 0) {
                            Object val = col.get(0);
                            if (obj instanceof String) {
                                extracted.put((String) key, (String) obj);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("extracting map values", e);
                }

            }
        }
        return extracted;
    }

    public static void sessionInit(boolean create, Map<String, String> newSessionAttrs) {

        //HttpSession session=null;
        FacesContext context = null;
        try {

            context = FacesContext.getCurrentInstance();
            if (context != null) {
                // cleanup .. in case there is an error, remove any keys before
                // invalidating
                removeUserSessionKeys(context.getExternalContext().getSessionMap());
                // invalidate session
                context.getExternalContext().invalidateSession();
            }
            //create
        } catch (Exception e) {
            LOGGER.error("sessionInit ", e);

        }

        try {
            if (create && context != null && context.getExternalContext().getSession(create) != null) {
                Map sessionMap = context.getExternalContext().getSessionMap();

                if (sessionMap != null && newSessionAttrs != null && !newSessionAttrs.isEmpty()) {

                    for (String key : newSessionAttrs.keySet()) {
                        if (key != null && !key.isEmpty()) {
                            String value = newSessionAttrs.get(key);
                            if (value != null && !value.isEmpty()) {
                                sessionMap.put(key, value);
                            }
                        }
                    }
                }
            }
            //create
        } catch (Exception e) {
            LOGGER.error("sessionInit ", e);

        }
    }

    /**
     * Invalidate Dyu OpenId RelyingParty and Session
     */
    //FIXXMEOPENID
    public static void invalidateSession() {
        try {
            //UserSessionService.invalidateOpenIdRelyingParty();
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                // cleanup .. in case there is an error, remove any keys before
                // invalidating
                //removeUserSessionKeys(context.getExternalContext()
                //        .getSessionMap());
                try {
                    Map map = context.getExternalContext().getSessionMap();
                    map.clear();
                } catch (Exception e0) {
                }
                // invalidate session
                context.getExternalContext().invalidateSession();
            }
        } catch (Exception e) {
            LOGGER.error("invalidating session ", e);

        }
    }

    public static boolean hasSession() {
        boolean exists = false;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null && context.getExternalContext() != null) {
                if (context.getExternalContext().getSession(false) != null) {
                    exists = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return exists;
    }

    public static Principal getUserPrincipalFromRequest() {
        Principal principal = null;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null && context.getExternalContext() != null) {
                principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            }
        } catch (Exception e) {
            LOGGER.error("getUserPrincipal" + e);
        }
        return principal;
    }

    /**
     * Get remote user
     *
     * @return
     */
    public static String getRemoteUser() {
        String remoteUser = null;

        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null && context.getExternalContext() != null) {
                remoteUser = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
            }
        } catch (Exception e) {
            LOGGER.error("getUserPrincipal" + e);
        }
        return remoteUser;

    }

    /**
     * Clear the session map, if fails, then remove important keys
     * PhrsConstants.SESSION_USER_PHR_OWNER_URI, SESSION_USER_PHR_FILTER_URI,
     * SESSION_USER_PHR_OBJECT,SESSION_USER_OPENID_OBJECT OPEN_ID_IS_VERIFIED
     *
     * @param map session map
     */
    public static void removeUserSessionKeys(Map map) {

        if (map != null && !map.isEmpty()) {
            try {
                map.clear();
            } catch (Exception e) {
            }
        }
        if (map != null && !map.isEmpty()) {
            map = UserSessionService.removeSessionAttr(map, PhrsConstants.SESSION_USER_PHR_OWNER_URI);
            map = UserSessionService.removeSessionAttr(map, PhrsConstants.SESSION_USER_AUTHORITY_ROLE);
            map = UserSessionService.removeSessionAttr(map, PhrsConstants.SESSION_USER_PHR_FILTER_OWNER_URI);
            map = UserSessionService.removeSessionAttr(map, PhrsConstants.SESSION_USER_PHR_OBJECT);
            map = UserSessionService.removeSessionAttr(map, PhrsConstants.SESSION_USER_OPENID_OBJECT);
            map = UserSessionService.removeSessionAttr(map, PhrsConstants.SESSION_USER_AUTHENTICATION_NAME);
            map = UserSessionService.removeSessionAttr(map, PhrsConstants.SESSION_USER_LOGIN_ID);
            map = UserSessionService.removeSessionAttr(map, PhrsConstants.OPEN_ID_IS_VERIFIED);
        }
    }

    public static Map removeSessionAttr(Map map, String propertyName) {
        try {
            if (propertyName != null && map != null && !map.isEmpty() && map.containsKey(propertyName)) {

                if (map.containsKey(propertyName)) {
                    map.remove(propertyName);
                }
            }
        } catch (Exception e) {
        }

        return map;
    }

    /**
     * Get the authenticated users PhrId (ownerUri or healthProfileId)
     *
     * @return
     */
    public static String getSessionAttributePhrId() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_OWNER_URI);

        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static String getSessionUserGreetName() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_GREET_NAME);

        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    /**
     * TODO User principal We will bring back another security package that is
     * compatible
     *
     * @return
     */
    public static Principal getSessionAttributeUserPrincipal() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PRINCIPAL);

        if (obj != null && obj instanceof Principal) {
            return (Principal) obj;
        }
        return null;
    }

    /**
     * Checks if session includes attribute
     * PhrsConstants.SESSION_USER_PHR_OWNER_URI
     *
     * @return
     */
    public static boolean loggedIn() {
        // return hasSessionAttribute(PhrsConstants.SESSION_USER_PHR_OWNER_URI);
        return hasSessionAttribute(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME);
    }

    public static boolean loggedIn(HttpServletRequest request) {
        // return hasSessionAttribute(PhrsConstants.SESSION_USER_PHR_OWNER_URI);
        return hasSessionAttribute(request,
                PhrsConstants.SESSION_USER_AUTHENTICATION_NAME);
    }

    public static boolean hasSessionAuthenication() {

        return loggedIn();
    }

    public static boolean hasSessionOwnerUri() {
        return hasSessionAttribute(PhrsConstants.SESSION_USER_PHR_OWNER_URI);
    }

    /**
     * Checks if session includes attribute
     * PhrsConstants.SESSION_USER_OPENID_OBJECT
     *
     * @return
     */
    public static boolean loggedInByOpenId() {
        return hasSessionAttribute(PhrsConstants.SESSION_USER_OPENID_OBJECT);
    }

    /**
     * A UI might set the view for reading another users data
     *
     * @return
     */
    public static String getSessionAttributeFilterPhrId() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_FILTER_OWNER_URI);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    /**
     * @param attrName
     * @return
     */
    public static String getSessionAttributeString(String attrName) {
        Object obj = getSessionAttribute(attrName);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static String getSessionAttributeRole() {
        String userId = getSessionAttributeUserLoginId();
        if (userId != null) {
            if (userId.startsWith(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)) {
                return PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE;
            }

            if (PhrsConstants.AUTHORIZE_USER_PREFIX_TEST_1.equals(userId)) {
                return PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHYSICIAN;
            }
        }

        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_AUTHORITY_ROLE);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static String getSessionAttributeFilterProtocolId() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_ID);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static String getSessionAttributeFilterProtocolNamespace() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static String getRequestParameterFilterProtocolId() {
        Object obj = getRequestAttributeString(PhrsConstants.REQUEST_USER_PHR_FILTER_PROTOCOL_ID);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static String getRequestParameterFilterProtocolNamespace() {
        Object obj = getRequestAttributeString(PhrsConstants.REQUEST_USER_PHR_FILTER_PROTOCOL_NAMESPACE);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static String getRequestParameterFilterOwnerUri() {
        Object obj = getRequestAttributeString(PhrsConstants.REQUEST_USER_PHR_FILTER_OWNER_URI);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static String getRequestAttributeString(String attrName) {
        Object obj = getRequestParameter(attrName);
        if (obj != null) {
            return (String) obj;
        }
        return null;
    }

    public static boolean hasSessionFilterProtocolId() {
        return null != UserSessionService.getSessionAttributeFilterProtocolId();
    }

    /**
     * @return
     */
//    public static OpenIdUser getSessionAttributeOpenIdUser() {
//
//        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_OPENID_OBJECT);
//        if (obj != null)
//            return (OpenIdUser) obj;
//        return null;
//    }
    public static PhrFederatedUser getSessionAttributePhrUser() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_OBJECT);
        if (obj != null) {
            return (PhrFederatedUser) obj;
        }
        return null;
    }

    public static String getSessionAttributeUserAuthenticatedName() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME);

        if (obj != null && obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    public static String getSessionAttributeUserLoginId() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_LOGIN_ID);

        if (obj != null && obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    /**
     * Gets the sessionMap from the FacesContext, however it does not create a
     * session if one does not exist
     *
     * @return
     */
    public static Map getSessionMap() {
        Map map = null;
        try {
            if (FacesContext.getCurrentInstance() != null) {
                Object sess = FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                if (sess != null) {
                    //getSessionMap creates session automatically
                    map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                }
            }
        } catch (Exception e) {
            LOGGER.error("getSessionMap ", e);
        }
        return map;
    }

    /**
     * @param attrName
     * @return
     */
    public static Object getSessionAttribute(String attrName) {
        String result = null;
        try {
            if (FacesContext.getCurrentInstance() != null) {

                Object sess = FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                if (sess != null) {

                    Map map = null;
                    try {
                        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }

                    if (map != null && map.containsKey(attrName)) {
                        result = (String) map.get(attrName);
                    }
                }
            }
        } catch (Exception e) {

            LOGGER.error("attrName=" + attrName, e);
        }
        return result;
    }

    /**
     * Get request parameter. For known strings, use
     * <code>getRequestParameter</code>
     *
     * @param attrName
     * @return
     */
    public static Object getRequestParameter(String attrName) {
        String result = null;
        try {
            if (FacesContext.getCurrentInstance() != null) {
                Map map = null;
                try {
                    map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
                } catch (Exception e) {
                    LOGGER.error("attrName=" + attrName, e);
                }

                if (map != null && map.containsKey(attrName)) {
                    result = (String) map.get(attrName);
                }
            }
        } catch (Exception e) {

            LOGGER.error("attrName=" + attrName, e);
        }
        // if(result!=null)
        // System.out.println("request attrName="+attrName+ " value="+result);

        return result;
    }

    /**
     * @param note
     * @param paramName
     * @return
     */
    public static boolean checkSessionMap(String note, String paramName) {
        boolean flag = false;
        try {
            Object sess = FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            if (sess != null) {

                Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                flag = map.containsKey(paramName);
                System.out.println("session map note=" + note + " parm="
                        + paramName + " flag=" + flag);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOGGER.error("", e);
        }
        return flag;
    }

    public static FacesContext getFacesContext() {
        FacesContext context = null;
        try {
            context = FacesContext.getCurrentInstance();
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return context;
    }

    public static boolean hasSessionAttribute(HttpServletRequest request,
            String attrName) {
        boolean flag = false;
        if (request != null && attrName != null) {
            // request
            HttpSession sess = request.getSession(false);
            if (sess != null) {
                Object value = sess.getAttribute(attrName);
                if (value != null) {
                    flag = true;
                }
            }

        }
        return flag;
    }

    public static boolean hasSessionAttribute(String attrName) {
        boolean flag = false;

        try {
            FacesContext context = getFacesContext();
            if (context != null) {
                Object sess = context.getExternalContext().getSession(false);
                if (sess != null && attrName != null) {

                    Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
                    flag = map.containsKey(attrName);

                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return flag;
    }

    public static void putSessionAttributeString(String attrName, String value) {

        if (attrName != null && value != null) {
            try {
                //create session if necessary
                FacesContext.getCurrentInstance().getExternalContext().getSession(true);

                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(attrName, value);

            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * Check if the request contains these request parameters and add to the
     * session
     * <p/>
     * PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_ID
     * PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE
     * PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE
     */
    public static void updateRequestToSessionParameters() {

        String filterProtcolId = UserSessionService.getRequestParameterFilterProtocolId();
        String filterProtcolNamespace = UserSessionService.getRequestParameterFilterProtocolNamespace();

        if (filterProtcolId != null) {
            putSessionAttributeString(
                    PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_ID,
                    filterProtcolId);
            // get ownerUri from protocolUri
            String ownerUri = getCommonDao().getOwnerUriByIdentifierProtocolId(
                    filterProtcolId, filterProtcolNamespace);
            if (ownerUri != null && ownerUri.length() > 2) {
                putSessionAttributeString(
                        PhrsConstants.SESSION_USER_PHR_FILTER_OWNER_URI,
                        ownerUri);
            }
        }
        if (filterProtcolNamespace != null) {
            putSessionAttributeString(
                    PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE,
                    filterProtcolNamespace);
        }

    }
//    final HttpServletRequest request =
//            (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//    final String logoUrl = request.getContextPath() + "/servlet/fileServlet?id="
//            + generatingOrg.getLogo().getIdLazyFix();
//    Contexts.getEventContext().set("orgLogoUrl", logoUrl);

    public static String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    public static String getContextPath(FacesContext context) {
        try {
            if (context != null) {
                HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                return getContextPath(request);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Relative path should include starting "/" otherwise it is inserted
     *
     * @param request
     * @param relativePath "/servlet/myServlet?id=", cannot be null
     * @return request.getContextPath() + "/servlet/myServlet?id=";
     */
    public static String getUrl(HttpServletRequest request, String relativePath) {
        String url = null;
        try {
            if (request != null && relativePath != null) {
                String path = relativePath.startsWith("/") ? relativePath : "/" + relativePath;

                url = request.getContextPath() + path;
                //   request.getContextPath() + "/servlet/fileServlet?id=";
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return url;
    }

    /**
     * Relative path should include starting "/"
     *
     * @param context
     * @param relativePath - cannot be null
     * @return
     */
    public static String getUrl(FacesContext context, String relativePath) {
        return WebUtil.getUrl(context, relativePath);
    }

    /**
     *
     * @param relativePath
     * @return
     */
    public static String getUrlByFacesContext(String relativePath) {
        return WebUtil.getUrlByFacesContext(relativePath);
    }

    /**
     * Set error messages from servlet into session and then pick up by JSF
     * based page after Open ID login attempt
     *
     * @param req
     * @param errorMsg if null, the attribute is removed
     */
    public static void setSessionLoginErrorMsg(HttpServletRequest req, String errorMsg) {

        if (req != null) {

            try {
                HttpSession sess = req.getSession(true);
                if (sess != null) {
                    if (errorMsg != null) {
                        sess.setAttribute(PhrsConstants.ERROR_MSG_ATTR, errorMsg);
                    } else {
                        //if null remove it
                        sess.removeAttribute(PhrsConstants.ERROR_MSG_ATTR);
                    }
                }
            } catch (Exception e) {
                LOGGER.debug("Error setting error message as session attribute from Openid login", e);
            }
        }
    }

    /**
     * Status of the Open ID login
     *
     * @return is_verified = true, false, or null if not set by OpenId
     * LoginServlet or registration service
     *
     */
    public static Boolean getSessionAttributeOpenIdIsVerified() {
        Boolean verified = null;
        Object obj = UserSessionService.getSessionAttribute(PhrsConstants.OPEN_ID_IS_VERIFIED);
        if (obj != null) {
            try {
                if (obj instanceof String) {
                    verified = Boolean.parseBoolean((String) obj);
                } else if (obj instanceof Boolean) {
                    verified = (Boolean) obj;
                }
            } catch (Exception e) {
                LOGGER.error("Boolean parsing exception on Session attribute is_verified");
            }
        }

        return verified;
    }
    public static boolean sessionUserHasMedicalRole(){
        String role= UserSessionService.getSessionAttributeRole();
        if(role==null) return false;
        return ConfigurationService.getInstance().isMedicalCareRole(role);
    }

    public static boolean getSystemStatus(){
        boolean status=false;
        try {
            status=PhrsStoreClient.getInstance().getSystemStatus();
            LOGGER.debug("getSystemStatus ="+status);
        } catch (Exception e) {
            LOGGER.error("getSystemStatus exception, system really failed");
        }
        return status;
    }
}
