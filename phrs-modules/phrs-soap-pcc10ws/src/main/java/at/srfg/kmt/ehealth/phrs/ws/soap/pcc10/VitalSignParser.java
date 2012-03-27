/*
 * Project :iCardea
 * File : MedicationParser.java
 * Encoding : UTF-8
 * Date : Feb 10, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ClientException;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.TermClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
     * The client used to access the all the iCardea terms.
     */
    private TermClient termClient;

    /**
     * Builds
     * <code>VitalSignParser</code> instance.
     */
    public VitalSignParser() {
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        triplestore = connectionFactory.getTriplestore();
        client = new VitalSignClient(triplestore);
        termClient = new TermClient(triplestore);
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

        //removed != 3, expect one or more
        if (templateIds.size() == 0) {
            LOGGER.error("No template extensions found, observation.getTemplateId() size = 0");
            return false;
        }


        boolean valid=false;

        for (II instanceId : templateIds) {
            String root = instanceId.getRoot();
            if(root==null){
                root=   instanceId.getExtension(); //this will support test samples
            }
            //TODO FIX VitalSignPCC10
            //Not extension... see http://wiki.ihe.net/index.php?title=1.3.6.1.4.1.19376.1.5.3.1.4.13.2
            // String extension = instanceId.getExtension();
            if(root != null)  root=root.trim();

            if (Constants.VITAL_SIGNS_OBSERVATIONS.equals(root)) {

                valid=true;
            }
            LOGGER.warn("found root {}",root);
        }


        if( ! valid)  {
            LOGGER.warn("The template id root is not for a vital sign for {} ", Constants.VITAL_SIGNS_OBSERVATIONS);
            return false;
        } else {
            LOGGER.warn("PCC-10 Vital Sign received");

        }
 //       final Set<String> requiredExtensions = new HashSet<String>();
 //       requiredExtensions.add(Constants.VITAL_SIGNS_OBSERVATIONS);

        //These are no longer required, we only set one  VITAL_SIGNS_OBSERVATIONS extension!!
        // requiredExtensions.add(Constants.SIMPLE_OBSERVATIONS);
        // requiredExtensions.add(Constants.ASTM_HL7CONTINUALITY_OF_CARE_DOCUMENT);
//        for (II instanceId : templateIds) {
//            final String extension = instanceId.getExtension();
//            if (!requiredExtensions.contains(extension)) {
//                LOGGER.warn("This template id extension {} is not specific for a vital sign. The vital sign specific extension are {}.", extension, requiredExtensions);
//                return false;
//            }
//        }


        return true;
    }

    @Override
    public void parse(REPCMT004000UV01PertinentInformation5 toParse, String userId) throws ParserException {
        LOGGER.debug("Tries to parse {}", toParse);

        final JAXBElement<POCDMT000040Observation> observation_JAXB =
                toParse.getObservation();
        final POCDMT000040Observation observation = observation_JAXB.getValue();


        LOGGER.debug("VitalSignParser Tries to parse this Vital Sign {}", observation);
        final CD code = observation.getCode();
        final String codeURI;
        try {
            codeURI = Util.buildCodeURI(termClient, code);
        } catch (ClientException exception) {
            LOGGER.error("The  observation.getCode() code information can not be processed, parse fails.");
            LOGGER.error(exception.getMessage(), exception);
            return;
        }

        final CS statusCode = observation.getStatusCode();
        final String statusURI = Util.getStatusURI(statusCode);

        final IVLTS effectiveTime = observation.getEffectiveTime();
        final String effectiveTimeValue;

        if(effectiveTime == null){
            effectiveTimeValue = "";
        }  else {
            effectiveTimeValue = effectiveTime.getValue();
        }

//        System.out.println("effectiveTime -->" + effectiveTimeValue);

        final List<ANY> value = observation.getValue();

        //not necessary to validate, we just want one
//        if (value.size() != 1) {
//            LOGGER.warn("To many values for the vital sign value, only one expected");
//            return;
//        }

        if (value.size() == 0) {
            LOGGER.warn("No values found for the vital sign value, only one expected");
            return;
        }

        final PQ quantity;
        try {
            quantity = (PQ) value.iterator().next();
        } catch (ClassCastException exception) {
            LOGGER.warn("The value list for this vital sign quantity contains wrong types, only PQ allowed.");
            return;
        } catch (Exception exception) {
            LOGGER.warn("PQ exception to get vital sign quantity.");
            return;
        }

        final String quantityValue = quantity.getValue();

        final String unitURI = Util.getUnitURI(quantity);
        final String vitalSignMsg = String.format(" Vital sign[ owner=%s, code=%s, effectiveTime=%s, statusURI=%s, quantityValue=%s, unitURI=%s]", userId, codeURI, effectiveTimeValue, statusURI, quantityValue, unitURI);
        try {
            LOGGER.debug("Tries to persist " + vitalSignMsg);
            client.addVitalSign(userId,
                    codeURI,
                    "importerd from EHR",
                    effectiveTimeValue,
                    statusURI,
                    quantityValue,
                    unitURI);
            LOGGER.debug(vitalSignMsg + " was persisted.");
            //show on console
            System.out.println("PCC-10 Vital Signs stored");
        } catch (TripleException tripleException) {
            LOGGER.error("The vital sign graph can not be created.");
            LOGGER.error(tripleException.getMessage(), tripleException);
        }


    }

    @Override
    public String toString() {
        return "VitalSignParser";
    }
}
