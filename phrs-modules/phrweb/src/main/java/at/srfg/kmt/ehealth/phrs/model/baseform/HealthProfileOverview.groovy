package at.srfg.kmt.ehealth.phrs.model.baseform

import java.util.Date

import at.srfg.kmt.ehealth.phrs.presentation.utils.HealthyUtils

import com.google.code.morphia.annotations.Entity
/**
 * 
 * Only one per ownerUri = healthProfileId
 *
 */
@Entity
public class HealthProfileOverview extends BasePhrsModel{
	
	Double bodyWeight
	Double bmi
	String mUnitWeight
	Date   dateWeight
	
	Double bodyHeight
	String mUnitHeight
	Date   dateHeight
	
	
	Integer systolic
	Integer diastolic
	Date   dateBloodPressure
	
	Integer heartRate
	Date   dateHeartRate
	
	Map<String, String> property
	Map<String, Collection<String>> propertyList
	
	Map<String, Double> thresholdValues
	Map<String, Date> thresholdDates
	
	public void latestBodyWeight(Integer heartRate, Date date){
		this.heartRate=heartRate
		this.dateHeartRate = date
}
	public void latestBodyWeight(Double bodyWeight, Date date){
			this.bodyWeight=bodyWeight
			this.dateWeight = date
	}
	public void latestBodyHeight(Double bodyHeight, Date date){
		this.bodyHeight=bodyHeight
		this.dateHeight = date
	}
	
	public static final THRESHOLD_SYSTOLIC 		= 'THRESHOLD_SYSTOLIC'
	public static final THRESHOLD_DIASTOLIC 	= 'THRESHOLD_DIASTOLIC'
	public static final THRESHOLD_WEIGHT 		= 'THRESHOLD_WEIGHT'
	public static final THRESHOLD_HEART_RATE 	= 'THRESHOLD_HEART_RATE'
	
	public static final THRESHOLD_DATE_WEIGHT_GOAL	= 'THRESHOLD_DATE_WEIGHT_GOAL'
	public static final THRESHOLD_DATE_BP_GOAL	= 'THRESHOLD_DATE_BP_GOAL'
	public static final THRESHOLD_DATE_HR_GOAL	= 'THRESHOLD_DATE_HR_GOAL'

		
	public void addThresholdValue(String key, Double threshold){
		if(key && threshold){
			this.thresholdValues.put(key, threshold)
		}
	}
		
	public Double getThresholdValue(String key){
		return key && thresholdValues.containsKey(key) ? thresholdValues.get(key) : null	
	}


	public Double getThresholdDate(String key){
		return key && thresholdDates.containsKey(key) ? thresholdDates.get(key) : null
	}
	
	public HealthProfileOverview(){
		super()

		property = new HashMap<String, String>()
		propertyList = new HashMap<String<Collection>>()
		
		thresholdValues = new HashMap<String, Double>()
		thresholdDates = new HashMap<String, Date>()
	}
	
	public Double getBodyBmi(){
		Double val = HealthyUtils.computeBMIMetric(bodyWeight,  bodyHeight)
		if(!val) val= 0d
		return val
	}
	
	public  ObsVitalsBodyWeight getLatestBodyWeightObject(String theOwnerUri){
		ObsVitalsBodyWeight obj= HealthyUtils.getCurrentWeightObjectFromAllEntries(theOwnerUri)
		return obj
	}
	
	public  ObsVitalsBloodPressure getLatestBLoodPressureObject(String theOwnerUri){
		ObsVitalsBloodPressure obj= HealthyUtils.getCurrentBloodPressureObjectFromAllEntries(theOwnerUri)
		return obj
	}
	

	

}
