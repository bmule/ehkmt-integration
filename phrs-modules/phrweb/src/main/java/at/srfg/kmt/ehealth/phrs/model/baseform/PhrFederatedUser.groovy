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



    public PhrFederatedUser(String theIdentifier, RegistrationModel model) {
        super();
        this.lastLogin = new Date().time
        if (!ownerUri) ownerUri = PhrFederatedUser.makeOwnerUri()
        if (!creatorUri) creatorUri = ownerUri
        if (identifier == null) this.identifier = theIdentifier
        if (userId == null) userId = identifier

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

    public updateRegistrationData(RegistrationModel model) {

        this.claimedId = model.getClaimedId()
        this.identity = model.getOpenId()
        this.providerIdentifier = model.getProvider()
        this.shortUserId = model.getLocalShortId()
        this.nickname = model.getNickname()
        this.fullname = model.getFullName()
        //no telephone
        this.email = model.getEmailAddress()
        this.lastname = model.getLastName()
        this.firstname = model.getFirstName()
        this.birthDate = model.getDateOfBirth()

        //verified, role
        this.role = model.getRole() ? model.role : this.role
        if (model.getRole()) {
            if (!roles) roles = []
            roles.add(model.getRole())
        }
        this.verified = model.is_verified
    }

    /**
     * Either the id from PIX or from the User
     */
    public String getProtocolId() {
        return protocolIdPix ? protocolIdPix : protocolIdUser
    }
    /**
     * Either the normalized namspace from PIX or the default Constants.ICARDEA_DOMAIN_PIX_OID
     */
    public String getProtocolIdNamespace() {
        return protocolIdNamespacePix ? protocolIdNamespacePix : Constants.ICARDEA_DOMAIN_PIX_OID
    }


}
