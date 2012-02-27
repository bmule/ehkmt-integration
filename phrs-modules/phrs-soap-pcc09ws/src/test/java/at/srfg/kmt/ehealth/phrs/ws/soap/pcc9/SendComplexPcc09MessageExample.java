/*
 * Project :iCardea
 * File : SendPcc09MessageExample.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import java.net.MalformedURLException;
import javax.xml.bind.JAXBException;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCIN043100UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Runnable class able to sends a <a
 * href="http://wiki.ihe.net/index.php?title=PCC-9">PCC09</a> request to a given
 * end-point;
 * the main method requires seven one arguments :
 * <ul>
 * <li> an URI (String) for the PCC09 end point
 * <li> an URI (String) for the PCC10(String) end point
 * <li> a care provision code (String)
 * <li> an unique patient ID (String)
 * <li> a patient name concatenated in to a single string. 
 * <li> a patient surname concatenated in to a single string.
 * <li> a patient gender (string).
 * </ul>
 * <br/>
 * To run this class this class from the cmand line and maven use the 
 * following command :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendComplexPcc09MessageExample -Dexec.classpathScope=test -Dexec.args="http://localhost:8080/testpcc9ws http://localhost:8080/responsews COBSCAT"
 * </pre> 
 * <b>Note : </b> the first argument (for the main method) is the end-point,
 * this is the URI where the PCC9 request will send. 
 * <b>Note : </b> the second argument (for the main method) is the
 * response-end-point, this is the URI where the PCC10 response will send. <br/>
 * This class is not design to be extended.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class SendComplexPcc09MessageExample {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.SendPcc09MessageExample</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SendComplexPcc09MessageExample.class);

    /**
     * Don't let anybody to instantiate this class.
     */
    private SendComplexPcc09MessageExample() {
        // UNIMPLEMENETD
    }

    /**
     * Runs this class from the command line. This method requires like argument
     * an array that has <b>three elements</b> : an URI for the ppc09 end point,
     * an URI for the ppc10(String) end point and a care provision code.
     *
     * @param args the command line arguments array. It must contains three one
     * elements other wise an <code>IllegalArgumentException</code> will raise.
     * @throws JAXBException by any JAXB related errors.
     * @throws MalformedURLException if the involved SOAP message is malformed.
     * @throws IllegalArgumentException if the
     * <code>args</code> array is null or it size it different than 1.
     */
    public static void main(String... args)
            throws JAXBException, MalformedURLException {
        if (args == null || args.length != 7) {
            final IllegalArgumentException exception =
                    new IllegalArgumentException("One argument expected (the PCC9 end point URI).");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final String endpointURI = args[0];
        final String responseURI = args[1];
        final String careProvisionCode = args[2];
        final String patientID = args[3];
        final String patientName = args[4];
        final String patientSurname = args[5];
        final String patientAdministrativeGender = args[6];


//        final String careProvisionCode = "COBSCAT";
        final String careProvisionReason = "iCardea Barcelona Meeting test";
        //String careRecordTimePeriodBegin = "201101010000";
//        final String careRecordTimePeriodBegin =
//                QueryFactoryUnitTest.getLastMonthDate();
        //String careRecordTimePeriodEnd =   "201101011000";
//        final String careRecordTimePeriodEnd = QueryFactoryUnitTest.getNowDate();
//        final String clinicalStatementTimePeriodBegin =
//                QueryFactoryUnitTest.getLastMonthDate();
        //String clinicalStatementTimePeriodBegin = "201001010000";
//        final String clinicalStatementTimePeriodEnd =
//                QueryFactoryUnitTest.getNowDate();
        //String clinicalStatementTimePeriodEnd = "201005011000";
        final String includeCarePlanAttachment = "true";
        final String maximumHistoryStatements = "30";

        final String patientBirthTime = "197903111010";
        
        final QUPCIN043100UV01 pcc9Request =
                QueryFactory.buildPCC9Request(careProvisionCode,
                null,
                null,
                null,
                null,
                null,
                null,
                maximumHistoryStatements,
                null,
                null,
                patientID,
                patientName,
                patientSurname);

        LOGGER.info("Tries to send a PCC9 query ({}) to {}", pcc9Request, endpointURI+" careProvisionCode: "+careProvisionCode+" patientID: "+patientID);
        final MCCIIN000002UV01 ack =
                SendPcc09Message.sendMessage(pcc9Request, endpointURI, responseURI);



        LOGGER.info("Acknowledge (respense) is : {}.", ack);
    }
}
