package tr.com.srdc.icardea.consenteditor.controller.message;

public class LoginResponse extends GUIResponse{
	protected boolean loginSuccessful;
	protected String loginResultMessage;
	protected String userSessionID;
	protected String serverTime;	
	protected long patientID;
	public boolean isLoginSuccessful() {
		return loginSuccessful;
	}
	public void setLoginSuccessful(boolean loginSuccessful) {
		this.loginSuccessful = loginSuccessful;
	}
	public String getLoginResultMessage() {
		return loginResultMessage;
	}
	public void setLoginResultMessage(String loginResultMessage) {
		this.loginResultMessage = loginResultMessage;
	}
	public String getUserSessionID() {
		return userSessionID;
	}
	public void setUserSessionID(String userSessionID) {
		this.userSessionID = userSessionID;
	}
	public String getServerTime() {
		return serverTime;
	}
	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
	public long getPatientID() {
		return patientID;
	}
	public void setPatientID(long patientID) {
		this.patientID = patientID;
	}
	
}
