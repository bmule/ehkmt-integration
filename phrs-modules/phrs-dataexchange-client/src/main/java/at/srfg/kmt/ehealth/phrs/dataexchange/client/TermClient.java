/*
 * Project :iCardea
 * File : TermClient.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public final class TermClient {

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    public TermClient(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
    }

    Iterable<Triple> getTermsRelatedWith(String resourceURI) throws TripleException {
        final Iterable<String> resources = 
                triplestore.getForPredicateAndValue(Constants.SKOS_RELATED, 
                resourceURI, ValueType.RESOURCE);
        
        final MultiIterable result = new MultiIterable();
        for (String resource : resources) {
            final Iterable<Triple> triples = triplestore.getForSubject(resource);
            result.addIterable(triples);
        }
        
        return result;
    }
}
