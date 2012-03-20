/*
 * Project :iCardea
 * File : PHRSRequestActor.java
 * Encoding : UTF-8
 * Date : Jan 29, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.StoreValidator;
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
 * This client is used to manage and manipulate PHRS Requests. A PHRS request
 * defines the relation between : <ul> <li> a patient unique id <li> a care
 * provision code <li> a response URI </ul>
 *
 * @author Mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public final class PHRSRequestClient {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.PHRSRequestClient</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PHRSRequestClient.class);

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    /**
     * Holds the name for the creator, the instance responsible to create
     * medication instances with this client.
     */
    private String creator;

    /**
     * Builds a
     * <code>PHRSRequestClient</code> instance. <br/> <b>Note : </b> This
     * constructor builds its own individual connection to the triple store and
     * don't share it with the rest of the application.
     *
     * @throws GenericRepositoryException if the underlying persistence layer
     * can not be initialized from any reasons.
     * @throws TripleException
     */
    public PHRSRequestClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();
    }

    /**
     * Builds a
     * <code>PHRSRequestClient</code> instance for a given triplestrore.
     *
     * @param triplestore the triplestore instance, it can not be null.
     * @throws NullPointerException if the
     * <code>triplestore</code> argument is null.
     */
    public PHRSRequestClient(GenericTriplestore triplestore) {
        this.triplestore = triplestore;
    }

    public String addPHRSRequest(String replyAddress, String id,
            String careProcisionCode) throws TripleException {

        LOGGER.debug("addPHRSRequest");
        //check for null
        StoreValidator.validateNotNull("replyAddress", replyAddress);
        StoreValidator.validateNotNull("patient id",id);
        StoreValidator.validateNotNull("careProcisionCode",careProcisionCode);

        final String[] toLog = {replyAddress, id, careProcisionCode};

        String existingUri=getExistingRequest(replyAddress,id,careProcisionCode);
        //Query for existing request... otherwise duplicates cause extra messages, no need to persist unless we note create date
        if(existingUri !=null){
            LOGGER.debug("addPHRSRequest found existing PHR request, do not add again for reply adress={}, id={}, care provision code={}",toLog);
           return existingUri;
        }

        final String subject =
                triplestore.persist(Constants.RDFS_TYPE,
                Constants.PHRS_REQUEST_CLASS,
                RESOURCE);

        triplestore.persist(subject,
                Constants.CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_REPLY_ADRESS,
                replyAddress,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_CARE_PROVISION_CODE,
                careProcisionCode,
                LITERAL);

        triplestore.persist(subject,
                Constants.PHRS_ACTOR_PROTOCOL_ID,
                id,
                LITERAL);


        LOGGER.debug("New PHR request sored for reply adress={}, id={}, care provision code={}", toLog);
        return subject;
    }

    public Iterable<String> getAllPHRSRequests() throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        return result;
    }

    public Iterable<Triple> getAllPHRSRequestsTriples() throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);

        final MultiIterable result = new MultiIterable();
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);
        for (String resource : resources) {
            final Iterable<Triple> forSubject = triplestore.getForSubject(resource);
            result.addIterable(forSubject);
        }

        return result;
    }
    
    public String getExistingRequest(String replyAddress, String id,
                          String careProcisionCode)  {

       Iterable<String> requests=null;
        String uri=null;
        try {
            requests = getAllPHRSRequests(replyAddress,id,careProcisionCode);
            if(requests != null){
                for(String req:requests){
                    uri=req;
                    break;
                }
            }
        } catch (Exception e) {
            //
        }
        return uri;

    }
    public Iterable<String> getAllPHRSRequests(String replyAddress, String id,
            String careProcisionCode) throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();

        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);
        queryMap.put(Constants.HL7V3_REPLY_ADRESS, replyAddress);
        queryMap.put(Constants.HL7V3_CARE_PROVISION_CODE, careProcisionCode);
        queryMap.put(Constants.PHRS_ACTOR_PROTOCOL_ID, id);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        return result;
    }

    public Iterable<String> getAllPHRSRequests(String replyAddress) throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();

        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);
        queryMap.put(Constants.HL7V3_REPLY_ADRESS, replyAddress);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        return result;
    }

    public Iterable<String> removeAllPHRSRequests() throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();

        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        for (String resource : result) {
            triplestore.deleteForSubject(resource);
        }

        return result;
    }

    public Iterable<String> removeAllPHRSRequests(String replyAddress, String id,
            String careProcisionCode) throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();

        queryMap.put(Constants.RDFS_TYPE, Constants.PHRS_REQUEST_CLASS);
        queryMap.put(Constants.HL7V3_REPLY_ADRESS, replyAddress);
        queryMap.put(Constants.HL7V3_CARE_PROVISION_CODE, careProcisionCode);
        queryMap.put(Constants.PHRS_ACTOR_PROTOCOL_ID, id);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(queryMap);
        for (String resource : result) {
            triplestore.deleteForSubject(resource);
        }

        return result;
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

    public String getCreator() {
        return creator;
    }
}
