/*
 * Project :iCardea
 * File : Triple.java
 * Encoding : UTF-8
 * Date : Jun 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public final class Triple {

    private final String subject;

    private final String predicate;

    private final String value;

    private final ValueType valueType;

    public Triple(String subject, String predicate, String value) {
        this(subject, predicate, value, null);
    }

    public Triple(String subject, String predicate, String value, ValueType type) {
        this.subject = subject;
        this.predicate = predicate;
        this.value = value;
        this.valueType = type;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getSubject() {
        return subject;
    }

    public String getValue() {
        return value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public String toString() {
        return "Triple{" + "subject=" + subject + ", predicate=" + predicate + ", value=" + value + '}';
    }
}
