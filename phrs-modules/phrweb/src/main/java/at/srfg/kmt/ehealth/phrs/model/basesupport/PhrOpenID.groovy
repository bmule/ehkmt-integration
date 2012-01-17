package at.srfg.kmt.ehealth.phrs.model.basesupport
import java.io.Serializable
import java.util.Date

import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.dyuproject.openid.OpenIdUser
import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Indexed
/**
 * 
 * This is embedded and no reference to the parent User is needed
 * @deprecated
 * 
 */
//@Embedded
class PhrOpenID implements Serializable{
	/*
	@Id
	ObjectId id
	 */
	
	@Indexed
	String resourceUri
	/**
	 * ownerUri is PhrId is healthProfileId
	 */
	@Indexed
	String ownerUri
	String creatorUri;
	Date createDate;
	Date modifyDate;
	String type
	
	
	boolean active = true
	
	boolean healthCare=true
	

	
	String identifier 
	String identity
	
	String shortId 
	
	String email
	String country
	String language
	
	String provider //
	
	String firstName
	String lastName

	String organization
	
	//user identifier throughout system and associated with this OpenId
	String protocolId
	String protocolNamespace
	String role
	String roleLocal
	
	Date dateCreated
	Date dateModified

	Map<String,String> attributes = [:]

	/**
	 * Should not be used in implementation, here for I/O reasons
	 */
	public PhrOpenID(){
		active=true
	}
	

	public PhrOpenID(String ownerUri, String url, String provider, String role, String protocolIdNamespace, String protocolId, Map<String,String> attributes, String shortId ) throws Exception{
		handleDate()
		
		handleAttributes(attributes) //do first, should we check attributes for following or replace?
		this.url = url
		this.provider = provider
		this.role = role
		this.protocolId= protocolId
		this.ownerUri = ownerUri
		this.protocolNamespace=protocolIdNamespace
		
		if( ! shortId && url && ! url.contains('://')){
			shortId = url		
		}
		this.shortId=shortId
		
	}
	public PhrOpenID(String ownerUri, String url, String provider, String role, String protocolIdNamespace, String protocolId, Map<String,String> attributes ) throws Exception{
		handleDate()
		
		handleAttributes(attributes) //do first, should we check attributes for following or replace?
		this.url = url
		this.provider = provider
		this.role = role
		this.protocolId= protocolId
		this.ownerUri = ownerUri
		this.protocolNamespace=protocolIdNamespace
		if(url && ! url.contains('://')){
			shortId = url
			
		}
		
	}
	public PhrOpenID(String ownerUri,OpenIdUser openIdUser){
		
		this.ownerUri = ownerUri
		this.identifier = openIdUser.getIdentifier()
		
		this.identity = openIdUser.getIdentity()
		this.claimId =openIdUser.getClaimId()
		handleDate()
		//TODO handle missing map keys
		handleAttributes(openIdUser.getAttributes())
		
	}
	public PhrOpenID(String ownerUri,String openIdIdentifier, String openIdIdentity,
		String claimId,Map<String,String> attributes ) throws Exception{
		
		
		this.ownerUri = ownerUri
		this.identifier = openIdIdentifier
		this.identity = openIdIdentifier
		this.claimId =claimId
		
		handleDate()
		//TODO handle missing map keys
		handleAttributes(attributes)
		
	}
	
	private void handleDate(){
		dateModified = new Date()
		if(! dateCreated) dateCreated = new Date()
		
	}

	
	private void handleAttributes(Map<String,String> theAttributes){
		if(theAttributes){
			this.attributes = theAttributes	
			identifier = theAttributes.containsKey(PhrsConstants.OPEN_ID_PARAM_IDENTIFIER) ? theAttributes.get(PhrsConstants.OPEN_ID_PARAM_IDENTIFIER) : identifier

			if(identifier && ! identifier.contains('://')){
				shortId = identifier
			}
			
			role 		= theAttributes.containsKey(PhrsConstants.OPEN_ID_PARAM_ROLE ) 	? theAttributes.get(PhrsConstants.OPEN_ID_PARAM_ROLE )		: role
			protocolId  = theAttributes.containsKey(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_ID) 	? theAttributes.get(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_ID) 	: protocolId
			language    = theAttributes.containsKey(PhrsConstants.OPEN_ID_PARAM_LANGUAGE ) 	? theAttributes.get(PhrsConstants.OPEN_ID_PARAM_LANGUAGE) 	: language
			email       = theAttributes.containsKey(PhrsConstants.OPEN_ID_PARAM_EMAIL) 		? theAttributes.get(PhrsConstants.OPEN_ID_PARAM_EMAIL) 		: email
			country     = theAttributes.containsKey(PhrsConstants.OPEN_ID_PARAM_COUNTRY ) 		? theAttributes.get(PhrsConstants.OPEN_ID_PARAM_COUNTRY ) 	: country
			
//			protocolId  = theAttributes.containsKey('protocolID') ? theAttributes.get('protocolID')	: protocolId
//			protocolId  = theAttributes.containsKey('protocolId') ? theAttributes.get('protocolId') 	: protocolId

			protocolNamespace = theAttributes.containsKey('protocolNS') ? theAttributes.get('protocolNS'): protocolNamespace 
			
		}
	
	}
	public boolean identifierIsUrl(){
		if(identifier && ! identifier.contains('://')) return false
		return true
	}
}
