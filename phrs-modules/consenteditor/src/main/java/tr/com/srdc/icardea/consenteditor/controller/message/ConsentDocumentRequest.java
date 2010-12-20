package tr.com.srdc.icardea.consenteditor.controller.message;



public class ConsentDocumentRequest extends GUIRequest{
	protected String patientID;

	public ConsentDocumentRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPatientID() {
		return patientID;
	}

	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}
	
}
