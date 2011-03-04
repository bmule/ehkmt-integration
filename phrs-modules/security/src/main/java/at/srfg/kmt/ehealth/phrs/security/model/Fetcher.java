/*
 * Project :iCardea
 * File : Fetcher.java
 * Date : Feb 14, 2011
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.model;

/**
 * Define an abstract fetch behavior for lazy initialized relations.
 * 
 * @param <T> the type for the entity where the fetch will be appied. 
 * @author mihai
 */
public  interface   Fetcher<T> {
    
    /**
     * Fetch lazy initialized relations.
     * 
     * @param toFetch the entity where the fetch will applied.
     */
    void fetch(T toFetch);
}
