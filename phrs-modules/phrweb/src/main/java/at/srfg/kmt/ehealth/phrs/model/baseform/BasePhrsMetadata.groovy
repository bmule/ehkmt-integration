package at.srfg.kmt.ehealth.phrs.model.baseform

import java.io.Serializable
import java.util.Date

import org.bson.types.ObjectId

import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id
import com.google.code.morphia.annotations.Indexed
import com.google.code.morphia.annotations.Transient
/**
 * 
 * Base properties with extensions such as temporal properties
 * These properties are needed for CRUD operations
 *  and but NOT for sending messages to the interop services. 
 *  
 * 
 */
@groovy.transform.EqualsAndHashCode
@Entity
class BasePhrsMetadata implements  Serializable{
	//private final static Logger logger = LoggerFactory.getLogger(BasePhrsMetadata.class);

	@Transient
	String strCreateDate
	@Transient
	String strId
    @Transient
    boolean newImport=false
	//temp fields for UI forms needed, these cause trouble as hidden fields
	/**
	 * UI fix
	 * @return
	 */
	public String getTempCreateDate(){
		//use existing date to create string
		String ds= createDate  ?  HealthyUtils.formatDate( createDate, (String)null, InteropAccessService.DATE_PATTERN_INTEROP_DATE_TIME) : null
		//if null, check if the string temp version was saved for the UI
		ds = ds ? ds : strCreateDate
		return ds
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
	@Id
	ObjectId id

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
	 * Status is used in many health objects and important for interoperability messages
	 */
	String status
	/**
	 * Any means of grouping e.g. if web form writes date  multiple parts
	 * These identifiers are resourceUris in the repository, not identifiers from services  (interop)
	 *
	 */
	String groupId
	String parentId
	/**
	 * To indicate that the origin was EHR or another ehealth application. This is copied from the interop record.
	 * Origin should be a profile URI that has a description.
	 */
	String origin
	String originStatus //origin status does not comply always to UI status
	/**
	 * Any reference to external identifier, this is not the parentId
	 */
	String externalReference
	@Indexed
	String ownerUri //the healthProfileUri, not the userId
	@Indexed
	String creatorUri //the healthProfileUri, not the userId


	@Indexed
	Date createDate
	@Indexed
	Date modifyDate

	String createId
	
	//Used for history to record the action
	String eventPubStatus
	String note
	Boolean deleted=Boolean.FALSE
	
	String label
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
	 * These are not needed by some models, can be used by interop services
	 */
	//Date beginDate
	//Date endDate

	public BasePhrsMetadata(){
		createId = createId ? createId : UUID.randomUUID().toString()
		

	}

}
