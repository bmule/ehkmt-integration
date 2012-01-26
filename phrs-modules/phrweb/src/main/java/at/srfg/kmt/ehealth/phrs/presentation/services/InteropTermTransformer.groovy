package at.srfg.kmt.ehealth.phrs.presentation.services

import java.io.Serializable

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.PhrsConstants

class InteropTermTransformer implements Serializable{
	private final static Logger LOGGER = LoggerFactory.getLogger(InteropTermTransformer.class);
	public final static CODE_WATCH_SPORT ='http://www.icardea.at/phrs/instances/OtherSport'

	private InteropAccessService interopAccessService;
        
	public InteropTermTransformer(InteropAccessService interopAccessService){
		this.interopAccessService=interopAccessService
	}

	public static  String transformStatus(String input){
		return transformStatus(input,null)
	}

	public  static String transformStatus(String input, String filter){
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
	public static String transformStandardStatusToLocal(String input, String phrsClass){
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
	public static String transformCode(String input,String filter){
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

	public static String transformCode(String input){
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


}
