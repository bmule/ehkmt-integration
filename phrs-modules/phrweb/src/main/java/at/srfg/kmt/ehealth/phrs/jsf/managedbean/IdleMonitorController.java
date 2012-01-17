package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.IdleEvent;

@ManagedBean(name = "idleMonitorController")
//@ View Scoped
@RequestScoped
public class IdleMonitorController {

	public void idleListener(IdleEvent event) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Your session is closed",
						"You have been idle for at least 5 seconds"));

		// invalidate session
	}
}
