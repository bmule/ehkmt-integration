package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date

import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Transient


/**
 * Physical Activities
 * Defined in Base Event
 *ObsActivityPhysical   
 }
 */

@Entity("ActionActivityBase")
public class  ActionActivityBase extends CommonModelProps{

	/**
	 * Use CommonModelProps.category and code, start and end dates
	 * label or title for activityName,
	 * can use status code for later .. actually done or not. 
	 */

	//String frequencyCode
	//String durationCode in base

	//Time durationTime

	@Transient
	String durationElapsedTime
	@Transient
	String durationElapsedTimeUnit
	//it might be easier to store these specially for now? or as transient?
	String beginDateText
	String endDateText

	Map<String,String> properties

	public ActionActivityBase(){
		super();
		properties= [:]
		//private Calendar cal = Calendar.getInstance();

		eventActivated=	Boolean.TRUE
		eventStatus=	PhrsConstants.EVENT_STATUS_ACTION_PLANNED
		eventTheme = 	PhrsConstants.EVENT_THEME_EVENT

		/**
		 * Need to set title or label, content, beginDate, endDate, locked
		 * color related properties are set to defaults
		 */
		setHeaderColor(checkHeaderColor(null));
		setContentColor(checkContentColor(null));


	}

	/**
	 *
	 * @param form resource
	 */
	public ActionActivityBase(BasePhrsModel base){
		super();
		setHeaderColor(checkHeaderColor(base.getHeaderColor()));
		setContentColor(checkContentColor(base.getContentColor()));

		setContent(base.getContent());
		setTitle(base.getTitle());
		setBeginDate(base.getBeginDate());
		setEndDate(base.getEndDate());
		setLocked(base.getLocked());

	}

	public ActionActivityBase(Date beginDate, Date endDate, String headerColor, String contentColor, String content) {
		super();
		setHeaderColor(checkHeaderColor(headerColor));
		setContentColor(checkContentColor(contentColor));

		setContent(content);
		setBeginDate(beginDate);
		setEndDate(endDate);
	}

	public ActionActivityBase(Date beginDate, Date endDate, String headerColor, String contentColor, String content, String title) {

		setHeaderColor(checkHeaderColor(headerColor));
		setContentColor(checkContentColor(contentColor));
		setContent(content);
		setTitle(title);
		setBeginDate(beginDate);
		setEndDate(endDate);
	}
	public ActionActivityBase(Date beginDate, Date endDate, String headerColor, String contentColor, String content, String title, boolean locked) {

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
		if(temp && temp.length() > 2){

		} else {
			temp = PhrsConstants.CALENDAR_EVENT_HEADER_COLOR_DEFAULT;
		}
		return temp;
	}
	private String checkContentColor(String color){
		String temp=color;
		if(temp && temp.length() > 2){

		} else {
			temp = PhrsConstants.CALENDAR_EVENT_CONTENT_COLOR_DEFAULT;
		}
		return temp;
	}

}
