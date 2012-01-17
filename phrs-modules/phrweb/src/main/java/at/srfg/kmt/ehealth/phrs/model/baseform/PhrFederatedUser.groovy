package at.srfg.kmt.ehealth.phrs.model.baseform;

import java.util.Map

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils

import com.google.code.morphia.annotations.Entity
/**
 * 
 * Extends the BaseUser
 * Includes access methods for the associated OpenIds
 * It includes cache of the relevant parameters for a default Health OpenId.
 * 
 * When getting, creating/updating OpenIds, the ownerUri (phrId) is known.
 */
@Entity
public class PhrFederatedUser extends BaseUser {

	/**
	 * These are not all currently used, except for searching on the identifier or identifiers.
	 * When the code settles for the OpenID implementation, this will include convenience methods
	 * 
	 * 
	 */
	//primary user identifier for whole system
	//short name vs long name
	String identifier;

	//if special, idp
	String domainCode

	//Login identity from login form
	String identity;
	//String claimId;

	//see email and dateOfBirth in BaseUser

	Map<String,String> ssoAttributes
	boolean healthCare=true
	
	String phrPinId


	/**
	 * For DB retrieval
	 */
	public PhrFederatedUser() {
		super();
		ssoAttributes=[:]
		
		if(!ownerUri) 	ownerUri=UUID.randomUUID().toString()
		if(!creatorUri) creatorUri=ownerUri
		if(!phrPinId) 	phrPinId = makePhrPinId(ownerUri)
	}
	/**
	 * When creating user for the first time from an OpenId or SSO, read attributes
	 * and assign
	 * @param identifier
	 * @param attrs
	 */
	public PhrFederatedUser(String identifier, Map<String,String> attrs) {
		super();
		ssoAttributes=[:]

		this.identifier=identifier
		this.loginId=identifier
		if(attrs){
			init(attrs)
			addOpenIdAttributes(identifier,attrs)
		}
		
		if(!ownerUri) 	ownerUri=UUID.randomUUID().toString()
		if(!creatorUri) creatorUri=ownerUri
		if(!phrPinId) 	phrPinId = ownerUri//makePhrPinId(ownerUri)
			
	}
	
	public  String makePhrPinId(){
		return makePhrPinId(UUID.randomUUID().toString())
	}
	public  String makePhrPinId(String theOwnerUri){
		
		
		String value =ownerUri
		/*
		String input =theOwnerUri
	
		//make test users easier to set up
		if(input && (input.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_AUTO_USER) || 
			  input.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST)
			  || input.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST_1) 
			  || input == PhrsConstants.AUTHORIZE_USER_ADMIN ) ){
		  			  
			value = input
		} else {
		
			value = HealthyUtils.lastChars(input,8)
		}
		*/
		return value
	}
	/**
	 * Setup from initial SSO 
	 * @param input
	 */
	private void init( Map<String,String> input){
		if(input){
			if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_EMAIL)){
				email=input.get(PhrsConstants.OPEN_ID_PARAM_EMAIL)
			}
			if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_ROLE)){
				role=input.get(PhrsConstants.OPEN_ID_PARAM_ROLE)
				getRoles().add(role)
			}
			if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_POST_CODE)){
				postCode =input.get(PhrsConstants.OPEN_ID_PARAM_POST_CODE)
			}

			if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_DATE_OF_BIRTH)){
				birthDate=input.get(PhrsConstants.OPEN_ID_PARAM_DATE_OF_BIRTH)
			}

			if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_IDENTITY)){
				identity=input.get(PhrsConstants.OPEN_ID_PARAM_IDENTITY)
			}
			if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_CLAIM_ID)){
				claimId=input.get(PhrsConstants.OPEN_ID_PARAM_CLAIM_ID)
			}
		}
	}


	public void addOpenIdAttributes(String identifier,Map<String,String> attrs){
		if(attrs){
			if(ssoAttributes == null ) ssoAttributes=[:]
			ssoAttributes.putAll(attrs)
			//When identifiers are stored by id - ssoAttributes.putAll(identifier,attrs)
		}
	}
	/*
	 * 
	 * @return Key is OpenId identifier
	
	public Map<String,BasePhrOpenId> findOpenIds(){

		return null;

	} */

	public Set<String> associatedOpenIds(){
		if(ssoAttributes){
			return ssoAttributes.keySet()
		}
		return new HashSet()
	}


}
