package at.srfg.kmt.ehealth.phrs.security.services;

import javax.servlet.http.HttpServletRequest;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.YadisDiscovery;

public class AuthenticationUtil {

	public static boolean determineLoginScenario(HttpServletRequest request) {

		boolean localLogin = false;

		String loginWith = request
				.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN_WITH);
		String identifier = request
				.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN);
		String username = request.getParameter("username");

		if (loginWith != null) {
			// If the ui supplies a LoginWithGoogle or LoginWithYahoo
			// link/button,
			// this will speed up the openid process by skipping discovery.

			// The override is done by adding the OpenIdUser to the request
			// attribute.
			if (loginWith.equals("google")) {
				OpenIdUser user = OpenIdUser.populate(
						"https://www.google.com/accounts/o8/id",// identifier,
						YadisDiscovery.IDENTIFIER_SELECT,
						"https://www.google.com/accounts/o8/ud");
				request.setAttribute(OpenIdUser.ATTR_NAME, user);

			} else if (loginWith.equals("yahoo")) {
				OpenIdUser user = OpenIdUser.populate(
						"http://yahoo.com/",// identifier,
						YadisDiscovery.IDENTIFIER_SELECT,
						"https://open.login.yahooapis.com/openid/op/auth");
				request.setAttribute(OpenIdUser.ATTR_NAME, user);
			}
			// http://wiki.openid.net/w/page/12995226/Run%20your%20own%20identity%20server
			/*
			 * Creates a new OpenIdUser pre-populated with discovery data (to
			 * skip discovery). public static OpenIdUser populate(String
			 * identifier, String claimedId, String openIdServer){ return new
			 * OpenIdUser(identifier, claimedId, openIdServer, null); }
			 */
			else if (loginWith.equals("icardea") || loginWith.contains("/idp")) {

				String openIdUserName = ConfigurationService.getInstance()
						.makeIcardeaOpenIdentifier(username);
				// .makeIcardeaOpenIdentifier(identifier);

				String provider = ConfigurationService.getInstance()
						.getProperty(PhrsConstants.OPENID_ICARDEA_PROVIDER_KEY,
								"https://localhost:8443/idp/");

				OpenIdUser user = OpenIdUser.populate(provider,// openIdUserName,
						YadisDiscovery.IDENTIFIER_SELECT, provider);

				request.setAttribute(OpenIdUser.ATTR_NAME, user);

			} else if (loginWith.equals("phrsopenid")) {

				OpenIdUser user = OpenIdUser.populate(
						"http://localhost:8080/provider",
						YadisDiscovery.IDENTIFIER_SELECT,
						"http://localhost:8080/provider");

				request.setAttribute(OpenIdUser.ATTR_NAME, user);

			} else if (loginWith.equals("local")) {

				System.out.println("local login");
				localLogin = true;
			}

		}
		return localLogin;

	}
	public static boolean handlingLoginForm(HttpServletRequest request) {
		boolean flag = false;

		String loginWith = request
				.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN_WITH);
		// String identifier = request
		// .getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN);
		String username = request.getParameter("username");

		if (loginWith != null || username != null)
			flag = true;

		return flag;
	}
}

