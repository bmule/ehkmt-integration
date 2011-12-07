/*
 * Project :iCardea
 * File : TripleException.java
 * Encoding : UTF-8
 * Date : Jun 16, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.persistence.api;


/**
 * Indicates an error related with the predicate calculus. This Most of the
 * times it wraps the real cause for the exception, this cause can be obtained
 * by using the
 * <code>getCause()</code> method. This method is also transports the triple
 * that may cause the problem, this information contains : the subject (a.k.a
 * resource), the predicate, the value and the value type. The triple related
 * information can be manipulated with the setXXX and getXXX methods. 
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai Radulescu
 */
public final class TripleException extends Exception {

    /**
     * A version number for this class so that serialization can occur without
     * worrying about the underlying class changing between serialization and
     * deserialization.
     */
    private static final long serialVersionUID = 5165L;

    /**
     * The subject (resource) that may cause this exception.
     */
    private String subject;

    /**
     * The predicate applied to the subject that may cause this exception.
     */
    private String predicate;

    /**
     * The value for predicate applied to the subject that may cause this
     * exception.
     */
    private String value;

    /**
     * The subject (resource) that may cause this exception.
     */
    private ValueType valueType;

    /**
     * Creates a new instance of
     * <code>TripleException</code> without detail message.
     */
    public TripleException() {
    }

    /**
     * Constructs an instance of
     * <code>TripleException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public TripleException(String msg) {
        super(msg);
    }

    /**
     * Constructs an
     * <code>TripleException</code> with a specified
     * <code>Exception</code> cause, this cause can be obtained using the
     * <code>getCause()</code> method.
     *
     * @param cause the cause for this exception. This is the exception to wrap
     * and chain.
     * @see Exception#getCause()
     */
    public TripleException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an
     * <code>TripleException</code> with a
     * detail message and a specified <code>Exception</code>
     * cause, this cause can be obtained using the
     * <code>getCause()</code> method.
     * 
     * @param message the detail message.
     * @param cause the cause for this exception. This is the
     *            exception to wrap and chain.
     * @see Exception#getCause()
     */
    public TripleException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("TripleException{");
        result.append("subject=");
        result.append(subject);
        result.append(", predicate=");
        result.append(predicate);
        result.append(", value=");
        result.append(value);
        result.append(", valueType=");
        result.append(valueType);
        result.append("}");

        return result.toString();
    }
}
