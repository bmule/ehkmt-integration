/*
 * Project :iCardea
 * File : VitalSignsFactory.java 
 * Encoding : UTF-8
 * Date : Apr 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.QUPCAR004030UVServiceUtil.buildQUPCIN043200UV01;
import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.Constants.PCC10_OUTPUT_FILE;
import static at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup.lookupLocal;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.util.Map;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicClassRepository;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10Factory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to build Problems and Risks objects according with the HL7 v3 standards
 * from a given <code>DynaBean</code>. </br>
 * This class can not be extended.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class VitalSignsFactory implements PCC10Factory<QUPCIN043200UV01> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.VitalSignsFactory</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VitalSignsFactory.class);

    /**
     * JAX-B object factory- used to build jax-b object 'hanged' in the
     * element(s) tree.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static final String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final DateFormat dateFormat = new SimpleDateFormat(pattern);

    /**
     * All vital signs to be transformed according with the HL7 v3.
     */
    private Set<DynaBean> vitalSigns;

    /**
     * All the code system related metadata for the BodyWeight class.
     */
    private final Map<String, DynamicPropertyMetadata> bodyWeightMetadata;

    /**
     * All the code system related metadata for the BloodPreasure class.
     */
    private final Map<String, DynamicPropertyMetadata> bloodPessureMetadata;

    VitalSignsFactory() {
        // FIXEM : use constants here.
        bodyWeightMetadata =
                getCodeMetadata("at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight");
        bloodPessureMetadata =
                getCodeMetadata("at.srfg.kmt.ehealth.phrs.datamodel.impl.BloodPressure");
    }

    @Override
    public QUPCIN043200UV01 build() throws PCC10BuildException {

        final QUPCIN043200UV01 query;
        try {
            query = buildQUPCIN043200UV01(PCC10_OUTPUT_FILE);
        } catch (JAXBException exception) {
            throw new PCC10BuildException(exception);
        }

        if (vitalSigns == null || vitalSigns.isEmpty()) {
            return query;
        }

        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess =
                query.getControlActProcess();

        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();

        for (DynaBean vitalSign : vitalSigns) {
            final List<REPCMT004000UV01PertinentInformation5> informations =
                    buildPertinentInformation(vitalSign);
            careProvisionEvent.getPertinentInformation3().addAll(informations);
        }

        return query;
    }

    Set<DynaBean> getVitalSigns() {
        return Collections.unmodifiableSet(vitalSigns);
    }

    void setVitalsigns(Set<DynaBean> medication) {
        this.vitalSigns = medication;
    }

    private CD buildCode(String codeSystemAndCode) {

        if (!codeSystemAndCode.contains(":")) {
            final CS statusCode = new CS();
            statusCode.setDisplayName(codeSystemAndCode);
            return statusCode;
        }

        final String[] codeAndCodeSystem = codeSystemAndCode(codeSystemAndCode);
        final CD code = new CD();
        code.setCode(codeAndCodeSystem[1]);
        code.setCodeSystem(codeAndCodeSystem[0]);
        return code;
    }

    private List<REPCMT004000UV01PertinentInformation5> buildPertinentInformation(DynaBean vitalSign) {

        final Object type = vitalSign.get("_phrsBeanClassURI");
        boolean isBodyWeight =
                "at.srfg.kmt.ehealth.phrs.datamodel.impl.BodyWeight".equals(type);
        boolean isBloodPressure =
                "at.srfg.kmt.ehealth.phrs.datamodel.impl.BloodPressure".equals(type);

        final List<REPCMT004000UV01PertinentInformation5> results =
                new ArrayList<REPCMT004000UV01PertinentInformation5>();

        if (!isBloodPressure && !isBodyWeight) {
            LOGGER.error("The type {} is not supported", type);
            return results;
        }

        // FIXME : ask the UI about teh status
        final String statusActive = "UMLS:C0205177";
        // the observation dates are the same
        final Date observationDate = (Date) vitalSign.get("observationDate");
        if (isBodyWeight) {
            final Object bodyBMIValue = vitalSign.get("bodyBMI");

            final REPCMT004000UV01PertinentInformation5 bodyBMI =
                    buildPertinentInformation(bodyBMIValue, "m/l2", statusActive,
                    observationDate, bodyWeightMetadata, "bodyBMI");
            results.add(bodyBMI);

            final Object bodyweightValue = vitalSign.get("bodyweight");
            final REPCMT004000UV01PertinentInformation5 bodyweight =
                    buildPertinentInformation(bodyweightValue, "kg", statusActive,
                    observationDate, bodyWeightMetadata, "bodyweight");
            results.add(bodyweight);

            final Object heightValue = vitalSign.get("height");
            final REPCMT004000UV01PertinentInformation5 height =
                    buildPertinentInformation(heightValue, "cm", statusActive,
                    observationDate, bodyWeightMetadata, "height");
            results.add(height);
        }

        if (isBloodPressure) {
            final Object bpDiastolicValue = vitalSign.get("bpDiastolic");
            final REPCMT004000UV01PertinentInformation5 bpDiastolic =
                    buildPertinentInformation(bpDiastolicValue, "mm/hg",
                    statusActive, observationDate, bloodPessureMetadata, "bpDiastolic");
            results.add(bpDiastolic);

            final Object bpSystolicValue = vitalSign.get("bpSystolic");
            final REPCMT004000UV01PertinentInformation5 bpSystolic =
                    buildPertinentInformation(bpSystolicValue, "mm/hg",
                    statusActive, observationDate, bloodPessureMetadata, "bpSystolic");
            results.add(bpSystolic);

            final Object bpHeartRateValue = vitalSign.get("bpHeartRate");
            final REPCMT004000UV01PertinentInformation5 bpHeartRate =
                    buildPertinentInformation(bpHeartRateValue, "min",
                    statusActive, observationDate, bloodPessureMetadata, "bpHeartRate");
            results.add(bpHeartRate);
        }

        return results;
    }

    private REPCMT004000UV01PertinentInformation5 buildPertinentInformation(Object value,
            String unit, String status, Date date, Map<String, DynamicPropertyMetadata> metadata, String property) {

        final POCDMT000040Observation observation =
                OBJECT_FACTORY.createPOCDMT000040Observation();

        // FIXME : ask the ui for it
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(XActMoodDocumentObservation.EVN);

        // the templates id I copyed from the specs, I hope they are correct
        final List<II> templateIds = buildTemplateIds();
        observation.getTemplateId().addAll(templateIds);

        // here I got the bodyweight metadata and the code is metadata
        final DynamicPropertyMetadata metaCode = metadata.get(property);

        final String codeMetada = metaCode == null
                ? value.toString()
                : metaCode.getValue().toString();

        CD code = buildCode(codeMetada);
        observation.setCode(code);

        final IVLTS effectiveTime = new IVLTS();
        effectiveTime.setValue(dateFormat.format(date));
        observation.setEffectiveTime(effectiveTime);

        final PQ qunatity = new PQ();
        qunatity.setValue(value.toString());
        qunatity.setUnit(unit);
        observation.getValue().add(qunatity);

        final CS statusCode = buildStatus(status);
        observation.setStatusCode(statusCode);

        final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5();
        final JAXBElement<POCDMT000040Observation> observation_je = 
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5Observation(observation);
        pertinentInformation.setObservation(observation_je);
        return pertinentInformation;
    }

    private CS buildStatus(String status) {

        if (!status.contains(":")) {
            final CS statusCode = new CS();
            statusCode.setDisplayName(status);
            return statusCode;
        }

        final String[] statusCodes = codeSystemAndCode(status);
        final CS statusCode = new CS();
        statusCode.setCode(statusCodes[1]);
        statusCode.setCodeSystem(statusCodes[0]);
        return statusCode;
    }

    private List<II> buildTemplateIds() {
        final List<II> iis = new ArrayList<II>(2);
        
        final II ii1 = new II();
        ii1.setExtension("1.3.6.1.4.1.19376.1.5.3.1.4.13");
        iis.add(ii1);
        
        final II ii2 = new II();
        ii2.setExtension("2.16.840.1.113883.10.20.1.31");
        iis.add(ii2);
        
        final II ii3 = new II();
        ii3.setExtension("1.3.6.1.4.1.19376.1.5.3.1.4.13.2");
        iis.add(ii3);

        return iis;
    }

    private String[] codeSystemAndCode(String in) {
        final String[] result = new String[2];

        final int indexOf = in.indexOf(":");
        result[0] = in.substring(0, indexOf);
        result[1] = in.substring(indexOf + 1, in.length());
        // I know I can use the scanner ot the String split :).
        return result;
    }

    /**
     * Extracts the needed metadatas for a given class (identified after its 
     * unique URI) from the underlying persistence layer and organize them in to
     * a map. More precisely this methods extracts all the
     * metadata(s) with the name "code" for all the properties for a given class
     * and store them in  to a map, in this map the key is the properties name 
     * and like value the code metadata value if there is one. If a property 
     * does not have metadata named "code" then this property name does not appears
     * in the resulted table. If the underlying persistence layer does not 
     * contains a clas for the given URI then this method returns an empty map.
     * 
     * @param classURI the class UIR to be analyzed.
     * @return a map that contains like key the property name and like value
     * the corresponding metadata (with the name "code").
     */
    private Map<String, DynamicPropertyMetadata> getCodeMetadata(String classURI) {

        final DynamicClassRepository classRepository;
        final Map<String, DynamicPropertyMetadata> result =
                new HashMap<String, DynamicPropertyMetadata>();
        try {
            classRepository = lookupLocal(DynamicClassRepository.class);

        } catch (Exception exception) {
            LOGGER.error("The metada can not be located, this can affect the functionality");
            LOGGER.error(exception.getMessage(), exception);
            return result;
        }

        final DynamicClass dynamicClass = classRepository.get(classURI);
        final Set<DynamicPropertyType> propertyTypes =
                dynamicClass.getPropertyTypes();

        for (DynamicPropertyType propertyType : propertyTypes) {
            final String propertyName = propertyType.getName();
            final Set<DynamicPropertyMetadata> metadatas =
                    propertyType.getMetadatas();
            for (DynamicPropertyMetadata metadata : metadatas) {
                final String metadataName = metadata.getName();
                if ("code".equals(metadataName)) {
                    result.put(propertyName, metadata);
                }
            }
        }

        return result;
    }
}
