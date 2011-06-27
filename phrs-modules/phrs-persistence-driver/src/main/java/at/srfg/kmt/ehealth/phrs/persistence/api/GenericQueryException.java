/*
 * Project :iCardea
 * File : GenericQueryException.java
 * Encoding : UTF-8
 * Date : Jun 20, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class GenericQueryException extends Exception {

    public GenericQueryException(Throwable thrwbl) {
        super(thrwbl);
    }

    public GenericQueryException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public GenericQueryException(String string) {
        super(string);
    }

    public GenericQueryException() {
    }
    
}
