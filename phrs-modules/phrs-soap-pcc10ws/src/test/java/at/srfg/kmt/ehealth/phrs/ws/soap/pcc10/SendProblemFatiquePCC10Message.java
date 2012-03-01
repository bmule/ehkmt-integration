/*
 * Project :iCardea
 * File : SendVitalSignPCC10Message.java
 * Encoding : UTF-8
 * Date : Jan 11, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.DynaBeanClient;
import at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemEntryClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runnable class able to sends a 
 * <a href="http://wiki.ihe.net/index.php?title=PCC-10">PCC10</a> 
 * (that contains a Problem) request to a given end-point; 
 * the main method requires only one argument : a URI (String) the ppc10 end point. <br/>
 * To run this class this class from
 * the command line and maven use the following command :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.SendProblemFatiquePCC10Message -Dexec.classpathScope=test -Dexec.args="http://localhost:8989/testws/pcc10"
 * </pre> 
 * <b>Note : </b> the first argument (for the main method) is the
 * end-point, this is the URI where the PCC10 request will send. <br/> 
 * This class is not design to be extended.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class SendProblemFatiquePCC10Message {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.SendVitalSignPCC10Message</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SendProblemFatiquePCC10Message.class);

    /**
     * Don't let anybody to instantiate this class.
     */
    private SendProblemFatiquePCC10Message() {
        // UNIMPLEMENETD
    }

    /**
     * Runs this class from the command line. This method requires like argument
     * an array that has <b>only one element</b>, this element is the end point
     * where the PCC10 end-point.
     *
     * @param args the command line arguments array. It must contains only one
     * element - the URI for PCC10 end point other wise an
     * <code>IllegalArgumentException</code> will raise.
     * @throws JAXBException by any JAXB related errors.
     * @throws MalformedURLException if the involved SOAP message is malformed.
     * @throws IllegalArgumentException if the
     * <code>args</code> array is null or it size it different than 1.
     */
    public static void main(String... args)
            throws JAXBException, MalformedURLException, TripleException, 
            IllegalAccessException, InstantiationException {

        if (args == null || args.length != 1) {
            final IllegalArgumentException exception =
                    new IllegalArgumentException("One argument expected (the PCC10 end point URI).");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final String endpointURI = args[0];

        final QUPCIN043200UV01 pcc10Request = buildMessage();

        LOGGER.info("Tries to send a PCC10 query ({}) to {}", pcc10Request, endpointURI);
        final MCCIIN000002UV01 ack =
                SendPcc10Message.sendMessage(pcc10Request, endpointURI);

        LOGGER.info("Acknowledge (respense) is : {}.", ack);
    }

    /**
     * Builds a dummy PCC10 that contains a Problem (Finding - fatigue).
     *
     * @return a dummy PCC10 that contains a Problem (Finding - fatigue).
     */
    private static QUPCIN043200UV01 buildMessage() 
            throws TripleException, IllegalAccessException, InstantiationException {
        
        final String owner = Constants.PROTOCOL_ID_UNIT_TEST;
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final ProblemEntryClient client = new ProblemEntryClient(triplestore);

        // this adds a problem-finding named fever fatique
        client.addProblemEntry(
                owner,
                Constants.HL7V3_FINDING,
                Constants.STATUS_COMPELETE,
                "201006010000",
                "201006010000",
                "Free text note for the problem.",
                Constants.HL7V3_FATIQUE);

        final Iterable<String> uris = client.getProblemEntriesURIForUser(owner);
        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String uri : uris) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(uri);
            beans.add(dynaBean);
        }

        final QUPCIN043200UV01 pcc10Message = ProblemEntryPCC10.getPCC10Message(owner,beans);
        return pcc10Message;
    }
}
