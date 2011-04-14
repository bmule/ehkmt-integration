/*
 * Project :iCardea
 * File : ModelClassFactory.java 
 * Encoding : UTF-8
 * Date : Apr 12, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.model;


import static at.srfg.kmt.ehealth.phrs.dataexchange.model.Constants.*;
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
     * Builds a map used to describe the Blood Pressure Observation class. This 
     * class contains only the following properties :
     * <ul>
     * <li>CLASS_URI - type : java.lang.String
     * <li>BLOOD_PREASURE_DIASTOLIC : java.lang.String
     * <li>BLOOD_PREASURE_SYSTOLIC : java.lang.String
     * <li>HEART_RATE : java.lang.String
     * <li>OBSERVATION_DATE : java.lang.String
     * <li>
     * </ul>
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
        diastolicProperty.setType(Integer.class.getName());
        final Set<DynamicPropertyMetadata> diastolicMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(diastolicProperty, diastolicMetadatas);


        final DynamicPropertyType systolicProperty = new DynamicPropertyType();
        systolicProperty.setName(BLOOD_PREASURE_SYSTOLIC);
        systolicProperty.setType(Integer.class.getName());
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

        final DynamicPropertyType commentProperty = new DynamicPropertyType();
        commentProperty.setName(COMMENT);
        commentProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> commentMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(commentProperty, commentMetadatas);


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

        final DynamicPropertyType observationDate = new DynamicPropertyType();
        observationDate.setName(OBSERVATION_DATE);
        observationDate.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> observationDateMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(observationDate, observationDateMetadatas);

        final DynamicPropertyType commentProperty = new DynamicPropertyType();
        commentProperty.setName(COMMENT);
        commentProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> commentMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(commentProperty, commentMetadatas);


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

        final DynamicPropertyType activityCodeProperty = new DynamicPropertyType();
        activityCodeProperty.setName(ACTIVITY_CODE);
        activityCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> activityCodeMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityCodeProperty, activityCodeMetadatas);

        final DynamicPropertyType activityDurationCodeProperty = new DynamicPropertyType();
        activityDurationCodeProperty.setName(ACTIVITY_DURATION_CODE);
        activityDurationCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> activityDurationCodeMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityDurationCodeProperty, activityDurationCodeMetadatas);

        final DynamicPropertyType isActivityAssistedProperty = new DynamicPropertyType();
        isActivityAssistedProperty.setName(IS_ACTIVITY_ASSISTED);
        isActivityAssistedProperty.setType(Boolean.class.getName());
        final Set<DynamicPropertyMetadata> isActivityAssistedMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(isActivityAssistedProperty, isActivityAssistedMetadatas);

        final DynamicPropertyType scoreProperty = new DynamicPropertyType();
        scoreProperty.setName(SCORE);
        scoreProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> scorePropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(scoreProperty, scorePropertyMetadatas);

        final DynamicPropertyType valueActivityProperty = new DynamicPropertyType();
        valueActivityProperty.setName(VALUE_ACTIVITY);
        valueActivityProperty.setType(String.class.getName());
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
        medicationQuantityProperty.setType(String.class.getName());
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
        medicationReasonKeywordCodesProperty.setName(MEDICATION_REASON_KEYWORD_CODES);
        medicationReasonKeywordCodesProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationReasonKeywordCodesMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationReasonKeywordCodesProperty, medicationReasonKeywordCodesMetadatas);

        final DynamicPropertyType medicationReasonPrimaryKeywordCodeProperty = new DynamicPropertyType();
        medicationReasonPrimaryKeywordCodeProperty.setName(MEDICATION_REASON_PRIMARY_KEYWORD);
        medicationReasonPrimaryKeywordCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> medicationReasonPrimaryKeywordCodeMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(medicationReasonPrimaryKeywordCodeProperty, medicationReasonPrimaryKeywordCodeMetadatas);

        final DynamicPropertyType medicationStatusProperty = new DynamicPropertyType();
        medicationStatusProperty.setName(MEDICATION_STATUS);
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
        dateEndProperty.setName(OBSERVATION_DATE_END);
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

        final DynamicPropertyType commentProperty = new DynamicPropertyType();
        commentProperty.setName(COMMENT);
        commentProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> commentMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(commentProperty, commentMetadatas);


        return result;
    }

    /**
     * Builds a map used to describe the Medication Activity.
     * 
     * @return a map used to describe the Medication Activity.
     */
    public static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createProblemsModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();

        final DynamicPropertyType issueTypeCodeProperty = new DynamicPropertyType();
        issueTypeCodeProperty.setName(ISSUE_TYPE_CODE);
        issueTypeCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> issueTypeCodePropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(issueTypeCodeProperty, issueTypeCodePropertyMetadatas);

        final DynamicPropertyType issueCodeProperty = new DynamicPropertyType();
        issueCodeProperty.setName(ISSUE_CODE);
        issueCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> issueCodePropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(issueCodeProperty, issueCodePropertyMetadatas);

        final DynamicPropertyType isActiveStatusProperty = new DynamicPropertyType();
        isActiveStatusProperty.setName(IS_ACTIVE);
        isActiveStatusProperty.setType(Boolean.class.getName());
        final Set<DynamicPropertyMetadata> isActiveStatusMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(isActiveStatusProperty, isActiveStatusMetadatas);


        final DynamicPropertyType dateStartProperty = new DynamicPropertyType();
        dateStartProperty.setName(OBSERVATION_DATE_START);
        dateStartProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> dateStartMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(dateStartProperty, dateStartMetadatas);

        final DynamicPropertyType dateEndProperty = new DynamicPropertyType();
        dateEndProperty.setName(OBSERVATION_DATE_END);
        dateEndProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> dateEndMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(dateEndProperty, dateEndMetadatas);


        final DynamicPropertyType commentProperty = new DynamicPropertyType();
        commentProperty.setName(COMMENT);
        commentProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> commentMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(commentProperty, commentMetadatas);


        return result;
    }

    public static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createRiskFactorModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();

        final DynamicPropertyType riskFactorTypeProperty = new DynamicPropertyType();
        riskFactorTypeProperty.setName(RISK_FACTOR_TYPE);
        riskFactorTypeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> riskFactorTypeMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(riskFactorTypeProperty, riskFactorTypeMetadatas);

        final DynamicPropertyType riskFactorCodeProperty = new DynamicPropertyType();
        riskFactorCodeProperty.setName(RISK_FACTOR_TYPE);
        riskFactorCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> riskFactorCodeMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(riskFactorCodeProperty, riskFactorCodeMetadatas);

        final DynamicPropertyType isActiveProperty = new DynamicPropertyType();
        isActiveProperty.setName(IS_ACTIVE);
        isActiveProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> risActivePropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(isActiveProperty, risActivePropertyMetadatas);

        final DynamicPropertyType riskFactorDurationProperty = new DynamicPropertyType();
        riskFactorDurationProperty.setName(IS_ACTIVE);
        riskFactorDurationProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> riskFactorDurationMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(riskFactorDurationProperty, riskFactorDurationMetadatas);


        final DynamicPropertyType hasContrFactorsProperty = new DynamicPropertyType();
        hasContrFactorsProperty.setName(HAS_CONTRIBUTION_FACTORS);
        hasContrFactorsProperty.setType(Boolean.class.getName());
        final Set<DynamicPropertyMetadata> hasContrFactorsMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(hasContrFactorsProperty, hasContrFactorsMetadatas);

        final DynamicPropertyType treatmentsCodesProperty = new DynamicPropertyType();
        treatmentsCodesProperty.setName(TREATMENTS_CODES);
        treatmentsCodesProperty.setType(HashSet.class.getName());
        final Set<DynamicPropertyMetadata> treatmentsCodesPropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(treatmentsCodesProperty, treatmentsCodesPropertyMetadatas);

        final DynamicPropertyType treatmentStatementPrimaryProperty = new DynamicPropertyType();
        treatmentStatementPrimaryProperty.setName(TRATMENTS_STATEMENT_PRIMARY);
        treatmentStatementPrimaryProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> treatmentStatementPrimaryPropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(treatmentStatementPrimaryProperty, treatmentStatementPrimaryPropertyMetadatas);

        final DynamicPropertyType dateStartProperty = new DynamicPropertyType();
        dateStartProperty.setName(OBSERVATION_DATE_START);
        dateStartProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> dateStartMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(dateStartProperty, dateStartMetadatas);

        final DynamicPropertyType dateEndProperty = new DynamicPropertyType();
        dateEndProperty.setName(OBSERVATION_DATE_END);
        dateEndProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> dateEndMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(dateEndProperty, dateEndMetadatas);

        final DynamicPropertyType commentProperty = new DynamicPropertyType();
        commentProperty.setName(COMMENT);
        commentProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> commentMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(commentProperty, commentMetadatas);


        return result;
    }

    public static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createActivityItemModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();


        final DynamicPropertyType activityCategoryProperty = new DynamicPropertyType();
        activityCategoryProperty.setName(ACTIVITY_CATEGORY);
        activityCategoryProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> activityCategoryPropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityCategoryProperty, activityCategoryPropertyMetadatas);

        final DynamicPropertyType activityCodeProperty = new DynamicPropertyType();
        activityCodeProperty.setName(ACTIVITY_CODE);
        activityCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> activityCodePropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityCodeProperty, activityCodePropertyMetadatas);

        final DynamicPropertyType activityNameProperty = new DynamicPropertyType();
        activityNameProperty.setName(ACTIVITY_NAME);
        activityNameProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> activityNameMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityNameProperty, activityNameMetadatas);

        final DynamicPropertyType assessmentIndicatorProperty = new DynamicPropertyType();
        assessmentIndicatorProperty.setName(ACTIVITY_NAME);
        assessmentIndicatorProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> assessmentIndicatorPropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(assessmentIndicatorProperty, assessmentIndicatorPropertyMetadatas);

        final DynamicPropertyType isActiveStatusProperty = new DynamicPropertyType();
        isActiveStatusProperty.setName(IS_ACTIVE);
        isActiveStatusProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> isActiveStatusMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(isActiveStatusProperty, isActiveStatusMetadatas);

        final DynamicPropertyType activityFeatureProperty = new DynamicPropertyType();
        activityFeatureProperty.setName(ACTIVITY_FEATURE);
        activityFeatureProperty.setType(HashMap.class.getName());
        final Set<DynamicPropertyMetadata> activityFeatureMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityFeatureProperty, activityFeatureMetadatas);

        final DynamicPropertyType activityFrequencyCodeProperty = new DynamicPropertyType();
        activityFrequencyCodeProperty.setName(ACTIVITY_FREQUENCY_FEATURE);
        activityFrequencyCodeProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> activityFrequencyCodeMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityFrequencyCodeProperty, activityFrequencyCodeMetadatas);

        final DynamicPropertyType dateStartProperty = new DynamicPropertyType();
        dateStartProperty.setName(OBSERVATION_DATE_START);
        dateStartProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> dateStartMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(dateStartProperty, dateStartMetadatas);

        final DynamicPropertyType dateEndProperty = new DynamicPropertyType();
        dateEndProperty.setName(OBSERVATION_DATE_END);
        dateEndProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> dateEndMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(dateEndProperty, dateEndMetadatas);

        final DynamicPropertyType commentProperty = new DynamicPropertyType();
        commentProperty.setName(COMMENT);
        commentProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> commentMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(commentProperty, commentMetadatas);

        return result;
    }

    public static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createActivityLevelModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();

        final DynamicPropertyType activityLevelIndicatorProperty = new DynamicPropertyType();
        activityLevelIndicatorProperty.setName(ACTIVITY_MOD_INDICATOR);
        activityLevelIndicatorProperty.setType(Integer.class.getName());
        final Set<DynamicPropertyMetadata> activityLevelIndicatorMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityLevelIndicatorProperty, activityLevelIndicatorMetadatas);

        final DynamicPropertyType activityMoodIndicatorProperty = new DynamicPropertyType();
        activityMoodIndicatorProperty.setName(ACTIVITY_MOD_INDICATOR);
        activityMoodIndicatorProperty.setType(Integer.class.getName());
        final Set<DynamicPropertyMetadata> activityMoodIndicatorMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(activityMoodIndicatorProperty, activityMoodIndicatorMetadatas);

        final DynamicPropertyType commentProperty = new DynamicPropertyType();
        commentProperty.setName(COMMENT);
        commentProperty.setType(String.class.getName());
        final Set<DynamicPropertyMetadata> commentMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(commentProperty, commentMetadatas);

        final DynamicPropertyType obsDateProperty = new DynamicPropertyType();
        obsDateProperty.setName(OBSERVATION_DATE);
        obsDateProperty.setType(Date.class.getName());
        final Set<DynamicPropertyMetadata> obsDateDateMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        result.put(obsDateProperty, obsDateDateMetadatas);




        return result;
    }
}
