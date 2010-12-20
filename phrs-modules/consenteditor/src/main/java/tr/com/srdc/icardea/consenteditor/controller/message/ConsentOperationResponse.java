package tr.com.srdc.icardea.consenteditor.controller.message;



public class ConsentOperationResponse extends GUIResponse{
	public ConsentOperationResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ConsentOperationResponse(String _message) {
		super();
		this.message=_message;
		// TODO Auto-generated constructor stub
	}

	public String message;
}
