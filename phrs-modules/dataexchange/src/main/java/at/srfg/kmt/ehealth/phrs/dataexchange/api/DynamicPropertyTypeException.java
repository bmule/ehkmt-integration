/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : DynamicPropertyTypeException.java 
 * Encoding : UTF-8
 * Date : Apr 1, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;

import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import javax.ejb.ApplicationException;



/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@ApplicationException
public class DynamicPropertyTypeException extends Exception {

    /**                                                                                                                                                                            
     * A version number for this class so that serialization                                                                                                                       
     * can occur without worrying about the underlying class                                                                                                                       
     * changing between serialization and deserialization.                                                                                                                         
     */
    private static final long serialVersionUID = 5165L;

    private DynamicPropertyType propertyType;

    /**
     * Creates a new instance of <code>DynamicPropertyTypeException</code> without detail message.
     */
    public DynamicPropertyTypeException() {
    }

    /**
     * Constructs an instance of <code>DynamicPropertyTypeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DynamicPropertyTypeException(String msg) {
        super(msg);
    }

    public DynamicPropertyTypeException(Throwable thrwbl) {
        super(thrwbl);
    }

    public DynamicPropertyTypeException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    
    public DynamicPropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(DynamicPropertyType propertyType) {
        this.propertyType = propertyType;
    }
    
    
}
