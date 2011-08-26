/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import static at.srfg.kmt.ehealth.phrs.Constants.*;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class can not be extended.
 * 
 * @author mihai
 */
public final class SchemeClient {
    
    private final GenericTriplestore triplestore;
    
    
    public SchemeClient(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
    }
    
    
    boolean propertyExists(String propertyURI) throws TripleException {
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
    
    Set<String> getPropertyTypes(String propertyURI) throws TripleException {
        
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
    
    boolean isPropertyLiteral(Set<String> propertyTypes) {
        final boolean result = propertyTypes.contains(RDF_LITERAL);
        return result;
    }
    
    boolean isPropertyLiteral(String propertyURI) throws TripleException {
        final Set<String> propertyTypes = getPropertyTypes(propertyURI);
        final boolean result = isPropertyLiteral(propertyTypes);
        return result;
    }
    
    boolean isPropertyResource(Set<String> propertyTypes) {
        final boolean result = !propertyTypes.contains(RDF_LITERAL);
        return result;
    }
    
    boolean isPropertyResource(String propertyURI) throws TripleException {
        final Set<String> propertyTypes = getPropertyTypes(propertyURI);
        final boolean result = isPropertyResource(propertyTypes);
        return result;
    }
    
    Set<String> getAllPropertiesForClass(String uri) {
        return null;
    }
}
