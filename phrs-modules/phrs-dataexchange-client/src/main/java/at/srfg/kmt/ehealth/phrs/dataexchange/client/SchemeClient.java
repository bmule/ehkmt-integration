/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import static at.srfg.kmt.ehealth.phrs.Constants.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * It provides support for RDFS (e.g. RDF properties or RDF classes). 
 * 
 * This class can not be extended.
 * 
 * @author mihai
 */
public final class SchemeClient {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.SchemeClient</code>..
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SchemeClient.class);
    private final GenericTriplestore triplestore;

    public SchemeClient(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
    }

    public boolean propertyExists(String propertyURI) throws TripleException {
        final boolean exists = triplestore.exists(propertyURI);
        if (!exists) {
            return false;
        }
        final Iterable<Triple> forSubject =
                triplestore.getForSubject(propertyURI);
        // TODO : not very performant you can do this better with a query
        for (Triple triple : forSubject) {
            final String predicate = triple.getPredicate();
            final String value = triple.getValue();
            final boolean hasType = predicate.equals(RDFS_TYPE);
            final boolean isRDFProperty = value.equals(RDF_PROPERTY_TYPE);
            if (hasType && isRDFProperty) {
                return true;
            }
        }

        return false;
    }

    public Set<String> getPropertyTypes(String propertyURI) throws TripleException {

        final boolean exists = triplestore.exists(propertyURI);
        if (!exists) {
            return null;
        }

        // they must be only one.
        final Iterable<String> ranges =
                triplestore.getForSubjectAndPredicate(propertyURI, RDFS_RANGE);
        final Set<String> result = new HashSet<String>();
        for (String type : ranges) {
            result.add(type);
        }

        return result;
    }

    public boolean isPropertyLiteral(Set<String> propertyTypes) {
        final boolean result = propertyTypes.contains(RDF_LITERAL);
        return result;
    }

    public boolean isPropertyLiteral(String propertyURI) throws TripleException {
        final Set<String> propertyTypes = getPropertyTypes(propertyURI);
        final boolean result = isPropertyLiteral(propertyTypes);
        return result;
    }

    public boolean isPropertyResource(Set<String> propertyTypes) {
        if (propertyTypes == null) {
            return false;
        }
        
        final boolean result = !propertyTypes.contains(RDF_LITERAL);
        return result;
    }

    public boolean isPropertyResource(String propertyURI) throws TripleException {
        final Set<String> propertyTypes = getPropertyTypes(propertyURI);
        final boolean result = isPropertyResource(propertyTypes);
        return result;
    }

    public Set<String> getAllPropertiesForClass(String classURI) throws TripleException {

        final Map<String, String> query = new HashMap<String, String>();
        query.put(RDFS_TYPE, RDF_PROPERTY_TYPE);
        query.put(RDF_DOMAIN, classURI);

        final Iterable<String> properties = triplestore.getForPredicatesAndValues(query);
        final Set<String> result = new HashSet<String>();
        for (String property : properties) {
            result.add(property);
        }

        return result;
    }
}
