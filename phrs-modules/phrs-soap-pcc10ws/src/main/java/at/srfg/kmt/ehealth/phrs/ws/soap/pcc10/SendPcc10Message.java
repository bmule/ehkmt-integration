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
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004030UVPortType;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class able to send PCC10 requests to a given PCC10 end point. <br/>
 * <b>Note : </b> this message sends <a
 * href="http://www.w3.org/TR/soap12-part0/">SOAP 1.2</a> based messages. <br/>
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
     * @param query               the PCC10 request. It can not be null.
     * @param endpointURI         the URI where the PCC10 end point runs. It can not be
     *                            null.
     * @param responseEndpointURI the URI where the response to the PCC10
     *                            request will be send. It can not be null.
     * @return the acknowledge (the response for the request)for the given
     *         request.
     * @throws MalformedURLException if the specified PCC10 end point URI is
     *                               malformed.
     */
    static MCCIIN000002UV01 sendMessage(QUPCIN043200UV01 query,
                                        String endpointURI)
            throws MalformedURLException {
    //SSL must be setup enabled if needed from the MedicationTask, etc
    //TODO change the enabling security methods in MediationTask, VitalSign Task, etc
     // to use a common solution with this override to trust all certificates
    //the sendSecureMessage is not used by MedicationTask, etc...but message receiver did not care on salk machine or?

        try {
            // trust all certificates
            com.sun.net.ssl.HostnameVerifier myHv = new com.sun.net.ssl.HostnameVerifier() {

                public boolean verify(String hostName, String a) {
                    return true;
                }
            };
            com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl.setDefaultHostnameVerifier(myHv);
        } catch (Exception e) {
            LOGGER.error(" trust all certificates");
        }
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

        final QUPCAR004030UVPortType portType = getQUPCAR004030UVService();

        // I set the end point for the PCC10 end point
        setEndPointURI(portType, endpointURI);

        final MCCIIN000002UV01 ack =
                portType.qupcAR004030UVQUPCIN043200UV(query);
        LOGGER.info("Acknowledge : {} ", ack);

        return ack;
    }

    /**
     * This is never used...
     *
     * Sends secure (SSL) a given PCC10 request to a given PCC10 end point and
     * returns the acknowledge (the response for the request). The message send
     * contains in its SOAP header the response URI.
     *
     * @param query                the PCC9 request. It can not be null.
     * @param endpointURI          the URI where the PCC10 end point runs. It can not be
     *                             null.
     * @param responseEndpointURI  the URI where the response to the PCC9 request
     *                             will be send. It can not be null.
     * @param keystoreFilePath     the path for the SSL certificate file, it can not
     *                             be null.
     * @param keystoreFilePassword the password for the SSL certificate file, it
     *                             can not be null.
     * @return the acknowledge (the response for the request)for the given
     *         request.
     * @throws MalformedURLException if the specified PCC10 end point URI is
     *                               malformed.
     */
    static MCCIIN000002UV01 sendSecureMessage(QUPCIN043200UV01 query,
                                              String endpointURI, String keystoreFilePath,
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
        com.sun.net.ssl.HostnameVerifier myHv = new com.sun.net.ssl.HostnameVerifier() {
            public boolean verify(String hostName, String a) {
                return true;
            }
        };
        com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl.setDefaultHostnameVerifier(myHv);
        // or this...	
        //	com.sun.net.ssl.HostnameVerifier myHv = new com.sun.net.ssl.HostnameVerifier() {
        //		public boolean verify(String hostName, String a) {
        //			return true;
        //		}
        //	};

        SSLClient.sslSetup(keystoreFilePath, keystoreFilePassword);

        final MCCIIN000002UV01 ack = sendMessage(query, endpointURI);
        return ack;
    }


    /**
     * Returns a proxy instance able to address the PCC10 service. The
     * (returned) Proxy instance is able to address the PCC10 SOAP based
     * services via a Java API.
     *
     * @return a proxy instance able to address the PCC10 service.
     */
    private static QUPCAR004030UVPortType getQUPCAR004030UVService() {
        final QName qName =
                new QName("urn:hl7-org:v3", "QUPC_AR004030UV_Service");
        final ClassLoader classLoader = SendPcc10Message.class.getClassLoader();
        final URL url = classLoader.getResource("wsdl/QUPC_AR004030UV_Service.wsdl");
//        final QUPCAR004030UVService result = new QUPCAR004030UVService(url, qName);
//        return result;

        final Service serviceFactory = Service.create(url, qName);

        for (final Iterator<QName> ports = serviceFactory.getPorts();
             ports.hasNext(); ) {
            final QName next = ports.next();
            LOGGER.debug("Available port :{} ", next);
        }

        // I do this just to be sure that the port SOAP 1.2 is used.
        // Otherwise the SOAP 1.1 may occur.
        // I am not sure why this problem occur because the specification says :
        // "On the client there is nothing special that has to be done. 
        // JAX-WS runtime looks into the WSDL to determine the binding being 
        // used and configures itself accordingly. wsimport command line tool
        // or wsimport ant task can be used to import the WSDL and to generated
        // the client side artifacts."
        // The upper logging lines are dispalying the SOAP header so if there 
        // is no port for the given QName then please find the reason. 
        final QName portQName =
                new QName("urn:hl7-org:v3", "QUPC_AR004030UV_PortSoap12");

        final QUPCAR004030UVPortType port =
                serviceFactory.getPort(portQName, QUPCAR004030UVPortType.class);
        return port;


    }

    /**
     * Registers a given URI like end point for a given SOAP based service proxy
     * defined via a port type.
     *
     * @param portType    the involved SOAP based service (port type). It can not
     *                    be null otherwise and exception will raise.
     * @param endpointURI the URI for the SOAP based service (port type).It can
     *                    not be null otherwise and exception will raise.
     * @throws NullPointerException if the
     *                              <code>portType</code> or
     *                              <code>endpointURI</code> arguments are null.
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
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURI);
//        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getWSDLURI(endpointURI));
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
