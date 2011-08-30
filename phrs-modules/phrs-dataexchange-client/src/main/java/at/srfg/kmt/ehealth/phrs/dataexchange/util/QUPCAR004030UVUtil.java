/*
 * Project :iCardea
 * File : QUPCAR004030UVServiceUtil.java
 * Encoding : UTF-8
 * Date : Aug 28, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.util;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004030UVPortType;
import org.hl7.v3.QUPCAR004030UVService;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.namespace.QName;

/**
 * Contains a set of common used methods all related with the PCC10 
 * (<code>QUPCIN043200UV01</code>) transactions. <br>
 * This is a <i>util</i> class and it can not be instantiated. The only way to 
 * use it is trough its methods.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class QUPCAR004030UVUtil {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.QUPCAR004030UVServiceUtil</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QUPCAR004030UVUtil.class);

    /**
     * Don't let anybody to instantiate this class.
     */
    private QUPCAR004030UVUtil() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a service proxy for a pcc10 end point. By using this proxy you
     * can use java api to send/receive soap based request and responses with a 
     * given end-point.
     * 
     * @return a service proxy for a pcc10 end point.
     * @throws MalformedURLException if the wsdl (service descriptor) file can 
     * not be located.
     */
    public static QUPCAR004030UVService getQUPCAR004040UVService() throws MalformedURLException {
        final QName qName = new QName("urn:hl7-org:v3", "QUPC_AR004040UV_Service");
        final URL url =
                QUPCAR004030UVUtil.class.getClassLoader().getResource("wsdl/QUPC_AR004040UV_Service.wsdl");
        final QUPCAR004030UVService result = new QUPCAR004030UVService(url, qName);
        return result;
    }

    /**
     * Sends a <code>QUPCIN043200UV01</code> (pcc10 request) to the given end
     * point.
     * 
     * @param request the pcc10 request to send, it can not be null.
     * @param endpoint the end point where the pcc10 request will be send, 
     * it can not be null.
     * @throws NullPointerException if request or/and endpoint arguments are 
     * null or empty strings.
     */
    public static void sendPCC10(QUPCIN043200UV01 request, String endpoint) {

        if (request == null || endpoint == null) {
            final NullPointerException nullException =
                    new NullPointerException("The request and/or the endpoitn argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        LOGGER.debug("Tries to send the request {} on the end point {}", request, endpoint);

        final QUPCAR004030UVService service;
        try {
            service = getQUPCAR004040UVService();
        } catch (MalformedURLException exception) {
            LOGGER.error("The Proxy for the end {} point can not be build.", endpoint);
            LOGGER.error(exception.getMessage(), exception);
            return;
        }

        // here I obtain the service (proxy).
        final QUPCAR004030UVPortType portType = service.getQUPCAR004030UVPort();

        // I set the end point for the 
        setEndPointURI(portType, endpoint);

        final MCCIIN000002UV01 ack =
                portType.qupcAR004030UVQUPCIN043200UV(request);

        LOGGER.debug("Acknoledge from endpoint {} is {} ", endpoint, ack);
    }

    /**
     * JBoss specific way to customize the WSDL client (end point address).
     * 
     * @param portType the client to be customized.
     */
    private static void setEndPointURI(QUPCAR004030UVPortType portType, String endpoint) {
        final BindingProvider bp = (BindingProvider) portType;
        final Map<String, Object> reqContext = bp.getRequestContext();
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
    }

    /**
     * Builds a <code>QUPCIN043200UV01</code> (pcc10 request) based on a given
     * serialized input. This serialized input is loaded with the current 
     * classloader.
     * 
     * @param fileName the name for the resource that contains the pcc10 request
     * in serialized form. It can not be null.
     * @return pcc10 request) based on a given serialized input.
     * @throws NullPointerException if the <code>fileName</code> argument is 
     * null.
     * @throws JAXBException if the serialized form (SOAP) can not be 
     * un-marshaled properly.
     */
    public static QUPCIN043200UV01 buildQUPCIN043200UV01(String fileName) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final InputStream inputStream = getStream(fileName);

        final QUPCIN043200UV01 result =
                (QUPCIN043200UV01) unmarshaller.unmarshal(inputStream);

        return result;
    }

    /**
     * Load a resource by using the current classloader and returns an input
     * stream for it. If the resource can not be located then this methods
     * returns null.
     * 
     * @param name the name for the resource to locate.d
     * @return the input stream for a given resource, or null if the specified
     * resource can not be located in the current classpath.
     */
    private static InputStream getStream(String name) {
        final ClassLoader classLoader =
                QUPCAR004030UVUtil.class.getClassLoader();

        final InputStream inputStream =
                classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            final String msg =
                    String.format("The %s must be placed in the classpath", name);
            throw new IllegalStateException(msg);
        }

        return inputStream;
    }

    public static void toWriteInTemp(Object toMarshal, String name) throws JAXBException {

        // FIXME : this action can be restricted by a JAAS securicy policy,
        // please consider this in the future, Padawn, the way to the dark
        // side is always the ealy one.
        final String tempDir = System.getProperty("java.io.tmpdir");
        final JAXBContext context =
                JAXBContext.newInstance(org.hl7.v3.QUPCIN043200UV01.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final File destiantion =
                new File(tempDir + File.separatorChar + name + ".xml");
        marshaller.marshal(toMarshal, destiantion);
        LOGGER.debug("The {} was persisted on : {} ", name, destiantion.getAbsolutePath());
    }
}
