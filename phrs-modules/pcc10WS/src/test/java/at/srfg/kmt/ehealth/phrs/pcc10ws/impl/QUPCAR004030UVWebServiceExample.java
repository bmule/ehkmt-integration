    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : QUPCAR004030UVWebServiceExample.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import java.io.IOException;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import org.hl7.v3.*;


/**
 * This runnable class send an empty PCC9 query to :
 * http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service. <br>
 * <b>Note : </b> This class required a running JBoss instance 
 * whit the PCC10 module installed. </br>
 * This is an example - it must be used only for didactical purposes. <br>
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.pcc09ws.impl.QUPCAR004030UVWebServiceExample -Dexec.classpathScope=test<br>
 * Take care the exec  does not compile the classes.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class QUPCAR004030UVWebServiceExample {

    /**
     * Sends an empty PCC10 query to :
     * http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service
     * 
     * @param args no arguments required
     * @throws JAXBException 
     */
    public static void main(String... args) throws JAXBException, IOException {
        final QUPCAR004030UVService service =
                new QUPCAR004030UVService();

        // here I obtain the service.
        final QUPCAR004030UVPortType portType = service.getQUPCAR004030UVPort();

        final QUPCIN043200UV01 response = QueryFactory.buildQUPCIN043200UV01();

        // I set the end point for the 
        setDefaultEndPointURI(portType);

        final MCCIIN000002UV01 ack =
                portType.qupcAR004030UVQUPCIN043200UV(response);

        if (ack == null) {
            throw new IllegalStateException("Tests fails, non null arknowledge was expected");
        }
        System.out.println("Acknowledge : " + ack);
    }

    private static void setDefaultEndPointURI(QUPCAR004030UVPortType portType) {
        final BindingProvider bp = (BindingProvider) portType;
        final Map<String, Object> reqContext = bp.getRequestContext();
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service");
    }
}
