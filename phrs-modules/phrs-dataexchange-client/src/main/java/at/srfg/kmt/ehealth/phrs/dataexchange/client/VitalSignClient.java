/*
 * Project :iCardea
 * File : PlainClinent.java
 * Encoding : UTF-8
 * Date : Jun 27, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;


import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.persistence.api.Triple;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
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
public class VitalSignClient {

    /**
     * The unique subject for the vital signs.
     */
    private static final String SUBJECT =
            "http://www.icardea.at/phrs/instances/example/MyVitalSignEntry";

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    /**
     * 
     * @throws GenericRepositoryException
     * @throws TripleException 
     */
    VitalSignClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();
    }

    void addVitalSign(String codeURI, String note, String date,
            String value, String unitURI) throws TripleException {

        
        // TODO : add the class here
        
        triplestore.persist(SUBJECT,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.SIMPLE_OBSERVATIONS,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.VITAL_SIGNS_OBSERVATIONS,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.ASTM_HL7CONTINUALITY_OF_CARE_DOCUMENT,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.HL7V3_CODE,
                codeURI,
                RESOURCE);

        triplestore.persist(SUBJECT,
                Constants.SKOS_NOTE,
                note,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.EFFECTIVE_TIME,
                date,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.HL7V3_VALUE,
                value,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.HL7V3_UNIT,
                unitURI,
                RESOURCE);
    }

    Iterable<Triple> getVitalSigns() throws TripleException {
        final Iterable<Triple> forSubject = triplestore.getForSubject(SUBJECT);
        return forSubject;
    }
}
