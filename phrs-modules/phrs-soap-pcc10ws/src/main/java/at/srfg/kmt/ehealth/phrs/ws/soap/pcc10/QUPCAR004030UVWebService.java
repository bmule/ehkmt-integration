/*
 * Project :iCardea
 * File : QUPCAR004040UVWebService.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import java.util.LinkedHashSet;
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
 * Handles HL7 PCC-10 requests. 
 * More precisely this SOAP based service uses a set of parsers to parse 
 * HL7 messages and to imported the PHRS repository. <br/>
 * This WS can parse :
 * <ul>
 * <li> HL7 V3 ProblemsEntries  
 * <li> HL7 V3 Vital Signs
 * <li> HL7 V3 Medication  
 * </ul>
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
     * All the parsers used in this ws.
     */
    private final Set<Parser<REPCMT004000UV01PertinentInformation5>> parsers;

    /**
     * Builds a <code>QUPCAR004030UVWebService</code> instance.
     */
    public QUPCAR004030UVWebService() {
        parsers = new LinkedHashSet<Parser<REPCMT004000UV01PertinentInformation5>>();
        parsers.add(new VitalSignParser());
        parsers.add(new ProblemEntryParser());
        parsers.add(new MedicationParser());
        LOGGER.debug("Available parsers {}", parsers);
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
                : Boolean.parseBoolean(property.trim());
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
        final JAXBElement<REPCMT004000UV01RecordTarget> recordTarget_JAXB = 
                careProvisionEvent.getRecordTarget();
        final REPCMT004000UV01RecordTarget recordTarget = recordTarget_JAXB.getValue();
        final JAXBElement<COCTMT050000UVPatient> patient_JAXB = recordTarget.getPatient();
        final COCTMT050000UVPatient patient = patient_JAXB.getValue();
        final List<II> ids = patient.getId();
        
        if (ids.isEmpty()) {
            LOGGER.error("No ids found - the parsing can not be appiled");
            return;
        }
        
        if (ids.size() > 1) {
            LOGGER.warn("Mutiple ids found, fuzzy logic - the involved patient Id it may be wrong.");
        }
        // Because there is only one user id and name space (spcifed by FROTH) I
        // only use the fist one. The list must ocontain only one element
        final String patientId = ids.get(0).getExtension();
        
        final List<REPCMT004000UV01PertinentInformation5> pertinentInformations =
                careProvisionEvent.getPertinentInformation3();

        LOGGER.debug("Pertinent Information5 amount to process is {}",
                pertinentInformations.size());
        for (REPCMT004000UV01PertinentInformation5 information5 : pertinentInformations) {
            parse(information5, patientId);
        }
    }
    
    /**
     * Uses all the registered parsers to parse the given 
     * <code>REPCMT004000UV01PertinentInformation5</code>.
     * 
     * @param information5 the <code>REPCMT004000UV01PertinentInformation5</code>
     * to be parsed.
     */
    private void parse(REPCMT004000UV01PertinentInformation5 information5, String id) {
        for (Parser<REPCMT004000UV01PertinentInformation5> parser : parsers) {
            if (parser.canParse(information5)) {
                try {
                    parser.parse(information5, id);
                    break;
                } catch (ParserException exception) {
                    LOGGER.warn("REPCMT004000UV01PertinentInformation5 can not be parsed");
                    LOGGER.warn(exception.getMessage(), exception);
                }
            }
        }
    }
}
