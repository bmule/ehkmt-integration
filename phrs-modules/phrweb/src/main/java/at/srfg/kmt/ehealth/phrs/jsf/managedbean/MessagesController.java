package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@ManagedBean(name="messagesController")
//@ View Scoped
@RequestScoped
@SuppressWarnings("serial")
public class MessagesController  implements Serializable {

	public void addInfo(ActionEvent actionEvent) {
		FacesContext.getCurrentInstance().addMessage(null, new 
				FacesMessage(FacesMessage.SEVERITY_INFO,"Sample info message", "PrimeFaces rocks!"));
	}

	public void addWarn(ActionEvent actionEvent) {
		FacesContext.getCurrentInstance().addMessage(null, new 
			FacesMessage(FacesMessage.SEVERITY_WARN,"Sample warn message", "Watch out for PrimeFaces!"));
	}

	public void addError(ActionEvent actionEvent) {
		FacesContext.getCurrentInstance().addMessage(null, new 
				FacesMessage(FacesMessage.SEVERITY_ERROR,"Sample error message", "PrimeFaces makes no mistakes"));
	}

	public void addFatal(ActionEvent actionEvent) {
		FacesContext.getCurrentInstance().addMessage(null, 
				new  FacesMessage(FacesMessage.SEVERITY_FATAL,"Sample fatal message", "Fatal  Error in System"));
	}
}
