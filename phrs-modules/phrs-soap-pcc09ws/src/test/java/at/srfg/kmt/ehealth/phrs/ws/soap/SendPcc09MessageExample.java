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
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.SendPcc09Message -Dexec.classpathScope=test -Dexec.args="http://localhost:8080/testws"
 * </pre>
 * <b>Note : </b> the end-point where the PCC9 request will send is specified
 * with the "-Dexec.args=" statement. <br/>
 * This class is not design to be extended.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class SendPcc09MessageExample {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.SendPcc09MessageExample</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SendPcc09MessageExample.class);

    /**
     * Don't let anybody to instantiate this class.
     */
    private SendPcc09MessageExample() {
        // UNIMPLEMENETD
    } 
    
    /**
     *
     * @param args
     */
    public static void main(String... args) throws JAXBException, MalformedURLException {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException();
        }

        final String endpointURI = args[0];
        final QUPCIN043100UV01 pccQuery = QueryFactory.buildQUPCIN043100UV01();
        LOGGER.info("Tries to send a PCC9 query ({}) to {}", pccQuery, endpointURI);
        final MCCIIN000002UV01 ack = 
                SendPcc09Message.sendMessage(pccQuery, endpointURI);
        LOGGER.info("Ack {}.", ack);
    }
}
