package at.srfg.kmt.ehealth.phrs.model.baseform

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity


@Entity
public class  ProfileMedicalContactInfo extends ProfileContactInfo{

	String noteAgents //secretary, nurse names and other notes
	
	public ProfileMedicalContactInfo(){
		super()
		
		contactType=PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_PROVIDER
		
		eventStatus=	PhrsConstants.EVENT_STATUS_NOT_EVENTABLE
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
	
	 
	 
}
