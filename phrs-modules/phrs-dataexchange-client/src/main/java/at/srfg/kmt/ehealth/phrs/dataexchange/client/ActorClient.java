/*
 * Project :iCardea
 * File : ActorClient.java
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
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client used to manage and manipulate name space related information. <br/>
 * This client can be used to manage and manipulate : 
 * <ul> 
 * <li> name space id
 * <li> PHR System id
 * <li> Protocol id
 * </ul> 
 * In the PHRS system every actor must have the upper listed information. 
 * The relation between the three upper listed properties is following :
 * <i>the PHR System id</i> is formed from <i>Name Space</i> and
 * <i>the Protocol Id</i>.<br/>
 * This class is not designed to be extended.
 *
 * @version 0.1
 * @since 0.1
 * @author m1s
 */
public final class ActorClient {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.dataexchange.client.ActorClient</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ActorClient.class);

    /**
     * Holds the name for the creator, the instance responsible to create
     * problem entry instances with this client.
     */
    private static final String CREATOR_NAME = ActorClient.class.getName();

    /**
     * Used to persist/retrieve informations from the persistence layer.
     */
    private final GenericTriplestore triplestore;

    /**
     * Builds a
     * <code>ActorClient</code> instance. <br/> <b>Note : </b> This
     * constructor builds its own individual connection to the triple store and
     * don't share it with the rest of the application.
     *
     * @throws GenericRepositoryException if the underlying persistence layer
     * can not be initialized from any reasons.
     * @throws TripleException if the connection with the triplestore can not be
     * establish from any reasons.
     */
    public ActorClient() throws GenericRepositoryException {
        triplestore = new SesameTriplestore();
        ((GenericTriplestoreLifecycle) triplestore).init();
    }

    /**
     * Builds a
     * <code>ActorClient</code> instance for a given triplestrore.
     *
     * @param triplestore the triplestore instance, it can not be null.
     * @throws NullPointerException if the
     * <code>triplestore</code> argument is null.
     */
    public ActorClient(GenericTriplestore triplestore) {
        if (triplestore == null) {
            throw new NullPointerException("The triplestore argumetn can not be null.");
        }

        this.triplestore = triplestore;
    }

    public String getProtocolId(String namespace, String phrId)
            throws TripleException {
        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, namespace);
        requestMap.put(Constants.OWNER, phrId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);
        final Iterable<String> forPredicatesAndValues =
                triplestore.getForPredicatesAndValues(requestMap);

        final String subject = forPredicatesAndValues.iterator().next();
        final Iterable<String> forSubjectAndPredicate =
                triplestore.getForSubjectAndPredicate(subject, Constants.PHRS_ACTOR_PROTOCOL_ID);
        return forSubjectAndPredicate.iterator().next();
    }

    public String getProtocolId(String phrId) throws TripleException {
        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, Constants.PHRS_NAMESPACE);
        requestMap.put(Constants.OWNER, phrId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);
        final Iterable<String> forPredicatesAndValues =
                triplestore.getForPredicatesAndValues(requestMap);

        return null;
    }

    /**
     * Creates a the relation between the namespace, PHRS Id and Protocol id and
     * returns the URI for this relation.
     *
     * @param namespace the name space unique id. It can not be null or empty
     * string otherwise an exception raises.
     * @param phrId the name space unique id. It can not be null or empty string
     * otherwise an exception raises.
     * @param protoclId the name space unique id. It can not be null or empty
     * string otherwise an exception raises.
     * @return the URI for the relation between the upper defined properties.
     * @throws TripleException if the relation between the upper listed
     * properties can not be establish from any reasons.
     * @throws NullPointerException if any method arguments is null or empty
     * string.
     * @see #register(java.lang.String, java.lang.String)
     */
    public final String register(String namespace, String phrId, String protoclId)
            throws TripleException {

        // the reason why this method is final is because  the other register
        // methods are basing on this method. Overriding may have
        // very unpleasant side effects.
        if (namespace == null || namespace.isEmpty()) {
            throw new NullPointerException("The namespace argumetn can not be null.");
        }

        if (phrId == null || phrId.isEmpty()) {
            throw new NullPointerException("The phrId argumetn can not be null.");
        }

        if (protoclId == null || protoclId.isEmpty()) {
            throw new NullPointerException("The protoclId argumetn can not be null.");
        }

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

        return subject;
    }

    /**
     * Registers the relation between a given PHRS Actor Namespace, PHRS Id and
     * Protocol Id and returns the URI for this relation. The PHRS Actor
     * Namespace is a constant.
     *
     * @param phrId
     * @param protoclId
     * @return
     * @throws TripleException
     * @see #register(java.lang.String, java.lang.String, java.lang.String)
     * @see Constants#PHRS_ACTOR_NAMESPACE
     */
    public String register(String phrId, String protoclId) throws TripleException {
        final String result =
                register(Constants.PHRS_ACTOR_NAMESPACE, phrId, protoclId);
        return result;
    }

    public final boolean exist(String namespace, String phrId, String protoclId)
            throws TripleException {

        // the reason why this method is final is because  the other register
        // methods are basing on this method. Overriding may have
        // very unpleasant side effects.

        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, namespace);
        requestMap.put(Constants.OWNER, phrId);
        requestMap.put(Constants.PHRS_ACTOR_PROTOCOL_ID, protoclId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(requestMap);

        // TODO : this check is not preformat enought you also must prove 
        // the content. 
        final boolean hasNext = result.iterator().hasNext();
        return hasNext;
    }

    public boolean protocolIdExist(String namespace, String phrId)
            throws TripleException {
        // the reason why this method is final is because  the other register
        // methods are basing on this method. Overriding may have
        // very unpleasant side effects.

        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, namespace);
        requestMap.put(Constants.OWNER, phrId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);

        // TODO : this check is not preformat enought you also must prove 
        // the content. 
        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(requestMap);

        final boolean hasNext = result.iterator().hasNext();
        return hasNext;
    }

    /**
     * Proves if there is at least one actor (relation between name-space, PHRS
     * Id and Protocol Id) with the given protocol id.
     *
     * @param protocolId the Protocol that presence is to be tested.
     * It can not be null or an empty string. 
     * @return true if the underlying system contains at least one actor
     * (relation between name-space, PHRS Id and Protocol Id) with the given
     * protocol id.
     *
     * @throws TripleException by any errors related with the underlying
     * persistence layer. In most of the cases this exception contains (chains)
     * the real cause for the exception.
     */
    public boolean protocolIdExist(String protocolId) throws TripleException {
        
        if (protocolId == null || protocolId.isEmpty()) {
            throw new NullPointerException("The protocolId can not be null.");
        }
        
        final boolean result =
                triplestore.exists(Constants.PHRS_ACTOR_PROTOCOL_ID, protocolId);
        return result;
    }

    String removeProtoclId(String namespace, String phrId) {
        return null;
    }

    String removeProtoclId(String phrId) {
        return null;
    }
}
