package tr.com.srdc.icardea.consenteditor.controller.message;

import java.util.List;

import tr.com.srdc.icardea.consenteditor.model.ConsentDocument;

public class ConsentDocumentResponse extends GUIResponse{
	protected List<ConsentDocument> consentDocuments;
	
	public ConsentDocumentResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<ConsentDocument> getConsentDocuments() {
		return consentDocuments;
	}

	public void setConsentDocuments(List<ConsentDocument> consentDocuments) {
		this.consentDocuments = consentDocuments;
	}

	
}
