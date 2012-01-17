package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date


import com.google.code.morphia.annotations.Entity

@Entity
public class ObsVitalSummary extends BasePhrsModel{
	
	Double bodyWeight
	Double bmi
	String mUnitWeight
	Date   dateWeight
	
	Double bodyHeight
	String mUnitHeight
	Date   dateHeight
	
	
	Integer systolic
	Integer diastolic
	Integer heartRate
	Date   dateBloodPressure
	
	
	
	
	public ObsVitalSummary(){
		super()
	}
}
