/*
 * Project :iCardea
 * File : PCC9Endpoint.java
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import java.net.MalformedURLException;
import javax.xml.ws.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is en end point able to accept <a
 * href="http://wiki.ihe.net/index.php?title=PCC-9">PCC09</a> transactions.<br/>
 * The usage is simple : build an instance for a given host, port and context
 * path and then use the <code>start</code> method.
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

    /**
     * The URL for end point.
     */
    private final String endpointURI;

    /**
     * Builds a end point for 
     * <a href="http://wiki.ihe.net/index.php?title=PCC-9">PCC09</a>
     * transactions for a given list of arguments.
     *
     * @param host the host i.p. for the end point, it can not be null.
     * @param port the port for the end point,
     * @param path path i.p. for the end point, it can not be null.
     * @throws NullPointerException if the <code>host</code> or 
     * the <code>path</code> is null.
     */
    public PCC9Endpoint(String host, int port, String path) {

        if (host == null) {
            final NullPointerException exception =
                    new NullPointerException("The host argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
        }

        if (path == null) {
            final NullPointerException exception =
                    new NullPointerException("The path argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
        }

        final StringBuilder builder = new StringBuilder("http://");
        builder.append(host);
        builder.append(":");
        builder.append(port);
        builder.append("/");
        builder.append(path);
        endpointURI = builder.toString();
    }

    /**
     * Starts the end point.
     *
     * @throws MalformedURLException if the URL defined in the constructor is 
     * malformated.
     */
    public void start() throws MalformedURLException {
        LOGGER.info("PCC9 endpoint runs on {}", endpointURI);
        final QUPCAR004040UVWebService webService = new QUPCAR004040UVWebService();
        //Endpoint.publish(endpointURI, webService);
        
        final Endpoint endpoint = Endpoint.create(webService);
        endpoint.publish(endpointURI);
    }
}
