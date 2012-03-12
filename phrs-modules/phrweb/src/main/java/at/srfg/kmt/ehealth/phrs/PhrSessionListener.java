package at.srfg.kmt.ehealth.phrs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class PhrSessionListener implements HttpSessionListener {
    /**
     * When the user session timedout, ({@link #sessionDestroyed(HttpSessionEvent)}) method will be invoked.
     * This method will make necessary cleanups (logging out user, updating db and audit logs, etc...)
     * As a result; after this method, we will be in a clear and stable state. So nothing left to think about
     * because session expired, user can do nothing after this point.
     *
     * @author hturksoy
     */
    //  <listener>
    //  <listener-class>com.your.listener.package.MySessionListener</listener-class>
    //  </listener>

    private static final Logger logger = LoggerFactory.getLogger(PhrSessionListener.class.getName());

    /**
     * our apps security service bean responsible from doing necessary operations at
     * login/logout progress...
     */
    //private ISecurityService securityService;
    public PhrSessionListener() {
    }

    public void sessionCreated(HttpSessionEvent event) {
        logger.debug("session created : " + event.getSession().getId());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        // get the destroying session...
        HttpSession session = event.getSession();
        logger.debug("session destroyed :" + session.getId() + " Logging out user...");

        /*
        * nobody can reach user data after this point because session is invalidated already.
        * So, get the user data from session and save its logout information
        * before losing it.
        * User's redirection to the timeout page will be handled by the SessionTimeoutFilter.
        */
        try {
            prepareLogoutInfoAndLogoutActiveUser(session);
        } catch (Exception e) {
            logger.error("error while logging out at session destroyed : " + e.getMessage(), e);
        }
    }

    /**
     * Gets the logged in user data from Acegi SecurityContext and makes necessary logout operations.
     */
    public void prepareLogoutInfoAndLogoutActiveUser(HttpSession httpSession) {
        //SecurityContext context = (SecurityContext) httpSession
        //        .getAttribute(HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY);

        // if(context!=null){

        //}
//            if (context != null) {
//
//                // get the authentication object from acegi context...
//                Authentication authentication = context.getAuthentication();
//
//                // update user logoninfo from active session...
//                LogonInfo logonInfo = LogonInfoHolder.getLogonInfo();
//
//                // set logout information...
//                logonInfo.setUserId(authentication.getName());
//                logonInfo.setLogonType(LogonInfo.LOGON_TYPE_LOGOUT);
//                logonInfo.setLogonTime(new Date());
//
//                if (logger.isDebugEnabled()) {
//                    logger.debug("Acegi SecuityContext found, calling logout service for user :" + logonInfo.getUserId());
//                }
//
//                // logout (and save logout info in database)...
//                getSecurityService(httpSession.getServletContext()).logout();
//            } else {
//                if (logger.isDebugEnabled()) {
//                    logger.warn("Acegi SecuityContext doesn't exist, no need to call logout service");
//                }
//            }
    }

//        public ISecurityService getSecurityService(ServletContext sc) {
//            if (securityService == null) {
//                securityService = (ISecurityService) WebApplicationContextUtils.getRequiredWebApplicationContext(sc)
//                        .getBean("securityServiceBean");
//            }
//            return securityService;
//        }
}

