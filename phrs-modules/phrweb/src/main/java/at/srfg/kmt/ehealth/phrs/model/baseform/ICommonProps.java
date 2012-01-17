package at.srfg.kmt.ehealth.phrs.model.baseform;

import java.util.Map;

import org.bson.types.ObjectId;


public interface ICommonProps {

	public ObjectId getId();	
	
	public String getResourceUri();
	public String getCreateDate();	
	public String getModifyDate();	
	public String getResourceStatus();	
	public Boolean getDeleted();
	
	public String getOwnerUri();	
	public String getCreatorUri();
	
	public String getType(); //class usually
	
	public String getParentId();
	
	
	//public String getTags(); part of annotations or separate from document
	
	public Map<String,String> getAnnotations();

	public String getCode();
	public String getContent();
	

	
	
	
	
	//public String getValue();
	
	
	
	
	

}
