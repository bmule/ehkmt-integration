package at.srfg.kmt.ehealth.phrs.model.basesupport


import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id


@Entity
public class AuditSummary {

	private final static Logger LOGGER = LoggerFactory.getLogger(AuditSummary.class);
	
	@Id
	ObjectId id	
	
	String resourceModifierUri
	
	String label
	Date auditDate 
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
	Boolean listAccess=false
	Boolean deleted=false
	//Map<String,Object> auditMap= new HashMap<String,Object>()
	//Map auditMap
	String jsonString
	
	
	public AuditSummary(){
		
	}
	public AuditSummary(String action, String resourceModifierUri, ObjectId id, String clazz, Boolean listingAccess, String resourceUri, 
		String ownerUri, String creatorUri, Date createDate,Date modifyDate, Boolean deleted, String label,String origin){
		
		this.action					= action
		this.resourceObjectId       = id
		this.clazz 					= clazz
		this.listAccess = listingAccess
		this.resourceUri 			= resourceUri
		this.resourceCreateDate		= createDate
		this.resourceModifyDate     = modifyDate
		this.resourceOwnerUri		= ownerUri
		this.resourceCreatorUri		= creatorUri
		this.deleted				= deleted
		
		this.label 					= label
		
		this.resourceOrigin	 		= origin
		
		this.resourceModifierUri=resourceModifierUri
		auditDate = new Date()
	}
	/**
	 * Any object that is derived from CommonModelProps only
	 * @param object
	 * @param auditMap
	 */
	public AuditSummary(def object,String action, String resourceModifierUri){
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
				
				this.label 					= object.label
				
				this.resourceOrigin	 		= object.origin
				
				this.resourceModifierUri=resourceModifierUri
				auditDate = new Date()

			}
		} catch (Exception e){
			println('Exception '+e)
			//TODO FIX  auditMap = new BeanMap(object);
			//logger.error("{} exeception: {}",PhrsConstants.PUBSUB_ACTION_CRUD_WRITE_AUDIT_DATA,e)
		}
	}
	

}
