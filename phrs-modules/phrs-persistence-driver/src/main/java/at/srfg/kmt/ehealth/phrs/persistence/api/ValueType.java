/*
 * Project :iCardea
 * File : ResourceType.java
 * Encoding : UTF-8
 * Date : Jun 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public enum ValueType {

    /**
     * Identifies a 'urified' resource. 
     */
    RESOURCE, 
    
    /**
     * Identifies a blank (RDF) node.
     */
    B_NODE, 
    
    /**
     * Identifies a literal.
     */
    LITERAL;
}
