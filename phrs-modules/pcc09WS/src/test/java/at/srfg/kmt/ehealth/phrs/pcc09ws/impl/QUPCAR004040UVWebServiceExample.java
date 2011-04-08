/*
 * Project :iCardea
 * File : QUPCAR004040UVWebServiceExample.java 
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCAR004040UVPortType;
import org.hl7.v3.QUPCAR004040UVService;
import org.hl7.v3.QUPCIN043100UV01;


/**
 * This runnable class send an empty PCC9 query to :
 * http://127.0.0.1:8080/pcc09ws/QUPCAR004040UV_Service. <br>
 * <b>Note : </b> This class required a running JBoss instance 
 * whit the PCC09 module installed. </br>
 * This is an example - it must be used only for didactical purposes. <br>
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.pcc09ws.impl.QUPCAR004040UVWebServiceExample -Dexec.classpathScope=test<br>
 * Take care the doesn not compile the classes.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class QUPCAR004040UVWebServiceExample {

    /**
     * Sends an empty PCC9 query to :
     * http://127.0.0.1:8080/pcc09ws/QUPCAR004040UV_Service.-co
     * 
     * @param args no arguments required
     * @throws JAXBException 
     */
    public static void main(String... args) throws JAXBException {
        final QUPCAR004040UVService service =
                new QUPCAR004040UVService();
        // here I obtain the service.
        final QUPCAR004040UVPortType portType = service.getQUPCAR004040UVPort();

        // I set the end point for the 
        setDefaultEndPointURI(portType);

        // builds a query
        final QUPCIN043100UV01 query = QueryFactory.buildQUPCIN043100UV01();

        // sends the query and poves if the ack is not null
        final MCCIIN000002UV01 ack =
                portType.qupcAR004040UVQUPCIN043100UV(query);
        System.out.println("Acknowledge : " + ack);

    }

    private static void setDefaultEndPointURI(QUPCAR004040UVPortType portType) {
        final BindingProvider bp = (BindingProvider) portType;
        final Map<String, Object> reqContext = bp.getRequestContext();
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://127.0.0.1:8080/pcc09ws/QUPCAR004040UV_Service");
    }
}
