package at.srfg.kmt.ehealth.phrs.presentation.services;

import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.PhrsConstants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.model.baseform.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.client.CommonDao;
import at.srfg.kmt.ehealth.phrs.persistence.client.InteropClients;
import at.srfg.kmt.ehealth.phrs.persistence.client.PhrsStoreClient;
import at.srfg.kmt.ehealth.phrs.presentation.utils.DynaUtil;
import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils;
import org.apache.commons.beanutils.DynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InteropProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InteropProcessor.class.getName());
    public final static String DATE_PATTERN_INTEROP_DATE_TIME = "yyyyMMddHHmm";
    public final static String REFERENCE_NOTE_PREFIX = "resourcephr=";
    public static final String USER_PROTOCOL_ID = Constants.PROTOCOL_ID_PIX_TEST_PATIENT;//Constants.PROTOCOL_ID_UNIT_TEST;
    public static final String PROTOCOL_ID_NAMESPACE = Constants.ICARDEA_DOMAIN_PIX_OID;

    public static final String CARE_PROVISION_CODE_MEDLIST = "MEDLIST";
    public static final String CARE_PROVISION_CODE_MEDCCAT = "MEDCCAT";
    public static final String CARE_PROVISION_CODE_COBSCAT = "COBSCAT";

    public final static String UNKNOWN_DRUG_CODE = "";//unknown  is normally blank TODO find actual code for unknown or undefined?
    public final static String DEFAULT_DATE_STRING = "";
    public final static String DEFAULT_DRUG_LABEL = "UNKNOWN";
    private boolean printDynabean = false;


    public InteropProcessor() {
        initInterop();
    }

    /**
     * @param phrsClient
     * @deprecated
     */
    public InteropProcessor(PhrsStoreClient phrsClient) {

        initInterop();
    }

    protected void initInterop() {
        //setupTest();

    }


    // +++++++++++++++ start helpers
    public CommonDao getCommonDao() {
        return getPhrsStoreClient().getCommonDao();
    }

    public InteropClients getInteropClients() {
        return getPhrsStoreClient().getInteropClients();
    }

    /**
     * @param repositoryObject
     */
    // TODO Remove after deprecating InteropAccessService
    public void sendMessages(Object repositoryObject) {
        InteropAccessService iaccess = new InteropAccessService();
        iaccess.sendMessages(repositoryObject);
    }


    public PhrsStoreClient getPhrsStoreClient() {
        return PhrsStoreClient.getInstance();
    }

    public static int showList(String title, List<String> results) {
        int count = 0;
        System.out.println("Title:" + title);
        for (String result : results) {
            count++;
            System.out.println("item (" + count + ") uri=" + result);
        }
        return count;
    }

    public static int showMap(String title, Map<String, String> map) {
        int count = 0;
        System.out.println("Title:" + title);
        for (String key : map.keySet()) {
            count++;
            System.out.println("item (" + count + ") thing=" + key + " val=" + map.get(key));
        }
        return count;
    }

    public static String parseReferenceNote(String note) {
        String out = null;
        if (note != null) {
            note = note.trim();
        }
        if (note != null) {
            if (note.contains(REFERENCE_NOTE_PREFIX)) {
                //or def parts, then use parts.size()
                String[] parts = note.split(REFERENCE_NOTE_PREFIX);

                if (parts != null && parts.length > 0) {
                    //split on whitespace, take [0]
                    out = parts[1];
                    if (out != null) {
                        out = out.trim();
                        String[] parts2 = out.split(" ");
                        if (parts2 != null && parts2.length > 0) {
                            out = parts2[0];
                        }
                    }
                }
            } else {
                //no parsing
                //out = note    
            }
        }
        return out;
    }
// +++++++++++++++ end helpers

    /**
     * @param ownerUri
     * @param phrsClass
     * @param onlyNewMessages - true collect only new messages; false - all
     *                        message, known and unknown to phrweb portal
     * @return
     */
    public Set<DynaBean> findInteropMessagesForUser(String ownerUri, String phrsClass, boolean onlyNewMessages) {
        Set<DynaBean> beans = new HashSet<DynaBean>();

        if (ownerUri != null && phrsClass != null) {
            try {
                //FIXID
                String protocolId = getProtocolId(ownerUri);
                if (protocolId != null && !protocolId.isEmpty()) {
                    // ok
                } else {
                    LOGGER.error("No protocolID yet for User, no send messages for ownerUri=" + ownerUri + " phrClass=" + phrsClass);
                    return beans;
                }

                final Map<String, String> queryMap = new HashMap<String, String>();

                queryMap.put(Constants.RDFS_TYPE, phrsClass);
                //FIXID
                queryMap.put(Constants.OWNER, protocolId);
                //queryMap.put(Constants.OWNER, ownerUri);

                Iterable<String> resources = getPhrsStoreClient().getGenericTriplestore().getForPredicatesAndValues(queryMap);

                //check each results, has it been tagged?

                //import the message, and also save it back to the Interop Service to tag it and make other listeners aware of it.
                if (resources != null) {
                    //transform message to


                    DynaBeanClient dynaBeanClient = getInteropClients().getDynaBeanClient();

                    for (String messageResourceUri : resources) {
                        try {
                            String referenceNote = null;
                            DynaBean dynaBean = dynaBeanClient.getDynaBean(messageResourceUri);
                            try {
                                referenceNote = DynaUtil.getStringProperty(dynaBean, Constants.SKOS_NOTE);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);

                            }
                            if (referenceNote != null) {
                                String aboutResourceUri = parseReferenceNote((String) referenceNote);

                                if (aboutResourceUri != null && aboutResourceUri.length() != 0) {
                                    //existing, already imported
                                    if (!onlyNewMessages) {
                                        beans.add(dynaBean);
                                    }
                                } else {
                                    //new
                                    beans.add(dynaBean);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);

                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return beans;

    }

    //private MedicationClient medicationClient   = phrsClient.getInteropClients().getMedicationClient();
    public String buildNullFrequency(MedicationClient medicationClient) {
        String value = null;
        try {
            value = medicationClient.buildNullFrequency();
        } catch (Exception e) {
        }
        if (value == null) {
            value = null;
        }

        return value;
    }

    public static Double convertDouble(String value) {

        Double newValue = null;
        if (value != null && !value.isEmpty()) {
            try {
                newValue = Double.parseDouble(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        if (newValue == null) {
            newValue = 0d;
        }
        return newValue;
    }

    public static Integer convertInteger(String value) {

        Integer newValue = null;
        if (value != null && !value.isEmpty()) {
            try {
                newValue = Integer.valueOf(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        if (newValue == null) {
            newValue = 0;
        }
        return newValue;
    }

    public static String convertDouble(Double value) {

        String newValue = null;
        if (value != null) {
            try {
                newValue = Double.toString(value);
            } catch (Exception e) {
            }
        }
        if (newValue == null) {
            newValue = "0";
        }
        return newValue;
    }

    public static String convertInteger(Integer value) {

        String newValue = null;
        if (value != null) {
            try {
                newValue = Integer.toString(value);
            } catch (Exception e) {
            }
        }
        if (newValue == null) {
            newValue = "0";
        }
        return newValue;
    }

    public String getProtocolId(String ownerUri) {
        return getCommonDao().getProtocolId(ownerUri);
    }

    public Map<String, String> sendMedicationMessage(MedicationTreatment domain) {
        //MedicationTreatment domain = (MedicationTreatment) resource;

        //res.note is by default not sharable
        /**
         * Use the note to tag this record. Be sure to write note to multiple
         * messages such as Vital signs with separate messages for Body weight,
         * height, sys, diastolic
         *
         */
        //create reference to phrweb object for note

        //FIXID
        Map<String, String> messageIdMap = new HashMap<String, String>();
        BasePhrsModel res = (BasePhrsModel) domain;

        String owner = res.getOwnerUri();

        String protocolId = getProtocolId(owner);
        LOGGER.debug("sendMedicationMessage  START owner= " + owner + " protocolId=" + protocolId + " beginDate=" + res.getBeginDate() + " endDate=" + res.getEndDate());

        if (protocolId != null && !protocolId.isEmpty()) {
            // ok
        } else {
            LOGGER.error("No protocolID yet for User, no send messages for ownerUri=" + owner);
            return messageIdMap;
        }

        String messageId = null;


        String resourceType = domain.getClass().getCanonicalName();


        String status = transformStatus(res.getStatus());
        status = status != null && status.length() > 0 ? status : Constants.STATUS_RUNNING;

        //String categoryCode = transformCategory(res.getCategory(), resourceType);
        //categoryCode = categoryCode != null ? categoryCode : null;

        //String valueCode = transformCode(res.getCode());
        //valueCode = valueCode != null ? valueCode :  xx;

        String dateStringStart = transformDate(res.getBeginDate(), res.getEndDate());
        dateStringStart = dateStringStart != null ? dateStringStart : null;

        String dateStringEnd = transformDate(res.getEndDate(), null);
        dateStringEnd = dateStringEnd != null ? dateStringEnd : InteropProcessor.DEFAULT_DATE_STRING;//blank

        String theParentId = res.getResourceUri();

        try {


            String name = domain.getLabel() != null ? domain.getLabel() : InteropProcessor.DEFAULT_DRUG_LABEL;

            String freqCode = domain.getFrequencyCode();
            //String dosageValue = domain.getTreatmentMatrix().getDosage() != null ? domain.getTreatmentMatrix().getDosage().toString() : "0";
            //String dosageQuantity = domain.getTreatmentMatrix().getDosageQuantity();

            String dosageValue = domain.getTreatmentMatrix().getDosage() != null ? convertDouble(domain.getTreatmentMatrix().getDosage()) : "0";

            String doseUnits = domain.getTreatmentMatrix().getDosageUnits();

            String doseInterval = domain.getTreatmentMatrix().getDosageInterval();
            String doseTimeOfDay = domain.getTreatmentMatrix().getDosageTimeOfDay();

            String adminRoute = domain.getTreatmentMatrix().getAdminRoute();
            adminRoute = tranformMedicationAdminRoute(adminRoute, doseUnits);

            MedicationClient medicationclient = getInteropClients().getMedicationClient();
            String productCode = domain.getProductCode();
            //assign default
            if (productCode != null && !productCode.isEmpty()) {
                //
            } else {
                productCode = InteropProcessor.UNKNOWN_DRUG_CODE;
            }
            String interopRef = null;
            //create new message each time rather than update.
            //findMessageWithReference(owner, theParentId, Constants.PHRS_MEDICATION_CLASS, null);

            String actionLabel = interopRef == null ? "CREATE" : "UPDATE ref=" + interopRef;

            LOGGER.debug(" Send Interop Message" + actionLabel + " resourceType " + resourceType + " owner " + owner + " refnote " + " status " + status + " dateStart " + dateStringStart
                    + " dateEnd " + dateStringEnd + " doseValue " + dosageValue + " doseUnits " + doseUnits + " drugName " + name + " productCode " + productCode);

            if (interopRef == null) {
                String referenceNote = createReferenceNote(domain.getResourceUri());

                LOGGER.debug("Interop referenceNote " + referenceNote);

                if (productCode != null) {

                    messageId = medicationclient.addMedicationSign(
                            protocolId,//FIXID owner,
                            referenceNote,
                            status,
                            dateStringStart,
                            dateStringEnd,//dateStringEnd,
                            medicationclient.buildNullFrequency(),//doseInterval, frequency,
                            adminRoute,
                            dosageValue,
                            doseUnits,
                            name,
                            productCode);
                } else {
                    messageId = medicationclient.addMedicationSign(
                            protocolId,//FIXID owner,
                            referenceNote,
                            status,
                            dateStringStart,
                            dateStringEnd,//dateStringEnd,
                            medicationclient.buildNullFrequency(),//doseInterval, frequency,
                            adminRoute,
                            dosageValue,
                            doseUnits,
                            name);
                }

                if (messageId != null && messageId.length() > 0) {

                    messageIdMap.put("default", messageId);
                }
            } else {


                //update
                updateMessageMedication(theParentId, interopRef, Constants.HL7V3_STATUS, status);
                updateMessageMedication(theParentId, interopRef, Constants.HL7V3_START_DATE, dateStringStart);
                updateMessageMedication(theParentId, interopRef, Constants.HL7V3_END_DATE, dateStringEnd);

                //FIXXME - not needed, but refactor to new buildFrequency from interval and TOD time of day

                //Never changes. updateMessageMedication(theParentId, interopRef, HL7V3_ADMIN_ROUTE, adminRoute);

                //Drug name change requires code. Dont update. updateMessageMedication(theParentId, interopRef, HL7V3_ADMIN_ROUTE, adminRoute);                                           
                //This needs a URI
                String newDosageURI = buildMedicationDosage(medicationclient, dosageValue, doseUnits);


                //PHRS_MEDICATION_DOSAGE if updating or HL7V3_DOSAGE if retrieving from EHR data
                //String newDosageURI = medicationClient.buildDosage(newDossageValue, newDosageUnit);

                updateMessageMedication(theParentId,
                        interopRef,
                        Constants.PHRS_MEDICATION_DOSAGE,
                        newDosageURI);

//                        if((domain.getCreatorUri()!=null 
//                            && (domain.getCreatorUri().equalsIgnoreCase(Constants.EXTERN)
//                                 || domain.getCreatorUri().contains("at.srfg."))
//                            ) || (domain.getOriginStatus()!=null 
//                                && domain.getOriginStatus().equalsIgnoreCase(PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED))   ) {

                //add this for any changes...if EHR record ignore change to 
                if (domain.getOriginStatus() != null
                        && domain.getOriginStatus().equalsIgnoreCase(PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED)) {
                    // dont update    
                } else {
                    //PHR born resource
                    updateMessageMedication(theParentId, interopRef, Constants.HL7V3_END_DATE, dateStringEnd);
                }
            }
            //Notify all,this is a shotgun notification for all care provision codes
            //Issue, if the protocolId is not yet defined, then we try to notify
            LOGGER.debug("Sending interop message, Prepare to notify for owner=" + owner + " CareProvisionCode" + CARE_PROVISION_CODE_MEDLIST);

            getInteropClients().notifyInteropMessageSubscribersByProtocolId(CARE_PROVISION_CODE_MEDLIST, protocolId);
            //          getInteropClients().notifyInteropMessageSubscribersByProtocolId(protocolId, resourceType);

        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);

        } catch (TripleException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return messageIdMap;
    }

    /**
     * @param ownerUri
     * @param resourceUri
     * @param categoryCode - this can be null, it is used to select a specific
     *                     code within a phrsClass e.g. ObservationClass includes resources by code
     *                     for Body height, symptom
     * @param phrsClass    - The Phrs class Constants.PHRS_MEDICATION_CLASS,
     *                     PHRS_VITAL_SIGN_CLASS, PHRS_OBSERVATION_ENTRY_CLASS
     * @return
     */
    public String findMessageWithReference(String ownerUri, String resourceUri, String phrsClass, String categoryCode) {
        //owner, theParentId, Constants.PHRS_OBSERVATION_ENTRY_CLASS,categoryCode
        //String value = findInteropMessageWithReferenceTag(ownerUri, resourceUri, phrsClass, categoryCode);
        return findMessageWithReference(ownerUri, resourceUri, phrsClass);

    }

    /**
     * @param ownerUri    is used to find ProtocolId
     * @param resourceUri
     * @param phrsClass
     * @return
     */
    public String findMessageWithReference(String ownerUri, String resourceUri, String phrsClass) {


        String result = null;
        if (ownerUri != null && phrsClass != null && resourceUri != null) {

            try {
                Set<DynaBean> results = findInteropMessagesForUser(ownerUri, phrsClass, false);//check false old and new messages
                if (results != null) {

                    for (DynaBean dynaBean : results) {
                        try {
                            String messageUri = dynaBean.getDynaClass().getName();
                            boolean match = false;

                            String note = DynaUtil.getStringProperty(dynaBean, Constants.SKOS_NOTE);
                            if (note != null) {
                                String foundRef = parseReferenceNote(note);
                                if (foundRef != null && foundRef.equals(resourceUri)) {
                                    match = true;
                                }
                            }
                            if (match) {
                                //list.add(messageUri);
                                result = messageUri;
                                break;
                                //queryResultBeans.add(dynaBean);
                            }

                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            // LOGGER.error(" message error, interop ownerUri= "+ownerUri+" messageResourceUri="+messageResourceUri, e)
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return result;

    }

    public static String createReferenceNote(String resourceUri) {
        String referenceNote = null;
        if (resourceUri != null) {
            referenceNote = InteropAccessService.REFERENCE_NOTE_PREFIX + resourceUri; //"" //res.note is by default not sharable
        } else {
            referenceNote = "error";
        }
        return referenceNote;
    }

    /*
   public void createManufactureName(String subjectUri, String drugName, String drugCode) {
       MedicationClient medicationClient = PhrsStoreClient.getInstance().getInteropClients().getMedicationClient();

       String newDrugProductUri = null;
       try {
           newDrugProductUri = medicationClient.buildManufacturedProduct(drugName, drugCode);
       } catch (TripleException e) {
            LOGGER.error(e.getMessage(),e);
       } catch (Exception e) {
           LOGGER.error(e.getMessage(),e);
       }


       try {
           // update
           medicationClient.updateMedication(
                   subjectUri,
                   //Constants.MANUFACTURED_PRODUCT,
                   PhrsConstants.MEDICATION_PROPERTY_MANUFACTURED_PRODUCT_URI,
                   newDrugProductUri);
       } catch (TripleException e) {
            LOGGER.error(e.getMessage(),e);
       } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
       }
   }
    */
    public String transformStatus(String status) {

        String out = InteropTermTransformer.transformStatus(status);
        return out;
    }

    public String transformCode(String code) {

        String out = InteropTermTransformer.transformCode(code);
        return out;
    }

    public String transformCategory(String category, String type) {

        String out = InteropTermTransformer.transformCategory(category);
        return out;
    }

    /**
     * @param date
     * @param defaultDate
     * @return
     */
    public String transformDate(Date date, Date defaultDate) {
        Date theDate = date;
        if (theDate == null) {
            if (defaultDate != null) {
                theDate = defaultDate;
            }
        }
        if (theDate != null) {

            return HealthyUtils.formatDate(theDate, (String) null, InteropAccessService.DATE_PATTERN_INTEROP_DATE_TIME);
        }
        return null;
    }

    /**
     * @param adminRoute
     * @param doseUnits
     * @return
     */
    public String tranformMedicationAdminRoute(String adminRoute, String doseUnits) {
        String out = adminRoute;
        out = Constants.HL7V3_ORAL_ADMINISTRATION;


        return out;
    }

    /**
     * @param ownerUri
     * @param phrsClass
     * @return
     */
    public List importNewMessages(String ownerUri, String phrsClass) {
        return importNewMessages(ownerUri, phrsClass, true, null);
    }

    /**
     * @param ownerUri
     * @param phrsClass
     * @param importMessage
     * @return
     */
    public List importNewMessages(String ownerUri, String phrsClass, boolean importMessage) {
        return importNewMessages(ownerUri, phrsClass, importMessage, null);
    }

    /**
     * Import messages
     *
     * @param ownerUri
     * @param phrsClass
     * @param importMessage
     * @param interopMessageIds pass a non null list to collect interopMessageIds
     * @return transformed PHR resources
     */
    public List importNewMessages(String ownerUri, String phrsClass, boolean importMessage, List<String> interopMessageIds) {

        List transformedResources = new ArrayList();
        if (ownerUri != null && phrsClass != null) {

            try {
                //Find new messages (true),ignore known messages that have been imported
                //pass ownerUri, the protocolId is found or not
                Set<DynaBean> results = findInteropMessagesForUser(ownerUri, phrsClass, true);
                //check each results, has it been tagged?

                //import the message, and also save it back to the Interop Service to tag it and make other listeners aware of it.
                if (results != null) {
                    //transactionId can be used to group records that were processed together, the beginDate or create date should be used
                    //to determine a finer degreee of relationship. We are uncertain if the dates alone will help to group records imported.

                    String transactionId = UUID.randomUUID().toString();

                    for (DynaBean dynaBean : results) {
                        String messageUri = null;

                        try {

                            messageUri = dynaBean.getDynaClass().getName();

                            //http://www.icardea.at/phrs#owner
                            //protocol ID, not phrs ownerUri
                            String protocolId = DynaUtil.getStringProperty(dynaBean, Constants.OWNER);
                            LOGGER.debug(" importNewMessages for protocolId interop ownerUri= " + ownerUri + " protocolId " + protocolId);
                            //String creator = DynaUtil.getStringProperty(dynaBean, Constants.CREATOR);

                            Object repositoryObject = transformInteropMessage(ownerUri, phrsClass, dynaBean, messageUri, transactionId);

                            if (repositoryObject != null) {

                                transformedResources.add(repositoryObject);

                                //collecting if caller needs it
                                if (interopMessageIds != null && dynaBean.getDynaClass() != null && dynaBean.getDynaClass().getName() != null) {
                                    interopMessageIds.add(dynaBean.getDynaClass().getName());     //add only the URI
                                }
                            }
                            //Import and mark the interop resource
                            if (importMessage && repositoryObject != null) {
                                //save transformed resource to local store
                                String resourceUri = null;

                                //Handle vitals. These do not map nicely to the web forms
                                if (repositoryObject instanceof ObsRecord) {
                                    //Save it but create a form object also
                                    getCommonDao().crudSaveResource(repositoryObject, ownerUri, "interopservice");

                                    ObsRecord obsRecord = (ObsRecord) repositoryObject;
                                    String code = obsRecord.getCode();
                                    //for these we can create new objects immedicately, not not for Blood pressure
                                    if (Constants.ICARDEA_INSTANCE_BODY_WEIGHT.equals(code)
                                            || Constants.ICARDEA_INSTANCE_BODY_HEIGHT.equals(code)) {
                                        //obsRecord must be saved first
                                        repositoryObject = this.transformObsRecordToPhrFormObject(obsRecord);
                                        //set initially, //saving creates resourceUri
                                        resourceUri = obsRecord.getResourceUri();
                                    }
                                }
                                //Possibly there is a ObsRecord but no second transformation to Phr form object
                                if (repositoryObject == null) {
                                    LOGGER.error("repositoryObject NULL after secondary transformation");
                                } else {
                                    getCommonDao().crudSaveResource(repositoryObject, ownerUri, "interopservice");
                                    //saving creates resourceUri
                                    if (repositoryObject instanceof BasePhrsModel) {
                                        resourceUri = ((BasePhrsModel) repositoryObject).getResourceUri();
                                    }
                                }
                                //saved, now has resourceUri


//                                else if (repositoryObject instanceof BasePhrsMetadata) {
//                                resourceUri = ((BasePhrsMetadata) repositoryObject).getResourceUri();
//
//                                }
//                              mark it, known to both portal and core that this resource has been imported OK
                                if (resourceUri != null) {
                                    //user resourceUri of saved resource to make reference
                                    String note = createReferenceNote(resourceUri);
                                    //mark the message to associate it with this resource
                                    this.updateInteropReferenceNote(messageUri, note);  //throws exception                          
                                } else {
                                    throw new Exception("Missing resourceUri from newly imported  saved resource");
                                }

                            }

                        } catch (Exception e) {

                            LOGGER.error(" message error, interop ownerUri= " + ownerUri, e);
                        }
                    }
                }
            } catch (Exception e) {

                LOGGER.error("", e);
            } catch (java.lang.Error e) {

                LOGGER.error("possible sesame error", e);
            }
        }

        return transformedResources;

    }


    /**
     * @param givenOwnerUri
     * @param messageOwner
     * @return
     */
    public boolean compareOwners(String givenOwnerUri, String messageOwner) {
        boolean valid = false;
        //should be ok, check again
        if (messageOwner != null && messageOwner.length() > 0 && givenOwnerUri != null) {
            if (messageOwner.equals(givenOwnerUri)) {
                valid = true;
            }

        }

        return valid;
    }


    /**
     * Transform message to medication resource
     *
     * @param givenOwnerUri
     * @param phrsClass
     * @param dynabean
     * @param messageResourceUri
     * @return a new phr resource the property originImported is set to True.
     *         This is a transient property that is detected later when persisting the
     *         imported resource for the first time
     */
    public Object transformInteropMessage(String phrOwnerUri, String phrsClass, DynaBean dynabean, String messageResourceUri, String transactionId) {

        Object theObject = null;

        if (Constants.PHRS_MEDICATION_CLASS.equals(phrsClass)) {
            theObject = transformInteropMedicationMessage(phrOwnerUri, phrsClass, dynabean, messageResourceUri);

        } else if (Constants.PHRS_VITAL_SIGN_CLASS.equals(phrsClass)) {
            //bw, bp, bh,etc
            String code = DynaUtil.getStringProperty(dynabean, Constants.HL7V3_CODE);

            if (code != null) {
                LOGGER.debug("transformInteropMessage code found code="+code);
                // ObsRecord

                theObject = transformInteropMessageToObsRecord(
                        phrOwnerUri,
                        phrsClass,
                        dynabean,
                        code,
                        messageResourceUri,
                        transactionId);


            } else {
                LOGGER.debug("transformInteropMessage No code found");
            }
        }
        return theObject;
    }

    /**
     * @param value
     * @param defaultValue
     * @return
     */
    public static Double toDouble(String value, Double defaultValue) {
        Double theValue = null;
        try {
            if (value != null && !value.isEmpty()) {
                theValue = Double.valueOf(value);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException converting = " + value);  //To change body of catch statement use File | Settings | File Templates.
        }
        if (theValue == null) theValue = defaultValue;

        return theValue;
    }

    /**
     * @param phrOwnerUri
     * @param phrsClass
     * @param dynabean
     * @param code
     * @param messageResourceUri
     * @param transactionId
     * @return
     */
    public ObsRecord transformInteropMessageToObsRecord(
            String phrOwnerUri,
            String phrsClass,
            DynaBean dynabean,
            String code,
            String messageResourceUri,
            String transactionId) {

        ObsRecord resource = null;
        LOGGER.debug("transformInteropMessageToObsRecord code found code="+code);
        if (code.equals(Constants.ICARDEA_INSTANCE_BODY_WEIGHT)
                || code.equals(Constants.ICARDEA_INSTANCE_BODY_HEIGHT)
                || code.equals(Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE)
                || code.equals(Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE)) {
            LOGGER.debug("transformInteropMessageToObsRecord create object code="+code);
            resource = new ObsRecord();
            resource.setCode(code);

            if (phrsClass == null) phrsClass = Constants.PHRS_VITAL_SIGN_CLASS;
            resource.setType(phrsClass);

            String value = DynaUtil.getStringProperty(dynabean, Constants.HL7V3_VALUE);
            resource.setValue(value);

            String units = DynaUtil.getStringProperty(dynabean, Constants.HL7V3_UNIT);
            resource.setUnits(units);

            String effectiveDateStr = DynaUtil.getStringProperty(dynabean, Constants.EFFECTIVE_TIME);// Constants.HL7V3_DATE_START);
            Date effectiveDate = transformDateFromMessage(effectiveDateStr, new Date());    //HealthyUtils.formatDate( dateBegin, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)

            resource.setBeginDate(effectiveDate);
            resource.setEndDate(effectiveDate);

            if (transactionId != null && !transactionId.isEmpty()) resource.setTransactionId(transactionId);

        } else {
            LOGGER.debug("transformInteropMessageToObsRecord Ignoring code= " + code);
        }
        //metadata
        if (resource != null) {

            String protocolId = DynaUtil.getStringProperty(dynabean, Constants.OWNER, null);
            if (protocolId != null) {
                protocolId = protocolId.trim();
            }

            //resource.setCode(DynaUtil.getStringProperty(dynabean, Constants.HL7V3_CODE));//FIXXME

            resource.setNewImport(true);

            resource.setOwnerUri(phrOwnerUri);
            resource.setPID(protocolId);//which PID

            resource.setCreatorUri(DynaUtil.getStringProperty(dynabean, Constants.CREATOR, "EHR"));//FIXXME

            String stdStatus = DynaUtil.getValueResourceUri(dynabean, Constants.HL7V3_STATUS, null);

            resource.setStatusStandard(stdStatus);

            resource.setStatus(InteropTermTransformer.transformStandardStatusToLocal(stdStatus, Constants.PHRS_MEDICATION_CLASS));
            resource.setOrigin(DynaUtil.getStringProperty(dynabean, Constants.ORIGIN, PhrsConstants.INTEROP_ORIGIN_DEFAULT_EHR));

            resource.setOriginStatus(PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED);

            resource.setExternalReference(messageResourceUri);

            resource.setCreateDate(new Date());
            resource.setModifyDate(resource.getCreateDate());
            resource.setType(MedicationTreatment.class.toString());

            LOGGER.debug("transformInteropMessageToObsRecord resource {} ",resource);
        }


        return resource;

    }

    /**
     * These should be saved first. If there is a dependency on other ObsRecords,
     * then the dependency is found and inserted such as with blood pressure
     * The transationId should be used or the beginDate
     * parentId is this obsResource
     *
     * @param resource
     * @return The form object relevant to the code
     */
    public Object transformObsRecordToPhrFormObject(ObsRecord obsRecord) {

        Object theObject = null;
        if (obsRecord != null && obsRecord.getCode() != null) {

            String code = obsRecord.getCode();
            LOGGER.debug("transformObsRecordToPhrFormObject found " + code);
            if (Constants.ICARDEA_INSTANCE_BODY_WEIGHT.equals(code)) {
                ObsVitalsBodyWeight resource = new ObsVitalsBodyWeight();
                Double value = toDouble(obsRecord.getValue(), 0d);
                resource.setBodyWeight(value);
                resource.setMeasurementUnit(obsRecord.getUnits());
                theObject = resource;

            } else if (Constants.ICARDEA_INSTANCE_BODY_HEIGHT.equals(code)) {

                ObsVitalsBodyHeight resource = new ObsVitalsBodyHeight();
                Double value = toDouble(obsRecord.getValue(), 0d);

                resource.setBodyHeight(value);
                resource.setMeasurementUnit(obsRecord.getUnits());

                theObject = resource;

            } else if (Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE.equals(code)) {

            } else if (Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE.equals(code)) {

            }

        }
        //set metadata
        if (theObject != null) {

            BasePhrsModel base = (BasePhrsModel) theObject;
            base.setNewImport(true);
            base.setCode(obsRecord.getCode());
            base.setOwnerUri(obsRecord.getOwnerUri());
            base.setPID(obsRecord.getPID());//which PID

            base.setParentId(obsRecord.getResourceUri());
            base.setBeginDate(obsRecord.getBeginDate());
            base.setEndDate(obsRecord.getEndDate());
            base.setTransactionId(obsRecord.getTransactionId());

            base.setCreatorUri(obsRecord.getCreatorUri());//FIXXME

            base.setStatusStandard(obsRecord.getStatusStandard());

            base.setStatus(obsRecord.getStatus());
            base.setOrigin(obsRecord.getOrigin());

            base.setOriginStatus(obsRecord.getOriginStatus());

            base.setExternalReference(obsRecord.getExternalReference());

            base.setCreateDate(obsRecord.getCreateDate());
            base.setModifyDate(obsRecord.getModifyDate());
            base.setType(obsRecord.getType());
        }
        return theObject;
    }


//    Constants.PHRS_VITAL_SIGN_CLASS
//    Constants.CREATE_DATE
//
//
//    //Constants.EFFECTIVE_TIME string
//    String effectiveTime=null;
//    //Constants.HL7V3_CODE
//    String categoryCode = null;
//
//    //Constants.HL7V3_UNIT
//    String units=null;
//
//    categoryCode = Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE
//            units=Constants.MM_HG;
//
//    categoryCode = Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE
//            units=Constants.MM_HG
//
//    categoryCode = Constants.ICARDEA_INSTANCE_BODY_WEIGHT
//            units=Constants.KILOGRAM
//
//    categoryCode = Constants.ICARDEA_INSTANCE_BODY_HEIGHT
//            units=Constants.CENTIMETER

    /**
     * @param givenOwnerUri
     * @param phrsClass
     * @param dynabean
     * @param messageResourceUri
     * @return
     */
    public MedicationTreatment transformInteropMedicationMessage(String phrOwnerUri, String phrsClass, DynaBean dynabean, String messageResourceUri) {
        MedicationTreatment theObject = null;
        if (dynabean != null && phrsClass != null) {
            try {

                LOGGER.debug("phrsClass=" + phrsClass + " bean properties =");

                //switch (phrsClass) {
                //   case Constants.PHRS_MEDICATION_CLASS:
                String protocolId = DynaUtil.getStringProperty(dynabean, Constants.OWNER, null);
                if (protocolId != null) {
                    protocolId = protocolId.trim();
                }
                //do no import additional messages about same drug code
                Map<String, String> attrMedName = getMedicationNameAttributes(dynabean);
                String medName = getMapValue(attrMedName, Constants.HL7V3_DRUG_NAME, "Drug");
                String productCode = getMapValue(attrMedName, Constants.HL7V3_VALUE, null);

                boolean hasDrug = productCode != null && !productCode.isEmpty() && getCommonDao().hasMedication(phrOwnerUri, medName, productCode);
                if (hasDrug) {
                    LOGGER.debug("duplicate Drug detected. New message or other error. productCode=" + productCode + " drugName" + medName + " phrOwnerUri=" + phrOwnerUri);
                    return theObject;
                }
                // Constants.HL7V3_DATE_START  Constants.HL7V3_DATE_END
                // Constants.HL7V3_STATUS Constants.HL7V3_FREQUENCY Constants.HL7V3_ADMIN_ROUTE Constants.HL7V3_DOSAGE
                // Constants.HL7V3_DRUG_NAME Constants.HL7V3_CODE

                MedicationTreatment med = new MedicationTreatment();
                med.setNewImport(true);

                med.setOwnerUri(phrOwnerUri);
                med.setPID(protocolId);//which PID

                med.setCreatorUri(DynaUtil.getStringProperty(dynabean, Constants.CREATOR, "EHR"));//FIXXME

                String stdStatus = DynaUtil.getValueResourceUri(dynabean, Constants.HL7V3_STATUS, null);

                //keep original
                med.setStatusStandard(stdStatus);
                //med.status = "medicationSummary_medicationStatus_true"
                //InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_STATUS, null)

                String defaultStatus = Constants.STATUS_COMPELETE;
                //User must indicate complience, reset to complete.
                med.setStatus(InteropTermTransformer.transformStandardStatusToLocal(defaultStatus, Constants.PHRS_MEDICATION_CLASS));
                //there is no code here, but it is likely a dynabean
                //deprecated med.setCode(DynaUtil.getStringProperty(dynabean, Constants.HL7V3_CODE, null));
                //check the origin of this message....
                med.setOrigin(DynaUtil.getStringProperty(dynabean, Constants.ORIGIN, PhrsConstants.INTEROP_ORIGIN_DEFAULT_EHR));

                med.setOriginStatus(PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED);

                med.setExternalReference(messageResourceUri);

                //check drug code earlier
//                Map<String, String> attrMedName = getMedicationNameAttributes(dynabean);
//                String medName = getMapValue(attrMedName, Constants.HL7V3_DRUG_NAME, "Drug");
//                String productCode = getMapValue(attrMedName, Constants.HL7V3_VALUE, null);

                med.setTitle(medName);
                med.setProductCode(productCode);

                med.getTreatmentMatrix().setAdminRoute(DynaUtil.getValueResourceUri(dynabean, Constants.HL7V3_ADMIN_ROUTE, Constants.HL7V3_ORAL_ADMINISTRATION));
                //this is a uri to another dynabean with dosage and units

                //ISSUE check DOSAGE is QUANTITY but we do not need double? Keep as String
                //ISSUE check dosage per unit not received from EHR, but web form no longer asks.

                //med.getTreatmentMatrix().setDosage(0d);

                Map<String, String> doseAttrs = getMedicationDosageAttributes(dynabean);

                String doseQuantity = getMapValue(doseAttrs, Constants.HL7V3_DOSAGE_VALUE, "0");

                Double dosage = convertDouble(doseQuantity);
                med.getTreatmentMatrix().setDosage(dosage != null ? dosage : 0d);
                //med.getTreatmentMatrix().setDosageQuantity(doseQuantity);

                String doseUnits = getMapValue(doseAttrs, Constants.HL7V3_DOSAGE_UNIT, "http://www.icardea.at/phrs/instances/pills");
                med.getTreatmentMatrix().setDosageUnits(doseUnits);

                //FIXXME - TOD and interval are not expected from EHR.  this create the frequency node
                String interval = "http://www.icardea.at/phrs/instances/other";
                med.getTreatmentMatrix().setDosageInterval(interval);

                String tod = "http://www.icardea.at/phrs/instances/NotSpecified";
                med.getTreatmentMatrix().setDosageTimeOfDay(tod);


                //dates. always need a start date
                String dateBegin = DynaUtil.getStringProperty(dynabean, Constants.HL7V3_DATE_START);
                //set new date if not found
                Date beginDate = transformDateFromMessage(dateBegin, new Date());    //HealthyUtils.formatDate( dateBegin, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)

                String dateEnd = DynaUtil.getStringProperty(dynabean, Constants.HL7V3_DATE_END, null);
                Date endDate = transformDateFromMessage(dateEnd, (Date) null);        //HealthyUtils.formatDate( dateEnd, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)//transformDate(dateEnd)

                med.setBeginDate(beginDate);
                med.setEndDate(endDate);


                med.setCreateDate(new Date());
                med.setModifyDate(med.getCreateDate());
                med.setType(MedicationTreatment.class.toString());

                theObject = med;

                if (med != null) {
                    //System.out.println("medication imported resourceUri=" + med.getResourceUri() + " name=" + med.getTitle() + " code=" + med.getProductCode());
                    LOGGER.debug("medication imported  name=" + med.getTitle() + " code=" + med.getProductCode());
                }
                //}


            } catch (Exception e) {

                if (dynabean != null) {
                    LOGGER.error(
                            "Interop Message transformation failed, phrOwnerUri=" + phrOwnerUri + " phrsClass=" + phrsClass, e);
                } else {
                    LOGGER.error("Interop Message transformation failed, dynabean NULL. ownerUri="
                            + phrOwnerUri + " phrsClass=" + phrsClass, e);
                }
            }
        }
        return theObject;
    }

    //HL7V3_DRUG_NAME
    public static String getMapValue(Map<String, String> map, String key) {
        return getMapValue(map, key, null);
    }

    public static String getMapValue(Map<String, String> map, String key, String defaultValue) {
        String value = null;
        if (map != null && key != null && map.containsKey(key)) {
            value = map.get(key);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }


    /**
     * Get the dosage details and put them into a map
     *
     * @param medicationDynabean
     * @return
     */
    public Map<String, String> getMedicationDosageAttributes(DynaBean medicationDynabean) {
        Map<String, String> map = null;
        try {
            DynaBean dynaBean = (DynaBean) DynaUtil.getDynaBeanProperty(medicationDynabean, Constants.HL7V3_DOSAGE); //http://www.icardea.at/phrs/hl7V3#dosage  (medicationDynabean);

            if (dynaBean != null) {

                //DynaBeanClient dbc = PhrsStoreClient.getInstance().getInteropClients().getDynaBeanClient();
                //DynaBean dynaBean = dbc.getDynaBean(uri);

                String dosage = DynaUtil.getStringProperty(dynaBean, Constants.HL7V3_DOSAGE_VALUE); //Constants.HL7V3_DOSAGE
                String units = DynaUtil.getValueResourceUri(dynaBean, Constants.HL7V3_DOSAGE_UNIT);
                map = new HashMap<String, String>();

                if (dosage != null) {
                    dosage = dosage.trim();
                }
                if (dosage != null && dosage.length() > 0) {
                    map.put(Constants.HL7V3_DOSAGE_VALUE, dosage);
                }
                if (units != null) {
                    units = units.trim();
                }
                if (units != null && units.length() > 0) {
                    map.put(Constants.HL7V3_DOSAGE_UNIT, units);
                }

            }

        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return map;
    }

    /**
     * Get name and product code attributes. These might be stored simply as a
     * name or as complex subnode
     *
     * @param medicationDynabean
     * @return
     */
    //Simple has HL7V3_DRUG_NAME string
    //dyn MANUFACTURED_PRODUCT_CLASS http://www.icardea.at/phrs/hl7V3#manufacturedProduct 
    //dyn ... MANUFACTURED_LABEL_DRUG_CLASS http://www.icardea.at/phrs/hl7V3#manufacturedLabeledDrug
    //String ......HL7V3_DRUG_NAME http://www.icardea.at/phrs/hl7V3#drugName
    //
    //Complex with drug code node dynabean HL7V3_CODE
    //dyn MANUFACTURED_PRODUCT
    //dyn ... MANUFACTURED_LABEL_DRUG
    //dyn ... HL7V3_CODE
    //String ...... HL7V3_VALUE ..drug code
    //String ...... SKOS_PREFLABEL
    //
    //    MANUFACTURED_PRODUCT
    //            "#";
    //    
    //    HL7_CLASS_CODE 
    //            ICARDEA_HL7V3_NS + "#classCode";
    //    
    //    MANUFACTURED_LABEL_DRUG = 
    //            ICARDEA_HL7V3_NS + "#manufacturedLabeledDrug";
    //    
    public Map<String, String> getMedicationNameAttributes(DynaBean medicationDynabean) {
        Map<String, String> map = null;
        try {
            DynaBean dynaBeanMfgProduct = DynaUtil.getDynaBeanProperty(
                    medicationDynabean,
                    PhrsConstants.MEDICATION_PROPERTY_MANUFACTURED_PRODUCT_URI);//TODO replace with Constants.MANUFACTURED_PRODUCT, not MANUFACTURED_PRODUCT_CLASS

            if (dynaBeanMfgProduct != null) {
                DynaBean mfgLabelDrugBean = DynaUtil.getDynaBeanProperty(
                        dynaBeanMfgProduct,
                        Constants.MANUFACTURED_LABEL_DRUG);//not MANUFACTURED_LABEL_DRUG_CLASS

                //Name provided either complex or simple
                //
                //simple:
                //HL7V3_DRUG_NAME literal
                //
                //or complex:                 
                //HL7V3_CODE dyna
                //....SKOS_PREFLABEL literal
                //....HL7V3_VALUE literal

                if (mfgLabelDrugBean != null) {
                    String name, drugId = null;
                    //simple name usually from PHR add  FIXXME WRONG!
                    name = DynaUtil.getStringProperty(mfgLabelDrugBean, Constants.HL7V3_DRUG_NAME); //Constants.HL7V3_DOSAGE
                    //or complex name and code from EHR FIXXME WRONG!
                    DynaBean codeBean = (DynaBean) DynaUtil.getDynaBeanProperty(mfgLabelDrugBean, Constants.HL7V3_CODE);

                    //Try to get name and code from a complex representation
                    if (codeBean != null) {
                        String name2 = DynaUtil.getStringProperty(codeBean, Constants.SKOS_PREFLABEL);
                        name = name2 != null && name2.length() > 0 ? name2 : name;

                        drugId = DynaUtil.getStringProperty(codeBean, Constants.HL7V3_VALUE);
                    }

                    map = new HashMap<String, String>();

                    if (name != null) {
                        name = name.trim();
                    }
                    if (name != null && name.length() > 0) {
                        map.put(Constants.HL7V3_DRUG_NAME, name);
                        //extra 
                        map.put(Constants.SKOS_PREFLABEL, name);
                    }
                    if (drugId != null) {
                        drugId = drugId.trim();
                    }
                    //this is a resource...another dynabean!
                    if (drugId != null && drugId.length() > 0) {
                        map.put(Constants.HL7V3_VALUE, drugId);
                    }
                }


            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return map;
    }

    /**
     * Transform to date string yyyyMMddHHmm
     *
     * @param date
     * @param dateTime
     * @return
     */
    public static Date transformDateFromMessage(String dateMessage, Date defaultDate) {
        Date theDate = null;
        try {
            if (dateMessage != null) {
                //throws nullexception on blank or null
                theDate = DateUtil.getFormatedDate(dateMessage);
            }
            if (theDate != null) {
                theDate = defaultDate != null ? defaultDate : new Date();
            }
        } catch (Exception e) {
            LOGGER.error("transforming date exception on date string=" + dateMessage);
        }
        if (theDate == null) {
            if (defaultDate != null) theDate = defaultDate;
            else theDate = new Date();
        }
        return theDate;
    }

    public String buildMedicationDosage(MedicationClient medicationClient, String dosageValue, String dosageUnits) {
        String newDosageURI = null;
        try {
            newDosageURI = medicationClient.buildDosage(dosageValue, dosageUnits);
            //newDosageURI = getInteropClients().getMedicationClient().buildDosage(dosageValue, dosageUnits);

        } catch (Exception e) {
            LOGGER.error("buildMedicationDosage dosageValue=" + dosageValue + " dosageUnits=" + dosageUnits, e);
        }
        return newDosageURI;
    }

    /**
     * @param resourceUri
     * @param interopResourceId
     * @param predicate
     * @param newValue
     */
    public boolean updateMessageVitals(String resourceUri, String interopResourceId, String predicate, String newValue) {
        boolean success = false;
        try {
            updateInteropStatement(interopResourceId, predicate, newValue);
            //getInteropClients().getVitalSignClient().updateVitalSign(interopResourceId, predicate, newValue);
            success = true;
        } catch (Exception e) {
            LOGGER.error("Interop client updateMessageVitals, interop resource= " + resourceUri + " interopResourceId=" + interopResourceId, e);
        }
        return success;
    }

    /**
     * This cannot not be used directly for the drug name and code or dosage and
     * dosage units - these nodes must be created and the URI assigned here
     *
     * @param resourceUri       - not used directly to update the
     * @param interopResourceId
     * @param predicate
     * @param newValue
     */
    public boolean updateMessageMedication(String resourceUri, String interopResourceId, String predicate, String newValue) {
        boolean success = false;
        try {
            updateInteropStatement(interopResourceId, predicate, newValue);
            // getInteropClients().getMedicationClient().updateMedication(interopResourceId, predicate, newValue);
            success = true;
        } catch (Exception e) {
            LOGGER.error("Interop client updateMessageMedication, interop resource= " + resourceUri + " interopResourceId=" + interopResourceId, e);
        }
        return success;
    }

    /**
     * @param interopResourceId
     * @param predicate
     * @param newValue
     * @throws Exception
     */
    public void updateInteropStatement(String interopResourceId, String predicate, String newValue)
            throws Exception {
        if (interopResourceId != null && newValue != null && predicate != null) {
            getPhrsStoreClient().updateTriple(interopResourceId, predicate, newValue, true);
        }

    }

    /**
     * @param phrResourceUri    - currently not needed
     * @param interopResourceId
     * @param predicate
     * @param newValue
     * @throws Exception
     */
    public void updateInteropStatement(String phrResourceUri, String interopResourceId, String predicate, String newValue)
            throws Exception {
        if (interopResourceId != null && newValue != null && predicate != null) {
            getPhrsStoreClient().updateTriple(interopResourceId, predicate, newValue, true);
        }
        //updateInteropStatement(resourceUri,interopResourceId,predicate,newValue);
    }

    /**
     * @param interopResourceId
     * @param value
     * @throws Exception
     */
    public void updateInteropReferenceNote(String interopResourceId, String newValue) throws Exception {
        if (interopResourceId != null && newValue != null) {
            getPhrsStoreClient().updateSkosNote(interopResourceId, newValue);
        }
    }

    /**
     * @param resourceUri
     * @param interopResourceId
     * @param predicate
     * @param newValue
     */
    public boolean updateMessageProblem(String resourceUri, String interopResourceId, String predicate, String newValue) {
        boolean success = false;
        try {
            updateInteropStatement(interopResourceId, predicate, newValue);
            //getInteropClients().getProblemEntryClient().updateProblemEntry(interopResourceId, predicate, newValue);
            success = true;
        } catch (Exception e) {
            LOGGER.error("Interop client updateMessageProblem, interop resource= " + resourceUri + " interopResourceId=" + interopResourceId, e);
        }
        return success;
    }

    /**
     * @deprecated
     */
    public void registerProtocolId(String owneruri, String protocolId, String namespace) {
        //phrsStoreClient.getInteropClients().getActorClient().register
        if (namespace == null) {
            namespace = Constants.ICARDEA_DOMAIN_PIX_OID;

        }
        //AUTHORIZE_USER_PREFIX_TEST   
        try {
            getInteropClients().getActorClient().register(namespace, owneruri, protocolId);
            LOGGER.debug("registered protocolId in core for owneruri= " + owneruri + " protocolId= " + protocolId + " namespace=" + namespace);

        } catch (TripleException e) {
            LOGGER.error("owneruri= " + owneruri + " protocolId= " + protocolId, e);
        }

    }

}
