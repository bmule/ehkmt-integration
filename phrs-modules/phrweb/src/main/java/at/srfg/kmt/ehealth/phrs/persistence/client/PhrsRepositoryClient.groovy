package at.srfg.kmt.ehealth.phrs.persistence.client

import java.io.Serializable
import java.util.Date
import java.util.List
import java.util.Map
import java.util.Set

import org.bson.types.ObjectId
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.HealthProfileOverview
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileMedicalContactInfo
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileUserContactInfo
import at.srfg.kmt.ehealth.phrs.model.basesupport.AuditBase
import at.srfg.kmt.ehealth.phrs.presentation.services.InteropAccessService
import at.srfg.kmt.ehealth.phrs.presentation.services.UserService

import com.google.code.morphia.Datastore
import com.google.code.morphia.query.Query

/**
 * 
 * A highlevel DAO
 * Use getPhrsDatastore to create more specific queries and more domain DAOs
 *
 */
public class PhrsRepositoryClient implements Serializable{
    private final static Logger LOGGER = LoggerFactory.getLogger(PhrsRepositoryClient.class);

    PhrsStoreClient phrsStoreClient
    CommonDao commonDao
    UserService userService

    public PhrsRepositoryClient(){

        phrsStoreClient= PhrsStoreClient.getInstance()
        userService = new UserService(this)
        commonDao= (CommonDao)userService
    }
	
    public PhrsRepositoryClient(PhrsStoreClient storeClient ){
		
        phrsStoreClient = storeClient
        userService = new UserService(this)
        commonDao= (CommonDao)userService
		
    }

    /**
     * User this to make more specific queries
     * @return
     */
    public Datastore getPhrsDatastore(){

        return phrsStoreClient.getPhrsDatastore();
    }
    public Datastore getPhrsVersioningDatastore(){

        return phrsStoreClient.getPhrsVersioningDatastore();
    }
    public Datastore getPhrsAuditDatastore(){

        return phrsStoreClient.getPhrsAuditDatastore();
    }

    public InteropAccessService getInteropService(){

        return phrsStoreClient.getInteropService();
    }
    /**
     * 
     * @param theObject
     * @return
     */
    def crudReadResource(def theObject){
        def result
        //&& getPhrsDatastore().exists(theObject)
        if(theObject ){
            result= getPhrsDatastore().get(theObject)
            //writeAuditData(theObject,theObject,PhrsConstants.PUBSUB_ACTION_CRUD_READ,null)
        }
        return result
    }
    public InteropClients getInteropClients(){
        return  phrsStoreClient.getInteropClients()
    }
    /**
     * sorted by create date
     * @param clazz
     * @return
     */
    public List crudReadResources(def clazz){
        //
        List list

        // && healthProfileId.length() >2

        Datastore store = getPhrsDatastore()
        list= store.find(clazz).order('-createDate').asList()


        if(!list) list=[]
        return list
    }
    /**
     * sorted by modify date
     * @param healthProfileId
     * @param clazz
     * @return
     */
    public List crudReadVersionedResources(String healthProfileId, def clazz){
        //
        List list

        // && healthProfileId.length() >2
        if(healthProfileId ){
            //println('crudReadResources 2')
            //			list= getPhrsDatastore().find(clazz,PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER,healthProfileId).asList()
            Datastore store = getPhrsVersioningDatastore()
            list= store.find(clazz,PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER,healthProfileId).order('-modifyDate').asList()


            //writeAuditdata(clazz,'LIST',[(PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER):healthProfileId,'java.util.Class':clazz])
            //writeAuditAction(String creatorOfAction,Set ownerOfResource,
            //Set targetResourceType, String action, Map<String,String> params)

        }

        if(!list) list=[]
        return list
    }

    public def crudReadResourceSingle(String healthProfileId, def clazz){
        Datastore store = getPhrsDatastore()
        return store.find(clazz,PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER,healthProfileId).get()

    }
    /**
     * 
     * @param healthProfileId
     * @param clazz
     * @param map
     * @return
     * Will ignore map keys for 'clazz' and PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER
     */
    public Query buildMorphiaQuery(String healthProfileId, def clazz, Map map){
        Datastore store = getPhrsDatastore()
        Query query
        try {
            query = store.createQuery(clazz)
            if(healthProfileId){
                query.field(PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER).equals(healthProfileId)
            }

            if(map){clazz
                map.keySet().each() { key ->
                    if(key != PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER
                        && key != 'clazz'
                        && map.get(key)){
                        query.field(key).equal(map.get(key))

                    }
                }
            }
        } catch(Exception e){
            println(' '+e)
        }

        return query
    }

    public List crudReadResourceByExample(String healthProfileId, Class clazz, Map map){
        Datastore store = getPhrsDatastore()

        Query query = buildMorphiaQuery(healthProfileId,clazz,map)
        if(query) return query.asList()
        else return new ArrayList()

        //return store.find(clazz,PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER,healthProfileId).get()
    }
	
    public def crudReadResourceByExampleToSingle(String healthProfileId, Class clazz, Map map){
        Datastore store = getPhrsDatastore()

        Query query = buildMorphiaQuery(healthProfileId,clazz,map)
		
        if(query) return query.get()
        else return null

        //return store.find(clazz,PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER,healthProfileId).get()
    }

    public List crudReadResources(String healthProfileId, def clazz){
        //
        List list

        // && healthProfileId.length() >2
        if(healthProfileId ){
            //println('crudReadResources 2')
            //			list= getPhrsDatastore().find(clazz,PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER,healthProfileId).asList()
            Datastore store = getPhrsDatastore()
            list= store.find(clazz,PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER,healthProfileId).order('-createDate').asList()


            //writeAuditdata(clazz,'LIST',[(PhrsConstants.PROPERTY_HEALTH_PROFILE_IDENTIFIER):healthProfileId,'java.util.Class':clazz])
            //writeAuditAction(String creatorOfAction,Set ownerOfResource,
            //Set targetResourceType, String action, Map<String,String> params)

        }

        if(!list) list=[]
        return list
    }

    def crudSaveOrUpdateResource(def theObject,String action){

        crudSaveResource(theObject,action)

    }
    String makeNewResourceUri(){
        return UUID.randomUUID().toString()
    }
    /**
     * Preferred usage for creating new resources. The resourceUri, ownerUri and creatorUri
     * 
     * @param theObject
     * @param action
     * @param resourceUri
     * @param ownerUri
     * @param creatorUri
     * @return
     */
    def crudSaveResource(def theObject,String action,String resourceUri, String ownerUri, String creatorUri){
        theObject.resourceUri = resourceUri
        theObject.ownerUri=ownerUri
        theObject.creatorUri = creatorUri

        return crudSaveResource( theObject, action)
        
        
    }

    public Map writeInteropMessages(def theObject){
        Map map
        try{
            //def x=getInteropService()
            map= getInteropService().sendMessages(theObject);
        } catch(Exception e){
            e.printStackTrace()
            LOGGER.error('writeInteropMessages ', e)
        }
        return map
    }
    /**
     * 
     * @param theObject
     * @param action
     * @return
     */
    def crudSaveResource(def theObject,String action){
        try{
            if(theObject){
                    
                if(theObject.resourceUri){
				
                    return this.crudUpdateResource(theObject)
                } else {
                    //set this but undo if there is a failure
                    theObject.setResourceUri(makeNewResourceUri())
                    theObject.setCreateDate(new Date())
                    theObject.setModifyDate(theObject.getCreateDate())
                    def keyset = getPhrsDatastore().save(theObject)
                    //Do not send messages after a fresh import from the interop service
                    if( ! theObject.getNewImport()) {
                        writeInteropMessages(theObject)
                    }else {
                        //reset, after persisted
                        theObject.setNewImport(false)
                    }
                    //TODO if there is an error, make the resource uri null
                    //writeAuditData(theObject,action,null)
                }
            }else {
                if(theObject && !theObject.resourceUri)
                LOGGER.error(' - missing object ID, action and object type: '+PhrsConstants.PUBSUB_ACTION_CRUD_CREATE+' '+theObject.class)
                else
                LOGGER.error(' - NULL object action='+PhrsConstants.PUBSUB_ACTION_CRUD_CREATE)
            }
        } catch(Exception e){
            LOGGER.error('crudSaveResource ', e)
        }
        return theObject
    }
        

    /**
     * 
     * @param theObject
     * @return
     */
    def crudSaveResource(def theObject){
        
        if(theObject) {

            if(theObject.resourceUri) return crudSaveResource(theObject,PhrsConstants.PUBSUB_ACTION_CRUD_CREATE)
            else return crudSaveResource(theObject,PhrsConstants.PUBSUB_ACTION_CRUD_UPDATE)

        }else {
            LOGGER.error(' NULL object action '+ PhrsConstants.PUBSUB_ACTION_CRUD_CREATE)
        }
        return theObject
    }
    /**
     * 
     * @param theObject
     * @return
     */
	
    def crudUpdateResource(def theObject){
        //def selected=theObject
        String tempId
        try{
            //DB key is null, but there is a stored temp key from the form. Restore it
            tempId = theObject.getTempId()
            if( !theObject.getId() && tempId){
                //form issue, cannot pass the id object from form
                //try the string or must search for resourceUri and get  ObjectId
				
                if(ObjectId.isValid(tempId)){
                    ObjectId replaceId = ObjectId.massageToObjectId(tempId);
                    if(replaceId) theObject.id = replaceId
                }
				
            }
        } catch (Exception e){
            LOGGER.error(' crudUpdateResource fail '+tempId, e)
        }
	try {
            if(theObject && theObject.resourceUri){

                //def keyset = getPhrsDatastore().update(arg0, arg1)
                theObject.setModifyDate(new Date())
                //create date needs to be converted...
                if( ! theObject.createDate) {
                    String dateStr=theObject.getTempCreateDate()
                    Date date
                    if(dateStr){
                        //println("getTempCreateDate="+dateStr)
                        date = InteropAccessService.transformDateFromMessage(dateStr, new Date())				
                    }
                    if(!date) date = new Date()
                    theObject.setCreateDate(date)
				
                }
                def keyset = getPhrsDatastore().save(theObject)
                        
                //writeAuditData(theObject,PhrsConstants.PUBSUB_ACTION_CRUD_UPDATE,null)
            } else if(theObject){
                LOGGER.error(' - no object ID, pass to SAVE NEW ACTION object action, object '+PhrsConstants.PUBSUB_ACTION_CRUD_UPDATE+' '+theObject.class)
                return crudSaveResource(theObject)

            } else{
                LOGGER.error(' - NULL object '+PhrsConstants.PUBSUB_ACTION_CRUD_UPDATE)
            }
        } catch(Exception e){
            LOGGER.error('crudUpdateResource ', e)
        }
        return theObject
    }
    /**
     * Prevent certain objects from being deleted e.g. single instances record user information
     * contact info
     * @param theObject
     * @return
     */
    public boolean allowDelete(def theObject){
        boolean flag = true

        if( theObject) {
            flag = (theObject instanceof HealthProfileOverview
                || theObject instanceof ProfileUserContactInfo
                || theObject instanceof ProfileMedicalContactInfo) ? false : true
        }
        return flag
    }
    /**
     * 
     * @param theObject
     * @return
     */
    def crudDeleteResource(def theObject){//, def selected){
        if(allowDelete(theObject)){

            def keyset
            if(theObject && theObject.resourceUri){
                keyset = getPhrsDatastore().delete(theObject)
                //writeAuditData(theObject,PhrsConstants.PUBSUB_ACTION_CRUD_DELETE,null)
            }else {
                if(theObject)
                LOGGER.error(' - missing object ID, object type: '+PhrsConstants.PUBSUB_ACTION_CRUD_DELETE+' '+ theObject.class)
                else
                LOGGER.error(' - NULL object id '+PhrsConstants.PUBSUB_ACTION_CRUD_DELETE)
            }
            return keyset
        }
        return null
    }
    //BasePhrsModel or BasePhrsMetadata resourceObj
    /**
     * 
     * @param resourceObj
     * @param action
     * @param params
     */
    void writeHistory(def resourceObj, def action, Map<String,String> params){
        if(resourceObj) {

            //clone
            try {
                //def theClone = resourceObj.clone
                //Cloneable
                //preserve the id, null it on object and save it,
                def temp= resourceObj.id
                //allow db to set new id for storing, other it updates existing
                resourceObj.id=null
                //or store in a properties map
                resourceObj.eventPubStatus=action

                this.getPhrsVersioningDatastore().save(resourceObj)
                //reset the id because this object is referenced
                resourceObj.id=temp

            } catch (Exception e){
                LOGGER.error('Exception '+e)
            }


        } else {
            LOGGER.error('writeHistory - NULL object ')
        }
    }
    // rewrite this to make one summary and one update and use getPhrsAuditDatastore

    /**
     * 
     * @param resourceObj
     * @param action
     * @param params
     * @return
     */
    def writeAuditData(def resourceObj, String action, Map<String,String> params){ 
        
//        if(resourceObj) {
//            //create and writes (true)
//            AuditBase audit=new AuditBase(resourceObj,action)
//            try {
//                getPhrsAuditDatastore().save(audit)
//            } catch (Exception e){
//                println('Exception '+e)
//                LOGGER.error('writeAuditData - NULL object '+e)
//                
//                writeHistory(resourceObj,action,params)
//            }
//
//            return audit
//        } else {
//            LOGGER.error('{} - NULL object',PhrsConstants.PUBSUB_ACTION_CRUD_WRITE_AUDIT_DATA)
//        }
        return null

    }
    /**
     * 
     * @param resourceObj
     * @param action
     * @param params
     * @param creatorOfAction
     * @return
     */
    def writeAuditData(def resourceObj, String action, Map<String,String> params, String creatorOfAction){
       
        if(resourceObj) {
            //create and writes (true)
            AuditBase audit=new AuditBase(resourceObj,action)
            try {
                getPhrsAuditDatastore().save(audit)
                writeHistory(resourceObj,action,params)
            } catch (Exception e){
                println('Exception '+e)
                LOGGER.error('writeAuditData  '+e)
            }
            return audit
        } else {
            logger.error(' - NULL object'+PhrsConstants.PUBSUB_ACTION_CRUD_WRITE_AUDIT_DATA)
        }
        return null

    }
    /**
     * 
     * @param creatorOfAction
     * @param ownerOfResource
     * @param targetResourceType
     * @param action
     * @param params
     * @return
     */
    def writeAuditAction(String creatorOfAction,Set ownerOfResource, Set targetResourceType, String action, Map<String,String> params){

        if(creatorOfAction) {
            //create and writes (true)
            //AuditBase audit=new AuditBase()
            //return audit
        } else {
            LOGGER.error(' - NULL object'+PhrsConstants.PUBSUB_ACTION_CRUD_WRITE_AUDIT_ACTION)
        }
        return null

    }
    //TODO add writeAuditSummary
    public void writeAuditSummary(String creatorOfAction,Set ownerOfResource, Set targetResourceType, String action, Map<String,String> params){

    }
    
}
