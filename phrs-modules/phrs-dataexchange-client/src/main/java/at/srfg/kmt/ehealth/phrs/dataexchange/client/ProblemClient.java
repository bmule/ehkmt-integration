/*
 * Project :iCardea
 * File : ProblemsClient.java
 * Encoding : UTF-8
 * Date : Aug 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;
import static at.srfg.kmt.ehealth.phrs.Constants.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;


/**
 * Used to persist and retrieve vital signs information. <br/>
 * This class can not be extended. 
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class ProblemClient {

    /**
     * Holds the name for the creator, the instance responsible to create vital 
     * signs instances with this client. 
     */
    private static final String CREATOR_NAME = ProblemClient.class.getName();;

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    /**
     * Builds a <code>ProblemClient</code> instance. <br/>
     * <b>Note : </b> This constructor builds its own individual connection 
     * (and don't) share it with the rest of the applications.
     * 
     * @throws GenericRepositoryException if the underlying persistence layer 
     * can not be initialized from any reasons.
     * @throws TripleException 
     */
    ProblemClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();
    }

    /**
     * Builds a <code>ProblemClient</code> instance for a given triplestrore.
     * 
     * @param triplestore the triplestore instance, it can not be null.
     * @throws NullPointerException if the <code>triplestore</code> 
     * argument is null. 
     */
    ProblemClient(GenericTriplestore triplestore) {
        
        if (triplestore == null) {
            throw new NullPointerException("The triplestore");
        }
        
        this.triplestore = triplestore;
    }

    void addProblem(String user, String estabilishCode, String statusURI, String startDate,
            String endDate, String note, String valueCode) throws TripleException {

        final String subject =
                triplestore.persist(Constants.OWNER, user, LITERAL);
        
        // this can help to find a vial sign, there are alos other way 
        // to do this (e.g. using the know templateRootID, for more )
        // information about this please consult the documentation)
        triplestore.persist(subject,
                Constants.RDFS_TYPE,
                Constants.PHRS_OBSERVATION_ENTRY_CLASS,
                RESOURCE);

        // Here I make from this Observation an problem
        triplestore.persist(subject,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                "2.16.840.1.113883.10.20.1.28",
                LITERAL);

        // Here I make from this Observation an problem
        triplestore.persist(subject,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                "1.3.6.1.4.1.19376.1.5.3.1.4.5",
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_CODE,
                estabilishCode,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_STATUS,
                statusURI,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_START_DATE,
                startDate,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_END_DATE,
                endDate,
                RESOURCE);

        triplestore.persist(subject,
                Constants.SKOS_NOTE,
                note,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_VALUE_CODE,
                valueCode,
                RESOURCE);
    }

    Iterable<Triple> getObservations() throws TripleException {
        final Iterable<String> resources =
                triplestore.getForPredicateAndValue(RDFS_TYPE, PHRS_OBSERVATION_ENTRY_CLASS, RESOURCE);

        final MultiIterable result = new MultiIterable();

        for (String resource : resources) {
            final Iterable<Triple> forSubject = triplestore.getForSubject(resource);
            result.addIterable(forSubject);
        }

        return result;
    }
    
        /**
     * Returns all the Vital Signs for a given user.
     * 
     * @param userId
     * @return
     * @throws TripleException 
     */
    Iterable<Triple> getObservationsForUser(String userId) throws TripleException {

        final Iterable<String> resources =
                triplestore.getForPredicateAndValue(Constants.OWNER, userId, LITERAL);

        final MultiIterable result = new MultiIterable();

        for (String resource : resources) {
            final Iterable<Triple> forSubject = triplestore.getForSubject(resource);
            result.addIterable(forSubject);
        }

        return result;
    }
}
