/*
 * Project :iCardea
 * File : DynamicBeanRepositoryRestWSExample.java 
 * Encoding : UTF-8
 * Date : Apr 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;


/**
 * This shows how the RestEasy is used to pull instances from the repository
 * and print them to the standard output. </br>
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.ws.RestWSPullExample -Dexec.classpathScope=test<br>
 * Take care the doesn not compile the classes.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class RestWSPullExample {

    public static final String GET_LAST_FOR_CLASS = 
            "http://localhost:8080/dataexchange_ws/dynamic_bean_repository/getLastForClass";

    public static void main(String args[]) throws Exception {
        pull("ActivityItem");
        pull("ActivityOfDailyLiving");
        pull("ActivityLevel");
        pull("BloodPressure");
        pull("BodyWeight");
        pull("Medication");
        pull("Problem");
        pull("Risk");
    }
    
    private static void pull(String string) throws Exception {
        final String uri = GET_LAST_FOR_CLASS;
        final ClientRequest request = new ClientRequest(uri);
        final String classURI = "at.srfg.kmt.ehealth.phrs.datamodel.impl." + string;
        request.queryParameter("class_uri", classURI);

        System.out.println("Tries to pull the last instace for " + classURI);
        final ClientResponse response = request.get();
        final int statusCode = response.getStatus();
        System.out.println("Status Code = " + statusCode);
        if (statusCode != 200) {
            String msg = uri + " GET request can not be solved.";
            throw new IllegalStateException(msg);
        }
        
        final Object body = (String) response.getEntity(String.class);
        System.out.println("Body : " + body);
    }
}
