package at.srfg.kmt.ehealth.phrs.model.baseform


import com.google.code.morphia.annotations.Embedded
import com.google.code.morphia.annotations.Entity
import com.google.code.morphia.annotations.Property

@Entity
public class  MedicationTreatment extends CommonModelProps{

	//Boolean useMultipleTreatments
	/**
	 * associate with Risk or other
	 * primary reason
	 */
	String reasonCode
	String noteReason
	
	/**
	 * kept in order of user's order
	 */
	@Property(concreteClass = java.util.TreeSet.class)
	Set<String> medicationReasonCodes
	
	//@Reference ProfileMedicalContactInfo prescribedBy
	String prescribedByName

	/**
	 * primary treatment matrix
	 */
	@Embedded
	MedicationTreatmentMatrix treatmentMatrix
	
	//@Embedded
	//List<MedicationTreatmentMatrix> treatmentMatrixMultiple
		
	//@Embedded
	//MedicationTreatmentMatrix treatmentMatrixHistory

	
	public MedicationTreatment(){
		super()
		//useMultipleTreatments  = new Boolean(Boolean.TRUE)
		treatmentMatrix  = new MedicationTreatmentMatrix()	
		//treatmentMatrixMultiple= new ArrayList<MedicationTreatmentMatrix>()
		//treatmentMatrixHistory = new ArrayList<MedicationTreatmentMatrix>()
		medicationReasonCodes  = new HashSet<String>()
		//prescribedBy = new ProfileMedicalContactInfo()
		
	}


}
