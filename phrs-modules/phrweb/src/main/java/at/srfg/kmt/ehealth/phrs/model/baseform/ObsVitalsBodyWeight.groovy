package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity

@Entity
//("phrsdocument")
public class  ObsVitalsBodyWeight extends CommonModelProps {
	/*
	 * Use beginDate as effectiveDate
	 */
	Double bodyWeight 
	Double bmi
	Double bodyHeight //store and retrieve from separate resource, not the body weight resource
	
	String measurementUnit
	Integer mood
	

	
	
	public ObsVitalsBodyWeight(){
		super();
		eventActivated=	Boolean.TRUE
		eventStatus=	PhrsConstants.EVENT_STATUS_ACTION_EXECUTED_EXPLICIT
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
}
