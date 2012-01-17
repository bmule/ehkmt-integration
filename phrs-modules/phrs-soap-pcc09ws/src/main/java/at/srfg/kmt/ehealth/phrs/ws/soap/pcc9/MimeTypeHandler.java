/*
 * Project :iCardea
 * File : MimeTypeHandler.java
 * Encoding : UTF-8
 * Date : Jan 16, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class MimeTypeHandler implements SOAPHandler<SOAPMessageContext> {
    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.MimeTypeHandler</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MimeTypeHandler.class);

    public Set<QName> getHeaders() {
        return null;
    }

    public void close(MessageContext context) {
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    public boolean handleMessage(SOAPMessageContext context) {
        final MimeHeaders mimeHeaders = context.getMessage().getMimeHeaders();

        LOGGER.debug("Inital Headers.");
        logHeader(mimeHeaders, CONTENT_TYPE_HEADER);

        mimeHeaders.setHeader("Content-Type", "appliaction/soap+xml");

        
        LOGGER.debug("Headers agter mime type header.");
        logHeader(mimeHeaders, CONTENT_TYPE_HEADER);

        return true;
    }

    private void logHeader(MimeHeaders mimeHeaders, String headerName) {
        final String[] headerValues = mimeHeaders.getHeader(headerName);

        if (headerValues == null) {
            LOGGER.debug("No Header with this name {}", headerName);
            return;
        }

        for (String headerValue : headerValues) {
            LOGGER.debug("{} = {}", headerName, headerValue);
        }
    }
}
