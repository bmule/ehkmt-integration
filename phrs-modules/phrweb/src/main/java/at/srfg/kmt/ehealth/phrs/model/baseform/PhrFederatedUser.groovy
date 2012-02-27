package at.srfg.kmt.ehealth.phrs.model.baseform;


import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Entity
import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.security.services.login.RegistrationModel

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
    String identifier

    //user provided short ID, email or other. The OP url might be added or it is a email address for OpenIds
    String shortUserId

    String providerIdentifier
    /**
     * @deprecated
     */
    String claimedId
    /**
     * @deprecated
     */
    String identity;
    //if special, idp
    String domainCode

    //String claimedId;

    //see email and dateOfBirth in BaseUser


    boolean healthCare = true

    //normally the ownerUri
    String phrPinId

    //query Id from User to get the protocol Id
    String pixQueryIdUser
    String pixQueryIdType
    //Using the CIED namespace
    String pixQueryIdNamespace = PhrsConstants.ICARDEA_DOMAIN_PIX_CIED //CIED&bbe3a050-079a-11e0-81e0-0800200c9a66&UUID

    //From the user to validate the Pix response; or a means to do testing without the Pix server
    //updated from any form, currently the Contact info
    String protocolIdUser

    //protocolId obtained from Pix, based on the CIED or pixQueryIdUser
    String protocolIdPix
    String protocolIdNamespacePix

    //openid implementation
    String verified

    /**
     * For DB retrieval
     */
    public PhrFederatedUser() {
        super();

        if (!ownerUri) ownerUri = makeOwnerUri()
        if (!creatorUri) creatorUri = ownerUri
        if (!phrPinId) phrPinId = makePhrPinId(ownerUri)
        this.lastLogin = new Date().time
    }


    /**
     * OpenId identifier, registration model
     * This can include updates, the logic checks if this is updating an existing user or creating a new user
     **/
    public PhrFederatedUser(String theIdentifier, RegistrationModel model) {
        super();
        this.lastLogin = new Date().time
        if (! ownerUri) ownerUri = PhrFederatedUser.makeOwnerUri()
        if (! creatorUri) creatorUri = ownerUri
        if (! identifier) this.identifier = theIdentifier
        if (! userId) userId = identifier

        if (model) {
            updateRegistrationData(model)

        }

        if (!phrPinId) phrPinId = ownerUri//makePhrPinId(ownerUri)

    }

    public static String makeOwnerUri() {
        return PhrsConstants.USER_HEALTH_PROFILE_PREFIX + PhrsConstants.USER_IDENTIFIER_DELIMITER + UUID.randomUUID().toString();
    }

    //using ownerUri
    public static String makePhrPinId(String theOwnerUri) {
        return theOwnerUri
    }
    /**
     * update or create 
     * If updating, it does not replace existing data with null from a "new" registration model, e.g. if the email exists, it will not be replaced with null a second time
     * 
     */
    public updateRegistrationData(RegistrationModel model) {

        this.claimedId =    model.getClaimedId()        ? model.getClaimedId()  : this.claimedId
        this.identity =     model.getOpenId()           ? model.getOpenId()     : this.identity 
        this.providerIdentifier = model.getProvider()   ? model.getProvider() : this.providerIdentifier 
        this.shortUserId =  model.getLocalShortId()     ? model.getLocalShortId() : this.shortUserId
        this.nickname =     model.getNickname()         ? model.getNickname()   : this.nickname
        this.fullname =     model.getFullName()         ? model.getFullName()   : this.fullname
        //no telephone
        this.email =        model.getEmailAddress()     ? model.getEmailAddress() : this.email
        this.lastname =     model.getLastName()         ? model.getLastName()   : this.lastname 
        this.firstname =    model.getFirstName()        ? model.getFirstName()  : this.firstname 
        this.birthDate =    model.getDateOfBirth()      ? model.getDateOfBirth() : this.birthDate

        //verified, role
        this.role = model.getRole() ? model.role : this.role
        if (model.getRole()) {
            if (!roles) roles = []
            roles.add(model.getRole())
        }
        
        this.verified = model.getIs_verified() ? model.getIs_verified() : this.verified 
    }

    /**
     * Either the id from PIX or from the User
     */
    public String getProtocolId() {
        String theId= protocolIdPix ? protocolIdPix : protocolIdUser
        if(theId) return theId
        return null
    }
    /**
     * Either the normalized namspace from PIX or the default Constants.ICARDEA_DOMAIN_PIX_OID
     */
    public String getProtocolIdNamespace() {
        return protocolIdNamespacePix ? protocolIdNamespacePix : Constants.ICARDEA_DOMAIN_PIX_OID
    }


}
