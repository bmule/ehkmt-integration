/*
 * Project :iCardea
 * File : TestServiceImpl.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.simple;

import javax.jws.WebService;

/**
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
@WebService(endpointInterface="at.srfg.kmt.ehealth.phrs.ws.soap.simple.TestService")
public class TestServiceImpl implements  TestService {

    @Override
    public void doStuff() {
        System.out.println("HERE");
        System.out.println("HERE");
        System.out.println("HERE");
        System.out.println("HERE");
    }

}
