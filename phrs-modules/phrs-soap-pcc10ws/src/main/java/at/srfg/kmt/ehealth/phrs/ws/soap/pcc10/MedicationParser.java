/*
 * Project :iCardea
 * File : MedicationParser.java
 * Encoding : UTF-8
 * Date : Feb 10, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import javax.xml.bind.JAXBElement;
import org.hl7.v3.POCDMT000040SubstanceAdministration;
import org.hl7.v3.REPCMT004000UV01PertinentInformation5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to transform HL7 Medication messages in PHRS information.
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class MedicationParser implements Parser<REPCMT004000UV01PertinentInformation5> {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.MedicationParser</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MedicationParser.class);

    @Override
    public boolean canParse(REPCMT004000UV01PertinentInformation5 toParse) {

        final JAXBElement<POCDMT000040SubstanceAdministration> substanceAdministration_JAXB =
                toParse.getSubstanceAdministration();
        final boolean hasSubstanceAdministration = substanceAdministration_JAXB != null;
        return hasSubstanceAdministration;
    }

    @Override
    public void parse(REPCMT004000UV01PertinentInformation5 toParse) throws ParserException {
        LOGGER.debug("Tries to parse {}", toParse);
    }
}
