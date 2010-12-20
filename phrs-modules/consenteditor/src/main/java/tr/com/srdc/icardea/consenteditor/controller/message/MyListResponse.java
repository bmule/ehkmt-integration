package tr.com.srdc.icardea.consenteditor.controller.message;

import java.util.List;

public class MyListResponse extends GUIResponse{
	
	protected List tempList;
	public MyListResponse() {
		super();
	}
	public List getTempList() {
		return tempList;
	}
	public void setTempList(List tempList) {
		this.tempList = tempList;
	}
	
}
