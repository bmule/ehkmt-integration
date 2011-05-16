/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : DynamicBeanRepositoryRestWSExample.java 
 * Encoding : UTF-8
 * Date : Apr 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;


/**
 * This example load all the default classes in to the repository. </br>
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.ws.RestWSLoadDefClassesExample -Dexec.classpathScope=test<br>
 * Take care the doesn not compile the classes.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class RestWSLoadDefClassesExample {

    public static void main(String args[]) throws Exception {
        final String uri =
                "http://localhost:8080/dataexchange_ws/dynamic_class_repository/loadDefaultClasses";
        System.out.println("Send GET request on : " + uri);

        final ClientRequest request = new ClientRequest(uri);

        final ClientResponse response = request.get();
        final int statusCode = response.getStatus();
        System.out.println("Status Code = " + statusCode);
        if (statusCode != 200) {
            String msg = uri + " POST request can not be solved.";
            throw new IllegalStateException(msg);
        }
    }
}
