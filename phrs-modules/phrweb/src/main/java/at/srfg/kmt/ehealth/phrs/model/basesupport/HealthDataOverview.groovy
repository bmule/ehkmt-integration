package at.srfg.kmt.ehealth.phrs.model.basesupport

import com.google.code.morphia.annotations.Entity

import at.srfg.kmt.ehealth.phrs.model.baseform.CommonModelProps;

/**
 * Common vitals and healthProfile Uri
 *
 */
// ntity("vital_signs") ("phrsdocument")
@Entity
public class HealthDataOverview extends CommonModelProps{
	/*
	Double bodyHeight
	String measurementUnit
	String formatedBloodPressure
	Integer heartRate
	*/
	Map<String,String> formattedHealthData = new HashMap<String,String>()
	
	
	HealthProfile healthProfileUri //this is in commons as ownerUri
	
	
	//height --> to VitalCommon.height
	public HealthDataOverview(){
		
	}
}
