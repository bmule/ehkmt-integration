package at.srfg.kmt.ehealth.phrs.security.services.login;


import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

//import flex.messaging.FlexContext;
//import flex.messaging.FlexSession;


/**
 * Consolidates business logic from the UI code for Registration activities.
 * <p/>
 * Most of this code modeled after ConsumerServlet, part of the openid4java
 * sample code available at
 * http://code.google.com/p/openid4java/wiki/SampleConsumer. Some of this code
 * was outright copied :->.
 *
 * @author J Steven Perry
 * @author http://makotoconsulting.com
 */

public class LoginServiceImpl implements LoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class.getName());

    public String createRedirect(String username) {
        return createRedirect(username, RegistrationService.getReturnToUrl());
    }

    public String createRedirect(String username, String returnUrl) {
        ResourceBundle properties = ResourceBundle.getBundle("icardea");
        String salkServer = properties.getString("salk.server");

        //FIXXME phrs use profiles
        boolean specialUsage = new Boolean(ResourceBundle.getBundle("icardea")
                .getString("salk.usage")).booleanValue();

        LOGGER.debug("special openId usage " + specialUsage);

        if (specialUsage == true) {
            username = salkServer + "/idp/u=" + username; //only valid for SALK server
        }
        LOGGER.debug("LoginServiceImpl " + "##############AT Discovery for: " + username);
        DiscoveryInformation discovery = RegistrationService
                .performDiscoveryOnUserSuppliedIdentifier(username);

        //String url = RegistrationService.getReturnToUrl();
        if (returnUrl == null) returnUrl = RegistrationService.getReturnToUrl();

        LOGGER.debug("LoginServiceImpl " + "##############AT return url:" + returnUrl);

        AuthRequest authRequest = RegistrationService.createOpenIdAuthRequest(discovery, returnUrl);

        String redirectUrl = authRequest.getDestinationUrl(true);

        return redirectUrl;
    }

    public RegistrationModel handleValidation() {
        LOGGER.debug("LoginServiceImpl " + "##############AT HANDLEVALIDATION");
        RegistrationModel model = new RegistrationModel();
//		FlexSession mySession= FlexContext.getFlexSession();
//		model.setIs_verified((String)mySession.getAttribute("is_verified"));
//		model.setEmailAddress((String)mySession.getAttribute("user_email"));
//		model.setOpenId((String)mySession.getAttribute("user_openid"));
//		model.setFullName((String)mySession.getAttribute("user_fullname"));
//		model.setRole((String)mySession.getAttribute("user_role"));

        model.setIs_verified(("is_verified"));

        model.setEmailAddress(("user_email"));
        model.setOpenId(("user_openid"));
        model.setFullName(("user_fullname"));
        model.setRole(("user_role"));


        //TODO sign and encrypt model
        return model;

    }

    /**
     * @deprecated Logout handled by JSF LoginBean
     *             This would actually fail in the servlet with JSF context
     *             unless we have the request or JSF context can be used
     *             Must set attribute of new session with  is_verified  false
     */
    public void doLogout() {
//		FlexSession mySession= FlexContext.getFlexSession();
        Map<String, String> params = new HashMap<String, String>();
        params.put("is_verified", "false");
        //init the new session and with the following params

        UserSessionService.sessionInit(true, params);

        //TODO control is_verified variable from Registration Model
    }


    public String getUriApplicationHome() {
        return getEndpointApplicationHome();

    }

    public String getUriLoginPage() {
        return getEndpointLoginPage();
    }


    public String getCurrentIP() {
        String hostname = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostAddress();

        } catch (UnknownHostException e) {
        }
        return hostname;
    }
    //TODO: TestLogin

    void main() {
        LoginServiceImpl logserv = new LoginServiceImpl();
        String openIdTest = ConfigurationService.getInstance().getProperty("openid.test.url", "http://kmt23.salzburgresearch.at:4545/idp/u=bob");


        String redirect = logserv.createRedirect(openIdTest);
        System.out.println("LoginServiceImpl " + redirect);

    }

    public static String getEndpointApplicationHome() {
        String uri = ConfigurationService.getInstance().getProperty("auth.application.home.uri", "/index.xhtml");

        return uri;

    }

    public static String getEndpointLoginPage() {
        String uri = ConfigurationService.getInstance().getProperty("auth.application.login.uri", "/WEB-INF/views/jsp/login.jsp");

        return uri;

    }

    public static String getEndpointApplicationHome(HttpServletRequest request) {
        String uri = ConfigurationService.getInstance().getProperty("auth.application.home.uri", "/index.xhtml");

        String endpoint = request.getContextPath() + uri;

        LOGGER.debug("getEndpointApplicationHome endpoint=" + endpoint);
        return endpoint;

    }

    public static String getEndpointLoginPage(HttpServletRequest request) {
        String uri = ConfigurationService.getInstance().getProperty("auth.application.login.uri", "/WEB-INF/views/jsp/login.jsp");

        String endpoint = request.getContextPath()
                + uri;
        LOGGER.debug("getEndpointApplicationHome endpoint=" + endpoint);
        return endpoint;

    }

    public boolean isLocalLogin(String username, String loginType) {
        boolean isLocal = false;

        //String loginType = request
        //        .getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN_WITH);
        //if (loginType != null) {
        //}

        /**
         * Check for local login
         */
        if (username != null &&
                (username.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_AUTO_USER)
                        || username.equals(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)
                        || username.equals(ConfigurationService.getInstance().getProperty("test.user.1.login.id", "phrtest")))
                ) {
            isLocal = true;
        }
        LOGGER.debug("isLocalLogin =" + isLocal + " username=" + username);
        return isLocal;
    }
}
