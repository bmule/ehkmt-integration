package at.srfg.kmt.ehealth.phrs.model.baseform
import groovy.transform.EqualsAndHashCode

import java.io.Serializable
import java.util.Date
import java.util.List

import org.bson.types.ObjectId

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropTermTransformer
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id
import com.google.code.morphia.annotations.Indexed
import com.google.code.morphia.annotations.Transient

@groovy.transform.EqualsAndHashCode
@Entity
public class BasePhrsModel implements  Serializable, Cloneable {
	@Id
	ObjectId id
	//private final static Logger logger = LoggerFactory.getLogger(BasePhrsModel.class);
	@Transient
	String strCreateDate
	@Transient
	String strId
        //resource is newly imported, but not yet saved
    	@Transient
	boolean newImport=false
	//temp fields for UI forms needed, these cause trouble as hidden fields
	/**
	 * UI fix
	 * @return
	 */
	public String getTempCreateDate(){
		//use existing date to create string
		String dateStr= createDate  ?  HealthyUtils.formatDate( createDate, (String)null, InteropAccessService.DATE_PATTERN_INTEROP_DATE_TIME) : null 
		//if null, check if the string temp version was saved for the UI
		dateStr = dateStr ? dateStr : strCreateDate
		return dateStr
		}
	
	public void setTempCreateDate(String strCreateDate){
		this.strCreateDate=strCreateDate
	}
	/**
	* UI fix
	* @return
	*/
	public String getTempId(){
		String value = id ? id.toString() : strId
		return value
	}
	
	public void setTempId(String strId){
		this.strId=strId
	}
	


	/**
	 * GUID based uri, rather than linking to id. for referencing the parent,etc
	 */
	@Indexed
	String resourceUri

	String type

	/**
	 * Code is normally derived from the standards based vocabulary and used for interopability purposes
	 * However, some web forms include more than one coded property and the code in this object
	 * might be for the aggregate entity or the detail entry. e.g. blood pressure resource vs resources for systolic and diastolic properties
	 * There is a ObsVitalsRecord for recording finer details, although the form must make reference to 
	 * a common parent instance (form instance identifier)  and the associated child instances
	 */
	@Indexed
	String code
	String category
	/**
	 * Used in many health objects, can be local message or standard message
	 */
	String status
	//convenience to store a standard status message
	String statusStandard
	/**
	 * status of this resource depends on subtype, for content draft, published,
	 * waiting reply,
	 */
	String resourceStatus
	/* TODO annotations, controlled vocabulary and medical contact integration.
	 * This carries the annotations for properties or other semantic annotations
	 * See also the PhrsConstants and Annotea
	 * for basic semantic annotations on property categories rather than only specific properties
	 * "I have a question about medication dosage(category) for my doctor(actor category)
	 * Dr. Smith (instance in Medical Contact or free text label that should be entered as a Medical contact)"
	 */
	Map<String,String> annotations
	/**
	 * Any means of grouping e.g. if web form writes date  multiple parts
	 * These identifiers are resourceUris in the repository, not identifiers from services  (interop)
	 */
	String groupId
	String parentId
	/**
	 * To indicate that the origin was EHR or another ehealth application. This is copied from the interop record.
	 * Origin should be a profile URI that has a description.
	 */
	String origin
	String originStatus //origin status does not comply always to UI status
	/*
	 * This is the message identifier
	 */
	//String originIdentifier
	/**
	 * Any reference to external identifier, this is not the parentId
	 */

	String externalReference

	@Indexed
	String ownerUri //the healthProfileUri, not the userId
	@Indexed
	String creatorUri //the healthProfileUri, not the userId

	String ownerPortalUserId //additional until not needed

	@Indexed
	Date createDate
	@Indexed
	Date modifyDate
	//createdId = UUID at creation - can be used for hashcode and compare. ResourceURI is set when resource is first stored.
	String createId

	/**
	 * Use as priority or rank
	 */
	Integer priority = -1

	Boolean deleted=Boolean.FALSE

	String label //optional, can be user created or system generated. Issue to use Event's label?

	@Indexed
	String note



	
	//Map cacheMap

	/**
	 * referes to resourceUri of other resources
	 */
	List<String> refersTo = []

	/**
	 * Indicates whether event description is relevant or not, used or not. It might exist, but turned off
	 */

	/* temporal properties, most everything can use it, otherwise de-activated it
	 where relevant. Dates might still be used, but the "Event" view is de-activated */

	Boolean eventActivated

	String eventStatus
	//can be set for public, group events
	String eventPubStatus
	/**
	 * high level them observation, dataentry,etc
	 */
	String eventTheme
	/**
	 * capture status history only as space delimited string list e.g. something planned and executed
	 */
	String eventStatusHistory

	/**
	 * Content and note are the same
	 */
	public String getContent(){
		return note
	}
	public void setContent(String content){
		note = content
	}

	//String eventScheduleCode
	/*
	 * String values which represent valid CSS colors. 
	 * These can be hex or named strings as both are accepted by the CSS specification. 
	 */
	String headerColor
	String contentColor

	Date beginDate
	Date endDate = displayDate
	Boolean allDay

	double scoreRating

	/**
	 * transform status to Interop code
	 * medication, physical activity, problem
	 * @return
	 */
	public String getStatusCorrection(){
		return getStatusCorrection(status)
	}

	public static String getStatusCorrection(String theStatus){
		return InteropTermTransformer.transformStatus(theStatus)

	}

	public String getDateStatusId(){
		getDateStatusId(status)
	}
	public static String getDateStatusId(String theStatus){

		String tempStatus = getStatusCorrection(theStatus)
		if(tempStatus){
			switch(tempStatus){
				case PhrsConstants.STATUS_RUNNING:
					return PhrsConstants.DATE_STATUS_BEGIN //="date.status.begin"
					break
				case PhrsConstants.STATUS_COMPLETE:
					return PhrsConstants.DATE_STATUS_END
					break

				default:
					return null
					break
			}
		}
		return null
	}

	public  Date getDisplayDate(){
		String tempStatus = getStatusCorrection(status)
		if(tempStatus){
			switch(tempStatus){
				case PhrsConstants.STATUS_RUNNING:
					return beginDate
					break
				case PhrsConstants.STATUS_COMPLETE:
					return endDate
					break

				default:
					return beginDate
					break
			}
		}
		return null
	}

	/**
	 * medication, physical activity, problem,smoke risk
	 * @param displayDate
	 */
	public void setDisplayDate(Date displayDate){

		String tempStatus = getStatusCorrection(status)
		if(tempStatus){
			switch(tempStatus){

				case PhrsConstants.STATUS_RUNNING:
					beginDate = displayDate
					break
				case PhrsConstants.STATUS_COMPLETE:
					endDate = displayDate
					break
				//??
				default:
					beginDate = displayDate
					break
			}
		}
		return null
	}


	/**
	 * Fix potential problems later
	 * @param date
	 */
	public void setEndDate(Date date){
		if(endDate && beginDate){
			if(beginDate.getTime() >  endDate.getTime()) {
				endDate= beginDate
			}
		}
	}

	/**
	 * Time is eventually merged with the begin and endDates.
	 * Transient for use by  event forms
	 * CalendarCoreUtils.getTimeIntervalList() provides a list of time strings for the user to choose in 
	 * dropdown list
	 */
	@Transient
	String transientBeginTime
	@Transient
	String transientEndTime

	@Transient
	Long durationTime

	String durationCode
	String frequencyCode

	boolean locked;
	/**
	 * title -  gets label
	 */
	public String getTitle(){
		return label
	}
	/**
	 * sets property label
	 */
	public void setTitle(String title){
		label= title
	}
	/*
	 * Duration can be formated
	 * @return
	 
	TimeDuration getTimeDuration(){
		return CalendarCoreUtils.getTimeDuration(endDate,beginDate)
	}
	String getTimeDurationFormated(){
		return CalendarCoreUtils.getTimeDurationFormated(getTimeDuration(), " ", true)
	}*/


	public String getZclass() {
		return "z-calevent";
	}

	public BasePhrsModel(){
		super()
                //transient property
                newImport =false
                
		createId = createId ? createId : UUID.randomUUID().toString()
		//cacheMap=[:]
		refersTo=[]
		annotations=[:]

		headerColor=  	PhrsConstants.CALENDAR_EVENT_HEADER_COLOR_DEFAULT
		contentColor= 	PhrsConstants.CALENDAR_EVENT_CONTENT_COLOR_DEFAULT
		locked=			false

		//reset in domain object constructors
		eventActivated=	Boolean.FALSE
		eventStatus=	PhrsConstants.EVENT_STATUS_UNKNOWN
		eventTheme = 	PhrsConstants.EVENT_THEME_DATA_ENTRY

		allDay = false
	
		//categories=[]
	}

}
