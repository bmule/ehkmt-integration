package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;


import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class  MessageBaseDescription extends CommonModelProps {
	//issue ownerUri --> about ownerUri the owner owns this message and it is about that person.
	//creatorUri by system etc.
	//
	/**
	 * high level category of message
	 */
	@Indexed
	String messageCategory
	/**
	 * action types (crud actions, reasons for message
	 */
	@Indexed
	Set<String> reasonTypes
	
	String portalId
	
	//@Serialized 
	Map<String,String> resourceSnapshot
	
	ObjectId aboutResource
	
	public MessageBaseDescription(){
		super();
		//this.setCreatorUri(PhrsConstants.USER_SYSTEM_HEALTH_PROFILE_ID)
		reasonTypes = []
		
		resourceSnapshot= [:]
		
	}
	

	
	

	
}
