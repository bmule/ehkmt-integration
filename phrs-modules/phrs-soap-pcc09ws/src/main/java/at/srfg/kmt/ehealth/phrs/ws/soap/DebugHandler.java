/*
 * Project :iCardea
 * File : SOAPHandlerAddressEnricher.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;


import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.xpath.XPathExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This SOAP handler is used to used to display in a human readable form 
 * the SOAP message parts (header and body). <br/>
 * This SOAP handler is logging the SOAP message parts (header and body) using
 * the underlying logging frame-work. <br/>
 * This class was not designed to be extended.
 * 
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public final class DebugHandler implements SOAPHandler<SOAPMessageContext> {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.DebugHandler</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DebugHandler.class);

    /**
     * Builds a <code>DebugHandler</code> instance.
     *
     */
    public DebugHandler() throws MalformedURLException {
        // UNIMPLEMENTED
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
        try {
            final SOAPHeader header = message.getSOAPHeader();
            process(header);
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
     * Logs a SOAP message header. if the message is null then this method has 
     * no effect (it only logs the null message).
     * 
     * @param header the SOAP level to log.
     */
    private void process(SOAPHeader header) {

        if (header == null) {
            LOGGER.debug("Header null nothing to process");
            return;
        }

        final Iterator childElements = header.examineAllHeaderElements();
        if (!childElements.hasNext()) {
            LOGGER.debug("No Header to process");
            return;
        }

        try {
            final String headerToString = Util.toString(header);
            LOGGER.debug("The header to precess is : {}", headerToString);
        } catch (Exception exception) {
            LOGGER.error("The SOAP header can not be parsed.");
            LOGGER.error(exception.getMessage(), exception);
        }
    }

    /**
     * Logs a SOAP message body.
     * 
     * @param body  the SOAP message body to be log log.
     */
    private void process(SOAPBody body) {
    }
}
