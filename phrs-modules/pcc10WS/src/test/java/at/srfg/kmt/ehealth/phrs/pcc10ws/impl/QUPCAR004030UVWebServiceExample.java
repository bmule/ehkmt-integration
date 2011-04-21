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


import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import org.hl7.v3.*;


/**
 * This runnable class send an empty PCC10 query to :
 * http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service. <.br>
 * <b>Note : </b> This class required a running JBoss instance 
 * whit the PCC10 module installed. </br>
 * This is an example - it must be used only for didactical purposes. </br>
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.pcc10ws.impl.QUPCAR004030UVWebServiceExample -Dexec.classpathScope=test</br>
 * Take care the exec  does not compile the classes.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class QUPCAR004030UVWebServiceExample {

    /**
     * Builds and sends an empty PCC10 query to :
     * http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service.
     * 
     * @param args no arguments required.
     * @throws PCC10BuildException by if the request can not be properly 
     * builded.
     * @throws MalformedURLException if the URL to the WSLD service file can not
     * be located via a standard URL.
     */
    public static void main(String... args) throws 
            PCC10BuildException, MalformedURLException {
        //final QUPCAR004030UVService service = new QUPCAR004030UVService();
        final QUPCAR004030UVService service = 
                QUPCAR004030UVServiceUtil.getQUPCAR004040UVService();

        // here I obtain the service (proxy).
        final QUPCAR004030UVPortType portType = service.getQUPCAR004030UVPort();
        final DefaultPCC10RequestFactory requestFactory = 
                new DefaultPCC10RequestFactory();
        final QUPCIN043200UV01 buildQUPCIN043200UV01 = requestFactory.build();

        // I set the end point for the 
        setDefaultEndPointURI(portType);

        final MCCIIN000002UV01 ack =
                portType.qupcAR004030UVQUPCIN043200UV(buildQUPCIN043200UV01);

        if (ack == null) {
            throw new IllegalStateException("Tests fails, non null arknowledge was expected");
        }
        
        System.out.println("Acknowledge : " + ack);
    }

    /**
     * JBoss specific way to customize the WSDL client (end point address).
     * 
     * @param portType the client to be customized.
     */
    private static void setDefaultEndPointURI(QUPCAR004030UVPortType portType) {
        final BindingProvider bp = (BindingProvider) portType;
        final Map<String, Object> reqContext = bp.getRequestContext();
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                "http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service");
    }
}
