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
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(new LoadRdfPostConstruct());
    }

    void addVitalSign(String codeURI, String note, String date,
            String value, String unitURI) throws TripleException {


        triplestore.persist(SUBJECT,
                Constants.ICARDEA_HL7V3_TEMPLATE_ID_ROOT,
                "1.3.6.1.4.1.19376.1.5.3.1.4.13",
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.ICARDEA_HL7V3_TEMPLATE_ID_ROOT,
                "1.3.6.1.4.1.19376.1.5.3.1.4.13.2",
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.ICARDEA_HL7V3_TEMPLATE_ID_ROOT,
                "2.16.840.1.113883.10.20.1.31",
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.ICARDEA_HL7V3_CODE,
                codeURI,
                RESOURCE);

        triplestore.persist(SUBJECT,
                Constants.SKOS_NOTE,
                note,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.ICARDEA_HL7V3_EFFECTIVE_TIME,
                date,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.ICARDEA_HL7V3_VALUE,
                value,
                LITERAL);

        triplestore.persist(SUBJECT,
                Constants.ICARDEA_HL7V3_UNIT,
                unitURI,
                RESOURCE);
    }

    Iterable<Triple> getVitalSigns() throws TripleException {
        final Iterable<Triple> forSubject = triplestore.getForSubject(SUBJECT);
        return forSubject;
    }
}
