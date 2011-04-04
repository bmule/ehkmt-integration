/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : DynamicBeanException.java 
 * Encoding : UTF-8
 * Date : Apr 1, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;


import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import javax.ejb.ApplicationException;


/**
 * ApplicationException used to signals a problem related with a 
 * <code>DynamicBean</code>.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@ApplicationException
public class DynamicBeanException extends Exception {

    /**                                                                                                                                                                            
     * A version number for this class so that serialization                                                                                                                       
     * can occur without worrying about the underlying class                                                                                                                       
     * changing between serialization and deserialization.                                                                                                                         
     */
    private static final long serialVersionUID = 5165L;
    
    private DynamicBean bean;

    /**
     * Creates a new instance of <code>DynamicBeanException</code> without detail message.
     */
    public DynamicBeanException() {
    }

    /**
     * Constructs an instance of <code>DynamicBeanException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DynamicBeanException(String msg) {
        super(msg);
    }
}
