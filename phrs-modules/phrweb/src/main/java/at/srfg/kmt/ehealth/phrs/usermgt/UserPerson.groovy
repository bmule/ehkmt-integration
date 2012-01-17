package at.srfg.kmt.ehealth.phrs.usermgt

import java.io.Serializable

import org.bson.types.ObjectId

import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrOpenId;
import at.srfg.kmt.ehealth.phrs.model.basesupport.PhrOpenID

import com.google.code.morphia.annotations.Id
/**
 * @deprecated
 *
 */
//@Entity
class UserPerson implements Serializable{
	
	//transient springSecurityService
	@Id
	ObjectId id
	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	
	Set<RemoteIdentity> remoteIds
	
	String ownerUri
	
	//pids received from OpenId attributes or from other sources. Every OpenId includes attributes, pid and role
	
	/**
	 * Issue:will protocol id include the provider also?
	 * @return
	 */
	public Set<String> getProtocolIds(){
		Set<String> temp=new HashSet()
		if(remoteIds){
			remoteIds.each { ri ->
				// if(category=='clinical'){					 
				//	  }
				if(ri.protocolId) temp.add(ri.protocolId)
			}
		}
		return temp
	}
	
	/**
	 * @return  A REST like string combining provider and protocol
	 * /category/clinical/source/xxxx/identifier/yyyy
	 * Returns null if provider is null or blank
	 * 
	 */
	
	public String getProviderProtocolIds(String provider){
		Set<String> temp=new HashSet()
		String rest
		RemoteIdentity ri = getIdentityByProviderOrProtocolNamespace(provider)
		if(ri){
			rest = "/category/${ri.category}/source/${ri.provider}/providerNamespace/${ri.protocolNamespaceId}/identifier/${ri.protocolId}"
			
		}

		return rest
	}
	public RemoteIdentity getIdentity(String providerOrProtocolIdNamespace){
		
	}

	
	public RemoteIdentity getIdentityByProviderOrProtocolNamespace(String protocolNamespaceId){
		Set<String> temp=new HashSet()
		if(remoteIds){
			remoteIds.each { ri ->
				// if(category=='clinical'){
				//	  }
				if( (ri.protocolNamespaceId == protocolNamespaceId) || (ri.provider == protocolNamespaceId)) {
					return ri
				}
			}
		}
		return null
	}

	public boolean hasMedicalRole(){
		return false
	}
	public String getRole(){
		return "non-medical-role"
	}
	
	public String getRoleLabel(String language){
		return null
	}
	public PhrOpenID getOpenId(String url){
		
	}
	//public void addReplaceOpenId(PhrOpenID openId){
	public void addReplaceOpenId(BasePhrOpenId openId){
		//TODO add role to both authority lists , transform, etc
		//TODO add to remoteId list
		//TODI add more helper methods
	}
	
	//roles received from OpenId attributes or from other sources
	Set<String> authoritiesImported
	
	//these are also roles, but considered to be more trustworthy and transformed to local vocabulary when needed
	Set<String> authorities

	public UserPerson(){
		authorities= new HashSet()
		authoritiesImported= new HashSet()
		pids = new HashSet()
		remoteIds= new HashSet()
	}
	/*
	static hasMany = [openIds: OpenID]

	static constraints = {

		
		//enabled=true
		username 	blank: false, unique: true
		password 	blank: false
		healthProfileId 		nullable:true
	}

	static mapping = {
		password column: '`password`'
	}*/
// embed authorities into user as
	/*
	Set<Authority> getAuthorities() {
		//UserPersonAuthority.findAllByUserPerson(this).collect { it.authority } as Set
		return null
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}*/
	

}
