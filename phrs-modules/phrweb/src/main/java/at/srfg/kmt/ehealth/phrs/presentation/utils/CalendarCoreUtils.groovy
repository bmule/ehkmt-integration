package at.srfg.kmt.ehealth.phrs.presentation.utils

import groovy.time.TimeCategory
import groovy.time.TimeDuration

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collection
import java.util.Date
import java.util.LinkedList
import java.util.List

import org.joda.time.DateTime

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.i18n.I18Tool
import at.srfg.kmt.ehealth.phrs.model.basesupport.CalendarEventSetter

class CalendarCoreUtils {

	/**
	 * Set minutes, seconds, milli to 0
	 * @return
	 */
	public static getInitDate(){

		Calendar tempCal = Calendar.getInstance()
		//set min,
		tempCal = setCalendar(tempCal, 0, 0, 0, 0)
		Date date = tempCal.getTime()
		return date
	}

	public static TimeDuration getTimeDuration(final Date endDate, final Date startDate) {
		TimeDuration timeDuration
		if(endDate && startDate) timeDuration= TimeCategory.minus(endDate, startDate)
		return timeDuration
	}

	public static String getTimeDurationFormated(final TimeDuration td, String delimiter, boolean shortTimeLabel){
		def duration=""
		//inititiatlize in case no difference
		String lang='en'//get UI language from tool
		if(td){
			if(td.getYears() > 0){
				duration <<= td.getYears()
				duration <<= " "
				duration <<= I18Tool.getInstance().getLabelFromi18Tool('calendar.time.duration.short.years.short')
				duration <<= delimiter
			}
			if(td.getMonths() > 0){
				duration <<= td.getMonths()
				duration <<= " "
				duration <<= I18Tool.getInstance().getLabelFromi18Tool('calendar.time.duration.short.months.short')
				duration <<= delimiter
			}
			if(td.getDays() > 0){
				duration <<= td.getDays()
				duration <<= " "
				duration <<= I18Tool.getInstance().getLabelFromi18Tool('calendar.time.duration.short.days.short')
				duration <<= delimiter
			}
			if(td.getHours() > 0){
				duration <<= td.getHours()
				duration <<= " "
				duration <<= I18Tool.getInstance().getLabelFromi18Tool('calendar.time.duration.short.hours.short')
				duration <<= delimiter
			}
			if(td.getMinutes() > 0){
				duration <<= td.getMinutes()
				duration <<= " "
				duration <<= I18Tool.getInstance().getLabelFromi18Tool('calendar.time.duration.short.Minutes')
				duration <<= delimiter
			}
		}
		if(duration ) return duration
		return ""
	}

	/**
	 * User default interval PhrsConstants.TIME_INTERVAL_LIST
	 * set to 15 minutes, not less because of problems with UI tools e.g. 5 minute interval causes trouble 
	 *  
	 * @return
	 */

	public static List getTimeDropdown() {
		return getTimeIntervalList()
	}
	public static List getTimeIntervalList() {
		return getTimeIntervalList(PhrsConstants.TIME_INTERVAL_LIST)
	}
	//	Date data = TimeCategory.plus(date, duration)
	/**
	 * 
	 * @param timeInterval
	 * @return
	 */
	public static List<String> getTimeIntervalList(int timeInterval) {
		//not thread safe, don't put elsewhere!
		SimpleDateFormat timeFormater = new SimpleDateFormat('HH:mm')
		List dateTimes = new LinkedList()

		Calendar tempCal = Calendar.getInstance()
		tempCal = setCalendar(tempCal, 0, 0, 0, 0)
		// 1440 =  (24* 60)
		int maxValue =   1440 /  timeInterval
		// 5 minute intervals = 288
		for (int i = 0; i < maxValue; i++) {
			dateTimes.add(timeFormater.format(tempCal.getTime()))
			//use 15 minute or half hour intervals, any thing less causes problems in UI tools
			tempCal.add(Calendar.MINUTE, timeInterval)
		}
		return dateTimes
	}
	//private SimpleDateFormat timeFormat = new SimpleDateFormat('HH:mm');
	public static String getTimeStringHHmm(Date date){

		String timeString
		if(date){
			timeString = new SimpleDateFormat('HH:mm').format(date)
		}
		return timeString
	}

	/**
	 * Using Joda DateTime instead of Calendar to update time in Date
	 * @param date
	 * @param hours - string or int
	 * @param mins string or in
	 * @return updated Date with new time
	 */
	public static Date updateDateTime(Date date, def hours, def mins){
		int theHours = 0
		int theMins = 0
		Date updated = date

		if( date && hours){

			if(hours instanceof String && ((String)hours).isNumber()) theHours =  ((String)hours).toInteger()
			if(mins && mins instanceof String &&  ((String)mins).isNumber()) theMins =  ((String)mins).toInteger()

			DateTime dt = new DateTime(Date.getMillisOf(date));
			dt.withHourOfDay(theHours)
			dt.withMinuteOfHour(theMins)
			dt.withSecondOfMinute(0)

			updated= dt.toDate()

		}

		return updated
	}

	/**
	 * 
	 * @param dateToUpdate
	 * @param HH_mm
	 * @param timeZone
	 * @return
	 */
	public static Date updateDateTime(Date dateToUpdate, def HH_mm, TimeZone timeZone){

		return updateDateTime(Calendar.getInstance(timeZone), dateToUpdate,HH_mm)
	}
	/**
	 * 
	 * @param dateToUpdate
	 * @param HH_mm
	 * @return
	 */
	public static Date updateDateTime(Date dateToUpdate, def HH_mm){

		return updateDateTime(Calendar.getInstance(), dateToUpdate,HH_mm)
	}
	/**
	 * 
	 * @param cal
	 * @param dateToUpdate
	 * @param HH_mm - a string (HH:mm) or array with two elements hours, minutes
	 * String should be created by: new SimpleDateFormat('HH:mm')
	 * 
	 * 
	 * @return
	 */
	public static Date updateDateTime(Calendar cal, Date dateToUpdate, def HH_mm){
		Date updated = dateToUpdate
		if(updated && HH_mm){
			String[] times

			if(HH_mm instanceof String){
				times = HH_mm.split(':')
			} else {
				times = HH_mm
			}
			cal.setTime(updated);
			cal = setCalendar(cal, Integer.parseInt(times[0]), Integer.parseInt(times[1]), 0, 0);
			updated = cal.getTime();
		}
		return updated
	}
	/**
	 * 
	 * @param cal
	 * @param dateToUpdate
	 * @param hours
	 * @param mins - can be null or blank, default 0
	 * @return
	 */

	public static Date updateDateTime(Calendar cal, Date dateToUpdate,  String hours, String mins){
		Date updated = dateToUpdate
		try{
			if(updated && hours){

				cal.setTime(updated);
				if(mins)
					cal = setCalendar(cal, Integer.parseInt(hours), Integer.parseInt(mins), 0, 0);
				else
					cal = setCalendar(cal, Integer.parseInt(hours), 0, 0, 0);

				updated = cal.getTime();
			}
		} catch(Exception e){
		}

		return updated
	}


	/**
	 * 	  
	 * @param calendar
	 * @param hod
	 * @param minutes
	 * @param seconds
	 * @param milliseconds
	 * @return
	 */
	public static Calendar setCalendar(Calendar calendar, int theHour, int theMinutes, int theSeconds, int theMilliseconds) {
		calendar.set(Calendar.HOUR_OF_DAY, 	theHour)
		calendar.set(Calendar.MINUTE, 		theMinutes)
		calendar.set(Calendar.SECOND, 		theSeconds)
		calendar.set(Calendar.MILLISECOND, 	theMilliseconds)

		return calendar
	}
	public static String getTimeListListString(int interval){
		return getTimeListListString(interval,false)
	}
	public static String getTimeListListString(int interval,boolean filterBadMinutes){
		List<String> list= getTimeList(interval,filterBadMinutes)

		def label=""
		label <<= '['
		list.eachWithIndex { item, index->
			if(index > 0) label <<= ','
			label <<= '\''
			label <<= item
			label <<= '\''
		}
		label <<= ']'

		return label
	}
	/**
	 * 
	 * @param timeInterval
	 * @param filterBadMinutes
	 * 
	 * @return String SimpleDateFormat('HH:mm')
	 */
	public static List<String> getTimeList(int timeInterval,boolean filterBadMinutes){
		SimpleDateFormat timeFormater = new SimpleDateFormat('HH:mm')
		List dateTimes = new LinkedList()

		Calendar tempCal = Calendar.getInstance()
		tempCal = setCalendar(tempCal, 0, 0, 0, 0)
		// 1440 =  (24* 60)
		int maxValue =   1440 /  timeInterval


		// 5 minute intervals = 288
		for (int i = 0; i < maxValue; i++) {
			int filterMin = tempCal.get(Calendar.MINUTE);
			if (! filterBadMinutes || (filterBadMinutes && filterMin != 5 && filterMin != 25 && filterMin != 35 && filterMin != 55)) {
				dateTimes.add(timeFormater.format(tempCal.getTime()))
			}
			//println('time='+tempCal.getTime())

			tempCal.add(Calendar.MINUTE, timeInterval)
		}
		return dateTimes


	}
	/**
	 * 
	 * @param timeInterval
	 * @param filterBadMinutes
	 * @return String SimpleDateFormat('mm')
	 */
	public static List<String> getTimeListMinutes(int timeInterval,boolean filterBadMinutes){
		SimpleDateFormat timeFormater = new SimpleDateFormat('mm')
		List dateTimes = new LinkedList()

		Calendar tempCal = Calendar.getInstance()
		tempCal = setCalendar(tempCal, 0, 0, 0, 0)
		// 1440 =  (24* 60)
		int maxValue =   1440 /  timeInterval


		// 5 minute intervals = 288
		for (int i = 0; i < maxValue; i++) {
			int filterMin = tempCal.get(Calendar.MINUTE);
			if (! filterBadMinutes || (filterBadMinutes && filterMin != 5 && filterMin != 25 && filterMin != 35 && filterMin != 55)) {
				dateTimes.add(timeFormater.format(tempCal.getTime()))
			}
			//println('time='+tempCal.getTime())

			tempCal.add(Calendar.MINUTE, timeInterval)
		}
		return dateTimes
	}
	public static String getTimeListListStringHours(){
		List<String> list= getTimeListHours()

		def label=''
		label <<= '['
		list.eachWithIndex { item, index->
			if(index > 0) label <<= ','
			label <<= '\''
			label <<= item
			label <<= '\''
		}
		label <<= ']'

		return label
	}
	public static String getTimeListListStringMinutes(int interval,boolean filterBadMinutes){
		List<String> list= getTimeListMinutes(interval,filterBadMinutes)

		def label=''
		label <<= '['
		list.eachWithIndex { item, index->
			if(index > 0) label <<= ','
			label <<= '\''
			label <<= item
			label <<= '\''
		}
		label <<= ']'

		return label
	}
	/**
	 * 
	 * @param timeInterval
	 * @param filterBadMinutes
	 * @return String SimpleDateFormat('HH')
	 */
	public static List<String> getTimeListHours(){
		SimpleDateFormat timeFormater = new SimpleDateFormat('HH')
		List dateTimes = new LinkedList()

		Calendar tempCal = Calendar.getInstance()
		tempCal = setCalendar(tempCal, 0, 0, 0, 0)
		// 1440 =  (24* 60)
		int maxValue =   1440 /  60


		// 5 minute intervals = 288
		for (int i = 0; i < maxValue; i++) {
			dateTimes.add(timeFormater.format(tempCal.getTime()))
			tempCal.add(Calendar.MINUTE, 60)
		}
		return dateTimes
	}

	public static Collection calendarEventCleaner(Collection<CalendarEventSetter> calendarEvents){
		return calendarEventCleaner( calendarEvents, null);
	}
	/*
	 public static List calendarEventCleaner(Collection<CalendarEventSetter> calendarEvents, Integer endDateMillsecFix){
	 Collection acceptedEvents= []
	 //add one hour if enddate problem
	 int endDateMillisecondFix = endDateMillsecFix ? endDateMillsecFix : 3600000
	 if(calendarEvents){
	 //clean up dates (not null, b > e), content, title -cannot be null
	 calendarEvents.each { CalendarEventSetter ce ->
	 //Date tempBegin=null;
	 boolean ok=true
	 boolean okStart=true
	 boolean okEnd=true
	 long b=0;
	 long e=0;
	 try {
	 if(ce.getBeginDate()){
	 b = ce.getBeginDate().getTime();
	 } 
	 if(ce.getEndDate()!=null){
	 e = ce.getEndDate().getTime();
	 } else {
	 e= b;
	 }
	 //fix odd errors
	 if(b > e){
	 //begin date GREATER THAN end date!
	 //e = b + endDateMillisecondFix;
	 e = b +endDateMillisecondFix;
	 ce.setEndDate(new Date(e));
	 } else if(b == e){
	 //begin date EQUALS end date!
	 //e = b + endDateMillisecondFix; 
	 e= b +endDateMillisecondFix;
	 ce.setEndDate(new Date(e));
	 } else if (e < 10 && b > 1000){
	 //no end date
	 //e = b + endDateMillisecondFix;
	 e= b
	 ce.setEndDate(new Date(e));
	 } else if (b < 10 && e > 1000){
	 //no begin date, but end date ... for a calendar, could set today...
	 //e = b + endDateMillisecondFix;
	 int today= new Date().getTime()
	 if(e > today) b= today
	 else b = e
	 ce.setBeginDate(new Date(b));
	 } else {
	 //fail
	 ok = false;
	 }
	 if(ok){
	 //fix content, not null
	 String content = ce.getContent();
	 if(ce.getContent()){
	 ce.setContent(ce.getContent().trim());
	 } else {
	 ce.setContent('...')
	 }
	 //fix title, not null
	 if(ce.getTitle()){
	 ce.setTitle(ce.getTitle().trim());
	 if(ce.getTitle().size()==0) ce.setTitle('Event')
	 } else {
	 ce.setTitle('Event')
	 }
	 acceptedEvents.add(ce)
	 }
	 } catch(Exception ex){
	 println('exception '+ex)
	 }
	 }
	 }
	 return acceptedEvents
	 }*/


}
