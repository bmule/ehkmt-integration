package at.srfg.kmt.ehealth.phrs.model.basesupport

import at.srfg.kmt.ehealth.phrs.model.baseform.CommonModelProps;

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference

//ntity("user_health_application") ("phrsdocument")
@Entity
public class UserHealthApplication extends CommonModelProps{
	
	//String type in base
	//String applicationIdentifier
	String theUserName
	String thePassword
	@Reference
	HealthApplicationEntry appDescriptor
	
	/*
	 * matches identifer in patient ID found in HealthProfile 
	 * 
	 */

	Map<String,String> params
	
	public UserHealthApplication(){
		//need for Entity annotation naming
	}

}
