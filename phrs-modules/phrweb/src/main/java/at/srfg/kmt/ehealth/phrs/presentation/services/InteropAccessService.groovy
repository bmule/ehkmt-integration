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


    /**
     * 
     */
    public InteropAccessService(){

        phrsStoreClient = PhrsStoreClient.getInstance()
    }
    /**
     * 
     * @param phrsStoreClient
     */
    public InteropAccessService(PhrsStoreClient phrsStoreClient){

        this.phrsStoreClient = phrsStoreClient
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
     * 
     * @param doseUnits
     * @return
     */
    public String tranformMedicationAdminRoute(String adminRoute,String doseUnits){
        /*String out=adminRoute

        if(!adminRoute){

        switch(doseUnits){
        //TODO Ask GUI question if mg/ml option chosen to determine if injected...
        case '':
        //fall into default
        default:
        out = HL7V3_ORAL_ADMINISTRATION
        break
        }
        } else {
        //default
        out = HL7V3_ORAL_ADMINISTRATION
        }

        return out*/
        return HL7V3_ORAL_ADMINISTRATION
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
                                
                String referenceNote 	= null;
                if(res.resourceUri) referenceNote = REFERENCE_NOTE_PREFIX+res.resourceUri //'' //res.note is by default not sharable
                else referenceNote='error'
                                
                //resourceUri appears only if resource already saved
                if(!res.resourceUri){
                    println("sendMessages  (note) resourceUri is null")
                    LOGGER.debug("sendMessages  (note) resourceUri is null");
                    throw new Exception(" resourceUri not yet set, PHR object was not yet saved?")   
                }
                String theParentId = res.resourceUri
                //update or create message? This does not apply to all objects, some forms had multiple messages with different categoryCodes

                // = findMessageWithReference(ownerUri, resourceUri, phrsClass,categoryCode)

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

                    String name=domain.label ? domain.label : domain.code
                    String freqCode=domain.getFrequencyCode()
                    String dosageQuantity=      domain.getTreatmentMatrix().getDosageQuantity()

                    String dosageValue=		domain.treatmentMatrix.dosage ? domain.treatmentMatrix.dosage.toString() : '0'
                    String doseUnits=		domain.treatmentMatrix.dosageUnits
                    String doseInterval= 	domain.treatmentMatrix.dosageInterval
                    String doseTimeOfDay=	domain.treatmentMatrix.dosageTimeOfDay
                    String adminRoute=		domain.treatmentMatrix.adminRoute
                    adminRoute = tranformMedicationAdminRoute(adminRoute,doseUnits)
                    //there is no categoryCode to filter

                    String interopRef = findMessageWithReference(owner, theParentId, Constants.PHRS_MEDICATION_CLASS,null)

                    //removeMessage(interopRef)

                    if( ! interopRef){
                      
                        messageId  = getInteropClients().getMedicationClient().addMedicationSign(
                            owner,
                            referenceNote,
                            status,
                            dateStringStart,
                            dateStringEnd,
                            doseInterval,//"MyFreqency",
                            adminRoute,
                            dosageQuantity,//not dosageValue error
                            doseUnits,
                            name);// 2 methods with and without drug code
                            //,DRUG_CODE_DEFAULT_PHR);
                        /*
                        domain.prescribedByName
                        domain.reasonCode
                        doseTimeOfDay not used
                         */
                        if(messageId)  {
                            messageIdMap.put(categoryCode,messageId)
                        }
                    } else {
                        //update only changes, 
                        //this.updateMessageProblem(theParentId, interopRef, ., valueCode); Constants.HL7V3_ADMIN_ROUTE
                        this.updateMessageMedication(theParentId, interopRef, HL7V3_STATUS, status);
                        this.updateMessageMedication(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                        this.updateMessageMedication(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);
                        
                        //FIXXME - not needed, but refactor to new buildFrequency from interval and TOD time of day
                        
                        //Never changes. this.updateMessageMedication(theParentId, interopRef, HL7V3_ADMIN_ROUTE, adminRoute);
                        
                       //Drug name change requires code. Dont update. this.updateMessageMedication(theParentId, interopRef, HL7V3_ADMIN_ROUTE, adminRoute);                                           
                       //This needs a URI
                        String newDosageURI = buildMedicationDosage(dosageValue, doseUnits);                                        
                        
         
                        //PHRS_MEDICATION_DOSAGE if updating or HL7V3_DOSAGE if retrieving from EHR data
                        //String newDosageURI = medicationClient.buildDosage(newDossageValue, newDosageUnit);

                        this.updateMessageMedication(theParentId, 
                            interopRef, 
                            PHRS_MEDICATION_DOSAGE, 
                            newDosageURI);
                        
//                        if((domain.getCreatorUri()!=null 
//                            && (domain.getCreatorUri().equalsIgnoreCase(Constants.EXTERN)
//                                 || domain.getCreatorUri().contains('at.srfg.'))
//                            ) || (domain.getOriginStatus()!=null 
//                                && domain.getOriginStatus().equalsIgnoreCase(PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED))   ) {
                                                                                                                   
                        //add this for any changes...if EHR record ignore change to 
                        if( domain.getOriginStatus() != null 
                                && domain.getOriginStatus().equalsIgnoreCase(PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED)   
                            ) {              
                            // dont update    
                        } else {
                             //PHR born resource
                             this.updateMessageMedication(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);
                        }
 
                    }
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
                        //this.updateMessageProblem(theParentId, interopRef, ., valueCode);
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
    public String findMessageWithReference(String ownerUri,String resourceUri,String phrsClass,categoryCode){
        //owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS,categoryCode
        String value = findInteropMessageWithReferenceTag( ownerUri, resourceUri, phrsClass,categoryCode)
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
    /**
     * Take second part after equals sign as an identifier.
     * resourceUri=1234
     * @param note
     * @return null or parsed identifier
     */
        
        
    public static String parseReferenceNote(String note){
        String out=null
        if(note) note = note.trim()
        if(note){
            if(note.contains(REFERENCE_NOTE_PREFIX)){
                //or def parts, then use parts.size()
                String[] parts = note.split(REFERENCE_NOTE_PREFIX);

                if(parts && parts.length > 0){
                    //split on whitespace, take [0]
                    out= parts[1]
                    if(out!=null){
                        out=out.trim()
                        String[] parts2 = out.split(' ');
                        if(parts2 && parts2.length > 0){
                            out = parts2[0];
                        }
                    }
                }
            } else {
                //no parsing
                //out = note    
            }
        }
        return out
    }

    /**
     * Used for list updates e.g. medication list
     * @param ownerUri
     * @param phrsClass
     * @param importMessage
     * @return list of message URIs (not local resource URIs)
     */
    /*
    public List importNewMessages(String ownerUri,String phrsClass, boolean importMessage){

        def list = []
        if(ownerUri && phrsClass){

            final MultiIterable result = new MultiIterable();
            final Map<String, String> queryMap = new HashMap<String, String>();
            try{
                Iterable<String> results=	findInteropMessagesForUser(ownerUri,phrsClass)
                //check each results, has it been tagged?

                //import the message, and also save it back to the Interop Service to tag it and make other listeners aware of it.
                if(importMessage){
                    //transform message to

                    DynaBeanClient dynaBeanClient = getInteropClients().getDynaBeanClient()

                    for(String messageResourceUri:results){
                        try{
                            DynaBean dynaBean = dynaBeanClient.getDynaBean(messageResourceUri)
                                                        
                            boolean isNewMessage=false;
                            Object referenceNote = dynaBean.get(Constants.SKOS_NOTE);
                                                        
                            if(referenceNote){
                                String aboutResourceUri=parseReferenceNote((String)referenceNote)
                                if(!aboutResourceUri){
                                    isNewMessage=true
                                }
                            }
                                                        
                            if(isNewMessage){
                                def repositoryObject = transformInteropMessage( ownerUri, phrsClass,  dynaBean,   messageResourceUri)
                                //
                                if(repositoryObject && importMessage){
                                    list.add(messageResourceUri)
                                    //save transformed resource to local store
                                    //the resourceUri issue
                                    getCommonDao().crudSaveResource(repositoryObject, ownerUri, 'interopservice')
                                    //send message to interop service
                                    //Map map=
                                    //sendMessages(repositoryObject)
                                    phrsStoreClient.updateTriple(messageResourceUri, 
                                        Constants.SKOS_NOTE,REFERENCE_NOTE_PREFIX+messageResourceUri,
                                        false) //no need to update date for the note change
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            LOGGER.error(' message error, interop ownerUri= '+ownerUri+" messageResourceUri="+messageResourceUri, e)
                        }
                    }
                }
            } catch(Exception e){
                LOGGER.error(' interop ownerUri= '+ownerUri, e)
            }
        }
        return list;

    }*/
   public List importNewMessages(String ownerUri, String phrsClass, boolean importMessage) throws Exception {
        
        //addNewMessagesEhr("1", "2", null);
        
        List list = new ArrayList();
        if (ownerUri != null && phrsClass != null) {

            final MultiIterable result = new MultiIterable();
            final Map<String, String> queryMap = new HashMap<String, String>();
            try {
                Iterable<String> results = iaccess.findInteropMessagesForUser(ownerUri, phrsClass);
                //check each results, has it been tagged?

                //import the message, and also save it back to the Interop Service to tag it and make other listeners aware of it.
                if (results != null) {
                    //transform message to

                    DynaBeanClient dynaBeanClient = iaccess.getInteropClients().getDynaBeanClient();

                    for (String messageResourceUri : results) {
                        try {
                            DynaBean dynaBean = dynaBeanClient.getDynaBean(messageResourceUri);

                            //DynaBeanUtil.toString(dynaBean);
                            System.out.println("importNewMessages getDynaClass()= " + dynaBean.getDynaClass().getName());

                            // DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
                            //http://www.icardea.at/phrs#owner
                            Object owner = DynaUtil.getStringProperty(dynaBean,Constants.OWNER);
                            System.out.println("importNewMessages owner= " + DynaUtil.getStringProperty(dynaBean,Constants.OWNER));
                            //System.out.println("importNewMessages drug name= " + DynaUtil.getStringProperty(dynaBean,MED_DRUG_NAME_URI));

                            
                            //String referenceNote= bean.get()
                            boolean isNewMessage = false;
                            Object creator = DynaUtil.getStringProperty(dynaBean,Constants.CREATOR);
                            System.out.println("importNewMessages creator= " + dynaBean.get(Constants.CREATOR));
                            Object referenceNote = null;
                            try {
                                referenceNote = DynaUtil.getStringProperty(dynaBean,Constants.SKOS_NOTE);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // LOGGER.error(' message error, interop ownerUri= '+ownerUri+" messageResourceUri="+messageResourceUri, e)
                            }
                            if (referenceNote != null) {
                                String aboutResourceUri = iaccess.parseReferenceNote((String) referenceNote);

                                if (aboutResourceUri != null && aboutResourceUri.length() != 0) {
                                    isNewMessage = false;
                                } else {
                                    isNewMessage = true;
                                }
                            }

                            if (isNewMessage) {
                                Object repositoryObject = iaccess.transformInteropMessage(ownerUri, phrsClass, dynaBean, messageResourceUri);
                                if (repositoryObject != null) {
                                    list.add(messageResourceUri);
                                }
                                if (importMessage) {
                                    list.add(messageResourceUri);
                                    //save transformed resource to local store
                                    //the resourceUri issue
                                    iaccess.getCommonDao().crudSaveResource(repositoryObject, ownerUri, "interopservice");
                                    //send message to interop service
                                    //Map map=
                                    iaccess.sendMessages(repositoryObject);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            // LOGGER.error(' message error, interop ownerUri= '+ownerUri+" messageResourceUri="+messageResourceUri, e)
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (list != null) {
            System.out.println("importNewMessages list size= " + list.size());
        } else {
            System.out.println("importNewMessages list=NULL " + list);
        }


        return list;

    }
    /**
     * Get the dosage details and put them into a map 
     * @param uri
     * @return
     */
    public  Map getMedicationDosageAttributes(String uri){
        Map map = null
        if(uri){
            DynaBeanClient dbc = getInteropClients().getDynaBeanClient()
            DynaBean bean = dbc.getDynaBean(uri)
            if(bean){
                String dosage = DynaUtil.getStringProperty(dynaBean,Constants.HL7V3_DOSAGE_VALUE) //Constants.HL7V3_DOSAGE
                String units = DynaUtil.getStringProperty(dynaBean,Constants.HL7V3_DOSAGE_UNIT)
                map = new HashMap()

                if(dosage)
                     map.put(Constants.HL7V3_DOSAGE_VALUE, dosage)
                if(units)
                     map.put(Constants.HL7V3_DOSAGE_UNIT, units)
            }

        }
        return map;
    }
    /**
     *
     * @param owerUri
     * @param phrsClass
     * @return
     */
    public Iterable<String> findInteropMessagesForUser(String ownerUri,String phrsClass) {

        Iterable<String> resources;
        if(ownerUri && phrsClass){
            final Map<String, String> queryMap = new HashMap<String, String>();
            try {
                queryMap.put(Constants.RDFS_TYPE, phrsClass);
                queryMap.put(Constants.OWNER, ownerUri);

                resources = getPhrsStoreClient().getGenericTriplestore()
                .getForPredicatesAndValues(queryMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resources;
    }

    /**
     *
     * @param triples
     * @param resourceUri
     * @return
     */
    public String interopFindMessageReferenceTag(Iterable<Triple> triples,String resourceUri){
        int count = 0;
        String foundSubjectUri
        if(triples && resourceUri){

            for (Triple triple : triples) {
                try{
                    final String predicate = triple.getPredicate();
                    final String value = triple.getValue();

                    String subjectUri  = triple.getSubject()
                    /*
                    if (predicate.equals(Constants.OWNER)) {
                    }
                     */
                    if (predicate.equals(Constants.SKOS_NOTE)) {

                        if(true){
                            foundSubjectUri = triple.getSubject()
                            continue
                        }
                    }

                    count++;
                } catch(Exception e){
                    LOGGER.error(' interop resourceUri= '+resourceUri, e)
                }
            }
        }
        return foundSubjectUri
    }
    /**
     *
     * @param ownerUri
     * @param resourceUri
     * @param phrsClass - message class string
     * @param categoryCode
     * @return
     */

    public String findInteropMessageWithReferenceTag(String ownerUri,String resourceUri,String phrsClass,String categoryCode){

        String refId
        try{

            String value = resourceUri
            Iterable<Triple> triples = this.interopFindMesssageTriplesForUserByType( ownerUri, resourceUri, phrsClass, categoryCode)
            refId = this.interopFindMessageReferenceTag(triples, resourceUri)

        } catch(Exception e){
            LOGGER.error('Interop findExternalReferenceInInteropMessageNote, interop ownerUri= '+ownerUri, e)
        }
        return refId
    }
    /**
     *
     * @param userId
     * @param phrsClass - Constants.PHRS_MEDICATION_CLASS
     * @return
     * @throws TripleException
     */

    public Iterable<Triple> interopFindMesssageTriplesForUserByType(String ownerUri,String resourceUri,String phrsClass,categoryCode){
        //String userId, String phrsClass, String categoryCode,String value) throws TripleException {
        final MultiIterable result = new MultiIterable();
        final Map<String, String> queryMap = new HashMap<String, String>();
        try{
            queryMap.put(Constants.RDFS_TYPE, phrsClass);//Constants.PHRS_MEDICATION_CLASS);
            queryMap.put(Constants.OWNER, ownerUri);
            //categoryCode
            queryMap.put(Constants.SKOS_NOTE, resourceUri);
            //filter on code
            if(categoryCode){
                queryMap.put(Constants.HL7V3_VALUE_CODE, categoryCode);
            }


            final Iterable<String> resources =
            getPhrsStoreClient().getGenericTriplestore().getForPredicatesAndValues(queryMap);


            for (String resource : resources) {
                final Iterable<Triple> subject = getPhrsStoreClient().getGenericTriplestore().getForSubject(resource);
                result.addIterable(subject);
            }
        } catch(Exception e){
            LOGGER.error('Interop findExternalReferenceInInteropMessageNote, interop ownerUri= '+ownerUri, e)
        }
        return result;
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

    public String buildMedicationDosage(String dosageValue, String dosageUnits){
        String newDosageURI
        try{
            newDosageURI = getInteropClients().getMedicationClient().buildDosage(dosageValue, dosageUnits);
        }catch (Exception e){
            LOGGER.error('buildMedicationDosage dosageValue='+dosageValue+' dosageUnits='+dosageUnits,e)
        }
        return newDosageURI
    }
    public String buildDrugProduct( String drugName, String drugCode) {
        //MedicationClient medicationClient = PhrsStoreClient.getInstance().getInteropClients().getMedicationClient();
        /*
         * triplestore.persist(subject,
         * "http://www.icardea.at/phrs/hl7V3#manufacturedProduct",
         * buildManufacturedProduct(drugName, drugCode), RESOURCE);
         */
        String newDrugProductUri = null;
        try {
            newDrugProductUri = getInteropClients().getMedicationClient().buildManufacturedProduct(drugName, drugCode);
        } catch (TripleException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDrugProductUri;
        //assertNotNull(newDrugProductUri);
        /*
        try {
        // update 
        medicationClient.updateMedication(
        subjectUri,
        PhrsConstants.MEDICATION_PROPERTY_MANUFACTURED_PRODUCT_URI,
        newDrugProductUri);
        } catch (TripleException e) {
        e.printStackTrace();
        } catch (Exception e) {
        e.printStackTrace();
        }*/
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
//transformInteropMessage(ownerUri, phrsClass, dynaBean, messageResourceUri);
//DynaUtil.getStringProperty(dynaBean, Constants.CREATOR);
    public def transformInteropMessage(String givenOwnerUri,String phrsClass, DynaBean bean, String messageResourceUri){
        def theObject=null
        try{

            if(bean && phrsClass){
                LOGGER.debug('phrsClass='+phrsClass+'bean properties ='+bean.getProperties());

                switch(phrsClass){
                    case Constants.PHRS_MEDICATION_CLASS:
                        String messageOwner=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.OWNER, null)
                        Map props = bean.getProperties()


                        //check ownerUri of message
                        if(messageOwner && (messageOwner != givenOwnerUri)){
                            LOGGER.error('Message ownerUri does not match given givenOwnerUri='+givenOwnerUri+' med.OwnerUri=');

                        } else {

                            // Constants.HL7V3_DATE_START  Constants.HL7V3_DATE_END
                            // Constants.HL7V3_STATUS Constants.HL7V3_FREQUENCY Constants.HL7V3_ADMIN_ROUTE Constants.HL7V3_DOSAGE
                            // Constants.HL7V3_DRUG_NAME Constants.HL7V3_CODE

                            MedicationTreatment med= new MedicationTreatment()
                            med.ownerUri=messageOwner
                            med.creatorUri=DynaUtil.getStringProperty(dynaBean,bean, Constants.CREATOR, med.ownerUri)

                            String stdStatus = DynaUtil.getStringProperty(dynaBean,bean, Constants.HL7V3_STATUS, null)//TODO default

                            med.statusStandard = stdStatus
                            //med.status = 'medicationSummary_medicationStatus_true'
                            //InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_STATUS, null)//TODO default
                            med.status= InteropTermTransformer.transformStandardStatusToLocal(stdStatus,Constants.PHRS_MEDICATION_CLASS)

                            med.code = DynaUtil.getStringProperty(dynaBean,bean, Constants.HL7V3_CODE, null)//TODO is this drug code?
                            //check the origin of this message....
                            med.origin= DynaUtil.getStringProperty(dynaBean,bean, Constants.ORIGIN, PhrsConstants.INTEROP_ORIGIN_DEFAULT_EHR)

                            med.originStatus=PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED

                            med.externalReference=messageResourceUri

                            //do not set resourceUri, but origin should be checked during udpates to interop messages

                            med.reasonCode='http://www.icardea.at/phrs/instances/NoSpecialTreatment'
                            //med.prescribedByName=

                            med.title= DynaUtil.getStringProperty(dynaBean,bean, Constants.HL7V3_DRUG_NAME, null)
                            //No notes, using for resourceUri marker
                            //med.note= DynaUtil.getStringProperty(dynaBean,bean, Constants.SKOS_NOTE, null)

                            med.treatmentMatrix.adminRoute= DynaUtil.getStringProperty(dynaBean,bean, Constants.HL7V3_ADMIN_ROUTE, null)
                            //this is a uri to another dynabean with dosage and units
                            //Constants.HL7V3_DOSAGE PHRS_MEDICATION_DOSAGE

                            String dosageUri= DynaUtil.getStringProperty(dynaBean,bean, Constants.HL7V3_DOSAGE, null)

                            if(dosageUri==null)
                            dosageUri= DynaUtil.getStringProperty(dynaBean,bean, Constants.PHRS_MEDICATION_DOSAGE, null)

                            Map attrs= getMedicationDosageAttributes(dosageUri)
                            if(attrs){
                                try{                
                                    if(attrs.containsKey(Constants.HL7V3_DOSAGE_VALUE)){
                                        String temp=attrs.get(Constants.HL7V3_DOSAGE_VALUE);
                                        if(temp) med.treatmentMatrix.dosage= Double.valueOf(temp)
                                    }

                                } catch (Exception e){
                                    LOGGER.error('dosage number conversion error',e)
                                }
                                if(attrs.containsKey(Constants.HL7V3_DOSAGE_UNIT)){
                                    med.treatmentMatrix.dosageUnits=attrs.get(Constants.HL7V3_DOSAGE_UNIT)
                                }
                            }

                            med.treatmentMatrix.dosageInterval=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_FREQUENCY, 'http://www.icardea.at/phrs/instances/other')
                            //FIXXME
                            med.treatmentMatrix.dosageTimeOfDay='http://www.icardea.at/phrs/instances/NotSpecified'

                            //dates
                            String dateBegin= DynaUtil.getStringProperty(dynaBean,bean, Constants.HL7V3_DATE_START)
                            //set new date if not found
                            Date beginDate = transformDateFromMessage(dateBegin, new Date()) 	//HealthyUtils.formatDate( dateBegin, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)

                            String dateEnd= DynaUtil.getStringProperty(dynaBean,bean, Constants.HL7V3_DATE_END, null)
                            Date endDate = transformDateFromMessage(dateEnd,(Date)null) 		//HealthyUtils.formatDate( dateEnd, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)//transformDate(dateEnd)

                            med.beginDate=beginDate
                            med.endDate=endDate


                            med.createDate=new Date()
                            med.modifyDate=med.createDate
                            med.type=MedicationTreatment.class.toString()
                            theObject = med

                            if(med){
                                System.out.println("medication imported "+med.toString())
                                LOGGER.debug(" medication imported "+med.toString());
                            }
                        }

                        //Date formatedDate = DateUtil.getFormatedDate(dateStr)

                    break
                    
                    default:
                    break
                }
            }
        } catch(Exception e){

            if(bean){
                LOGGER.error('Interop Message transformation failed, ownerUri='+givenOwnerUri+' phrsClass='+phrsClass,e)
            } else {
                LOGGER.error('Interop Message transformation failed, dynabean NULL, ownerUri='+givenOwnerUri+' phrsClass='+phrsClass,e)
            }
        }
        return theObject
    }
    /*
    return HealthyUtils.formatDate( theDate, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)
     */

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
