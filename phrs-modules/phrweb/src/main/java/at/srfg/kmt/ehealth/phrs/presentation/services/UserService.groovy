package at.srfg.kmt.ehealth.phrs.presentation.services

import java.io.Serializable
import java.util.List

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsMetadata
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsModel
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsRepositoryClient
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient
import at.srfg.kmt.ehealth.phrs.security.services.AuthorizationService
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo


public class UserService  extends CommonDao implements Serializable {
	private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	//need constructor for serialization JSF
	public UserService(){
		super(PhrsStoreClient.getInstance().getPhrsRepositoryClient())
	}
	public UserService(PhrsRepositoryClient phrsRepositoryClient){
		super(phrsRepositoryClient)
	}

	/**
	 * This is the common way to get the User PhrId. 
	 * Same as getUserPhrId(), formally called the healthProfileId
	 * @return
	 */
	public String getOwnerUri(){
		return getUserPhrId()
	}

	/**
	 * Could be useful within a portal server 
	 * @return
	 */
	public String getRemoteUser(){
		return UserSessionService.getRemoteUser()
	}
    public ProfileContactInfo getProfileContactInfo(){

        return getProfileContactInfo(getUserPhrId())
    }
	/**
	 * Critical method to determine the User PhrId of the authenticated user. This user has their phrId stored in the session
	 * 
	 * Used as ownerUri that is assigned to each resource.
	 * This is also known formally as healthProfileId 
	 * 
	 * Possible there is a filter user phrId in the session as well, that is for viewing by the authenticated user
	 * 
	 * When the application mode is in test mode, the phrId might be null in the session, therefore only in test mode
	 * the PhrsConstants.USER_TEST_HEALTH_PROFILE_ID is assigned
	 * Otherwise, if null, then this is a serious error. The login filter might be bypassed ...
	 * 
	 * @return
	 */
	public  String getUserPhrId(){
		String phrId = null
		phrId = UserSessionService.getSessionAttributePhrId()
		//println('userService ownerUri phrId='+phrId)
		if(!phrId && ConfigurationService.isAppModeSingleUserTest()){
			phrId = PhrsConstants.USER_TEST_HEALTH_PROFILE_ID
			//println('userService ownerUri null, appModeTest, set ownerUri phrId='+phrId)
		} 
		return phrId
	}


	public String getHealthProfileFilterUri(){
		return getUserPhrId()
	}
	/**
	 * Authorization Access Control
	 * @return
	 */
	public AuthorizationService getAuthorizationService(){
		println('AuthorizationService getAuthorizationService'+this.getOwnerUri())
		return AuthorizationService.getDefaultByOwnerUri(this.getOwnerUri())
	}
	/**
	 * If UserService = null, then a default restricted Authorization setup 
	 * @return
	 */
	public static AuthorizationService getAuthorizationServiceDefault(){
		return AuthorizationService.getDefault(0)
	}

	/**
	 * Get all resources for this entity class.
	 * By default, only the for a user with the known phrId (ownerUri). Not the filter id
	 * @param entityClazz
	 * @return
	 */
	public List getResources( Class entityClazz){
		return getResources(getUserPhrId(),entityClazz)
	}

	public List getResources(String theOwnerUri, Class entityClazz){
		//load the primary model from storage

		List temp= phrsRepositoryClient.crudReadResources( theOwnerUri,  entityClazz)
		if( !temp) temp = []//new BindingListModelList([],true)
		return temp
		//to be clear, not needed by groovy
	}
	/**
	 * Uses the session phrId as ownerUri
	 * @param entityClazz
	 * @param create
	 * @return
	 */
	public def getResourceSingle(Class entityClazz, boolean create){

		def single  =null

		try {
			single = phrsRepositoryClient.crudReadResourceSingle( getUserPhrId(),  entityClazz)

			if( !single && create) {
				single = entityClazz.newInstance()
				single.ownerUri = getUserPhrId()
				//don't set resourceUri or dates yet
				single.creatorUri = single.ownerUri
			}
		} catch (Exception e){
			println("getResourceSingle "+e)
		}
		return single
	}

	/**
	 * 
	 * @param entityClazz
	 * @param map
	 * @param create
	 * @return
	 */
	public def getResourceByExampleToSingle(Class entityClazz,Map map, boolean create){
		def single =null

		try {
			single = phrsRepositoryClient.crudReadResourceByExampleToSingle( getUserPhrId(),  entityClazz, map)

			if( !single && create) {
				single = entityClazz.newInstance()
				single.ownerUri = getUserPhrId()
				//don't set resourceUri or dates yet
				single.creatorUri = single.ownerUri
			}
		} catch (Exception e){
			println("getResourceByExample "+e)
		}

		return single
	}


	/**
	 * retrieve list
	 * @param entityClazz
	 * @param map
	 * @param create - if no results, create one new resource
	 * @return
	 */
	public List getResourceByExampleToList(Class entityClazz,Map map){
		def list  =null

		try {
			list = phrsRepositoryClient.crudReadResourceByExample( getUserPhrId(),  entityClazz, map)

			if( !list) {
				def single = entityClazz.newInstance()
				single.ownerUri = getUserPhrId()
				//don't set resourceUri or dates yet
				single.creatorUri = single.ownerUri
				list=[single]
			}
		} catch (Exception e){
			println("getResourceByExampleToList "+e)
		}

		return list
	}

	public List getVersionedResources(String healthProfileId, Class entityClazz){
		//load the primary model from storage

		def temp= phrsRepositoryClient.crudReadResources( healthProfileId,  entityClazz)
		if( !temp) temp = []//new BindingListModelList([],true)
		return temp
		//to be clear, not needed by groovy
	}

	public List getVersionedResources( Class entityClazz){
		return getVersionedResources(getUserPhrId(),entityClazz)
	}

	public List getHistoricalResources(String healthProfileId, Class entityClazz){
		//load the primary model from storage

		def temp= phrsRepositoryClient.crudReadResources( healthProfileId,  entityClazz)
		if( !temp) temp = []//new BindingListModelList([],true)
		return temp
		//to be clear, not needed by groovy
	}

	public List getHistoricalResources( Class entityClazz){
		return getHistoricalResources(getUserPhrId(),entityClazz)
	}

	public void crudSaveResource(def theObject){
		
		loadCommonPropertiesForCreate(theObject)
		phrsRepositoryClient.crudSaveResource(theObject)
	}


	public void crudDeleteResource(def theObject){
		phrsRepositoryClient.crudDeleteResource(theObject)
	}

	public void loadCommonPropertiesForCreate(BasePhrsModel obj){
		//new when resourceUri is null
		if(! obj.resourceUri){
			obj.setOwnerUri(getOwnerUri())
			obj.setCreatorUri(getUserPhrId())
			obj.type = obj.getClass().toString()

			//resourceUri is set before actually saving the the object
		}
		//double check. With JSF RequestScoped these must be checked
		if( ! obj.getOwnerUri()) obj.setOwnerUri(getOwnerUri())
		if( ! obj.getCreatorUri()) obj.setCreatorUri(getUserPhrId())
		if( ! obj.getType()) obj.setType(obj.getClass().toString())

	}
	/**
	 * 
	 * 
	 * @param status
	 * @param include - filter  by status: true accept all with this status, or false - exclude all with this status
	 * @param results
	 * @return
	 */
	public static List filterResultsByStatus(String status, boolean include, List results){
		def temp = []
		if(status && results){
			try{
				results.each {item ->
					if(item instanceof BasePhrsMetadata || item instanceof BasePhrsModel){
						if(include && item.status == status){
							temp.add(item)
						} else if( ! include &&  item.status != status){
							temp.add(item)
						}
					}
				}
			} catch (Exception e){
				println("exception "+e)
			}
		}
		return temp

	}

	public List crudReadHistory( Class entityClazz){
		return crudReadHistory(getHealthProfileFilterUri(),entityClazz);
	}
	public  List crudReadHistory(String healthProfileId, Class entityClazz){
		def temp = phrsRepositoryClient.crudReadVersionedResources( healthProfileId,  entityClazz)
		if( !temp) temp = []
		return temp
	}


}