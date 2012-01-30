package at.srfg.kmt.ehealth.phrs.presentation.services
import static at.srfg.kmt.ehealth.phrs.Constants.*

import java.util.Date
import java.util.Map

import org.apache.commons.beanutils.DynaBean
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil
import at.srfg.kmt.ehealth.phrs.model.baseform.ActionPlanEvent
import at.srfg.kmt.ehealth.phrs.model.baseform.BasePhrsModel
import at.srfg.kmt.ehealth.phrs.model.baseform.MedicationTreatment
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsActivityPhysical
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsProblem
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBloodPressure
import at.srfg.kmt.ehealth.phrs.model.baseform.ObsVitalsBodyWeight
import at.srfg.kmt.ehealth.phrs.model.baseform.PixIdentifier
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileActivityDailyLiving
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileContactInfo
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileMedicalContactInfo
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileRisk
import at.srfg.kmt.ehealth.phrs.model.baseform.ProfileUserContactInfo
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils
import at.srfg.kmt.ehealth.phrs.security.services.PixService
import at.srfg.kmt.ehealth.phrs.presentation.utils.DynaUtil

/**
 * Used by the PhrsStoreClient to send messages whenever an appropriate resource is saved
 * 
 */
/*
See Terminology service and
status for actions: action.categories.activity.sport
TAG_ACTIVITIES_OF_DAILY_LIVING_STATUS="http://www.icardea.at/phrs/instances/ActivityStatus"; http://www.icardea.at/phrs/instances/ICanDo http://www.icardea.at/phrs/instances/IRequireAssistance
 */
public class  InteropAccessService implements Serializable{
    private final static Logger LOGGER = LoggerFactory.getLogger(InteropAccessService.class);
    public final static String DATE_PATTERN_INTEROP_DATE_TIME ="yyyyMMddHHmm";
    public final static String REFERENCE_NOTE_PREFIX='resourcephr='
    public final static String DRUG_CODE_DEFAULT_PHR="MyDrugCode";
    /**
     * Expected by the interop services: yyyyMMddHHmm
     */
    /*
    1. Risks:
    a. Constants.HL7V3_RISK_OF
    2. ActivityDailyLiving
    a. http://www.icardea.at/phrs/instances/ActivitiesOfDailyLiving
    b. Constants.HL7V3_FINDING ?
    c. ??
    3. Physical Activities and Sport
    a. http://www.icardea.at/phrs/instances/ActivitiesOfDailyLiving
    b. Constants.HL7V3_FINDING ?
    c. ??
    4. Meds
    No adminRoute asked...know about Pills, tablets, mg
    What are other codes for adminroute?
    I changed my local git copy : Problem client constructors are now "public", they were package scoped
     */
    PhrsStoreClient phrsStoreClient

    InteropProcessor iprocess;
    /**
     * 
     */
    public InteropAccessService(){

        phrsStoreClient = PhrsStoreClient.getInstance()
        init()
    }
    /**
     * 
     * @param phrsStoreClient
     */
    public InteropAccessService(PhrsStoreClient phrsStoreClient){

        this.phrsStoreClient = phrsStoreClient
        init()
    }
    
    private void init(){
        iprocess=new InteropProcessor(phrsStoreClient);
    }
    
    public InteropClients getInteropClients(){
        return phrsStoreClient.getInteropClients()
    }
    
    public CommonDao getCommonDao(){
        return phrsStoreClient.getCommonDao()
    }

    /**
     * 
     * @param ownerUri
     * @param protocolId
     */
    public void registerUser(String ownerUri, String protocolId){
        //ownerUri = phrId
        getInteropClients().getActorClient().register(ownerUri,protocolId)

        return

    }
    /**
     * 
     * @param ownerUri
     * @param protocolId
     * @param protocolNamespace
     */
    public void registerUser(String ownerUri, String protocolId, String protocolNamespace){

        getInteropClients().getActorClient().register(protocolNamespace,ownerUri,protocolId)
    }
    /**
     * 
     * @param ownerUri
     * @param protocolNamespace
     * @return
     */
    public String getProtocolId(String ownerUri, String protocolNamespace){
        return getInteropClients().getActorClient().getProtocolId(protocolNamespace, ownerUri)
    }
    /**
     * 
     * @param ownerUri
     * @return
     */
    public String getProtocolId(String ownerUri){
        return getInteropClients().getActorClient().getProtocolId( ownerUri)
    }
    /**
     * 
     * @param resource
     * @return
     */
    public Map sendMessages(def resource){
        sendMessages(resource,null)
    }

    /**
     * ResourceType is the resource class canonical Name
     * Default action for saving resouce
     * @param resource
     * @param attrs
     * @return
     */
    public Map sendMessages(def resource, Map attrs){
        String resourceType= resource.getClass().getCanonicalName()
        //write list to MessageInterop
        return sendMessages(resource,resourceType,null,attrs )
    }
    /**
     * 
     * @param resource
     * @param action - allows finer details for handling other client services e.g. delete
     * @param attrs
     * @return
     */
    public Map sendMessages(def resource, String action,Map attrs){
        String resourceType= resource.getClass().getCanonicalName()
        //write MessageInterop
        return sendMessages(resource,resourceType,action,attrs )
    }

    /**
     * Transform, Enrich status code
     * Transform any Local resource.status to standard status 
     * @param status
     * @return
     */
    public String transformStatus(String status){
        String out=status

        return out
    }

    /**
     * Transform, Enrich
     * Transform any Local resource.code to a standard code
     * @param code
     * @return
     */
    public String transformCode(String code){
        String out=code

        return out
    }
    /**
     * 
     * @param category
     * @param type
     * @return
     */
    public String transformCategory(String category, String type){
        String out=category

        return out
    }

    /**
     * Transform to date string yyyyMMddHHmm
     * @param date
     * @param dateTime
     * @return
     * 
     */
    public String transformDate(Date date, Date defaultDate){
        Date theDate = date
        if(!theDate) {
            if(defaultDate) theDate = defaultDate
        }
        if(theDate){

            return HealthyUtils.formatDate( theDate, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)
        }
        return null
    }

    public static def getDynaBeanPropertyValue(DynaBean bean, String property, String defaultValue){
        return DynaUtil.getStringProperty(bean,property,defaultValue)

    }
    /*
    public String transformDate(Date date,boolean dateTime, Date defaultDate){
    Date theDate = date
    if(!theDate) {
    if(defaultDate) theDate = defaultDate
    }
    if(theDate)
    return HealthyUtils.formatDate( theDate, null, DATE_PATTERN_INTEROP_DATE_TIME)
    return null
    }
     */
    public  String createReferenceNote(String resourceUri){   
        return InteropProcessor.createReferenceNote()
    }
    /**
     *
     * @param resource
     * @param resourceType - resource might be a map, etc in future, etc. Currently the resource canonical Name
     * @param attrs
     * @return Map of URIs associated with the objects stored by each message. 'code':interop resource identifier of the message stored
     * There can multiple messages per PHR portal object e.g. Systolic and Diastolic messages. Upon updates, the values can be updated
     *  Theses can be stored interop message table.  Useful if these are to be deleted
     */
    public Map sendMessages(def resource,String resourceType, String action,Map attrs){
        Map messageIdMap=[:]

        if(resource && resource instanceof BasePhrsModel){

            try{
                //the resulting message URI that is added to the result list
                String messageId

                BasePhrsModel res = (BasePhrsModel)resource
                String owner 	= res.ownerUri

                String status 	= this.transformStatus(res.status)
                //status = status?: null
                status = status!=null ? status : Constants.STATUS_RUNNING;

                String categoryCode 	= this.transformCategory(res.category,resourceType)
                categoryCode = categoryCode?: null

                String valueCode 	= this.transformCode(res.code)
                valueCode = valueCode?: null

                String dateStringStart 	= transformDate(res.beginDate,res.endDate)
                dateStringStart = dateStringStart?: transformDate(new Date(),new Date())

                String dateStringEnd 	= transformDate(res.endDate,(Date)null)
                dateStringEnd = dateStringEnd?: null

                    
                    
                //res.note is by default not sharable
                /**
                 * Use the note to tag this record. 
                 * Be sure to write note to multiple messages such as Vital signs with separate messages for Body weight, height, sys, diastolic
                 * 				
                 */
                //note cannot be null
                                
                String referenceNote = createReferenceNote(res.resourceUri) //'' //res.note is by default not sharable
                
                                
                //resourceUri appears only if resource already saved
                if(!res.resourceUri){
                    println("sendMessages  (note) resourceUri is null")
                    LOGGER.debug("sendMessages  (note) resourceUri is null");
                    throw new Exception(" resourceUri not yet set, PHR object was not yet saved?")   
                }
                String theParentId = res.resourceUri
     

                switch ( resourceType ) {

                    case ProfileRisk.getCanonicalName():
                    //Risks reported under Problem
                    //ProfileRisk domain=(ProfileRisk)resource
                    if(categoryCode != PhrsConstants.HL7V3_CODE_CATEGORY_RISK){
                        categoryCode = PhrsConstants.HL7V3_CODE_CATEGORY_RISK  //TODO logger, should be category, but always HL7V3_SYMPTOM for this object type
                    }
                    String interopRef = findMessageWithReference(owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS,categoryCode)
                    if( ! interopRef){
                        messageId = getInteropClients().getProblemEntryClient()
                        .addProblemEntry(
                            owner,
                            categoryCode,// TODO more specific detail code part of finding
                            status,
                            dateStringStart,
                            dateStringEnd,
                            referenceNote,
                            valueCode);

                        if(messageId)  {
                            messageIdMap.put(categoryCode,messageId)
                        }
                    } else {
                        //this.updateMessageProblem(theParentId, interopRef, ., valueCode);
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_STATUS, status);
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);
                    }
                    break

                    case ProfileActivityDailyLiving.class.getCanonicalName():

                    categoryCode = PhrsConstants.HL7V3_CODE_CATEGORY_ADL //TODO logger, should be category, but always HL7V3_SYMPTOM for this object type
                    String interopRef = findMessageWithReference(owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS,categoryCode)

                    if( ! interopRef){
                        // TODO more specific detail code part of finding
                        messageId = getInteropClients().getProblemEntryClient()
                        .addProblemEntry(
                            owner,
                            categoryCode,
                            status,
                            dateStringStart,
                            dateStringEnd,
                            referenceNote,
                            valueCode);

                        if(messageId)  {
                            messageIdMap.put(categoryCode,messageId)
                        }
                    } else {
                        //this.updateMessageProblem(theParentId, interopRef, ., valueCode);
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_STATUS, status);
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);

                    }
                    break

                    case ActionPlanEvent.class.getCanonicalName():

                    //send message only for the sports identifier resource.code= action.categories.activity.sport
                    if(valueCode == InteropTermTransformer.CODE_WATCH_SPORT){
                        categoryCode = PhrsConstants.HL7V3_CODE_CATEGORY_PHYS_ACTIVITY //TODO logger, should be category, but always HL7V3_SYMPTOM for this object type
                        String interopRef = findMessageWithReference(owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS,categoryCode)
                        if( ! interopRef){
                            messageId = getInteropClients().getProblemEntryClient()
                            .addProblemEntry(
                                owner,
                                categoryCode,
                                status,
                                dateStringStart,
                                dateStringEnd,
                                referenceNote,
                                valueCode);

                            if(messageId)  {
                                messageIdMap.put(categoryCode,messageId)
                            }

                        } else {
                            //this.updateMessageProblem(theParentId, interopRef, ., valueCode);
                            this.updateMessageProblem(theParentId, interopRef, HL7V3_STATUS, status);
                            this.updateMessageProblem(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                            this.updateMessageProblem(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);

                        }
                    }
                    break

                    case MedicationTreatment.class.getCanonicalName():

                    MedicationTreatment domain=(MedicationTreatment)resource
                    messageIdMap = iprocess.sendMedicationMessage(domain);
                    
                    break

                    case ObsActivityPhysical.class.getCanonicalName():

                    break

                    case ObsProblem.class.getCanonicalName():

                    ObsProblem domain=(ObsProblem)resource

                    if(categoryCode != Constants.HL7V3_SYMPTOM){
                        categoryCode = Constants.HL7V3_SYMPTOM
                        //TODO logger, should be category, but always HL7V3_SYMPTOM for this object type
                    }
                    String interopRef = findMessageWithReference(owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS,categoryCode)
                    if( ! interopRef){
                        messageId = getInteropClients().getProblemEntryClient()
                        .addProblemEntry(
                            owner,
                            categoryCode,
                            status,
                            dateStringStart,
                            dateStringEnd,
                            referenceNote,
                            valueCode);

                        if(messageId)  {
                            messageIdMap.put(categoryCode,messageId)
                        }
                    } else {
                        
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_STATUS, status);
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                        this.updateMessageProblem(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);

                    }
                    break

                    case ObsVitalsBloodPressure.class.getCanonicalName():

                    ObsVitalsBloodPressure domain=(ObsVitalsBloodPressure)resource
                    //not using category code, instead we split form into a few pieces
                    categoryCode=Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE
                    String interopRef = findMessageWithReference(owner, theParentId, Constants.PHRS_VITAL_SIGN_CLASS,categoryCode)
                    String value=domain.getSystolic() ? domain.getSystolic().toString() : '0'

                    if( ! interopRef){
                        messageId = getInteropClients().getVitalSignClient().addVitalSign(owner,
                            Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE,
                            referenceNote,
                            dateStringStart,
                            status,
                            value,
                            Constants.MM_HG);

                        if(messageId) {
                            messageIdMap.put(categoryCode,messageId)
                        }
                    } else {
                        this.updateMessageVitals(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                        this.updateMessageVitals(theParentId, interopRef, HL7V3_VALUE, value);
                    }

                    categoryCode=Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE
                    interopRef = findMessageWithReference(owner, theParentId, Constants.PHRS_VITAL_SIGN_CLASS,categoryCode)
                    value=domain.getDiastolic() ? domain.getDiastolic().toString() : '0'
           
                    if( ! interopRef){
                        messageId = getInteropClients().getVitalSignClient().addVitalSign(owner,
                            categoryCode,
                            referenceNote,
                            dateStringStart,
                            status,
                            value,
                            Constants.MM_HG);

                        if(messageId)  {
                            messageIdMap.put(categoryCode,messageId)
                        }
                    } else {
                        this.updateMessageVitals(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                        this.updateMessageVitals(theParentId, interopRef,HL7V3_VALUE , value);

                    }
                    break

                    case ObsVitalsBodyWeight.class.getCanonicalName():

                    ObsVitalsBodyWeight domain=(ObsVitalsBodyWeight)resource
                    categoryCode=Constants.ICARDEA_INSTANCE_BODY_WEIGHT
                    // ownerUri, resourceUri, phrsClass,categoryCode,valueCode
                    String interopRef = findMessageWithReference(
                        owner, theParentId,  Constants.PHRS_VITAL_SIGN_CLASS, categoryCode)
                    String value = domain.getBodyWeight() ? domain.getBodyWeight().toString() : '0'
                    if( ! interopRef){
                        messageId = getInteropClients().getVitalSignClient().addVitalSign(owner,
                            categoryCode,
                            referenceNote,
                            dateStringStart,
                            status,
                            value,
                            Constants.KILOGRAM);

                        if(messageId)  messageIdMap.put(categoryCode,messageId)
                    } else {
                        //this.updateMessageProblem(theParentId, interopRef, ., valueCode);
                        this.updateMessageVitals(theParentId, interopRef, HL7V3_STATUS, status);
                        this.updateMessageVitals(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                        this.updateMessageVitals(theParentId, interopRef, HL7V3_END_DATE, value);

                    }
                    categoryCode=Constants.ICARDEA_INSTANCE_BODY_HEIGHT
                    interopRef = findMessageWithReference(owner, theParentId, Constants.PHRS_VITAL_SIGN_CLASS,categoryCode)
                    value = domain.getBodyHeight() ? domain.getBodyHeight().toString() : '0'

                    if(interopRef){
                        messageId = getInteropClients().getVitalSignClient()
                        .addVitalSign(owner,
                            categoryCode,
                            referenceNote,
                            dateStringStart,
                            Constants.STATUS_COMPELETE,//use instead of current status
                            value,
                            Constants.CENTIMETER);

                        if(messageId)  {
                            messageIdMap.put(categoryCode,messageId)
                        }
                    } else {
                        //update
                        this.updateMessageVitals(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                        this.updateMessageVitals(theParentId, interopRef,HL7V3_VALUE , value);

                    }
                    break

                    case [
                        ProfileContactInfo.class.getCanonicalName(),
                        ProfileUserContactInfo.class.getCanonicalName(),
                        ProfileMedicalContactInfo.class.getCanonicalName()
                    ]:
                    //do nothing
                    break

                    default:
                    break
                }
            } catch (TripleException e) {
                LOGGER.error('Interop client error sending message for resource= '+resourceType, e)
                e.printStackTrace();
            
            } catch (Exception e){
                LOGGER.error('Interop client error sending message for resource= '+resourceType, e)
            
            } catch (java.lang.Error e) {
                LOGGER.error("sesame throws errors not exception..."+resourceType,,e);
                e.printStackTrace();
            }
        }
        return messageIdMap
    }

    /**
     * 
     * @param ownerUri
     * @param resourceUri
     * @param categoryCode - this can be null, it is used to select a  specific code within a phrsClass e.g. ObservationClass includes resources by code for Body height, symptom
     * @param phrsClass - The Phrs class Constants.PHRS_MEDICATION_CLASS, PHRS_VITAL_SIGN_CLASS, PHRS_OBSERVATION_ENTRY_CLASS
     * @return
     */
    public String findMessageWithReference(String ownerUri,String resourceUri,String phrsClass, String categoryCode){
        
        String value = iprocess.findMessageWithReference( ownerUri, resourceUri, phrsClass,categoryCode)
        return value
    }
    /**
     * 
     * @param resourceUri
     * @param interopId
     * @return
     */
    public boolean removeMessage(String resourceUri,String interopId){
        boolean exists=false
        if(interopId){
            try{
                getInteropClients().getProblemEntryClient().deleteProblemEntry(interopId)
                this.interopDeleteMessage(interopId)
                exists=true
            } catch(Exception e){
                LOGGER.error('Interop client removeMessage resource= '+resourceUri+' interopResourceId='+interopId, e)
            }
        }
        return exists
    }
    /**
     * 
     * @param resourceUri
     * @param interopResourceId
     * @param predicate
     * @param newValue
     */
    public void updateMessageVitals(String resourceUri, String interopResourceId, String predicate, String newValue){
        try{
            getInteropClients().getVitalSignClient().updateVitalSign(interopResourceId, predicate, newValue)
        } catch(Exception e){
            LOGGER.error('Interop client updateMessageVitals, interop resource= '+resourceUri+' interopResourceId='+interopResourceId, e)
        }
    }
    /**
     * This cannot not be used for the dosage and dosage units because these are separate graphs
     * For now, delete message and then add it new....
     * @deprecated
     * @param resourceUri
     * @param interopResourceId
     * @param predicate
     * @param newValue
     */
    public void updateMessageMedication(String resourceUri,String interopResourceId, String predicate, String newValue){
        try{
            getInteropClients().getMedicationClient().updateMedication(interopResourceId, predicate, newValue);
        } catch(Exception e){
            LOGGER.error('Interop client updateMessageMedication, interop resource= '+resourceUri+' interopResourceId='+interopResourceId, e)
        }
    }
    /**
     * 
     * @param resourceUri
     * @param interopResourceId
     * @param predicate
     * @param newValue
     */
    public void updateMessageProblem(String resourceUri,String interopResourceId, String predicate, String newValue){

        try{
            getInteropClients().getProblemEntryClient().updateProblemEntry(interopResourceId, predicate, newValue)
        } catch(Exception e){
            LOGGER.error('Interop client updateMessageProblem, interop resource= '+resourceUri+' interopResourceId='+interopResourceId, e)
        }
    }
    /**
     * 
     * @param referenceId
     * @return
     */
    public def getInteropResourceDynabeanByExternalReferenceId(String referenceId){
        return getInteropClients().getResourceDynabean(referenceId)
    }

    public String getTest(){
        return "1234"
    }
 
    public List importNewMessages(String ownerUri, String phrsClass)  {
        
        return iprocess.importNewMessages( ownerUri,  phrsClass,  true)
    }
    
    public List importNewMessages(String ownerUri, String phrsClass, boolean importMessage)  {
        
        return iprocess.importNewMessages( ownerUri,  phrsClass,  importMessage)
    }

    /**
     *
     * @param resourceURI
     * @throws TripleException
     */
    public void interopDeleteMessage(String resourceUri) {

        if (resourceUri == null) {
            throw new NullPointerException("The resourceURI argument can not be null.");
        }
        try{
            final boolean exists =  getPhrsStoreClient().getGenericTriplestore().exists(resourceUri);
            if (exists) {
                getPhrsStoreClient().getGenericTriplestore().deleteForSubject(resourceUri);
            } else {
                LOGGER.warn("No resource for this {} URI, remove has no effect.", resourceUri);

            }
        } catch(Exception e){
            LOGGER.error(' interop resourceUri= '+resourceUri, e)
        }
    }
    /**
     * Transform to date string yyyyMMddHHmm
     * @param date
     * @param dateTime
     * @return
     *
     */
    public static Date transformDateFromMessage(String dateMessage, Date defaultDate){
        Date theDate
        try{
            if(dateMessage){
                theDate= DateUtil.getFormatedDate( dateMessage)
            }
            if(! theDate){
                theDate = defaultDate ? defaultDate : new Date()
            }
        } catch (Exception e){
            LOGGER.error('transforming date',e)
        }
        return theDate
    }


    /**
     * @deprecated
     */
    public def transformInteropMessage(String givenOwnerUri,String phrsClass, DynaBean bean, String messageResourceUri,PhrsStoreClient client){
        return this.transformInteropMessage(givenOwnerUri,phrsClass,bean,messageResourceUri)
    }
    /**
     * Already know this is new (origin and no resource tag) but must update this record? Get the resourceURI and then update the note with local resourceUri
     * @param givenOwnerUri
     * @param phrsClass
     * @param bean
     * @return
     */
  
    //DynaUtil.getStringProperty(dynaBean, Constants.CREATOR);
    public def transformInteropMessage(String givenOwnerUri,String phrsClass, DynaBean bean, String messageResourceUri){
        return iprocess.transformInteropMessage(givenOwnerUri,phrsClass,bean,messageResourceUri)
    }
   

    public boolean sendPixValidationMessage(ProfileContactInfo contactInfo,boolean pixRevalidatePixId){
        boolean validated=false
        try{
            if(contactInfo && contactInfo.pixIdentifier ){
                //validate and update the pci.pixIdentifier object status. Audit message is sent
                validated= validatePixIdentifier(contactInfo.ownerUri, contactInfo.pixIdentifier, pixRevalidatePixId);//updateInteropActorRegistry, revalidate
            }
        } catch (Exception e){
            LOGGER.error(' '+e)
        }
        return validated
    }
    /**
     * 
     * @param ownerUri
     * @param pixIdentifier - the status field is updated with a validation code
     * @param updateInteropActorRegistry
     * @param revalidate
     */
    public boolean validatePixIdentifier(String ownerUri, PixIdentifier pixIdentifier, boolean pixRevalidatePixId){
        boolean validated=false;
        Map map = [:]
        validated = validatePixIdentifier(ownerUri, pixIdentifier, pixRevalidatePixId,  map);

        return validated;

    }

    public boolean validatePixIdentifier(String ownerUri,PixIdentifier pixIdentifier,boolean pixRevalidatePixId, Map patientInfoMap){

        boolean validated=false;


        if( ! pixRevalidatePixId && (pixIdentifier.status = PhrsConstants.IDENTIFIER_STATUS_VALID)){
            //don't revalidate
            validated=true;

        } else{
            try{
                PixService pixService = new PixService();
                String status= pixService.validatePatientProtocolId(ownerUri, pixIdentifier,patientInfoMap)

            } catch (Exception e){
                LOGGER.error('error to setup pix query pixIdentifier identifier='+pixIdentifier.getIdentifier(), e)
            }
        }
        //evaluate result
        if( pixIdentifier.status  == PhrsConstants.IDENTIFIER_STATUS_VALID) {
            validated=true;
        } else {

        }


        //testing, just return true, although the pixIdentifier has the correct information
        //the flag, supports other logic
        if(ConfigurationService.isAppModePixTest()){
            if(!validated){
                //provide message
                LOGGER.debug('Configuration AppModeTest PixIdentifier did no validate via PIX, but PHRS component registered it anyways into the PHRS core')
            }
            validated=true;
        }


        //register validated protocol ID with the PHRS core component
        if(validated){
            //update the interop registry
            this.registerUser(ownerUri, pixIdentifier.identifier);
            // if(pixIdentifier.getDomain()) this.registerUser(ownerUri, pixIdentifier.getIdentifier()) with domain pixIdentifier.getDomain()

        }


        return validated;
    }
    

    }
