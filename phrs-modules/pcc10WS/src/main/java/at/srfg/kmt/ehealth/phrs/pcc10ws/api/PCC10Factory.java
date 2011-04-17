/*
 * Project :iCardea
 * File : PCC10ResponseFactory.java 
 * Encoding : UTF-8
 * Date : Apr 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.api;


import java.util.Map;


/**
 * Defines ways how a PCC 10(<code>QUPCIN043200UV01</code>) transaction 
 * request/response acknowledge are builded. The builded request/response
 * can have a default value or it can be based on a configuration.
 * The configuration is specified like a Map where the key and the values must 
 * have a certain (given type).
 * 
 * @param RESULT_TYPE the builded result type.
 * @param CONF_KEY_TYPE  the key type used in configuration.
 * @param CONF_VALUE_TYPE the value type used in configuration.
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface  PCC10Factory <RESULT_TYPE, CONF_KEY_TYPE, CONF_VALUE_TYPE> {

    /**
     * Builds a certain request/response, the implementation decide if the 
     * result is a request or a response acknowledge. 
     * 
     * @return a certain request/response.
     * @throws PCC10ResponseBuildException 
     */
    RESULT_TYPE build() throws PCC10BuildException;
    
    /**
     * Builds a request/response for a certain configuration, 
     * the implementation decide if the result is a request or a response 
     * acknowledge. 
     * 
     * @param config the configuration.
     * @return a a request/response for a certain configuration.
     */
    RESULT_TYPE build(Map<CONF_KEY_TYPE, CONF_VALUE_TYPE> config) throws PCC10BuildException;
}
