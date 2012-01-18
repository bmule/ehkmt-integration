/*
 * Project :iCardea
 * File : ConsumeException.java
 * Encoding : UTF-8
 * Date : Jan 18, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


/**
 * Indicates that a certain map of parameters can not be consumed from any
 * reasons. Most of the times it wraps the real cause for the exception, this
 * cause can be obtained by using the
 * <code>getCause()</code> method.
 *
 * @author M1s
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class ConsumeException extends Exception {

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
    public ConsumeException() {
        // UNIMPLEMENTED
    }

    /**
     * Constructs an
     * <code>ConsumeException</code> with a detail message.
     *
     * @param message the detail message.
     */
    public ConsumeException(String string) {
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
    public ConsumeException(Throwable thrwbl) {
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
    public ConsumeException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
