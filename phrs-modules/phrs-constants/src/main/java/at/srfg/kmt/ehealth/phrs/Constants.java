/*
 * Project :iCardea
 * File : Constants.java
 * Encoding : UTF-8
 * Date : Jun 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Contains all the constants used in the PCC09 WS module.
 * The value for some contains can be configurated via a properties file named
 * 'configuration.properties' placed in the classpath.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class Constants {

    public static String COMMENT = "comment";

    public static String ICARDEA_NS = "http://www.icardea.at/phrs";

    public static String STATUS_NS = getICardeaResource("status");

    public static String STATUS_STATE = STATUS_NS + "#statusState";
    
    /**
     * This is an instance URI.
     */
    public static String STATUS_COMPELETE =
            getICardeaResource("status/instance/Complete");
    
    /**
     * This is an instance URI.
     */
    public static String STATUS_INCOMPELETE =
            getICardeaResource("status/instance/Incomplete");
    
    /**
     * This is an instance URI.
     */
    public static String STATUS_RUNNING =
            getICardeaResource("status/instance/Runnig");
    
    /**
     * This is an instance URI.
     */
    public static String STATUS_INTERRUPTED =
            getICardeaResource("status/instance/Interrupted");
    
    public static String CODE_NS = getICardeaResource("code");

    public static String CODE = CODE_NS + "#code";

    public static String CODE_NAME = CODE_NS + "#codeName";

    public static String CODE_SYSTEM = CODE_NS + "#codeSystem";

    public static String CODE_SYSTEM_NS = getICardeaResource("codeSystem");

    public static String CODE_SYSTEM_CODE = CODE_SYSTEM_NS + "#codeSystemCode";

    public static String CODE_SYSTEM_NAME = CODE_SYSTEM_NS + "#codeSystemName";

    /**
     * This is an instance URI.
     */
    public static String LOINC_CODE_SYSTEM =
            getICardeaResource("codeSystem/instance/LOINC");

    /**
     * This is an instance URI.
     */
    public static String SNOMED_CODE_SYSTEM =
            getICardeaResource("codeSystem/instance/SNOMED");

    /**
     * This is an instance URI.
     */
    public static String PHRS_CODE_SYSTEM =
            getICardeaResource("codeSystem/instance/PHRS");

    public static String BLOOD_PRESSURE_NS = getICardeaResource("bloodpressure");

    public static String BLOOD_PRESSURE_DIASTOLIC = BLOOD_PRESSURE_NS + "#bpDiastolic";

    public static String BLOOD_PRESSURE_SYSTOLIC = BLOOD_PRESSURE_NS + "#bpSystolic";

    public static String HEART_NS = getICardeaResource("heart");

    public static String HEART_RATE = HEART_NS + "#bpHeartRate";

    public static String OBSERVATION_DATE = "observationDate";

    public static String BODY_NS = getICardeaResource("body");

    public static String BODY_BMI = BODY_NS + "#bodyBMI";

    public static String BODY_WEIGHT = BODY_NS + "#bodyweight";

    public static String HEIGHT = BODY_NS + "#height";

    public static String MEASURE_SYSTEM_NS = getICardeaResource("mSystem");

    public static String MEASURE_SYSTEM_UNIT = MEASURE_SYSTEM_NS + "#mSystemValue";

    public static String MEASURE_SYSTEM_VALUE = MEASURE_SYSTEM_NS + "#mSystemUnit";

    public static String MEASURE_SYSTEM_QUANTITY = MEASURE_SYSTEM_NS + "#mSystemQuantity";
    
    /**
     * This is an instance URI.
     */
    public static String MM_HG =
            getICardeaResource("MeasureSystem/instance/MmHg");
    
    /**
     * This is an instance URI.
     */
    public static String MILLIGRAMS =
            getICardeaResource("MeasureSystem/instance/Milligram");
    
    /**
     * This is an instance URI.
     */
    public static String TABLET =
            getICardeaResource("MeasureSystem/instance/Tablet");
    
    /**
     * This is an instance URI.
     */
    public static String PILL =
            getICardeaResource("MeasureSystem/instance/Pill");
    
    /**
     * This is an instance URI.
     */
    public static String CENTIMETER =
            getICardeaResource("MeasureSystem/instance/Centimeter");
    
    /**
     * This is an instance URI.
     */
    public static String METER =
            getICardeaResource("MeasureSystem/instance/Meter");
    
    /**
     * This is an instance URI.
     */
    public static String KILOGRAM =
            getICardeaResource("MeasureSystem/instance/Kilogram");
    
    /**
     * This is an instance URI.
     */
    public static String GRAM =
            getICardeaResource("MeasureSystem/instance/Gram");
    

    public static String ACTIVITY_NS = getICardeaResource("activity");

    public static String ACTIVITY_CATEGORY = ACTIVITY_NS + "#activityCategory";

    public static String ACTIVITY_ASSEMENT_INDICATOR = ACTIVITY_NS + "#assessmentIndicator";

    public static String ACTIVITY_DURATION_CODE = ACTIVITY_NS + "#activityDurationCode";

    public static String ACTIVITY_FEATURE = ACTIVITY_NS + "#activityFeature";

    public static String ACTIVITY_FREQUENCY_CODE = ACTIVITY_NS + "#activityFrequencyCode";

    public static String ACTIVITY_MOD_INDICATOR = ACTIVITY_NS + "#activityMoodIndicator";

    public static String SCORE = "score";

    public static String VALUE_ACTIVITY = "valueActivity";

    public static String MEDICATION_NS = getICardeaResource("medications");

    public static String MEDICATION_ACTIVITY = MEDICATION_NS + "#medicationActivity";

    public static String MEDICATION_CODE = MEDICATION_NS + "#medicationCode";

    public static String MEDICATION_FREQUENCY_INTERVAL = MEDICATION_NS + "#medicationFrequencyInterval";

    public static String MEDICATION_FREQUENCY_TIMEOFDAY = MEDICATION_NS + "medicationFrequencyTimeOfDay";

    public static String MEDICATION_FREQUENCY_QUANTITY = MEDICATION_NS + "medicationFrequencyQuantity";

    public static String MEDICATION_NAME_TEXT = MEDICATION_NS + "#medicationNameText";

    public static String MEDICATION_REASON_COMMENT = MEDICATION_NS + "#medicationReasonComment";

    public static String MEDICATION_REASON_KEYWORD_CODES = MEDICATION_NS + "#medicationReasonKeywordCodes";

    public static String MEDICATION_REASON_PRIMARY_KEYWORD = MEDICATION_NS + "#medicationReasonPrimaryKeywordCode";

    public static String OBSERVATION_DATE_START = MEDICATION_NS + "#observationDateStart";

    public static String OBSERVATION_DATE_END = MEDICATION_NS + "#observationDateEnd";

    public static String PRESCRIBED_BY = MEDICATION_NS + "#prescribedByPerson";

    public static String PRESCRIBED_BY_ROLE = MEDICATION_NS + "#prescribedByRole";

    public static String MANUFACTURED_PRODUCT_CLASS_CODE =
            MEDICATION_NS + "manufacturedProductClassCode";

    public static String MANUFACTURED_LABELED_DRUG_CLASS_CODE =
            MEDICATION_NS + "#manufacturedLabeledDrugClassCode";

    public static String MANUFACTURED_LABELED_DRUG_DETERMINER_CODE =
            MEDICATION_NS + "#manufacturedLabeledDrugDeterminerCode";

    public static String DRUG_CODE_SYSTEM = MEDICATION_NS + "#drugCodeSystem";

    public static String DRUG_CODE_SYSTEM_NAME = MEDICATION_NS + "#drugCodeSystemName";

    public static String DRUG_CODE = MEDICATION_NS + "#drugCode";

    public static String DRUG_TEXT = MEDICATION_NS + "#drugText";

    public static String RISK_NS = getICardeaResource("risk");

    public static String RISK_FACTOR_TYPE = RISK_NS + "#riskFactorType";

    public static String RISK_FACTOR_CODE = RISK_NS + "#riskFactorCode";

    public static String RISK_FACTOR_DURATION = RISK_NS + "#riskFactorDuration";

    public static String HAS_CONTRIBUTION_FACTORS = RISK_NS + "#hasContributingFactors";

    public static String CONTRIBUTION_FACTORS_CODES = RISK_NS + "#contributingFactorCodes";

    public static String RISK_FACTRORS_ATTRIBUTES = RISK_NS + "#riskFactorAttributes";

    public static String IS_TREADED = RISK_NS + "#isTreated";

    public static String TREATMENTS_CODES = RISK_NS + "#treatmentStatmentCodes";

    public static String TRATMENTS_STATEMENT_PRIMARY = RISK_NS + "#treatmentStatementPrimary";

    public static String MOOD_INDICATOR = RISK_NS + "#moodIndicator";

    /**
     * Used to load the default values for the constants - if this is required.
     */
    static {
        final ClassLoader classLoader = Constants.class.getClassLoader();
        final InputStream stream =
                classLoader.getResourceAsStream("constants.properties");
        final Properties properties = new Properties();
        if (stream != null) {
            try {
                properties.load(stream);
            } catch (IOException ioException) {
                // I don't care. I use the default
            }
        }
    }

    private static String getICardeaResource(String resouce) {
        final StringBuilder result = new StringBuilder(ICARDEA_NS);
        result.append("/");
        result.append(resouce);

        return result.toString();
    }

    private static String getICardeaResource(String resouce, String separator) {
        final StringBuilder result = new StringBuilder(ICARDEA_NS);
        result.append(separator);
        result.append(resouce);

        return result.toString();
    }
}
