package at.srfg.kmt.ehealth.phrs.security.services;

import javax.servlet.http.HttpServletRequest;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;

import com.dyuproject.openid.OpenIdUser;
import com.dyuproject.openid.YadisDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationUtil {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(AuthenticationUtil.class);

//    openid.claimed_id.server= https://icardea-server.lksdom21.lks.local/idp/
//    OPENID_USER_ID_PREFIX_KEY
//       openid.claimed_id.user= https://icardea-server.lksdom21.lks.local/idp/u
//    openid.identity.discovery= https://icardea-server.lksdom21.lks.local/idp
//    openid.claimed_id.discovery= https://icardea-server.lksdom21.lks.local/idp

	public static boolean determineLoginScenario(HttpServletRequest request) {

		boolean localLogin = false;

		String loginWith = request
				.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN_WITH);
		String userLoginIdentifier = request
				.getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN);
        if(userLoginIdentifier==null) userLoginIdentifier="";
		String username = request.getParameter("username");
        //Bypass discovery,
		if (loginWith != null) {
			// If the ui supplies a LoginWithGoogle or LoginWithYahoo
			// link/button,
			// this will speed up the openid process by skipping discovery.

			// The override is done by adding the OpenIdUser to the request
			// attribute.
            OpenIdUser openIdUser =null;
            //Setup to make the discovery request, OP will then redirect user to google authentication page
            //DyuOpenID includes the request attribute from the login form openid_identifier

//          "identifier" is the first URL used in the discovery process in order to get the OP endpoint
//          "openIdServer" is the endpoint
//          OpenIdUser populate(String identifier, String claimedId, String openIdServer,
 //                   and also String openIdDelegate)

			if (loginWith.equals("google")) {
                //by pass discovery process:

                //
                openIdUser = OpenIdUser.populate(
						"https://www.google.com/accounts/o8/id",// OP identifier,
						YadisDiscovery.IDENTIFIER_SELECT,
						"https://www.google.com/accounts/o8/ud");

                request.setAttribute(OpenIdUser.ATTR_NAME, openIdUser);
// Example login authenication request as URL from http://code.google.com/apis/accounts/docs/OpenID.html#endpoint
//  OP identifier              https://www.google.com/accounts/o8/id
//                ?openid.ns=http://specs.openid.net/auth/2.0
//                &openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select
//                &openid.identity=http://specs.openid.net/auth/2.0/identifier_select
//                &openid.return_to=http://www.example.com/checkauth
//                &openid.realm=http://www.example.com/
//                &openid.assoc_handle=ABSmpf6DNMw
//                        &openid.mode=checkid_setup
//
//                Sample response from discovery process
// The example below illustrates a redirect after a successful user log-in and confirmation:
//                http://www.example.com/checkauth
//                ?openid.ns=http://specs.openid.net/auth/2.0
//                &openid.mode=id_res
//                        &openid.op_endpoint=https://www.google.com/accounts/o8/ud
//                &openid.response_nonce=2008-09-18T04:14:41Zt6shNlcz-MBdaw
//                        &openid.return_to=http://www.example.com:8080/checkauth
//                &openid.assoc_handle=ABSmpf6DNMw
//                        &openid.signed=op_endpoint,claimed_id,identity,return_to,response_nonce,assoc_handle
//                        &openid.sig=s/gfiWSVLBQcmkjvsKvbIShczH2NOisjzBLZOsfizkI=
//                &openid.identity=https://www.google.com/accounts/o8/id/id=ACyQatixLeLODscWvwqsCXWQ2sa3RRaBhaKTkcsvUElI6tNHIQ1_egX_wt1x3fAY983DpW4UQV_U
//                &openid.claimed_id=https://www.google.com/accounts/o8/id/id=ACyQatixLeLODscWvwqsCXWQ2sa3RRaBhaKTkcsvUElI6tNHIQ1_egX_wt1x3fAY983DpW4UQV_U

			} else if (loginWith.equals("yahoo")) {
				openIdUser = OpenIdUser.populate(
						"http://yahoo.com/",// identifier,
						YadisDiscovery.IDENTIFIER_SELECT,
						"https://open.login.yahooapis.com/openid/op/auth");

                request.setAttribute(OpenIdUser.ATTR_NAME, openIdUser);
			}
			// http://wiki.openid.net/w/page/12995226/Run%20your%20own%20identity%20server
			/*
			 * Creates a new OpenIdUser pre-populated with discovery data (to
			 * skip discovery). public static OpenIdUser populate(String
			 * identifier, String claimedId, String openIdServer){ return new
			 * OpenIdUser(identifier, claimedId, openIdServer, null); }
			 */
 //			else if (loginWith.equals("icardea") || userLoginIdentifier.contains("/idp")) {                       
			else if (loginWith.equals("icardea") || userLoginIdentifier.contains("/idp")) {

				String openIdUserName = ConfigurationService.getInstance()
						.makeIcardeaOpenIdentifier(username);
				// .makeIcardeaOpenIdentifier(identifier);

				String discoveredOpIdentifier = ConfigurationService.getInstance()
						.getProperty(PhrsConstants.OPENID_DISCOVERY_IDENTIFIER_KEY,
								"https://127.0.0.1/idp");

                        String discoveredOpClaimedId = ConfigurationService.getInstance()
                        .getProperty(PhrsConstants.OPENID_DISCOVERY_CLAIM_ID_KEY,
                                "https://127.0.0.1/idp");

				openIdUser = OpenIdUser.populate(
                        discoveredOpIdentifier,
						YadisDiscovery.IDENTIFIER_SELECT,
                        discoveredOpClaimedId);


                request.setAttribute(OpenIdUser.ATTR_NAME, openIdUser);

			} else if (loginWith.equals("phrsopenid")) {

				openIdUser = OpenIdUser.populate(
						"http://localhost:8080/provider",
						YadisDiscovery.IDENTIFIER_SELECT,
						"http://localhost:8080/provider");

				request.setAttribute(OpenIdUser.ATTR_NAME, openIdUser);

			} else if (loginWith.equals("local")) {

				localLogin = true;
			}

            if(openIdUser !=null){
                LOGGER.debug("Login Form - loginWith = "+loginWith
                        + openIdUser.getIdentifier()
                        + " claimedId" + openIdUser.getClaimedId());
            }else {
                if(loginWith.equals("icardea") || userLoginIdentifier.contains("/idp")){
                    LOGGER.debug("Login Form - loginWith = icardea");
                } else {
                LOGGER.debug("Login Form - loginWith = "+loginWith);
                }
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

