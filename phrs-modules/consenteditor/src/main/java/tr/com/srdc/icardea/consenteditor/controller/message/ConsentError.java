package tr.com.srdc.icardea.consenteditor.controller.message;

public class ConsentError {
	private int errorCode;
	private String errorDescription;
	private int errorScope;
	
	public ConsentError() {
		
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorScope(int errorScope) {
		this.errorScope = errorScope;
	}
	public int getErrorScope() {
		return errorScope;
	}
	
	
}
