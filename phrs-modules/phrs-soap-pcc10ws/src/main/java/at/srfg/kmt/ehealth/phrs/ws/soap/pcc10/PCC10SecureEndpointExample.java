/*
 * Project :iCardea
 * File : PCC9EndpointExample.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runnable class used to start the PCC10 web service end point. The URI for the
 * end point is specified with the
 * <code>main</code> method arguments; the main method requires five arguments
 * : a host (String), a port (integer) and a path context (string), the path for
 * the keystore file (String) and the password for the keystore file. <br/>
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10SecureEndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8989 testws/pcc10 my-keystore.ks mypasswd"
 * </pre> <b>Note : </b> this class requires three arguments a host, a port and
 * a path.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class PCC10SecureEndpointExample {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendPcc09Message</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC10SecureEndpointExample.class);

    /**
     * Don't let anybody to instantiate this class.
     */
    private PCC10SecureEndpointExample() {
        // UNIMPLEMEENTD
    }

    /**
     * Run this class from the command line.
     *
     * @param args three arguments a host, a port and a path.
     */
    public static void main(String... args) throws Exception {

        if (args == null || args.length != 5) {
            final IllegalArgumentException extension =
                    new IllegalArgumentException("Five arguments are required. The argumetns are : Host(string) , port(integer), context path(Strign), keystore file (String) and keystore file password (String).");
            LOGGER.error(extension.getMessage(), extension);
            throw extension;
        }

        final String host = args[0];
        final String port = args[1];
        final String path = args[2];
        final String keystoreFile = args[3];
        final String keystorePasswd = args[4];

        final Object[] toLog = {host, port, path, keystoreFile, keystorePasswd};
        LOGGER.info("Tries to start a PCC10 secure end point on host : {}, port : {}, context : {}, keystore file : {} and keystore password : {}", toLog);
        final int portInt = Integer.parseInt(port);

        final PCC10SecureEndpoint endpoint =
                new PCC10SecureEndpoint(host, portInt, path, keystoreFile, keystorePasswd);
        endpoint.start();
    }
}
