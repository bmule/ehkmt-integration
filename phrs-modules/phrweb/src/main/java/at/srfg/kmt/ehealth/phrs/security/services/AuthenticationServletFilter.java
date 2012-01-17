package at.srfg.kmt.ehealth.phrs.security.services;

//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.util.ClassLoaderUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated 
 * See <code>LoginServlet</code> There is a problem with JSF Viewscope and 
 * using the servlet filter
 * 
 * A servlet filter that forwards users to the login page if they are not
 * authenticated and redirects the user to their openid provider once they've
 * submitted their openid identifier. The required web.xml configuration is the
 * init-parameter "forwardUri".
 * 
 * Based on code sample from:
 * 
 * @author David Yu
 * @created Jan 8, 2009 OpenIdServletFilter
 * 
 *          Note: OpenIdContextListener sets up the RelyingParty and listeners
 *          for particular OpenId attributes,etc and stores in ServletContext
 *          under class name _relyingParty = (RelyingParty)
 *          config.getServletContext
 *          ().getAttribute(RelyingParty.class.getName());
 * 
 *          See Axschema properties and sreq properties for relevant OpenId
 *          attributes to extract, set up listener
 * 
 *          Relevant PhrsContants.OPEN_ID_PARAM_ must match the 'alias' names
 *          found in: resources/com.dyuproject.openid.ext/axschema.properties
 *          resources/com.dyuproject.openid.ext/sreg.properties
 */

public class AuthenticationServletFilter implements Filter {
	private final static Logger LOGGER = LoggerFactory
			.getLogger(AuthenticationServletFilter.class);
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
	protected ConfigurationService configService;
	protected String _forwardUri;
	// OpenIdContextListener sets up the RelyingParty and listeners and stores
	// in ServletContext
	protected RelyingParty _relyingParty;

	protected ForwardUriHandler _forwardHandler;

	/**
	 * This method is called by the servlet container to configure this filter;
	 * The init parameter "forwardUri" is required. The relying party used will
	 * be the default {@link RelyingParty#getInstance() instance} if it is not
	 * found in the servlet context attributes.
	 */
	public void init(FilterConfig config) throws ServletException {
		configService = ConfigurationService.getInstance();
		_forwardUri = configService.getProperty("login_openid_forwardUri");
		if (_forwardUri == null)
			_forwardUri = config.getInitParameter("forwardUri");
		if (_forwardUri == null)
			throw new ServletException("forwardUri must not be null.");

		// resolve from ServletContext
		_relyingParty = (RelyingParty) config.getServletContext().getAttribute(
				RelyingParty.class.getName());

		// default config if null
		if (_relyingParty == null)
			_relyingParty = RelyingParty.getInstance();

		String forwardUriHandlerParam = config
				.getInitParameter("forwardUriHandler");
		// not provided in web.xml
		if (forwardUriHandlerParam != null) {
			try {
				_forwardHandler = ClassLoaderUtil.newInstance(
						forwardUriHandlerParam,
						AuthenticationServletFilter.class);// OpenIdServletFilter.class);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		} else
			_forwardHandler = DEFAULT_FORWARD_URI_HANDLER;
	}

	/**
	 * Gets the configured forward uri.
	 */
	public String getForwardUri() {
		return _forwardUri;
	}

	/**
	 * Gets the configured relying party.
	 */
	public RelyingParty getRelyingParty() {
		return _relyingParty;
	}

	/**
	 * Delegates to the filter chain if the user associated with this request is
	 * authenticated.
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if (handle((HttpServletRequest) req, (HttpServletResponse) res))
			chain.doFilter(req, res);
	}

	public void destroy() {

	}

	/**
	 * Returns true if the user associated with this request is authenticated.
	 */
	public boolean handle(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		return handle(request, response, _relyingParty, _forwardHandler,
				_forwardUri);
	}

	/**
	 * Returns true if the user associated with this request is authenticated.
	 */
	public static boolean handle(HttpServletRequest request,
			HttpServletResponse response, String forwardUri)
			throws IOException, ServletException {
		return handle(request, response, RelyingParty.getInstance(),
				DEFAULT_FORWARD_URI_HANDLER, forwardUri);
	}

	/*
	 * public static boolean handleLocalLogin(HttpServletRequest request,
	 * HttpServletResponse response, RelyingParty relyingParty,
	 * ForwardUriHandler forwardUriHandler, String forwardUri) throws
	 * IOException, ServletException {
	 * 
	 * // param username return true; }
	 */
	/*
	 * Must know LoginWith icardea uses a short name and we must distinguish
	 * either icardea, local phrs user name, google or yahoo All local accounts
	 * phradmin, phruser
	 * 
	 * @param request
	 * 
	 * @param relyingParty
	 * 
	 * @return
	 * 
	 * public static OpenIdUser doDiscover(HttpServletRequest request,
	 * RelyingParty relyingParty) { OpenIdUser user = null; try { // check
	 * loginWith String discoverWith = request
	 * .getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN_WITH);
	 * 
	 * if (discoverWith != null && discoverWith.length() > 2) { //
	 * ConfigurationService Also openId related properties // Must update
	 * identifier in request! boolean isLocalLogin =
	 * preDiscoveryLoginWith(request); user = relyingParty.discover(request);
	 * 
	 * 
	 * 
	 * } else { // Uses identifier_select.properties to discover google, yahoo,
	 * // etc user = relyingParty.discover(request); }
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace(); } return user; }
	 */



	/**
	 * Handles the authentication filter check.
	 * 
	 * 
	 * 1. Check the actual user session which might already contain an
	 * authentication token. If session is invalid or or the session
	 * authentication token missing, then require login. 2. Look for login
	 * parameters, then the login form was submitted and is handled for either a
	 * local login or an OpenID login
	 * 
	 * Handle a login scenario local or OpenID.
	 * 
	 * If this is not a local login, check the OpenID relyingParty. the
	 * RelyingParty.isAuthenticated might still be true and the session is
	 * reinitialized with OpenId attributes
	 * 
	 * However, if ReplyingParty.isAuthorized=true, then recreate the session
	 * and add the authorization tokens.
	 * 
	 * The OpenID authentication is handled here and the OP is directed to this
	 * method also
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param relyingParty
	 * @param forwardUriHandler
	 * @param forwardUri
	 * @return Returns true if the user associated with this request is
	 *         authenticated. The user can the access the restricted pages
	 *         (filter servlet)
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	public static boolean handle(HttpServletRequest request,
			HttpServletResponse response, RelyingParty relyingParty,
			ForwardUriHandler forwardUriHandler, String forwardUri)

	throws IOException, ServletException {
		String errorMsg = DEFAULT_ERROR_MSG;
		/*
		 * Check session exist and logged in ? Do not try the FacesContext yet,
		 * send the HttpSerlvetRequest and try to get the session and login
		 * status. If there was already a local login or a successful OpenId
		 * login then the session holds the authentication token and no need to
		 * continue checking openID
		 */
		String requestUri = request.getRequestURI();
		System.out.println("request.getRequestURI()=" + requestUri);
		if (UserSessionService.loggedIn(request)) {// OK
			// response.sendRedirect(request.getRequestURI());
			return true;
		}
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
			if (loginFormIdentifier != null && loginFormIdentifier.length() > 3) {

				// If LoginWith set (must), then set up the request with one
				// designated OpenID provider instead of using default discovery
				// over many providers
				isLocalLogin = AuthenticationUtil.determineLoginScenario(request);

			} else { // if login form, the form javascript should validate

			}

			if (isLocalLogin) {
				// local login, bypass OpenID
				// handle local user login. For iCardea testing, this is very
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
					return true;// authenticated
				} else {
					// return to login form
					request.setAttribute(ERROR_MSG_ATTR, errorMsg);
					System.out.println("isLocalLogin " + forwardUri);
					forwardUriHandler.handle(forwardUri, request, response);
					return false;// authenticated
				}

			} else {

				openIdUser = relyingParty.discover(request);

				if (openIdUser == null) {

					if (RelyingParty.isAuthResponse(request)) {// authentication
						// CHECK session! // timeout
						System.out
								.println("RelyingParty.isAuthResponse authentication, user null, timeout ,redirect to request.getRequestURI()="
										+ request.getRequestURI());
						response.sendRedirect(request.getRequestURI());
					} else {
						// set error msg if the openid_identifier is not
						// resolved.
						if (request.getParameter(relyingParty
								.getIdentifierParameter()) != null)
							request.setAttribute(ERROR_MSG_ATTR, errorMsg);

						System.out.println("new user login forwardUri="
								+ forwardUri);
						// forward to user login
						forwardUriHandler.handle(forwardUri, request, response);
					}
					return false;
				}
			}

			// ReplyingParty has onAuthenticate listener that sets up the user,
			// there should already be session?
			if (openIdUser.isAuthenticated()) {
				// user already authenticated, however, the session token was
				// not found. We already checked this.
				System.out
						.println("user.isAuthenticated, but session needs updating ");
				// setup session with OpenID. We did not find an authentication
				// token in the session or the session was not already created
				UserSessionService
						.managePhrUserSessionByOpenIdUserLoginScenario(
								openIdUser, request);
				return true;
			}

			if (openIdUser.isAssociated()
					&& RelyingParty.isAuthResponse(request)) {
				// verify authentication
				if (relyingParty.verifyAuth(openIdUser, request, response)) {
					// authenticated
					// redirect to home to remove the query params
					response.sendRedirect(request.getRequestURI());
				} else {
					// failed verification
					System.out.println("failed verification ");
					forwardUriHandler.handle(forwardUri, request, response);
				}
				return false;
			}

			// associate and authenticate user
			StringBuffer url = request.getRequestURL();
			String trustRoot = url.substring(0, url.indexOf(SLASH, 9));
			String realm = url.substring(0, url.lastIndexOf(SLASH));
			String returnTo = url.toString();
			System.out.println("forward UriHandler ");
			if (relyingParty.associateAndAuthenticate(openIdUser, request,
					response, trustRoot, realm, returnTo)) {
				// user is associated and then redirected to his openid provider
				// for authentication
				return false;
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
		System.out.println("forward UriHandler " + forwardUri);
		forwardUriHandler.handle(forwardUri, request, response);
		return false;
	}

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
