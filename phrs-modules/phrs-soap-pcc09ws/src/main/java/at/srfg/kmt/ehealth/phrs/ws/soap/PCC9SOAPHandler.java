/*
 * Project :iCardea
 * File : PCC9SOAPHandler.java
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;

import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

/**
 * The
 * <code>SOAPHandler</code> used to manage and manipulate SOAP header/body
 * information. More precisely this
 * <code>SOAPHandler<code/> implementation
 * extracts
 * <ul>
 * <li> the response end point address (URI). The end point address (URI) is
 * trated according with the
 * <a href="http://en.wikipedia.org/wiki/WS-Addressing">WS-Addressing</a>
 * standard and it it extracted from the SOAP message header.
 * <li> the involved user (patient). This information is extracted from the
 * SOAP message body.
 * <li> the care provision code.  This information is extracted from the
 * SOAP message.
 * </ul>
 * <br/>
 * This class is not desing to be extended.
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public final class PCC9SOAPHandler implements SOAPHandler<SOAPMessageContext> {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.PCC9Endpoint</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC9SOAPHandler.class);

    /**
     * Builds a
     * <code>PCC9SOAPHandler</code>
     */
    public PCC9SOAPHandler() {
        // UNIMPLEMENTED
    }

    /**
     * It returns null, always.
     *
     * @return returns null, always.
     */
    @Override
    public Set<QName> getHeaders() {
        // TODO : null is wrong, use the header Q name !
        return null;
    }

    /**
     * Extracts the following information :
     * <ul> 
     * <li> the response end point address (URI). The end point address
     * (URI) is trated according with the 
     * <a href="http://en.wikipedia.org/wiki/WS-Addressing">WS-Addressing</a>
     * standard and it it extracted from the SOAP message header. 
     * <li> the involved user (patient). This information is extracted from the 
     * SOAP message body. 
     * <li> the care provision code. This information is extracted
     * from the SOAP message. 
     * </ul>
     *
     * @param context  the message context to be processed.
     * @return true to continue processing or false to block processing.
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
            LOGGER.error("Error during the SAOP message processing. The SOAP message chain processing is interupted !");
            LOGGER.error(exception.getMessage(), exception);
            return false;
        }

        LOGGER.debug("handleMessage {}", message.toString());
        return true;
    }

    /**
     * Extracts the response end point URI from a given <code>SOAPHeader</code>.
     * The end point address (URI) is treated according with the
     * <a href="http://en.wikipedia.org/wiki/WS-Addressing">WS-Addressing</a>
     * standard. <br>
     * If the <code>header</code> is null then this method has no effect.
     * 
     * @param header the header to process.
     */
    private void process(SOAPHeader header) {

        if (header == null) {
            LOGGER.debug("SOPA Header is null nothing to process");
            return;
        }

        final Iterator childElements = header.examineAllHeaderElements();
        if (!childElements.hasNext()) {
            LOGGER.debug("No SOAP Header to process");
            return;
        }

        NodeList nodes = header.getElementsByTagName("wsa:Address");

        final int length = nodes.getLength();
        if (length != 1) {
            final IllegalStateException illegalStateException =
                    new IllegalStateException("The SOAP Header must contain only one WSA Address element.");
            LOGGER.error(illegalStateException.getMessage(), illegalStateException);
            throw illegalStateException;
        }

        final Node wsAdressNode = (Node) nodes.item(0);
        final String wsAddress = wsAdressNode.getTextContent();
        LOGGER.debug("Response end point address is {}", wsAddress);
    }

    /**
     * Extracts the patient name and care provision code from a given 
     * <code>SOAPBody</code>. <br>
     * If the <code>header</code> is null then this method has no effect.
     * 
     * @param body the body to process.
     */
    private void process(SOAPBody body) {
    }

    /**
     * It returns true, always.
     * 
     * @param context the message to process.
     * @return returns true, always.
     */
    @Override
    public boolean handleFault(SOAPMessageContext context) {
        final SOAPMessage message = context.getMessage();
        LOGGER.debug("handleFault {}", message.toString());

        return true;
    }

    @Override
    public void close(MessageContext mc) {
        LOGGER.debug("{} is closed", PCC9SOAPHandler.class.getName());
    }
}
