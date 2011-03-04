/*
 * Project :iCardea
 * File : PersistorException.java
 * Encoding : UTF-8
 * Date : Mar 4, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.api;

/**
 * Signals a problem during the persisting. 
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class PersistorException extends Exception {

    /**
     * Creates a new instance of <code>PersistorException</code> without detail message.
     */
    public PersistorException() {
        super();
    }


    /**
     * Constructs an instance of <code>PersistorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PersistorException(String msg) {
        super(msg);
    }
}
