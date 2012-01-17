package at.srfg.kmt.ehealth.phrs.model.baseform

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity


/**
 * Physical Activities
 * Defined in Base Event
 *ObsActivityPhysical   
    }
 */

@Entity
public class  ObsActivityPhysical extends CommonModelProps{

	/**
	 * Use CommonModelProps.status and code
	 */

	String codeOtherText
	//String statusCode // statusCode ..can be string - true or false, later add additional codes
	//String frequencyCode
	//String durationCode
	
	public ObsActivityPhysical(){
		super();
		eventStatus=	PhrsConstants.EVENT_STATUS_EVENTABLE_NOT_ACTIONABLE
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
	
}
