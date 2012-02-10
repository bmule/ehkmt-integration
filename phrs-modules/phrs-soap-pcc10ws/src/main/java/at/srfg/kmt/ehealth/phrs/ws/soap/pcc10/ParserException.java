/*
 * Project :iCardea
 * File : ParserException.java
 * Encoding : UTF-8
 * Date : Feb 10, 2012
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


/**
 * Used to signal a exception in the parsing process. <br/>
 * This
 * 
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class ParserException extends Exception {
    
    private Object toBeParsed;

    /**
     * Creates a new instance of <code>ParserException</code> without detail message.
     */
    public ParserException() {
        // UNIMPLEMENTED
    }

    /**
     * Constructs an instance of <code>ParserException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ParserException(String msg) {
        super(msg);
    }

    public ParserException(Throwable thrwbl) {
        super(thrwbl);
    }

    public ParserException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public Object getToBeParsed() {
        return toBeParsed;
    }

    public void setToBeParsed(Object toBeParsed) {
        this.toBeParsed = toBeParsed;
    }
    
}
