package at.srfg.kmt.ehealth.phrs.persistence.client

import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrOpenId
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrFederatedUser
import at.srfg.kmt.ehealth.phrs.model.baseform.PhrMessageLog
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService
import at.srfg.kmt.ehealth.phrs.security.services.login.LoginUtils
import at.srfg.kmt.ehealth.phrs.security.services.login.RegistrationModel
import at.srfg.kmt.ehealth.phrs.support.test.CoreTestData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment

/**
 *
 * Common methods that including those that require the ownerUri (phrId) in the method 
 * The UserService class uses the ownerUri from authenticated user in session
 *
 */
public class CommonDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommonDao.class);



    public CommonDao() {

    }
    /**
     * @deprecated
     */
    public CommonDao(PhrsStoreClient phrsStoreClient) {

    }
    /**
     * @deprecated
     */
    public CommonDao(PhrsRepositoryClient repoClient) {

    }


    public PhrsRepositoryClient getPhrsRepositoryClient() {
        return getPhrsStoreClient().getPhrsRepositoryClient()
    }

    public PhrsStoreClient getPhrsStoreClient() {
        return PhrsStoreClient.getInstance()
    }

    /**
     * Should use the default method, however, to search for a User  object, there might not be a phrId stored in the session yet
     * @param entityClazz
     * @param create
     * @param theOwnerUri
     * @return
     */
    public def getResourceSingle(Class entityClazz, boolean create, String theOwnerUri) {

        def single = null

        try {
            single = getPhrsRepositoryClient().crudReadResourceSingle(theOwnerUri, entityClazz)

            if (!single && create) {
                single = entityClazz.newInstance()
                single.ownerUri = theOwnerUri //getUserPhrId()

                //don't set resourceUri or dates yet
                single.creatorUri = theOwnerUri
            }
        } catch (Exception e) {

            LOGGER.error('getResourceSingle', e)
        }
        return single
    }
    /**
     *
     * @param ownerUri
     * @return
     */
    public String getUserGreetName(String ownerUri) {
        String value = null
        if (ownerUri) {
            try {
                PhrFederatedUser puser = getPhrUser(ownerUri)
                if (puser) {
                    value = puser.getNickname()
                    if (!value) value = puser.getFullname()
                }
                if (!value) {
                    ProfileContactInfo contact = getProfileContactInfo(ownerUri)
                    if (contact) {
                        value = contact.getFirstName()
                        if (!value) value = contact.getLastName()
                    }
                }
                if (!value && puser) {
                    value = puser.getIdentifier()
                }
            } catch (Exception e) {
                LOGGER.error('greet name ownerUri=' + ownerUri, e)
            }
        }
        return value
    }

    public ProfileContactInfo getProfileContactInfo(String theOwnerUri) {
        return (ProfileContactInfo) getResourceSingle(ProfileContactInfo.class, false, theOwnerUri)
    }
    /**
     *
     * @param theOwnerUri
     * @param create
     * @return
     */
    public PhrFederatedUser getPhrUser(String theOwnerUri, boolean create) {

        PhrFederatedUser user = null;
        if (theOwnerUri) {

            user = (PhrFederatedUser) getResourceSingle(PhrFederatedUser.class, create, theOwnerUri)
            //already done, but check after a new create
            if (user && !user.getOwnerUri()) user.setOwnerUri(theOwnerUri)

        }
        return user;
    }

    public PhrFederatedUser getPhrUser(String theOwnerUri) {
        return getPhrUser(theOwnerUri, false);
    }

    /**
     *
     * @param ownerUri
     * @return
     */
    public boolean existsUserByOwnerUri(String ownerUri) {
        def result = getPhrUser(ownerUri, false)

        return result ? true : false
    }
    /**
     *
     * @param identifier
     * @return
     */
    public boolean existsUserByOpenId(String identifier) {
        def result = getUserByOpenId(identifier)

        return result ? true : false
    }


    public PhrFederatedUser getUserByOpenId(String identifier) throws Exception {

        PhrFederatedUser user = null;
        if (identifier) {
            Map query = ['identifier': identifier]
            user = (PhrFederatedUser) getResourceByExampleToSingle(PhrFederatedUser.class, query, false, null)// theOwnerUri)
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
    public PhrFederatedUser getPhrUserByLocalUserId(String userId, Map attrs, boolean create) throws Exception {
        //if exists ...get
        //replace - update or create if new
        PhrFederatedUser user = null;
        boolean saveUser = true;
        if (userId) {
            String testUser = ConfigurationService.getInstance().getProperty("test.user.1.login.id", PhrsConstants.AUTHORIZE_USER_PREFIX_TEST);

            if (userId.equals(testUser)) {//if(userId.equals(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST)){
                //phrtest user ALWAYS reset this user
                user = CoreTestData.createTestUserData()


            } else {

                Map query = ['userId': userId] //do not search on identifier, that is for sso or openIDs
                user = (PhrFederatedUser) getResourceByExampleToSingle(PhrFederatedUser.class, query, false, null)// theOwnerUri should be null because of the querymap

                if (create && !user) {
                    user = new PhrFederatedUser(userId, null);
                    //owner Uri created automatically
                    user.setUserId(userId);
                    user.setCanLocalLogin(true)
                    user.setIdentifier(userId);//init to local identifier, but could later assign to an OpenId.
                    user.setRole(PhrsConstants.AUTHORIZE_ROLE_PHRS_SUBJECT_CODE_USER_LOCAL_LOGIN);
                    //remove
                    if (userId.equals(testUser)) {//if(userId.equals(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST)){
                        //phrtest user
                        //CoreTestData.createTestUserData();
                        saveUser = false;

                    } else if (LoginUtils.isLocalLogin(userId)) {
                        //just clean up the ownerUris and roles for tests etc

                        if (userId.equals(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST_1)) {

                            user.setOwnerUri(PhrsConstants.USER_TEST_HEALTH_PROFILE_ID_1);
                            user.setRole(PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_DOCTOR);

                        } else if (userId.equals(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)) {

                            user.setOwnerUri(userId);
                            user.setRole(PhrsConstants.AUTHORIZE_ROLE_SUBJECT_CODE_NURSE);

                        } else {
                            user.setOwnerUri(userId);
                        }

                    }
                    if (attrs) {
                        user.setAttributes(attrs)
                    }

                    if (saveUser) this.crudSaveResource(user, user.getOwnerUri(), user.getOwnerUri());

                }
            }
        }
        return user;
    }
    /*
                       } else if (userId.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_TEST)
                            || userId.startsWith(PhrsConstants.AUTHORIZE_USER_ADMIN)
                            || userId.startsWith(PhrsConstants.AUTHORIZE_USER_PREFIX_AUTO_USER)
                            || userId.startsWith(PhrsConstants.AUTHORIZE_USER_VT_SCENARIO_NURSE)) {
     */
    /**
     * @deprecated
     * Updates interop component registry for actors
     * @param owneruri
     * @param protocolId
     * @param namespace
     */
    public void registerProtocolId(String ownerUri, String protocolId, String namespace) {

        //null namespace will use default
        // getPhrsStoreClient().getInteropClients().registerProtocolId( ownerUri,  protocolId,  namespace)
    }

    /**
     * Manage user and update incoming attributes
     * @param model
     * @param create
     * @param update
     * @return
     */
    public PhrFederatedUser updatePhrUser(RegistrationModel model, boolean create, boolean update) {
        //Save the updated phrUser with the Registration model
        PhrFederatedUser phrUser = null;
        if (model != null && model.getOpenId() != null) {
            String identifier = model.getOpenId();
            try {
                phrUser = getUserByOpenId(identifier);

                if (phrUser && update) {   //exists
                    //update attributes for this identifier
                    phrUser.updateRegistrationData(model);

                    crudSaveResource(phrUser, phrUser.getOwnerUri(), phrUser.getOwnerUri());
                    LOGGER.debug(" updatePhrUser UPDATE registration identifer" + identifier);
                } else if (create) {

                    // phrUser = crudCreatePhrUserByOpenId(identifier, attrs)
                    phrUser = new PhrFederatedUser(identifier, model);
                    crudSaveResource(phrUser, phrUser.getOwnerUri(), phrUser.getOwnerUri());
                    LOGGER.debug(" updatePhrUser CREATE registration identifer" + identifier);
                }
            } catch (Exception e1) {
                LOGGER.error("error creating phrUser", e1);
                //give something
                if (create && phrUser == null && identifier != null) {
                    //phrUser = crudCreatePhrUserByOpenId(identity, attrs)
                    phrUser = new PhrFederatedUser(identifier, model);
                }
            }
        }

        return phrUser

    }

    /**
     * @param entityClazz
     * @param map
     * @param create
     * @param ownerUri
     * @return
     */
    public def getResourceByExampleToSingle(Class entityClazz, Map map, boolean create, String theOwnerUri) {
        def single = null

        try {
            single = getPhrsRepositoryClient().crudReadResourceByExampleToSingle(theOwnerUri, entityClazz, map)
            //if create=true, create if not found
            if (!single && create) {
                single = entityClazz.newInstance()
                single.ownerUri = theOwnerUri
                //don't set resourceUri or dates yet
                single.creatorUri = theOwnerUri
            }
        } catch (Exception e) {
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
    public List crudReadResources(String ownerUri, def clazz) {
        if (ownerUri && clazz) {
            return getPhrsRepositoryClient().crudReadResources(ownerUri, clazz);
        }
        return null
    }
    /**
     * Refactoring example
     * @param ownerUri
     * @return
     */
    public List crudReadMedicationResources(String ownerUri) {
        if (ownerUri) {
            return getPhrsRepositoryClient().crudReadResources(ownerUri, at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment.class)
        }
        return null
    }
    public List getResourcesVitalBodyWeight(String ownerUri){
        if (ownerUri) {
            return getPhrsRepositoryClient().crudReadResources(ownerUri, at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBodyWeight.class)
        }
        return null
    }

    public List getResourcesMedication(String ownerUri){

        return getResourcesVitalBodyWeight(ownerUri)
    }

    public List getResourcesVitalBloodPressure(String ownerUri){
        if (ownerUri) {
            return getPhrsRepositoryClient().crudReadResources(ownerUri, at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBloodPressure.class)
        }
        return null
    }

    public List getResourcesProblem(String ownerUri){
        if (ownerUri) {
            return getPhrsRepositoryClient().crudReadResources(ownerUri, at.srfg.kmt.ehealth.phrs.model.baseform.ObsProblem.class)
        }
        return null
    }

    public List getResourcesADL(String ownerUri){
        if (ownerUri) {
            return getPhrsRepositoryClient().crudReadResources(ownerUri, at.srfg.kmt.ehealth.phrs.model.baseform.ProfileActivityDailyLiving.class)
        }
        return null
    }

    /**
     *
     * @param ownerUri
     * @param clazz
     * @return
     */
    public def crudReadResourceSingle(String ownerUri, def clazz) {
        def resource
        if (ownerUri && clazz) {
            return getPhrsRepositoryClient().crudReadResourceSingle(ownerUri, clazz);
        }
        return null
    }
    /**
     *
     * @param ownerUri
     * @return
     */
    public PhrFederatedUser crudReadPhrFederatedUser(String ownerUri) {
        PhrFederatedUser resource

        if (ownerUri) {
            return (PhrFederatedUser) crudReadResourceSingle(ownerUri, PhrFederatedUser.class);
        }
        return null
    }
    /**
     * @deprecated
     * @param ownerUri
     * @return
     */
    public List<BasePhrOpenId> crudReadOpenIdUsers(String ownerUri) {
        if (ownerUri) {
            return getPhrsRepositoryClient().crudReadResources(ownerUri, BasePhrOpenId.class);
        }
        return null
    }
    /**
     * Find a BasePhrOpenId that each stored each time there is a new login
     * @param ownerUri
     * @return
     */
    public BasePhrOpenId crudReadOpenIdByOwnerUri(String ownerUri) {
        BasePhrOpenId open = null
        if (ownerUri) {
            open = (BasePhrOpenId) crudReadResourceSingle(ownerUri, BasePhrOpenId.class)
        }
        return open
    }
    /**
     * @deprecated
     * @param identifier
     * @return
     */
    public BasePhrOpenId crudReadOpenIdByIdentifier(String identifier) {
        BasePhrOpenId open = null
        if (identifier) {
            Map map = ['identifier': identifier]
            List list = getPhrsRepositoryClient().crudReadResourceByExample(null, BasePhrOpenId.class, map);
            if (list && list.size() > 0) {
                open = (BasePhrOpenId) list.get(0)
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
    public void crudSaveResource(def theObject, String ownerUri, String creatorUri) {
        //println("user service crudSaveResource, ownerUri")
        if (!theObject.resourceUri) {
            theObject.setOwnerUri(ownerUri)
            theObject.setCreatorUri(creatorUri)

            //resourceUri is set before actually saving the the object
        }
        theObject.type = theObject.getClass().toString()
        getPhrsRepositoryClient().crudSaveResource(theObject)
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
    public void crudSavePhrInteropMessageLog(String parentId, String ownerUri, String type, String interopResourceId, String categoryCode, String code) {

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
    public void crudSavePhrMessageLog(String parentId, String ownerUri, String type, String interopResourceId, String categoryCode, String code, String messageType) {
        try {
            if (code) {
                PhrMessageLog ref = new PhrMessageLog()
                ref.parentId = parentId
                ref.ownerUri = ownerUri
                ref.externalReference = interopResourceId
                ref.category = categoryCode
                ref.code = code
                //deprecated ref.codeValue=codeValue
                ref.messageType = messageType
                getPhrsStoreClient().getPhrsDatastore().save(ref);
            }
        } catch (Exception e) {
            LOGGER.error('Interop crudSavePhrMessageLog, interop parentId= ' + parentId + ' interopResourceId=' + interopResourceId + ' messageType=' + messageType, e)
        }
    }

    /**
     * Searches for protocolIdPix
     * @param filterProtocolId
     * @param filterProtocolNamespace
     * @return
     */
    //YYYYY
    public String getOwnerUriByIdentifierProtocolId(String filterProtocolId, String filterProtocolNamespace) {
        String ownerUri = null;
        if (filterProtocolId) {
            Map query = ['protocolIdPix': filterProtocolId]
            //,'identifierType':PhrsConstants.PROFILE_USER_IDENTIFIER_PROTOCOL_ID
            //'namespace',filterProtocolNamespace
            //ownerUri null because of querymap
            PhrFederatedUser info = (PhrFederatedUser) getResourceByExampleToSingle(PhrFederatedUser.class, query, false, null)

            if (info) {
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
    public boolean hasProtocolId(String ownerUri) {
        String id = getProtocolId(ownerUri);
        if (id) {
            return true
        }
        return false
    }
    /**
     * get the ProtocolId stored locally
     * @param ownerUri
     * @return
     */
    public String getProtocolId(String ownerUri) {
        String protocolId = null
        if (ownerUri) {

            PhrFederatedUser pfu = getPhrUser(ownerUri)
            if (pfu) {
                protocolId = pfu.getProtocolId()
            }

        }
        return protocolId;

    }
    public String getProtocolId(String ownerUri,String namespace) {

        return getProtocolId(ownerUri);

    }

    /**
     * Get the CIED serial number (prefix of cied:)
     * that includes parts
     * @param ownerUri
     * @return map keys: type, id
     * This might be a protocol id with type=pid
     */
    public Map<String, String> getPixQueryAttributes(String ownerUri) {
        Map<String, String> map = new HashMap<String, String>();
        if (ownerUri) {

            PhrFederatedUser pfu = getPhrUser(ownerUri)
            if (pfu) {
                String id = pfu.getPixQueryIdUser()
                String type = pfu.getPixQueryIdType()
                if (id && type) {
                    map.put("id", id);
                    map.put("type", id);
                }
            }

        }
        return map;

    }

    public boolean hasMedication(String ownerUri, String  medName, String productCode) {
       
        //not blank or null
        if(ownerUri && productCode){
            try{
                Map query = ['productCode': productCode]

                def obj= getResourceByExampleToSingle(MedicationTreatment.class,query,false,ownerUri)

                if(obj!=null) return true;
            } catch(Exception e) {
                LOGGER.error("check hasMedication ",e)
            }
        } else {
            LOGGER.error("check hasMedication null parameter "+ownerUri+" productCode="+productCode)
        }
        return false;
    }

}
 
	
