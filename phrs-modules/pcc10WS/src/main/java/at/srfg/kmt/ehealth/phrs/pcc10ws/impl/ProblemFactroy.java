/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10Factory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

        final Date observationDate = (Date) problem.get("observationDate");
        final IVLTS effectiveTime = new IVLTS();
        effectiveTime.setValue(dateFormat.format(observationDate));
        observation.setEffectiveTime(effectiveTime);

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
}
