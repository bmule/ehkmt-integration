/*
 * Project :iCardea
 * File : Processor.java 
 * Encoding : UTF-8
 * Date : Apr 20, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.api;


import java.util.Set;
import org.jboss.jms.client.delegate.ClientConnectionDelegate.getExceptionListener_N4742818582415724694;


/**
 * Defines an abstract way to process items.
 * 
 * @param <RESULT_TYPE> if the process produces a result then this is its type.
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface Processor <RESULT_TYPE> {
    
    /**
     * Decides if a certain input can be processed.
     * 
     * @param input the input to be analyzed. 
     * @return true if the specified input can be processed with this processor.
     */
    boolean canProcess(Object input);
    
    /**
     * Process the input.
     * 
     * @param input the input to process.
     * @return true if the process was successfully, false by any kind of
     * failures. The failures causes can be obtained with the 
     * <code>getExceptions</code> method.
     * @see #getExceptions() 
     */
    boolean process(Object input);
    
    /**
     * 
     * @return 
     */
    Set<Exception> getExceptions();
    
    /**
     * 
     * @return 
     */
    RESULT_TYPE getResult();
    
}
