package tr.com.srdc.icardea.consenteditor.controller;

import java.util.Date;

public class ConsentSession {
	private String userID;	
	private String sessionID;
	private Date sessionStart;
	private boolean heartBeatReceived = true;
	//Blazeds Session and Client Ids
	private String blazeDSClientID;
	private String blazeDSSessionID;
	
	
	public ConsentSession(String userID){
		this.userID = userID;
		setHeartBeatReceived(true);
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserID() {
		return userID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getSessionID() {
		return sessionID;
	}	
	public void setSessionStart(Date sessionStart) {
		this.sessionStart = sessionStart;
	}
	public Date getSessionStart() {
		return sessionStart;
	}

	public void setHeartBeatReceived(boolean heartBeatReceived) {
		this.heartBeatReceived = heartBeatReceived;
	}

	public boolean isHeartBeatReceived() {
		return heartBeatReceived;
	}

	public void setBlazeDSClientID(String blazeDSClientID) {
		this.blazeDSClientID = blazeDSClientID;
	}

	public String getBlazeDSClientID() {
		return blazeDSClientID;
	}

	public void setBlazeDSSessionID(String blazeDSSessionID) {
		this.blazeDSSessionID = blazeDSSessionID;
	}

	public String getBlazeDSSessionID() {
		return blazeDSSessionID;
	}
}
