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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
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

    private static final String PARAMETER_LIST_ELEMENT_NAME = "parameterList";
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
     * Extracts the following information : <ul> <li> the response end point
     * address (URI). The end point address (URI) is trated according with the
     * <a href="http://en.wikipedia.org/wiki/WS-Addressing">WS-Addressing</a>
     * standard and it it extracted from the SOAP message header. <li> the
     * involved user (patient). This information is extracted from the SOAP
     * message body. <li> the care provision code. This information is extracted
     * from the SOAP message. </ul>
     *
     * @param context the message context to be processed.
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
     * Extracts the Care Provision Code information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Care Provision Code information.
     *
     * @param paramter the
     * <code>org.w3c.dom.Element</code> instance that contains the Care
     * Provision Code information.
     * @return the Care Provision Code information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Care Provision Code information.
     * @throws IllegalStateException if the
     * <code>paramter</code> argument does not contains the right information
     * for the Care Provision Code.
     * @throws NullPointerException if the
     * <code>paramter</code> argument is null.
     */
    private String getCareProvisionCode(Element paramter) {
        if (paramter == null) {
            final NullPointerException exception =
                    new NullPointerException("The parameter argument can not be null");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        // TODO : use XPATH !
        final NodeList careProvisionList =
                paramter.getElementsByTagName("careProvisionCode");
        if (careProvisionList.getLength() == 0) {
            return null;
        }
        if (careProvisionList.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount careProvisionCode element. Only one careProvisionCode element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element careProvisionCode = (Element) careProvisionList.item(0);


        final NodeList values = careProvisionCode.getElementsByTagName("value");
        if (values.getLength() == 0) {
            return null;
        }
        if (values.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount values element. Only one values element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element value = (Element) values.item(0);
        final String result = value.getAttribute("code");
        return result;
    }

    /**
     * Extracts the Patient Name information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Patient Name information.
     *
     * @param paramter the
     * <code>org.w3c.dom.Element</code> instance that contains the Patient Name
     * information.
     * @return the Patient Name information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Patient Name information.
     * @throws IllegalStateException if the
     * <code>paramter</code> argument does not contains the right information
     * for the Patient Name.
     * @throws NullPointerException if the
     * <code>paramter</code> argument is null.
     */
    private String getPatientName(Element paramter) {
        if (paramter == null) {
            final NullPointerException exception =
                    new NullPointerException("The parameter argument can not be null");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        // TODO : use XPATH !
        final NodeList names =
                paramter.getElementsByTagName("patientName");
        if (names.getLength() == 0) {
            return null;
        }
        if (names.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount patientName element. Only one patientName element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element careProvisionCode = (Element) names.item(0);

        final NodeList values = careProvisionCode.getElementsByTagName("value");
        if (values.getLength() == 0) {
            return null;
        }
        if (values.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount values element. Only one values element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element value = (Element) values.item(0);
        final String result = value.getTextContent();
        return result;
    }

    /**
     * Extracts the Patient ID information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Patient ID information.
     *
     * @param paramter the
     * <code>org.w3c.dom.Element</code> instance that contains the Patient ID
     * information.
     * @return the Patient ID information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Patient ID information.
     * @throws IllegalStateException if the
     * <code>paramter</code> argument does not contains the right information
     * for the Patient ID.
     * @throws NullPointerException if the
     * <code>paramter</code> argument is null.
     */
    private String getPatientId(Element paramter) {
        if (paramter == null) {
            final NullPointerException exception =
                    new NullPointerException("The parameter argument can not be null");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        // TODO : use XPATH !
        final NodeList ids =
                paramter.getElementsByTagName("patientId");
        if (ids.getLength() == 0) {
            return null;
        }
        if (ids.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount patientId element. Only one patientId element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element careProvisionCode = (Element) ids.item(0);

        final NodeList values = careProvisionCode.getElementsByTagName("value");
        if (ids.getLength() == 0) {
            return null;
        }
        if (values.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount value element. Only one value element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element value = (Element) values.item(0);
        final String result = value.getAttribute("extension");
        return result;
    }

    /**
     * Extracts the response end point URI from a given
     * <code>SOAPHeader</code>. The end point address (URI) is treated according
     * with the <a
     * href="http://en.wikipedia.org/wiki/WS-Addressing">WS-Addressing</a>
     * standard. <br> If the
     * <code>header</code> is null then this method has no effect.
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

        final NodeList nodes = header.getElementsByTagName("wsa:Address");
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
     * <code>SOAPBody</code>. <br> If the
     * <code>header</code> is null then this method has no effect.
     *
     * @param body the body to process.
     */
    private void process(SOAPBody body) {
        final NodeList paramterList = body.getElementsByTagName(PARAMETER_LIST_ELEMENT_NAME);
        final int size = paramterList.getLength();
        for (int i = 0; i < size; i++) {
            final Element item = (Element) paramterList.item(i);
            processParamarer(item);
        }

    }

    private void processParamarer(Element paramter) {
        final String careProvisionCode = getCareProvisionCode(paramter);
        LOGGER.debug("careProvisionCode : " + careProvisionCode);

        final String patientId = getPatientId(paramter);
        LOGGER.debug("patientId : " + patientId);

        final String patientName = getPatientName(paramter);
        LOGGER.debug("patientName : " + patientName);
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
