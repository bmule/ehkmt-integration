/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : DynaClassException.java 
 * Encoding : UTF-8
 * Date : Apr 1, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;


import javax.ejb.ApplicationException;
import org.apache.commons.beanutils.DynaClass;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@ApplicationException
public class DynaClassException extends Exception {

    /**                                                                                                                                                                            
     * A version number for this class so that serialization                                                                                                                       
     * can occur without worrying about the underlying class                                                                                                                       
     * changing between serialization and deserialization.                                                                                                                         
     */
    private static final long serialVersionUID = 5165L;

    private DynaClass dynaClass;

    /**
     * Creates a new instance of <code>DynaClassException</code> without detail message.
     */
    public DynaClassException() {
    }

    /**
     * Constructs an instance of <code>DynaClassException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DynaClassException(String msg) {
        super(msg);
    }

    public DynaClass getDynaClass() {
        return dynaClass;
    }

    public void setDynaClass(DynaClass dynaClass) {
        this.dynaClass = dynaClass;
    }

    public DynaClassException(Throwable thrwbl) {
        super(thrwbl);
    }

    public DynaClassException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
