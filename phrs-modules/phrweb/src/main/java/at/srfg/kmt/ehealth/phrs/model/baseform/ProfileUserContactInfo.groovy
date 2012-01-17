package at.srfg.kmt.ehealth.phrs.model.baseform

import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Entity



@Entity
public class  ProfileUserContactInfo extends ProfileContactInfo{

	public ProfileUserContactInfo(){
		super()
		contactType=PhrsConstants.PARAM_NAME_CONTACT_TYPE_HEALTH_CARE_USER
		
		eventStatus=	PhrsConstants.EVENT_STATUS_NOT_EVENTABLE
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}

}
