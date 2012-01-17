package at.srfg.kmt.ehealth.phrs.usermgt

import org.bson.types.ObjectId

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.HealthProfileOverview
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id
import com.google.code.morphia.annotations.Indexed

@Entity
class UserPortalEntry {

	@Id
	ObjectId id

	@Indexed
	String resourceUri

	@Indexed
	String ownerUri

	String remoteUser

	String provider
	String host


	public UserPortalEntry(){
		provider="default"
		host="default"
	}

	protected static Map getQueryAttributes(String ownerUri, String remoteUser, String provider){
		return [(PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER):ownerUri,
			'remoteUser':remoteUser,
			'provider':provider,
			'clazz':UserPortalEntry.class]
	}
}
