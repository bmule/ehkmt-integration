package tr.com.srdc.icardea.consenteditor.controller.message;

public abstract class GUIResponse {
	protected boolean operationSuccessful = true;
	protected ConsentError error;
	
	public GUIResponse(){
		error = new ConsentError();
	}
	
	public void setOperationSuccessful(boolean operationSuccessful) {
		this.operationSuccessful = operationSuccessful;
	}
	public boolean isOperationSuccessful() {
		return operationSuccessful;
	}
	
	public void setError(ConsentError error) {
		this.error = error;
	}
	
	public ConsentError getError() {
		return error;
	}
}
