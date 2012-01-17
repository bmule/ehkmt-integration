package at.srfg.kmt.ehealth.phrs.jsf.view.schedule;
import java.io.Serializable
import java.util.Collection
import java.util.Date
import java.util.List

import org.primefaces.model.DefaultScheduleEvent
import org.primefaces.model.DefaultScheduleModel
import org.primefaces.model.ScheduleEvent
import org.primefaces.model.ScheduleModel

import at.srfg.kmt.ehealth.phrs.model.baseform.ActionPlanEvent
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsModel


/*
 TODO http://johannburkard.de/blog/programming/java/date-time-parsing-formatting-joda-time.html
 Revise date fixing using joda and joda utils
 plus minutes, etc
 */
public class PhrsScheduleEvent extends DefaultScheduleEvent implements Serializable {
	//BasePhrsModel base
	String status
	String code
	String category
	//Duration duration
	//EventDuration timeDuration
	private ActionPlanEvent internalActionPlanEvent
	
	public void setContent(String content){
		data = content
	
	}
	public String getContent(){
		if(data && data instanceof String){
			return data;
		}
		return ""
	}
	public PhrsScheduleEvent(String title, Date start, Date end, boolean allDay) {
		super(title, start, end, allDay);
		init()
		
	}
	public PhrsScheduleEvent(String title, Date start, Date end, Object data) {
		super(title, start, end, data);
		init()
		
	}
	public PhrsScheduleEvent(String title, Date start, Date end,
			String styleClass) {
		super(title, start, end, styleClass);
		init()
		
	}
	public PhrsScheduleEvent(String title, Date start, Date end) {
		super(title, start, end);
		init()
		
	}
	public PhrsScheduleEvent(){
		init()
	}
	protected void init(){
		//TODO 
		//timeDuration = new EventDuration(this.getStartDate(),this.getEndDate())
		
		
		
	}
	
	public String getDurationString(){
		//eventDuration = new EventDuration(getStartDate,getEndDate())
		
		//return eventDuration.toString()
		return '...'
	}
	
	public String getDurationString(String labelDays,String labelHours,String labelMins){
		//eventDuration = new EventDuration(getStartDate,getEndDate())
		
		//return eventDuration.toString(labelDays, labelHours, labelMins, "", true)
		return '...'
	}
	
	public ActionPlanEvent getActionPlanEvent(){
		
	//this is the "selected" resource that must be used to set in the controller
		if(! internalActionPlanEvent){
			
			internalActionPlanEvent = new ActionPlanEvent();
			
		}
		internalActionPlanEvent.title 		= title
		internalActionPlanEvent.code 		= code
		internalActionPlanEvent.category 	= category
		internalActionPlanEvent.status 		= status 
		internalActionPlanEvent.content 	= content
		
		internalActionPlanEvent.beginDate	= startDate
		internalActionPlanEvent.endDate		= endDate
		
		internalActionPlanEvent.allDay		= startDate == endDate ? true : false //this.getAllDay()
		
		return internalActionPlanEvent
	}
	
	public PhrsScheduleEvent(ActionPlanEvent base){			
		super(base.getTitle(),base.getBeginDate(),base.getEndDate(),base.getAllDay())
		
		internalActionPlanEvent = base
		
		setId(base.getResourceUri())
		setStatus(base.getStatus())
		//this.category = base.getCategory()
		setCode( base.getCode())
		setCategory( base.getCategory())
		
		setStartDate(base.getBeginDate())
		setEndDate(base.getEndDate())
		setTitle(base.title ? base.title.trim() : 'Event')
		setAllDay(base.getAllDay())
		
		//title = base.title ? base.title.trim() : null
		//trimmed
		//title = title ? title : 'Event'

		if(base.getContent()) setContent(base.getContent())
		

		
		//this.setData(data)
		//this.setStyleClass(styleClass)
		//this.setProperty(property, value)

	}
	/**
	 * 
	 * @param list
	 * @return
	 */
	public static Collection<PhrsScheduleEvent> transform(List<BasePhrsModel> list){
		Collection collection = []
		
		list.each { item ->
			
			
			PhrsScheduleEvent event = new PhrsScheduleEvent(item)
			
			collection.add((ScheduleEvent)event) 
		}
		
		return collection
	}
	public static ScheduleModel getScheduleModel(Collection list){
		Collection clean= calendarEventCleaner(list,null);
		ScheduleModel model = new DefaultScheduleModel(clean);
		return model;
	}
	
	public static Collection<ScheduleEvent> calendarEventCleaner(Collection<BasePhrsModel> calendarEvents, Integer endDateMillsecFix){
		Collection acceptedEvents= []
		//add one hour if enddate problem
		int endDateMillisecondFix = endDateMillsecFix ? endDateMillsecFix : 3600000
		
		if(calendarEvents){
			
			//clean up dates (not null, b > e), content, title -cannot be null
			calendarEvents.each() { BasePhrsModel ce ->
				boolean allday=ce.getAllDay()
	
				long b=0;
				long e=0;
				
				try {
					if(ce.getBeginDate()){
						b = ce.getBeginDate().getTime();
					} else {
						if(ce.getEndDate()!=null) b= ce.getEndDate().getTime();
					}
				  
					
					if(ce.getEndDate()!=null){
						e = ce.getEndDate().getTime();
					} else {
						e= b;
						allday=true
					}
					//fix odd errors
					if(b==0){
						b= e
						allday=true
					}
					if(b > e){
						//begin date GREATER THAN end date!
						//e = b + endDateMillisecondFix;
						e = b +3600000;//endDateMillisecondFix;
						//ce.setEndDate(new Date(e));
					} else if(b == e){
						//begin date EQUALS end date!
					
						//e= b +endDateMillisecondFix;;
						
					} 
					
					if(b > 0){
						PhrsScheduleEvent event = new PhrsScheduleEvent(ce)
						event.setAllDay(allday)
						event.setStartDate(new Date(b));					
						
						if(e > 0){
							event.setEndDate(new Date(e));
						} else {
							event.setEndDate(new Date(b));
							event.setAllDay(true)
						}

						acceptedEvents.add(event)
					}
					
				} catch(Exception ex){
					println('exception '+ex)
				}
			}
			
		}
		return acceptedEvents
	}
	

}
