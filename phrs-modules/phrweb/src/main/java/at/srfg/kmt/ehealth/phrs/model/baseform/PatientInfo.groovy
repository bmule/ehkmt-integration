package at.srfg.kmt.ehealth.phrs.model.baseform

import com.google.code.morphia.annotations.Entity

@Entity
class PatientInfo extends BasePhrsModel {
	
	 String assigningAuthority
	
	 String identifierTypeCode
	
	 String givenName
	
	 String familyName
	
	 String secondName
	
	 String dateTimeOfBirth
	
	 String administrativeSex
	
	 String street
	
	 String city
	
	 String postalCode
	
	 String country
	
	 String patientIdentifier
	 String protocolId //same as patientIdentifier
	
	 String citizenshipNumber
}
