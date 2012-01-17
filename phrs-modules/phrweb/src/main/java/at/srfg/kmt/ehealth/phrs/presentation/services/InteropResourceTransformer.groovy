package at.srfg.kmt.ehealth.phrs.presentation.services

import java.io.Serializable

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.PhrsConstants

class InteropResourceTransformer implements Serializable{
	private final static Logger LOGGER = LoggerFactory.getLogger(InteropResourceTransformer.class);
	public final static CODE_WATCH_SPORT ='http://www.icardea.at/phrs/instances/OtherSport'

	private InteropAccessService interopAccessService;
	public InteropResourceTransformer(InteropAccessService interopAccessService){
		this.interopAccessService=interopAccessService
	}

	public static transformStatus(String input){
		transformStatus(input,null)
	}

	public static transformStatus(String input, String filter){
		String out=input
		if(input){
			out = toCompletionStatus(input)
			/*
			 switch(filter){
			 default:
			 out = toCompletionStatus(input)
			 break
			 }*/
		}
		return out
	}
	/**
	 * 
	 * @param input
	 * @param phrsClass
	 * @return
	 */
	public static transformStandardStatusToLocal(String input, String phrsClass){
		String out=input

		if(input){
			//out = toCompletionStatus(input)

			switch(input){

				//The UI does not offer all possibilities, when sending a new message to the interop, translate the original status appropriately
				case Constants.STATUS_COMPELETE:
					if(phrsClass == Constants.PHRS_MEDICATION_CLASS) out='medicationSummary_medicationStatus_true'
					break
				case Constants.STATUS_RUNNING:
					if(phrsClass == Constants.PHRS_MEDICATION_CLASS) out='medicationSummary_medicationStatus_true'
					break
				case Constants.STATUS_INCOMPELETE:
					if(phrsClass == Constants.PHRS_MEDICATION_CLASS) out='medicationSummary_medicationStatus_false_completed'
					break
				case Constants.STATUS_SUSPENDED:
					if(phrsClass == Constants.PHRS_MEDICATION_CLASS) out='medicationSummary_medicationStatus_false_completed'
					break
				case Constants.STATUS_INTERRUPTED:
					if(phrsClass == Constants.PHRS_MEDICATION_CLASS) out='medicationSummary_medicationStatus_false_completed'
					break
				case Constants.STATUS_ABORTED:
					if(phrsClass == Constants.PHRS_MEDICATION_CLASS) out='medicationSummary_medicationStatus_false_completed'
					break
				default:
					out = null
					break
			}
		}
		return out
	}
	/**
	 * Transform known local Portal resource.code to an acceptable Interoperability completion code 
	 * Also provides higher semantic meaning.
	 * @param input
	 * @param filter
	 * @return
	 */
	public static transformCode(String input,String filter){
		String out=input
		if(input){
			switch(input){
				//sports in Action Plan uses local code
				case [
					'action.categories.activity.sport'
				]:
					out=CODE_WATCH_SPORT
				default:
					break
			}
		}
		return out
	}

	public static transformCode(String input){
		transformCode(input,null)
	}

	/**
	 * Transform known local Portal resource.status code to an acceptable Interoperability completion code 
	 * Also provides higher semantic meaning.
	 * @param theStatus
	 * @return
	 */
	public static String toCompletionStatus(String theStatus){
		String out = theStatus

		if(theStatus){
			switch(theStatus){
				case [
					PhrsConstants.STATUS_RUNNING
					,
					'medicationSummary_medicationStatus_true'
					,
					'default_activeStatusTrue'
					,
					'activityItem_isActiveStatusTrue'
					,
					'riskfactor_isActiveStatus',
					,
					'riskfactor_isActiveStatus_short'
					,
					'action_isActiveStatusTrue'
				]:
					out= PhrsConstants.STATUS_RUNNING
					break
				case [
					PhrsConstants.STATUS_COMPLETE
					,
					'medicationSummary_medicationStatus_true'
					,
					'default_activeStatusFalse'
					,
					'activityItem_isActiveStatusFalse'
					,
					'action_isActiveStatusFalse'
				]:
					out= PhrsConstants.STATUS_COMPLETE
					break

				case [
					PhrsConstants.STATUS_INCOMPLETE
					,
					'medicationSummary_medicationStatus_false_completed',
					,
					'medicationSummary_medicationStatus_false'
					,
					'default_activeStatusNotSpecified'
					,
					(PhrsConstants.SELECTION_NO_ANSWER)
				]:
					out= PhrsConstants.STATUS_COMPLETE
					break

				default:
				//should comply with interop messaging
					out= PhrsConstants.STATUS_COMPLETE
					break
			}
		}

		if(!out) out= PhrsConstants.STATUS_COMPLETE

		return out
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
