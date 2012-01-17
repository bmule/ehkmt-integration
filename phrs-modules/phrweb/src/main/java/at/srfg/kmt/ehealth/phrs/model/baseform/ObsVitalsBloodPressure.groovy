package at.srfg.kmt.ehealth.phrs.model.baseform


import java.util.Date

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity


@Entity
//("phrsdocument")
class ObsVitalsBloodPressure extends CommonModelProps{

	/*
	 * note are already defined in CommonModelProps
	 */
	Integer systolic
	Integer diastolic
	Integer heartRate

	Date effectiveDate //could include time??
	Integer  mood
	
	public ObsVitalsBloodPressure(){
		super();
		eventActivated=	Boolean.TRUE
		eventStatus=	PhrsConstants.EVENT_STATUS_ACTION_EXECUTED_EXPLICIT
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
	
}
