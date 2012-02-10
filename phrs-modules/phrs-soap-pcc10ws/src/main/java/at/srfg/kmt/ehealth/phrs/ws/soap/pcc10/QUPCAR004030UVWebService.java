/*
 * Project :iCardea
 * File : QUPCAR004040UVWebService.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingType;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
@WebService(endpointInterface = "org.hl7.v3.QUPCAR004030UVPortType")
@HandlerChain(file = "handler-chain.xml")
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
public class QUPCAR004030UVWebService implements QUPCAR004030UVPortType {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.QUPCAR004040UVWebService</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QUPCAR004030UVWebService.class);

    /**
     * Builds a <code>QUPCAR004030UVWebService</code> instance.
     */
    public QUPCAR004030UVWebService() {
    }
    
    

    /**
     * Handles a PCC09 query and returns a acknowledge if the query was proper
     * processed or null by if the query can not be process from any reason.
     *
     * @param body the query, it can not be null.
     * @return and PCC09 specific acknowledge or null if the the query can not
     * be process from any reason.
     */
    @Override
    public MCCIIN000002UV01 qupcAR004030UVQUPCIN043200UV(QUPCIN043200UV01 body) {
        LOGGER.debug("Query [{}] was received. The query preocess starts.", body);

        if (body == null) {
            final NullPointerException nullException =
                    new NullPointerException("The body argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
            // FIXME : singals the error back to the client.
        }


        final String property = System.getProperty("pcc10.process");

        final boolean processMessage = property == null
                ? false
                : Boolean.parseBoolean(property);
        LOGGER.debug("PCC 10 processing is {} ", processMessage ? "enable" : "disabled");
        if (processMessage) {
            process(body);
        }

        try {
            final MCCIIN000002UV01 result = AcknowledgeFactory.build();
            return result;
        } catch (JAXBException exception) {
            LOGGER.error(exception.getMessage(), exception);
        }
        return null;
    }

    private void process(QUPCIN043200UV01 query) {
        final QUPCIN043200UV01MFMIMT700712UV01ControlActProcess controlActProcess =
                query.getControlActProcess();
        final MFMIMT700712UV01QueryAck queryAck = controlActProcess.getQueryAck();

        final QUPCIN043200UV01MFMIMT700712UV01Subject5 subject2 =
                controlActProcess.getSubject().get(0).getRegistrationEvent().getSubject2();
        final REPCMT004000UV01CareProvisionEvent careProvisionEvent =
                subject2.getCareProvisionEvent();
        final List<REPCMT004000UV01PertinentInformation5> pertinentInformations =
                careProvisionEvent.getPertinentInformation3();

        LOGGER.debug("Pertinent Information5 amount to process is {}",
                pertinentInformations.size());
        for (REPCMT004000UV01PertinentInformation5 information5 : pertinentInformations) {
            process(information5);
        }
    }

    private void process(REPCMT004000UV01PertinentInformation5 pertinentInformation5) {
        final JAXBElement<POCDMT000040Observation> observation_JAXB =
                pertinentInformation5.getObservation();
        final boolean hasObservation = observation_JAXB != null;
        if (hasObservation) {
            final POCDMT000040Observation observation = observation_JAXB.getValue();
            process(observation);
        }

        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JAXB =
                pertinentInformation5.getSubstanceAdministration();
        final boolean hasSubstanceAdministration = substanceAdministration_JAXB != null;
        if (hasSubstanceAdministration) {
            final POCDMT000040SubstanceAdministration substanceAdministration =
                    substanceAdministration_JAXB.getValue();
            process(substanceAdministration);
        }
    }

    private void process(POCDMT000040Observation observation) {
        LOGGER.debug("Tries to parse observation : {}", observation);
        final boolean isProblemEntry = isProblemEntry(observation);
        final boolean isVitalSign = isVitalSing(observation);

        if (isProblemEntry) {
            processProblemEntry(observation);
        }

        if (isVitalSign) {
            processVitalSign(observation);
        }

//        final CD code = observation.getCode();
//        final List<ANY> value = observation.getValue();
    }

    private boolean isVitalSing(POCDMT000040Observation observation) {
        final List<II> templateIds = observation.getTemplateId();
        if (templateIds.size() != 3) {
            return false;
        }

        final Set<String> requiredExtensions = new HashSet<String>();
        requiredExtensions.add(Constants.SIMPLE_OBSERVATIONS);
        requiredExtensions.add(Constants.VITAL_SIGNS_OBSERVATIONS);
        requiredExtensions.add(Constants.ASTM_HL7CONTINUALITY_OF_CARE_DOCUMENT);
        for (II instanceId : templateIds) {
            final String extension = instanceId.getExtension();
            if (!requiredExtensions.contains(extension)) {
                LOGGER.warn("This template id extension {} is not specific for a vital sign. The vital sign specific extension are {}.", extension, requiredExtensions);
                return false;
            }
        }

        return true;
    }

    private void processVitalSign(POCDMT000040Observation observation) {
        LOGGER.debug("Tries to parse this Vital Sign {}", observation);
        final CD code = observation.getCode();
        buildCodeURI(code);

        final IVLTS effectiveTime = observation.getEffectiveTime();
        final String effectiveTimeValue = effectiveTime.getValue();
        System.out.println("effectiveTime -->" + effectiveTimeValue);

        final List<ANY> value = observation.getValue();
        if (value.size() != 1) {
            LOGGER.warn("To many values for the vital sign value, only one expected");
            return;
        }

        final PQ quantity;
        try {
            quantity = (PQ) value.iterator().next();
        } catch (ClassCastException exception) {
            LOGGER.warn("The value list for this vital sign contains wrong types, only PQ allowed.");
            return;
        }

        final String quantityValue = quantity.getValue();
        final String quantityUnit = quantity.getUnit();
        System.out.println("quantityValue -->" + quantityValue);
        System.out.println("quantityUnit -->" + quantityUnit);
    }

    private String buildCodeURI(CD cd) {
        final String code = cd.getCode();
        final String displayName = cd.getDisplayName();
        final String codeSystem = cd.getCodeSystem();
        final String codeSystemName = cd.getCodeSystemName();

        System.out.println("code -->" + code);
        System.out.println("displayName -->" + displayName);
        System.out.println("codeSystem -->" + codeSystem);
        System.out.println("codeSystemName -->" + codeSystemName);

        return null;
    }

    private String buildUnitURI(CD cd) {


        return null;
    }

    private boolean isProblemEntry(POCDMT000040Observation observation) {
        final List<II> templateIds = observation.getTemplateId();
        if (templateIds.size() != 2) {
            return false;
        }

        final Set<String> requiredExtensions = new HashSet<String>();
        requiredExtensions.add(Constants.PROBLEM_OBSERVATION);
        requiredExtensions.add(Constants.PROBLEM_ENTRY);
        for (II instanceId : templateIds) {
            final String extension = instanceId.getExtension();
            if (!requiredExtensions.contains(extension)) {
                LOGGER.warn("This template id extension {} is not specific for a problem entry. The problem entry specific extension are {}.", extension, requiredExtensions);
                return false;
            }
        }

        return true;
    }

    private void processProblemEntry(POCDMT000040Observation observation) {
        LOGGER.debug("Tries to parse this ProblemEntry : {}", observation);
        final CD code = observation.getCode();
        buildCodeURI(code);

        final List<ANY> value = observation.getValue();
        if (value.size() != 1) {
            LOGGER.warn("To many values for the vital sign value, only one expected");
            return;
        }
        
        final CD valueCode =  (CD) value.iterator().next();
        buildCodeURI(valueCode);


        final IVLTS effectiveTime = observation.getEffectiveTime();
        final List<JAXBElement<? extends QTY>> timeIntervals = effectiveTime.getRest();
        for (JAXBElement timeInterval_JAXB : timeIntervals) {
            final IVXBTS ivxbts = (IVXBTS) timeInterval_JAXB.getValue();
            System.out.println("time inerval -->" + ivxbts.getValue());
        }
    }

    private void process(POCDMT000040SubstanceAdministration substanceAdministration) {
        LOGGER.debug("Tries to parse this Medication : {}", substanceAdministration);
    }
}
