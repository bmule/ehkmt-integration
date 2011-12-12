/*
 * Project :iCardea
 * File : PCC9Endpoint.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import org.hl7.v3.QUPCAR004040UVPortType;
import org.hl7.v3.QUPCAR004040UVService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class PCC9Endpoint {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.PCC9Endpoint</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC9Endpoint.class);

    private final String endpointURI;

    public PCC9Endpoint(String host, int port, String path) {
        final StringBuilder builder = new StringBuilder("http://");
        builder.append(host);
        builder.append(":");
        builder.append(port);
        builder.append("/");
        builder.append(path);
        endpointURI = builder.toString();
    }

    public void start() throws MalformedURLException {
        LOGGER.info("PCC9 endpoint runs on {}", endpointURI);
        final QUPCAR004040UVWebService webService = new QUPCAR004040UVWebService();
        
        //final QUPCAR004040UVService service = getQUPCAR004040UVService();
        Endpoint.publish(endpointURI, webService);
    }
}
