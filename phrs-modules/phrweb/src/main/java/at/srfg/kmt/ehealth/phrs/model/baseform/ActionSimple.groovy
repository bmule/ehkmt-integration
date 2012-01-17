package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date

import at.srfg.kmt.ehealth.phrs.PhrsConstants;

import com.google.code.morphia.annotations.Entity

@Entity
public class ActionSimple extends BasePhrsModel{
	/**
	 * BasePhrsModel :id, resourceUri, healthProfileId, label, priority, date, status
	 * parent, etc
	 */
	
	public ActionSimple(){

		}

	/**
	 * 
	 * @param label
	 * @param priority
	 * @param date
	 */
	public ActionSimple(String label,Integer priority,Date date){
		super()
		this.label = label;
		this.priority = priority;
		this.setBeginDate(date)
	}
	/**
	*
	* @param resourceUri
	* @param label
	* @param priority
	* @param date
	*/
   public ActionSimple(String resourceUri,String label,Integer priority,Date date){
	   this.resourceUri = resourceUri;
	   this.label = label;
	   this.priority = priority;
	   this.setBeginDate(date)
	   eventActivated=	Boolean.TRUE
	   eventStatus=		PhrsConstants.EVENT_STATUS_ACTION_PLANNED_NO_TIMEPLAN
	   eventTheme = 	PhrsConstants.EVENT_THEME_EVENT
   }
}
