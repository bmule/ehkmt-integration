/*
 * Project :iCardea
 * File : Constants.java 
 * Encoding : UTF-8
 * Date : Apr 12, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.datamodel.impl;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class Constants {
 
    
    /**
     * Don't let anybody to instantiate this class.
     */
    private Constants() {
        // UNIMPLEMENTED
    }
    
    public static String ACTIVITY_OF_DAILY_LIVING_CLASS_URI = 
            "at.srfg.kmt.ehealth.phrs.datamodel.impl.ActivityOfDailyLiving";
    public static String ACTIVITY_OF_DAILY_LIVING_CLASS_NAME = "Activity Of Daily Living";

    public static String BLOOD_PREASURE_CLASS_URI = 
            "at.srfg.kmt.ehealth.phrs.datamodel.impl.BloodPressure";
    public static String BLOOD_PREASURE_CLASS_NAME = "Blood Pressure";
    
    public static String BODY_WEIGHT_CLASS_URI = 
            "at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight";
    public static String BODY_WEIGHT_CLASS_NAME = "Body Weight";
    
    public static String MEDICATION_CLASS_URI = 
            "at.srfg.kmt.ehealth.phrs.datamodel.impl.Medication";
    public static String MEDICATION_CLASS_NAME = "MEDICATION";
    
    public static String CLASS_URI = "class_uri"; 
    public static String BLOOD_PREASURE_DIASTOLIC = "bpDiastolic"; 
    public static String BLOOD_PREASURE_SYSTOLIC = "bpSystolic"; 
    public static String HEART_RATE = "bpHeartRate"; 
    public static String OBSERVATION_DATE = "observationDate"; 
    public static String BODY_BMI = "bodyBMI"; 
    public static String BODY_WEIGHT = "bodyweight"; 
    public static String HEIGHT = "height"; 
    public static String MEASURE_SYSTEM = "msystem"; 
    
    public static String ACTIVITY_CATEGORY = "activityCategory"; 
    public static String ACTIVITY_CODE = "activityCode"; 
    public static String ACTIVITY_DURATION_CODE = "activityDurationCode"; 
    public static String ACTIVITY_FEATURE = "activityFeature"; 
    public static String IS_ACTIVITY_ASSISTED = "isActivityAssisted"; 
    public static String IS_ACTIVITY_UNASSISTED = "isActivityUnassisted"; 
    public static String SCORE = "score"; 
    public static String VALUE_ACTIVITY = "valueActivity"; 
    
    public static String MEDICATION_ACTIVITY = "medicationActivity"; 
    public static String MEDICATION_CODE = "medicationCode"; 
    public static String MEDICATION_FREQENCY_INTERVAL = "medicationFrequencyInterval"; 
    public static String MEDICATION_FREQENCY_TIMEOFDAY = "medicationFrequencyTimeOfDay"; 
    public static String MEDICATION_NAME_TEXT = "medicationNameText"; 
    public static String MEDICATION_QUANTITY = "medicationQuantity"; 
    public static String MEDICATION_QUANTITY_UNIT = "medicationQuantityUnit"; 
    public static String MEDICATION_REASON_COMMENT = "medicationReasonComment"; 
    public static String MEDICATION_REASON_KEYWORD_CODE = "medicationReasonKeywordCodes"; 
    public static String MEDICATION_REASON_PRIMARY_KEYWORD = "medicationReasonPrimaryKeywordCode"; 
    public static String MEDICATION_STATUS = "medicationStatus"; 
    public static String OBSERVATION_DATE_START = "observationDateStart"; 
    public static String OBSERVATION_DATE_END = "observationDateEnd"; 
    public static String PRESCRIBED_BY = "prescribedByPerson"; 
    public static String PRESCRIBED_BY_ROLE = "prescribedByRole"; 
    
    
}
