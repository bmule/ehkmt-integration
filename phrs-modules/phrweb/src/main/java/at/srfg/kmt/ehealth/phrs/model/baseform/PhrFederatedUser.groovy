package at.srfg.kmt.ehealth.phrs.model.baseform;


import at.srfg.kmt.ehealth.phrs.PhrsConstants

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
	String identifier
    String providerIdentifier
    String claimedId
    String identity;
	//if special, idp
	String domainCode



	//String claimedId;

	//see email and dateOfBirth in BaseUser

	Map<String,String> ssoAttributes
	boolean healthCare=true

    //normally the ownerUri
	String phrPinId


	/**
	 * For DB retrieval
	 */
	public PhrFederatedUser() {
		super();
		ssoAttributes=[:]
		
		if(!ownerUri) 	ownerUri=makeOwnerUri()
		if(!creatorUri) creatorUri=ownerUri
		if(!phrPinId) 	phrPinId = makePhrPinId(ownerUri)
	}
	/**
	 * When creating user for the first time from an OpenId or SSO, read attributes
	 * and assign
	 * @param identifier  is the claimedId
	 * @param attrs
	 */
	public PhrFederatedUser(String identifier, Map<String,String> attrs) {
		super();
		ssoAttributes=[:]

		this.identifier=identifier
        //This can be set with a local login
        this.userId=identifier

		//this.loginId=identifier

		if(attrs){
			init(attrs)
			addOpenIdAttributes(identifier,attrs)
		}
		
		if(!ownerUri) 	ownerUri= PhrFederatedUser.makeOwnerUri()
		if(!creatorUri) creatorUri=ownerUri
        //TODO refactor pin code
		if(!phrPinId) 	phrPinId = ownerUri//makePhrPinId(ownerUri)
			
	}

	public  static String makeOwnerUri(){
		return PhrsConstants.USER_HEALTH_PROFILE_PREFIX+ PhrsConstants.USER_IDENTIFIER_DELIMITER+UUID.randomUUID().toString();
	}
    //using ownerUri
	public  static String makePhrPinId(String theOwnerUri){

		String value =theOwnerUri

		return value
	}
	/**
	 * Setup from initial SSO 
	 * @param input
	 */
	private void init( Map<String,String> input){
		if(input){

            if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_FULL_NAME)){
                fullname=input.get(PhrsConstants.OPEN_ID_PARAM_FULL_NAME)
            }
            if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_NICK_NAME)){
                nickname=input.get(PhrsConstants.OPEN_ID_PARAM_NICK_NAME)
            }

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
            //ISSUE: naming clash: openid identifier is provider,
            // identity is the OP's identifier for the user's claimedId
			if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_IDENTITY)){
				identity=input.get(PhrsConstants.OPEN_ID_PARAM_IDENTITY)
			}
            //careful of the identifier clash. This is not the PhrFederatedUser.identifier
            if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_OP_IDENTIFIER)){
                providerIdentifier=input.get(PhrsConstants.OPEN_ID_PARAM_OP_IDENTIFIER)
            }
            // The claimedId *is*  the PhrFederatedUser.identifier unless it is null, then it is the opendUser.identity (the OP's representation of the claimedId)
            if(input.containsKey(PhrsConstants.OPEN_ID_PARAM_CLAIM_ID)){
                claimedId=input.get(PhrsConstants.OPEN_ID_PARAM_CLAIM_ID)
            }

		}
	}


	public void addOpenIdAttributes(String identifier,Map<String,String> attrs){
		if(attrs){
			if(ssoAttributes == null ) ssoAttributes=[:]
			ssoAttributes.putAll(attrs)
            init(ssoAttributes); //update values
			//When identifiers are stored by id - ssoAttributes.putAll(identifier,attrs)
		}
	}


	public Set<String> associatedOpenIds(){
		if(ssoAttributes){
			return ssoAttributes.keySet()
		}
		return new HashSet()
	}


}
