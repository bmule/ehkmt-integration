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
 * Runnable class used to send a PCC9 message to a the PCC9 end point. 
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.SendPcc09MessageExample -Dexec.classpathScope=test -Dexec.args="http://localhost:8080/testws"
 * </pre>
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
