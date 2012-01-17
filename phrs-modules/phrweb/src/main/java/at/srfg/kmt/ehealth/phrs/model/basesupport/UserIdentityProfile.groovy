package at.srfg.kmt.ehealth.phrs.model.basesupport

import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id

//("profile_user_identity") 
@Entity
class UserIdentityProfile {//extends BasePhrsModel {
/*
 * PHRSID for the patient, and will register the OpenID, PHRSID, and the Protocol ID 
 */
	@Id
	ObjectId id
	
	String healthProfileUri;//PHRSID
	
	String globalProtocolId;
	
	String federatedId;
	
	String federatedProviderUrl;

	String role
	
	@Embedded
	Set<String> roles = []
	
	String email
	@Embedded
	Set<String> emails = []
	
	String userName
	String firstName
	String lastName
	String dob

	
	//???
	String portalUserId
	
	Date createDate
	Date modifyDate
	/**
	 * additional protocolIds
	 */
	@Embedded
	Map<String,String> protocolIds = [:]
	/**
	* additional protocolIds
	*/
	@Embedded
	Map<String,String> federatedIds = [:]
	/**
	 * other 3rd party software, if need to make queries, login etc
	 */
	@Embedded
	Map<String,String> otherUserAppIds = [:]
	
	public UserIdentityProfile(){
		createDate= new Date()
	}
}
