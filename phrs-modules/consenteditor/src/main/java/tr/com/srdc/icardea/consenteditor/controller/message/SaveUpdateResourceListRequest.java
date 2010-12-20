package tr.com.srdc.icardea.consenteditor.controller.message;

import java.util.List;

import tr.com.srdc.icardea.consenteditor.model.ResourceElement;

public class SaveUpdateResourceListRequest{
	
	protected String patientID;
	protected List<ResourceElement> resourceList;
	

	public SaveUpdateResourceListRequest() {
		super();
	}


	public List<ResourceElement> getResourceList() {
		return resourceList;
	}


	public void setResourceList(List<ResourceElement> resourceList) {
		this.resourceList = resourceList;
	}


	public String getPatientID() {
		return patientID;
	}


	public void setPatientID(String patientID) {
		this.patientID = patientID;
	}
	
}
