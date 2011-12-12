/*
 * Project :iCardea
 * File : TestEndPoint.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ihe;

import javax.xml.ws.Endpoint;

/**
 * mvn exec:java -Dexec.mainClass=at.srfg.kmt.ehealth.phrs.dataexchange.ihe.TestEndPoint -Dexec.classpathScope=test<br/>
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class TestEndPoint {

    public static void main(String ...arg) {
        System.out.println("End point starts");
        Endpoint.publish("http://localhost:8989/testws", new QUPCAR004030UVWebService());
    }
}
