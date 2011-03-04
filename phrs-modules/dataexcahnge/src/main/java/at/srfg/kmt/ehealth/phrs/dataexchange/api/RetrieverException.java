/*
 * Project :iCardea
 * File : RetreiverException.java
 * Encoding : UTF-8
 * Date : Mar 4, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.api;

/**
 * Used to signals a problem during querying for objects 
 * (on the underlaying layers).
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class RetrieverException extends Exception {

    /**
     * Creates a new instance of <code>RetreiverException</code> without detail message.
     */
    RetrieverException() {
    }


    /**
     * Constructs an instance of <code>RetreiverException</code> with the specified detail message.
     * @param msg the detail message.
     */
    RetrieverException(String msg) {
        super(msg);
    }
}
