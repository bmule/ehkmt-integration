package at.srfg.kmt.ehealth.phrs.model.baseform

import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Entity



@Entity
public class ObsProblem extends CommonModelProps{
	/* 
	 code
	status
	beginDate, endDate
	note
	
	use categories to add Hl7 types: problem root code: finding, problem, etc codes or ?
	*/
	
	public ObsProblem(){
		super();
		
		eventActivated=	Boolean.TRUE
		eventStatus=	PhrsConstants.EVENT_STATUS_EVENTABLE_NOT_ACTIONABLE
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
		//default category
		category=Constants.HL7V3_COMPILANT
        //Constants.HL7V3_SYMPTOM
		//HL7V3_FINDING
	}
	
}
