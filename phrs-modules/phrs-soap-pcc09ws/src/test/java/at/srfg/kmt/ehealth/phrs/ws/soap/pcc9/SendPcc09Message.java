/*
 * Project :iCardea
 * File : SendPcc09Message.java
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.sql.rowset.serial.SerialArray;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004040UVPortType;
import org.hl7.v3.QUPCAR004040UVService;
import org.hl7.v3.QUPCIN043100UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class able to send PCC9 requests to a given PCC9 end point. <br/> All
 * the messages send with this class will contain in the SOAP header the result
 * end point address. The response end point address (URI) is treated according
 * with the <a href="http://en.wikipedia.org/wiki/WS-Addressing">
 * WS-Addressing</a> standards. <br/> This class can not be extended.
 *
 * @author Mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class SendPcc09Message {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendPcc09Message</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SendPcc09Message.class);

    /**
     * Don't let anybody to be instantiate this class.
     */
    private SendPcc09Message() {
        // UNIMPLEMENTED
    }

    /**
     * Sends a given PCC9 request to a given PCC9 end point and returns the
     * acknowledge (the response for the request). The message send contains in
     * its SOAP header the response URI.
     *
     * @param query the PCC9 request. It can not be null.
     * @param endpointURI the URI where the PCC9 end point runs. It can not be
     * null.
     * @param responseEndpointURI the URI where the response to the PCC9 request
     * will be send. It can not be null.
     * @return the acknowledge (the response for the request)for the given
     * request.
     * @throws MalformedURLException if the specified PCC9 end point URI is
     * malformed.
     */
    static MCCIIN000002UV01 sendMessage(QUPCIN043100UV01 query,
            String endpointURI, String responseEndpointURI)
            throws MalformedURLException {

        if (query == null) {
            final NullPointerException exception =
                    new NullPointerException("The query argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (endpointURI == null || endpointURI.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The endpointURI argument can not be null or empty string.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (responseEndpointURI == null || responseEndpointURI.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The responseEndpointURI argument can not be null or empty string.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final QUPCAR004040UVPortType portType = getQUPCAR004040UVService();
        // here I obtain the service.
//        final QUPCAR004040UVPortType portType = ((Service)service).getQUPCAR004040UVPort();
        setWSAddressHandler(portType, responseEndpointURI);
        
        

        // I set the end point for the PCC9 end point
        setEndPointURI(portType, endpointURI);

        final MCCIIN000002UV01 ack =
                portType.qupcAR004040UVQUPCIN043100UV(query);
        LOGGER.info("Acknowledge : {} ", ack);

        return ack;
    }

    /**
     * Sends secure (SSL) a given PCC9 request to a given PCC9 end point and 
     * returns the acknowledge (the response for the request). 
     * The message send contains in its SOAP header the response URI.
     *
     * @param query the PCC9 request. It can not be null.
     * @param endpointURI the URI where the PCC9 end point runs. It can not be
     * null.
     * @param responseEndpointURI the URI where the response to the PCC9 request
     * will be send. It can not be null.
     * @param keystoreFilePath the path for the SSL certificate file, it can not be null.
     * @param keystoreFilePassword the password for the SSL certificate file, it can not
     * be null.
     * @return the acknowledge (the response for the request)for the given
     * request.
     * @throws MalformedURLException if the specified PCC9 end point URI is
     * malformed.
     */
    static MCCIIN000002UV01 sendSecureMessage(QUPCIN043100UV01 query,
            String endpointURI, String responseEndpointURI, String keystoreFilePath,
            String keystoreFilePassword) throws MalformedURLException {

        if (keystoreFilePath == null) {
            final NullPointerException exception =
                    new NullPointerException("The certPath argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (keystoreFilePassword == null) {
            final NullPointerException exception =
                    new NullPointerException("The certPassword argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }


        SSLClient.sslSetup(keystoreFilePath, keystoreFilePassword);

        final MCCIIN000002UV01 ack = sendMessage(query, endpointURI, responseEndpointURI);
        return ack;
    }

    /**
     * Returns a proxy instance able to address the PCC9 service. The (returned)
     * Proxy instance is able to address the PCC9 SOAP based services via a Java
     * API.
     *
     * @return a proxy instance able to address the PCC9 service.
     */
    private static QUPCAR004040UVPortType getQUPCAR004040UVService() {
        final QName qName =
                new QName("urn:hl7-org:v3", "QUPC_AR004040UV_Service");
        final ClassLoader classLoader = SendPcc09Message.class.getClassLoader();
        final URL url = classLoader.getResource("wsdl/QUPC_AR004040UV_Service.wsdl");
//        final QUPCAR004040UVService result = new QUPCAR004040UVService(url, qName);
//        return result;
        Service service = Service.create(url, qName);
        final QUPCAR004040UVPortType port = service.getPort(QUPCAR004040UVPortType.class);
        return port;
    }

    /**
     * Registers a given URI like end point for a given SOAP based service proxy
     * defined via a port type.
     *
     * @param portType the involved SOAP based service (port type). It can not
     * be null otherwise and exception will raise.
     * @param endpointURI the URI for the SOAP based service (port type).It can
     * not be null otherwise and exception will raise.
     * @throws NullPointerException if the
     * <code>portType</code> or
     * <code>endpointURI</code> arguments are null.
     */
    private static void setEndPointURI(QUPCAR004040UVPortType portType,
            String endpointURI) {

        if (portType == null) {
            final NullPointerException exception =
                    new NullPointerException("The portType argument can no be null");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (endpointURI == null) {
            final NullPointerException exception =
                    new NullPointerException("The endpointURI argument can no be null");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final BindingProvider bindingProvider = (BindingProvider) portType;
        final Map<String, Object> reqContext = bindingProvider.getRequestContext();
        LOGGER.debug("Usign the  End point : {}", endpointURI);
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getWSDLURI(endpointURI));
//        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8989/testws/pcc9?wsdl");
    }

    /**
     * Builds a WSDL URI for a given URI.
     *
     * @param uri the involved uri.
     * @return a wsld URI for a given uri.
     */
    private static String getWSDLURI(String uri) {
        final StringBuffer result = new StringBuffer();
        result.append(uri);
        result.append("?wsdl");

        return result.toString();
    }

    /**
     * Attaches a SOAP Handler (to a given SOAP based service) that is able to
     * enrich the SOAP header with response end point related information. The
     * response end point address (URI) is treated according with the <a
     * href="http://en.wikipedia.org/wiki/WS-Addressing">WS-Addressing</a>
     * standards. <br/> More precisely if this methods is applied to a given
     * port type (corresponding to a SOAP based service) then all the messages
     * send via this services will cary the response end point in the SOAP
     * message header.
     *
     * @param portType the involved SOAP based service (port type). It can not
     * be null otherwise and exception will raise.
     * @param endpointURI the URI for the SOAP based service (port type).It can
     * not be null otherwise and exception will raise.
     * @throws MalformedURLException if the URI address specified with the
     * <code>endpointURI</code> is a malformed.
     */
    private static void setWSAddressHandler(QUPCAR004040UVPortType portType,
            String endpointURI)
            throws MalformedURLException {
        final BindingProvider bindingProvider = (BindingProvider) portType;
        final Binding binding = bindingProvider.getBinding();
        final List<Handler> handlerChain = binding.getHandlerChain();
        //final String endpoint = "http://localhost:8080/phrs/pcc09";
        handlerChain.add(new WSAdressingHeaderEnricher(endpointURI));
        binding.setHandlerChain(handlerChain);
    }
    
    private void setSOAP12Binding(QUPCAR004040UVPortType portType) {
        final BindingProvider bindingProvider = (BindingProvider) portType;
        final Binding binding = bindingProvider.getBinding();
        final Map<String, Object> reqContext = bindingProvider.getRequestContext();
        //reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getWSDLURI(endpointURI));
        
    }
}
