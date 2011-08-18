/*
 * Project :iCardea
 * File : ProblemsClient.java
 * Encoding : UTF-8
 * Date : Aug 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;


/**
 * Used to persist and retrieve vital signs information. 
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class ProblemClient {

    /**
     * The unique subject for the problem.
     */
    private static final String PROBLEM_SUBJECT =
            "http://www.icardea.at/phrs/instances/example/ProblemEntry";

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    /**
     * 
     * @throws GenericRepositoryException
     * @throws TripleException 
     */
    ProblemClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();
    }
    
    void addProblem(String estabilishCode, String statusURI, String startDate,
            String endDate, String note, String valueCode) throws TripleException {

        // Here I make from this Observation an problem
        triplestore.persist(PROBLEM_SUBJECT,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                "2.16.840.1.113883.10.20.1.28",
                LITERAL);

        // Here I make from this Observation an problem
        triplestore.persist(PROBLEM_SUBJECT,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                "1.3.6.1.4.1.19376.1.5.3.1.4.5",
                LITERAL);

        triplestore.persist(PROBLEM_SUBJECT,
                Constants.HL7V3_CODE,
                estabilishCode,
                LITERAL);

        triplestore.persist(PROBLEM_SUBJECT,
                Constants.HL7V3_STATUS,
                statusURI,
                RESOURCE);

        triplestore.persist(PROBLEM_SUBJECT,
                Constants.HL7V3_START_DATE,
                startDate,
                RESOURCE);

        triplestore.persist(PROBLEM_SUBJECT,
                Constants.HL7V3_END_DATE,
                endDate,
                RESOURCE);

        triplestore.persist(PROBLEM_SUBJECT,
                Constants.SKOS_NOTE,
                note,
                RESOURCE);

        triplestore.persist(PROBLEM_SUBJECT,
                Constants.HL7V3_VALUE_CODE,
                valueCode,
                RESOURCE);
    }

    Iterable<Triple> getProblems() throws TripleException {
        final Iterable<Triple> forSubject = triplestore.getForSubject(PROBLEM_SUBJECT);
        return forSubject;
    }
}
