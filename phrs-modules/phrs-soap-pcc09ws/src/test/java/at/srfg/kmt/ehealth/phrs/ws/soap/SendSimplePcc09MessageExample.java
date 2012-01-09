/*
 * Project :iCardea
 * File : SendPcc09MessageExample.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;


import java.net.MalformedURLException;
import javax.xml.bind.JAXBException;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCIN043100UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runnable class able to sends a  <a
 * href="http://wiki.ihe.net/index.php?title=PCC-9">PCC09</a> request to a given
 * end-point. To run this class this class from the command line and maven use
 * the following command :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.SendSimplePcc09MessageExample -Dexec.classpathScope=test -Dexec.args="http://localhost:8080/testws"
 * </pre>
 * <b>Note : </b> the end-point where the PCC9 request will send is specified
 * with the "-Dexec.args=" statement. <br/>
 * This class is not design to be extended.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class SendSimplePcc09MessageExample {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.SendPcc09MessageExample</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SendSimplePcc09MessageExample.class);

    /**
     * Don't let anybody to instantiate this class.
     */
    private SendSimplePcc09MessageExample() {
        // UNIMPLEMENETD
    }

    /**
     * Runs this class from the command line. This method requires like argument
     * an array that has <b>only one element</b>, this element is the end point
     * where the PCC9 end-point.
     *
     * @param args the  command line arguments array. It must contains only one
     * element - the URI for PCC9 end point other wise an
     * <code>IllegalArgumentException</code> will raise.
     * @throws JAXBException by any JAXB related errors.
     * @throws MalformedURLException if the involved SOAP message is malformed.
     * @throws IllegalArgumentException if the <code>args</code> array is null
     * or it size it different than 1.
     */
    public static void main(String... args)
            throws JAXBException, MalformedURLException {
        if (args == null || args.length != 1) {
            final IllegalArgumentException exception =
                    new IllegalArgumentException("One argument expected (the PCC9 end point URI).");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final String endpointURI = args[0];
        final QUPCIN043100UV01 pcc9Request = QueryFactory.buildEmptyPCC9Request();
        LOGGER.info("Tries to send a PCC9 query ({}) to {}", pcc9Request, endpointURI);
        final MCCIIN000002UV01 ack =
                SendPcc09Message.sendMessage(pcc9Request, endpointURI);
        LOGGER.info("Acknowledge (respense) is : {}.", ack);
    }
}
