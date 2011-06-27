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

    public static final String ICARDEA_NS = "http://www.icardea.at/phrs";

    public static final String STATUS_NS = getICardeaResource("status");

    public static final String STATUS_STATE = STATUS_NS + "#statusState";

    /**
     * This is an instance URI.
     */
    public static final String STATUS_COMPELETE =
            getICardeaResource("status/instance/Complete");

    /**
     * This is an instance URI.
     */
    public static final String STATUS_INCOMPELETE =
            getICardeaResource("status/instance/Incomplete");

    /**
     * This is an instance URI.
     */
    public static final String STATUS_RUNNING =
            getICardeaResource("status/instance/Runnig");

    /**
     * This is an instance URI.
     */
    public static final String STATUS_INTERRUPTED =
            getICardeaResource("status/instance/Interrupted");

    public static final String CODE_NS = getICardeaResource("code");

    public static final String CODE = CODE_NS + "#code";

    public static final String CODE_NAME = CODE_NS + "#codeName";

    public static final String CODE_SYSTEM = CODE_NS + "#codeSystem";

    public static final String CODE_SYSTEM_NS = getICardeaResource("codeSystem");

    public static final String CODE_SYSTEM_CODE = CODE_SYSTEM_NS + "#codeSystemCode";

    public static final String CODE_SYSTEM_NAME = CODE_SYSTEM_NS + "#codeSystemName";

    /**
     * This is an instance URI.
     */
    public static final String LOINC_CODE_SYSTEM =
            getICardeaResource("codeSystem/instance/LOINC");

    /**
     * This is an instance URI.
     */
    public static final String SNOMED_CODE_SYSTEM =
            getICardeaResource("codeSystem/instance/SNOMED");

    /**
     * This is an instance URI.
     */
    public static final String PHRS_CODE_SYSTEM =
            getICardeaResource("codeSystem/instance/PHRS");

    public static final String BLOOD_PRESSURE_NS = getICardeaResource("bloodpressure");

    public static final String BLOOD_PRESSURE_DIASTOLIC = BLOOD_PRESSURE_NS + "#bpDiastolic";

    public static final String HEART_NS = getICardeaResource("heart");

    public static final String HEART_RATE = HEART_NS + "#bpHeartRate";

    public static final String OBSERVATION_DATE = "observationDate";

    public static final String BODY_NS = getICardeaResource("body");

    public static final String BODY_BMI = BODY_NS + "#bodyBMI";

    public static final String BODY_WEIGHT = BODY_NS + "#bodyweight";

    public static final String HEIGHT = BODY_NS + "#height";

    public static final String MEASURE_SYSTEM_NS = getICardeaResource("mSystem");

    public static final String MEASURE_SYSTEM_UNIT = MEASURE_SYSTEM_NS + "#mSystemValue";

    public static final String MEASURE_SYSTEM_VALUE = MEASURE_SYSTEM_NS + "#mSystemUnit";

    public static final String MEASURE_SYSTEM_QUANTITY = MEASURE_SYSTEM_NS + "#mSystemQuantity";
    

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
    public static final String TABLET = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Tablet";

    /**
     * This is an instance URI.
     */
    public static final String PILL = ICARDEA_INSTANCES_MESURE_SYSTEM_NS + "#Pill";

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
    
    
    public static final String ICARDEA_HL7V3_NS = ICARDEA_NS + "/hl7V3";

    public static final String ICARDEA_HL7V3_TEMPLATE_ID_ROOT = ICARDEA_HL7V3_NS + "#templIdRoot";

    public static final String ICARDEA_HL7V3_CODE = ICARDEA_HL7V3_NS + "#code";
    
    public static final String ICARDEA_HL7V3_EFFECTIVE_TIME = ICARDEA_HL7V3_NS + "#effectiveTime";
    public static final String ICARDEA_HL7V3_VALUE = ICARDEA_HL7V3_NS + "#value";
    public static final String ICARDEA_HL7V3_UNIT = ICARDEA_HL7V3_NS + "#unit";

    public static final String ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE =
            ICARDEA_NS + "/instances/SystolicBloodPressure";

    public static final String ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE =
            ICARDEA_NS + "/instances/DiastolicBloodPressure";

    
    public static final String SKOS_NS = "http://www.w3.org/2004/02/skos/core";
    public static final String SKOS_NOTE = SKOS_NS + "#note";

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
