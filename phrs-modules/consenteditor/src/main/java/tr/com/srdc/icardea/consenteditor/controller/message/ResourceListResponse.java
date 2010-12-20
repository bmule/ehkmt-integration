package tr.com.srdc.icardea.consenteditor.controller.message;

import java.util.List;

import tr.com.srdc.icardea.consenteditor.model.ResourceElement;

public class ResourceListResponse extends GUIResponse{
	
	protected List<ResourceElement> tempList;
	public ResourceListResponse() {
		super();
	}
	public List<ResourceElement> getTempList() {
		return tempList;
	}
	public void setTempList(List<ResourceElement> tempList) {
		this.tempList = tempList;
	}
	
}
