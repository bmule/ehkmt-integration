package at.srfg.kmt.ehealth.phrs.model.baseform;

 
import java.util.Date;

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity;
@SuppressWarnings("serial")
@Entity("ActionActivityBase")
public class CalendarEventWrapper extends ActionActivityBase  {//extends SimpleCalendarEvent {
	
	/**
	 * Need to set title or label, content, beginDate, endDate, locked
	 * color related properties are set to defaults
	 */
	public CalendarEventWrapper(){

        setHeaderColor(checkHeaderColor(null));
        setContentColor(checkContentColor(null));
        
	}
	/**
	 * 
	 * @param form resource
	 */
	public CalendarEventWrapper(BasePhrsModel base){
		
        setHeaderColor(checkHeaderColor(base.getHeaderColor()));
        setContentColor(checkContentColor(base.getContentColor()));

        setContent(base.getContent());
        setTitle(base.getTitle());
        setBeginDate(base.getBeginDate());
        setEndDate(base.getEndDate());
        setLocked(base.getLocked());
        
	}
	
    public CalendarEventWrapper(Date beginDate, Date endDate, String headerColor, String contentColor, String content) {
 	
        setHeaderColor(checkHeaderColor(headerColor));
        setContentColor(checkContentColor(contentColor));
        
        setContent(content);
        setBeginDate(beginDate);
        setEndDate(endDate);
    }
 
    public CalendarEventWrapper(Date beginDate, Date endDate, String headerColor, String contentColor, String content, String title) {
    	
        setHeaderColor(checkHeaderColor(headerColor));
        setContentColor(checkContentColor(contentColor));
        setContent(content);
        setTitle(title);
        setBeginDate(beginDate);
        setEndDate(endDate);
    }
    public CalendarEventWrapper(Date beginDate, Date endDate, String headerColor, String contentColor, String content, String title, boolean locked) {
    	
    	setHeaderColor(checkHeaderColor(headerColor));
        setContentColor(checkContentColor(contentColor));
        setContent(content);
        setTitle(title);
        setBeginDate(beginDate);
        setEndDate(endDate);
        setLocked(locked);
    }
    
    private String checkHeaderColor(String color){
    	String temp=color;
    	if(temp!=null && temp.length() > 4){
    		
    	} else {
    		temp = PhrsConstants.CALENDAR_EVENT_HEADER_COLOR_DEFAULT;
    	}
    	return temp;
    }
    private String checkContentColor(String color){
    	String temp=color;
    	if(temp!=null && temp.length() > 4){
    		
    	} else {
    		temp = PhrsConstants.CALENDAR_EVENT_CONTENT_COLOR_DEFAULT;
    	}
    	return temp;
    }

}
