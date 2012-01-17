package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Embedded;


@Embedded
public class  PhoneInfo {
	String identifier //uri
	
	String type
	String telephone
	String countryCode
	//String phoneNumber //often a zero is the prefix for a local call.
	String note
	
	public PhoneInfo(){
		super()
	}
}
