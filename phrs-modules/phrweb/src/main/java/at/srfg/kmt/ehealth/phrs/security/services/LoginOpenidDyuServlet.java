//========================================================================
//Copyright 2007-2008 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package at.srfg.kmt.ehealth.phrs.security.services;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.ext.AxSchemaExtension;
import com.dyuproject.openid.ext.SRegExtension;
import com.dyuproject.util.http.UrlEncodedParameterMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The User chooses to login. The user is directed to starting index.xhtml after
 * successful login The starting page should then check the session for session
 * tokes and provide the use with access to the menu and form functionalities.
 *
 * Based on  DyuProject from  David Yu, Sep 22, 2008
 * @author David Yu
 * @created Sep 22, 2008
 */
@SuppressWarnings("serial")
public class LoginOpenidDyuServlet extends HttpServlet {
    // AxSchemaExtension test add full name and nick name but need correct key name from the properties file
    static {
        RelyingParty
                .getInstance()
                .addListener(new SRegExtension()
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_NICK_NAME)
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_FULL_NAME)
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_EMAIL)
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_POST_CODE)
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_DATE_OF_BIRTH)
                )
                .addListener(new AxSchemaExtension()
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_EMAIL)
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_COUNTRY)
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_LANGUAGE)
                        .addExchange(PhrsConstants.OPEN_ID_PARAM_ROLE)

                )
                .addListener(new RelyingParty.Listener() {

                    public void onDiscovery(OpenIdUser user, HttpServletRequest request) {
                        LOGGER.debug("LoginServlet discovered user claimId: "
                                + user.getClaimedId());
                    }

                    public void onPreAuthenticate(OpenIdUser user, HttpServletRequest request,
                                                  UrlEncodedParameterMap params) {
                        LOGGER.debug("LoginServlet pre-authenticate user claimId: "
                                + user.getClaimedId());
                    }
                    public void onAuthenticate(OpenIdUser user, HttpServletRequest request) {
                        if (user != null) {

                            LOGGER.debug("LoginServlet onAuthenticate newly authenticated openid user.identity: "
                                    + user.getIdentity());
                            try {
                                PhrFederatedUser phrUser = UserSessionService
                                        .managePhrUserSessionByOpenIdUserLoginScenario(user,request);
                                LOGGER.debug("LoginServlet onAuthenticate newly authenticated openid phrUser ownerUri: "
                                        + phrUser.getOwnerUri());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            LOGGER.debug("LoginServlet onAuthenticate error Null OpenIdUser: ");
                        }
                    }

                    public void onAccess(OpenIdUser user, HttpServletRequest request) {
                        LOGGER.debug("LoginServlet user access: "
                                + user.getIdentity());
                        LOGGER.debug("info: " + user.getAttribute("info"));
                    }

                });
    }


    RelyingParty _relyingParty;

    private final static Logger LOGGER = LoggerFactory
            .getLogger(LoginOpenidDyuServlet.class);
    public static final String ERROR_MSG_ATTR = "openid_servlet_filter_msg";

    public static final String DEFAULT_ERROR_MSG = "Your openid could not be resolved.";// openid.servlet_filter.default_error_msg
    public static final String ID_NOT_FOUND_MSG = "Your openid does not exist.";// openid.servlet_filter.id_not_found_msg

    /**
     * The default forward uri handler that basically executes:
     * request.getRequestDispatcher(forwardUri).forward(request, response);
     */
    public static final ForwardUriHandler DEFAULT_FORWARD_URI_HANDLER = new ForwardUriHandler() {
        public void handle(String forwardUri, HttpServletRequest request,
                           HttpServletResponse response) throws IOException,
                ServletException {
            request.getRequestDispatcher(forwardUri).forward(request, response);
        }
    };

    static final String SLASH = "/";

    protected String _forwardUri;
    // OpenIdContextListener sets up the RelyingParty and listeners and stores
    // in ServletContext
    //protected RelyingParty _relyingParty;// init from context or

    protected ForwardUriHandler _forwardHandler;

    protected ForwardUriHandler getForwardUriHandler() {
        return _forwardHandler;
    }

    protected RelyingParty getRelyingParty() {
        return _relyingParty;
    }

    /**
     * This method is called by the servlet container to configure this filter;
     * The init parameter "forwardUri" is required. The relying party used will
     * be the default {@link RelyingParty#getInstance() instance} if it is not
     * found in the servlet context attributes.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        _forwardUri = ConfigurationService.getInstance().getProperty(
                "login_openid_forwardUri");
        /*
           * if (_forwardUri == null){ _forwardUri =
           * config.getInitParameter("forwardUri"); }
           */
        if (_forwardUri == null) {
            _forwardUri = "/WEB-INF/views/jsp/login.jsp";
        }
/*
		// resolve from ServletContext
		_relyingParty = (RelyingParty) config.getServletContext().getAttribute(
				RelyingParty.class.getName());
*/
        // default config if null
        if (_relyingParty == null)
            _relyingParty = RelyingParty.getInstance();

        _forwardHandler = DEFAULT_FORWARD_URI_HANDLER;
    }

    /**
     * Gets the configured forward uri.
     */
    public String getForwardUriLoginPage() {
        return _forwardUri;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // This should direct back to a page that indicates the login status of
        // the user
        // and allows them to continue as an authenticated user
        // be safe and redirect to index.xhtml, it checks the session
        // authentication token

        String defaultRedirectToApplication = request.getContextPath()
                + "/index.xhtml";
        // request.getRequestURI();

        String errorMsg = DEFAULT_ERROR_MSG;
        /*
           * Check session exist and logged in ? Do not try the FacesContext yet,
           * send the HttpSerlvetRequest and try to get the session and login
           * status. If there was already a local login or a successful OpenId
           * login then the session holds the authentication token and no need to
           * continue checking openID
           */
        String requestUri = request.getRequestURI();
        // String requestUri = request.getRequestURL()
        LOGGER.debug("Preparing request for either login scenario. request.getRequestURI()="
                + defaultRedirectToApplication
                + " ...request.getRequestURL()="
                + request.getRequestURL());
        String loginFormAction =null;
        // Already logged in redirect back to main index.html
        if (UserSessionService.loggedIn(request)) {// OK
            response.sendRedirect(defaultRedirectToApplication);
            LOGGER.debug("Authenticated user session is active,  no further authentication steps are taken");
            // return true;
            return;
        } else {
            LOGGER.debug("Authenticated user session was not found,  start authentication steps");

            // there is no token, ALWAYS add authentication related tokens to
            // session. The RelyingParty might not have be invalidated properly.
            // boolean webSessionActive = UserSessionService.hasSession();

            /*
                * try { // } catch (Exception e) { e.printStackTrace(); errorMsg =
                * DEFAULT_ERROR_MSG; }
                */

            OpenIdUser openIdUser = null;

            try {
                //Is this a request directly from the loginForm?
               loginFormAction = request
                        .getParameter("action");
               boolean isLoginFormRequest=false;

                if(loginFormAction !=null && "login".equalsIgnoreCase(loginFormAction)) {
                    isLoginFormRequest=true;

                    boolean isLocalLogin = false;

                    String loginFormIdentifier = request
                            .getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN);
                    String username = request.getParameter("username");

                    if (loginFormIdentifier == null)
                        loginFormIdentifier = username;

                    // check for valid identifier
                    if (loginFormIdentifier != null
                            && loginFormIdentifier.length() > 3) {

                        // If LoginWith set (must), then set up the request with one
                        // designated OpenID provider instead of using default
                        // discovery
                        // over many providers
                        isLocalLogin = AuthenticationUtil
                                .determineLoginScenario(request);
                        LOGGER.debug("Local user authentication detected, not OpenID scenario");

                    } else {
                        // if login form, the form javascript should validate
                        // error return to login form
                        request.setAttribute(ERROR_MSG_ATTR, errorMsg);
                        getForwardUriHandler().handle(getForwardUriLoginPage(),
                                request, response);
                        return;
                    }

                    if (isLocalLogin) {

                        // local login, bypass OpenID
                        // handle local user login. For iCardea testing, this is
                        // very
                        // simple so that we can run the live tests easily
                        PhrFederatedUser pfu = null;
                        try {
                            pfu = UserSessionService
                                    .managePhrUserSessionLocalLoginScenario(
                                            loginFormIdentifier, request);
                        } catch (Exception e) {
                            LOGGER.error("create error  PhrFederatedUser managePhrUserSessionLocalLoginScenario" + e);
                        }

                        if (pfu != null) {

                            response.sendRedirect(defaultRedirectToApplication);
                            return;// true
                        } else {
                            // return to login form
                            request.setAttribute(ERROR_MSG_ATTR, errorMsg);
                            LOGGER.debug("isLocalLogin user " + pfu.getIdentifier() + " forward to"
                                    + getForwardUriLoginPage());
                            getForwardUriHandler().handle(getForwardUriLoginPage(),
                                    request, response);
                            return;// false authenticated
                        }

                    }
                }
                /*
                     * Only OpenId related, local login was handled above and forwarded or redirected with 'return'
                     *
                     * If from the login form with loginWith, then we might have bypassed discovery, there is a OpenId object, but need to
                      * go to the OP next
                     * If the OpenId is returning from the OP, then check for authentication,etc
                     */
                openIdUser = getRelyingParty().discover(request);

                if (openIdUser == null) {
                    // two possibilities, this is a new login request or it is the
                    // OpenId returning  ...but if our session expired, we go back to the login form

                    if (RelyingParty.isAuthResponse(request)) {// authentication
                        // openIdUser is null, user authenicated,
                        // Returns true if we have a positive response from the
                        // OpenID provider.
                        // If the user is associated and we have an auth
                        // response,
                        // then we can verify the user.

                        LOGGER.debug("Redirecting to request.getRequestURI().  RelyingParty.isAuthResponse authentication = true and openIdUser == null, but problem with one of these openIdUser is  null, or  timeout."
                                + requestUri);
                        // There is no session now, but the verify step should
                        // lead to the onAuthentication event listener
                        // where the OpenIdUser login scenario inits the session
                        // and auth token, etc
                        // Without the session, the redirect will lead to a page
                        // without an active session
                        // leave as getRequestURI(), not
                        // defaultRedirectToApplication

                        //TODO FIXXME We do not have an authenticated session user
                        //We have no openIdUser to get data. 
                        //if we already have user in DB, then get init session.

                        //all the request params
                        //Map<String,String> authParams= RelyingParty.getAuthParameters(request);

                        //This is set: com.dyuproject.openid.Constants.OPENID_MODE  and then
                        //forward to login form and we start over
                        //original way, but we need to set up the session again

                        //The user might be in an app on the same web browser, but not this app's session

                        //response.sendRedirect(requestUri);
                        request.setAttribute("OpenID authenticated, but no authenticated session, start over and login", errorMsg);
                        getForwardUriHandler().handle(getForwardUriLoginPage(),
                                request, response);

                    } else {
                        // Two more possibilities: the OpenID failed
                        // or the initial login request
                        // set error msg if the openid_identifier was not resolved.
                        if (request.getParameter(getRelyingParty()
                                .getIdentifierParameter()) != null) {
                            request.setAttribute(ERROR_MSG_ATTR, errorMsg);
                        }
                        if(isLoginFormRequest)   {
                            LOGGER.debug("Found initial login request, forwarding to  login page. ForwardUri="
                                    + getForwardUriLoginPage());
                        } else{
                            LOGGER.debug("Failed OpenID login, forward back again to login page. ForwardUri="
                                + getForwardUriLoginPage());
                        }
                        // forward to user login
                        getForwardUriHandler().handle(getForwardUriLoginPage(),
                                request, response);
                    }
                    return; // false;
                }

                // ReplyingParty has onAuthenticate listener that sets up the
                // user,
                // there should already be session?
                if (openIdUser.isAuthenticated()) {
                    // user already authenticated, however, the session token
                    // was
                    // not found. We already checked this.
                    LOGGER.debug("openIdUser.isAuthenticated=TRUE,  but Application SESSION is expired. PREPARE to recreate USER APP SESSION for openIdUser identifier:"
                            + openIdUser.getIdentifier() + " claimedId"
                            + openIdUser.getClaimedId());
                    // setup session with OpenID. We did not find an
                    // authentication
                    // token in the session or the session was not already
                    // created
                    UserSessionService
                            .managePhrUserSessionByOpenIdUserLoginScenario(
                                    openIdUser, request);
                    return;// true;
                }

                if (openIdUser.isAssociated()
                        && RelyingParty.isAuthResponse(request)) {
                    // verify authentication
                    if (getRelyingParty().verifyAuth(openIdUser, request,
                            response)) {
                        // authenticated
                        // redirect to home app to remove the query params
                        LOGGER.debug("RelyingPart.verifyAuth TRUE, Prepare managePhrUserSessionByOpenIdUserLoginScenario for openIdUser identifier:"
                                + openIdUser.getIdentifier() + " claimedId"
                                + openIdUser.getClaimedId());
                        UserSessionService
                                .managePhrUserSessionByOpenIdUserLoginScenario(
                                        openIdUser, request);
                        response.sendRedirect(defaultRedirectToApplication);
                        return;
                    } else {
                        // failed verification
                        //LOGGER.debug("Failed OpenId verification. openIdUser.isAssociated=true, but false = getRelyingParty().verifyAuth(openIdUser, request,response) ");

                        LOGGER.debug("Failed OpenId verification openIdUser identifier="
                                + openIdUser.getIdentifier() + " claimedId"
                                + openIdUser.getClaimedId());

                        getForwardUriHandler().handle(getForwardUriLoginPage(),
                                request, response);
                    }
                    return;// false;
                }

                // Prepare to associate and authenticate user (use RequestURL
                // not RequestURI)
                StringBuffer url = request.getRequestURL();

                String trustRoot = url.substring(0, url.indexOf(SLASH, 9));
                String realm = url.substring(0, url.lastIndexOf(SLASH));
                String returnTo = url.toString();

                if (getRelyingParty().associateAndAuthenticate(openIdUser,
                        request, response, trustRoot, realm, returnTo)) {
                    // user is associated and then redirected to his openid
                    // provider
                    // for authentication
                    LOGGER.debug("User is Associated Redirecting to his/her OpenId Provider. RelyingParty().associateAndAuthenticate=TRUE"
                            +" getIdentifier "+ openIdUser.getIdentifier() + " claimedId"
                            + openIdUser.getClaimedId());
                    return;// false;
                }
            } catch (UnknownHostException e) {
                errorMsg = ID_NOT_FOUND_MSG;
                LOGGER.error("", e);
            } catch (FileNotFoundException e) {
                errorMsg = ID_NOT_FOUND_MSG;
                LOGGER.error("", e);
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = DEFAULT_ERROR_MSG;
                LOGGER.error("", e);
            }
            request.setAttribute(ERROR_MSG_ATTR, errorMsg);
            LOGGER.error("openId or local login related error " + errorMsg + " forward to"
                    + getForwardUriLoginPage());
            getForwardUriHandler().handle(getForwardUriLoginPage(), request,
                    response);

        }
    }

    /*
      * static { RelyingParty.getInstance() .addListener(new AxSchemaExtension()
      * .addExchange("email") .addExchange("country") .addExchange("language") );
      * }
      *
      * public void doGet(HttpServletRequest request, HttpServletResponse
      * response) throws IOException, ServletException { OpenIdUser user =
      * (OpenIdUser)request.getAttribute(OpenIdUser.ATTR_NAME);
      * Map<String,String> axschema = AxSchemaExtension.get(user); String email =
      * axschema.get("email"); String country = axschema.get("country"); String
      * language = axschema.get("language");
      *
      * // do something with your user's data }
      */

    /**
     * Pluggable handler to dispatch the request to a view/template.
     */
    public interface ForwardUriHandler {
        /**
         * Dispatches the request to a view/template.
         */
        public void handle(String forwardUri, HttpServletRequest request,
                           HttpServletResponse response) throws IOException,
                ServletException;

    }

}
