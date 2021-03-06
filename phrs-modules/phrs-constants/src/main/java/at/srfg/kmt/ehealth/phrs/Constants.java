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
 * Contains all the constants used in the PCC09 WS module. The value for some
 * contains can be configurated via a properties file named
 * 'configuration.properties' placed in the classpath.
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class Constants {

    public static final String ICARDEA_NS = "http://www.icardea.at/phrs";
    /**
     * This is an instance URI.
     */
    public static final String STATUS_COMPELETE =
            ICARDEA_NS + "/instances/Complete";
    /**
     * This is an instance URI.
     */
    public static final String STATUS_INCOMPELETE =
            ICARDEA_NS + "/instances/Incomplete";
    /**
     * This is an instance URI.
     */
    public static final String STATUS_RUNNING =
            ICARDEA_NS + "/instances/Runnig";
    /**
     * This is an instance URI.
     */
    public static final String STATUS_INTERRUPTED =
            ICARDEA_NS + "/instances/Interrupted";
    public static final String STATUS_ABORTED =
            ICARDEA_NS + "/instances/Aborted";
    public static final String STATUS_SUSPENDED =
            ICARDEA_NS + "/instances/Suspended";
    public static final String STATUS_ACTIVE =
            ICARDEA_NS + "/instances/Active";
    public static final String CODE_NS = getICardeaResource("code");
    public static final String CODE = CODE_NS + "#code";
    public static final String CODE_NAME = CODE_NS + "#codeName";
    public static final String CODE_SYSTEM = CODE_NS + "#codeSystem";
    public static final String CODE_VALUE = CODE_NS + "#codeValue";
    public static final String CODE_SYSTEM_NS = getICardeaResource("codeSystem");
    public static final String CODE_SYSTEM_CODE = CODE_SYSTEM_NS + "#codeSystemCode";
    public static final String CODE_SYSTEM_NAME = CODE_SYSTEM_NS + "#codeSystemName";
    /**
     * This is an instance URI.
     */
    public static final String LOINC_CODE_SYSTEM =
            getICardeaResource("instances/codeSystem/Loinc");
    /**
     * This is an instance URI.
     */
    public static final String SNOMED_CODE_SYSTEM =
            getICardeaResource("instances/codeSystem/Snomed");
    /**
     * This is an instance URI.
     */
    public static final String PHRS_CODE_SYSTEM =
            getICardeaResource("instances/codeSystem/PHRS");
    public static final String ICARDEA_INSTANCES_NS = ICARDEA_NS + "/instances";
    public static final String ICARDEA_INSTANCES_MESURE_SYSTEM_NS =
            ICARDEA_INSTANCES_NS + "/MeasureSystem";
    /**
     * This is an instance URI.
     */
    public static final String MM_HG = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#MmHg";
    /**
     * This is an instance URI.
     */
    public static final String MILLIGRAM = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Milligram";
    /**
     * This is an instance URI.
     */
    public static final String DROPS = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Drops";
    /**
     * This is an instance URI.
     */
    public static final String TABLET = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Tablet";
    /**
     * This is an instance URI.
     */
    public static final String PILL = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Tablet";
    /**
     * This is an instance URI.
     */
    public static final String CENTIMETER = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Centimeter";
    /**
     * This is an instance URI.
     */
    public static final String METER = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Meter";
    /**
     * This is an instance URI.
     */
    public static final String KILOGRAM = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Kilogram";
    /**
     * This is an instance URI.
     */
    public static final String GRAM = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Gram";
    
    /**
     * This is an instance URI.
     */
    public static final String BPS = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#BPS";

    
    public static final String ICARDEA_HL7V3_NS = ICARDEA_NS + "/hl7V3";
    
    
    /**
     * The Root template Id property.
     */
    public static final String HL7V3_TEMPLATE_ID_ROOT = ICARDEA_HL7V3_NS + "#templIdRoot";
    /**
     * The establish code property.
     */
    public static final String HL7V3_CODE = ICARDEA_HL7V3_NS + "#code";
    public static final String EFFECTIVE_TIME = ICARDEA_HL7V3_NS + "#effectiveTime";
    public static final String HL7V3_VALUE = ICARDEA_HL7V3_NS + "#value";
    public static final String HL7V3_UNIT = ICARDEA_HL7V3_NS + "#unit";
    public static final String HL7V3_START_DATE = ICARDEA_HL7V3_NS + "#startDate";
    public static final String HL7V3_END_DATE = ICARDEA_HL7V3_NS + "#endDate";
    public static final String HL7V3_VALUE_CODE = ICARDEA_HL7V3_NS + "#valueCode";
    /**
     * The URI for the instance used to define Systolic Blood Pressure in IHE
     * acceptation.<br/> This is a Vital Sign.
     *
     */
    public static final String ICARDEA_INSTANCE_BLOOD_PRESSURE =
            ICARDEA_NS + "/instances/BloodPressure";
    /**
     * The URI for the instance used to define Systolic Blood Pressure in IHE
     * acceptation.<br/> This is a Vital Sign.
     */
    public static final String ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE =
            ICARDEA_NS + "/instances/SystolicBloodPressure";
    /**
     * The URI for the instance used to define Diastolic Blood Pressure in IHE
     * acceptation.<br/> This is a Vital Sign.
     */
    public static final String ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE =
            ICARDEA_NS + "/instances/DiastolicBloodPressure";
    /**
     * The URI for the instance used to define Heart Rate in IHE
     * acceptation.<br/> This is a Vital Sign.
     */
    public static final String ICARDEA_INSTANCE_HEART_RATE =
            ICARDEA_NS + "/instances/HeartRate";
    /**
     * The URI for the instance used to define Body Weight in IHE
     * acceptation.<br/> This is a Vital Sign.
     */
    public static final String ICARDEA_INSTANCE_BODY_MASS_INDEX =
            ICARDEA_NS + "/instances/BodyMassIndex";
    /**
     * The URI for the instance used to define Body Height Measure in IHE
     * acceptation.<br/> This is a Vital Sign.
     */
    public static final String ICARDEA_INSTANCE_BODY_HEIGHT =
            ICARDEA_NS + "/instances/BodyHeightMeasure";
    /**
     * The URI for the instance used to define Body Height Measure in IHE
     * acceptation.<br/> This is a Vital Sign.
     */
    public static final String ICARDEA_INSTANCE_BODY_WEIGHT =
            ICARDEA_NS + "/instances/BodyWeight";
    public static final String SKOS_NS = "http://www.w3.org/2004/02/skos/core";
    public static final String SKOS_NOTE = SKOS_NS + "#note";
    public static final String SKOS_RELATED = SKOS_NS + "#related";
    public static final String SKOS_PREFLABEL = SKOS_NS + "#prefLabel";
    public static final String SKOS_NOTATION = SKOS_NS + "#notation";
    /**
     * Provides collating information. <br/> This is a iCarea PHRS specific
     * property.
     */
    public static final String ITEM_ORDER = ICARDEA_NS + "#itemOrder";
    /**
     * The date when a resource was created in iCarea PHRS context. <br/> This
     * is a iCarea PHRS specific property.
     */
    public static final String CREATE_DATE = ICARDEA_NS + "#createDate";
    /**
     * The date when a resource was updated in iCarea PHRS context. <br/> This
     * is a iCarea PHRS specific property.
     */
    public static final String UPDATE_DATE = ICARDEA_NS + "#updateDate";
    /**
     * The user (entity) responsible for a given resource. <br/> This is a
     * iCarea PHRS specific property.
     */
    public static final String OWNER = ICARDEA_NS + "#owner";
    /**
     * Hols information about the origin for a given resource, in the iCarea
     * PHRS context. <br/> This is a iCarea PHRS specific property.
     */
    public static final String CREATOR = ICARDEA_NS + "#creator";
    /**
     * Hols information about the origin for a given resource, in the iCarea
     * PHRS context. <br/> This is a iCarea PHRS specific property.
     */
    public static final String ORIGIN = ICARDEA_NS + "#origin";
    /**
     * The unique id for "Simple Observation" in the IHE acceptation.<br/> In
     * most of the cases this is used like value (of type literal) for a
     * property named templIdRoot.
     *
     * @see #ICARDEA_HL7V3_TEMPLATE_ID_ROOT
     * @see
     * <url>http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.13</url>
     */
    public static final String SIMPLE_OBSERVATIONS = "1.3.6.1.4.1.19376.1.5.3.1.4.13";
    /**
     * The unique id for "Medication" in the IHE acceptation.<br/> In most of
     * the cases this is used like value (of type literal) for a property named
     * templIdRoot.
     *
     * @see #ICARDEA_HL7V3_TEMPLATE_ID_ROOT
     * @see
     * <url>http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.12</url>
     */
    public static final String IMUNISATION = "1.3.6.1.4.1.19376.1.5.3.1.4.12";
    /**
     * The unique id for "Medication" in the IHE acceptation.<br/> In most of
     * the cases this is used like value (of type literal) for a property named
     * templIdRoot.
     *
     * @see #ICARDEA_HL7V3_TEMPLATE_ID_ROOT
     * @see
     * <url>http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.7</url>
     */
    public static final String MEDICATION = "1.3.6.1.4.1.19376.1.5.3.1.4.7";
    /**
     * The unique id for "Medication Normal dosing" in the IHE acceptation.<br/>
     * In most of the cases this is used like value (of type literal) for a
     * property named templIdRoot.
     *
     * @see #ICARDEA_HL7V3_TEMPLATE_ID_ROOT
     * @see
     * <url>http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.7</url>
     */
    public static final String MEDICATION_NORMAL_DOSING = "1.3.6.1.4.1.19376.1.5.3.1.4.7.1";
    /**
     * The unique id for "Vital Signs Observation" in the IHE acceptation.<br/>
     * In most of the cases this is used like value (of type literal) for a
     * property named templIdRoot.
     *
     * @see #ICARDEA_HL7V3_TEMPLATE_ID_ROOT
     * @see
     * <url>http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.13.2</url>
     */
    public static final String VITAL_SIGNS_OBSERVATIONS = "1.3.6.1.4.1.19376.1.5.3.1.4.13.2";
    /**
     * The unique id for "Vital Signs Observation" in the IHE acceptation.<br/>
     * In most of the cases this is used like value (of type literal) for a
     * property named templIdRoot.
     *
     *
     * @see #ICARDEA_HL7V3_TEMPLATE_ID_ROOT
     * @see
     * <url>http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.1.12.3.6</url>
     */
    public static final String ASTM_HL7CONTINUALITY_OF_CARE_DOCUMENT = "2.16.840.1.113883.10.20.1.31";
    /**
     * The unique id for "Problem Entry" in the IHE acceptation.<br/> In most of
     * the cases this is used like value (of type literal) for a property named
     * templIdRoot.
     *
     *
     * @see #ICARDEA_HL7V3_TEMPLATE_ID_ROOT
     * @see
     * <url>http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5</url>
     */
    public static final String PROBLEM_ENTRY = "1.3.6.1.4.1.19376.1.5.3.1.4.5";
    /**
     * The unique id for "Problem Entry" in the IHE acceptation.<br/> In most of
     * the cases this is used like value (of type literal) for a property named
     * templIdRoot.
     *
     *
     * @see #ICARDEA_HL7V3_TEMPLATE_ID_ROOT
     * @see
     * <url>http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5</url>
     */
    public static final String PROBLEM_OBSERVATION = "2.16.840.1.113883.10.20.1.28";
    /**
     * Describes the process of establishing a problem like condition.
     */
    public static final String HL7V3_CONDITION =
            ICARDEA_NS + "/instances/Problem/Condition";
    /**
     * Describes the process of establishing a problem like symptom.
     */
    public static final String HL7V3_SYMPTOM =
            ICARDEA_NS + "/instances/Problem/Symptom";
    /**
     * Describes the process of establishing a problem like finding.
     */
    public static final String HL7V3_FINDING =
            ICARDEA_NS + "/instances/Problem/Finding";
    /**
     * Describes the process of establishing a problem like functional
     * limitation.
     */
    public static final String HL7V3_FUNCTIONAL_LIMITATION =
            ICARDEA_NS + "/instances/Problem/FunctionalLimitation";
    /**
     * Describes the process of establishing a problem like complaint.
     */
    public static final String HL7V3_COMPILANT =
            ICARDEA_NS + "/instances/Problem/Complaint";
    /**
     * Describes the process of establishing a problem entry like problem.
     */
    public static final String HL7V3_PROBLEM =
            ICARDEA_NS + "/instances/Problem/Problem";
    /**
     * Describes the process of establishing a problem entry like diagnosis.
     */
    public static final String HL7V3_DIAGNOSIS =
            ICARDEA_NS + "/instances/Problem/Diagnosis";
    public static final String HL7V3_STATUS = ICARDEA_HL7V3_NS + "#status";
    public static final String HL7V3_DATE_START = ICARDEA_HL7V3_NS + "#dateStart";
    public static final String HL7V3_DATE_END = ICARDEA_HL7V3_NS + "#dateEnd";
    /**
     * Used to describe the medication dosage. This is rdfs resource.
     */
    public static final String HL7V3_DOSAGE = ICARDEA_HL7V3_NS + "#dosage";
    /**
     * Used to describe the medication dosage value (e.g. 1). This is a literal,
     * a float.
     */
    public static final String HL7V3_DOSAGE_VALUE = ICARDEA_HL7V3_NS + "#dosageValue";
    /**
     * Used to describe the medication dosage unit (e.g. pill). This is a
     * resource.
     */
    public static final String HL7V3_DOSAGE_UNIT = ICARDEA_HL7V3_NS + "#dosageUnit";
    public static final String HL7V3_FREQUENCY = ICARDEA_HL7V3_NS + "#frequency";
    public static final String HL7V3_ADMIN_ROUTE = ICARDEA_HL7V3_NS + "#adminRoute";
    public static final String HL7V3_DRUG_NAME = ICARDEA_HL7V3_NS + "#drugName";
    public static final String HL7V3_FEVER = ICARDEA_NS + "/instances/Fever";
    public static final String HL7V3_PALPITATION = ICARDEA_NS + "/instances/Palpitations";
    public static final String HL7V3_FATIQUE = ICARDEA_NS + "/instances/Fatigue-Symptom";
    public static final String HL7V3_BICYCLE_RIDING = ICARDEA_NS + "/instances/BicycleRiding";
    public static final String HL7V3_SHORTHENS_OF_BREATH = ICARDEA_NS + "/instances/DShortnessOfBreath";
    public static final String HL7V3_ANXIETY = ICARDEA_NS + "/instances/Anxiety";
    public static final String HL7V3_FEELING_WELL = ICARDEA_NS + "/instances/FeelingWell";
    public static final String HL7V3_SICK_TO_STOMACH = ICARDEA_NS + "/instances/SicktoStomach";
    public static final String HL7V3_UNABLE_TO_EAT = ICARDEA_NS + "/instances/UnableToEat";
    public static final String HL7V3_WALKING = ICARDEA_NS + "/instances/Walking";
    public static final String HL7V3_WEIGHT_INCREASED = ICARDEA_NS + "/instances/WeightIncreased";
    public static final String HL7V3_BRUSING = ICARDEA_NS + "/instances/BruisingSymptom";
    public static final String HL7V3_TEMPORRALY_LOSS_OF_SPEECH =
            ICARDEA_NS + "/instances/TemporarayLossOfSpeech";
    
    public static final String HL7V3_BLEEDINGS_GUMS =
            ICARDEA_NS + "/instances/BleedingGums";

    /**
     * Used to describe oral administration of treatment in the HL3 V3
     * acception. This is a resource.
     */
    public static final String HL7V3_ORAL_ADMINISTRATION =
            ICARDEA_NS + "/instances/OralAdministration";
    public static final String HL7V3_INJECTION_ADMINISTRATION =
            ICARDEA_NS + "/instances/InjectionAdministration";
    

    public static final String RDFS_TYPE =
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    public static final String RDFS_RANGE =
            "http://www.w3.org/2000/01/rdf-schema#range";
    public static final String RDF_PROPERTY_TYPE =
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property";
    public static final String RDF_LITERAL =
            "http://www.w3.org/2000/01/rdf-schema#Literal";
    public static final String RDF_RESOURCE =
            "http://www.w3.org/2000/01/rdf-schema#Resource";
    public static final String RDF_DOMAIN =
            "http://www.w3.org/2000/01/rdf-schema#domain";
    /**
     * The rdf class for a vital sign in the PHRS acceptation.
     */
    public static final String PHRS_VITAL_SIGN_CLASS =
            "http://www.icardea.at/phrs/types/1.0/VitalSign";
    /**
     * The rdf class for a Problem Entry in the PHRS acceptation.
     */
    public static final String PHRS_OBSERVATION_ENTRY_CLASS =
            "http://www.icardea.at/phrs/types/1.0/Observation";
    /**
     * The rdf class for a medication in the PHRS acceptation.
     */
    public static final String PHRS_MEDICATION_CLASS =
            "http://www.icardea.at/phrs/types/1.0/Medication";
    /**
     * The rdf class for a medication dosage in the PHRS acceptation.
     */
    public static final String PHRS_MEDICATION_DOSAGE_CLASS =
            "http://www.icardea.at/phrs/types/1.0/MedicationDosage";
    /**
     * The phrs medication dosage predicate URI. This is a resource property.
     */
    public static final String PHRS_MEDICATION_DOSAGE =
            ICARDEA_NS + "/code#dosage";
    /**
     * Describes an observation as risk.
     */
    public static final String HL7V3_RISK_OF = ICARDEA_NS + "/instances/RiskOf";
    /**
     * The rdf class for the PHRSActor acceptation. The PHRSActor is used for
     * any extern entity that interact with the PHRS and needs to be unique
     * identified.
     */
    public static final String PHRS_ACTOR_CLASS =
            ICARDEA_NS + "/types/1.0/PhrsActor";
    /**
     * The unique identifiers for the "id" predicate, this predicate must be
     * applied to an actor.
     */
    public static final String PHRS_ACTOR_ID =
            ICARDEA_NS + "/actor#id";
    /**
     * The unique identifiers for the "namespace" predicate, this predicate must
     * be applied to an actor. This property is a literal.
     */
    public static final String PHRS_ACTOR_NAMESPACE =
            ICARDEA_NS + "/actor#namespace";
    /**
     * The unique identifiers for the "protocolId" predicate, this predicate
     * must be applied to an actor. This property is a literal.
     */
    public static final String PHRS_ACTOR_PROTOCOL_ID =
            ICARDEA_NS + "/actor#protocolId";
    /**
     * The string used to unique identify the iCardea name space.
     */
    public static final String ICARDEA_NAMESPACE = "icardea";
    /**
     * The string used to unique identify the iCardea-PHR System name space.
     */
    public static final String PHRS_NAMESPACE = "icardea.phrs";
    /**
     * The string used to unique identify the HL7 V3 medication frequency class.
     */
    public static final String HL7_FREQUECY_CLASS =
            "http://www.icardea.at/phrs/types/1.0/MedicationFrequency";
    /**
     * The string used to unique identify the HL7 V3 medication frequency
     * property. This property is a resource.
     */
    public static final String HL7_FREQUECY_EVENT = ICARDEA_HL7V3_NS + "#event";
    /**
     * The string used to unique identify the HL7 V3 medication frequency offset
     * property. This property is a literal.
     */
    public static final String HL7_FREQUECY_OFFSET = ICARDEA_HL7V3_NS + "#offset";
    /**
     * The string used to unique identify the HL7 V3 medication frequency repeat
     * property. This property is a literal.
     */
    public static final String HL7_FREQUECY_REPEAT = ICARDEA_HL7V3_NS + "#repeat";
    /**
     * The string used to unique identify the the Value Object class. This class
     * is used to define values and its unit.
     */
    public static final String PHRS_VALUE_OBJECT_CLASS =
            "http://www.icardea.at/phrs/types/1.0/ValueObject";
    /**
     * The string used to unique identify the Hour instance. The Hour instance
     * is used to describe the hour concept.
     */
    public static final String HOUR =
            "http://www.icardea.at/phrs/instances/MeasureSystem#Hour";
    /**
     * The string used to unique identify the Minute instance. The Hour instance
     * is used to describe the minute concept.
     */
    public static final String MINUTE =
            "http://www.icardea.at/phrs/instances/MeasureSystem#Minute";
    /**
     * The string used to unique identify the Second instance. The Hour instance
     * is used to describe the second concept.
     */
    public static final String SECOND =
            "http://www.icardea.at/phrs/instances/MeasureSystem#Second";
    /**
     * Identify the Manufactured Product instance #manufacturedProduct
     */
    public static final String MANUFACTURED_PRODUCT =
            ICARDEA_HL7V3_NS + "#manufacturedProduct";
    
    public static final String MANUFACTURED_PRODUCT_CLASS =
            "http://www.icardea.at/phrs/types/1.0/ManufacturedProduct";
    public static final String HL7_CLASS_CODE =
            ICARDEA_HL7V3_NS + "#classCode";
    public static final String MANUFACTURED_LABEL_DRUG =
            ICARDEA_HL7V3_NS + "#manufacturedLabeledDrug";
    public static final String MANUFACTURED_LABEL_DRUG_CLASS =
            "http://www.icardea.at/phrs/types/1.0/ManufacturedLabeledDrug";
    public static final String HL7_DETERMINER_CODE =
            ICARDEA_HL7V3_NS + "#determinerCode";
    public static String EXTERN =
            "http://www.icardea.at/phrs/types/1.0/ExternCreator";
    
    /**
     * Holds the unique instance for the Dispathed To property.
     * This property is a literal.
     */
    public static String DISTPATCH_TO = ICARDEA_HL7V3_NS  + "#dispathedTo";
    
    public static String PHRS_REQUEST_CLASS = 
            "http://www.icardea.at/phrs/types/1.0/PHRSRequest";
    
    public static String HL7V3_CARE_PROVISION_CODE = ICARDEA_HL7V3_NS + "#careProcisionCode";
    public static String HL7V3_REPLY_ADRESS = ICARDEA_HL7V3_NS + "#wsReplyAddress"; 
    
    
    public static final String ICARDEA_DOMAIN_PIX_OID = "1.2.826.0.1.3680043.2.44.248240.1";
    public static final String OWNER_URI_CORE_PORTAL_TEST_USER = "phr/test/testuser";
    public static final String PROTOCOL_ID_UNIT_TEST = "191";//"AAA14920263490BBB";
    public static final String PROTOCOL_ID_PIX_TEST_PATIENT = "191";
    public static final String OWNER_URI_PIX_TEST_PATIENT = "phr/test/testuser2";
    public static final String EHR_OWNER = "EHR";
    
    /**
     * The unique code for the UMLS code system.
     */
    public static final String UMLS_CODESYSTEM_CODE = "2.16.840.1.113883.6.86";
    
    


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
}
