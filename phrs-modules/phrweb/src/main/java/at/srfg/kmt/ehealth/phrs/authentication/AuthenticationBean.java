package at.srfg.kmt.ehealth.phrs.authentication;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService;
/**
 * 
 * This supports a traditional login without OpenId
 * This is no longer used, however, when refactoring a  new user management/authentication scenario
 * we will bring this back.
 * login_restricted.jsf 
 *
 * @deprecated
 */
@ManagedBean(name = "authenticationBean")
@SessionScoped
public class AuthenticationBean {
	
	public static final String AUTH_KEY = PhrsConstants.SESSION_USER_AUTHENTICATION_NAME;//AUTHENTICATION_USER_KEY;//"app.user.identifier";
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * conditional rendered="#{authenticationBean.loggedIn}"
	 */
	public boolean isLoggedIn() {
		return UserSessionService.loggedIn();
		//return FacesContext.getCurrentInstance().getExternalContext()
		//		.getSessionMap().get(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME) != null;
	}

	public boolean loggedIn() {
		//return FacesContext.getCurrentInstance().getExternalContext()
		//		.getSessionMap().get(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME) != null;
		return UserSessionService.loggedIn();
	}
	/**
	 * Traditional non-OpenId login.
	 * The name is used to lookup an existing user
	 * @return
	 */
	public String login() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.put(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME, name);
		//can put a redirect here or use the faces-config
		//
		return "success";// secret

	}

	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.remove(PhrsConstants.SESSION_USER_AUTHENTICATION_NAME);
		return null;
	}

	public Map getSessionMap() {
		Map map = FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap();
		return map;

	}

	/*
	 * <filter> <filter-name>AuthenticationFilter</filter-name>
	 * <filter-class>restricted.AuthenticationFilter</filter-class> </filter>
	 * <filter-mapping> <filter-name>AuthenticationFilter</filter-name>
	 * <url-pattern>/restricted/*</url-pattern> </filter-mapping>
	 * 
	 * 
	 * <f:view> <p><a href="restricted/secret.faces">test go to this restricted
	 * file page</a></p> <h:form> Username: <h:panelGroup
	 * rendered="#{not authenticationBean.loggedIn}"> <h:inputText
	 * value="#{authenticationBean.name}" /> <h:commandButton value="login"
	 * action="#{authenticationBean.login}" /> </h:panelGroup> <h:commandButton
	 * value="logout" action="#{authenticationBean.logout}"
	 * rendered="#{authenticationBean.loggedIn}" /> </h:form> </f:view>
	 */

}
