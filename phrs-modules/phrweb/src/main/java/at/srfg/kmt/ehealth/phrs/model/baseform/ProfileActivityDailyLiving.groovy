package at.srfg.kmt.ehealth.phrs.model.baseform


import java.util.Set
import java.util.TreeMap

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Property

@Entity
public class  ProfileActivityDailyLiving extends CommonModelProps{
	//Using Base properties
	/*
	 * ADL and status
	 * Simpler, but no note
	 */
	//@Property(concreteClass = java.util.TreeMap.class)
	//TreeMap<String,String> activityStatus = new TreeMap<String,String>()
	/*
	 * Optional, ADL and ADL object with code, status, note
	 */
	//@Embedded
	//Set<ActivityDailyLivingSimple> activities = new HashSet<ActivityDailyLivingSimple>()
	
	public ProfileActivityDailyLiving(){
		super();
		eventActivated=	Boolean.TRUE
		eventStatus=	PhrsConstants.EVENT_STATUS_NOT_EVENTABLE
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY
	}
}
