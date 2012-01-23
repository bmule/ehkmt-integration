/*
 * Project :iCardea
 * File : ProblemsClient.java
 * Encoding : UTF-8
 * Date : Aug 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import static at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.QUPCAR004030UVUtil.buildQUPCIN043200UV01;
import java.util.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * It is used to generate a <a
 * href="http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5">Problem
 * Entry</a> according with the IHE standards for a given input.
 *
 * @author Mihai
 */
final class ProblemEntryPCC10 {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ProblemEntryPCC10</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProblemEntryPCC10.class);

    /**
     * JAX-B object factory- used to build jax-b object 'hanged' in the
     * element(s) tree.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    /**
     * Don't let anybody to instantiate this class.
     */
    private ProblemEntryPCC10() {
        // UNIMPLEMENTD
    }

    /**
     * Builds a <a
     * href="http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5">Problem
     * Entry</a> for a Set of dyna-beans.
     *
     * @param beans the set of dyna beans used to build the <a
     * href="http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5">Problem
     * Entry</a> , it can not be null.
     * @return a <a
     * href="http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.5">Problem
     * Entry</a> for the given set of dyna-beans.
     */
    static QUPCIN043200UV01 getPCC10Message(Set<DynaBean> beans) throws TripleException {

        if (beans == null) {
            final NullPointerException exception =
                    new NullPointerException("The beans argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final QUPCIN043200UV01 query;
        try {
            query = buildQUPCIN043200UV01("PCC-10-Empty-Input.xml");
        } catch (JAXBException jaxException) {
            final RuntimeException exception = new RuntimeException(jaxException);
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (beans == null || beans.isEmpty()) {
            LOGGER.debug("Input set is empty - the resunt will be also empty.");
            return query;
        }

        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess =
                query.getControlActProcess();

        final MFMIMT700712UV01QueryAck queryAck = controlActProcess.getQueryAck();
        II queryId = buildQueryId();
        queryAck.setQueryId(queryId);


        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();

        final List<REPCMT004000UV01PertinentInformation5> informations =
                new ArrayList<REPCMT004000UV01PertinentInformation5>();
        for (DynaBean bean : beans) {


            final List<String> rootIds =
                    (List<String>) bean.get(Constants.HL7V3_TEMPLATE_ID_ROOT);
            final DynaBean code = (DynaBean) bean.get(Constants.HL7V3_CODE);
            final String note = (String) bean.get(Constants.SKOS_NOTE);
            final DynaBean status = (DynaBean) bean.get(Constants.HL7V3_STATUS);
            final String startDate = (String) bean.get(Constants.HL7V3_START_DATE);
            //final String endDate = (String) bean.get(Constants.HL7V3_DATE_END);
            final DynaBean value = (DynaBean) bean.get(Constants.HL7V3_VALUE_CODE);


            final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                    getPertinentInformation(rootIds, code, note, status,
                    startDate, startDate, value, bean);
            informations.add(pertinentInformation);
        }
        careProvisionEvent.getPertinentInformation3().addAll(informations);

        return query;
    }

    private static II buildQueryId() {
        final II queryId = new II();
        queryId.setRoot("1");
        queryId.setExtension("3");
        return queryId;
    }

    private static REPCMT004000UV01PertinentInformation5 getPertinentInformation(List<String> rootIds,
            DynaBean code, String note, DynaBean status, String startDateStr,
            String endDateStr, DynaBean codeValueBean, DynaBean valueUnitBean) {
        final POCDMT000040Observation observation =
                OBJECT_FACTORY.createPOCDMT000040Observation();

        final ED text = buildED("test");
        observation.setText(text);
        
        // template ids
        final List<II> templateIds = buildTemplateIds(rootIds);
        observation.getTemplateId().addAll(templateIds);

        final CD cd = buildCode(code);
        observation.setCode(cd);

        // effective time
        final IVLTS startDate = new IVLTS();
        startDate.setValue(startDateStr);
        observation.setEffectiveTime(startDate);
        // FIXME : how do I add here an interval ? end date ?

        final CS statusCode = buildStatus(status);
        observation.setStatusCode(statusCode);

        final CD codeValue = buildCodeValue(codeValueBean);
        observation.getValue().add(codeValue);

        final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5();
        final JAXBElement<POCDMT000040Observation> observation_je =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5Observation(observation);
        pertinentInformation.setObservation(observation_je);
        return pertinentInformation;
    }

    private static List<II> buildTemplateIds(Collection<String> rootIds) {

        final List<II> iis = new ArrayList<II>(rootIds.size());
        for (String rootId : rootIds) {
            final II ii1 = new II();
            ii1.setExtension(rootId);
            iis.add(ii1);
        }

        return iis;
    }

    private static CD buildCode(DynaBean dynaBean) {
        final String codePrefLabel = (String) dynaBean.get(Constants.SKOS_PREFLABEL);

        final DynaBean codeBean = (DynaBean) dynaBean.get(Constants.CODE);
        final String codeValue = (String) codeBean.get(Constants.CODE_VALUE);

        final CD code = new CD();
        code.setCode(codeValue);
        code.setDisplayName(codePrefLabel);

        final DynaBean codeSystemBean =
                (DynaBean) codeBean.get(Constants.CODE_SYSTEM);

        final String codeSystemCode =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_CODE);
        code.setCodeSystem(codeSystemCode);
        final String codeSystemName = (String) codeSystemBean.get(Constants.CODE_SYSTEM_NAME);
        code.setCodeSystemName(codeSystemName);

        return code;
    }

    private static CS buildStatus(DynaBean bean) {
        final String prefLabel = (String) bean.get(Constants.SKOS_PREFLABEL);

        final CS statusCode = new CS();
        statusCode.setDisplayName(prefLabel);

        final DynaBean codeBean = (DynaBean) bean.get(Constants.CODE);
        final String codeValue = (String) codeBean.get(Constants.CODE_VALUE);
        statusCode.setCode(codeValue);

        final DynaBean codeSystemBean = (DynaBean) codeBean.get(Constants.CODE_SYSTEM);

        final String codeSystemCode =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_CODE);
        statusCode.setCode(codeSystemCode);

        final String codeSystemName =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_NAME);
        statusCode.setCodeSystem(codeSystemName);

        return statusCode;
    }

    private static CD buildCodeValue(DynaBean bean) {
        final CD cd = new CD();
        final String prefLabel = (String) bean.get(Constants.SKOS_PREFLABEL);
        cd.setDisplayName(prefLabel);

        final DynaBean codeBean = (DynaBean) bean.get(Constants.CODE);
        final String codeValue = (String) codeBean.get(Constants.CODE_VALUE);
        cd.setCode(codeValue);

        final DynaBean codeSystemBean = (DynaBean) codeBean.get(Constants.CODE_SYSTEM);
        final String codeSystemCode =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_CODE);
        cd.setCodeSystem(codeSystemCode);

        final String codeSystemName =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_NAME);
        cd.setCodeSystemName(codeSystemName);

        return cd;
    }
    
        private static ED buildED(String text) {
        LOGGER.debug("Tries to build a HL7 V3 DE instance with the following text : {} like content.", text);
        
        final ED result = new ED();
        // TODO : get the right language
        result.setLanguage(Locale.ENGLISH.getLanguage());
        result.setRepresentation(BinaryDataEncoding.TXT);

        final TEL reference = OBJECT_FACTORY.createTEL();
        reference.setValue(text);
        result.setReference(reference);

        return result;

    }
}
