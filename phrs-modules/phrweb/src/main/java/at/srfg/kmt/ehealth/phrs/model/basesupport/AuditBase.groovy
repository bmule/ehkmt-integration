package at.srfg.kmt.ehealth.phrs.model.basesupport


import org.bson.types.ObjectId

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id

//('audit_base')
/**
 * 
 * @deprecated
 *
 */
@Entity
public class AuditBase {

	//private final static Logger logger = LoggerFactory.getLogger(AuditBase.class);
	
	@Id
	ObjectId id	
	
	String label
	Date auditDate = new Date()
	Date resourceCreateDate
	Date resourceModifyDate
	ObjectId resourceObjectId
	String resourceUri
	String resourceOwnerUri
	String resourceCreatorUri
	String resourceOrigin

	/**
	 *  Action taken
	 */
	String action
	String clazz
	boolean deleted=false
	
	//Map<String,Object> auditMap= new HashMap<String,Object>()
	//Map auditMap
	String jsonString
	
	
	public AuditBase(){
		
	}
	/**
	 * Any object that is derived from CommonModelProps only
	 * @param object
	 * @param auditMap
	 */
	public AuditBase(def object,String action){
		try{
			if(object){
				
				this.action					= action
				this.resourceObjectId       = object.id
				this.clazz 					= object.class.toString()
				this.resourceUri 			= object.resourceUri
				this.resourceCreateDate		= object.createDate
				this.resourceModifyDate     = object.modifyDate
				this.resourceOwnerUri		= object.ownerUri
				this.resourceCreatorUri		= object.creatorUri
				this.deleted				= object.deleted
				this.resourceOrigin	 		= object.origin
				
				//convert pojo object to bean map 
				//auditMap = new BeanMap(object);
				 /*dump = object.dump()
				 println('dump ='+dump)
				 println('object.toString '+object.toString())
				 */
				 //TODO FIXME ObjectMapper mapper = new ObjectMapper();		 
				 //mapper.writeValue(jsonString, object);
				// println('jsonString='+jsonString)

			}
		} catch (Exception e){
			println('Exception '+e)
			//TODO FIX  auditMap = new BeanMap(object);
			//logger.error('{} exeception: {}',PhrsConstants.PUBSUB_ACTION_CRUD_WRITE_AUDIT_DATA,e)
		}
	}
	

}
