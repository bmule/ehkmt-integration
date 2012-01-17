package at.srfg.kmt.ehealth.phrs.model.baseform


import java.util.Date

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity
/**
 * Alternative means to write multiple records from a form
 *
 */

@Entity
public class  ObsVitalsRecord extends BasePhrsModel{
	
	/**
	* Interop code e.g. blood pressure
	*/
	//use code  in commons
	/**
	 * A means to define the value if necessary
	 */
	String property //Can be used for the value e.g. either systolic or diastolic

	String value
	Date observationDate
	
	public ObsVitalsRecord(){
		super();
		eventActivated=	Boolean.TRUE
		eventStatus=	PhrsConstants.EVENT_STATUS_ACTION_EXECUTED_EXPLICIT
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
	
}
