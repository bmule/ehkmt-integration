/*
 * Project :iCardea
 * File : MedicinClient.java
 * Encoding : UTF-8
 * Date : Aug 31, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.client;

import static at.srfg.kmt.ehealth.phrs.persistence.api.ValueType.*;
import at.srfg.kmt.ehealth.phrs.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.util.DateUtil;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericRepositoryException;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestore;
import at.srfg.kmt.ehealth.phrs.persistence.api.GenericTriplestoreLifecycle;
import at.srfg.kmt.ehealth.phrs.persistence.api.TripleException;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.LoadRdfPostConstruct;
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
/**
 * Used to persist and retrieve medicine related information. <br/>
 * This class can not be extended. 
 * 
 * @version 0.1
 * @since 0.1
 * @author Miahi
 */
public final class MedicinClient {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.MedicinClient</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MedicinClient.class);
    /**
     * Holds the name for the creator, the instance responsible to create 
     * medication instances with this client. 
     */
    private static final String CREATORN_NAME = MedicinClient.class.getName();
    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;
    private final SchemeClient schemeClient;

    public MedicinClient() throws GenericRepositoryException, TripleException {
        triplestore = new SesameTriplestore();
        final LoadRdfPostConstruct loadRdfPostConstruct = new LoadRdfPostConstruct("startup.test.rdf");
        // I load the need instances.
        ((GenericTriplestoreLifecycle) triplestore).addToPostconstruct(loadRdfPostConstruct);
        ((GenericTriplestoreLifecycle) triplestore).init();

        schemeClient = new SchemeClient(triplestore);
    }

    /**
     * Builds a <code>MedicinClient</code> instance for a given triplestrore.
     * 
     * @param triplestore the triplestore instance, it can not be null.
     * @throws NullPointerException if the <code>triplestore</code> 
     * argument is null. 
     */
    public MedicinClient(GenericTriplestore triplestore) {

        if (triplestore == null) {
            throw new NullPointerException("The triplestore");
        }

        this.triplestore = triplestore;

        schemeClient = new SchemeClient(triplestore);
    }

    public String addMedicationSign(String user, String note, String statusURI, 
            String startDate, String endDate, String frequencyURI, 
            String adminRouteURI, String dosageURI, String drugURI) throws TripleException {

        final String subject =
                triplestore.persist(Constants.OWNER, user, LITERAL);

        // this can help to find a medication, there are alos other way 
        // to do this (e.g. using the know templateRootID, for more )
        // information about this please consult the documentation)
        triplestore.persist(subject,
                Constants.RDFS_TYPE,
                Constants.PHRS_MEDICATION_CLASS,
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
        // according with the specification the medcation requires this 
        // template root id.
        triplestore.persist(subject,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.IMUNISATION,
                LITERAL);

        // HL7 specific informations.
        // according with the specification the medcation requires this 
        // template root id.
        triplestore.persist(subject,
                Constants.HL7V3_TEMPLATE_ID_ROOT,
                Constants.MEDICATION,
                LITERAL);

        triplestore.persist(subject,
                Constants.SKOS_NOTE,
                note,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_STATUS,
                statusURI,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_DATE_START,
                startDate,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_DATE_END,
                endDate,
                LITERAL);

        triplestore.persist(subject,
                Constants.HL7V3_FREQUENCY,
                frequencyURI,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_ADMIN_ROUTE,
                adminRouteURI,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_DOSAGE,
                dosageURI,
                RESOURCE);

        triplestore.persist(subject,
                Constants.HL7V3_DRUG,
                drugURI,
                RESOURCE);

        return subject;
    }
}
