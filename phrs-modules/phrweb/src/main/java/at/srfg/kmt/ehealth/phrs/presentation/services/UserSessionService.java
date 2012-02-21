package at.srfg.kmt.ehealth.phrs.presentation.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrOpenId;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.security.services.ConsentMgrService;
import at.srfg.kmt.ehealth.phrs.security.services.OpenIdConstants;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.ext.AxSchemaExtension;
import com.dyuproject.openid.ext.SRegExtension;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSessionService {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(UserSessionService.class);
    // The configuration.xml will have these

    public static final String forwardRedirectIsAuthenticatedToPage = "/jsf/home.xhtml";
    public static final String formwardRedirectFilteredDirectory = "/jsf/";
    public static final String forwardRedirectLoginPage = "/WEB-INF/views/jsp/login.jsp";

    // public static final String forwardRedirectLoginPageAlternate =
    // "/WEB-INF/views/jsp/login.jsp";

    public static CommonDao getCommonDao() {
        return PhrsStoreClient.getInstance().getCommonDao();
    }

    public static void invalidateOpenIdRelyingParty() {
        try {
            RelyingParty.getInstance().invalidate(
                    (HttpServletRequest) FacesContext.getCurrentInstance()
                            .getExternalContext().getRequest(),
                    (HttpServletResponse) FacesContext.getCurrentInstance()
                            .getExternalContext().getResponse());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public static boolean isSessionUser(String targetUserOwnerUri) {

        if (targetUserOwnerUri != null) {
            String sessionUser = UserSessionService.getSessionAttributePhrId();
            if (sessionUser != null && targetUserOwnerUri.equals(sessionUser))
                return true;
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
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(uri);// "article.jsp?article_id=" + articleId);
        } catch (IOException e) {
            LOGGER.error("uri=" + uri, e);
        } catch (Exception e) {
            LOGGER.error("uri=" + uri, e);
        }
    }

    public static void invalidateOpenIdRelyingParty(HttpServletRequest request,
                                                    HttpServletResponse response) {
        try {
            RelyingParty.getInstance().invalidate(request, response);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public static PhrFederatedUser managePhrUserSessionLocalLoginScenario(
            String localId, HttpServletRequest req) throws Exception {

        PhrFederatedUser phrUser = null;
        if (req != null) {
            String theUserId = localId;

            if (theUserId == null)
                theUserId = req
                        .getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN);
            if (theUserId == null)
                theUserId = req.getParameter("username");
            if (theUserId == null)
                throw new Exception("Missing user name or invalid parameter");

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

                sess.setAttribute(PhrsConstants.SESSION_USER_PHR_OBJECT,
                        phrUser);

                sess.setAttribute(
                        PhrsConstants.SESSION_USER_AUTHENTICATION_NAME,
                        phrUser.getOwnerUri());

                sess.setAttribute(PhrsConstants.SESSION_USER_AUTHORITY_ROLE,
                        phrUser.getRole());

                String greetName=getCommonDao().getUserGreetName(phrUser.getOwnerUri());
                if(greetName!=null) sess.setAttribute(PhrsConstants.SESSION_USER_GREET_NAME,greetName);
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
     */
    public static PhrFederatedUser managePhrUserSessionLocalLoginScenario(
            String localId) throws Exception {

        PhrFederatedUser phrUser = null;
        FacesContext context = UserSessionService.getFacesContext();
        if (context != null) {
            HttpServletRequest req = (HttpServletRequest) context
                    .getExternalContext().getRequest();
            phrUser = managePhrUserSessionLocalLoginScenario(localId, req);


        }
        return phrUser;
    }

    /**
     * To facilitate testing and demonstration
     * <p/>
     * Automatic user setup for local login without password for:
     * <p/>
     * 1. prefix: phruser*
     * 2. prefix: phrtest*
     * <p/>
     * or Actual user names:
     * 3. ellen    role nurse
     * 4. phruser  role nurse
     * 5. phruser1 role doctor
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

    /**
     * Processes an OpenId User, gets or creates a new PhrUser and
     * creates/updates the session This User management scenario requires an
     * OpenId to create a user account
     *
     * @param openIdUser update session data
     * @return PhrUser - if this is null, then
     *         <p/>
     *         Note: Any user mgt and authentication scenario will store a
     *         session variable for:
     *         PhrsConstants.SESSION_USER_AUTHENTICATION_NAME that can indicate
     *         whether the user is authentication / logged in There are other
     *         params stored for OpenId and ownerUri in the Session.
     *         <p/>
     *         PhrsConstants: SESSION_USER_AUTHENTICATION_NAME
     *         SESSION_USER_PHR_OWNER_URI
     *         <p/>
     *         These might later be removed: SESSION_USER_OPENID_OBJECT
     *         SESSION_USER_PHR_OBJECT
     *         <p/>
     *         Previous implementation, however, left for future refactoring
     *         SESSION_USER_PRINCIPAL
     */

    public static PhrFederatedUser managePhrUserSessionByOpenIdUserLoginScenario(
            OpenIdUser openIdUser, HttpServletRequest req) throws Exception {

        PhrFederatedUser phrUser = null;

        if (req != null) {
            HttpSession sess = req.getSession(false);

            if (sess == null) {
                System.out
                        .println("initOpenIdUserSession - session NULL, create it ");
            } else {
                System.out.println("initOpenIdUserSession - session NOT NULL ");
            }

            sess = req.getSession(true);


            sess.setAttribute(PhrsConstants.SESSION_USER_OPENID_OBJECT,
                    openIdUser);

            if (sess != null) {
                try {

                    // FederatedUser has the identifier, now. Update when
                    // support for multiple OpenIds

                    // create or update phrUser and ave, extract params to
                    // 'info' key into openIdUser

                    Map<String, String> openIdAttrs = extractOpenIdParameters(openIdUser);  //don't remove params openId object
                    Map<String, String> temp = new HashMap<String, String>();
                    //get/update or create a new  PhrFederatedUser
                    phrUser = processAuthenticatedUser(openIdUser, temp);

                    // persist the OpenIdUser, this is not the PhrFederatedUser
                    // update the data from the incoming userOpenId
                    //optional, but for debugging and future multiple OpenIds per user
                    BasePhrOpenId basePhrOpenId_2 = getCommonDao()
                            .crudSaveOpenIdUser(phrUser.getOwnerUri(),
                                    openIdUser, false);//don't created the PhrFederatedUser

                    // put relevant objects into the session

                    if (sess != null) {

                        // sess.setAttribute(PhrsConstants.SESSION_USER_LOGIN_ID,
                        // phrUser.getUserId());

                        // User OpenId identifier directly instead of
                        // PhrFederatedUser directly
                        sess.setAttribute(PhrsConstants.SESSION_USER_LOGIN_ID,
                                CommonDao.extractId(openIdUser));//claimId
                        //openIdUser.getIdentifier());

                        sess.setAttribute(
                                PhrsConstants.SESSION_USER_PHR_OWNER_URI,
                                phrUser.getOwnerUri());

                        sess.setAttribute(
                                PhrsConstants.SESSION_USER_PHR_OBJECT, phrUser);

                        sess.setAttribute(
                                PhrsConstants.SESSION_USER_AUTHENTICATION_NAME,
                                phrUser.getOwnerUri());
                        // Get role directly?

                        if (openIdAttrs != null
                                && openIdAttrs
                                .containsKey(PhrsConstants.OPEN_ID_PARAM_ROLE)) {
                            sess.setAttribute(
                                    PhrsConstants.SESSION_USER_AUTHORITY_ROLE,
                                    openIdAttrs
                                            .get(PhrsConstants.OPEN_ID_PARAM_ROLE));
                        } else {
                            sess.setAttribute(
                                    PhrsConstants.SESSION_USER_AUTHORITY_ROLE,
                                    phrUser.getRole());
                        }

                        String greetName=getCommonDao().getUserGreetName(phrUser.getOwnerUri());
                        if(greetName!=null) sess.setAttribute(PhrsConstants.SESSION_USER_GREET_NAME,greetName);
      
                    }
                } catch (Exception e) {
                    LOGGER.error("OpenId init error context, session ", e);
                    throw new Exception();
                }

            }
        }
        return phrUser;
    }

    /*
      * Gets the ProtocolId, but checks for ConfigurationService.isAppModeTest()
      * in case of null values
      *
      * @param openIdUser
      *
      * @return
      *
      *
      * public static String extractProtocolId(Map<String, String> map) { String
      * value = null; if (map != null &&
      * map.containsKey(PhrsConstants.OPEN_ID_PARAM_PROTOCOL_ID)) { value =
      * map.get(PhrsConstants.OPEN_ID_PARAM_PROTOCOL_ID); }
      *
      * if (value == null && ConfigurationService.isAppModeTest()) { value =
      * PhrsConstants.PROTOCOL_ID_TEST_VALUE; }
      *
      * // String protocolId = getOpenIdAttribute(openIdUser, //
      * PhrsConstants.OPEN_ID_PARAM_PROTOCOL_ID, devaultValue);
      *
      * return value; }
      *
      * Gets the ProtocolId Namespace, but checks for
      * ConfigurationService.isAppModeTest() in case of null values
      *
      * @param openIdUser
      *
      * @return
      *
      * public static String extractProtocolNamespace(Map<String, String> map) {
      * String value = null; if (map != null &&
      * map.containsKey(PhrsConstants.OPEN_ID_PARAM_PROTOCOL_NAMESPACE)) { value
      * = map.get(PhrsConstants.OPEN_ID_PARAM_PROTOCOL_NAMESPACE); }
      *
      * if (value == null && ConfigurationService.isAppModeTest()) { value =
      * PhrsConstants.PROTOCOL_NAMESPACE_TEST_VALUE; } return value; }
      */
    public static String extractRole(Map<String, String> map) {
        String value = null;
        if (map != null && map.containsKey(PhrsConstants.OPEN_ID_PARAM_ROLE)) {
            
            //value = map.get(PhrsConstants.OPEN_ID_PARAM_ROLE);
            value = ConsentMgrService.extractMappedRole(map);
            LOGGER.debug("OpenId role found = "+value);
        } else {
            LOGGER.debug("No role key found in aux schema map");
            logMap(map);
            
            value = PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER;
        }

        // if (value == null && ConfigurationService.isAppModeTest()) {
        // value = PhrsConstants.PROTOCOL_ROLE_TEST_VALUE;
        // }
        return value;
    }
    

    /**
     * @param openIdUser
     * @param attrName
     * @param defaultValue - can be null. In calling methods, one can test for
     *                     ConfigurationService.isAppModeTest() if a special test value
     *                     should be used when the value is not found in the OpenId
     *                     attribute list
     * @return
     */
    public static String getOpenIdAttribute(OpenIdUser openIdUser,
                                            String attrName, String defaultValue) {

        String value = null;

        if (openIdUser != null && attrName != null) {
            try {
                value = (String) openIdUser.getAttribute(attrName);
            } catch (Exception e) {
                LOGGER.error("openIdUser " + openIdUser.getIdentifier() + " attr=" + attrName, e);
            }
        }
        // ConfigurationService.isAppModeTest() &&
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
    public static void logMap(Map<String,String> map){
        LOGGER.debug("Logging map contents:");
        if(map!=null && !map.isEmpty()){
            for(String key:map.keySet()){
                LOGGER.debug(" key="+key+" value="+map.get(key));
            }
        }  else {
            LOGGER.debug(" ....map is null or empty");
            
        }
    }
    public static Map<String,String> extractSingleValues(Map inputMap){
        Map<String,String> extracted=new HashMap<String,String>();
        if(inputMap!=null && !inputMap.isEmpty()){
            
              for(Object key:inputMap.keySet()) {

                  try {
                      Object obj=inputMap.get((String)key);

                      if(obj instanceof String){
                          if(obj !=null && ((String) obj).isEmpty())
                              extracted.put((String)key,(String)obj);
                          //In case non-standard e.g. role
                      }  else if(obj instanceof List){
                          List col=  (List) obj;
                          if(obj!=null && col.size() > 0) {
                              Object val= col.get(0);
                              if(obj instanceof String){
                                extracted.put((String)key,(String)obj);
                              }
                          }
                      }
                  } catch (Exception e) {
                     LOGGER.error("extracting map values",e);
                  }

              }
        }
        return extracted;
    }

    public static Map<String, String> extractOpenIdAttributeExchange(
            OpenIdUser user, boolean removeUser) {

        Map<String, String> map = null;
        try {
            Map attrMap = (Map)user.getAttribute(AxSchemaExtension.ATTR_NAME);
            map = extractSingleValues(attrMap);
            String role=extractRole(attrMap);
            if(role!=null && !role.isEmpty()){
                map.put(PhrsConstants.OPEN_ID_PARAM_ROLE, role);
            } else {
                
            }
            //The following casts Map<String,String>
            /*
            if (removeUser)
                map = AxSchemaExtension.remove(user);
            else
                map = AxSchemaExtension.get(user);
            */
            LOGGER.error("extract OpenId Attribute Exchange "+" for claimedID"+user.getClaimedId());
            logMap(map);
        } catch (Exception e) {

            if (user != null) LOGGER.error("openIdUser " + user.getIdentifier(), e);
            else LOGGER.error("extract OpenId Attribute Exchange openIdUser null", e);
        }
        return map;
    }

    /**
     * @param user
     * @param removeUser - remove the user from the register, but get the map
     * @return
     */
    public static Map<String, String> extractOpenIdSimpleRegistration(
            OpenIdUser user, boolean removeUser) {
        Map<String, String> map = null;
        try {
            Map attrMap = (Map)user.getAttribute(AxSchemaExtension.ATTR_NAME);
            map = extractSingleValues(attrMap);
//            if (removeUser)
//                map = SRegExtension.remove(user);
//            else
//                map = SRegExtension.get(user);

            LOGGER.error("extract OpenId Simple Registration "+" for claimedID"+user.getClaimedId());
            logMap(map);
        } catch (Exception e) {
            if (user != null) LOGGER.error("OpenIdUser=" + user.getIdentifier(), e);
            else LOGGER.error("extract OpenId Simple Registration OpenIdUser=null", e);
        }


        /*
        Normalize and conforms to listener
        This should not be necessary, PhrContants use same attr alias
        issue if Axschema and SREG use different aliases !
         */
        if(map!=null && !map.isEmpty()){

             if(map.containsKey(OpenIdConstants.SREG_NICKNAME)) {
                 map.put(PhrsConstants.OPEN_ID_PARAM_NICK_NAME,
                         map.get(OpenIdConstants.SREG_NICKNAME));
             }
            if(map.containsKey(OpenIdConstants.SREG_EMAIL)) {
                map.put(PhrsConstants.OPEN_ID_PARAM_EMAIL,
                        map.get(OpenIdConstants.SREG_EMAIL));
            }
            if(map.containsKey(OpenIdConstants.SREG_LANGUAGE)) {
                map.put(PhrsConstants.OPEN_ID_PARAM_LANGUAGE,
                        map.get(OpenIdConstants.SREG_LANGUAGE));
            }
            if(map.containsKey(OpenIdConstants.SREG_FULLNAME)) {
                map.put(PhrsConstants.OPEN_ID_PARAM_FULL_NAME,
                        map.get(OpenIdConstants.SREG_FULLNAME));
            }
            if(map.containsKey(OpenIdConstants.SREG_POSTCODE)) {
                map.put(PhrsConstants.OPEN_ID_PARAM_POST_CODE,
                        map.get(OpenIdConstants.SREG_POSTCODE));
            }
            if(map.containsKey(OpenIdConstants.SREG_COUNTRY)) {
                map.put(PhrsConstants.OPEN_ID_PARAM_COUNTRY,
                        map.get(OpenIdConstants.SREG_COUNTRY));
            }
            if(map.containsKey(OpenIdConstants.SREG_DOB)) {
                map.put(PhrsConstants.OPEN_ID_PARAM_DATE_OF_BIRTH,
                        map.get(OpenIdConstants.SREG_DOB));
            }
            if(map.containsKey(OpenIdConstants.SREG_GENDER)) {
                map.put(PhrsConstants.OPEN_ID_PARAM_GENDER,
                        map.get(OpenIdConstants.SREG_GENDER));
            }
            if(map.containsKey(OpenIdConstants.SREG_TIMEZONE)) {
                map.put(PhrsConstants.OPEN_ID_PARAM_TIME_ZONE,
                        map.get(OpenIdConstants.SREG_TIMEZONE));
            }

        }
        return map;
    }
    
    public static Map<String, String> extractOpenIdStringAttributes(
            OpenIdUser user) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            Map<String, Object> omap = user.getAttributes();
            if (omap != null && !omap.isEmpty()) {
                for (String key : omap.keySet()) {
                    Object value = omap.get(key);
                    if (value != null && value instanceof String)
                        map.put(key, (String) value);
                }
            }
        } catch (Exception e) {
            if (user != null) LOGGER.error("OpenIdUser=" + user.getIdentifier(), e);
            else LOGGER.error("OpenIdUser=null", e);
        }

        return map;
    }

    /**
     * Extract OpenId string attributes, Attribute Exchange and Simple
     * Registration attributes into common attribute map
     *
     * @param openIdUser
     * @param removeUserFromExtensions - retrieve the maps and clean up
     * @return
     */
    public static Map<String, String> extractOpenIdAllAttributes(
            OpenIdUser openIdUser, boolean includeOpenIdStringAttributes,
            boolean removeUserFromExtensions) {

        Map<String, String> all = new HashMap<String, String>();
        if (openIdUser == null) return all;

        Map<String, String> smap = extractOpenIdSimpleRegistration(openIdUser,
                removeUserFromExtensions);

        Map<String, String> axMap = extractOpenIdAttributeExchange(openIdUser,
                removeUserFromExtensions);

        if (includeOpenIdStringAttributes) {
            Map<String, String> omap = extractOpenIdStringAttributes(openIdUser);


            if (omap != null && !omap.isEmpty()) {
                all.putAll(omap);
            }
        }
        if (smap != null && !smap.isEmpty()) {
            all.putAll(smap);
        }
        if (axMap != null && !axMap.isEmpty()) {
            all.putAll(axMap);
        }
        String id = CommonDao.extractId(openIdUser);
        if (id != null) {

            if (!all.containsKey(PhrsConstants.OPEN_ID_PARAM_CLAIM_ID))
                all.put(PhrsConstants.OPEN_ID_PARAM_CLAIM_ID, id);

        }
        if (openIdUser.getIdentifier() != null) {

            if (!all.containsKey(PhrsConstants.OPEN_ID_PARAM_OP_IDENTIFIER))
                all.put(PhrsConstants.OPEN_ID_PARAM_OP_IDENTIFIER, openIdUser.getIdentifier());

        }
        if (openIdUser.getIdentifier() != null) {

            if (!all.containsKey(PhrsConstants.OPEN_ID_PARAM_IDENTITY))
                all.put(PhrsConstants.OPEN_ID_PARAM_IDENTITY, openIdUser.getIdentity());

        }
        openIdUser.setAttribute("info", all);
        return all;
    }

    public static Map<String, String> extractOpenIdParameters(OpenIdUser user) {

        return extractOpenIdAllAttributes(user, false, false);
        /*
        Map<String, String> map = new HashMap<String, String>();

        // request.getAttribute(OpenIdUser.ATTR_NAME);
        if (user != null) {
            Map<String, String> sreg = SRegExtension.get(user);
            Map<String, String> axschema = AxSchemaExtension.get(user);

            LOGGER.debug("OpenID parameters OP identifier= "
                    + user.getIdentifier()+" claimedId="+user.getClaimedId());

            if (sreg != null && !sreg.isEmpty()) {
                map.putAll(sreg);

                LOGGER.debug("...sreg= " + sreg);

            } else if (axschema != null && !axschema.isEmpty()) {
                map.putAll(axschema);
                LOGGER.debug("...axschema= " + axschema);
            }
            user.setAttribute("info", map);

        }
        return map; */
    }

    /**
     * Process the phrUser. Extract and update the attributes, add the OpenId
     * attributes to the phrUser and also update the OpenIdUser with attribute
     * 'info' that contains summary of simple and aux parameters
     *
     * @param openIdUser
     * @param attrs
     */
    public static PhrFederatedUser processAuthenticatedUser(
            OpenIdUser openIdUser, Map<String, String> attrs) {

        // create phrUser if not found by identifier
        Map<String, String> extract = extractOpenIdParameters(openIdUser);
        if (attrs == null)
            attrs = new HashMap<String, String>();
        if (extract != null && !extract.isEmpty())
            attrs.putAll(extract);

        PhrFederatedUser phrUser = null;
        // boolean exists=false;
        try {
            phrUser = getCommonDao().updatePhrUserByClaimedId(CommonDao.extractId(openIdUser),
                    attrs, true, true);
        } catch (Exception e1) {

            LOGGER.error("error creating phrUser", e1);

        }
        if (phrUser == null) {
            phrUser = new PhrFederatedUser();
            phrUser.setIdentifier(CommonDao.extractId(openIdUser));
            phrUser.setUserId(CommonDao.extractId(openIdUser));
        }

        return phrUser;
    }

    /**
     * Invalidate OpenId RelyingParty and Session
     */
    public static void invalidateSession() {
        try {
            UserSessionService.invalidateOpenIdRelyingParty();
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                // cleanup .. in case there is an error, remove any keys before
                // invalidating
                removeUserSessionKeys(context.getExternalContext()
                        .getSessionMap());
                // invalidate session
                context.getExternalContext().invalidateSession();
            }
        } catch (Exception e) {
            LOGGER.error("", e);

        }
    }

    public static boolean hasSession() {
        boolean exists = false;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null && context.getExternalContext() != null) {
                if (context.getExternalContext().getSession(false) != null)
                    exists = true;
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
                principal = FacesContext.getCurrentInstance()
                        .getExternalContext().getUserPrincipal();
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
                remoteUser = FacesContext.getCurrentInstance()
                        .getExternalContext().getRemoteUser();
            }
        } catch (Exception e) {
            LOGGER.error("getUserPrincipal" + e);
        }
        return remoteUser;

    }

    /**
     * Remove PhrsConstants.SESSION_USER_PHR_OWNER_URI,
     * SESSION_USER_PHR_FILTER_URI,
     * SESSION_USER_PHR_OBJECT,SESSION_USER_OPENID_OBJECT
     *
     * @param map
     */
    public static void removeUserSessionKeys(Map map) {
        if (map != null && !map.isEmpty()) {

            if (map.containsKey(PhrsConstants.SESSION_USER_PHR_OWNER_URI))
                map.remove(PhrsConstants.SESSION_USER_PHR_OWNER_URI);

            if (map.containsKey(PhrsConstants.SESSION_USER_AUTHORITY_ROLE))
                map.remove(PhrsConstants.SESSION_USER_AUTHORITY_ROLE);

            if (map.containsKey(PhrsConstants.SESSION_USER_PHR_FILTER_OWNER_URI))
                map.remove(PhrsConstants.SESSION_USER_PHR_FILTER_OWNER_URI);

            if (map.containsKey(PhrsConstants.SESSION_USER_PHR_OBJECT))
                map.remove(PhrsConstants.SESSION_USER_PHR_OBJECT);

            if (map.containsKey(PhrsConstants.SESSION_USER_OPENID_OBJECT))
                map.remove(PhrsConstants.SESSION_USER_OPENID_OBJECT);

            if (map.containsKey(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME))
                map.remove(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME);

            if (map.containsKey(PhrsConstants.SESSION_USER_LOGIN_ID))
                map.remove(PhrsConstants.SESSION_USER_LOGIN_ID);

        }
    }

    /**
     * Get the authenticated users PhrId (ownerUri or healthProfileId)
     *
     * @return
     */
    public static String getSessionAttributePhrId() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_OWNER_URI);

        if (obj != null)
            return (String) obj;
        return null;
    }
    public static String getSessionUserGreetName() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_GREET_NAME);

        if (obj != null)
            return (String) obj;
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

        if (obj != null && obj instanceof Principal)
            return (Principal) obj;
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
        if (obj != null)
            return (String) obj;
        return null;
    }

    /**
     * @param attrName
     * @return
     */
    public static String getSessionAttributeString(String attrName) {
        Object obj = getSessionAttribute(attrName);
        if (obj != null)
            return (String) obj;
        return null;
    }

    public static String getSessionAttributeRole() {
        String userId = getSessionAttributeUserLoginId();
        if (userId != null) {
            if ("phrtest".equals(userId)) return PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE;
            if ("phrtest1".equals(userId)) return PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_PHYSICIAN;
        }

        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_AUTHORITY_ROLE);
        if (obj != null)
            return (String) obj;
        return null;
    }

    public static String getSessionAttributeFilterProtocolId() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_ID);
        if (obj != null)
            return (String) obj;
        return null;
    }

    public static String getSessionAttributeFilterProtocolNamespace() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE);
        if (obj != null)
            return (String) obj;
        return null;
    }

    public static String getRequestParameterFilterProtocolId() {
        Object obj = getRequestAttributeString(PhrsConstants.REQUEST_USER_PHR_FILTER_PROTOCOL_ID);
        if (obj != null)
            return (String) obj;
        return null;
    }

    public static String getRequestParameterFilterProtocolNamespace() {
        Object obj = getRequestAttributeString(PhrsConstants.REQUEST_USER_PHR_FILTER_PROTOCOL_NAMESPACE);
        if (obj != null)
            return (String) obj;
        return null;
    }

    public static String getRequestParameterFilterOwnerUri() {
        Object obj = getRequestAttributeString(PhrsConstants.REQUEST_USER_PHR_FILTER_OWNER_URI);
        if (obj != null)
            return (String) obj;
        return null;
    }

    public static String getRequestAttributeString(String attrName) {
        Object obj = getRequestParameter(attrName);
        if (obj != null)
            return (String) obj;
        return null;
    }

    public static boolean hasSessionFilterProtocolId() {
        return null != UserSessionService.getSessionAttributeFilterProtocolId();
    }

    /**
     * @return
     */
    public static OpenIdUser getSessionAttributeOpenIdUser() {

        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_OPENID_OBJECT);
        if (obj != null)
            return (OpenIdUser) obj;
        return null;
    }

    public static PhrFederatedUser getSessionAttributePhrUser() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_PHR_OBJECT);
        if (obj != null)
            return (PhrFederatedUser) obj;
        return null;
    }

    public static String getSessionAttributeUserAuthenticatedName() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME);

        if (obj != null && obj instanceof String)
            return (String) obj;
        return null;
    }

    public static String getSessionAttributeUserLoginId() {
        Object obj = getSessionAttribute(PhrsConstants.SESSION_USER_LOGIN_ID);

        if (obj != null && obj instanceof String)
            return (String) obj;
        return null;
    }

    /**
     * @return
     */
    public static Map getSessionMap() {
        Map map = null;
        try {
            map = FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap();
        } catch (Exception e) {
            LOGGER.error("", e);
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

                Object sess = FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(false);
                if (sess != null) {

                    Map map = null;
                    try {
                        map = FacesContext.getCurrentInstance()
                                .getExternalContext().getSessionMap();
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }

                    if (map != null && map.containsKey(attrName)) {
                        result = (String) map.get(attrName);
                    } /*
					 * else { try { if (sess != null && sess instanceof
					 * HttpSession) { Object temp = ((HttpSession) sess)
					 * .getAttribute(attrName); if (temp != null) result =
					 * (String) temp; } } catch (Exception e) {
					 * e.printStackTrace(); } }
					 */
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
                    map = FacesContext.getCurrentInstance()
                            .getExternalContext().getRequestParameterMap();
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
            Object sess = FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(false);
            if (sess != null) {

                Map map = FacesContext.getCurrentInstance()
                        .getExternalContext().getSessionMap();
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
                if (value != null)
                    flag = true;
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

                    Map map = FacesContext.getCurrentInstance()
                            .getExternalContext().getSessionMap();
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
                FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(true);

                FacesContext.getCurrentInstance().getExternalContext()
                        .getSessionMap().put(attrName, value);

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

        String filterProtcolId = UserSessionService
                .getRequestParameterFilterProtocolId();
        String filterProtcolNamespace = UserSessionService
                .getRequestParameterFilterProtocolNamespace();

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

}
