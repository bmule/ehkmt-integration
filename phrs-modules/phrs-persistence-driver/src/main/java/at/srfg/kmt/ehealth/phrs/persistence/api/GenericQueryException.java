/*
 * Project :iCardea
 * File : GenericQueryException.java
 * Encoding : UTF-8
 * Date : Jun 20, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;

/**
 * Used to signal an error that occurs during a query opperation. The reason for
 * this exception can be a triplestore specific exception. Most of the times it
 * wraps the real cause for the exception, this cause can be obtained by using
 * the
 * <code>getCause()</code> method.
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class GenericQueryException extends Exception {

    /**
     * A version number for this class so that serialization can occur without
     * worrying about the underlying class changing between serialization and
     * deserialization.
     */
    private static final long serialVersionUID = 5165L;

    /**
     * Creates a new instance of
     * <code>GenericQueryException</code> without detail message.
     */
    public GenericQueryException() {
        // UNIMPLEMETNED
    }

    /**
     * Constructs an instance of
     * <code>GenericQueryException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public GenericQueryException(String string) {
        super(string);
    }

    /**
     * Constructs an
     * <code>GenericQueryException</code> with a specified
     * <code>Exception</code> cause, this cause can be obtained using the
     * <code>getCause()</code> method.
     *
     * @param cause the cause for this exception. This is the exception to wrap
     * and chain.
     * @see Exception#getCause()
     */
    public GenericQueryException(Throwable thrwbl) {
        super(thrwbl);
    }

    /**
     * Constructs an
     * <code>GenericRepositoryException</code> with a
     * detail message and a specified <code>Exception</code>
     * cause, this cause can be obtained using the
     * <code>getCause()</code> method.
     * 
     * @param message the detail message.
     * @param cause the cause for this exception. This is the
     *            exception to wrap and chain.
     * @see Exception#getCause()
     */
    public GenericQueryException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
