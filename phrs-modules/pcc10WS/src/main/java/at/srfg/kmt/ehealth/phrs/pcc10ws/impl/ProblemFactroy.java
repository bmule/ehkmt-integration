/*
 * Project :iCardea
 * File : ProblemFactroy.java 
 * Encoding : UTF-8
 * Date : Apr 25, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import at.srfg.kmt.ehealth.phrs.dataexchange.api.ControlledItemRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
import javax.xml.bind.JAXBException;
import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.QUPCAR004030UVServiceUtil.buildQUPCIN043200UV01;
import static at.srfg.kmt.ehealth.phrs.pcc10ws.impl.Constants.PCC10_OUTPUT_FILE;
import static at.srfg.kmt.ehealth.phrs.util.JBossJNDILookup.lookupLocal;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.MetadataRepository;
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

    public static final String IS_OBSERVATION_CODE = "isObservationCode";

    public static final String IS_OBSERVATION_VALUE = "isObservationValue";

    public static final String PROBLEM_URI = "at.srfg.kmt.ehealth.phrs.datamodel.impl.Problem";

    public static final String RISK_URI = "at.srfg.kmt.ehealth.phrs.datamodel.impl.Risk";

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

    /**
     * All problems to be transformed according with the HL7 v3.
     */
    private Set<DynaBean> problems;

    private Map<String, String> problemsMeta;

    private Map<String, String> riskMeta;

    /**
     * Builds a <code>ProblemFactroy</code> instance.
     */
    ProblemFactroy() {
        problemsMeta = new HashMap<String, String>();
        riskMeta = new HashMap<String, String>();
        solveMetadata();
        LOGGER.debug("The Problem class metadata : ", problems);
        LOGGER.debug("The Risk class metadata : ", riskMeta);
    }

    private void solveMetadata() {
        final MetadataRepository metadataRepository;
        try {
            metadataRepository = lookupLocal(MetadataRepository.class);

        } catch (Exception exception) {
            LOGGER.error("The metada can not be located, this can affect the functionality");
            LOGGER.error(exception.getMessage(), exception);
            return;
        }

        final Set<DynamicPropertyType> problemObsCode =
                metadataRepository.getByMetadataName(PROBLEM_URI, IS_OBSERVATION_CODE);
        // FIXME : the key values is overwrite 
        for (DynamicPropertyType propertyType : problemObsCode) {
            problemsMeta.put(IS_OBSERVATION_CODE, propertyType.getName());
        }

        final Set<DynamicPropertyType> problemObsValue =
                metadataRepository.getByMetadataName(PROBLEM_URI, IS_OBSERVATION_VALUE);
        for (DynamicPropertyType propertyType : problemObsValue) {
            problemsMeta.put(IS_OBSERVATION_VALUE, propertyType.getName());
        }

        final Set<DynamicPropertyType> riskObsCode =
                metadataRepository.getByMetadataName(RISK_URI, IS_OBSERVATION_CODE);
        for (DynamicPropertyType propertyType : riskObsCode) {
            riskMeta.put(IS_OBSERVATION_CODE, propertyType.getName());
        }

        final Set<DynamicPropertyType> riskObsValue =
                metadataRepository.getByMetadataName(RISK_URI, IS_OBSERVATION_VALUE);
        for (DynamicPropertyType propertyType : riskObsValue) {
            riskMeta.put(IS_OBSERVATION_VALUE, propertyType.getName());
        }
    }

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
            REPCMT004000UV01PertinentInformation5 information =
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

        //final String issueTypeCode = (String) problem.get("issueTypeCode");
        final String codePropName = getCodePropertyName(problem);
        final String issueTypeCode = (String) problem.get(codePropName);
        final CD code = buildCode(issueTypeCode);
        observation.setCode(code);

        final Date dateStart = (Date) problem.get("observationDateStart");

        final IVLTS effectiveTime = new IVLTS();
        effectiveTime.setValue(dateFormat.format(dateStart));
        observation.setEffectiveTime(effectiveTime);

        // FIXME : do I need to care about the end date ?

        // final String issueCode = (String) problem.get("issueCode");
        final String codeValuePropName = getValuePropertyName(problem);
        final String issueCode = (String) problem.get(codeValuePropName);
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

    private String getCodePropertyName(DynaBean bean) {
        final Object type = bean.get("_phrsBeanClassURI");
        if (PROBLEM_URI.equals(type)) {
            final String result = problemsMeta.get(IS_OBSERVATION_CODE);
            return result;
        }

        if (RISK_URI.equals(type)) {
            final String result = riskMeta.get(IS_OBSERVATION_CODE);
            return result;
        }

        final String msg =
                String.format("The actual type %s is not supported", type);
        throw new IllegalArgumentException(msg);
    }

    private String getValuePropertyName(DynaBean bean) {
        final Object type = bean.get("_phrsBeanClassURI");
        if (PROBLEM_URI.equals(type)) {
            final String result = problemsMeta.get(IS_OBSERVATION_VALUE);
            return result;
        }

        if (RISK_URI.equals(type)) {
            final String result = riskMeta.get(IS_OBSERVATION_VALUE);
            return result;
        }

        final String msg =
                String.format("The actual type %s is not supported", type);
        throw new IllegalArgumentException(msg);
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

        final String prefLabel = getPrefLabel(statusCodes[0], statusCodes[1]);
        code.setDisplayName(prefLabel);

        return code;
    }

    private String getPrefLabel(String codeSystemCode, String code) {
        // FIXME : repeated look up can cause performance !
        final ControlledItemRepository itemRepository;
        try {
            itemRepository = lookupLocal(ControlledItemRepository.class);

        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            return codeSystemCode + ":" + code;
        }
        final ControlledItem item =
                itemRepository.getByCodeSystemAndCode(codeSystemCode, code);

        if (item == null) {
            final String msg =
                    String.format("No controlled item for code %s and code system code %s", code, codeSystemCode);
            LOGGER.warn(msg);
        }

        return item != null ? item.getPrefLabel() : codeSystemCode + ":" + code;

    }

    private CS buildStatus(String status) {

        if (!status.contains(":")) {
            final CS statusCode = new CS();
            statusCode.setDisplayName(status);
            return statusCode;
        }

        final String[] statusCodes = codeSystemAndCode(status);
        final CS statusCode = new CS();
        statusCode.setCodeSystem(statusCodes[0]);
        statusCode.setCode(statusCodes[1]);

        final String prefLabel = getPrefLabel(statusCodes[0], statusCodes[1]);
        statusCode.setDisplayName(prefLabel);


        return statusCode;
    }

    private String[] codeSystemAndCode(String in) {
        final String[] result = new String[2];

        final int indexOf = in.indexOf(":");
        result[0] = in.substring(0, indexOf);
        result[1] = in.substring(indexOf + 1, in.length());
        // I know I can use the scanner ot the String split :).
        return result;
    }

    private CD buildValue(String status) {
        return buildCode(status);
    }
}
