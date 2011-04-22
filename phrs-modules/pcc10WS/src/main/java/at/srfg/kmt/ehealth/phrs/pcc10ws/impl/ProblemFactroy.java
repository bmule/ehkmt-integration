/*
 * Project :iCardea
 * File : ProblemsFactroy.java 
 * Encoding : UTF-8
 * Date : Apr 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.QUPCAR004030UVServiceUtil.buildQUPCIN043200UV01;
import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.Constants.PCC10_OUTPUT_FILE;
import static at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup.lookupLocal;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10Factory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class ProblemFactroy implements PCC10Factory<QUPCIN043200UV01> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.ProblemFactroy</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProblemFactroy.class);

    /**
     * JAX-B object factory- used to build jax-b object 'hanged' in the
     * element(s) tree.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static final String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static final DateFormat dateFormat = new SimpleDateFormat(pattern);
    
    private Map<String, String> propertiesNames;
    
    ProblemFactroy() {
        
    }

    /**
     * All problems to be transformed according with the HL7 v3.
     */
    private Set<DynaBean> problems;

    @Override
    public QUPCIN043200UV01 build() throws PCC10BuildException {
        final QUPCIN043200UV01 query;
        try {
            query = buildQUPCIN043200UV01(PCC10_OUTPUT_FILE);
        } catch (JAXBException exception) {
            throw new PCC10BuildException(exception);
        }

        if (problems == null || problems.isEmpty()) {
            return query;
        }

        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess =
                query.getControlActProcess();

        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();

        for (DynaBean problem : problems) {
            final REPCMT004000UV01PertinentInformation5 information =
                    buildPertinentInformation(problem);
            careProvisionEvent.getPertinentInformation3().add(information);
        }

        return query;

    }

    Set<DynaBean> getProblems() {
        return Collections.unmodifiableSet(problems);
    }

    void setProblems(Set<DynaBean> problems) {
        this.problems = problems;
    }

    private REPCMT004000UV01PertinentInformation5 buildPertinentInformation(DynaBean problem) {

        final POCDMT000040Observation observation =
                OBJECT_FACTORY.createPOCDMT000040Observation();

        // FIXME : ask the ui for it
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(XActMoodDocumentObservation.EVN);

        // the templates id I copyed from the specs, I hope they are correct
        final List<II> templateIds = buildTemplateIds();
        observation.getTemplateId().addAll(templateIds);

        final String issueTypeCode = (String) problem.get("issueTypeCode");
        CD code = buildCode(issueTypeCode);
        observation.setCode(code);

        final Date dateStart = (Date) problem.get("observationDateStart");

        final IVLTS effectiveTime = new IVLTS();
        effectiveTime.setValue(dateFormat.format(dateStart));
        observation.setEffectiveTime(effectiveTime);

        // FIXME : do I need to care about the end date ?

        final String issueCode = (String) problem.get("issueCode");
        final CD value = buildValue(issueCode);
        observation.getValue().add(value);

        // FIXME : ask the UI about teh status
        final String statusActive = "UMLS:C0205177";
        final CS statusCode = buildStatus(statusActive);
        observation.setStatusCode(statusCode);

        final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5();
        final JAXBElement<POCDMT000040Observation> observation_je =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5Observation(observation);
        pertinentInformation.setObservation(observation_je);
        return pertinentInformation;
    }

    private CD buildCode(String status) {

        if (!status.contains(":")) {
            final CD code = new CD();
            code.setDisplayName(status);
            return code;
        }

        final String[] statusCodes = codeSystemAndCode(status);
        final CD code = new CD();
        code.setCode(statusCodes[1]);
        code.setCodeSystem(statusCodes[0]);
        return code;
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

    private CD buildValue(String status) {
        return buildCode(status);
    }

    private String[] codeSystemAndCode(String in) {
        final String[] result = new String[2];

        final int indexOf = in.indexOf(":");
        result[0] = in.substring(0, indexOf);
        result[1] = in.substring(indexOf + 1, in.length());
        // I know I can use the scanner ot the String split :).
        return result;
    }

    private List<II> buildTemplateIds() {
        final List<II> iis = new ArrayList<II>(2);

        final II ii1 = new II();
        ii1.setExtension("2.16.840.1.113883.10.20.1.28");
        iis.add(ii1);

        final II ii2 = new II();
        ii2.setExtension("1.3.6.1.4.1.19376.1.5.3.1.4.5");
        iis.add(ii2);

        return iis;
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
    private Map<String, DynamicPropertyMetadata> getObservationCodeMetadata(String classURI) {
        final Map<String, DynamicPropertyMetadata> result = getMetadata(classURI, "isObservationCode");
        return result;
    }

    private Map<String, DynamicPropertyMetadata> getObservationValueMetadata(String classURI) {
        final Map<String, DynamicPropertyMetadata> result =
                getMetadata(classURI, "isObservationValue");
        return result;
    }

    private Map<String, DynamicPropertyMetadata> getObservationEfectiveDateMetadata(String classURI) {
        final Map<String, DynamicPropertyMetadata> result =
                getMetadata(classURI, "isObservationEfectiveDate");
        return result;
    }

    private Map<String, DynamicPropertyMetadata> getMetadata(String classURI, String metaName) {

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
                if (metaName.equals(metadataName)) {
                    result.put(propertyName, metadata);
                }
            }
        }

        return result;
    }
}
