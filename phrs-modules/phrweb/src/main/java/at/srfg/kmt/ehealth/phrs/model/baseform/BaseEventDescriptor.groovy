package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date
import java.util.List



/**
 * Observations might connect to an Event in the calendar
 * Some observation objects are temporal, others note 
 */
// @Entity("events_base")
//@Embedded
/**
 * 
 * @deprecated
 *
 */
public class BaseEventDescriptor{// implements CalendarEvent
	//do not inherit BaseModelProps or CommonModelProps
	
	/**
	 * Event type
	 * High level
	 */
	//@Indexed
	String type
	/**
	 * a code e.g. jogging
	 */
	//@Indexed
	String subType
	
	String eventStatus
	
	//@Indexed
	String aboutResource
	//@Indexed
	String aboutResourceType
	/**
	 * label or title
	 * This title might be created based on type or form, user can change later
	 */
	String title 
	/**
	 * description or content
	 */
	String content 
	//String eventScheduleCode
	
	String headerColor
	String contentColor
	
	Date beginDate;
	Date endDate;
	
	boolean locked;
	/*
	 * optional, default belongs to ownerUri
	 * User can publish calendar into a new space and belong to both ownerUri and
	 * other calendar
	 */
	//ObjectId partOfCalendar
	/**
	 * These can be other health resources such as objects, observations, etc
	 * resourceUri
	 */
	List<String> refersToResources

	
	String view

   public String getZclass() {
	   return "z-calevent";
   }
   
	//explains non temporal or fine detail of temporal event.
	//String temporalStatusCode
	
	/**
	 * occurs once - effective datetime, weight on a date  12:04
	 * occurs range in day - eg appointment
	 */
	String occurenceTypeCode
	
	//Integer occurenceFrequency=1
	
	Boolean hasRepeatRules=Boolean.FALSE
	/*
	 * observation or event start time or effective time
	 */
	//Date effectiveDateTime
	/*
	 * the event lasted one hour, this is the end time
	 * but patient when to consultation for one week each day.
	 * Use occurenceUntilDate for final day
	 */
	//Date effectiveDateTimeEnd

	/**
	 * end date or repeats until date
	 */
	Date occurenceUntilDate
	
	/**
	 * future
	 */
	String repeatRules
	
	String getActivityCodeLabel(){
		return "TODO label lookup"
	}
	public BaseEventDescriptor(){
		super();
		refersToResources= []
	}

}
