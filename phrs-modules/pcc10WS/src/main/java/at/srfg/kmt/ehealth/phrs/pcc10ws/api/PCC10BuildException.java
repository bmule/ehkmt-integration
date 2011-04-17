/*
 * Project :iCardea
 * File : PCC10ResponseException.java 
 * Encoding : UTF-8
 * Date : Apr 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.api;


/**
 * Indicates a certain error in the response build process. </br>
 * Most of the times it wraps the real cause for the exception,                                                                                                                 
 * this cause can be obtained by using the <code>getCause()</code>                                                                                                              
 * method. 
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class PCC10BuildException extends Exception {

    /**                                                                                                                                                                         
     * A version number for this class so that serialization                                                                                                                    
     * can occur without worrying about the underlying class                                                                                                                    
     * changing between serialization and deserialization.                                                                                                                      
     */
    private static final long serialVersionUID = 5165L;

    /**
     * Creates a new instance of <code>PCC10ResponseException</code> without 
     * detail message.
     */
    public PCC10BuildException() {
        // UNIMPLEMENTED.
    }

    /**
     * Constructs an instance of <code>PCC10ResponseException</code> with the 
     * specified detail message.
     * @param msg the detail message.
     */
    public PCC10BuildException(String msg) {
        super(msg);
    }

    /**                                                                                                                                                                         
     * Constructs an <code>PCC10ResponseException</code> with                                                                                                                   
     * a specified <code>Exception</code> cause, this cause                                                                                                                     
     * can be obtained using the <code>getCause()</code>                                                                                                                        
     * method.                                                                                                                                                                  
     *                                                                                                                                                                          
     * @param cause     the cause for this exception.                                                                                                                           
     *                  This is the exception to wrap and                                                                                                                       
     *                  chain.                                                                                                                                                  
     * @see             Exception#getCause()                                                                                                                                    
     */
    public PCC10BuildException(Throwable cause) {
        super(cause);
    }

    /**                                                                                                                                                                         
     * Constructs an <code>PCC10BuildException</code> with                                                                                                                   
     * a detail message and a specified <code>Exception</code>                                                                                                                  
     * cause, this cause can be obtained using the                                                                                                                              
     * <code>getCause()</code> method.                                                                                                                                          
     *                                                                                                                                                                          
     * @param message   the detail message.                                                                                                                                     
     * @param cause     the cause for this exception.                                                                                                                           
     *                  This is the exception to wrap and                                                                                                                       
     *                  chain.                                                                                                                                                  
     * @see             Exception#getCause()                                                                                                                                    
     */
    public PCC10BuildException(String message, Throwable cause) {
        super(message, cause);
    }
}
