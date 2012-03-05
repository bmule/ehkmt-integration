/*
 * Project :iCardea
 * File : ClientException.java
 * Encoding : UTF-8
 * Date : Mar 5, 2012
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.client;

/**
 * Indicates a client side error. 
 * Most of the times it wraps the real cause for the exception, this
 * cause can be obtained by using the
 * <code>getCause()</code> method.
 
 * 
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class ClientException extends Exception {

    /**
     * A version number for this class so that serialization can occur without
     * worrying about the underlying class changing between serialization and
     * deserialization.
     */
    private static final long serialVersionUID = 5165L;

    /**
     * Constructs an
     * <code>ConsumeException</code> without a detail message or a specified
     * cause.
     */
    public ClientException() {
        // UNIMPLEMENTED
    }

    /**
     * Constructs an
     * <code>ConsumeException</code> with a detail message.
     *
     * @param message the detail message.
     */
    public ClientException(String string) {
        super(string);
    }

    /**
     * Constructs an
     * <code>ConsumeException</code> with a specified
     * <code>Exception</code> cause, this cause can be obtained using the
     * <code>getCause()</code> method.
     *
     * @param cause the cause for this exception. This is the exception to wrap
     * and chain.
     * @see Exception#getCause()
     */
    public ClientException(Throwable thrwbl) {
        super(thrwbl);
    }

    /**
     * Constructs an
     * <code>ConsumeException</code> with a detail message and a specified
     * <code>Exception</code> cause, this cause can be obtained using the
     * <code>getCause()</code> method.
     *
     * @param message the detail message.
     * @param cause the cause for this exception. This is the exception to wrap
     * and chain.
     * @see Exception#getCause()
     */
    public ClientException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
