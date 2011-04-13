/*
 * Project :iCardea
 * File : ModelClassFactory.java 
 * Encoding : UTF-8
 * Date : Apr 12, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.datamodel.impl;


import static at.srfg.kmt.ehealth.phrs.datamodel.impl.Constants.*;
import java.util.Date;
import java.util.HashSet;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Builds severals map used to build dynamic classes.
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public final class ModelClassFactory {

    /**
     * Don't let anybody to instantiate this class.
     * 
     */
    private ModelClassFactory() {
        // UNIMPLEMENETD
    }

    /**
     * Builds a map used to describe the Blood Pressure Observation class.
     * 
     * @return a map used to describe the Blood Pressure Observation class.
     */
    public static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createBloodPressureModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();

        final DynamicPropertyType classUriProperty = new DynamicPropertyType();
        classUriProperty.setName(CLASS_URI);
        classUriProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> classUriMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(classUriProperty, classUriMetadatas);


        final DynamicPropertyType diastolicProperty = new DynamicPropertyType();
        diastolicProperty.setName(BLOOD_PREASURE_DIASTOLIC);
        diastolicProperty.setType(Long.class.getName());
        final Set<DynamicPropertyMetadata> diastolicMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(diastolicProperty, diastolicMetadatas);


        final DynamicPropertyType systolicProperty = new DynamicPropertyType();
        systolicProperty.setName(BLOOD_PREASURE_SYSTOLIC);
        systolicProperty.setType(Long.class.getName());
        final Set<DynamicPropertyMetadata> systolicMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(systolicProperty, systolicMetadatas);


        final DynamicPropertyType heartRateProperty = new DynamicPropertyType();
        heartRateProperty.setName(HEART_RATE);
        heartRateProperty.setType(Integer.class.getName());
        final Set<DynamicPropertyMetadata> heartRateMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(heartRateProperty, heartRateMetadatas);


        final DynamicPropertyType obsDateProperty = new DynamicPropertyType();
        obsDateProperty.setName(OBSERVATION_DATE);
        obsDateProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> obsDateMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(obsDateProperty, obsDateMetadatas);

        return result;
    }

    /**
     * Builds a map used to describe the Body Weigh class.
     * 
     * @return a map used to describe the Body Weigh class.
     */
    public static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createBodyWeighModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();

        final DynamicPropertyType classUriProperty = new DynamicPropertyType();
        classUriProperty.setName(CLASS_URI);
        classUriProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> classUriMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(classUriProperty, classUriMetadatas);

        final DynamicPropertyType bodyBMIProperty = new DynamicPropertyType();
        bodyBMIProperty.setName(BODY_BMI);
        bodyBMIProperty.setType(Double.class.getName());
        final Set<DynamicPropertyMetadata> bodyBMIMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(bodyBMIProperty, bodyBMIMetadatas);

        final DynamicPropertyType bodyWeightProperty = new DynamicPropertyType();
        bodyWeightProperty.setName(BODY_WEIGHT);
        bodyWeightProperty.setType(Double.class.getName());
        final Set<DynamicPropertyMetadata> bodyWeightMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(bodyWeightProperty, bodyWeightMetadatas);

        final DynamicPropertyType heightProperty = new DynamicPropertyType();
        heightProperty.setName(HEIGHT);
        heightProperty.setType(Double.class.getName());
        final Set<DynamicPropertyMetadata> heightMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(heightProperty, heightMetadatas);

        final DynamicPropertyType measureSystemProperty = new DynamicPropertyType();
        measureSystemProperty.setName(MEASURE_SYSTEM);
        measureSystemProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> measureSystemMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(measureSystemProperty, measureSystemMetadatas);


        return result;
    }

    /**
     * Proves if the specified name is a legal BodyWeigh class property. 
     * 
     * @param propertyName the property to prove.
     * @return true if the specified name is a legal BodyWeigh class property.
     */
    public static boolean isBodyWeighProperty(String propertyName) {

        final Set<String> filer = new HashSet<String>();
        filer.add(Constants.BODY_BMI);
        filer.add(Constants.BODY_WEIGHT);
        filer.add(Constants.HEIGHT);
        filer.add(Constants.MEASURE_SYSTEM);
        
        return filer.contains(propertyName);
    }
    

    /**
     * Builds a map used to describe the Activity of Daily Living class.
     * 
     * @return a map used to describe the Activity of Daily Living  class.
     */
    public static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createActivityOfDailyLivingModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();

        final DynamicPropertyType classUriProperty = new DynamicPropertyType();
        classUriProperty.setName(CLASS_URI);
        classUriProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> classUriMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(classUriProperty, classUriMetadatas);

        final DynamicPropertyType activityCategoryProperty = new DynamicPropertyType();
        activityCategoryProperty.setName(ACTIVITY_CATEGORY);
        activityCategoryProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> activityCategoryMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityCategoryProperty, activityCategoryMetadatas);

        final DynamicPropertyType activityDurationCodeProperty = new DynamicPropertyType();
        activityDurationCodeProperty.setName(ACTIVITY_DURATION_CODE);
        activityDurationCodeProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> activityDurationCodeMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityDurationCodeProperty, activityDurationCodeMetadatas);

        final DynamicPropertyType activityFeatureProperty = new DynamicPropertyType();
        activityFeatureProperty.setName(ACTIVITY_FEATURE);
        activityFeatureProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> activityFeatureMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityFeatureProperty, activityFeatureMetadatas);

        final DynamicPropertyType isActivityAssistedProperty = new DynamicPropertyType();
        isActivityAssistedProperty.setName(IS_ACTIVITY_ASSISTED);
        isActivityAssistedProperty.setType(Boolean.class.getName());
        final Set<DynamicPropertyMetadata> isActivityAssistedMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(isActivityAssistedProperty, isActivityAssistedMetadatas);

        final DynamicPropertyType scoreProperty = new DynamicPropertyType();
        scoreProperty.setName(SCORE);
        scoreProperty.setType(Integer.class.getName());
        final Set<DynamicPropertyMetadata> scorePropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(scoreProperty, scorePropertyMetadatas);

        final DynamicPropertyType valueActivityProperty = new DynamicPropertyType();
        valueActivityProperty.setName(VALUE_ACTIVITY);
        valueActivityProperty.setType(Integer.class.getName());
        final Set<DynamicPropertyMetadata> valueActivityMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(valueActivityProperty, valueActivityMetadatas);

        return result;
    }

    /**
     * Builds a map used to describe the Medication Activity.
     * 
     * @return a map used to describe the Medication Activity.
     */
    public static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createMedicationModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();

        final DynamicPropertyType classUriProperty = new DynamicPropertyType();
        classUriProperty.setName(CLASS_URI);
        classUriProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> classUriMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(classUriProperty, classUriMetadatas);

        final DynamicPropertyType medicationActivityProperty = new DynamicPropertyType();
        medicationActivityProperty.setName(MEDICATION_ACTIVITY);
        medicationActivityProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationActivityMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationActivityProperty, medicationActivityMetadatas);

        final DynamicPropertyType medicationCodeProperty = new DynamicPropertyType();
        medicationCodeProperty.setName(MEDICATION_CODE);
        medicationCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationCodeMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationCodeProperty, medicationCodeMetadatas);

        final DynamicPropertyType medicationFrequencyIntervalProperty = new DynamicPropertyType();
        medicationFrequencyIntervalProperty.setName(MEDICATION_FREQENCY_INTERVAL);
        medicationFrequencyIntervalProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationFrequencyIntervalMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationFrequencyIntervalProperty, medicationFrequencyIntervalMetadatas);

        final DynamicPropertyType medicationFrequencyProperty = new DynamicPropertyType();
        medicationFrequencyProperty.setName(MEDICATION_QUANTITY);
        medicationFrequencyProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationFrequencyQuantityMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationFrequencyProperty, medicationFrequencyQuantityMetadatas);

        final DynamicPropertyType medicationFrequencyTimeOfDayProperty = new DynamicPropertyType();
        medicationFrequencyTimeOfDayProperty.setName(MEDICATION_FREQENCY_TIMEOFDAY);
        medicationFrequencyTimeOfDayProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationFrequencyTimeOfDayMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationFrequencyTimeOfDayProperty, medicationFrequencyTimeOfDayMetadatas);

        final DynamicPropertyType medicationNameProperty = new DynamicPropertyType();
        medicationNameProperty.setName(MEDICATION_NAME_TEXT);
        medicationNameProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationNameMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationNameProperty, medicationNameMetadatas);

        final DynamicPropertyType medicationQuantityProperty = new DynamicPropertyType();
        medicationQuantityProperty.setName(MEDICATION_QUANTITY);
        medicationQuantityProperty.setType(Long.class.getName());
        final Set<DynamicPropertyMetadata> medicationQuantityMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationQuantityProperty, medicationQuantityMetadatas);

        final DynamicPropertyType medicationQuantityUnitProperty = new DynamicPropertyType();
        medicationQuantityUnitProperty.setName(MEDICATION_QUANTITY_UNIT);
        medicationQuantityUnitProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationQuantityunitMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationQuantityUnitProperty, medicationQuantityunitMetadatas);

        final DynamicPropertyType medicationReasonKeywordCodesProperty = new DynamicPropertyType();
        medicationReasonKeywordCodesProperty.setName(MEDICATION_REASON_KEYWORD_CODE);
        medicationReasonKeywordCodesProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationReasonKeywordCodesMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationReasonKeywordCodesProperty, medicationReasonKeywordCodesMetadatas);

        final DynamicPropertyType medicationStatusProperty = new DynamicPropertyType();
        medicationStatusProperty.setName(MEDICATION_REASON_KEYWORD_CODE);
        medicationStatusProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationStatusMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationStatusProperty, medicationStatusMetadatas);

        final DynamicPropertyType dateStartProperty = new DynamicPropertyType();
        dateStartProperty.setName(OBSERVATION_DATE_START);
        dateStartProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> dateStartMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(dateStartProperty, dateStartMetadatas);

        final DynamicPropertyType dateEndProperty = new DynamicPropertyType();
        dateEndProperty.setName(OBSERVATION_DATE_START);
        dateEndProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> dateEndMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(dateEndProperty, dateEndMetadatas);

        final DynamicPropertyType prescribedByPersonProperty = new DynamicPropertyType();
        prescribedByPersonProperty.setName(PRESCRIBED_BY);
        prescribedByPersonProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> prescribedByPersonMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(prescribedByPersonProperty, prescribedByPersonMetadatas);

        final DynamicPropertyType prescribedByRoleProperty = new DynamicPropertyType();
        prescribedByRoleProperty.setName(PRESCRIBED_BY_ROLE);
        prescribedByRoleProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> prescribedByRoleMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(prescribedByRoleProperty, prescribedByRoleMetadatas);

        return result;
    }
}
