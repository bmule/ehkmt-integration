/*
 * Project :iCardea
 * File : MedicationParser.java
 * Encoding : UTF-8
 * Date : Feb 10, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicationClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.*;
import javax.xml.bind.JAXBElement;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to transform HL7 vital sign in PHRS information.
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class VitalSignParser implements Parser<REPCMT004000UV01PertinentInformation5> {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.VitalSignParser</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VitalSignParser.class);

    /**
     * The connection to the triple store.
     */
    private final GenericTriplestore triplestore;

    /**
     * The client used to store the vital sign related information.
     */
    private final VitalSignClient client;

    /**
     * Builds
     * <code>VitalSignParser</code> instance.
     */
    public VitalSignParser() {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        triplestore = connectionFactory.getTriplestore();
        client = new VitalSignClient(triplestore);
        client.setCreator(Constants.EHR_OWNER);
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
        final boolean isVitalSign = isVitalSing(observation);
        if (!isVitalSign) {
            LOGGER.debug("This is not a HL7 vital sign, it can not be parsed.");
            return false;
        }

        return true;
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

    @Override
    public void parse(REPCMT004000UV01PertinentInformation5 toParse, String userId) throws ParserException {
        LOGGER.debug("Tries to parse {}", toParse);

        final JAXBElement<POCDMT000040Observation> observation_JAXB =
                toParse.getObservation();
        final POCDMT000040Observation observation = observation_JAXB.getValue();



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

    private String buildCodeSystemURI(String codeSystemName, String codeSystemCode) 
            throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.CODE_SYSTEM_CODE, codeSystemCode);

        final Iterable<String> uris =
                triplestore.getForPredicatesAndValues(queryMap);
        final Iterator<String> iterator = uris.iterator();
        if (!iterator.hasNext()) {
            
            final String newCodeSystemURI =
                    triplestore.persist(Constants.CODE_SYSTEM_CODE, 
                                        codeSystemCode, 
                                        ValueType.LITERAL);

            final String name = codeSystemName == null 
                    ? "NO CODESYSTEM NAME" 
                    : codeSystemName;

            triplestore.persist(newCodeSystemURI,
                    Constants.CODE_SYSTEM_NAME,
                    codeSystemName,
                    ValueType.LITERAL);
            
            final String msg = 
                    String.format("New code system created. Code System code = %s, code system name %s and code system URI = %s", codeSystemCode, codeSystemName, newCodeSystemURI);
            LOGGER.debug(msg);
        }

        // TODO : this iterator must contain only one element.
        // build a check here !
        final String result = iterator.next();
        return result;

    }

    @Override
    public String toString() {
        return "VitalSignParser";
    }
}
