package tr.com.srdc.icardea.consenteditor.controller;

import java.util.List;

import tr.com.srdc.icardea.consenteditor.model.ActionElements;
import tr.com.srdc.icardea.consenteditor.model.ConsentDocument;
import tr.com.srdc.icardea.consenteditor.model.ResourceElements;
import tr.com.srdc.icardea.consenteditor.model.SubjectElements;

public interface PHRInterface {

	/**
	 * getPatientId
	 * 
	 * When called by the Consent Manager, you will return the currently logged
	 * in patient id.
	 * */
	String getPatientId();
	
	/**
	 * getConsentDocuments
	 * 
	 * When this method is called by the Consent Manager, you will return the
	 * consent documents of the specified patient as ConsentDocument array.
	 * */
	List<ConsentDocument> getConsentDocuments(String patientid);
	
	/**
	 * getConsentDocumentById
	 * 
	 * When called by the Consent Manager, you will return the consent document that 
	 * has the id given in parameter
	 */
	
	ConsentDocument getConsentDocumentById(String consentID);
	
	/**
	 * registerConsentDocument
	 * 
	 * When called by the Consent Manager, you will register this consent document to 
	 * database. If another consent document already exists with same id, that consent document 
	 * will be updated according to given consentDocument in parameter
	 */
	
	void registerConsentDocument(ConsentDocument consentDocument);
	
	/**
	 * deleteConsentDocument
	 * 
	 * When called by the Consent Manager, you will delete the consent document which has the 
	 * id given in parameter
	 */
	
	void deleteConsentDocument(String consentID);
		
	/**
	 * getActiveConsentDocumentByPatientId
	 * 
	 * When called by the Consent Manager, you will return the active consent of the patient
	 * There should be at least and at most one active consent document of every patient
	 * If the patient is new and has not any consent, then a template consent document 
	 * may be returned 
	 * This template can be determined according to hospital or doctor of the patient
	 */

	ConsentDocument getCurrentConsentDocumentByPatientId(String patientID);
	
	/**
	 * setCurrentConsentDocument
	 * 
	 * When called by the Consent Manager, you will set the consent document which has the 
	 * id given in parameter as Current Consent Document
	 */

	void setCurrentConsentDocumentofPatient(ConsentDocument consentDocument);
	
	/**
	 * getSubjectList
	 * 
	 * When called by the Consent Manager, you will return the "SubjectElements" object which 
	 * contains "SubjectElement"s related with patient whose id is given in parameter
	 */

	SubjectElements getSubjectList(String patientID);

	/**
	 * getResourceList
	 * 
	 * When called by the Consent Manager, you will return the "ResourceElements" object which 
	 * contains "ResourceElement"s related with patient whose id is given in parameter
	 */

	ResourceElements getResourceList(String patientID);
	
	/**
	 * getActionList
	 * 
	 * When called by the Consent Manager, you will return the "ActionElements" object which 
	 * contains "ActionElement"s related with patient whose id is given in parameter
	 */

	ActionElements getActionList(String patientID);
	
	
	/**
	 * saveUpdateSubjectList
	 * 
	 * When called by the Consent Manager, you will save the given "SubjectElements" 
	 * as related with the patient whose id is given in the parameter
	 */

	void saveSubjectList(SubjectElements subjectElements);
	
	/**
	 * saveUpdateResourceList
	 * 
	 * When called by the Consent Manager, you will save the given "ResourceElements" 
	 * as related with the patient whose id is given in the parameter
	 */

	void saveResourceList(ResourceElements resourceElements);
	

}
