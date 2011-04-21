/*
 * Project :iCardea
 * File : PCC10ResponseFactory.java 
 * Encoding : UTF-8
 * Date : Apr 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.api;


/**
 * Defines ways how a PCC 10(<code>QUPCIN043200UV01</code>) transaction 
 * request/response acknowledge are builded. The result type must be specified
 * like type parameter.
 * 
 * @param RESULT_TYPE the builded result type.
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface  PCC10Factory <RESULT_TYPE> {

    /**
     * Builds a certain request/response, the implementation decide if the 
     * result is a request or a response acknowledge. 
     * 
     * @return a certain request/response.
     * @throws PCC10ResponseBuildException 
     */
    RESULT_TYPE build() throws PCC10BuildException;
    
}
