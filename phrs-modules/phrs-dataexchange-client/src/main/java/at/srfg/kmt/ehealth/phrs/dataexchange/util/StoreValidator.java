package at.srfg.kmt.ehealth.phrs.dataexchange.util;


import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import org.apache.commons.validator.routines.UrlValidator;


/**
 * It contains a collection of validation related methods. <br/>
 * This class is not be design to be extended.
 */
public final class StoreValidator {

    /**
     * The URL validator used in this utility class.
     */
    private final static UrlValidator URL_VALIDATOR;
    static {
        final String[] schemes = {"http", "https"};
        URL_VALIDATOR = new UrlValidator(schemes);
    }

    /**
     * Don't let anybody to instantiate this class.
     */
    private StoreValidator() {
        // UNIMPLEMENTED
    }

    /**
     * Proves if the provided value is a valid URL. Null is not considered a
     * valid URL.
     *
     * @param value the value to e proved.
     * @return true if the provided value is a valid URL, false otherwise.
     */
    public static boolean isValidReourceValue(String value) {
        final boolean isValidURL = URL_VALIDATOR.isValid(value);
        return isValidURL;
    }

    /**
     * Builds a
     * <code>IllegalArgumentException</code> for a given property and value
     * values. This exception is used to signals that the specified value for
     * the given property is invalid (e.g. is null).
     *
     * @param property the property.
     * @param value the value.
     * @return a
     * <code>IllegalArgumentException</code> for a given property and value
     * values.
     */
    public static IllegalArgumentException buildWrongReourceValueException(String property, String value) {
        final String msg = String.format("The property [%s] value [%s] is not valid. The value must be a valid URL", value, property);
        return new IllegalArgumentException(msg);
    }

    /**
     * Checks whether null or has http prefix
     *
     * @param propertyName
     * @param value
     * @throws TripleException
     * @deprecated use instead the StoreValidator.isValidReourceValue
     */
    @Deprecated
    public static void validateResource(final String propertyName, final String value) throws TripleException {
        final boolean isValidURL = URL_VALIDATOR.isValid(value);
        if (!isValidURL) {
            final String msg = String.format("This string [%s] does represents a valid URI.", value);
            throw new IllegalArgumentException(msg);
        }

        if (value != null && value.startsWith("http")) {
            //ok
        } else {
            throw new TripleException("ERROR, reject. Valid URI was expected. Value did not start with http or is NULL. property=" + propertyName + " value=" + value);
        }
    }

    /**
     * Checks whether null or has http prefix. TODO check existence of URI
     *
     * @param propertyName
     * @param value
     * @param triplestore
     * @throws TripleException
     * @deprecated use instead the StoreValidator.isValidReourceValue
     */
    @Deprecated
    public static void validateResource(final String propertyName, final String value, final GenericTriplestore triplestore) throws TripleException {
        validateResource(propertyName, value);

    }

    /**
     * 
     * @param propertyName
     * @param value
     * @throws TripleException
     * @deprecated use instead the StoreValidator.isValidReourceValue
     */
    @Deprecated
    public static void validateNotNull(String propertyName, String value) throws TripleException {

        if (value == null) {
            throw new TripleException("ERROR, reject. Property is NULL: " + propertyName);
        }

    }
}
