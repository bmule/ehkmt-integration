package at.srfg.kmt.ehealth.phrs.jsf.view.schedule;

/*
 * Copyright 2010 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
//@ ManagedBean(name="scheduleController")
//@ View Scoped
//@ RequestScoped
/**
 * @deprecated
 */
public class ScheduleController2 {

	private ScheduleModel eventModel;
	
	private ScheduleModel lazyEventModel;

	private ScheduleEvent event = new DefaultScheduleEvent();

	public ScheduleController2() {
		eventModel = new DefaultScheduleModel();
		eventModel.addEvent(new DefaultScheduleEvent("test1", previousDay8Pm(), previousDay11Pm()));
		eventModel.addEvent(new DefaultScheduleEvent("test2", today1Pm(), today6Pm()));
		eventModel.addEvent(new DefaultScheduleEvent("test3", nextDay9Am(), nextDay11Am()));
		eventModel.addEvent(new DefaultScheduleEvent("test4", theDayAfter3Pm(), fourDaysLater3pm()));
		
		lazyEventModel = new LazyScheduleModel() {
			
			@Override
			public void loadEvents(Date start, Date end) {
				clear();
				
				Date random = getRandomDate(start);
				addEvent(new DefaultScheduleEvent("Lazy Event 1", random, random));
				
				random = getRandomDate(start);
				addEvent(new DefaultScheduleEvent("Lazy Event 2", random, random));
			}	
		};
	}
	
	public Date getRandomDate(Date base) {
		Calendar date = Calendar.getInstance();
		date.setTime(base);
		date.add(Calendar.DATE, ((int) (Math.random()*30)) + 1);	//set random day of month
		
		return date.getTime();
	}
	
	public Date getInitialDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);
		
		return calendar.getTime();
	}
	
	public ScheduleModel getEventModel() {
		return eventModel;
	}
	
	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}
	public  Date nextHour(){
		Calendar t = (Calendar) today().clone();
		return nextHour(t);
	}
	public static Date nextHour(Calendar t) {
		
		t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE));
		t.set(Calendar.HOUR, t.get(Calendar.HOUR) + 1);
		t.set(Calendar.MINUTE, 0);
		t.set(Calendar.SECOND, 0);
		
		return t.getTime();
	}
	private Calendar today() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

		return calendar;
	}
	
	private Date previousDay8Pm() {
		Calendar t = (Calendar) today().clone();
		if(! CLOCK_24_HOUR) t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
		t.set(Calendar.HOUR, 8);
		
		return t.getTime();
	}
	
	private Date previousDay11Pm() {
		Calendar t = (Calendar) today().clone();
		if(! CLOCK_24_HOUR) t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
		t.set(Calendar.HOUR, 11);
		
		return t.getTime();
	}
	boolean CLOCK_24_HOUR=false;
	
	private Date today1Pm() {
		Calendar t = (Calendar) today().clone();
		if(! CLOCK_24_HOUR) t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 1);
		
		return t.getTime();
	}
	
	private Date theDayAfter3Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 2);		
		if(! CLOCK_24_HOUR)t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 3);
		
		return t.getTime();
	}

	private Date today6Pm() {
		Calendar t = (Calendar) today().clone(); 
		if(! CLOCK_24_HOUR) t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 6);
		
		return t.getTime();
	}
	
	private Date nextDay9Am() {
		Calendar t = (Calendar) today().clone();
		if(! CLOCK_24_HOUR) t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 9);
		
		return t.getTime();
	}
	
	private Date nextDay11Am() {
		Calendar t = (Calendar) today().clone();
		if(! CLOCK_24_HOUR) t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 11);
		
		return t.getTime();
	}
	
	private Date fourDaysLater3pm() {
		Calendar t = (Calendar) today().clone(); 
		if(! CLOCK_24_HOUR) t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
		t.set(Calendar.HOUR, 3);
		
		return t.getTime();
	}
	
	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}
	
	public void addEvent(ActionEvent actionEvent) {
		if(event.getId() == null)
			eventModel.addEvent(event);
		else
			eventModel.updateEvent(event);
		
		event = new DefaultScheduleEvent();
	}
	
	public void onEventSelect(ScheduleEntrySelectEvent selectEvent) {
		event = selectEvent.getScheduleEvent();
	}
	
	public void onDateSelect(DateSelectEvent selectEvent) {
		event = new DefaultScheduleEvent("", selectEvent.getDate(), selectEvent.getDate());
	}
	
	public void onEventMove(ScheduleEntryMoveEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
		
		addMessage(message);
	}
	
	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
		
		addMessage(message);
	}
	
	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	/**
	 * TODO create wrapper DefaultScheduleEvent impl to add functionality to correct data
	 * and with interface for setters, etc
	 * @param objs
	 * @return
	 */
	public static List<DefaultScheduleEvent> loadEvents(Collection objs){
		
		return null;
	}
}
