package at.srfg.kmt.ehealth.phrs.presentation.utils

import java.util.Date

import org.joda.time.Duration
/**
 * 
 * provides a helper Duration object
 * Used for Action plan, to provide the duration time between two date times
 * TODO see http://mike-java.blogspot.com/2008/02/java-date-time-api-vs-joda.html
 */
/*
DateTime before = new DateTime();
Thread.sleep(30);
Interval interval = new Interval(before, new DateTime());
int timeInterval = interval.toDuration().getMillis();

DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

String formattedTime = dateTime.toString(dtf);
 */
class EventDuration implements Serializable{
	int days
	int hours
	int mins

	boolean same
	boolean sameHour
	boolean sameDay

	Duration duration
	


	public EventDuration(Date beginDate, Date endDate){
		
		long startInstant 	= beginDate ? beginDate.getTime() : 0
		long endInstant 	= endDate ? endDate.getTime() : 0
		
		init( startInstant,  endInstant)
	}
	
	public EventDuration(long startInstant, long endInstant){
			
		if(startInstant > endInstant){
			long temp=startInstant
			startInstant = endInstant
			endInstant = temp
		}
		
		init( startInstant,  endInstant)
	}
	/**
	 * 
	 */
	protected void init(long startInstant, long endInstant){
		duration = getDuration(startInstant,  endInstant)
		days = duration.toStandardDays().getDays()
		hours = duration.toStandardHours().getHours()
		mins = duration.toStandardMinutes() .getMinutes()
		
		same = days+hours+mins == 0 ? true :false
		sameHour = days+hours == 0 ? true :false
		sameDay = days == 0 ? true :false
	}

	@Override
	public String toString(){
		//GString
		return this.toString('Days', 'Hrs', 'Min', '', true)
	}
	/**
	 * Returns formatted string depending on whether there is a difference for each unit
	 *	days > 0 ? "$labelDays $days $labelHours $hours $labelMins $mins" :
	 *	hours > 0 ? "$labelHours $hours $labelMins $mins" :
	 *	mins > 0 ? "$labelMins $mins" :
	 *	sameTime ? sameTime :""
	 * 
	 * @param labelDays
	 * @param labelHours
	 * @param labelMins
	 * @param sameTime - if the times are the same, otherwise null returns blank
	 * @return
	 * 
	 */
	public String toString(String labelDays,String labelHours, String labelMins, String sameTime, boolean oneFormat){
		//GString
		String labDay= 	labelDays 	? labelDays 	+ ':'   : "dd:"
		String labHour= labelHours 	? labelHours 	+ ':'	: "hh:"
		String labMin= 	labelMins 	? labelMins 	+ ':'   : "mm:"
		
		String out=  sameTime ? sameTime :
				(days  > 0  || oneFormat ) ? "$labDay $days $labHour $hours $labMin $mins" :
				hours > 0 ? "$labHour $hours $labMin $mins" :
				mins  > 0 ? "$labMin $mins" :
				""
				
		return out
				
	}
	/**
	 * 
	 * @param labelDays
	 * @param labelHours
	 * @param labelMins
	 * @return  returns blank if same time
	 */
	public String toString( String labelDays,String labelHours,  String labelMins, boolean oneFormat){
		this.toString(labelDays, labelHours, labelMins, null, oneFormat)
	}


	/**
	 *
	 * @param beginDate
	 * @param endDate
	 * @return
	 * http://joda-time.sourceforge.net/apidocs/org/joda/time/Duration.html
	 */
	public static Duration getDuration(Date beginDate, Date endDate){
		Duration duration = new Duration( beginDate,  endDate)
	}
	
	public static Duration getDuration(long startInstant, long endInstant){
		Duration duration = new Duration( startInstant,  endInstant)
	}
}
