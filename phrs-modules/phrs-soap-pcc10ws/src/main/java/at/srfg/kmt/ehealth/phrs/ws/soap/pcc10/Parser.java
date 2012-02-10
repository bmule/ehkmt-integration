/*
 * Project :iCardea
 * File : Parser.java
 * Encoding : UTF-8
 * Date : Feb 10, 2012
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


/**
 * Defines the basic functionality for a parser.
 *  
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public interface Parser<T> {
    
    /**
     * Returns true if this parser can parse the given object.
     * 
     * @param toParse the object to be parsed.
     * @return 
     */
    boolean canParse(T toParse);
    
    /**
     * Parses the given object.
     * 
     * @param toParse the object to be parsed.
     * @throws ParserException by any kind of error.
     */
    void parse(T toParse) throws ParserException;

}
