/*
 * Project :iCardea
 * File : PlainClinent.java
 * Encoding : UTF-8
 * Date : Jun 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import java.util.Date;
import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;
import static at.srfg.kmt.ehealth.phrs.Constants.*;
import java.util.HashMap;
import java.util.Map;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.api.ValueType;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to persist and retrieve vital signs information. <br/> This class can
 * not be extended.
 *
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public final class VitalSignClient {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.security.impl.QUPCAR004030UVServiceUtil</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(VitalSignClient.class);

    /**
     * Holds the name for the creator, the instance responsible to create vital
     * signs instances with this client.
     */
    private static final String CREATORN_NAME = VitalSignClient.class.getName();

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
     * <code>VitalSignClient</code> instance. <br/> <b>Note : </b> This
     * constructor builds its own individual connection (and it does not) share
     * it with the rest of the applications.
     *
     * @throws GenericRepositoryException if the underlying persistence layer
     * can not be initialized from any reasons.
     * @throws TripleException
     */
    public VitalSignClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();

        schemeClient = new SchemeClient(triplestore);
        creator = VitalSignClient.class.getName();
    }

    /**
     * Builds a
     * <code>VitalSignClient</code> instance for a given triplestrore.
     *
     * @param triplestore the triplestore instance, it can not be null.
     * @throws NullPointerException if the
     * <code>triplestore</code> argument is null.
     */
    public VitalSignClient(GenericTriplestore triplestore) {

        if (triplestore == null) {
            throw new NullPointerException("The triplestore");
        }

        this.triplestore = triplestore;

        schemeClient = new SchemeClient(triplestore);
        creator = VitalSignClient.class.getName();
    }

    /**
     * Adds a vital sign resource and return the URI for this resource. <br/>
     * This method generates the following triples, this triples are pointing
     * for the properties : <ol> <li> owner <li> create date <li> update date
     * (this is add only if the resource is updated) <li> creator <li> owner
     * <li> rdf type <li> create date <li> creator <li> template root id1 <li>
     * template root id2 <li> template root id3 <li> HL7 V3 code <li> SKOS note
     * <li> effective time <li> unit <li> value for the unit </ol>
     *
     * @param user
     * @param codeURI
     * @param note
     * @param date
     * @param value
     * @param unitURI
     * @return the URI for the new added vital sign (resource).
     * @throws TripleException
     */
    public String addVitalSign(String user, String codeURI, String note,
            String date, String statusURI,
            String value, String unitURI) throws TripleException {

        final String subject =
                triplestore.persist(Constants.OWNER, user, LITERAL);

        // this can help to find a vial sign, there are alos other way 
        // to do this (e.g. using the know templateRootID, for more )
        // information about this please consult the documentation)
        triplestore.persist(subject,
                Constants.RDFS_TYPE,
                Constants.PHRS_VITAL_SIGN_CLASS,
                RESOURCE);


        // generic informarion (beside the 'OWNER' they are not really relevant 
        // for the HL7 V3 message)
        triplestore.persist(subject,
                Constants.CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);

        // I preffer to hang a specific name for the Creator only for test 
        // purposes. In this way I can follow the the origin for a certain 
        // resource. 
        triplestore.persist(subject,
                Constants.CREATOR,
                CREATORN_NAME,
                LITERAL);

        // HL7 specific informations.
        triplestore.persist(subject,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.SIMPLE_OBSERVATIONS,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.VITAL_SIGNS_OBSERVATIONS,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.ASTM_HL7CONTINUALITY_OF_CARE_DOCUMENT,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_CODE,
                codeURI,
                RESOURCE);

        triplestore.persist(subject,
                HL7V3_STATUS,
                statusURI,
                RESOURCE);


        triplestore.persist(subject,
                Constants.SKOS_NOTE,
                note,
                LITERAL);

        triplestore.persist(subject,
                Constants.EFFECTIVE_TIME,
                date,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_VALUE,
                value,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_UNIT,
                unitURI,
                RESOURCE);

        return subject;
    }

    /**
     * Returns all the vital sings for all the users.
     *
     * @return all the vital sings for all the users.
     * @throws TripleException by any kind of triplestore related error.
     */
    public Iterable<Triple> getVitalSignsTriples() throws TripleException {
        final Iterable<String> resources =
                triplestore.getForPredicateAndValue(RDFS_TYPE, PHRS_VITAL_SIGN_CLASS, RESOURCE);

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
    public Iterable<Triple> getVitalSignsTriplesForUser(String userId) throws TripleException {

        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(RDFS_TYPE, PHRS_VITAL_SIGN_CLASS);
        queryMap.put(OWNER, userId);

        // here I search for all resources with 
        // rdf type == vital sign 
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

    public Iterable<String> getVitalSignURIsForUser(String userId)
            throws TripleException {
        final Map<String, String> queryMap = new HashMap<String, String>();
        // like this I indetify the type
        queryMap.put(RDFS_TYPE, PHRS_VITAL_SIGN_CLASS);
        queryMap.put(OWNER, userId);

        // here I search for all resources with 
        // rdf type == vital sign 
        // and
        // owner == user id
        final Iterable<String> resources =
                triplestore.getForPredicatesAndValues(queryMap);

        return resources;
    }

    public void updateVitalSign(String resourceURI, String predicate, String newValue)
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
        // the needs for the Vital Sign
        final boolean isPropertyLiteral = schemeClient.isPropertyLiteral(predicate);
        final ValueType type = isPropertyLiteral
                ? ValueType.LITERAL
                : ValueType.RESOURCE;

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

    public void deleteVitalSign(String resourceURI) throws TripleException {

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

    public String getCreator() {
        return creator;
    }

    public void setDispathedTo(String resource, String dispatchToID)
            throws TripleException {
        if (dispatchToID == null) {
            final NullPointerException exception =
                    new NullPointerException("The dispatchToID argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        triplestore.persist(resource,
                Constants.DISTPATCH_TO,
                dispatchToID,
                LITERAL);
    }

    public String getCodeURI(String code, String dispalyName, String codeSystemURI) {
        return null;
    }

    public String getCodeURI(String code, String dispalyName, String codeSystemName, String codeSystemCode) {
        return null;
    }
}
