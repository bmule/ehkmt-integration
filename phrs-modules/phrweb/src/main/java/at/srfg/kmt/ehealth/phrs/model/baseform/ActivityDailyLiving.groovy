package at.srfg.kmt.ehealth.phrs.model.baseform

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity


@Entity
public class ActivityDailyLiving extends CommonModelProps {
	/* 
activityOfDailyLivingItem.label

activityOfDailyLivingItem.activityCode.label
3 OPTIONS yes, no, unknown
activityOfDailyLivingItem.valueActivity.label

TERM_ACTIVITY_OF_DAILY_LIVING_PHRS
TERM_ACTIVITY_LIFESTYLE_STATUS_VALUE
*/
/**
code 
status
	**/
	
	public ActivityDailyLiving(){
		super();
		eventActivated=	Boolean.FALSE
		eventStatus=	PhrsConstants.EVENT_STATUS_EVENTABLE_NOT_ACTIONABLE
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
	
}
