package at.srfg.kmt.ehealth.phrs.persistence.client

import java.util.HashMap
import java.util.Map

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ActorClient
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrOpenId
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrMessageLog
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileUserIdentifiers
import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService

import com.dyuproject.openid.OpenIdUser


/**
 * 
 * Common methods that including those that require the ownerUri (phrId) in the method 
 * The UserService class uses the ownerUri from authenticated user in session
 *
 */
public class CommonDao{

	private final static Logger LOGGER = LoggerFactory.getLogger(CommonDao.class);

	PhrsRepositoryClient phrsRepositoryClient
	PhrsStoreClient phrsStoreClient

	/**
	 * 
	 */
	public CommonDao(PhrsRepositoryClient repoClient){
		//TODO refactoring to eliminate one
		phrsRepositoryClient=repoClient
		phrsStoreClient = phrsRepositoryClient.getPhrsStoreClient()
		
	}


	/**
	 * Should use the default method, however, to search for a User  object, there might not be a phrId stored in the session yet
	 * @param entityClazz
	 * @param create
	 * @param theOwnerUri
	 * @return
	 */
	public def getResourceSingle(Class entityClazz, boolean create,String theOwnerUri){

		def single

		try {
			single = phrsRepositoryClient.crudReadResourceSingle( theOwnerUri,  entityClazz)

			if( !single && create) {
				single = entityClazz.newInstance()
				single.ownerUri =theOwnerUri //getUserPhrId()

				//don't set resourceUri or dates yet
				single.creatorUri = theOwnerUri
			}
		} catch (Exception e){
			println("getResourceSingle "+e)
			LOGGER.error('getResourceSingle', e)
		}
		return single
	}
	/**
	 *
	 * @param theOwnerUri
	 * @param create
	 * @return
	 */
	public PhrFederatedUser getPhrUser(String theOwnerUri,boolean create){

		PhrFederatedUser user=null;
		if(theOwnerUri){

			user = getResourceSingle(PhrFederatedUser.class, create, theOwnerUri)
			//already done
			if(user) user.setOwnerUri(theOwnerUri)

		}
		return user;
	}


	/**
	 * 
	 * @param ownerUri
	 * @return
	 */
	public   boolean existsUserByOwnerUri(String ownerUri){
		def result = getPhrUser(ownerUri,false)

		return result ? true : false
	}
	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public   boolean existsUserByOpenId(String identifier){
		def result = getUserByOpenId(identifier,null,false)

		return result ? true : false
	}

	/**
	 * 
	 * @param identifier
	 * @return
	 * @throws Exception
	 */
	public  PhrFederatedUser getUserByOpenId(String identifier,Map attrs,boolean create) throws Exception{
		//if exists ...get
		//replace - update or create if new
		PhrFederatedUser user=null;
		if(identifier){
			Map query =  ['identifier':identifier]
			user = this.getResourceByExampleToSingle(PhrFederatedUser.class, query, false, null)// theOwnerUri)

			if(create && ! user){
				user = crudCreatePhrUserByOpenId(identifier,attrs)
			}
		}
		return user;
	}
	/**
	 * Local user - restricted to phruserXXXX and admin for testing in Application pilot.
	 * @param userId
	 * @param attrs
	 * @param create
	 * @return
	 * @throws Exception
	 */
	public  PhrFederatedUser getPhrUserByLocalUserId(String userId,Map attrs,boolean create) throws Exception{
		//if exists ...get
		//replace - update or create if new
		PhrFederatedUser user=null;
		if(userId){
			Map query =  ['userId':userId] //do not search on identifier, that is for sso or openIDs
			user = this.getResourceByExampleToSingle(PhrFederatedUser.class, query, false, null)// theOwnerUri)

			if(create && ! user){
				//user = crudCreatePhrUserByOpenId(userId,attrs)
		
				user= new PhrFederatedUser(userId,null);

				user.setUserId(userId);
				user.setCanLocalLogin(true)
				user.setIdentifier(userId);//init to local identifier, but could later assign to an OpenId.
				user.setRole(PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER_LOCAL_LOGIN);
//    public static final String ICARDEA_DOMAIN_PIX_OID = "1.2.826.0.1.3680043.2.44.248240.1";
//    public static final String OWNER_URI_CORE_PORTAL_TEST_USER = "phr/test/testuser";
//    public static final String PROTOCOL_ID_UNIT_TEST = "14920263490";
//    public static final String PROTOCOL_ID_PIX_TEST_PATIENT = "191";
//    public static final String OWNER_URI_PIX_TEST_PATIENT = "phr/test/testuser2";		
				
				if(userId.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST) 
					|| userId.startsWith(PhrsConstants.AUTHORIZE_USER_ADMIN) 
					|| userId.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_AUTO_USER)
                                        || userId.startsWith(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)) {
                                        
					//for phrtest or phrtest1, give it one known ownuri so that we have one test user with a known ownerUri
					//these refer to test data created in the database that might be extracted later for sample data
					
					if(userId.equals(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST_1)){
						
						user.setOwnerUri(PhrsConstants.USER_TEST_HEALTH_PROFILE_ID_1);
						user.setRole(PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DOCTOR);
						
					} else if(userId.equals(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST)){
                                                //phrtest user
						user.setOwnerUri(PhrsConstants.USER_TEST_HEALTH_PROFILE_ID);
						user.setRole(PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE);
                                                //updateProtocolId(ownerUri,Constants.PROTOCOL_ID_TEST_PHRUSER);
                                                
  					} else if(userId.equals(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)){
					
						user.setOwnerUri(userId);
						user.setRole(PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE);                                              
						
					} else {
						user.setOwnerUri(userId);
					}
				
				} 
				
				if(attrs){
					 user.getAttributes().putAll(attrs)
				} 
				this.crudSaveResource(user, user.getOwnerUri(), user.getOwnerUri());

			}
		}
		return user;
	}

	/**
	 * 
	 * @param identifier
	 * @param attrs
	 * @param createPhrUser
	 * @param update
	 * @return
	 */
	public PhrFederatedUser updatePhrUser(String identifier,Map attrs,boolean createPhrUser,boolean update){
		//Save the updated phrUser with the OpenId attributes
		PhrFederatedUser phrUser =null;
		boolean exists = existsUserByOpenId(identifier)
		//if (!ConfigurationService.isAppModeTest()) {
		try{
			phrUser = getUserByOpenId(identifier,attrs,true);

			if(exists && update){
				//update attributes for this identifier
				phrUser.addOpenIdAttributes(identifier, attrs);
				crudSaveResource(phrUser, phrUser.getOwnerUri(),phrUser.getOwnerUri());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			//LOGGER.error("error creating phrUser", e1);
			//gives somethings
			if(phrUser==null){
				phrUser= new PhrFederatedUser(identifier,null);
				//phrUser.setIdentifier(identifier);
			}
		}
		
		return phrUser
	}

	/**
	 * Creates new phrUser using OpenId attributes or any attributes matching the 
	 * required keys
	 * @param identifier
	 * @param attrs
	 * @return
	 */
	public PhrFederatedUser crudCreatePhrUserByOpenId(String identifier,Map attrs){
		PhrFederatedUser user =null;
		try{
			user = new PhrFederatedUser( identifier, attrs);
			this.crudSaveResource(user, user.getOwnerUri(), user.getOwnerUri())
		} catch (Exception e){
			LOGGER.error('error creating new user from OpenId', e)
		}
		return user
	}
	/**
	 * @param entityClazz
	 * @param map
	 * @param create
	 * @param ownerUri
	 * @return
	 */
	
	public def getResourceByExampleToSingle(Class entityClazz,Map map, boolean create,String theOwnerUri){
		def single

		try {
			single = phrsRepositoryClient.crudReadResourceByExampleToSingle( theOwnerUri,  entityClazz, map)
			//if create=true, create if not found
			if( !single && create) {
				single = entityClazz.newInstance()
				single.ownerUri = theOwnerUri
				//don't set resourceUri or dates yet
				single.creatorUri = theOwnerUri
			}
		} catch (Exception e){
			println('getResourceByExample '+e)
			LOGGER.error('getResourceByExample', e)
		}
		//test
		List list = phrsRepositoryClient.crudReadResourceByExample( theOwnerUri,  entityClazz, map)
		
		if(list) println('getResourceByExampleToSingle list size='+list.size())
		else println('getResourceByExampleToSingle list=null')
		
		
		return single
	}
	/*
	 *
	 * @param entityClazz
	 * @param map
	 * @return null if not found
	 * To automatically create and assign required params, use getResourceByExample(Class entityClazz,Map map, boolean create)
	 public def getResourceByExample(Class entityClazz,Map map){
	 return getResourceByExample(entityClazz, map,false)
	 }*/

	/**
	 * 
	 * @param ownerUri
	 * @param clazz
	 * @return
	 */
	public def crudReadResourceSingle(String ownerUri, def clazz){
		def resource
		if(ownerUri!=null){
			return phrsRepositoryClient.crudReadResourceSingle(ownerUri, clazz);
		}
		return null
	}
	/**
	 * 
	 * @param ownerUri
	 * @return
	 */
	public PhrFederatedUser crudReadPhrFederatedUser(String ownerUri){
		PhrFederatedUser resource

		if(ownerUri!=null){
			return phrsRepositoryClient.crudReadResourceSingle(ownerUri, PhrFederatedUser.class);
		}
		return null
	}
	/**
	 * 
	 * @param ownerUri
	 * @return
	 */
	public  List<BasePhrOpenId> crudReadOpenIdUsers(String ownerUri){
		if(ownerUri!=null){
			return phrsRepositoryClient.crudReadResource(ownerUri, BasePhrOpenId.class);
		}
		return null
	}
	/**
	 * 
	 * @param ownerUri
	 * @return
	 */
	public  BasePhrOpenId crudReadOpenIdByOwnerUri(String ownerUri){
		BasePhrOpenId open
		if(ownerUri!=null){
			open =crudReadResourceSingle(ownerUri,BasePhrOpenId.class)
		}
		return open
	}
	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public  BasePhrOpenId crudReadOpenIdByIdentifier(String identifier){
		BasePhrOpenId open
		if(identifier!=null){
			Map map = ['identifier':identifier]
			List list= phrsRepositoryClient.crudReadResourceByExample(null, BasePhrOpenId.class, map);
			if(list && list.size()>0){
				open= list.get(0)
			}
		}
		return open
	}
	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public  PhrFederatedUser crudReadPhrFederatedUserByOpenIdentifier(String identifier){
		PhrFederatedUser user
		if(identifier!=null){
			Map map = ['identifier':identifier]
			List list= phrsRepositoryClient.crudReadResourceByExample(null, BasePhrOpenId.class, map);
			if(list && list.size()>0){
				BasePhrOpenId base= list.get(0)
				String ownerUri = base.ownerUri
				if(ownerUri){
					user=  crudReadPhrFederatedUser(ownerUri)
				}
			}
		}
		return user
	}
	/**
	 * 
	 * @param ownerUri
	 * @param allOpenIdAttributes
	 * @throws Exception
	 */
	public void registerProtocolId(String ownerUri, Map<String,String> attributes) throws Exception{

		if(attributes){

			String protocolId = null;
			if ( attributes.containsKey(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_ID)) {
				value = attributes.get(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_ID);
			}
			//allOpenIdAttributes.containsKey(PhrsConstants.xxx) ? allOpenIdAttributes.get(PhrsConstants.xxx) : null
			if(! protocolId){
				LOGGER.error("BaseDaoService registerProtocolId cannot register null protocolID! for ownerUri="+ownerUri)
				throw new Exception("cannot register null protocolID! for ownerUri="+ownerUri)
			}
			//can be null, for now
			String protocolNamespace = null
			if (attributes.containsKey(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE)) {
				protocolNamespace = attributes.get(PhrsConstants.SESSION_USER_PHR_FILTER_PROTOCOL_NAMESPACE);
			}
			//This updates but we do not unregister Ids with the actor client
			ActorClient actorClient= phrsRepositoryClient.getInteropClients().getActorClient()
			if(protocolNamespace) actorClient.register(protocolNamespace, ownerUri, protocolId)
			else actorClient.register( ownerUri, protocolId)

		}
	}
	/**
	 * 
	 * @param ownerUri
	 * @param openIdUser
	 * @param createPhrUser
	 * @return
	 */
	public BasePhrOpenId crudSaveOpenIdUser(String ownerUri,OpenIdUser openIdUser,boolean createPhrUser){
		BasePhrOpenId open
		if(ownerUri!=null){
			open = crudReadOpenIdByIdentifier(openIdUser.identifier)

			if(open){
				//update
				open.identity= openIdUser.identity
				open.identifier=openIdUser.identifier
				Map attrs = UserSessionService.extractOpenIdParameters(openIdUser)
				if( ! open.getAttributes()) open.setAttributes(new HashMap<String,String>())
				open.getAttributes().putAll(attrs)

				//We have the OpenIdUser, this is called from is authenticated
				open.claimIdAuthenticated=true

			} else {
				//new, create
				open = new BasePhrOpenId()
				//We have the OpenIdUser, this is called from is authenticated
				open.claimIdAuthenticated=true

				open.identifier=openIdUser.identifier
				open.identity= openIdUser.identity
				//extremely important
				open.ownerUri=ownerUri
				open.creatorUri=ownerUri
				

				Map<String,String> attrs = UserSessionService.extractOpenIdParameters(openIdUser)
				if(attrs) open.setAttributes(attrs)

			}
			//store BaseOpenId
			phrsRepositoryClient.crudSaveResource(open)

			//register  protocolId from OpenId This updates but we do not unregister Ids with the actor client
			/*try{
				registerProtocolId(ownerUri, open.getAttributes())
			} catch (Exception e){
				println("error register open id protocol id for owner="+ownerUri+" "+e)
				LOGGER.error("crudSaveOpenIdUser cannot register null protocolID! for ownerUri="+ownerUri)
			}*/

			//get PhrUser, if null create
			if(createPhrUser){
				PhrFederatedUser phrUser= crudReadPhrFederatedUser(ownerUri)
				if( ! phrUser){
					phrUser = new PhrFederatedUser()
					phrUser.ownerUri=ownerUri
					
					phrsRepositoryClient.crudSaveResource(phrUser)
				}
			}
		}
		return open
	}

	/**
	 * 
	 * @param theObject
	 * @param ownerUri
	 * @param creatorUri
	 */
	public void crudSaveResource(def theObject,String ownerUri,String creatorUri){
		//println("user service crudSaveResource, ownerUri")
		if(! theObject.resourceUri){
			theObject.setOwnerUri(ownerUri)
			theObject.setCreatorUri(creatorUri)
			
			//resourceUri is set before actually saving the the object
		}
                theObject.type = theObject.getClass().toString()
		phrsRepositoryClient.crudSaveResource(theObject)
	}
	/**
	 * 	
	 * @param parentId
	 * @param ownerUri
	 * @param type
	 * @param interopResourceId
	 * @param categoryCode
	 * @param code
	 */
	public void crudSavePhrInteropMessageLog(String parentId, String ownerUri, String type, String interopResourceId ,String categoryCode,String code){

		this.crudSavePhrMessageLog(parentId, ownerUri, type, interopResourceId, categoryCode, code, PhrsConstants.MESSAGING_TYPE_INTEROP)
	}
	/**
	 * 
	 * @param parentId
	 * @param ownerUri
	 * @param type
	 * @param interopResourceId
	 * @param categoryCode
	 * @param code
	 * @param messageType
	 */
	public void crudSavePhrMessageLog(String parentId, String ownerUri, String type, String interopResourceId, String categoryCode,String code,String messageType){
		try{
			if(code){
				PhrMessageLog ref = new PhrMessageLog()
				ref.parentId = parentId
				ref.ownerUri = ownerUri
				ref.externalReference=interopResourceId
				ref.category=categoryCode
				ref.code=code
				//deprecated ref.codeValue=codeValue
				ref.messageType=messageType
				phrsStoreClient.getPhrsDatastore().save(ref);
			}
		} catch(Exception e){
			LOGGER.error('Interop crudSavePhrMessageLog, interop parentId= '+parentId+' interopResourceId='+interopResourceId+' messageType='+messageType, e)
		}
	}

	/**
	 * 
	 * @param filterProtocolId
	 * @param filterProtocolNamespace
	 * @return
	 */
	public String getOwnerUriByIdentifierProtocolId(String filterProtocolId,String filterProtocolNamespace) {
			String ownerUri =null;
			if(filterProtocolId){
				Map query =  ['identifier':filterProtocolId,
					'identifierType':PhrsConstants.PROFILE_USER_IDENTIFIER_PROTOCOL_ID
					]
				//'namespace',filterProtocolNamespace
				ProfileUserIdentifiers pui = this.getResourceByExampleToSingle(ProfileUserIdentifiers.class, query, false, null)// theOwnerUri)
				if(pui){
					ownerUri = pui.ownerUri
				}
			}
			return ownerUri;

	}
	/*
	public String crudSaveResourceOnMatches(String ownerUri,Map matchByExample, def theObject) {
		
		if(matchByExample){
			Map query =  ['identifier':filterProtocolId,
				'identifierType':PhrsConstants.PROFILE_USER_IDENTIFIER_PROTOCOL_ID
				]
			//'namespace',filterProtocolNamespace
			ProfileUserIdentifiers pui = this.getResourceByExampleToSingle(ProfileUserIdentifiers.class, query, false, null)// theOwnerUri)
			if(pui){
				ownerUri = pui.ownerUri
			}
		}
		return ownerUri;

}
*/
 
	
}
