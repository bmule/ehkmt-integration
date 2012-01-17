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

package at.srfg.kmt.ehealth.phrs.usermgt.openid.dyu.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.dyuproject.openid.OpenIdServletFilter;
import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.RelyingParty;
import com.dyuproject.openid.YadisDiscovery;
import com.dyuproject.openid.ext.AxSchemaExtension;
import com.dyuproject.openid.ext.SRegExtension;
import com.dyuproject.util.http.UrlEncodedParameterMap;

/**
 * Home Servlet. If authenticated, goes to the home page. If not, goes to the
 * login page.
 * 
 * @author David Yu
 * @created Sep 22, 2008
 */
@SuppressWarnings("serial")
public class HomeViaOpenIDServlet extends HttpServlet {
	/*
	 * In JSF, a menu click could
	 * a. leads user to login page, then a redirect in servlet must be to the home.xhtml or index.html
	 * page with menubar enabled OR home.xhtml or index.xhtml shows login or welcome
	 * 
	 * b. or  popup feature JSF index.xhtml (remove link to jsf/home.xhtml  
	 * 		if authenticated, then update the index page and menu bar area with log out(?) or login
	 * 		default page to openID to ... returns to home.xhtml and asks user to login. 
	 * 
	
	 * Alias = test= http://axschema.org/icardea/test protocolrole=
	 * http://axschema.org/icardea/protocolrole protocolid=
	 * http://axschema.org/icardea/protocolid protocolns=
	 * http://axschema.org/icardea/protocolns Must use the property files loaded
	 * from the PHRs resources, not files packed in Dyuproject JAR file
	 */
	String forwardRedirectIsAuthenticatedToPage="/jsf/home.xhtml";
	String formwardRedirectFilteredDirectory="/jsf/";
	String forwardRedirectLoginPage="/WEB-INF/views/jsp/login.jsp";
	
	static {
		RelyingParty.getInstance()
				.addListener(
						new SRegExtension().addExchange("email")
								.addExchange("country").addExchange("language")
								.addExchange("test").addExchange("protocolid")
								.addExchange("protocolns")
								.addExchange("protocolrole"))
				.addListener(
						new AxSchemaExtension().addExchange("email")
								.addExchange("country").addExchange("language")
								.addExchange("test").addExchange("protocolid")
								.addExchange("protocolns")
								.addExchange("protocolrole"))
				/*
				 * .addListener( new SRegExtension().addExchange("email")
				 * .addExchange("country").addExchange("language"))
				 * .addListener( new AxSchemaExtension().addExchange("email")
				 * .addExchange("country").addExchange("language")
				 * .addExchange("role").addExchange("protocolID")
				 * .addExchange("protocolId")
				 * .addExchange("username").addExchange("name")
				 * .addExchange("firstname") .addExchange("lastname"))
				 */
				.addListener(new RelyingParty.Listener() {
					public void onDiscovery(OpenIdUser user,
							HttpServletRequest request) {
						System.err.println("discovered user: "
								+ user.getClaimedId());
					}

					public void onPreAuthenticate(OpenIdUser user,
							HttpServletRequest request,
							UrlEncodedParameterMap params) {
						
						System.err.println("pre-authenticate user: "
								+ user.getClaimedId());
					}

					public void onAuthenticate(OpenIdUser user,
							HttpServletRequest request) {
						
						System.err.println("newly authenticated user: "
								+ user.getIdentity());
						
						Map<String, String> sreg = SRegExtension.remove(user);
						Map<String, String> axschema = AxSchemaExtension
								.remove(user);
						if (sreg != null && !sreg.isEmpty()) {
							System.err.println("sreg: " + sreg);
							user.setAttribute("info", sreg);
						} else if (axschema != null && !axschema.isEmpty()) {
							System.err.println("axschema: " + axschema);
							user.setAttribute("info", axschema);
						}
					}

					public void onAccess(OpenIdUser user,
							HttpServletRequest request) {
						System.err.println("user access: " + user.getIdentity());
						System.err.println("info: " + user.getAttribute("info"));
					}
				});
	}

	RelyingParty _relyingParty = RelyingParty.getInstance();

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String loginWith = request.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN_WITH);

		if (loginWith != null)
			loginWith = "icardea";

		if (loginWith != null) {
			// If the ui supplies a LoginWithGoogle or LoginWithYahoo
			// link/button,
			// this will speed up the openid process by skipping discovery.
			
			// The override is done by adding the OpenIdUser to the request
			// attribute.
			if (loginWith.equals("google")) {
				OpenIdUser user = OpenIdUser.populate(
						"https://www.google.com/accounts/o8/id",
						YadisDiscovery.IDENTIFIER_SELECT,
						"https://www.google.com/accounts/o8/ud");
				request.setAttribute(OpenIdUser.ATTR_NAME, user);

			} else if (loginWith.equals("yahoo")) {
				OpenIdUser user = OpenIdUser.populate("http://yahoo.com/",
						YadisDiscovery.IDENTIFIER_SELECT,
						"https://open.login.yahooapis.com/openid/op/auth");
				request.setAttribute(OpenIdUser.ATTR_NAME, user);
			}
			//http://wiki.openid.net/w/page/12995226/Run%20your%20own%20identity%20server
			else if (loginWith.equals("icardea")) { 
			
				OpenIdUser user =
				 OpenIdUser.populate( "http://localhost:8080/provider",
				 YadisDiscovery.IDENTIFIER_SELECT,
				 "http://localhost:8080/provider");
				 request.setAttribute(OpenIdUser.ATTR_NAME, user); 
				 
			}	else if (loginWith.equals("test")) { 
				
				OpenIdUser user =
					OpenIdUser.populate( "http://localhost:8080/provider",
					YadisDiscovery.IDENTIFIER_SELECT,
					"http://localhost:8080/provider");
					request.setAttribute(OpenIdUser.ATTR_NAME, user); 
			} else if (loginWith.contains("://")) { 
				
				OpenIdUser user =
					OpenIdUser.populate( loginWith,
					YadisDiscovery.IDENTIFIER_SELECT,
					loginWith);
					request.setAttribute(OpenIdUser.ATTR_NAME, user); 
			}

		}

		String errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;
		try {
			// request might contain abovve overrides OpenIdUser.ATTR_NAME
			OpenIdUser user = _relyingParty.discover(request);
			if (user == null) {
				if (RelyingParty.isAuthResponse(request)) {
					// authentication timeout
					response.sendRedirect(request.getRequestURI());
				} else {
					// set error msg if the openid_identifier is not resolved.
					if (request.getParameter(_relyingParty
							.getIdentifierParameter()) != null)
						request.setAttribute(
								OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);

					// new user
					request.getRequestDispatcher(forwardRedirectLoginPage)
							.forward(request, response);
				}
				return;
			}

			/*
			 * isAuthenticated isAssociated with provider
			 */
			if (user.isAuthenticated()) {
				// user already authenticated
				request.getRequestDispatcher(forwardRedirectIsAuthenticatedToPage).forward(request,
						response);
				return;
			}

			if (user.isAssociated() && RelyingParty.isAuthResponse(request)) {
				// verify authentication
				if (_relyingParty.verifyAuth(user, request, response)) {
					// authenticated
					// redirect to home to remove the query params instead of
					// doing:
					// request.getRequestDispatcher("/home.jsp").forward(request,
					// response);
					response.sendRedirect(request.getContextPath() + formwardRedirectFilteredDirectory);//"/home/");
				} else {
					// failed verification
					request.getRequestDispatcher(forwardRedirectLoginPage).forward(request,
							response);
				}
				return;
			}

			// associate and authenticate user
			StringBuffer url = request.getRequestURL();
			String trustRoot = url.substring(0, url.indexOf("/", 9));
			String realm = url.substring(0, url.lastIndexOf("/"));
			String returnTo = url.toString();
			
			if (_relyingParty.associateAndAuthenticate(user, request, response,
					trustRoot, realm, returnTo)) {
				// successful association
				return;
			}
		} catch (UnknownHostException uhe) {
			System.err.println("not found");
			errorMsg = OpenIdServletFilter.ID_NOT_FOUND_MSG;
		} catch (FileNotFoundException fnfe) {
			System.err.println("could not be resolved");
			errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = OpenIdServletFilter.DEFAULT_ERROR_MSG;
		}
		
		request.setAttribute(OpenIdServletFilter.ERROR_MSG_ATTR, errorMsg);
		request.getRequestDispatcher(forwardRedirectLoginPage).forward(request, response);
	}

	/*
	 * static { RelyingParty.getInstance() .addListener(new AxSchemaExtension()
	 * .addExchange("email") .addExchange("country") .addExchange("language") );
	 * }
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		OpenIdUser user = (OpenIdUser) request
				.getAttribute(OpenIdUser.ATTR_NAME);
		Map<String, String> axschema = AxSchemaExtension.get(user);
		String email = axschema.get("email");
		String country = axschema.get("country");
		String language = axschema.get("language");

		// do something with your user's data
	} */

	protected boolean handleAuthenticatedUser(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException {
		
		boolean flag=false;
		OpenIdUser user = (OpenIdUser)request.getAttribute(OpenIdUser.ATTR_NAME);
        Map<String,String> axschema = AxSchemaExtension.get(user);
        
        String email = axschema.get(PhrsConstants.OPEN_ID_PARAM_EMAIL);
        System.out.println(PhrsConstants.OPEN_ID_PARAM_EMAIL+" "+email);
        
        String country = axschema.get(PhrsConstants.OPEN_ID_PARAM_COUNTRY);  
        System.out.println(PhrsConstants.OPEN_ID_PARAM_COUNTRY+" "+country);
        
        String language = axschema.get(PhrsConstants.OPEN_ID_PARAM_LANGUAGE);
        System.out.println(PhrsConstants.OPEN_ID_PARAM_LANGUAGE+" "+language);
        
        String test = axschema.get("test");

        String protocolrole = axschema.get(PhrsConstants.OPEN_ID_PARAM_ROLE);
        System.out.println(PhrsConstants.OPEN_ID_PARAM_ROLE+" "+protocolrole);
       
        System.out.println("test "+test);
 
        // do something with your user's data
        
        return flag;
	}

}
