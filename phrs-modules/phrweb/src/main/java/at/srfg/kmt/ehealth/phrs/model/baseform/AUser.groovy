package at.srfg.kmt.ehealth.phrs.model.baseform;

import java.util.Date

import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id
@Entity
public class AUser{
	@Id
	ObjectId id
	String username //this could be the healthcare OpenID as the starting identifier or independent of OpenID, but is must be unique
	String ownerUri //phrsID or healthProfileID
	String resourceUri // for user objects ownerUri = resourceUri initially
	String creatorUri // same
	
	Date createDate
	Date modifyDate
	
	
	String emailId
	String openId
	
	//get from OpenId, set up Contact Info first time
	String phone
	Date dob
	String gender
	String address
	Map<String,String> attributes

	public AUser() {
		
	}
	
	public AUser(String ownerUri, String username, String emailId, String phone,
	Date dob, String gender, String address){
		this.ownerUri = ownerUri;
		this.username = username;
		this.emailId = emailId;
		this.phone = phone;
		this.dob = dob;
		this.gender = gender;
		this.address = address;
		attributes=[]
	}

	public AUser(String ownerUri, String username, String openID,  Map attributes){
		this.ownerUri = ownerUri
		this.username = username
		this.attributes= attributes
		
	}
	//setter and getters
}
