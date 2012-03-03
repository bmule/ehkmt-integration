package at.srfg.kmt.ehealth.phrs.dataexchange.util;


import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;

public class StoreValidator {

    public static void validateNotNull(String propertyName,String value) throws TripleException{

        if(value == null) {
            throw new TripleException("ERROR, reject. Property is NULL: "+propertyName);
        }

    }

    /**
     * Checks whether null or has http prefix
     * @param propertyName
     * @param value
     * @throws TripleException
     */
    public static void validateResource(final String propertyName,final String value) throws TripleException{

        if(value != null && value.startsWith("http")) {
           //ok
        } else {
            throw new TripleException("ERROR, reject. Valid URI was expected. Value did not start with http or is NULL. property="+propertyName+" value="+value);
        }


    }

    /**
     *   Checks whether null or has http prefix.
     *   TODO check existence of URI
     * @param propertyName
     * @param value
     * @param triplestore
     * @throws TripleException
     */
    public static void validateResource(final String propertyName,final String value,final GenericTriplestore triplestore) throws TripleException{

        validateResource(propertyName,value);


    }
}
