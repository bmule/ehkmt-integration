package at.srfg.kmt.ehealth.phrs.presentation.services

//import at.srfg.kmt.ehealth.phrs.model.baseform.ObsActivityPhysical


import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.PhrsConstants
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient
import at.srfg.kmt.ehealth.phrs.presentation.utils.DynaUtil
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils
import org.apache.commons.beanutils.DynaBean
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import static at.srfg.kmt.ehealth.phrs.Constants.*
import at.srfg.kmt.ehealth.phrs.model.baseform.*

/**
 * Used by the PhrsStoreClient to send messages whenever an appropriate resource is saved
 *
 */
/*
See Terminology service and
status for actions: action.categories.activity.sport
TAG_ACTIVITIES_OF_DAILY_LIVING_STATUS='http://www.icardea.at/phrs/instances/ActivityStatus'; http://www.icardea.at/phrs/instances/ICanDo http://www.icardea.at/phrs/instances/IRequireAssistance
 */
public class InteropAccessService implements Serializable {
    private final static Logger LOGGER = LoggerFactory.getLogger(InteropAccessService.class);
    public final static String DATE_PATTERN_INTEROP_DATE_TIME = 'yyyyMMddHHmm';
    public final static String REFERENCE_NOTE_PREFIX = 'resourcephr='
    public final static String DRUG_CODE_DEFAULT_PHR = 'MyDrugCode';

    public InteropProcessor iprocess;
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
     */

    /**
     *
     */
    public InteropAccessService() {

        //phrsStoreClient = PhrsStoreClient.getInstance()
        init()
    }
    /**
     * @deprecated
     * @param phrsStoreClient
     */
    public InteropAccessService(PhrsStoreClient phrsStoreClient) {

        init()
    }

    private void init() {
        iprocess = new InteropProcessor();
    }

    public PhrsStoreClient getPhrsStoreClient() {
        return PhrsStoreClient.getInstance()
    }

    public InteropClients getInteropClients() {
        return getPhrsStoreClient().getInteropClients()
    }

    public CommonDao getCommonDao() {
        return getPhrsStoreClient().getCommonDao()
    }

    /**
     *
     * @param ownerUri
     * @param protocolId
     */
    public void registerUser(String ownerUri, String protocolId) {
        //ownerUri = phrId
        getInteropClients().getActorClient().register(ownerUri, protocolId)

    }
    /**
     *
     * @param ownerUri
     * @param protocolId
     * @param protocolNamespace
     */
    public void registerUser(String ownerUri, String protocolId, String protocolNamespace) {

        getInteropClients().getActorClient().register(protocolNamespace, ownerUri, protocolId)
    }
    /**
     *
     * @param ownerUri
     * @param protocolNamespace
     * @return
     */
    public String getProtocolId(String ownerUri, String protocolNamespace) {
        return getCommonDao().getProtocolId(ownerUri);

    }
    /**
     *
     * @param ownerUri
     * @return
     */
    public String getProtocolId(String ownerUri) {
        return getCommonDao().getProtocolId(ownerUri);

    }
    /**
     *
     * @param resource
     * @return
     */
    public Map sendMessages(def resource) {
        sendMessages(resource, null)
    }

    /**
     * ResourceType is the resource class canonical Name
     * Default action for saving resouce
     * @param resource
     * @param attrs
     * @return
     */
    public Map sendMessages(def resource, Map attrs) {
        String resourceType = resource.getClass().getCanonicalName()
        //write list to MessageInterop
        return sendMessages(resource, resourceType, null, attrs)
    }
    /**
     *
     * @param resource
     * @param action - allows finer details for handling other client services e.g. delete
     * @param attrs
     * @return
     */
    public Map sendMessages(def resource, String action, Map attrs) {
        String resourceType = resource.getClass().getCanonicalName()
        //write MessageInterop
        return sendMessages(resource, resourceType, action, attrs)
    }

    /**
     * Transform, Enrich status code
     * Transform any Local resource.status to standard status 
     * @param status
     * @return
     */
    public String transformStatus(String status) {

        return InteropTermTransformer.transformStatus(status)
    }

    /**
     * Transform, Enrich
     * Transform any Local resource.code to a standard code
     * @param code
     * @return
     */
    public String transformCode(String code) {
        String out = code

        return out
    }
    /**
     *
     * @param category
     * @param type
     * @return
     */
    public String transformCategory(String category, String type) {
        String out = category

        return out
    }

    /**
     * Transform to date string yyyyMMddHHmm
     * @param date
     * @param dateTime
     * @return
     *
     */
    public String transformDate(Date date, Date defaultDate) {
        Date theDate = date
        if (!theDate) {
            if (defaultDate) theDate = defaultDate
        }
        if (theDate) {

            return HealthyUtils.formatDate(theDate, (String) null, DATE_PATTERN_INTEROP_DATE_TIME)
        }
        return null
    }

    public static def getDynaBeanPropertyValue(DynaBean bean, String property, String defaultValue) {
        return DynaUtil.getStringProperty(bean, property, defaultValue)

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

    public String createReferenceNote(String resourceUri) {
        return InteropProcessor.createReferenceNote(resourceUri)
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

    //TODO we do not try to update an existing message, that code can be removed.

    public Map sendMessages(def resource, String resourceType, String action, Map attrs) {
        Map messageIdMap = [:]
        LOGGER.debug('sendMessages START resourceType= '+resourceType+' action='+action);
        //also check on transitive field isNewImport. When there are many to process, this is a bottleneck for notification
        if (resource && resource instanceof BasePhrsModel && ! resource.isNewImport()) {

            try {

                //the resulting message URI that is added to the result list
                String messageId

                BasePhrsModel res = (BasePhrsModel) resource
                String owner = res.ownerUri
                //FIXID
                String protocolId = getProtocolId(owner)

                if (protocolId) {
                    // ok
                } else {
                    LOGGER.error("No protocolID yet for User, no send messages for ownerUri=" + owner + " resourceType=" + resourceType);
                    return messageIdMap
                }
                String careProvisionCode = null

                String status = this.transformStatus(res.status)
                //status = status?: null
                status = status != null ? status : Constants.STATUS_COMPELETE//Constants.STATUS_RUNNING;

                String categoryCode = this.transformCategory(res.category, resourceType)
                categoryCode = categoryCode ?: null

                String valueCode = this.transformCode(res.code)
                valueCode = valueCode ?: null

                String dateStringStart = transformDate(res.beginDate, res.endDate)
                dateStringStart = dateStringStart ?: transformDate(new Date(), new Date())

                String dateStringEnd = transformDate(res.endDate, (Date) null)
                dateStringEnd = dateStringEnd ?: InteropProcessor.DEFAULT_DATE_STRING // blank

                //res.note is by default not sharable
                /**
                 * Use the note to tag this record. 
                 * Be sure to write note to multiple messages such as Vital signs with separate messages for Body weight, height, sys, diastolic
                 *
                 */
                //note cannot be null

                String referenceNote = createReferenceNote(res.resourceUri) //'' //res.note is by default not sharable

                //resourceUri appears only if resource already saved
                if (!res.resourceUri) {

                    LOGGER.debug('sendMessages  (note) resourceUri is null');
                    throw new Exception(' resourceUri not yet set, PHR object was not yet saved?')
                }
                String theParentId = res.resourceUri

                boolean notifySubscribers = false;
                LOGGER.debug('sendMessages SWITCH resourceType= '+resourceType+' protocolId='+protocolId+' status='+status+' dateStart='+dateStringStart);
                switch (resourceType) {

                    case ProfileRisk.class.getCanonicalName():
                        careProvisionCode= InteropProcessor.CARE_PROVISION_CODE_MEDCCAT

                        //Risks reported under Problem
                        //ProfileRisk domain=(ProfileRisk)resource
                        if (categoryCode != PhrsConstants.HL7V3_CODE_CATEGORY_RISK) {
                            categoryCode = PhrsConstants.HL7V3_CODE_CATEGORY_RISK  //TODO logger, should be category, but always HL7V3_SYMPTOM for this object type
                        }

                        //No need to update, write again
                        String interopRef = null // findMessageWithReference(owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS, categoryCode)

                        String actionLabel = interopRef == null ? 'CREATE' : 'UPDATE ref=' + interopRef;
                        LOGGER.debug(' Send Interop Message' + actionLabel
                                + ' resourceType ' + resourceType + ' owner ' + owner + ' protocolId ' + protocolId + ' refnote '
                                + ' status ' + status + ' dateStart ' + dateStringStart + ' dateEnd ' + dateStringEnd);

                        if (!interopRef) {

                            messageId = getInteropClients().getProblemEntryClient()
                                    .addProblemEntry(
                                    protocolId,//owner,
                                    categoryCode,// TODO more specific detail code part of finding
                                    status,
                                    dateStringStart,
                                    dateStringEnd,
                                    referenceNote,
                                    valueCode);

                            if (messageId) {
                                messageIdMap.put(categoryCode, messageId)
                            }
                        } else {
                            //iprocess.updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_STATUS, status);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);

                        }
                        //notify subscribers about changes
                        notifySubscribers = true

                        break
//Constants.HL7V3_COMPILANT
//Constants.HL7V3_FINDING
//status
//Constants.STATUS_COMPELETE
//Constants.STATUS_ACTIVE
                    case ProfileActivityDailyLiving.class.getCanonicalName():
                        careProvisionCode= InteropProcessor.CARE_PROVISION_CODE_MEDCCAT

                        categoryCode =  Constants.HL7V3_FINDING
                            //PhrsConstants.HL7V3_CODE_CATEGORY_ADL This is finding...

                        String interopRef = null  //findMessageWithReference(owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS, categoryCode)

                        String actionLabel = interopRef == null ? 'CREATE' : 'UPDATE ref=' + interopRef;
                        LOGGER.debug(' Send Interop Message' + actionLabel
                                + ' resourceType ' + resourceType + ' owner ' + owner + ' protocolId ' + protocolId + ' refnote '
                                + ' status ' + status + ' dateStart ' + dateStringStart + ' dateEnd ' + dateStringEnd);

                        if (!interopRef) {
                            // TODO more specific detail code part of finding
                            messageId = getInteropClients().getProblemEntryClient()
                                    .addProblemEntry(
                                    protocolId,//owner,
                                    categoryCode,
                                    status,
                                    dateStringStart,
                                    dateStringEnd,
                                    referenceNote,
                                    valueCode);

                            if (messageId) {
                                messageIdMap.put(categoryCode, messageId)
                            }
                        } else {
                            //iprocess.updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_STATUS, status);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);

                        }
                        //notify subscribers about changes
                        notifySubscribers = true

                        break

                    case ActionPlanEvent.class.getCanonicalName():
                        careProvisionCode= InteropProcessor.CARE_PROVISION_CODE_MEDCCAT

                        //send message only for the sports identifier resource.code= action.categories.activity.sport
                        if (valueCode == InteropTermTransformer.CODE_WATCH_SPORT) {
                            categoryCode = PhrsConstants.HL7V3_CODE_CATEGORY_PHYS_ACTIVITY //TODO logger, should be category, but always HL7V3_SYMPTOM for this object type

                            //No need to update, write again
                            String interopRef = null // findMessageWithReference(owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS, categoryCode)

                            String actionLabel = interopRef == null ? 'CREATE' : 'UPDATE ref=' + interopRef;
                            LOGGER.debug(' Send Interop Message' + actionLabel
                                    + ' resourceType ' + resourceType + ' owner ' + owner + ' protocolId ' + protocolId + ' refnote '
                                    + ' status ' + status + ' dateStart ' + dateStringStart + ' dateEnd ' + dateStringEnd);

                            if (!interopRef) {
                                messageId = getInteropClients().getProblemEntryClient()
                                        .addProblemEntry(
                                        protocolId,//owner,
                                        categoryCode,
                                        status,
                                        dateStringStart,
                                        dateStringEnd,
                                        referenceNote,
                                        valueCode);

                                if (messageId) {
                                    messageIdMap.put(categoryCode, messageId)
                                }

                            } else {
                                //iprocess.updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);
                                iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_STATUS, status);
                                iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                                iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);
                            }
                        }
                        //notify subscribers about changes
                        notifySubscribers = true

                        break

                    case MedicationTreatment.class.getCanonicalName():

                        MedicationTreatment domain = (MedicationTreatment) resource
                        LOGGER.debug('sendMessages prepare sendMedicationMessage MedicationTreatment resourceType= '+resourceType+' protocolId='+protocolId+' status='+status+' dateStart='+dateStringStart);

                        messageIdMap = iprocess.sendMedicationMessage(domain);
                        //notify done already in medication
                        break

                    //case ObsActivityPhysical.class.getCanonicalName():

                    //    break

                    case ObsProblem.class.getCanonicalName():

                        careProvisionCode= InteropProcessor.CARE_PROVISION_CODE_MEDCCAT

                        ObsProblem domain = (ObsProblem) resource

                        //if (categoryCode != Constants.HL7V3_SYMPTOM) {
                            categoryCode = Constants.HL7V3_COMPILANT
                                //Constants.HL7V3_SYMPTOM
                            //TODO logger, should be category, but always HL7V3_SYMPTOM for this object type
                            //HL7V3_FINDING
                        //}

                        //No need to update, write again
                        String interopRef = null // findMessageWithReference(owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS, categoryCode)

                        String actionLabel = interopRef == null ? 'CREATE' : 'UPDATE ref=' + interopRef;
                        LOGGER.debug(' Send Interop Message' + actionLabel
                                + ' resourceType ' + resourceType + ' owner ' + owner + ' protocolId ' + protocolId + ' refnote '
                                + ' status ' + status + ' dateStart ' + dateStringStart + ' dateEnd ' + dateStringEnd);

                        if (!interopRef) {
                            messageId = getInteropClients().getProblemEntryClient()
                                    .addProblemEntry(
                                    protocolId,//owner,
                                    categoryCode,
                                    status,
                                    dateStringStart,
                                    dateStringEnd,
                                    referenceNote,
                                    valueCode);

                            if (messageId) {
                                messageIdMap.put(categoryCode, messageId)
                            }
                        } else {
                            //iprocess.updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_STATUS, status);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);
                        }
                        //notify subscribers about changes
                        notifySubscribers = true
                        break

                    case ObsVitalsBloodPressure.class.getCanonicalName():

                        careProvisionCode= InteropProcessor.CARE_PROVISION_CODE_COBSCAT
                        ObsVitalsBloodPressure domain = (ObsVitalsBloodPressure) resource
                        //not using category code, instead we split form into a few pieces
                        categoryCode = Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE

                        String value = domain.getSystolic() ? domain.getSystolic().toString() : '0'

                        String interopRef = null //findMessageWithReference(owner, theParentId, Constants.PHRS_VITAL_SIGN_CLASS, categoryCode)

                        String actionLabel = interopRef == null ? 'CREATE' : 'UPDATE ref=' + interopRef;
                        LOGGER.debug(' Send Interop Message' + actionLabel
                                + ' resourceType ' + resourceType + ' owner ' + owner + ' protocolId ' + protocolId + ' refnote '
                                + ' status ' + status + ' dateStart ' + dateStringStart + ' dateEnd ' + dateStringEnd);

                        if (!interopRef) {
                            messageId = getInteropClients().getVitalSignClient().addVitalSign(
                                    protocolId,//owner,
                                    Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE,
                                    referenceNote,
                                    dateStringStart,
                                    status,
                                    value,
                                    Constants.MM_HG);

                            if (messageId) {
                                messageIdMap.put(categoryCode, messageId)
                            }
                        } else {
                            //iprocess.updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_VALUE, value);
                        }

                        categoryCode = Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE
                        value = domain.getDiastolic() ? domain.getDiastolic().toString() : '0'

                        //No need to update, write again
                        String interopRef_2 = null
                        interopRef_2 = null //findMessageWithReference(owner, theParentId, Constants.PHRS_VITAL_SIGN_CLASS, categoryCode)

                        if (!interopRef_2) {
                            messageId = getInteropClients().getVitalSignClient().addVitalSign(
                                    protocolId,//owner,
                                    categoryCode,
                                    referenceNote,
                                    dateStringStart,
                                    status,
                                    value,
                                    Constants.MM_HG);

                            if (messageId) {
                                messageIdMap.put(categoryCode, messageId)
                            }
                        } else {
                            //iprocess.updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);
                            iprocess.updateInteropStatement(theParentId, interopRef_2, HL7V3_START_DATE, dateStringStart);
                            iprocess.updateInteropStatement(theParentId, interopRef_2, HL7V3_VALUE, value);

                        }
                        //notify subscribers about changes
                        notifySubscribers = true
                        break

                    case ObsVitalsBodyWeight.class.getCanonicalName():

                        careProvisionCode= InteropProcessor.CARE_PROVISION_CODE_COBSCAT

                        ObsVitalsBodyWeight domain = (ObsVitalsBodyWeight) resource
                        categoryCode = Constants.ICARDEA_INSTANCE_BODY_WEIGHT
                        String value = domain.getBodyWeight() ? domain.getBodyWeight().toString() : '0'

                        //No need to update, write again
                        String interopRef = null //findMessageWithReference(owner, theParentId, Constants.PHRS_VITAL_SIGN_CLASS, categoryCode)

                        String actionLabel = interopRef == null ? 'CREATE' : 'UPDATE ref=' + interopRef;
                        LOGGER.debug(' Send Interop Message' + actionLabel
                                + ' resourceType ' + resourceType + ' owner ' + owner + ' protocolId ' + protocolId + ' refnote '
                                + ' status ' + status + ' dateStart ' + dateStringStart + ' dateEnd ' + dateStringEnd);

                        if (!interopRef) {
                            messageId = getInteropClients().getVitalSignClient().addVitalSign(
                                    protocolId,//owner,
                                    categoryCode,
                                    referenceNote,
                                    dateStringStart,
                                    status,
                                    value,
                                    Constants.KILOGRAM);

                            if (messageId) messageIdMap.put(categoryCode, messageId)
                        } else {
                            //iprocess.updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);

                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_STATUS, status);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_END_DATE, value);

                        }

                        categoryCode = Constants.ICARDEA_INSTANCE_BODY_HEIGHT
                        value = domain.getBodyHeight() ? domain.getBodyHeight().toString() : '0'

                        //No need to update, write again
                        interopRef = null //findMessageWithReference(owner, theParentId, Constants.PHRS_VITAL_SIGN_CLASS, categoryCode)


                        LOGGER.debug(' Send Interop Message' + actionLabel
                                + ' resourceType ' + resourceType + ' owner ' + owner + ' protocolId ' + protocolId + ' refnote '
                                + ' status ' + status + ' dateStart ' + dateStringStart + ' dateEnd ' + dateStringEnd);

                        if (interopRef) {
                            messageId = getInteropClients().getVitalSignClient()
                                    .addVitalSign(
                                    protocolId,//owner,
                                    categoryCode,
                                    referenceNote,
                                    dateStringStart,
                                    Constants.STATUS_COMPELETE,//use instead of current status
                                    value,
                                    Constants.CENTIMETER);

                            if (messageId) {
                                messageIdMap.put(categoryCode, messageId)
                            }
                        } else {
                            //update
                            //iprocess.updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
                            iprocess.updateInteropStatement(theParentId, interopRef, HL7V3_VALUE, value);

                        }
                        //notify subscribers about changes
                        notifySubscribers = true
                        break

//                    case [
//                        ProfileContactInfo.class.getCanonicalName(),
//                        ProfileUserContactInfo.class.getCanonicalName(),
//                        ProfileMedicalContactInfo.class.getCanonicalName()
//                    ]:
//                    //do nothing
//                    break

                    default:
                        break
                }

                if (notifySubscribers) {
                    LOGGER.debug('Sent interop message, Prepare to notify for owner=' + owner + ' protocolId=' + protocolId + " careProvisionCode " + careProvisionCode);
                    getInteropClients().notifyInteropMessageSubscribersByProtocolId(careProvisionCode,protocolId);//owner,
                }

            } catch (TripleException e) {
                LOGGER.error('Interop client error sending message for resource= ' + resourceType, e)


            } catch (Exception e) {
                LOGGER.error('Interop client error sending message for resource= ' + resourceType, e)

            } catch (java.lang.Error e) {
                LOGGER.error('sesame throws errors not exception...' + resourceType, e);

            }

        } else {

            if( resource && (resource instanceof BasePhrsModel)){
                LOGGER.debug('Interop message Did not process, resource is new import? '+resource.isNewImport() );
            } else {
                LOGGER.debug('Interop message Did not process, resource is null or not BaseModel' );
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
    public String findMessageWithReference(String ownerUri, String resourceUri, String phrsClass, String categoryCode) {

        String value = iprocess.findMessageWithReference(ownerUri, resourceUri, phrsClass, categoryCode)
        return value
    }

    /**
     *
     * @param resourceUri
     * @param interopId
     * @return
     */
    public boolean removeMessage(String resourceUri, String interopId) {
        boolean exists = false
        if (interopId) {
            try {
                getInteropClients().getProblemEntryClient().deleteProblemEntry(interopId)
                this.interopDeleteMessage(interopId)
                exists = true
            } catch (Exception e) {
                LOGGER.error('Interop client removeMessage resource= ' + resourceUri + ' interopResourceId=' + interopId, e)
            }
        }
        return exists
    }

    /**
     *
     This cannot not be used for the dosage and dosage units because these are separate graphs
     * For now, delete message and then add it new....
     */
    public void updateMessageProblem(String resourceUri, String interopResourceId, String predicate, String newValue) {

        try {
            getInteropClients().getProblemEntryClient().updateProblemEntry(interopResourceId, predicate, newValue)
        } catch (Exception e) {
            LOGGER.error('Interop client updateMessageProblem, interop resource= ' + resourceUri + ' interopResourceId=' + interopResourceId, e)
        }
    }
    /**
     *
     * @param referenceId
     * @return
     */
    public def getInteropResourceDynabeanByExternalReferenceId(String referenceId) {
        return getInteropClients().getResourceDynabean(referenceId)
    }

    public String getTest() {
        return '1234'
    }

    public List importNewMessages(String ownerUri, String phrsClass) {

        return iprocess.importNewMessages(ownerUri, phrsClass, true)
    }

    public List importNewMessages(String ownerUri, String phrsClass, boolean importMessage) {

        return iprocess.importNewMessages(ownerUri, phrsClass, importMessage)
    }

    /**
     *
     * @param resourceURI
     * @throws TripleException
     */
    public void interopDeleteMessage(String resourceUri) {

        if (resourceUri == null) {
            throw new NullPointerException('The resourceURI argument can not be null.');
        }
        try {
            final boolean exists = getPhrsStoreClient().getGenericTriplestore().exists(resourceUri);
            if (exists) {
                getPhrsStoreClient().getGenericTriplestore().deleteForSubject(resourceUri);
            } else {
                LOGGER.warn('No resource for this {} URI, remove has no effect.', resourceUri);

            }
        } catch (Exception e) {
            LOGGER.error(' interop resourceUri= ' + resourceUri, e)
        }
    }


}
