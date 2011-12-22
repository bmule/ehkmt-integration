/*
 * Project :iCardea
 * File : SendPcc09Message.java
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004040UVPortType;
import org.hl7.v3.QUPCAR004040UVService;
import org.hl7.v3.QUPCIN043100UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.SendPcc09Message -Dexec.classpathScope=test -Dexec.args="http://localhost:8080/testws"
 * </pre>
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class SendPcc09Message {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.SendPcc09Message</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SendPcc09Message.class);

    /**
     *
     */
    private SendPcc09Message() {
        // UNIMPLEMENTED
    }

    static MCCIIN000002UV01 sendMessage(QUPCIN043100UV01 query, String endpointURI)
            throws MalformedURLException {

        final QUPCAR004040UVService service = getQUPCAR004040UVService();
        
        final URL documentLocation = service.getWSDLDocumentLocation();
        LOGGER.debug("Actaul service wsdl location : {}", documentLocation);
        // here I obtain the service.
        final QUPCAR004040UVPortType portType = service.getQUPCAR004040UVPort();
        setHandler(portType);

        // I set the end point for the 
        setDefaultEndPointURI(portType, endpointURI);
        
        

        final MCCIIN000002UV01 ack =
                portType.qupcAR004040UVQUPCIN043100UV(query);
        LOGGER.info("Acknowledge : {} ", ack);

        return ack;
    }

    private static QUPCAR004040UVService getQUPCAR004040UVService()
            throws MalformedURLException {

        final QName qName =
                new QName("urn:hl7-org:v3", "QUPC_AR004040UV_Service");
        final ClassLoader classLoader = SendPcc09Message.class.getClassLoader();
        final URL url = classLoader.getResource("wsdl/QUPC_AR004040UV_Service.wsdl");
        final QUPCAR004040UVService result = new QUPCAR004040UVService(url, qName);
        return result;
    }

    private static void setDefaultEndPointURI(QUPCAR004040UVPortType portType, String endpointURI) {
        final BindingProvider bp = (BindingProvider) portType;
        final Map<String, Object> reqContext = bp.getRequestContext();
        LOGGER.debug("Usign the  End point : {}", endpointURI);
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getWSDLURI(endpointURI));
//        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8989/testws/pcc9?wsdl");
    }

    private static String getWSDLURI(String uri) {
        final StringBuffer result = new StringBuffer();
        result.append(uri);
        result.append("?wsdl");

        return result.toString();
    }
    
    private static void setHandler(QUPCAR004040UVPortType portType) throws MalformedURLException {
        final BindingProvider bp = (BindingProvider) portType;
        final Binding binding = bp.getBinding();
        final List<Handler> handlerChain = binding.getHandlerChain();
        final String endpoint = "http://localhost:8080/phrs/pcc09";
        handlerChain.add(new WSAdressingHeaderEnricher(endpoint));
        binding.setHandlerChain(handlerChain);
    }
}
