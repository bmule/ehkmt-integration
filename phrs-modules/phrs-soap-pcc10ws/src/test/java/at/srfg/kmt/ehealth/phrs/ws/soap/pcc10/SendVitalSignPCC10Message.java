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
import at.srfg.kmt.ehealth.phrs.dataexchange.client.VitalSignClient;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.TriplestoreConnectionFactory;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.commons.beanutils.DynaBean;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCIN043200UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runnable class able to sends a <a
 * href="http://wiki.ihe.net/index.php?title=PCC-10">PCC10</a> that contains
 * Vital Signs request to a given end-point. To run this class this class from
 * the command line and maven use the following command :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.SendVitalSignPCC10Message -Dexec.classpathScope=test -Dexec.args="http://localhost:8080/testPCC10ws http://localhost:8080/responsews"
 * </pre> <b>Note : </b> the first argument (for the main method) is the
 * end-point, this is the URI where the PCC10 request will send. <b>Note : </b>
 * the second argument (for the main method) is the response-end-point, this is
 * the URI where the PCC10 response will send. <br/> This class is not design to
 * be extended.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class SendVitalSignPCC10Message {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc10.SendVitalSignPCC10Message</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SendVitalSignPCC10Message.class);

    /**
     * Don't let anybody to instantiate this class.
     */
    private SendVitalSignPCC10Message() {
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
     * Builds a dummy PCC10 that contains some Vital Signs.
     *
     * @return a dummy PCC10 that contains some Vital Signs.
     */
    private static QUPCIN043200UV01 buildMessage() 
            throws TripleException, IllegalAccessException, InstantiationException {
        
        final String owner = "testOwner";
        final TriplestoreConnectionFactory connectionFactory =
                TriplestoreConnectionFactory.getInstance();
        final GenericTriplestore triplestore = connectionFactory.getTriplestore();

        final VitalSignClient client = new VitalSignClient(triplestore);

        client.addVitalSign(owner,
                Constants.ICARDEA_INSTANCE_SYSTOLIC_BLOOD_PRESSURE,
                "Free text note for systolic.",
                "201006010000",
                Constants.STATUS_COMPELETE,
                "100",
                Constants.MM_HG);

        client.addVitalSign(owner,
                Constants.ICARDEA_INSTANCE_DIASTOLIC_BLOOD_PRERSSURE,
                "Free text note for diasystolic.",
                "201006010000",
                Constants.STATUS_COMPELETE,
                "80",
                Constants.MM_HG);

        client.addVitalSign(owner,
                Constants.ICARDEA_INSTANCE_BODY_HEIGHT,
                "Free text note for body height.",
                "201006010000",
                Constants.STATUS_COMPELETE,
                "180",
                Constants.CENTIMETER);

        client.addVitalSign(owner,
                Constants.ICARDEA_INSTANCE_BODY_WEIGHT,
                "Free text note for body weight.",
                "201006010000",
                Constants.STATUS_COMPELETE,
                "80",
                Constants.KILOGRAM);


        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE,
                Constants.PHRS_VITAL_SIGN_CLASS);
        queryMap.put(Constants.OWNER, owner);

        // here I search for all resources with 
        // rdf type == vital sign 
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);

        final DynaBeanClient dynaBeanClient = new DynaBeanClient(triplestore);
        final Set<DynaBean> beans = new HashSet<DynaBean>();
        for (String resoure : resources) {
            final DynaBean dynaBean = dynaBeanClient.getDynaBean(resoure);
            beans.add(dynaBean);
        }

        final QUPCIN043200UV01 pcc10Message = VitalSignPCC10.getPCC10Message(beans);
        return pcc10Message;
    }
}
