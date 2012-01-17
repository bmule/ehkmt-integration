package at.srfg.kmt.ehealth.phrs.jsf.managedbean;

import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import at.srfg.kmt.ehealth.phrs.model.baseform.AUser;
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService;
/**
 * 
 * @deprecated
 * Test bean for an openid implementation
 *
 */
//@ManagedBean(name="userManagedBean")
//@ApplicationScoped
public class AUserMgtBean {
	//TODO at applicationScoped scope, create interface that only
	//exposes particular methods. Can also have userServices without user properties
	UserService userService = null;
	

	private String username;
	private String password;
	private String searchUser;
	private List<AUser> searchUsersResults; //Collection
	private AUser selectedUser;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AUser getSelectedUser() {
		if (selectedUser == null) {
			selectedUser = new AUser();
		}
		return selectedUser;
	}

	public void setSelectedUser(AUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	public Collection<AUser> getSearchUsersResults() {
		return searchUsersResults;
	}

	public void setSearchUsersResults(List<AUser> searchUsersResults) {//Collection
		this.searchUsersResults = searchUsersResults;
	}

	public String getSearchUser() {
		return searchUser;
	}

	public void setSearchUser(String searchUser) {
		this.searchUser = searchUser;
	}

	public String login() {
		if ("test".equalsIgnoreCase(getUsername())
				&& "test".equals(getPassword())) {
			return "home";
		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage("username", new FacesMessage(
					"Invalid UserName and Password"));
			return "login";
		}
	}

	public String searchUser() {
		if(userService!=null)
			searchUsersResults = userService.getResources(AUser.class);//.searchUsers(username);// userService.searchUsers(username);
		System.out.println(searchUsersResults);

		return "home";
	}

	public String updateUser() {
		
		userService.crudSaveResource(this.selectedUser);//.update(this.selectedUser);
		
		return "home";
	}

	public void onUserSelect(SelectEvent event) {
	}

	public void onUserUnselect(UnselectEvent event) {
	}
}
