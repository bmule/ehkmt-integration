/*
 * Project :iCardea
 * File : SOAPHandlerAddressEnricher.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;


import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <code>SOAPHandler</code> implementation able to enrich the SOAP envelope
 * header with information about the response end-point (URL) following the
 * <a href="http://en.wikipedia.org/wiki/WS-Addressing">WS-Addressing</a>
 * standards. <br/>
 * This class is not design to be extended.
 *
 * @author mradules
 * @version 0.1
 * @since 0.1
 */
public final class WSAdressingHeaderEnricher implements SOAPHandler<SOAPMessageContext> {

    private static final String NS_PREFIX = "wsa";

    private static final String REPLY_TO_LOCAL_NAME = "ReplyTo";

    private static final String ADDRESS_LOCAL_NAME = "Address";

    private static final String WSADRESSING_NS = "http://www.w3.org/2005/08/addressing";

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.WSAdressingHeaderEnricher</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WSAdressingHeaderEnricher.class);

    /**
     * The URL where the response will be send.
     */
    private final String endpoint;

    /**
     * Builds a <code>WSAdressingHeaderEnricher</code> for a given end-point 
     * URL.
     * 
     * 
     * @param endpoint the end point for this <code>WSAdressingHeaderEnricher</code>
     * instance, it can not be null or empty string.
     * @throws NullPointerException if the <code>endpoint</code> argument is null
     * or empty string.
     */
    public WSAdressingHeaderEnricher(String endpoint) {
        if (endpoint == null || endpoint.isEmpty()) {
            final NullPointerException nullPointerException = 
                    new NullPointerException("The endpoint argument can not be null or empty sting");
            LOGGER.error(nullPointerException.getMessage(), nullPointerException);
            throw nullPointerException;
        }
        
        this.endpoint = endpoint;
    }

    @Override
    public Set<QName> getHeaders() {
        final Set headers = new HashSet();
        headers.add(new QName(WSADRESSING_NS, REPLY_TO_LOCAL_NAME));
        return headers;
    }

    @Override
    public void close(MessageContext mc) {
        ;
    }

    @Override
    public boolean handleFault(SOAPMessageContext c) {
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        SOAPMessage msg = context.getMessage();
        if ((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)) {
            try {
                final SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
                final SOAPHeader header = envelope.addHeader();
                final Name replyTo =
                        envelope.createName(REPLY_TO_LOCAL_NAME, NS_PREFIX, WSADRESSING_NS);
                final SOAPElement replyToElement =
                        header.addHeaderElement(replyTo);

                final Name adress = envelope.createName(ADDRESS_LOCAL_NAME, NS_PREFIX, WSADRESSING_NS);
                final SOAPElement adressElement =
                        replyToElement.addChildElement(adress);

                adressElement.setValue(endpoint);
                msg.saveChanges();

            } catch (SOAPException sOAPException) {
                LOGGER.error(sOAPException.getMessage(), sOAPException);
                return false;
            }
        }

        return true;
    }
}
