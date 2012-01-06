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
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.xpath.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

/**
 * The
 * <code>SOAPHandler</code> used to manage and manipulate SOAP header/body
 * information. This handler is used to extract the protocol id from the
 *
 *
 * @author Mihai
 * @version 0.1
 * @since 0.1
 */
public class PCC9SOAPHandler implements SOAPHandler<SOAPMessageContext> {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.PCC9Endpoint</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC9SOAPHandler.class);


    /**
     * Builds a <code>PCC9SOAPHandler</code> 
     */
    public PCC9SOAPHandler()  {
        // UNIMPLEMENTED
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

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

    private void process(SOAPHeader header) throws XPathExpressionException {

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

    private void process(SOAPBody body) {
    }

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
