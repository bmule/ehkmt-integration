/*
 * Project :iCardea
 * File : GenericTriplestoreLifecycle.java
 * Encoding : UTF-8
 * Date : Jun 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public interface GenericTriplestoreLifecycle {
    void init() throws GenericRepositoryException;
    void shutdown() throws GenericRepositoryException;
    void cleanEnvironment() throws GenericRepositoryException;
    
    void addToPostconstruct(Runnable runnable);
    void addToPredestroy(Runnable runnable);
}
