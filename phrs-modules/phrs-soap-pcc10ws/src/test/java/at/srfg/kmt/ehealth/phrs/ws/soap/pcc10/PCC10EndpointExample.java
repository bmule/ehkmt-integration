/*
 * Project :iCardea
 * File : PCC9EndpointExample.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import java.net.MalformedURLException;

/**
 * Runnable class used to start the PCC10 web service end point. 
 * The URI for the end point is specified with the <code>main</code> method
 * arguments; the main method requires three arguments : a host (String),
 * a port (integer) and a path context. <br/>
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.PCC10EndpointExample -Dexec.classpathScope=test -Dexec.args="localhost 8989 testws/pcc10"
 * </pre>
 * <b>Note : </b> this class requires three arguments a host, a port and a path.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class PCC10EndpointExample {

    /**
     * Don't let anybody to instantiate this class.
     */
    private PCC10EndpointExample() {
        // UNIMPLEMEENTD
    }

    /**
     * Run this class from the command line.
     *
     * @param args  three arguments a host, a port and a path.
     */
    public static void main(String... args) throws MalformedURLException {
        
        if (args == null || args.length != 3) {
            throw new IllegalArgumentException("Three arguments are required. The argumetns are : Host(string) , port(integer) and context path(Strign).");
        }
        
        final String host = args[0];
        final String port = args[1];
        final String path = args[2];
        
//        final PCC9Endpoint endpoint = new PCC9Endpoint("localhost", 8989, "testws/pcc9");
        final PCC10Endpoint endpoint = new PCC10Endpoint(host, Integer.parseInt(port), path);
        endpoint.start();
    }
}
