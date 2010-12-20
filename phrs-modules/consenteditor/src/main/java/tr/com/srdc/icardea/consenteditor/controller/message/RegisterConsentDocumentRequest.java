package tr.com.srdc.icardea.consenteditor.controller.message;

import tr.com.srdc.icardea.consenteditor.model.ConsentDocument;

public class RegisterConsentDocumentRequest{
	protected ConsentDocument consentDocument;
	

	public RegisterConsentDocumentRequest() {
		super();
	}

	public ConsentDocument getConsentDocument() {
		return consentDocument;
	}


	public void setConsentDocument(ConsentDocument consentDocument) {
		this.consentDocument = consentDocument;
	}


	
	
}
