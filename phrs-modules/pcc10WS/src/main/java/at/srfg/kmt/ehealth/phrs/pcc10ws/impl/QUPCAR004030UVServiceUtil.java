/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : QUPCAR004030UVServiceUtil.java 
 * Encoding : UTF-8
 * Date : Apr 20, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;

import at.srfg.kmt.ehealth.phrs.util.Util;
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
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class QUPCAR004030UVServiceUtil {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.QUPCAR004030UVServiceUtil</code>.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QUPCAR004030UVServiceUtil.class);

    private static QUPCAR004030UVService getQUPCAR004040UVService() throws MalformedURLException {
        final QName qName = new QName("urn:hl7-org:v3", "QUPC_AR004040UV_Service");
        final URL url = NotifyRestWS.class.getClassLoader().getResource("wsdl/QUPC_AR004040UV_Service.wsdl");
        final QUPCAR004030UVService result = new QUPCAR004030UVService(url, qName);
        return result;
    }

    static void sendPCC10(QUPCIN043200UV01 request, String endpoint) {

        LOGGER.debug("Tries to send the request {} on the end point {}",
                Util.forLog(request, endpoint));

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
        final DefaultPCC10RequestFactory requestFactory =
                new DefaultPCC10RequestFactory();

        // I set the end point for the 
        setEndPointURI(portType, endpoint);

        final MCCIIN000002UV01 ack =
                portType.qupcAR004030UVQUPCIN043200UV(request);

        LOGGER.debug("Acknoledge from endpoint {} is {} ", Util.forLog(endpoint, ack));
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

    static void toWriteInTemp(Object toMarshal, String name) throws JAXBException {
        final String tempDir = System.getProperty("java.io.tmpdir");
        final JAXBContext context =
                JAXBContext.newInstance(org.hl7.v3.MCCIIN000002UV01.class);

        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final File destiantion =
                new File(tempDir + File.separatorChar + name + ".xml");
        marshaller.marshal(toMarshal, destiantion);
        LOGGER.debug("The " + name + " was persisted on : " + destiantion.getAbsolutePath());
    }

    static QUPCIN043200UV01 buildQUPCIN043200UV01(String fileName) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final InputStream inputStream = getStream(fileName);

        final QUPCIN043200UV01 result =
                (QUPCIN043200UV01) unmarshaller.unmarshal(inputStream);

        return result;
    }

    private static InputStream getStream(String name) {
        final ClassLoader classLoader =
                QUPCAR004030UVServiceUtil.class.getClassLoader();

        final InputStream inputStream =
                classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            final String msg =
                    String.format("The %s must be placed in the classpath", name);
            throw new IllegalStateException(msg);
        }

        return inputStream;
    }
}
