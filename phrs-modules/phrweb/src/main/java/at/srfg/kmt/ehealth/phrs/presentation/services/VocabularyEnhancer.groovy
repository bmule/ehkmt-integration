package at.srfg.kmt.ehealth.phrs.presentation.services

import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.PhrsConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import at.srfg.kmt.ehealth.phrs.model.baseform.*

public class  VocabularyEnhancer implements Serializable {

	private final static Logger LOGGER = LoggerFactory.getLogger(VocabularyEnhancer.class);
	
	public VocabularyEnhancer(){
	}



	public static List getVocabTaggedByTest(String tag,def sortType,boolean pleaseChoose,String langauge){
		List  list
		//check for special tag ordering
		list = specialSortedLabelValues(tag,langauge)

		// lookup tag and sort alphabetically, but need language
		if(list && ! list.isEmpty()){
			//special sorted labels
		} else {

			list= []
            //add choice option
			if(pleaseChoose){
				ModelLabelValue lv = new ModelLabelValue()
				lv.id=null
				lv.label=PhrsConstants.SELECTION_BOX_PLEASE_CHOOSE;//lookup from I18
				lv.sortOrder=lv.label

				list.add(lv)
			}

			ModelLabelValue lv2 = new ModelLabelValue()
			lv2.id="http://www.icardea.at/phrs/instances/Rashes"
			lv2.label="Rashes test";//lookup from I18
			lv2.sortOrder=lv2.label

			list.add(lv2)
			//TODO sort needs language
			//list.sort{it.sortOrder}
		}
		return list

	}
	/**
	 * Organize terms according to non-alphabetic rules.
	 * TODO replace when collator field is available
	 * @param uri
	 * @param language
	 * @return
	 */
	static  List<ModelLabelValue> specialSortedLabelValues(String uri, String language) {

		List list = alternativeVocabValues(uri)
		List<ModelLabelValue> lvs = null
		if(list){
			lvs = []
			list.each(){ code ->
				ModelLabelValue lv  = VocabularyService.getTermLabelValue(code,language)
				if(lv) lvs.add(lv)
			}
		}

		return lvs
	}

	static  List<String> alternativeVocabValues(String uri) {
		List list = null

		switch ( uri ) {

			case [
				PhrsConstants.TAG_RISK_TREATMENTS_DIABETES
			]:
				list=[
					'http://www.icardea.at/phrs/instances/Diet',
					'http://www.icardea.at/phrs/instances/Drug',
					'http://www.icardea.at/phrs/instances/NoSpecialTreatment'
				]
			//list =['UMLS:C0012155','UMLS:C0013227','http://www.icardea.at/phrs/instances/NoSpecialTreatmentPHRSCode']
				break

			case [
				PhrsConstants.TAG_RISK_TREATMENTS_HYPERTENSION
			]:
				list =[
					'http://www.icardea.at/phrs/instances/Diet',
					'http://www.icardea.at/phrs/instances/Drug',
					'http://www.icardea.at/phrs/instances/NoSpecialTreatment'
				]
			//list =['UMLS:C0012155','UMLS:C0013227','http://www.icardea.at/phrs/instances/S0021']
				break

			case [
				PhrsConstants.TAG_RISK_TREATMENTS_CHOLESTEROL
			]:
				list =[
					'http://www.icardea.at/phrs/instances/Diet',
					'http://www.icardea.at/phrs/instances/MedicationForHighCholesterol',
					'http://www.icardea.at/phrs/instances/NoSpecialTreatment'
				]
				break


			/*case [PhrsConstants.TAG_RISK_TREATMENTS_DIABETES]:
			 list =['UMLS:C0012155','UMLS:C0013227','http://www.icardea.at/phrs/instances/S0021']
			 break
			 case [PhrsConstants.TAG_RISK_TREATMENTS_HYPERTENSION]:
			 list =['UMLS:C0012155','UMLS:C0013227','http://www.icardea.at/phrs/instances/S0021']
			 break
			 case [PhrsConstants.TAG_RISK_TREATMENTS_CHOLESTEROL]:
			 list =['UMLS:C0012155','UMLS:C2924556','http://www.icardea.at/phrs/instances/S0021']
			 break*/
			case [
				PhrsConstants.TAG_MEDICATION_REASON_WITH_RISK_FACTORS
			]:
				list=[
					'http://www.icardea.at/phrs/instances/DiabetesMellitus',
					'http://www.icardea.at/phrs/instances/Cholesterol',
					'http://www.icardea.at/phrs/instances/Hypertension',
					'http://www.icardea.at/phrs/instances/NoSpecialTreatment'
				]
			//TODO add otherrisk to vocab
			/*list = ['UMLS:C0011849',   //diabetes
			 'UMLS:C0008377',//cholesterol
			 'UMLS:C0020538',  //hypertension
			 //'UMLS:C0337664',  //smoker   UMLS:C0337664 http://www.icardea.at/phrs/instances/Smoker
			 'http://www.icardea.at/phrs/instances/otherrisk']*/
				break
			case [
				PhrsConstants.TAG_RISK_FACTORS
			]:
				list = [
					'http://www.icardea.at/phrs/instances/DiabetesMellitus',
					'http://www.icardea.at/phrs/instances/Cholesterol',
					'http://www.icardea.at/phrs/instances/Hypertension',
					'http://www.icardea.at/phrs/instances/Smoker'
				]
			/*
			 list = ['UMLS:C0011849',
			 'UMLS:C0301579',
			 'UMLS:C0020538',
			 'UMLS:C0337664']
			 */
				break
			//http://www.icardea.at/phrs/instances/SmokingTypesCigars_Cigs_Pip_TERM_RISK_SMOKING_TYPES
			case PhrsConstants.TAG_RISK_SMOKING_TYPES:
				list = [
					'http://www.icardea.at/phrs/instances/CigaretteSmoker',
					'http://www.icardea.at/phrs/instances/CigarSmoker',
					'http://www.icardea.at/phrs/instances/PipeSmoker'
				]
				break

			case PhrsConstants.TAG_RISK_SMOKING_QUANTITY:
				list = [
					'http://www.icardea.at/phrs/instances/less_than_1_pack_day',
					//TODO add to vocab
					'http://www.icardea.at/phrs/instances/1PackPerDay',
					'http://www.icardea.at/phrs/instances/2PacksPerDay',
					'http://www.icardea.at/phrs/instances/3PacksPerDay',
					'http://www.icardea.at/phrs/instances/4PacksPerDay'
				]
				break


			case [
				PhrsConstants.TAG_RISK_SMOKING_DURATION
			]:
				list = [
					'http://www.icardea.at/phrs/instances/1Year',
					'http://www.icardea.at/phrs/instances/2Years',
					'http://www.icardea.at/phrs/instances/3Years',
					'http://www.icardea.at/phrs/instances/4Years',
					'http://www.icardea.at/phrs/instances/5To10Years',
					'http://www.icardea.at/phrs/instances/11To15Years',
					'http://www.icardea.at/phrs/instances/16To20Years',
					'http://www.icardea.at/phrs/instances/21To30Years',
					'http://www.icardea.at/phrs/instances/Over30Years'
					//'UMLS:C1271062']  smoke free week why??
				]
				break
			case [
				PhrsConstants.TAG_PHYSICAL_ACTIVITY_FREQUENCY
			]:
				list = [
					'http://www.icardea.at/phrs/instances/EveryDay',
					'http://www.icardea.at/phrs/instances/AFewTimesPerWeek',
					'http://www.icardea.at/phrs/instances/EveryWeek',
					'http://www.icardea.at/phrs/instances/SeveralTimesPerWeek',
					'http://www.icardea.at/phrs/instances/EveryMonth',
					'http://www.icardea.at/phrs/instances/SeveralTimesPerMonth',
					'http://www.icardea.at/phrs/instances/OnOccasion'
				]
				break

			case [
				PhrsConstants.TAG_PHYSICAL_ACTIVITY_DURATION
			]:
				list = [
					'http://www.icardea.at/phrs/instances/15Minutes',
					'http://www.icardea.at/phrs/instances/30Minutes',
					'http://www.icardea.at/phrs/instances/45Minutes',
					'http://www.icardea.at/phrs/instances/1Hour',
					'http://www.icardea.at/phrs/instances/2Hours',
					'http://www.icardea.at/phrs/instances/3Hours',
					'http://www.icardea.at/phrs/instances/4Hours',
					'http://www.icardea.at/phrs/instances/5Hours',
					'http://www.icardea.at/phrs/instances/6Hours',
					'http://www.icardea.at/phrs/instances/7Hours',
					'http://www.icardea.at/phrs/instances/7To12Hours',
					'http://www.icardea.at/phrs/instances/MoreThan12Hours'
				]
				break
			case [
				PhrsConstants.TAG_MEDICATION_DOSAGE_FREQUENCY_QUANTITY
			]:
				list = [
					'http://www.icardea.at/phrs/instances/medfreq_1_time',
					//1 time or einmal
					'http://www.icardea.at/phrs/instances/medfreq_2_time',
					//2 times per day or zweimal
					'http://www.icardea.at/phrs/instances/medfreq_3_time',
					'http://www.icardea.at/phrs/instances/medfreq_4_time',
					'http://www.icardea.at/phrs/instances/medfreq_5_time',
					'http://www.icardea.at/phrs/instances/otherrisk'
				]
				break
			case [
				PhrsConstants.TAG_MEDICATION_DOSAGE_FREQUENCY_INTERVAL_1
			]:
				list = [
					'http://www.icardea.at/phrs/instances/OnOccasion',
					'http://www.icardea.at/phrs/instances/EveryHour',
					'http://www.icardea.at/phrs/instances/PerDay',
					'http://www.icardea.at/phrs/instances/PerWeek',
					'http://www.icardea.at/phrs/instances/PerMonth',
					'http://www.icardea.at/phrs/instances/PerYear',
					'http://www.icardea.at/phrs/instances/other'
				]
				break
			case [
				PhrsConstants.TAG_MEDICATION_DOSAGE_TIME_OF_DAY_1
			]:
				list = [
					'http://www.icardea.at/phrs/instances/NotSpecified',
					'http://www.icardea.at/phrs/instances/InTheMorning',
					'http://www.icardea.at/phrs/instances/AtNoon',
					'http://www.icardea.at/phrs/instances/afternoon',
					'http://www.icardea.at/phrs/instances/InTheEvening',
					'http://www.icardea.at/phrs/instances/AtBedtime'
				]
				break
			/**
			 * TODO TAG_DRUG_MEDICATION_COMPLIANCE_STATUS replace with vocab URIs, update vocab!!
			 * or these can also be map to and from the appropriate  interop code
			 * The vocab service will check the I18 files first before querying the vocabulary
			 *  
			 */
			//remove 'medicationSummary_medicationStatus_false_completed',
			case [
				PhrsConstants.TAG_MEDICATION_COMPLIANCE_STATUS
			]:
				list = [
					'medicationSummary_medicationStatus_true',
					//Yes, I currently take this medication
					'medicationSummary_medicationStatus_false'
				]
				break
			case [
				PhrsConstants.TAG_RISK_FACTORS_SMOKING_STATUS
			]:
				list = [
					'riskfactor.isActiveStatus.smoking.true.label',
					'riskfactor.isActiveStatus.smoking.false.label'
				]
				break
			/**
			 * The labels are in the I18 files, not vocab server
			 */
			/*
			 'action.categories.appointment.medical.doctor',
			 'action.categories.appointment.medical.dentist',
			 'action.categories.appointment.medical.other',
			 'action.categories.appointment.selfhelp',
			 'action.categories.appointment.selfhelp.group',
			 'action.categories.log.problem',
			 */
			case [
				PhrsConstants.TAG_ACTION_CATEGORIES
			]:
				list = [
					'action.categories.appointment',
					'action.categories.appointment.medical.other',
					'action.categories.activity.sport',
					'action.categories.log.nutrition',
					'action.categories.log.medication',
					'action.categories.activity_other'
				]
				break
			case [
				PhrsConstants.TAG_ROLES_MEDICAL_PROFESSIONAL
			]:
				list = [
					'role_medical_physician',
					'role_medical_physician_gp',
					'role_medical_physician_cardiologist',
					'role_medical_dentist',
					'role_medical_dietitian',
					'role_medical_practictioner_healthcare',
					'role_medical_nurse',
					'role_medical_other_specialist',
					'role_medical_professional_other'
				]


				break
			case [
				PhrsConstants.TAG_ROLES_NON_MEDICAL_USER
			]:
				list = [
					'role_medical_patient',
					'role_medical_health_user'
				]


				break
			case [
				PhrsConstants.TAG_ROLES_NON_MEDICAL_USER
			]:
				list = [
					'role_nonmedical_patient',
					'role_nonmedical_caretaker',
					'role_nonmedical_other'
				]
				break
			//TODO Pills is not associated to diabetes medications in vocabulary
                        //'http://www.icardea.at/phrs/instances/pills',
			case PhrsConstants.TAG_DIABETES_MEDICATIONS:
				list = [
					'http://www.icardea.at/phrs/instances/Insulin', 
                                        Constants.PILL,
					'http://www.icardea.at/phrs/instances/NoSpecialTreatment'
				]
				break
			case PhrsConstants.TAG_MEDICATION_DOSAGE_DOSAGE_UNITS:
				list = [ Constants.PILL,Constants.DROPS, Constants.MILLIGRAM,Constants.GRAM]
                                //list = [ Constants.PILL,Constants.DROPS, Constants.MILLIGRAM]
				break
		

		}

		return list
	}
 

	public static Map localVocabularies(Class filterClass,String language){

		Map<String,Collection<ModelLabelValue>> map = [:]
		if(! filterClass) {
			println('localVocabularies filterClass is null')
			LOGGER.error('localVocabularies filterClass is null')
			return map
		}

		String canonicalName= filterClass.canonicalName


		switch ( canonicalName ) {
			case [
				ProfileRisk.getCanonicalName()
			]:

				map.put('TAG_RISK_TREATMENTS_DIABETES', 	getTerms(PhrsConstants.TAG_RISK_TREATMENTS_DIABETES,language))
			//				map.put('RISK_DIABETES_TYPE_MEDICATION', 	getTerms(PhrsConstants.RISK_DIABETES_TYPE_MEDICATION,language))
				map.put('TAG_DIABETES_MEDICATIONS',             getTerms(PhrsConstants.TAG_DIABETES_MEDICATIONS,language))

			// code  PhrsConstants.TAG_RISK_FACTORS_CHOLESTEROL
				map.put('TAG_RISK_TREATMENTS_HYPERTENSION',     getTerms(PhrsConstants.TAG_RISK_TREATMENTS_HYPERTENSION,language))

			// code PhrsConstants.TAG_RISK_FACTORS_HYPERTENSION
				map.put('TAG_RISK_TREATMENTS_CHOLESTEROL',      getTerms(PhrsConstants.TAG_RISK_TREATMENTS_CHOLESTEROL,language))

			// code PhrsConstants.TAG_RISK_FACTORS_SMOKING
				map.put('TAG_RISK_FACTORS_SMOKING_STATUS', 	getTerms(PhrsConstants.TAG_RISK_FACTORS_SMOKING_STATUS,language))
				map.put('TAG_RISK_SMOKING_TYPES', 		getTerms(PhrsConstants.TAG_RISK_SMOKING_TYPES,language))
				map.put('TAG_RISK_SMOKING_DURATION', 		getTerms(PhrsConstants.TAG_RISK_SMOKING_DURATION,language))
				map.put('TAG_RISK_SMOKING_QUANTITY', 		getTerms(PhrsConstants.TAG_RISK_SMOKING_QUANTITY,language))
				break

			case [
				ProfileActivityDailyLiving.class.getCanonicalName(),
				ActivityDailyLiving.class.getCanonicalName(),
				ActivityDailyLivingSimple.class.getCanonicalName()
			]:
				map.put('TAG_ACTIVITIES_OF_DAILY_LIVING', 	getTerms(PhrsConstants.TAG_ACTIVITIES_OF_DAILY_LIVING,language))
				map.put('TAG_ACTIVITIES_OF_DAILY_LIVING_STATUS',getTerms(PhrsConstants.TAG_ACTIVITIES_OF_DAILY_LIVING_STATUS,language))

				break

			case [
				ActionActivityBase.class.getCanonicalName(),
				ActionPlanEvent.class.getCanonicalName()
			]:
             //, CalendarEventWrapper.class.getCanonicalName()
				map.put('TAG_ACTION_CATEGORIES', getTerms(PhrsConstants.TAG_ACTION_CATEGORIES,language))
				break

			case MedicationTreatment.class.getCanonicalName():

				map.put('TAG_MEDICATION_COMPLIANCE_STATUS', 	getTerms(PhrsConstants.TAG_MEDICATION_COMPLIANCE_STATUS,language))
				//query vocab for http://www.icardea.at/phrs/instances/MedicationUnits
                                map.put('TAG_MEDICATION_DOSAGE_DOSAGE_UNITS', 	getTerms(PhrsConstants.TAG_MEDICATION_DOSAGE_DOSAGE_UNITS,language))
				map.put('TAG_MEDICATION_DOSAGE_FREQUENCY_INTERVAL_1', getTerms(PhrsConstants.TAG_MEDICATION_DOSAGE_FREQUENCY_INTERVAL_1,language))
				map.put('TAG_MEDICATION_DOSAGE_TIME_OF_DAY_1', 	getTerms(PhrsConstants.TAG_MEDICATION_DOSAGE_TIME_OF_DAY_1,language))
				map.put('TAG_MEDICATION_REASON_WITH_RISK_FACTORS', getTerms(PhrsConstants.TAG_MEDICATION_REASON_WITH_RISK_FACTORS,language))

				break

			case ObsActivityPhysical.class.getCanonicalName():
				map.put('TAG_PHYSICAL_ACTIVITIES', 		getTerms(PhrsConstants.TAG_PHYSICAL_ACTIVITIES,language))
				map.put('TAG_PHYSICAL_ACTIVITY_FREQUENCY', 	getTerms(PhrsConstants.TAG_PHYSICAL_ACTIVITY_FREQUENCY,language))
				map.put('TAG_PHYSICAL_ACTIVITY_DURATION', 	getTerms(PhrsConstants.TAG_PHYSICAL_ACTIVITY_DURATION,language))
			//map.put('STATUS_ACTIVITY', 			null,language)
			/*
			 TODO Can't do from here ??, must use direct lookup on radio	
			 stringModel_4 = ['activityItem_isActiveStatusTrue','activityItem_isActiveStatusFalse']				
			 */
				break

			case ObsProblem.class.getCanonicalName():
				map.put('TAG_PROBLEM_SYMPTOM',                  getTerms(PhrsConstants.TAG_PROBLEM_SYMPTOM,language))
			/*
			 TODO Can't do from here ??, must use direct lookup on radio	
			 stringModel_2 = ['default_activeStatusTrue','default_activeStatusFalse']				
			 */
				break

			case ObsVitalsBloodPressure.class.getCanonicalName():
		
				break

			case ObsVitalsBodyWeight.class.getCanonicalName():
			
				break
		    //				ProfileUserContactInfo.class.getCanonicalName(),
			case [
				ProfileContactInfo.class.getCanonicalName(),
				ProfileMedicalContactInfo.class.getCanonicalName()
			]:
				map.put('TAG_ROLES_MEDICAL_PROFESSIONAL', 	getTerms(PhrsConstants.TAG_ROLES_MEDICAL_PROFESSIONAL,language))
			//TAG_ROLES_NON_MEDICAL_USER
				map.put('TAG_ROLES_NON_MEDICAL_USER',           getTerms(PhrsConstants.TAG_ROLES_NON_MEDICAL_USER,language))

				break

			default:
				break
		}

		return map
	}

	public static Collection<ModelLabelValue> getTerms(String tag,String language){

		return VocabularyService.getVocabTaggedBy( tag,0,false, language)

	}
	/**
	 * Riskfactor treatment lists
	 * @param theCode
	 * @param vocabMap
	 * @return
	 */
	public static Collection<ModelLabelValue> getRiskfactorTreatmentsList(String theCode, Map vocabMap){

		if(vocabMap && theCode){

			switch (theCode ){
				case PhrsConstants.TAG_RISK_FACTORS_CHOLESTEROL :
					return vocabMap.get('TAG_RISK_TREATMENTS_CHOLESTEROL')
					break

				case PhrsConstants.TAG_RISK_FACTORS_DIABETES :
					return vocabMap.get('TAG_RISK_TREATMENTS_DIABETES')
					break

				case PhrsConstants.TAG_RISK_FACTORS_HYPERTENSION :
					return vocabMap.get('TAG_RISK_TREATMENTS_HYPERTENSION')
					break
			}
		}
		return new ArrayList<ModelLabelValue>()
	}

}
