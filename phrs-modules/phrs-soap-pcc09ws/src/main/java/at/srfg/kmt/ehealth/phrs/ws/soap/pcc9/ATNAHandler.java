/*
 * Project :iCardea
 * File : DebugHandler.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.srdc.icardea.atnalog.client.Audit;


/**
 * This SOAP handler is used to used to log any PCC9 interaction to an ATNA
 * server. The ATNA message contains : <ul> <li> a message type - always "PCC-9"
 * <li> a code - the care provision code - extracted from the message body. <li>
 * an ID - the protocol id - extracted from the message body. <li> a requester
 * Role - always "IHE+RFC-3881". <ul> <br/> This handler is configurated via a
 * properties file named "pcc-9-atna.properties", placed in the classpath. If
 * this file is not loaded then this handler has no effect (no ATNA messages are
 * send).<br/> This class was not designed to be extended.
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public final class ATNAHandler implements SOAPHandler<SOAPMessageContext> {

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
            LoggerFactory.getLogger(ATNAHandler.class);

    /**
     * The only ATNA message type used in this handler.
     */
    private final String MESSAGE_TYPE = "PCC-9";

    /**
     * The name for the configuration file.
     */
    private final String CONFIG_FILE = "pcc-9-atna.properties";

    /**
     * The audit client.
     */
    private Audit audit;

    /**
     * Builds a
     * <code>ATNAHandler</code> instance based on a property file configuration
     * file, the file is named "pcc-9-atna.properties" and it is placed in the
     * classpath. If this file is not loaded then this handler has no effect (no
     * ATNA messages are send).
     */
    public ATNAHandler() {
        final ClassLoader classLoader = ATNAHandler.class.getClassLoader();
        final InputStream resourceAsStream =
                classLoader.getResourceAsStream(CONFIG_FILE);
        
        if (resourceAsStream == null) {
            LOGGER.warn("The ATNA configunration file named {} must be placed in the classpath", CONFIG_FILE);
            LOGGER.warn("NO ATNA AUDIT MESSAGES CAN BE SEND!");
            return;
        }
        
        try {
            final Properties config = new Properties();            
            config.load(resourceAsStream);
            final String host = config.getProperty("atna-server-host").trim();
            final String port = config.getProperty("atna-server-port").trim();
            audit = new Audit(host, Integer.parseInt(port));
        } catch (Exception exception) {
            LOGGER.warn("NO ATNA AUDIT MESSAGES CAN BE SEND!");
            LOGGER.warn(exception.getMessage(), exception);
        }
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
        logMimeHeaders(mimeHeaders);
        
        try {
            final SOAPHeader header = message.getSOAPHeader();
            final SOAPBody body = message.getSOAPBody();
            process(body);
        } catch (Exception exception) {
            LOGGER.error("Error during the SAOP message processing.");
            LOGGER.error(exception.getMessage(), exception);
        }
        
        LOGGER.debug("handleMessage {}", message.toString());
        return true;
    }

    /**
     * Logs all the given mime-type headers.
     *
     * @param headers the mime-type header to be logged.
     */
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
    }

    /**
     * Logs a SOAP message body using the ATNA client.
     *
     * @param body the SOAP message body to be log log.
     */
    private void process(SOAPBody body) {
        if (body == null) {
            LOGGER.debug("SOAP Body null nothing to process");
            return;
        }
        
        final Iterator childElements = body.getChildElements();
        if (!childElements.hasNext()) {
            LOGGER.debug("No Body to process");
            return;
        }
        
        String code = Util.getCareProvisionCode(body);
        if (code == null) {
            return;
        }
        code = code.trim();
        
        String patientId = Util.getPatientId(body);
        if (patientId == null) {
            return;
        }
        patientId = patientId.trim();
        
        String patientNames = Util.getPatientName(body);
        if (patientNames == null) {
            return;
        }
        patientNames = patientNames.trim();
        
        
        final String message =
                audit.createMessage(MESSAGE_TYPE, patientId, code, REQUESTER_ROLE);
        try {
            LOGGER.debug("Tries to send this the following ATNA message {}", message);
            if (audit != null) {
                audit.send_udp(message.getBytes());
            } else {
                LOGGER.warn("The ATNA was not initailized, the ATNA message can not eb send.");
            }
            
            LOGGER.debug("The ATNA message was send.");
            
        } catch (Exception exception) {
            LOGGER.warn("The folowing ATNA message can not be send {}", message);
            LOGGER.warn(exception.getMessage(), exception);
        }
    }
}
