/*
 * Project :iCardea
 * File : TripleException.java
 * Encoding : UTF-8
 * Date : Jun 16, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public final class TripleException extends Exception {
    
    private String subject; 
    private String predicate; 
    private String value; 
    private ValueType valueType; 

    /**
     * Creates a new instance of <code>TripleException</code> without detail message.
     */
    public TripleException() {
    }

    /**
     * Constructs an instance of <code>TripleException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TripleException(String msg) {
        super(msg);
    }

    public TripleException(Throwable thrwbl) {
        super(thrwbl);
    }

    public TripleException(String string, Throwable thrwbl) {
        super(string, thrwbl);
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
