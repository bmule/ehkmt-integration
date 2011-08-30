/*
 * Project :iCardea
 * File : ProblemsClient.java
 * Encoding : UTF-8
 * Date : Aug 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ihe;

import static at.srfg.kmt.ehealth.phrs.dataexchange.util.QUPCAR004030UVUtil.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mihai
 */
public class VitalSignPCC10 {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.VitalSignPCC10</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VitalSignPCC10.class);
    /**
     * JAX-B object factory- used to build jax-b object 'hanged' in the
     * element(s) tree.
     */
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static QUPCIN043200UV01 getPCC10Message(Set<DynaBean> beans) throws TripleException {


        final QUPCIN043200UV01 query;
        try {
            query = buildQUPCIN043200UV01("PCC-10-Empty-Input.xml");
        } catch (JAXBException exception) {
            throw new RuntimeException(exception);
        }

        if (beans == null || beans.isEmpty()) {
            return query;
        }

        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess =
                query.getControlActProcess();

        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();

        final List<REPCMT004000UV01PertinentInformation5> informations =
                new ArrayList<REPCMT004000UV01PertinentInformation5>();
        for (DynaBean vitalBean : beans) {
            final List<String> rootIds = 
                    (List<String>)  vitalBean.get(Constants.HL7V3_TEMPLATE_ID_ROOT);
            final DynaBean code = (DynaBean) vitalBean.get(Constants.HL7V3_CODE);
            final String note = (String) vitalBean.get(Constants.SKOS_NOTE);
            final DynaBean status = (DynaBean) vitalBean.get(Constants.HL7V3_STATUS);
            final String effectiveTime = (String) vitalBean.get(Constants.EFFECTIVE_TIME);
            final String value = (String) vitalBean.get(Constants.HL7V3_VALUE);
            final DynaBean unit = (DynaBean) vitalBean.get(Constants.HL7V3_UNIT);

            final REPCMT004000UV01PertinentInformation5 pertinentInformation = 
                    getPertinentInformation(rootIds, code, note, status, 
                    effectiveTime, value, unit);
            informations.add(pertinentInformation);
        }
        careProvisionEvent.getPertinentInformation3().addAll(informations);

        return query;
    }

    private static REPCMT004000UV01PertinentInformation5 getPertinentInformation(List<String> rootIds,
            DynaBean code, String note, DynaBean status, String effectiveTimeStr,
            String value, DynaBean valueUnitBean) {
        final POCDMT000040Observation observation =
                OBJECT_FACTORY.createPOCDMT000040Observation();

        // template ids
        final List<II> templateIds = buildTemplateIds(rootIds);
        observation.getTemplateId().addAll(templateIds);

        final CD cd = buildCode(code);
        observation.setCode(cd);

        // effective time
        final IVLTS effectiveTime = new IVLTS();
        effectiveTime.setValue(effectiveTimeStr);
        observation.setEffectiveTime(effectiveTime);

        final CS statusCode = buildStatus(status);
        observation.setStatusCode(statusCode);

        final PQ qunatity = new PQ();
        qunatity.setValue(value);

        final String notation = (String) valueUnitBean.get(Constants.SKOS_NOTATION);
        qunatity.setUnit(notation);
        observation.getValue().add(qunatity);


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
}
