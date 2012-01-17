package at.srfg.kmt.ehealth.phrs.model.basesupport

import java.util.Date
import java.util.Map
import java.util.Set

import org.apache.commons.beanutils.BeanMap
import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient

import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Id

//('audit_base')
@Entity
public class AuditBaseAction {
//	def writeAuditAction(String creatorOfAction,Set ownerOfResource, Set targetResourceType, String action, Map<String,String> params){

	private final static Logger LOGGER = LoggerFactory.getLogger(AuditBaseAction.class);
	
	@Id
	ObjectId id	
	
	String label
	Date auditDate = new Date()
	Date resourceCreateDate
	//Date resourceModifyDate
	//ObjectId resourceObjectId
	//String resourceUri
	String resourceOwnerUri
	String resourceCreatorUri
	String resourceOrigin
	
	/**
	 *  Action taken
	 */
	String action
	//String clazz
	//boolean deleted=false
	
	//Map<String,Object> auditMap= new HashMap<String,Object>()
	Map auditMap= []
	
	String creatorOfAction
	Set ownerOfResource
	Set targetResourceType
	
	public AuditBaseAction(){
		
	}
	/**
	 * Any object that is derived from CommonModelProps only
	 * @param object
	 * @param auditMap
	 */
	public AuditBaseAction(
		String creatorOfAction,
		Set ownerOfResource, 
		Set targetResourceType, 
		String action, 
		String origin,
		Map<String,String> params){

		try{
			if(creatorOfAction){
				
				this.ownerOfResource        = ownerOfResource
				this.targetResourceType     = targetResourceType
				this.action					= action		
				this.resourceCreateDate		= new Date()
				this.resourceOwnerUri		= ownerOfResource
				this.resourceCreatorUri		= creatorOfAction
				this.resourceOrigin	 		= origin
								
				//convert pojo to map and then to json string?
				if(params) auditMap = new BeanMap(params)
				else auditMap= []
				
				//auditMap = PhrsStoreClient.getInstance.getJsonMapper().readValue(map, Map.class);
								
				//if(persistAuditData){		
					PhrsStoreClient.getInstance().getPhrsDatastore().save(this)
				//}
			}
		} catch (Exception e){
			println('Exception '+e)
		}
	}
/*
	public AuditBaseAction(def object,String action, boolean persistAuditData ){
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
				
				//convert pojo to map and then to json string?
				auditMap = new BeanMap(this);
				
				//auditMap = PhrsStoreClient.getInstance.getJsonMapper().readValue(map, Map.class);
								
				if(persistAuditData){		
					PhrsStoreClient.getInstance().getPhrsDatastore().save(this)
				}
			}
		} catch (Exception e){
			println('Exception '+e)
		}
	}*/
	

}
