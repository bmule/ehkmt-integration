package at.srfg.kmt.ehealth.phrs.model.basesupport;

import java.io.Serializable
import java.util.Date

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id;

@Entity
public class OpenIdRegistrationModel implements Serializable {

	private static final long serialVersionUID = -2834908356868001273L;
	
	@Id
	ObjectId id
	
	String openId;
	String fullName;
	String emailAddress;
	String zipCode;
	Date dateOfBirth;
	 
	String protocolID
	String role
	
	
	//from local application for user..
	String healthProfileId
	//from local application for user..
	String primaryUserId
	
	 
	String favoriteColor;
	
	//issue when data is updated?
	Map<String,String> attributeMap
	
	public OpenIdRegistrationModel(){
		
		attributeMap=[:]
	}
	 
	

}
