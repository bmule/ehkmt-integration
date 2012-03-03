/*
 * Project :iCardea
 * File : DebugHandler.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import org.hl7.v3.MCCIIN000002UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Iterator;
import java.util.Set;


/**
 * This SOAP handler creates a new PCC09 message to an EHR
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public final class PCC09EHRHandler implements SOAPHandler<SOAPMessageContext> {

    /**
     * The value for the requester role used for this handler.
     */
    public static final String REQUESTER_ROLE = "IHE+RFC-3881";

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.ATNAHandler</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC09EHRHandler.class);

    /**
     * The only ATNA message type used in this handler.
     */
    private final String MESSAGE_TYPE = "PCC-9";


    /**
     * Builds a
     * <code>PCC09EHRHandler</code> instance based on a property file configuration
     * file, the file is named "pcc-9.properties" and it is placed in the
     * classpath.
     */
    public PCC09EHRHandler() {

    }

    /**
     * This method returns null, always.
     *
     * @return returns null, always.
     */
    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    /**
     * This method is not implemented.
     *
     * @param mc not used, the method is not implemented.
     */
    @Override
    public void close(MessageContext mc) {
        // UNIMPLEMENTED
    }

    /**
     * This method return true, always.
     *
     * @param c not used.
     * @return it return true, always.
     */
    @Override
    public boolean handleFault(SOAPMessageContext c) {
        return true;
    }

    /**
     * Logs the given SOAP message.
     *
     * @param context the SOAP message to log.
     * @return true, always.
     */
    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        final SOAPMessage message = context.getMessage();
        final MimeHeaders mimeHeaders = message.getMimeHeaders();
        //logMimeHeaders(mimeHeaders);

        try {
            final SOAPHeader header = message.getSOAPHeader();
            final SOAPBody body = message.getSOAPBody();
            process(body);
        } catch (Exception exception) {
            LOGGER.error("PCC09EHRHandler Error during the SAOP  1 message processing.");
            LOGGER.error(exception.getMessage(), exception);
        }

        LOGGER.debug("handleMessage {}", message.toString());
        return true;
    }

    /*
     * Logs all the given mime-type headers.
     *
     * @param headers the mime-type header to be logged.

    private void logMimeHeaders(MimeHeaders headers) {
        if (headers == null) {
            LOGGER.debug("No mime headers found");
            return;
        }
        
        final StringBuffer msg = new StringBuffer("Available SOAP Headers :\n");
        for (Iterator headersIt = headers.getAllHeaders(); headersIt.hasNext();) {
            final MimeHeader header = (MimeHeader) headersIt.next();
            msg.append("Header ");
            msg.append(header.getName());
            msg.append(" = ");
            msg.append(header.getValue());
            msg.append('\n');
        }
        
        LOGGER.debug(msg.toString());
    } */

    /**
     * Logs a SOAP message body using the ATNA client.
     *
     * @param body the SOAP message body to be log log.
     */
    private void process(SOAPBody body) {
        if (body == null) {
            LOGGER.debug("PCC09EHRHandler SOAP Body null nothing to process");
            return;
        }

        final Iterator childElements = body.getChildElements();
        if (!childElements.hasNext()) {
            LOGGER.debug("PCC09EHRHandler No Body to process");
            return;
        }

        final String code = Util.getCareProvisionCode(body);
        if (code == null) {
            LOGGER.debug("PCC09EHRHandler No Care Provision Code to process, no ATNA message will be send.");
            return;
        }

        final String patientId = Util.getPatientId(body);
        if (patientId == null) {
            LOGGER.debug("PCC09EHRHandler No Patient Id Code to process, no ATNA message will be send.");
            return;
        }

        try {

            LOGGER.debug("Tries to send PCC09 message for patientId= " + patientId + " careprovision code=" + code);
            PCC09Query client = new PCC09Query();
            MCCIIN000002UV01 ack = client.sendPcc09Message(patientId, code);

            if (ack != null) {
                LOGGER.debug("Ackknowledgement received for patientId= " + patientId + " careprovision code=" + code);
                //audit.send_udp(message.getBytes());
            } else {
                LOGGER.warn("FAIL Null response to PCC09 message ");
            }
        } catch (Exception exception) {
            LOGGER.warn("PCC09EHRHandler "+exception.getMessage(), exception);
        }
    }
}
