package at.srfg.kmt.ehealth.phrs.security.services.login;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService;
import org.openid4java.association.AssociationException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.MessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ResourceBundle;

public class LoginServlet extends javax.servlet.http.HttpServlet {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(LoginServlet.class);

    private static final long serialVersionUID = 1L;
    //final static String YAHOO_ENDPOINT  = "https://me.yahoo.com";
    //final static String GOOGLE_ENDPOINT = "https://www.google.com/accounts/o8/id";


    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }



    /**
     * For authenicated user.
     *
     * @return
     */
    protected String getApplicationHomeUrl(HttpServletRequest req) {
        return LoginUtils.getEndpointApplicationHome(req);

    }



    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        LOGGER.debug("OpenID servlet");

        ResourceBundle properties = ResourceBundle.getBundle("icardea");
        String applicationServer = properties.getString("salk.server");
        String securePort = properties.getString("secure.port");

        //String salkServer = ConfigurationService.getInstance().getProperty("");

        //String loginId = getLocalLogin(req);
        String errorMsg = null;
        boolean success = false;
        String identifier = null;

//        //User is logged in, session active. This could happen once normally.
//        if (UserSessionService.loggedIn(req)) {// OK
//            success=true;

//        } else  {

        LOGGER.debug("Start Open ID Login for loginId  " + identifier);
        // --- processing the authentication response ---

        RegistrationModel model = null;
        try {
            model = RegistrationService.processReturn(req);

            HttpSession session = req.getSession(true);
//                System.out.println("is_verified: "+model.getIs_verified());
//                System.out.println("user_role: "+model.getRole());
//                System.out.println("user_fullname: "+model.getFullName());
//                System.out.println("user_openid: "+ model.getOpenId());
//
            if(model == null){
                LOGGER.debug("Login servlet registration model= null");

            }
            if(session == null){
                LOGGER.debug("Login servlet session = null, but tried to set it to new");
            }

            session.setAttribute(PhrsConstants.OPEN_ID_IS_VERIFIED, model.getIs_verified());

            if (model.getRole() != null && ! model.getRole().isEmpty()) {
                LOGGER.debug("LoginServlet setting session role "+model.getRole()+" for "+model.getOpenId());
                session.setAttribute(PhrsConstants.SESSION_USER_AUTHORITY_ROLE,
                        model.getRole());

            }   else {
                LOGGER.debug("LoginServlet  session role not set Null "+" for "+model.getOpenId());
            }
//                session.setAttribute("user_role", model.getRole());
//                session.setAttribute("user_fullname", model.getFullName());
//                session.setAttribute("user_email", model.getEmailAddress());
//                session.setAttribute("user_openid", model.getOpenId());

            if (model != null && model.getIs_verified() != null && "true".equalsIgnoreCase(model.getIs_verified())) {
                identifier = model.getOpenId();
                success = true;
                LOGGER.debug("OpenId successful, prepare session and user account."
                        + "is_verified? " + model.getIs_verified()
                        + " openId " + model.getOpenId()
                        + " fullname " + model.getFullName()
                        + " nickname " + model.getNickname());
            }else {
                if(model ==null){
                  LOGGER.debug("Registration model is null");  
                } else {
                   
                    LOGGER.debug("Registration model ok, but is_verified NOT TRUE = "+model.getIs_verified());  
                    
                }
            }


        } catch (MessageException e) {
            errorMsg = "openidMessageException";//;PhrsConstants.DEFAULT_ERROR_MSG_OPEN_ID;
            LOGGER.debug("MessageException", e);
        } catch (DiscoveryException e) {
            errorMsg = "openidDiscoveryException";
            LOGGER.debug("DiscoveryException ", e);
        } catch (AssociationException e) {
            errorMsg = "openidAssociationException";
            LOGGER.debug("AssociationException ", e);
        } catch (Exception e) {
            errorMsg = "openidException";
            LOGGER.debug("Other openid exception", e);
        }

        if (success) {
            try {
                //setup session and create user account
                LOGGER.debug("Openid Servlet - prepare User account by openID managePhrUserSessionByOpenIdUserLoginScenario for "+identifier);
                PhrFederatedUser phrUser= null;
                try {
                    phrUser= UserSessionService.managePhrUserSessionByOpenIdUserLoginScenario(identifier, model, req);
                } catch (Exception e) {
                    LOGGER.debug("OpenId managePhrUserSessionByOpenIdUserLoginScenario failed phrUser null ");
                }

                success = phrUser != null;
                if(phrUser == null){
                    LOGGER.debug("LoginServlet after managePhrUserSessionByOpenIdUserLoginScenario phrUser=null, success false");
                }

            } catch (Exception e) {
                errorMsg = PhrsConstants.DEFAULT_ERROR_MSG_OPEN_ID;
                LOGGER.debug("Error managePhrUserSessionByOpenIdUserLoginScenario ", e);
                success = false;
            }
        } else {
            try {
                if(model != null){
                    LOGGER.debug("OpenId failed  "
                            + "is_verified? " + model.getIs_verified()
                            + " openId " + model != null ? model.getOpenId() : "unknown"
                            + " fullname " +  model.getFullName() != null ? model.getFullName() : "unknown"
                            + " nickname " +  model.getNickname() != null ? model.getNickname() : "unknown");
                } else {
                    LOGGER.debug("OpenId failed  - registration model is null");
                }
            } catch (Exception e) {
                LOGGER.debug("OpenId failed  Error writing debug on model ", e);
            }
        }

        //resp.sendRedirect(applicationServer + ":"+securePort+"/icardea_careplaneditor/flex-client/iCardea.html"); //only valid for SALK server
        //}
        //final decision - where to next

        if (success) {
            String endpoint = this.getApplicationHomeUrl(req);
            LOGGER.debug(" login preparation success, redirect  user loginId= " + identifier + " to endpoint=" + endpoint);
            UserSessionService.setSessionLoginErrorMsg(req, null);//remove any error msg with null
            resp.sendRedirect(endpoint);
            //remove any messages
            
        } else {
            LOGGER.debug("Login not successfull, redirect back to login page");
            if (errorMsg == null) {
                errorMsg = PhrsConstants.DEFAULT_ERROR_MSG_OPEN_ID;
            }
            //TODO forward or redirect?
            //req.setAttribute(LoginService.ERROR_MSG_ATTR, errorMsg);

            String endpoint = LoginUtils.getEndpointLoginPage(req);
            LOGGER.debug(" login failed, redirect  user loginId= " + identifier + " to endpoint=" + endpoint);

            //Set in session, then pickup by
            UserSessionService.setSessionLoginErrorMsg(req, errorMsg);
            //strips request params
            resp.sendRedirect(endpoint);

        }

//        while (en.hasMoreElements()) {
//            String paramName = ((String) en.nextElement()).trim();
//            System.out.println(paramName + " = " + req.getParameter(paramName) + "\n");
//            session.setAttribute(paramName, req.getParameter(paramName));
//        }
//		try {
//		    InetAddress addr = InetAddress.getLocalHost();
//
//		    // Get IP Address
//		    byte[] ipAddr = addr.getAddress();
//		    String ipadd = ipAddr.toString();
//		    // Get hostname
//		    String hostname = addr.getHostAddress();
//		    resp.sendRedirect("https://"+hostname+":"+securePort+"/icardea_careplaneditor/flex-client/iCardea.html");
//
//		} catch (UnknownHostException e) {
//		}

    }


}
