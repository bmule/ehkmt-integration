package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Entity
/**
 * Users provide this data and usually there is no corresponding identifier for person or organization
 * however, email might be one means to aggregate these records
 * WAIT ....
 */
// Entity("medication_prescription_source")
class MedicationPrescriptionSource {
	
	String personId
	String organisationId
	String organisationName
	
	String personFullName
	String personTitle
	String personFirstName
	String personLastName
	
	String email
	String telephone

		
	public MedicationPrescriptionSource(){
		
	}
}
