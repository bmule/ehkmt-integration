package at.srfg.kmt.ehealth.phrs.jsf;

import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import javax.faces.context.FacesContext;

//idleMonitorController
@ManagedBean(name = "idleMonitorController")
@RequestScoped
public class IdleMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdleMonitor.class.getName());

    public class IdleMonitorController {

        public void idleListener() {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Your session is closed", "You have been idle too long"));
            LOGGER.debug("idelListener invalidateSessionAndRedirectToLogin");
            UserSessionService.invalidateSessionAndRedirectToLogin();

            //invalidate session
        }

        public void activeListener() {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Welcome Back", "That's a long coffee break!"));
        }
    }
}
