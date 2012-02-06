package at.srfg.kmt.ehealth.phrs.model.baseform


import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Indexed
@Entity
class BaseUser extends BasePhrsMetadata{
	// OwnerUri is the important one. Unique userId for ownerUri and Unique in DB
	@Indexed
	String userId;
	// owneruri status, type, etc from BasePhrsMetadata

	//not supported, but the use of OpenIds might reveal users with multiple user accounts
	Set<String> aliasConfirmedOwnerUris
	Set<String> aliasOwnerUris
	Map<String,String> attributes
	/**
	 * If BaseUser is extended as in PhrFederatedUser, then it includes the primary health OpenId
	 * That will access this map
	 */

	String role;
	Set<String> roles
	//expect from OpenID
	String birthDate //date of birth from external systems
	String email
	String fullname
    String nickname
	String postCode

	long lastLogin;
	//local login possible instead of SSO or OpenId. For testing, crude test admin support,
	boolean canLocalLogin=false;
	String localPassword

	boolean active=true



	public boolean isSaved(){
		//check null createDate or resourceUri
		return  resourceUri ? true : false
	}
	public BaseUser(String ownerUri ){
		attributes = [:]
		aliasOwnerUris=[]
		aliasConfirmedOwnerUris=[]
		role=PhrsConstants.SESSION_USER_AUTHORITY_ROLE
		roles = new HashSet()
		roles.add(role)
		this.ownerUri=ownerUri
		//set it
		if(!ownerUri) ownerUri=UUID.randomUUID().toString()
		creatorUri=ownerUri
	}
	public BaseUser(){
		attributes = [:]
		aliasOwnerUris=[]
		aliasConfirmedOwnerUris=[]
		role=PhrsConstants.SESSION_USER_AUTHORITY_ROLE
		roles = new HashSet()
		roles.add(role)
		//by default there is always a UUID defined, however, it is
		//normally overwritten
		ownerUri=UUID.randomUUID().toString()
		creatorUri='phrs'
		//resourceUri do not set, this is set during the first save, so we know what is new
	}


}
