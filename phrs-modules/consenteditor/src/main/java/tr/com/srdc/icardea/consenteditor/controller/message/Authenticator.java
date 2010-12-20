package tr.com.srdc.icardea.consenteditor.controller.message;

public class Authenticator {
	private String userID;
	private String sessionID;
	private String role;
	
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public String getSessionID() {
		return sessionID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserID() {
		return userID;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}
	
}
