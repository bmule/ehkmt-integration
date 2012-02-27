package at.srfg.kmt.ehealth.phrs.security.services.login;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.jsf.utils.WebUtil;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LoginUtils {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(LoginUtils.class);

    /**
     * @param input
     * @return
     */
    public static String transformRole(String input) {
        String transformed = null;
        if (input != null) {
            String theRole = input.toLowerCase();
            if (theRole.contains("doctor")) {
                transformed = PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DOCTOR;

            } else if (theRole.contains("nurse")) {
                transformed = PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE;

            }
        }

        return transformed;
    }

    /**
     * Normally, we expect one string, but this OP provides a list of roles
     *
     * @param roleObj
     * @return
     */
    public static String processRole(Object roleObj) {
        String theRole = null;

        try {
            if (roleObj != null) {
                if (roleObj instanceof String) {
                    theRole = transformRole((String) roleObj);
                } else {
                    //
                    List roles = (List) roleObj;
                    if (roles.size() > 0) {


                        try {
                            for (Object item : roles) {
                                if (item != null && item instanceof String) {
                                    try {
                                        String transformed = transformRole((String) item);
                                        if (transformed != null) {
                                            theRole = transformed;
                                            break;
                                        }
                                    } catch (Exception e) {
                                        LOGGER.error("error processing Role ", e);
                                    }
                                }

                            }
                        } catch (Exception e) {
                            LOGGER.error("error processing Role ", e);
                        }

                        if (theRole == null) {
                            Object out = roles.get(0);
                            theRole = (String) out;
                        }

                        LOGGER.debug("OpenID  role =" + theRole + " from list size: " + roles.size());

                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("error processing Role ", e);
        }

        return theRole;
    }

    public static String getEndpointApplicationHome() {
        return ConfigurationService.getInstance().getProperty("auth.application.home.uri", "/index.xhtml");
    }

    public static String getEndpointLoginPage() {
        return ConfigurationService.getInstance().getProperty("auth.application.login.uri", "/WEB-INF/views/jsp/login.jsp");

    }

    /**
     * This is the application start,  /index.html or /jsf/home.xhtml. Send to /index.html and
     * it will show the login status etc
     *
     * @param request
     * @return
     */
    public static String getEndpointApplicationHome(HttpServletRequest request) {
        String uri = ConfigurationService.getInstance().getProperty("auth.application.home.uri", "/index.xhtml");

        String endpoint = request.getContextPath() + uri;

        LOGGER.debug("getEndpointApplicationHome endpoint=" + endpoint);
        return endpoint;

    }

    /**
     * This page will display whether the user successfully logged in or not
     *
     * @param request
     * @return
     */
    public static String getEndpointLoginPage(HttpServletRequest request) {
        String uri = ConfigurationService.getInstance().getProperty("auth.application.login.uri", "/index.xhtml");

        String endpoint = request.getContextPath()
                + uri;
        LOGGER.debug("getEndpointApplicationHome endpoint=" + endpoint);
        return endpoint;

    }

    /**
     * Determines whether the username conforms to a local login name for demos
     * LoginType can indicate whether this is a demo or not...or an OpenId based login
     * prefix: phr
     * or username = nurse
     *
     * @param username
     * @param loginType local_1 -> demo based local login. No password needed, ...
     * @return
     */
    public static boolean isLocalLogin(String username, String loginType) {
       //String loginType = request
        //        .getParameter(PhrsConstants.OPEN_ID_PARAM_NAME_LOGIN_WITH);
        //if (loginType != null) {
        //}

        return isLocalLogin(username);
    }

    /**
     *
     * @param username
     * @return
     */
    public static boolean isLocalLogin(String username) {
        if (username == null) return false;

        boolean isLocal = false;

        if (username.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST)
                || username.startsWith(PhrsConstants.AUTHORIZE_USER_ADMIN)
                || username.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_AUTO_USER)
                || username.startsWith(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)
                || username.equals(ConfigurationService.getInstance().getProperty("test.user.1.login.id", "phrtest"))) {
            isLocal = true;
        }
        LOGGER.debug("isLocalLogin =" + isLocal + " username=" + username);
        return isLocal;
    }
    //TODO make message labels for OpenId exception codes.  message codes

    public String getMessageLabel(String messageCode, Locale locale) {

        return messageCode;
    }
    public String getMessageLabel(String messageCode) {

        return messageCode;
    }
    public List getMessageLabel(List<String> messageCodes, Locale locale) {
        List<String> labels = new ArrayList<String>();
        for (String msg : messageCodes) {
            String label = this.getMessageLabel(msg, locale);
            if (label != null && !label.isEmpty()) labels.add(label);
        }
        return labels;
    }

    public static String getOpenIdReturnToUrl() {
        return ConfigurationService.getInstance().getProperty("openid.returnurl.default", "https://icardea-server.lksdom21.lks.local:6060/phrweb/openid");

    }

    public static String getOpenIdReturnToUrl(FacesContext context) {
        String appReturnUrl = null;
        if (context != null) {
            appReturnUrl = WebUtil.getUrl(context, "/openid");

            if (appReturnUrl != null) {
                if (appReturnUrl.contains("localhost")
                        || appReturnUrl.contains("127.0.0.1")) {
                    appReturnUrl = null;
                }
            }
        }

        if (appReturnUrl != null) return appReturnUrl;
        return LoginUtils.getOpenIdReturnToUrl();
    }

}