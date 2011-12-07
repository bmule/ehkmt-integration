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
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Client used to manage and manipulate name space related information. This
 * client group together three kind of information and allows the user to
 * manipulate it like one.; this group is named actor. <br/> This client can be
 * used to manage and manipulate : <ul> <li> Name Space Id - this information
 * allows grouping. <li> Protocol Id - this is the unique identifier for the
 * entire iCardea (among all subsystems). <li> PHR System Id - this is the
 * unique identifier for the PHR System. </ul> In the PHRS System every actor
 * must have the upper listed information. The relation between the three upper
 * listed properties is following : <i>the PHR System id</i> is formed from
 * <i>Name Space</i> and <i>the Protocol Id</i>.<br/> This class is not designed
 * to be extended.
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
     * <code>ActorClient</code> instance. <br/> <b>Note : </b> This constructor
     * builds its own individual connection to the triple store and don't share
     * it with the rest of the application.
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
            final NullPointerException nullPointerException =
                    new NullPointerException("The triplestore argumetn can not be null.");
            LOGGER.warn(nullPointerException.getMessage(), nullPointerException);
            throw nullPointerException;
        }

        this.triplestore = triplestore;
    }

    /**
     * Searches for a Protocol Id that corresponds to a given Name-Space and PHR
     * System id. If the underlying persistence layer does not contains any
     * relation between the specified arguments then this method returns a null.
     *
     * @param namespace the involved Name-space, it can not be null or empty
     * string.
     * @param phrId the involved PHR System id, it can not be null or empty
     * string.
     * @return the Protocol Id that corresponds to a given Name-space and PHR
     * System id or null of no relation can be found.
     * @throws TripleException by any predicate calculus related problems. In
     * most of the cases this exception chains the real cause for this
     * exception, this can be obtained by calling the
     * <code>getCause</code> method.
     * @throws NullPointerException if the
     * <code>namespace</code> or
     * <code>phrId</code> arguments are null or empty string.
     */
    public String getProtocolId(String namespace, String phrId)
            throws TripleException {

        if (namespace == null || namespace.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The namespace argument can not be null");
            LOGGER.warn(exception.getMessage(), exception);
        }

        if (phrId == null || phrId.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The phrId argument can not be null");
            LOGGER.warn(exception.getMessage(), exception);
        }

        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, namespace);
        requestMap.put(Constants.PHRS_ACTOR_ID, phrId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);
        final Iterable<String> forPredicatesAndValues =
                triplestore.getForPredicatesAndValues(requestMap);

        final Iterator<String> subjectIterator = forPredicatesAndValues.iterator();
        if (!subjectIterator.hasNext()) {
            LOGGER.debug("No actor for Name-Space [{}] and PHR System Id [{}]", namespace, phrId);
            // if there is no subject (resource) then the rest does not count. 
            return null;
        }

        final String subject = subjectIterator.next();
        final Iterable<String> forSubjectAndPredicate =
                triplestore.getForSubjectAndPredicate(subject, Constants.PHRS_ACTOR_PROTOCOL_ID);
        final Iterator<String> iterator = forSubjectAndPredicate.iterator();

        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * Searches for a Protocol Id that corresponds to a given PHR System id, the
     * Name-Space is the default one. The default Name-Space value is defined
     * here :
     * <code>Constants.PHRS_NAMESPACE</code>. If the underlying persistence
     * layer does not contains any relation between the specified arguments then
     * this method returns a null.
     *
     * @param phrId the involved PHR System id, it can not be null or empty
     * string.
     * @return the Protocol Id that corresponds to a given Name-Space and PHR
     * System id or null of no relation can be found.
     * @throws TripleException by any predicate calculus related problems. In
     * most of the cases this exception chains the real cause for this
     * exception, this can be obtained by calling the
     * <code>getCause</code> method.
     * @throws NullPointerException if the
     * <code>namespace</code> argument is null or empty string.
     * @see Constants#PHRS_NAMESPACE
     */
    public String getProtocolId(String phrId) throws TripleException {
        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, Constants.PHRS_NAMESPACE);
        requestMap.put(Constants.PHRS_ACTOR_ID, phrId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);
        final Iterable<String> forPredicatesAndValues =
                triplestore.getForPredicatesAndValues(requestMap);
        final Iterator<String> iterator = forPredicatesAndValues.iterator();

        return iterator.hasNext() ? iterator.next() : null;
    }
    
    public Set<String> getProtocolIdsInNamespace(String namespace) 
            throws TripleException {
        
        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, namespace);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);
        
        final Iterable<String> forPredicatesAndValues = 
                triplestore.getForPredicatesAndValues(requestMap);
        final Iterator<String> iterator = forPredicatesAndValues.iterator();
        
        final Set<String> result = new HashSet<String>();
        if (!iterator.hasNext()) {
            return result;
        }
        
        // TODO : use SPQL for better (faster/performater) queries, avoid multiple calls.
        for (String subject = ""; iterator.hasNext();) {
            subject = iterator.next();
            final Iterable<String> forSubjectAndPredicate =
                    triplestore.getForSubjectAndPredicate(subject, Constants.PHRS_ACTOR_PROTOCOL_ID);
            final Iterator<String> protocolIdIterator = forSubjectAndPredicate.iterator();
            updateSet(result, protocolIdIterator);
            triplestore.deleteForSubject(subject);
        }
        
        
        
        return result;
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
     * properties can not be establish from any reasons. In most of the cases
     * this exception chains the real cause for this exception, this can be
     * obtained by calling the
     * <code>getCause</code> method.
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


        triplestore.persist(subject, Constants.CREATOR, CREATOR_NAME, LITERAL);

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

        triplestore.persist(subject,
                Constants.PHRS_ACTOR_ID,
                phrId,
                LITERAL);

        return subject;
    }

    /**
     * Registers the relation between the PHRS Default Name-Space, PHRS Id and
     * Protocol Id and returns the URI for this relation. The default Name-Space
     * value is defined here :
     * <code>Constants.PHRS_NAMESPACE</code>.
     *
     * @param phrId the name space unique id. It can not be null or empty string
     * otherwise an exception raises.
     * @param protoclId the name space unique id. It can not be null or empty
     * string otherwise an exception raises.
     * @return the URI for the relation between the upper defined properties.
     * @throws TripleException if the relation between the upper listed
     * properties can not be establish from any reasons. In most of the cases
     * this exception chains the real cause for this exception, this can be
     * obtained by calling the
     * <code>getCause</code> method.
     * @throws NullPointerException if any method arguments is null or empty
     * string.
     * @see #register(java.lang.String, java.lang.String, java.lang.String)
     * @see Constants#PHRS_ACTOR_NAMESPACE
     */
    public String register(String phrId, String protoclId) throws TripleException {
        final String result =
                register(Constants.PHRS_ACTOR_NAMESPACE, phrId, protoclId);
        return result;
    }

    /**
     * Proves if a relation defined with Name-Space, PHR System id and Protocol
     * Id was already registered.
     *
     * @param namespace the Name-Space value for the relation that presence is
     * to be tested. It can not be null or an empty string.
     * @param phrId the PHR System Id value for the relation that presence is to
     * be tested. It can not be null or an empty string.
     * @param protoclId the Protocol Id value for the relation that presence is
     * to be tested. It can not be null or an empty string.
     * @return true if the a relation defined with Name-Space, PHR System id and
     * Protocol Id was already registered, false otherwise.
     * @throws TripleException if the relation between the upper listed
     * properties can not be establish from any reasons. In most of the cases
     * this exception chains the real cause for this exception, this can be
     * obtained by calling the
     * <code>getCause</code> method.
     * @throws NullPointerException if any method arguments is null or empty
     * string.
     */
    public final boolean exist(String namespace, String phrId, String protoclId)
            throws TripleException {

        if (namespace == null || namespace.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The namespace argument can not ne null.");
            LOGGER.warn(exception.getMessage(), exception);
        }

        if (phrId == null || phrId.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The phrId argument can not ne null.");
            LOGGER.warn(exception.getMessage(), exception);
        }

        if (protoclId == null || protoclId.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The protoclId argument can not ne null.");
            LOGGER.warn(exception.getMessage(), exception);
        }

        // the reason why this method is final is because  the other register
        // methods are basing on this method. Overriding may have
        // very unpleasant side effects.
        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, namespace);
        requestMap.put(Constants.PHRS_ACTOR_ID, phrId);
        requestMap.put(Constants.PHRS_ACTOR_PROTOCOL_ID, protoclId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);

        final Iterable<String> result =
                triplestore.getForPredicatesAndValues(requestMap);

        // TODO : this check is not preformat enought you also must prove 
        // the content. 
        final boolean hasNext = result.iterator().hasNext();
        return hasNext;
    }

    /**
     * Proves if a relation defined with Name-Space and PHR System id was
     * already registered; this methods returns true for any possible Protocol
     * Id value.
     *
     * @param namespace the Name-Space value for the relation that presence is
     * to be tested. It can not be null or an empty string.
     * @param phrId the PHR System Id value for the relation that presence is to
     * be tested. It can not be null or an empty string.
     * @return true if the a relation defined with Name-Space, PHR System id and
     * Protocol Id was already registered, false otherwise.
     * @throws TripleException if the relation between the upper listed
     * properties can not be establish from any reasons. In most of the cases
     * this exception chains the real cause for this exception, this can be
     * obtained by calling the
     * <code>getCause</code> method.
     * @throws NullPointerException if any method arguments is null or empty
     * string.
     * @see #exist
     */
    public boolean protocolIdExist(String namespace, String phrId)
            throws TripleException {


        if (phrId == null || phrId.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The phrId argument can not ne null.");
            LOGGER.warn(exception.getMessage(), exception);
        }

        // the reason why this method is final is because  the other register
        // methods are basing on this method. Overriding may have
        // very unpleasant side effects.

        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, namespace);
        requestMap.put(Constants.PHRS_ACTOR_ID, phrId);
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
     * @param protocolId the Protocol that presence is to be tested. It can not
     * be null or an empty string.
     * @return true if the underlying system contains at least one actor
     * (relation between name-space, PHRS Id and Protocol Id) with the given
     * protocol id.
     * @throws TripleException by any predicate calculus related errors. In most
     * of the cases this exception chains the real cause for this exception,
     * this can be obtained by calling the
     * <code>getCause</code> method.
     * @throws NullPointerException if the protocolId argument is null or empty
     * string.
     * @see #exist
     */
    public boolean protocolIdExist(String protocolId) throws TripleException {

        if (protocolId == null || protocolId.isEmpty()) {
            throw new NullPointerException("The protocolId can not be null.");
        }

        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);
        requestMap.put(Constants.PHRS_ACTOR_PROTOCOL_ID, protocolId);

        // TODO : this check is not preformat enought you also must prove 
        // the content. 
        final Iterable<String> results =
                triplestore.getForPredicatesAndValues(requestMap);

        final boolean result = results.iterator().hasNext();
        return result;
    }

    /**
     * Removes all the actors (relations) that involves a given Name-Space and
     * PHR System Id and returns the Protocol Id for the deleted actors. If
     * there are no actors (relations) for the specified Name-Space and PHR
     * System Id then this method returns an empty set.
     *
     * @param namespace the Name-Space for the actor to be deleted. It can not
     * be null or empty string.
     * @param phrId the PHR System Id for the actor to be deleted. It can not be
     * null or empty string.
     * @return all the Protocol Id for the deleted actors (relations) that
     * involves a given Name-Space and PHR System Id.
     * @throws TripleException if the relation between the upper listed
     * properties can not be establish from any reasons. In most of the cases
     * this exception chains the real cause for this exception, this can be
     * obtained by calling the
     * <code>getCause</code> method.
     * @throws NullPointerException if any method arguments is null or empty
     * string.
     */
    public Set<String> removeProtocolIds(String namespace, String phrId)
            throws TripleException {

        if (namespace == null || namespace.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The namespace argument can not ne null.");
            LOGGER.warn(exception.getMessage(), exception);
        }

        if (phrId == null || phrId.isEmpty()) {
            final NullPointerException exception =
                    new NullPointerException("The phrId argument can not ne null.");
            LOGGER.warn(exception.getMessage(), exception);
        }

        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_NAMESPACE, namespace);
        requestMap.put(Constants.PHRS_ACTOR_ID, phrId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);
        final Iterable<String> forPredicatesAndValues =
                triplestore.getForPredicatesAndValues(requestMap);
        final Iterator<String> iterator = forPredicatesAndValues.iterator();
        final Set<String> result = new HashSet<String>();
        if (!iterator.hasNext()) {
            return result;
        }

        // TODO : use SPQL for better (faster/performater) queries, avoid multiple calls.
        for (String subject = ""; iterator.hasNext();) {
            subject = iterator.next();
            final Iterable<String> forSubjectAndPredicate =
                    triplestore.getForSubjectAndPredicate(subject, Constants.PHRS_ACTOR_PROTOCOL_ID);
            final Iterator<String> protocolIdIterator =
                    forSubjectAndPredicate.iterator();
            updateSet(result, protocolIdIterator);
            triplestore.deleteForSubject(subject);
        }

        return result;
    }

    private Set<String> updateSet(Set<String> set, Iterator<String> iterator) {
        if (!iterator.hasNext()) {
            return set;
        }
        for (String s = ""; iterator.hasNext();) {
            s = iterator.next();
            set.add(s);
        }
        return set;
    }

    /**
     * Removes all the actors (relations) that involves a given PHR System Id
     * and returns the Protocol Id for the deleted actors. If there are no
     * actors (relations) for the specified PHR System Id then this method
     * returns an empty set.<br/> <b>Note : </b> This methods removes all the
     * clients from all name-spaces.
     *
     *
     * @param phrId the PHR System Id for the actor to be deleted. It can not be
     * null or empty string.
     * @return all the Protocol Id for the deleted actors (relations) that
     * involves a given Name-Space and PHR System Id.
     * @throws TripleException if the relation between the upper listed
     * properties can not be establish from any reasons. In most of the cases
     * this exception chains the real cause for this exception, this can be
     * obtained by calling the
     * <code>getCause</code> method.
     * @throws NullPointerException if any method arguments is null or empty
     * string.
     */
    public Set<String> removeProtocolIds(String phrId) throws TripleException {
        final Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put(Constants.PHRS_ACTOR_ID, phrId);
        requestMap.put(Constants.RDFS_TYPE, Constants.PHRS_ACTOR_CLASS);

        final Iterable<String> forPredicatesAndValues =
                triplestore.getForPredicatesAndValues(requestMap);
        final Iterator<String> iterator = forPredicatesAndValues.iterator();
        final Set<String> result = new HashSet<String>();
        if (!iterator.hasNext()) {
            return result;
        }

        // TODO : use SPQL for better (faster/performater) queries, avoid multiple calls.
        for (String subject = ""; iterator.hasNext();) {
            subject = iterator.next();
            final Iterable<String> forSubjectAndPredicate =
                    triplestore.getForSubjectAndPredicate(subject, Constants.PHRS_ACTOR_PROTOCOL_ID);
            final Iterator<String> protocolIdIterator = forSubjectAndPredicate.iterator();
            updateSet(result, protocolIdIterator);
            triplestore.deleteForSubject(subject);
        }

        return result;
    }
}
