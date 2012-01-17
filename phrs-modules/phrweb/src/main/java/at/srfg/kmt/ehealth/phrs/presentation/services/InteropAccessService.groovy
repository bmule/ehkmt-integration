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
		String out=adminRoute

		if(!adminRoute){

			switch(doseUnits){
				//TODO Ask GUI question if mg/ml option chosen to determine if injected...
				default:
					out = HL7V3_ORAL_ADMINISTRATION
					break
			}
		}

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
		def value
		try{
			value = bean.get(property)

			if(!value) value = defaultValue
		} catch (Exception e){
			LOGGER.error('property= '+property, e)
		}
		return value
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
	protected Map sendMessages(def resource,String resourceType, String action,Map attrs){
		Map messageIdMap=[:]

		if(resource && resource instanceof BasePhrsModel){

			try{
				//the resulting message URI that is added to the result list
				String messageId

				BasePhrsModel res = (BasePhrsModel)resource
				String owner 	= res.ownerUri

				String status 	= this.transformStatus(res.status)
				status = status?: null

				String categoryCode 	= this.transformCategory(res.category,resourceType)
				categoryCode = categoryCode?: null

				String valueCode 	= this.transformCode(res.code)
				valueCode = valueCode?: null

				String dateStringStart 	= transformDate(res.beginDate,res.endDate)
				dateStringStart = dateStringStart?: null

				String dateStringEnd 	= transformDate(res.endDate,(Date)null)
				dateStringEnd = dateStringEnd?: null

				//res.note is by default not sharable
				/**
				 * Use the note to tag this record. 
				 * Be sure to write note to multiple messages such as Vital signs with separate messages for Body weight, height, sys, diastolic
				 * 				
				 */
				//String referenceNote 	=
				String referenceNote 	= res.resourceUri //'' //res.note is by default not sharable

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
						if(valueCode == InteropResourceTransformer.CODE_WATCH_SPORT){
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
									dosageValue,
									doseUnits,
									name);
							/*
						 domain.prescribedByName
						 domain.reasonCode
						 doseTimeOfDay not used
						 */
							if(messageId)  {
								messageIdMap.put(categoryCode,messageId)
							}
						} else {
							//update
							//this.updateMessageProblem(theParentId, interopRef, ., valueCode); Constants.HL7V3_ADMIN_ROUTE
							this.updateMessageMedication(theParentId, interopRef, HL7V3_STATUS, status);
							this.updateMessageMedication(theParentId, interopRef, HL7V3_START_DATE, dateStringStart);
							this.updateMessageMedication(theParentId, interopRef, HL7V3_END_DATE, dateStringEnd);
							this.updateMessageMedication(theParentId, interopRef, HL7V3_FREQUENCY, doseInterval);
							this.updateMessageMedication(theParentId, interopRef, HL7V3_ADMIN_ROUTE, adminRoute);
							//This needs a URI
							String newDosageURI = buildMedicationDosage(dosageValue, doseUnits);
							//	PHRS_MEDICATION_DOSAGE if updating or HL7V3_DOSAGE if retrieving from EHR data

							this.updateMessageMedication(theParentId, interopRef, PHRS_MEDICATION_DOSAGE, newDosageURI);
							//this.updateMessageProblem(theParentId, interopRef, xxxx, doseUnits);
							this.updateMessageMedication(theParentId, interopRef, HL7V3_DRUG_NAME, name);

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


	/**
	 * Take second part after equals sign as an identifier.
	 * @param note
	 * @return
	 */
	public static String parseReferenceNote(String note){
		String out=null

		if(note){
			//or def parts, then use parts.size()
			String[] parts = note.split('=');

			if(parts && parts.length() > 0){
				out= parts[1]
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

					DynaBeanClient dbc = getInteropClients().getDynaBeanClient()

					for(String messageResourceUri:results){
						try{
							DynaBean bean = dbc.getDynaBean(messageResourceUri)

							def repositoryObject = transformInteropMessage( ownerUri, phrsClass,  bean,   messageResourceUri, phrsStoreClient)

							if(repositoryObject){
								list.add(messageResourceUri)
								//save transformed resource to local store
								//the resourceUri issue
								getCommonDao().crudSaveResource(repositoryObject, ownerUri, 'interopservice')
								//send message to interop service
								//Map map=
								sendMessages(repositoryObject)
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch(Exception e){
				LOGGER.error(' interop ownerUri= '+ownerUri, e)
			}
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
				String dosage = bean.get(Constants.HL7V3_DOSAGE_VALUE) //Constants.HL7V3_DOSAGE
				String units = bean.get(Constants.HL7V3_DOSAGE_UNIT)
				map = new HashMap()


				map.put(Constants.HL7V3_DOSAGE_VALUE, dosage)
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
	/**
	 * Already know this is new (origin and no resource tag) but must update this record? Get the resourceURI and then update the note with local resourceUri
	 * @param givenOwnerUri
	 * @param phrsClass
	 * @param bean
	 * @param client
	 * @return
	 */
	public def transformInteropMessage(String givenOwnerUri,String phrsClass, DynaBean bean, String messageResourceUri,PhrsStoreClient client){
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
							med.creatorUri=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.CREATOR, med.ownerUri)


							String stdStatus = InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_STATUS, null)//TODO default

							med.statusStandard = stdStatus
							//med.status = 'medicationSummary_medicationStatus_true'
							//InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_STATUS, null)//TODO default
							med.status=InteropResourceTransformer.transformStandardStatusToLocal(stdStatus,Constants.PHRS_MEDICATION_CLASS)

							med.code = InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_CODE, null)//TODO is this drug code?
							//check the origin of this message....
							med.origin=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.ORIGIN, PhrsConstants.INTEROP_ORIGIN_DEFAULT_EHR)

							med.originStatus=PhrsConstants.INTEROP_ORIGIN_STATUS_IMPORTED
							med.externalReference=messageResourceUri

							//do not set resourceUri, but origin should be checked during udpates to interop messages

							med.reasonCode='http://www.icardea.at/phrs/instances/NoSpecialTreatment'
							//med.prescribedByName=

							med.title=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_DRUG_NAME, null)

							med.note=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.SKOS_NOTE, null)

							med.treatmentMatrix.adminRoute=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_ADMIN_ROUTE, null)
							//this is a uri to another dynabean with dosage and units
							//Constants.HL7V3_DOSAGE PHRS_MEDICATION_DOSAGE
							String dosageUri=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_DOSAGE, null)
							if(dosageUri==null)
								dosageUri=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.PHRS_MEDICATION_DOSAGE, null)

							Map attrs= this.getMedicationDosageAttributes(dosageUri)
							try{
								if(attrs){
									if(attrs.containsKey(Constants.HL7V3_DOSAGE_VALUE)){
										med.treatmentMatrix.dosage=Double.valueOf(attrs.get(Constants.HL7V3_DOSAGE_VALUE))
									}
								}

							}catch (Exception e){
								LOGGER.error('dosage number conversion error',e)
							}
							if(attrs.containsKey(Constants.HL7V3_DOSAGE_UNIT)){
								med.treatmentMatrix.dosageUnits=attrs.get(Constants.HL7V3_DOSAGE_UNIT)

							}

							med.treatmentMatrix.dosageInterval=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_FREQUENCY, 'http://www.icardea.at/phrs/instances/other')
							med.treatmentMatrix.dosageTimeOfDay='http://www.icardea.at/phrs/instances/NotSpecified'

							//dates
							String dateBegin=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_DATE_START, null)
							Date beginDate = transformDateFromMessage(dateBegin,(Date)null) 	//HealthyUtils.formatDate( dateBegin, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)

							String dateEnd=InteropAccessService.getDynaBeanPropertyValue(bean, Constants.HL7V3_DATE_END, null)
							Date endDate = transformDateFromMessage(dateEnd,(Date)null) 		//HealthyUtils.formatDate( dateEnd, (String)null, DATE_PATTERN_INTEROP_DATE_TIME)//transformDate(dateEnd)

							med.beginDate=beginDate
							med.endDate=endDate


							med.createDate=new Date()
							med.modifyDate=med.createDate
							med.type=MedicationTreatment.class.toString()
							theObject = med
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
	//	addMedicationSign(String user, String note, String statusURI,
	//		String startDate, String endDate, String frequencyURI,
	//		String adminRouteURI, String dosageValue, String dosageUnit,
	//		String drugName)
	//	final String subject =
	//			triplestore.persist(Constants.OWNER, user, LITERAL);
	//
	//	// this can help to find a medication, there are alos other way
	//	// to do this (e.g. using the know templateRootID, for more )
	//	// information about this please consult the documentation)
	//	triplestore.persist(subject,
	//			Constants.RDFS_TYPE,
	//			Constants.PHRS_MEDICATION_CLASS,
	//			RESOURCE);
	//
	//	// generic informarion (beside the 'OWNER' they are not really relevant
	//	// for the HL7 V3 message)
	//	triplestore.persist(subject,
	//			Constants.CREATE_DATE,
	//			DateUtil.getFormatedDate(new Date()),
	//			LITERAL);
	//
	//	// I preffer to hang a specific name for the Creator only for test
	//	// purposes. In this way I can follow the the origin for a certain
	//	// resource.
	//	triplestore.persist(subject,
	//			Constants.CREATOR,
	//			CREATORN_NAME,
	//			LITERAL);
	//
	//	// HL7 specific informations.
	//	// according with the specification the medcation requires this
	//	// template root id.
	//	triplestore.persist(subject,
	//			Constants.HL7V3_TEMPLATE_ID_ROOT,
	//			Constants.IMUNISATION,
	//			LITERAL);
	//
	//	// HL7 specific informations.
	//	// according with the specification the medcation requires this
	//	// template root id.
	//	triplestore.persist(subject,
	//			Constants.HL7V3_TEMPLATE_ID_ROOT,
	//			Constants.MEDICATION,
	//			LITERAL);
	//
	//	triplestore.persist(subject,
	//			Constants.SKOS_NOTE,
	//			note,
	//			LITERAL);
	//
	//	triplestore.persist(subject,
	//			Constants.HL7V3_STATUS,
	//			statusURI,
	//			RESOURCE);

	//
	//	triplestore.persist(subject,
	//			Constants.HL7V3_DATE_START,
	//			startDate,
	//			LITERAL);
	//
	//	triplestore.persist(subject,
	//			Constants.HL7V3_DATE_END,
	//			endDate,
	//			LITERAL);
	//
	//	triplestore.persist(subject,
	//			Constants.HL7V3_FREQUENCY,
	//			frequencyURI,
	//			RESOURCE);
	//
	//	triplestore.persist(subject,
	//			Constants.HL7V3_ADMIN_ROUTE,
	//			adminRouteURI,
	//			RESOURCE);
	//
	//	final String dosage = buildDosage(dosageValue, dosageUnit);
	//	triplestore.persist(subject,
	//			Constants.HL7V3_DOSAGE,
	//			dosage,
	//			LITERAL);
	//
	//	triplestore.persist(subject,
	//			Constants.HL7V3_DRUG_NAME,
	//			drugName,
	//			LITERAL);
	//
	//        for (String resoure : resources) {
	//            final DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
	//            final Object rdfType = dynaBean.get(Constants.RDFS_TYPE);
	//            assertEquals(Constants.PHRS_VITAL_SIGN_CLASS, rdfType);
	//
	//            final Object createDate = dynaBean.get(Constants.CREATE_DATE);
	//            assertNotNull(createDate);
	//
	//            final Object creator = dynaBean.get(Constants.CREATOR);
	//            assertEquals(VitalSignClient.class.getName(), creator);
	//
	//            final List rootTemplates =
	//                    (List) dynaBean.get(Constants.HL7V3_TEMPLATE_ID_ROOT);
	//            assertTrue(rootTemplates.size() == 3);
	//
	//            final DynaBean code = (DynaBean) dynaBean.get(Constants.HL7V3_CODE);
	//            proveCode(code);
	//
	//            final Object note = dynaBean.get(Constants.SKOS_NOTE);
	//            assertEquals(NOTE, note);
	//
	//            final Object effectiveTime = dynaBean.get(Constants.EFFECTIVE_TIME);
	//            assertEquals(TIME, effectiveTime);
	//
	//            final DynaBean statusBean =
	//                    (DynaBean) dynaBean.get(Constants.HL7V3_STATUS);
	//            proveStatusBean(statusBean);
	//
	//            final Object value = dynaBean.get(Constants.HL7V3_VALUE);
	//            assertEquals(VALUE, value);
	//
	//            final DynaBean unit = (DynaBean) dynaBean.get(Constants.HL7V3_UNIT);
	//            proveUnit(unit);
	//        }oveUnit(unit);
	//	 }

	/*
	 final DynaProperty dynaProperty =
	 newInstance.getDynaClass().getDynaProperty(predicate);
	 final boolean isList = dynaProperty.getType().equals(ArrayList.class);
	 if (!isList) {
	 newInstance.set(predicate, value);
	 } else {
	 List props = (List) newInstance.get(predicate);
	 if (props == null) {
	 props = new ArrayList<String>();
	 newInstance.set(predicate, props);
	 }
	 props.add(value);
	 }
	 */
}
