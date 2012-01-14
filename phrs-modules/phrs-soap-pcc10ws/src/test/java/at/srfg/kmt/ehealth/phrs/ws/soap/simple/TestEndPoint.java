/*
 * Project :iCardea
 * File : TestEndPoint.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.simple;


import javax.xml.ws.Endpoint;

/**
 * Runnable class used to start a dummy web service end point. 
 * To run this class from maven environment use :
 * <pre>
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.ws.soap.simple.TestEndPoint -Dexec.classpathScope=test<br/>
 * </pre>
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class TestEndPoint {

    public static void main(String ...arg) {
        final String testWSURI = "http://localhost:8989/testws";
        System.out.println("End point starts on " + testWSURI);
        final Endpoint ep = Endpoint.create(new TestServiceImpl());
        ep.publish(testWSURI);
    }
}
