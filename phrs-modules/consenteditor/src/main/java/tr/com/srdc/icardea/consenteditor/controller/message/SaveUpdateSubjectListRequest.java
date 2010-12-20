package tr.com.srdc.icardea.consenteditor.controller.message;

import java.util.List;

import tr.com.srdc.icardea.consenteditor.model.SubjectElement;

public class SaveUpdateSubjectListRequest{
	
	protected String patientID;
	protected List<SubjectElement> subjectList;
	

	public SaveUpdateSubjectListRequest() {
		super();
	}


	public List<SubjectElement> getSubjectList() {
		return subjectList;
	}


	public void setSubjectList(List<SubjectElement> subjectList) {
		this.subjectList = subjectList;
	}


	public String getPatientID() {
		return patientID;
	}


	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}

}
