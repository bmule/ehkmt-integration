package tr.com.srdc.icardea.consenteditor.controller.message;

public class LoginRequest extends GUIRequest{
	protected String username;
	protected String password;	
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
}
