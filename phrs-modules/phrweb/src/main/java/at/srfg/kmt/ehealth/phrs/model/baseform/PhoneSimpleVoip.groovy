package at.srfg.kmt.ehealth.phrs.model.baseform

import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id


@Entity
public class  PhoneSimpleVoip {
	@Id
	ObjectId id
	String type
	String providerIdentity
	String accessIdentifier
	
	//String htmlString
	/**
	 * if user or application local and has a profile URI
	 */
	String profileUri

	String note
	
	public PhoneSimpleVoip(){
		super()
	}
	//http://www.fastcompany.com/blog/chris-dannen/techwatch/six-brilliant-ways-use-google-voice
}
