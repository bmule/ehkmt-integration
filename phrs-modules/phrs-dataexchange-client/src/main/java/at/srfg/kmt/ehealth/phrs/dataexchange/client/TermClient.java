/*
 * Project :iCardea
 * File : TermClient.java
 * Encoding : UTF-8
 * Date : Aug 19, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import static at.srfg.kmt.ehealth.phrs.Constants.SKOS_RELATED;

import at.srfg.kmt.ehealth.phrs.dataexchange.util.StoreValidator;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.RESOURCE;
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

    public Iterable<Triple> getTermsRelatedWith(String resourceURI) throws TripleException {
        //check for null
        StoreValidator.validateNotNull("getTermsRelatedWith resourceURI is null",resourceURI);

        final Iterable<String> resources = 
                triplestore.getForPredicateAndValue(SKOS_RELATED, resourceURI, RESOURCE);
        
        final MultiIterable result = new MultiIterable();
        for (String resource : resources) {
            final Iterable<Triple> triples = triplestore.getForSubject(resource);
            result.addIterable(triples);
        }
        
        return result;
    }
}
