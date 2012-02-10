package at.srfg.kmt.ehealth.phrs.persistence.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.Constants

import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrOpenId
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrMessageLog

import at.srfg.kmt.ehealth.phrs.presentation.services.UserSessionService

import com.dyuproject.openid.OpenIdUser
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData

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

        def single =null

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
     * @param ownerUri
     * @return
     */
    public String getUserGreetName(String ownerUri){
        String value=null
        if(ownerUri){
          def puser = this.getPhrUser(ownerUri)
            if(puser){
                value= puser.getNickname() ? puser.getNickname() : null
                value= value ? value : puser.getFullname() ?  puser.getFullname() : null
            }
            if( ! value)  {
                def contact = this.getProfileContactInfo(ownerUri)
                if(contact){
                    value = contact.getFirstName()
                    value = value? value : contact.getLastName()
                }
            }
            if( ! value){
                value= puser.getIdentifier() ? puser.getIdentifier() : null
            }
        }
        return value
    }

    public ProfileContactInfo getProfileContactInfo(String theOwnerUri){
        return getResourceSingle(ProfileContactInfo.class,false,theOwnerUri)
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
            //already done, but check after a new create
            if(user && !user.getOwnerUri()) user.setOwnerUri(theOwnerUri)

        }
        return user;
    }
    public PhrFederatedUser getPhrUser(String theOwnerUri){
        return getPhrUser(theOwnerUri,false);
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
     *  Find the PhrFederatedUser by the claimedId identifier
     * @param identifier
     * @param attrs
     * @param create
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
             //new ownerUri
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

		
                user= new PhrFederatedUser(userId,null);

                user.setUserId(userId);
                user.setCanLocalLogin(true)
                user.setIdentifier(userId);//init to local identifier, but could later assign to an OpenId.
                user.setRole(PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER_LOCAL_LOGIN);

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

                        registerProtocolId(user.getOwnerUri(),
                                Constants.PROTOCOL_ID_PIX_TEST_PATIENT,null);
                                                
                    } else if(userId.equals(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)){
					
                        user.setOwnerUri(userId);
                        user.setRole(PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE);                                              
						
                    } else {
                        user.setOwnerUri(userId);
                    }

                    CoreTestData.createTestUserData();
				
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
      * @param owneruri
     * @param protocolId
     * @param namespace
     */
    public void registerProtocolId(String owneruri, String protocolId, String namespace){
           
        phrsStoreClient.getInteropProcessor().registerProtocolId( owneruri,protocolId,namespace)
        
    }

    /**
     * 
     * @param identifier is claimedId
     * @param attrs
     * @param createPhrUser
     * @param update
     * @return
     */
    public PhrFederatedUser updatePhrUserByClaimedId(String identity,
                  Map attrs,boolean createPhrUser,boolean update){
        //Save the updated phrUser with the OpenId attributes
        PhrFederatedUser phrUser =null;
       // boolean exists = existsUserByOpenId(identity)

        try{
            //find, but create=false
            phrUser = getUserByOpenId(identity,attrs,false);

            if(phrUser && update){   //exists
                //update attributes for this identifier
                phrUser.addOpenIdAttributes(identity, attrs);
                crudSaveResource(phrUser, phrUser.getOwnerUri(),phrUser.getOwnerUri());

            }  else if(createPhrUser){

                phrUser= crudCreatePhrUserByOpenId(identity,attrs)
            }
        } catch (Exception e1) {
            LOGGER.error("error creating phrUser", e1);
            //gives somethings
            if(createPhrUser && phrUser==null && identity!=null){
                phrUser=crudCreatePhrUserByOpenId(identity,attrs)
            }
        }
		
        return phrUser
    }

    /**
     * Creates new phrUser using OpenId attributes or any attributes matching the 
     * required keys
     * @param identity  -  claimedId becomse the the PhrFederatedUser identity
     * @param attrs
     * @return
     */
    //FIXXME
    public PhrFederatedUser crudCreatePhrUserByOpenId(String identity,Map attrs){
        PhrFederatedUser user =null;
        if(identity) {
            try{
                user = new PhrFederatedUser( identity, attrs);
                crudSaveResource(user, user.getOwnerUri(), user.getOwnerUri())
            } catch (Exception e){
                LOGGER.error('error creating new user from OpenId', e)
            }
        }  else {
            LOGGER.error('error creating new user from OpenId, identity(claimedId) is null')
        }
        return user
    }

    public PhrFederatedUser crudCreatePhrUserByOpenId(OpenIdUser openIdUser){
        PhrFederatedUser user =null;
        if(openIdUser && extractId(openIdUser)) {
            try{
                String id=extractId(openIdUser)
                Map attrs = UserSessionService.extractOpenIdParameters(openIdUser)

                user = new PhrFederatedUser( id, attrs);
                crudSaveResource(user, user.getOwnerUri(), user.getOwnerUri())
            } catch (Exception e){
                LOGGER.error('error creating new user from OpenId', e)
            }
        }  else {
                LOGGER.error('error creating new user from OpenId, openIdUser is null')
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
        def single =null

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
            LOGGER.error('getResourceByExample', e)
        }
        return single
    }
    /**
     * 
     * @param ownerUri
     * @param class
     * @return
     */                  
    public  List crudReadResources(String ownerUri, def clazz){
        if(ownerUri && clazz){
            return phrsRepositoryClient.crudReadResources(ownerUri, clazz);
        }
        return null
    }
    /**
     * Refactoring example
     * @param ownerUri
     * @return
     */
    public  List crudReadMedicationResources(String ownerUri){
        if(ownerUri){
            return phrsRepositoryClient.crudReadResources(ownerUri, at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment.class)
        }
        return null
    }
    /**
     * 
     * @param ownerUri
     * @param clazz
     * @return
     */
    public def crudReadResourceSingle(String ownerUri, def clazz){
        def resource
        if(ownerUri && clazz){
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

        if(ownerUri){
            return crudReadResourceSingle(ownerUri, PhrFederatedUser.class);
        }
        return null
    }
    /**
     * 
     * @param ownerUri
     * @return
     */
    public  List<BasePhrOpenId> crudReadOpenIdUsers(String ownerUri){
        if(ownerUri){
            return phrsRepositoryClient.crudReadResources(ownerUri, BasePhrOpenId.class);
        }
        return null
    }
    /**
     * Find a BasePhrOpenId that each stored each time there is a new login
     * @param ownerUri
     * @return
     */
    public  BasePhrOpenId crudReadOpenIdByOwnerUri(String ownerUri){
        BasePhrOpenId open =null
        if(ownerUri){
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
        BasePhrOpenId open=null
        if(identifier){
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

    public  PhrFederatedUser crudReadPhrFederatedUserByOpenIdentifier(String identifier){
        PhrFederatedUser user=null
        if(identifier){
            Map map = ['identifier':identifier]
            List list= phrsRepositoryClient.crudReadResourceByExample(null, PhrFederatedUser.class, map);
            if(list && list.size()>0){
                PhrFederatedUser base= list.get(0)
                String ownerUri = base.ownerUri
                if(ownerUri){
                    user=  crudReadPhrFederatedUser(ownerUri)
                }
            }
        }
        return user
    } */
  
    /**
     *  Stores the OpenId info.
     *  This is not the PhrFederatedUser, but a basic copy of the OpenId info
     *  This might enable  multiple OpenIds per user
     * @param ownerUri
     * @param openIdUser
     * @param createPhrUser
     * @return
     */
    public BasePhrOpenId crudSaveOpenIdUser(String ownerUri,OpenIdUser openIdUser,boolean createPhrUser){
        BasePhrOpenId open =null
        if(ownerUri){
            open = crudReadOpenIdByIdentifier(openIdUser.identifier)
            if(!open) open = new BasePhrOpenId()
            //name clash on PhrFederatedUser- identity is not the same
            open.identity= openIdUser.identity
            open.identifier=openIdUser.identifier
            open.claimedId= openIdUser.claimedId

            //pen.identifier=openIdUser.identifier

            Map attrs = UserSessionService.extractOpenIdParameters(openIdUser)
            if( ! open.getAttributes()) open.setAttributes(new HashMap<String,String>())
            open.getAttributes().putAll(attrs)

            //We have the OpenIdUser, this is called from is authenticated
            open.claimedIdAuthenticated=true
            if(attrs) {
                open.setAttributes(attrs)
            }
            //reset
            open.ownerUri=ownerUri
            open.creatorUri=ownerUri

            //store BaseOpenId 
            //openidfix
            phrsRepositoryClient.crudSaveResource(open)
           //get PhrUser, if null create
            if(createPhrUser){
                PhrFederatedUser phrUser= crudReadPhrFederatedUser(ownerUri)
                if( ! phrUser){
                    //pass IDENTITY not openIdUser.identifier
                    this.crudCreatePhrUserByOpenId(extractId(openIdUser),attrs)
                }
            }
        }
        return open
    }
    /**
     * This provides the choosen user id from the OpenId
     * It will return the user's "claimedId" unless it is null in the OpenId object from the OP (OpenId provider), otherwise, it is
     * the OpenIdUser "identity" - this is the OP's representation of the "claimedId". For example, from Google, the identity and claimedId
     * appear to be the same.
     *
     * @param openIdUser
     * @return
     */
    public static String extractId(OpenIdUser openIdUser )  {
            return openIdUser.getClaimedId() ? openIdUser.getClaimedId() :  openIdUser.getIdentity();
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
            Map query =  ['pixIdentifier.identifier':filterProtocolId]
            //,'identifierType':PhrsConstants.PROFILE_USER_IDENTIFIER_PROTOCOL_ID

            //'namespace',filterProtocolNamespace
            ProfileContactInfo info = this.getResourceByExampleToSingle(ProfileContactInfo.class, query, false, null)


            if(info){
                ownerUri = info.ownerUri
            }
        }
        return ownerUri;

    }
    /**
     *
     * @param ownerUri
     * @return
     */
    public boolean hasProtocolId(String ownerUri){
       String id =   getProtocolId(ownerUri);
       if(id){
           return true
       }
        return false
    }
    /**
     * get the ProtocolId stored locally
     * @param ownerUri
     * @return
     */
    public String getProtocolId(String ownerUri){
        String protocolId=null
        if(ownerUri){

            //'namespace',filterProtocolNamespace

            ProfileContactInfo info = this.getResourceSingle(ProfileContactInfo.class,false,ownerUri)

            if(info && info.pixIdentifier){
                protocolId = info.pixIdentifier.identifier
            }
        }
        return protocolId;


    }
// This is the alternative when this issues about protocolID settle down. Ultimately, and PIX like component is needed
// to handle various IDs, but the interop component must also use the component.
//    public String getOwnerUriByIdentifierProtocolId(String filterProtocolId,String filterProtocolNamespace) {
//        String ownerUri =null;
//        if(filterProtocolId){
//            Map query =  ['identifier':filterProtocolId,
//                    'identifierType':PhrsConstants.PROFILE_USER_IDENTIFIER_PROTOCOL_ID
//            ]
//            //'namespace',filterProtocolNamespace
//            ProfileUserIdentifiers pui = this.getResourceByExampleToSingle(ProfileUserIdentifiers.class, query, false, null)// theOwnerUri)
//            if(pui){
//                ownerUri = pui.ownerUri
//            }
//
//        }
//        return ownerUri;
//
//    }
}
 
	
