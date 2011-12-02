/*
 * Project :iCardea
 * File : NameSpaceClient.java
 * Encoding : UTF-8
 * Date : Dec 2, 2011
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
import at.srfg.kmt.ehealth.phrs.persistence.impl.sesame.SesameTriplestore;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 0.1
 * @since 0.1
 * @author m1s
 */
public final class NameSpaceClient {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.ProblemEntryClient</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProblemEntryClient.class);
    /**
     * Holds the name for the creator, the instance responsible to create
     * problem entry instances with this client.
     */
    private static final String CREATOR_NAME = NameSpaceClient.class.getName();
    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    public NameSpaceClient() throws GenericRepositoryException {
        triplestore = new SesameTriplestore();
        ((GenericTriplestoreLifecycle) triplestore).init();
    }

    public String getProtocolId(String namespace, String phrId) {
        return null;
    }

    public String getProtocolId(String phrId) {
        return null;
    }

    public void register(String namespace, String phrId, String protoclId) throws TripleException {

        final String subject =
                triplestore.persist(Constants.OWNER, phrId, LITERAL);

        triplestore.persist(subject,
                Constants.CREATOR,
                CREATOR_NAME,
                LITERAL);
        
         // this can help to find a phrs actor in an easy way
        triplestore.persist(subject,
                Constants.RDFS_TYPE,
                Constants.PHRS_ACTOR_CLASS,
                RESOURCE);

        triplestore.persist(subject,
                Constants.CREATE_DATE,
                DateUtil.getFormatedDate(new Date()),
                LITERAL);
        
        triplestore.persist(subject,
                Constants.PHRS_ACTOR_NAMESPACE,
                namespace,
                LITERAL);
        
        triplestore.persist(subject,
                Constants.PHRS_ACTOR_PROTOCOL_ID,
                protoclId,
                LITERAL);
    }

    void register(String phrId, String protoclId) {
    }

    boolean exist(String namespace, String phrId, String protoclId) {
        return false;
    }

    boolean protoclIdExist(String namespace, String phrId) {
        return false;
    }

    boolean protoclIdExist(String phrId) {
        return false;
    }

    String removeProtoclId(String namespace, String phrId) {
        return null;
    }

    String removeProtoclId(String phrId) {
        return null;
    }
}
