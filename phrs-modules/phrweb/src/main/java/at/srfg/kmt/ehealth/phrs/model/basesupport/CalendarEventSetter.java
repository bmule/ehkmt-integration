package at.srfg.kmt.ehealth.phrs.model.basesupport;

import java.util.Date;

//import org.zkoss.calendar.api.CalendarEvent;
//extends CalendarEvent
public interface CalendarEventSetter  {
	
	public void setBeginDate(Date beginDate);
	public void setEndDate(Date endDate);
	public void setTitle(String title);
	public void setContent(String content);
}
