package at.srfg.kmt.ehealth.phrs.model.basesupport

import at.srfg.kmt.ehealth.phrs.model.baseform.CommonModelProps;

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Reference

//@tity("phrs_user_profile")
//@Entity
/**
 * @deprecated
 */
public class PhrsUserProfile extends CommonModelProps{
	
	//String healthProfileUri
	
//	@Reference
	HealthProfile healthProfile
	
	public PhrsUserProfile(){
		
	}
}
