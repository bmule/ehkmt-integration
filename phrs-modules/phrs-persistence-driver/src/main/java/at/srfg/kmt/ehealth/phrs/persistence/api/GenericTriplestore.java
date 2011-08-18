/*
 * Project :iCardea
 * File : GenericTriplestore.java
 * Encoding : UTF-8
 * Date : Jun 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.persistence.api;


import java.util.List;
import java.util.Map;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public interface GenericTriplestore {

    boolean exists(String subject, String predicate, String value, ValueType valueType);

    boolean exists(String subject, String predicate);

    boolean exists(String subject);

    boolean exists(List<PathElement> path);

    void persist(String subject, String predicate, String value, ValueType valueType)
            throws TripleException;

    String persist(String predicate, String value, ValueType valueType) throws TripleException;
    
    void delete(String subject, String predicate, String value, ValueType valueType) throws TripleException;

    void delete(String subject, String predicate) throws TripleException;

    void delete(List<PathElement> path) throws TripleException;

    void deleteForSubject(String subject) throws TripleException;

    Iterable<String> getForPredicateAndValue(String predicate, String value, ValueType valueType) 
            throws TripleException;

    Iterable<String> getForPredicatesAndValues(Map<String, String> predicatesValues) throws TripleException;
    
    Iterable<String> getForSubjectAndPredicate(String sub, String pred) throws TripleException;

    Iterable<Triple> getForSubject(String subject) throws TripleException;

    Iterable<Triple> getForPath(List<PathElement> path) throws TripleException;

    
    // TODO: think about this
    Object getForQuery(String query) 
            throws GenericRepositoryException, GenericQueryException;
}
