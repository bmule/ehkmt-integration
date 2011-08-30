/*
 * Project :iCardea
 * File : ProblemsClient.java
 * Encoding : UTF-8
 * Date : Aug 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ihe;

import at.srfg.kmt.ehealth.phrs.Constants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import org.hl7.v3.IVLTS;
import static at.srfg.kmt.ehealth.phrs.dataexchange.util.QUPCAR004030UVUtil.*;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mradules
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

    public static String getPCC10Message(Set<DynaBean> beans) throws TripleException {


        return null;
    }

    private REPCMT004000UV01PertinentInformation5 getPertinentInformation(Set<String> rootIds,
            DynaBean code, String note, DynaBean status, String effectiveTimeStr,
            String value, DynaBean valueUnitBean) {
        final POCDMT000040Observation observation =
                OBJECT_FACTORY.createPOCDMT000040Observation();

        // template ids
        final List<II> templateIds = buildTemplateIds(null);
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
        final String valueUnit = (String) valueUnitBean.get(Constants.SKOS_NOTATION);
        qunatity.setUnit(valueUnit);
        observation.getValue().add(qunatity);


        final REPCMT004000UV01PertinentInformation5 pertinentInformation =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5();
        final JAXBElement<POCDMT000040Observation> observation_je =
                OBJECT_FACTORY.createREPCMT004000UV01PertinentInformation5Observation(observation);
        pertinentInformation.setObservation(observation_je);
        return pertinentInformation;

    }

    private List<II> buildTemplateIds(Collection<String> rootIds) {

        final List<II> iis = new ArrayList<II>(rootIds.size());
        for (String rootId : rootIds) {
            final II ii1 = new II();
            ii1.setExtension(rootId);
            iis.add(ii1);
        }

        return iis;
    }

    private CD buildCode(DynaBean dynaBean) {
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

    private CS buildStatus(DynaBean bean) {
        final String prefLabel = (String) bean.get(Constants.SKOS_PREFLABEL);
        final CS statusCode = new CS();
        statusCode.setDisplayName(prefLabel);

        final DynaBean codeSystemBean = (DynaBean) bean.get(Constants.CODE_SYSTEM);

        final String codeSystemCode =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_CODE);
        statusCode.setCode(codeSystemCode);

        final String codeSystemName =
                (String) codeSystemBean.get(Constants.CODE_SYSTEM_NAME);

        statusCode.setCodeSystem(codeSystemName);

        return statusCode;
    }
}
