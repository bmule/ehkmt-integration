package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date;

import org.bson.types.ObjectId;

import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class MessageInterop {
	@Id
	ObjectId id
	@Indexed
	String ownerUri //the healthProfileUri, not the userId
	@Indexed
	String creatorUri //the healthProfileUri, not the userId
	String parentId
	String resourceUri

	String action
	@Indexed
	Date createDate
	@Indexed
	Date modifyDate
	
	List<String> objectReferences

	public MessageInterop(){
		super();
		objectReferences=[]
		action=null
		createDate=new Date()
		modifyDate=createDate
	}
	public MessageInterop(String parentId, String ownerUri,List objectReferences, String action){
		super();
		this.objectReferences = objectReferences
		if(!objectReferences) objectReferences=[]
		this.action=action
		createDate=new Date()
		modifyDate=createDate

	}

}
