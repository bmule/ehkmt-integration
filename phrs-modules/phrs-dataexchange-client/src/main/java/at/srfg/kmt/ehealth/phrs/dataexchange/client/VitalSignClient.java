/*
 * Project :iCardea
 * File : PlainClinent.java
 * Encoding : UTF-8
 * Date : Jun 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;
import static at.srfg.kmt.ehealth.phrs.Constants.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.util.MultiIterable;


/**
 * Used to persist and retrieve vital signs information. 
 * 
 * @version 0.1
 * @since 0.1
 * @author mradules
 */
public class VitalSignClient {

    private static final String CREATORN_NAME = "VitalSignPlainClient";

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    /**
     * Builds a <code>VitalSignClient</code> instance. <br/>
     * <b>Note : </b> This constructor builds its own individual connection 
     * (and don't) share it with the rest of the applications.
     * 
     * @throws GenericRepositoryException if the underlying persistence layer 
     * can not be initialized from any reasons.
     * @throws TripleException 
     */
    VitalSignClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();
    }

    VitalSignClient(GenericTriplestore triplestore) throws GenericRepositoryException, TripleException {
        this.triplestore = triplestore;
    }

    /**
     * Adds a vital sign resource and return the URI for this resource.
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
    String addVitalSign(String user, String codeURI, String note, String date,
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
                "201006010000",
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

    Iterable<Triple> getVitalSigns() throws TripleException {
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
    Iterable<Triple> getVitalSignsForUser(String userId) throws TripleException {

        final Iterable<String> resources =
                triplestore.getForPredicateAndValue(Constants.OWNER, userId, LITERAL);

        final MultiIterable result = new MultiIterable();

        for (String resource : resources) {
            final Iterable<Triple> forSubject = triplestore.getForSubject(resource);
            result.addIterable(forSubject);
        }

        return result;
    }

    /**
     * Clean all the changes from all previous sessions 
     * (done with all the clients).
     * 
     * @throws GenericRepositoryException by any kind of exception related with 
     * the underlying triplestore.
     */
    void cleanEnviroment() throws GenericRepositoryException {
        ((GenericTriplestoreLifecycle) triplestore).shutdown();
        ((GenericTriplestoreLifecycle) triplestore).cleanEnvironment();
    }
}
