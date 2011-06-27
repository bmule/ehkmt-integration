/*
 * Project :iCardea
 * File : GenericRepositoryException.java
 * Encoding : UTF-8
 * Date : Jun 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class GenericRepositoryException extends Exception {

    /**
     * Creates a new instance of <code>GenericRepositoryException</code> without detail message.
     */
    public GenericRepositoryException() {
    }

    /**
     * Constructs an instance of <code>GenericRepositoryException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public GenericRepositoryException(String msg) {
        super(msg);
    }

    public GenericRepositoryException(Throwable thrwbl) {
        super(thrwbl);
    }

    public GenericRepositoryException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
    
}
