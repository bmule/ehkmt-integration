package at.srfg.kmt.ehealth.phrs.model.baseform

import java.io.Serializable

import at.srfg.kmt.ehealth.phrs.Constants
import at.srfg.kmt.ehealth.phrs.PhrsConstants

import com.google.code.morphia.annotations.Embedded

@Embedded
class MedicationTreatmentMatrix implements  Serializable{
	String identifier
	String origin

	String status
	String treatmentType
	/**
	 * could be integer  or double for describing 1.5 mg
	 */
	Double dosage
	/**
	 * @deprecated
	 */
	String dosageQuantity // take 2 tablets/ twice a day?? better to use interval
	/**
	 * the parent medication object has dosage info, otherwise these are the modifications
	 * for this part
	 * FALSE
	 */
	String dosageInterval
	String dosageTimeOfDay


	/**
	 * Code e.g pill, milligram
	 */
	String dosageUnits
	String adminRoute
	/**
	 * how can this be used? 
	 * conflicts in many cases with dosage
	 * 1 tablet, twice in morning
	 * 
	 */

	Boolean flagQuantityIgnore
	/**
	 * the parent medication object has dosage info, otherwise these are the modifications
	 * for this part
	 * FALSE
	 */
	Boolean flagDosageIgnore

	//Integer sortOrder
	Integer priority
	String note

	String patternTOD

	public MedicationTreatmentMatrix() {
		super()
		treatmentType= "medication"
		status= PhrsConstants.STATUS_RUNNING //running or complete? Might be overriden in controller for UI local code
		flagQuantityIgnore= new Boolean(Boolean.FALSE)
		flagDosageIgnore= 	new Boolean(Boolean.FALSE)
		Integer sortOrder = new Integer(-1)
		Integer priority = 	new Integer(-1)
		adminRoute=Constants.HL7V3_ORAL_ADMINISTRATION 
	}



}
