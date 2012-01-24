/*
 * Project :iCardea
 * File : ProblemsClient.java
 * Encoding : UTF-8
 * Date : Aug 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import static at.srfg.kmt.ehealth.phrs.Constants.*;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.*;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.LITERAL;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.RESOURCE;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to persist and retrieve Problem Entries information. <br/> A Problem
 * entry can be used to describe following : <ul> <li> Problem <li> Risk <li>
 * Activities of daily living <li> Symptom <li> </ul>
 *
 * To to mutate a <i>Problem Entry</i> in to one of he upper described types you
 * need set the
 * <code>estabilishCode</code> property with one of the proper value, the value
 * must be accorded with a controlled vocabulary (for more information you need
 * to use the constant class. </br>
 *
 * This class can not be extended.
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public final class ProblemEntryClient {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemEntryClient</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProblemEntryClient.class);

    /**
     * Holds the name for the creator, the instance responsible to create
     * medication instances with this client.
     */
    private String creator;

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    private final SchemeClient schemeClient;

    /**
     * Builds a
     * <code>ProblemEntryClient</code> instance. <br/> <b>Note : </b> This
     * constructor builds its own individual connection to the triple store and
     * don't share it with the rest of the application.
     *
     * @throws GenericRepositoryException if the underlying persistence layer
     * can not be initialized from any reasons.
     * @throws TripleException
     */
    public ProblemEntryClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();

        schemeClient = new SchemeClient(triplestore);
        creator = ProblemEntryClient.class.getName();
    }

    /**
     * Builds a
     * <code>ProblemEntryClient</code> instance for a given triplestrore.
     *
     * @param triplestore the triplestore instance, it can not be null.
     * @throws NullPointerException if the
     * <code>triplestore</code> argument is null.
     */
    public ProblemEntryClient(GenericTriplestore triplestore) {
        if (triplestore == null) {
            throw new NullPointerException("The triplestore argument can not be null.");
        }

        this.triplestore = triplestore;

        schemeClient = new SchemeClient(triplestore);
        creator = ProblemEntryClient.class.getName();
    }

    /**
     *
     *
     * @param user
     * @param estabilishCode
     * @param statusURI
     * @param startDate
     * @param endDate
     * @param note
     * @param valueCode
     * @return
     * @throws TripleException
     */
    public String addProblemEntry(String user,
            String estabilishCode,
            String statusURI,
            String startDate,
            String endDate,
            String note,
            String valueCode) throws TripleException {

        final String subject =
                triplestore.persist(Constants.OWNER, user, LITERAL);


        // generic informarion (beside the 'OWNER' they are not really relevant 
        // for the HL7 V3 message)
        triplestore.persist(subject,
                CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);

        // I preffer to hang a specific name for the Creator only for test 
        // purposes. In this way I can follow the the origin for a certain 
        // resource. 
        triplestore.persist(subject,
                CREATOR,
                creator,
                LITERAL);

        // this can help to find a problem entry, there are alos other way 
        // to do this (e.g. using the know templateRootID, for more )
        // information about this please consult the documentation)
        triplestore.persist(subject,
                RDFS_TYPE,
                PHRS_OBSERVATION_ENTRY_CLASS,
                RESOURCE);


        // Here I make from this Observation an problem
        triplestore.persist(subject,
                HL7V3_TEMPLATE_ID_ROOT,
                PROBLEM_OBSERVATION,
                LITERAL);

        // Here I make from this Observation an problem
        triplestore.persist(subject,
                HL7V3_TEMPLATE_ID_ROOT,
                PROBLEM_ENTRY,
                LITERAL);

        triplestore.persist(subject,
                HL7V3_CODE,
                estabilishCode,
                LITERAL);

        triplestore.persist(subject,
                HL7V3_STATUS,
                statusURI,
                RESOURCE);

        final String startDateStr = startDate == null
                ? DateUtil.getFormatedDate(new Date())
                : startDate;
        triplestore.persist(subject,
                HL7V3_START_DATE,
                startDateStr,
                LITERAL);

        final String endDateStr = endDate == null
                ? DateUtil.getFormatedDate(new Date())
                : endDate;
        triplestore.persist(subject,
                HL7V3_END_DATE,
                endDateStr,
                LITERAL);

        triplestore.persist(subject,
                SKOS_NOTE,
                note,
                LITERAL);

        triplestore.persist(subject,
                HL7V3_VALUE_CODE,
                valueCode,
                RESOURCE);

        return subject;
    }

    public Iterable<Triple> getProblemEntryTriples() throws TripleException {
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
     * Returns all the Problem Entries for a given user.
     *
     * @param userId
     * @return
     * @throws TripleException
     */
    public Iterable<Triple> getProblemEntryTriplesForUser(String userId) throws TripleException {

        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(RDFS_TYPE, PHRS_OBSERVATION_ENTRY_CLASS);
        queryMap.put(OWNER, userId);

        // here I search for all resources with 
        // rdf type == prblem entry (observation entry) 
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);

        final MultiIterable result = new MultiIterable();

        for (String resource : resources) {
            final Iterable<Triple> forSubject = triplestore.getForSubject(resource);
            result.addIterable(forSubject);
        }

        return result;
    }

    /**
     * Returns all the Problem Entries for a given user.
     *
     * @param userId
     * @return
     * @throws TripleException
     */
    public Iterable<String> getProblemEntriesURIForUser(String userId) throws TripleException {

        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(RDFS_TYPE, PHRS_OBSERVATION_ENTRY_CLASS);
        queryMap.put(OWNER, userId);

        // here I search for all resources with 
        // rdf type == prblem entry (observation entry) 
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);


        return resources;
    }

    public void updateProblemEntry(String resourceURI, String predicate, String newValue)
            throws TripleException {
        final boolean resourceExists = triplestore.exists(resourceURI);
        if (!resourceExists) {
            final String msg =
                    String.format("There is no resource for this URI %s ", resourceURI);
            throw new IllegalArgumentException(msg);
        }

        final boolean propertyExists = schemeClient.propertyExists(predicate);
        if (!propertyExists) {
            final String msg =
                    String.format("The predicate %s does not exists like property", predicate);
            throw new IllegalArgumentException(msg);
        }

        // TODO : this mode to detect the value type is not the best one,
        // it has its limitations. E.G. no Bnode is supported, no expicit
        // classes are supported.
        // I chose to se this simple (and limited) solution becuase I it fits 
        // the needs for the Problem Entries
        final boolean isPropertyLiteral = schemeClient.isPropertyLiteral(predicate);
        final ValueType type = isPropertyLiteral
                ? LITERAL
                : RESOURCE;

        triplestore.delete(resourceURI, predicate);
        triplestore.persist(resourceURI, predicate, newValue, type);

        // NOTA BENE : the update operatin is responsible for the UPDATE_DATE property.
        // More preciselly the update must set the updated date to the date when
        // the resource was modified.
        setUpdateDate(resourceURI);
    }

    private void setUpdateDate(String resourceURI) throws TripleException {
        final boolean updateDateExists = triplestore.exists(resourceURI, UPDATE_DATE);
        if (updateDateExists) {
            triplestore.delete(resourceURI, UPDATE_DATE);
        }

        final String newDate = DateUtil.getFormatedDate(new Date());
        triplestore.persist(resourceURI, UPDATE_DATE, newDate, LITERAL);
    }

    public void deleteProblemEntry(String resourceURI) throws TripleException {

        if (resourceURI == null) {
            throw new NullPointerException("The resourceURI argument can not be null.");
        }

        final boolean exists = triplestore.exists(resourceURI);
        if (!exists) {
            LOGGER.warn("No resource for this {} URI, remove has no effect.", resourceURI);
            return;
        }

        triplestore.deleteForSubject(resourceURI);
    }

    /**
     * Registers a new creator for all the resources generated with this client.
     * All the generated resources will gain a triple with the predicate :
     * <code>Constants.CREATOR</code> and the value specified with the argument
     * <code>creator</code>.
     *
     * @param creator the new owner for this client, it can not be null.
     * @throws NullPointerException if the
     * <code>creator</code> argument is null.
     */
    public void setCreator(String creator) {
        if (creator == null) {
            final NullPointerException exception =
                    new NullPointerException("The creator argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        this.creator = creator;
    }
}
