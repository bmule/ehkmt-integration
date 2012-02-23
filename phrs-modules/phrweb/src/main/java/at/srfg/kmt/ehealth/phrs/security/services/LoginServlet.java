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
 * Based on
 * 
 * @author David Yu
 * @created Sep 22, 2008
 */
@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

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
			.getLogger(LoginServlet.class);
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
	 * be the default {@link com.dyuproject.openid.RelyingParty#getInstance() instance} if it is not
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
		if (_relyingParty == null) {
			_relyingParty = RelyingParty.getInstance();
        }
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
		LOGGER.debug("request.getRequestURI()="
				+ defaultRedirectToApplication 
				+ " ...request.getRequestURL()="
				+ request.getRequestURL());

		// Already logged in redirect back to main index.html
		if (UserSessionService.loggedIn(request)) {// OK
			response.sendRedirect(defaultRedirectToApplication);

			// return true;
			return;
		} else {
			// there is no token, ALWAYS add authentication related tokens to
			// session. The RelyingParty might not have be invalidated properly.
			// boolean webSessionActive = UserSessionService.hasSession();

			/*
			 * try { // } catch (Exception e) { e.printStackTrace(); errorMsg =
			 * DEFAULT_ERROR_MSG; }
			 */
			boolean isLocalLogin = false;
			try {

				String loginFormIdentifier = request
						.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN);
				String username = request.getParameter("username");

				if (loginFormIdentifier == null)
					loginFormIdentifier = username;

				OpenIdUser openIdUser = null;
				// check for valid identifier
				if (loginFormIdentifier != null
						&& loginFormIdentifier.length() > 3) {

					// If LoginWith set (must), then set up the request with one
					// designated OpenID provider instead of using default
					// discovery
					// over many providers
					isLocalLogin = AuthenticationUtil
							.determineLoginScenario(request);

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
						LOGGER.error("pfu" + e);
					}

					if (pfu != null) {

						response.sendRedirect(defaultRedirectToApplication);
						return;// true
					} else {
						// return to login form
						request.setAttribute(ERROR_MSG_ATTR, errorMsg);
						LOGGER.debug("isLocalLogin username="+username+" "
								+ getForwardUriLoginPage());
						getForwardUriHandler().handle(getForwardUriLoginPage(),
								request, response);
						return;// false authenticated
					}

				}
				/*
				 * Only OpenId related, local login was handled above and forwarded or redirected with 'return'
				 */
				openIdUser = getRelyingParty().discover(request);

				if (openIdUser == null) {
					// two possibilities, this is a login request or it is the
					// OpenId returning

					if (RelyingParty.isAuthResponse(request)) {// authentication
						// openIdUser is null, user authenicated,
						// Returns true if we have a positive response from the
						// OpenID provider.
						// If the user is associated and we have an auth
						// response,
						// then we can verify the user.

						LOGGER.debug("RelyingParty.isAuthResponse authentication, user null or timeout. redirect to request.getRequestURI()="
										+ requestUri);
						// There is no session now, but the verify step should
						// lead to the onAuthentication event listener
						// where the OpenIdUser login scenario inits the session
						// and auth token, etc
						// Without the session, the redirect will lead to a page
						// without an active session
						// leave as getRequestURI(), not
						// defaultRedirectToApplication

						response.sendRedirect(requestUri);

					} else {
						// Two more possibilities: the OpenID failed
						// or the initial login request
						// set error msg if the openid_identifier is not
						// resolved.
						if (request.getParameter(getRelyingParty()
								.getIdentifierParameter()) != null)
							request.setAttribute(ERROR_MSG_ATTR, errorMsg);

						LOGGER.debug("new login forwardUri="
								+ getForwardUriLoginPage());
						// forward to user login
						getForwardUriHandler().handle(getForwardUriLoginPage(),
								request, response);
					}
					return; // false;
				}

				// ReplyingParty has onAuthenticate listener that sets up the
				// user,
				// Is there a session auth user? Not yet, setup  session
				if (openIdUser.isAuthenticated()) {
					// user already authenticated, however, the session token
					// was
					// not found. We already checked this.
					LOGGER.debug("user.isAuthenticated, but session needs updating ");
					// setup session with OpenID. We did not find an
					// authentication
					// token in the session or the session was not already
					// created
                                        //check
					UserSessionService
							.managePhrUserSessionByOpenIdUserLoginScenario(
									openIdUser, request);
                                        response.sendRedirect(defaultRedirectToApplication);
                                        
					return;// true;
				}

				if (openIdUser.isAssociated()
						&& RelyingParty.isAuthResponse(request)) {
					// verify authentication
					if (getRelyingParty().verifyAuth(openIdUser, request,
							response)) {
						// authenticated
						// redirect to home to remove the query params
                                                //check
                        UserSessionService
							.managePhrUserSessionByOpenIdUserLoginScenario(
									openIdUser, request);
						response.sendRedirect(defaultRedirectToApplication);
					} else {
						// failed verification
						

						LOGGER.debug("Failed verification, forward back to login page. identifier="
								+ openIdUser.getIdentifier() + " claimId"
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
					return;// false;
				}
			} catch (UnknownHostException uhe) {
				errorMsg = ID_NOT_FOUND_MSG;
			} catch (FileNotFoundException fnfe) {
				errorMsg = ID_NOT_FOUND_MSG;
			} catch (Exception e) {
				e.printStackTrace();
				errorMsg = DEFAULT_ERROR_MSG;
			}
			request.setAttribute(ERROR_MSG_ATTR, errorMsg);
			LOGGER.error("error " + errorMsg + " forward to"
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
