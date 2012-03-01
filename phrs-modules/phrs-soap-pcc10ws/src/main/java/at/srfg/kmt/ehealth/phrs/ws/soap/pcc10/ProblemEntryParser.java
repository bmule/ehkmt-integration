/*
 * Project :iCardea
 * File : MedicationParser.java
 * Encoding : UTF-8
 * Date : Feb 10, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to transform HL7 problem entry in PHRS information.
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class ProblemEntryParser implements Parser<REPCMT004000UV01PertinentInformation5> {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.ProblemEntryParser</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProblemEntryParser.class);

    /**
     * Builds
     * <code>ProblemEntryParser</code> instance.
     */
    public ProblemEntryParser() {
        // UNIMPLEMENTED
    }

    @Override
    public boolean canParse(REPCMT004000UV01PertinentInformation5 toParse) {
        final JAXBElement<POCDMT000040Observation> observation_JAXB =
                toParse.getObservation();
        final boolean hasObservation = observation_JAXB != null;
        if (!hasObservation) {
            LOGGER.debug("This is not a HL7 observation, it can not be parsed.");
            return false;
        }

        final POCDMT000040Observation observation = observation_JAXB.getValue();
        final boolean ispProblemEntry = isProblemEntry(observation);
        if (!ispProblemEntry) {
            LOGGER.debug("This is not a HL7 problem entry, it can not be parsed.");
            return false;
        }

        return true;
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

    @Override
    public void parse(REPCMT004000UV01PertinentInformation5 toParse, String userId) throws ParserException {
        LOGGER.debug("Tries to parse {}", toParse);

        final JAXBElement<POCDMT000040Observation> observation_JAXB =
                toParse.getObservation();
        final POCDMT000040Observation observation = observation_JAXB.getValue();

        LOGGER.debug("Tries to parse this ProblemEntry : {}", observation);
        final CD code = observation.getCode();
        buildCodeURI(code);

        final List<ANY> value = observation.getValue();
        if (value.size() != 1) {
            LOGGER.warn("To many values for the vital sign value, only one expected");
            return;
        }

        final CD valueCode = (CD) value.iterator().next();
        buildCodeURI(valueCode);


        final IVLTS effectiveTime = observation.getEffectiveTime();
        final List<JAXBElement<? extends QTY>> timeIntervals = effectiveTime.getRest();
        for (JAXBElement timeInterval_JAXB : timeIntervals) {
            final IVXBTS ivxbts = (IVXBTS) timeInterval_JAXB.getValue();
            System.out.println("time inerval -->" + ivxbts.getValue());
        }
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

    @Override
    public String toString() {
        return "ProblemEntryParser";
    }
}
