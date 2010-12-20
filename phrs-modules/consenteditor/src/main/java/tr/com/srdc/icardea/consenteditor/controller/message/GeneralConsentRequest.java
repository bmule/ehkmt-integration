package tr.com.srdc.icardea.consenteditor.controller.message;



public class GeneralConsentRequest extends GUIRequest{
	protected String consentID;
	protected String patientID;
	public GeneralConsentRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getConsentID() {
		return consentID;
	}
	public void setConsentID(String consentID) {
		this.consentID = consentID;
	}
	public String getPatientID() {
		return patientID;
	}
	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}
	
}
