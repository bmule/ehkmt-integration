/*
 * Project :iCardea
 * File : PCC09InputProcessorException.java 
 * Encoding : UTF-8
 * Date : Apr 11, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09.api;


import javax.ejb.ApplicationException;


/**
 * Indicate an error that occurs a pcc 09 query processing.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@ApplicationException
public class PCC09InputProcessorException extends Exception {

    /**                                                                                                                                                                            
     * A version number for this class so that serialization                                                                                                                       
     * can occur without worrying about the underlying class                                                                                                                       
     * changing between serialization and deserialization.                                                                                                                         
     */
    private static final long serialVersionUID = 5165L;

    /**
     * Creates a new instance of <code>PCC09InputProcessorException</code> without detail message.
     */
    public PCC09InputProcessorException() {
    }

    /**
     * Constructs an instance of <code>PCC09InputProcessorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PCC09InputProcessorException(String msg) {
        super(msg);
    }

    public PCC09InputProcessorException(Throwable thrwbl) {
        super(thrwbl);
    }

    public PCC09InputProcessorException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
