/*
 * Project :iCardea
 * File : DynamicBeanRepositoryRestWSExample.java 
 * Encoding : UTF-8
 * Date : Apr 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;


/**
 * This example is used to read a directory with json files and pus them in to 
 * the bean repository like bean instances. </br>
 * To use this class from maven environment use :
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.ws.RestWSPushExample -Dexec.classpathScope=test<br>
 * Take care the doesn not compile the classes.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class RestWSPushExample {

    public static final String JSON_FILES_DIRECOTRY = 
            "src/test/bash/json.instances/";

    public static void main(String args[]) throws Exception {
        final File dir = new File(JSON_FILES_DIRECOTRY);
        final File[] files = dir.listFiles();
        for (File file : files) {
            System.out.println("Process : " + file);
            if (!file.getName().startsWith(".")) {
                push(file);
            }
        }
    }

    private static void push(File jsonFile) throws Exception {
        final String uri =
                "http://localhost:8080/dataexchange_ws/dynamic_bean_repository/persist";
        System.out.println("Send POST request on : " + uri);
        
        final String json = 
                FileUtils.readFileToString(jsonFile);
        
        if (json == null) {
            throw new FileNotFoundException("File " + jsonFile.getAbsolutePath() + " can not be found.");
        }

        final ClientRequest request = new ClientRequest(uri);
        // send post with a certain from paramter
        request.formParameter("dynaBean", json);

        final ClientResponse response = request.post();
        final int statusCode = response.getStatus();
        System.out.println("Status Code = " + statusCode);
        if (statusCode != 200) {
            String msg = uri + " POST request can not be solved.";
            throw new IllegalStateException(msg);
        }
    }
}
