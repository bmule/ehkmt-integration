/*
 * Project :iCardea
 * File : SendPcc09Message.java
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004030UVPortType;
import org.hl7.v3.QUPCAR004030UVService;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class able to send PCC10 requests to a given PCC10 end point. <br/>
 * This class can not be extended.
 *
 * @author Mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class SendPcc10Message {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.SendPcc10Message</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SendPcc10Message.class);

    /**
     * Don't let anybody to be instantiate this class.
     */
    private SendPcc10Message() {
        // UNIMPLEMENTED
    }

    /**
     * Sends a given PCC10 request to a given PCC10 end point and returns the
     * acknowledge (the response for the request). The message send contains in
     * its SOAP header the response URI.
     *
     * @param query the PCC10 request. It can not be null.
     * @param endpointURI the URI where the PCC10 end point runs. It can not be
     * null.
     * @param responseEndpointURI the URI where the response to the PCC10 request
     * will be send. It can not be null.
     * @return the acknowledge (the response for the request)for the given
     * request.
     * @throws MalformedURLException if the specified PCC10 end point URI is
     * malformed.
     */
    static MCCIIN000002UV01 sendMessage(QUPCIN043200UV01 query,
            String endpointURI)
            throws MalformedURLException {

        if (query == null) {
            final NullPointerException exception =
                    new NullPointerException("The query argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
        }

        if (endpointURI == null || endpointURI.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The endpointURI argument can not be null or empty string.");
            LOGGER.error(exception.getMessage(), exception);
        }

        final QUPCAR004030UVService service = getQUPCAR004040UVService();
        final URL documentLocation = service.getWSDLDocumentLocation();
        LOGGER.debug("Actaul service wsdl location : {}", documentLocation);
        // here I obtain the service.
        final QUPCAR004030UVPortType portType = service.getQUPCAR004030UVPort();

        // I set the end point for the PCC10 end point
        setEndPointURI(portType, endpointURI);

        final MCCIIN000002UV01 ack =
                portType.qupcAR004030UVQUPCIN043200UV(query);
        LOGGER.info("Acknowledge : {} ", ack);

        return ack;
    }

    /**
     * Returns a proxy instance able to address the PCC10 service. The (returned)
     * Proxy instance is able to address the PCC10 SOAP based services via a Java
     * API.
     *
     * @return a proxy instance able to address the PCC10 service.
     */
    private static QUPCAR004030UVService getQUPCAR004040UVService() {
        final QName qName =
                new QName("urn:hl7-org:v3", "QUPC_AR004030UV_Service");
        final ClassLoader classLoader = SendPcc10Message.class.getClassLoader();
        final URL url = classLoader.getResource("wsdl/QUPC_AR004030UV_Service.wsdl");
        final QUPCAR004030UVService result = new QUPCAR004030UVService(url, qName);
        return result;
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
    private static void setEndPointURI(QUPCAR004030UVPortType portType,
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
//        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8989/testws/PCC10?wsdl");
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
}
