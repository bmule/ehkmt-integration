/*
 * Project :iCardea
 * File : DistpacherTask.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemEntryClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.util.*;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This
 * <code>Runnable</code> class is used to generate a PCC10 message for a given
 * set of arguments, the message will be send to a given end-point. All the
 * information about the PCC10 message are contained in a Key-Pair set specified
 * via the constructor. <br/> This class was not designed to be extended.
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
final class PCC10Task implements Runnable {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space cd
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.PCC9Endpoint</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC10Task.class);

    /**
     * All the properties for the PCC10 message.
     */
    private final Map<PCC10TaskProperty, Object> properties;

    /**
     * Builds a
     * <code>PCC10Task</code> for a given set of Key-Pair set.
     *
     * @param properties all the properties required for a PCC10 message, it can
     * not be null.
     * @throws NullPointerException if the
     * <code>properties</code> argument is null.
     */
    public PCC10Task(final Map<PCC10TaskProperty, Object> properties) {

        if (properties == null) {
            final NullPointerException exception =
                    new NullPointerException("The properties argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public void run() {
        LOGGER.debug("Tries to execute this tasks with the following propertiess : {}", properties);
        final String patientId = (String) properties.get("patientId");
        final String patientNames = (String) properties.get("patientNames");
        final String careProvisionCode = (String) properties.get("careProvisionCode");
        final String responseEndpointURI = (String) properties.get("responseEndpointURI");

        if (responseEndpointURI == null
                || careProvisionCode == null
                ||  (patientId == null && patientNames == null)) {
            LOGGER.error("This properties map [{}] does not contain enought informations. Task aborted.", properties);
            return;
        }

        try {
//            final QUPCIN043200UV01 pcc10Request = buildMessage();
//                    LOGGER.info("Tries to send a PCC10 query ({}) to {}", pcc10Request, "http://localhost:8989/testws/pcc10");
//        final MCCIIN000002UV01 ack =
//                SendPcc10Message.sendMessage(pcc10Request, "http://localhost:8989/testws/pcc10");
            final QUPCIN043200UV01 pcc10Request = buildMessage();
            LOGGER.info("Tries to send a PCC10 query ({}) to {}", pcc10Request, responseEndpointURI);
            final MCCIIN000002UV01 ack =
                    SendPcc10Message.sendMessage(pcc10Request, responseEndpointURI);

            LOGGER.info("Acknowledge (respense) is : {}.", ack);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }



        LOGGER.debug("Task end.");
    }

    private QUPCIN043200UV01 buildMessage()
            throws TripleException, IllegalAccessException, InstantiationException {

        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final ProblemEntryClient client = new ProblemEntryClient(triplestore);

        // this adds a problem-symptom named fever
        client.addProblemEntry(
                owner,
                Constants.HL7V3_SYMPTOM,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                "Free text note for the problem.",
                Constants.HL7V3_FEVER);

        final Iterable<String> uris = client.getProblemEntriesURIForUser(owner);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }

        final QUPCIN043200UV01 pcc10Message = ProblemEntryPCC10.getPCC10Message(beans);
        return pcc10Message;
    }
}
