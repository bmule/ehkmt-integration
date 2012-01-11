/*
 * Project :iCardea
 * File : TestService.java
 * Encoding : UTF-8
 * Date : Dec 9, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
@WebService
public interface TestService {

    @WebMethod
    void doStuff();
}
