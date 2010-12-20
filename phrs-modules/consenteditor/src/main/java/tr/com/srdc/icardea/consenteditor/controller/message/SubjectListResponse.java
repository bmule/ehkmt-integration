package tr.com.srdc.icardea.consenteditor.controller.message;

import java.util.List;

import tr.com.srdc.icardea.consenteditor.model.SubjectElement;




public class SubjectListResponse extends GUIResponse{
	
	protected List<SubjectElement> tempList;
	public SubjectListResponse() {
		super();
	}
	public List<SubjectElement> getTempList() {
		return tempList;
	}
	public void setTempList(List<SubjectElement> tempList) {
		this.tempList = tempList;
	}
	
}