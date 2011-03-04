/*
 * Project :iCardea
 * File : ActorManagerBean.java
 * Date : Dec 15, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.api.ActorManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stateless bean local scoped used to manage and manipulate
 * <code>PhrActor</code> and related information. <br>
 * FIXME : remove this - it is not used.
 *
 * @author Mihai
 */
@Stateless
@Local(ActorManager.class)
public class ActorManagerBean implements ActorManager {

    private static final String ANONYMUS_NAME = "Anonymus";

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.ActorManagerBean</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(ActorManagerBean.class);

    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    /**
     * Registers an <code>PhrActor</code> instance or update and existing
     * one if the underlying persistence contains already a actor with
     * the same name. The method returns true if the actor is
     * added and false if the actor is updated.
     *
     * @param actor the actor to add, it can not be null.
     * @return true if the actor is added, false if the actor is updated.
     * @throws NullPointerException if the <code>actor</code> argument is null.
     */
    @Override
    public final boolean addActor(PhrActor actor) {
        // the reason why this method is final is becuase the
        // addActors is uses this method and I wnat to
        // avoid commplication by overwriting

        if (actor == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actor argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        logger.debug("Tries to add actor [{}]", actor);
        final Long id = actor.getId();
        if (id == null) {
            logger.debug("Actor [{}] was addd to the PHRS system.", actor);
            entityManager.persist(actor);
            return true;
        }

        entityManager.merge(actor);
        logger.debug("Actor [{}] was upadted.", actor);
        return false;
    }

    /**
     * Registers more actors.
     * 
     * @param actors the actors to add, it can not be null or empty set
     * otherwise an exception raises.
     * @throws NullPointerException if the actors argument is null.
     * @throws IllegalArgumentException if the actors argument is an empty set.
     */
    @Override
    public void addActors(Set<PhrActor> actors) {
        if (actors == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actors argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (actors.isEmpty()) {
            final IllegalArgumentException argumentException =
                    new IllegalArgumentException("The actors can not be an empty exception.");
            logger.error(argumentException.getMessage(), argumentException);
            throw argumentException;
        }

        for (PhrActor actor : actors) {
            addActor(actor);
        }
    }

    /**
     * Proves if the underlying persistence contains a given actor.
     *
     * @param actor the actor which the existence is to be tested.
     * @return true if the underlying persistence contains a given
     * actor, false other wise.
     * @throws NullPointerException if the actor argument is null.
     */
    @Override
    public boolean actorExist(PhrActor actor) {
        if (actor == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actor argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Long id = actor.getId();
        if (id == null) {
            return false;
        }

        final PhrActor result = entityManager.find(PhrActor.class, id);

        return result != null;
    }

    /**
     * Proves if there is any registered actors.
     * 
     * @return true if there is any registered actors.
     */
    @Override
    public boolean areAnyActorsRegistered() {
        final Query query = entityManager.createNamedQuery("countAllActors");
        final Long actorsCount = (Long) query.getSingleResult();

        return actorsCount.intValue() > 0;
    }

    /**
     * Returns all the actors where the name match exactly a certain name.
     * If there are no matchers for the given name then this method will
     * return null.
     * 
     * @param name the name to match.
     * @return all the actors where the name match exactly a certain name.
     * @throws NullPointerException if the name argument is null.
     */
    @Override
    public Set<PhrActor> getActorsForName(String name) {

        if (name == null) {
            final NullPointerException nullException =
                    new NullPointerException("The name argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }


        final Query query = entityManager.createNamedQuery("selectActorsByName");
        query.setParameter("name", name);

        final List<PhrActor> actors = query.getResultList();
        final HashSet<PhrActor> results = new HashSet<PhrActor>();
        results.addAll(actors);

        return results;
    }

    /**
     * Returns a set that contains all the PHRS actors with the name matching
     * a given JPSQ like patten.<br>
     * The namePattern is a string literal or a string-valued
     * input parameter in which an underscore (_) stands for any single
     * character, a percent (%) character stands for any sequence of
     * characters (including the empty sequence), and all other characters
     * stand for themselves.
     *
     * @param namePattern the name pattern to search, it can not be null.
     * @return a set that contains all the PHRS actors with the name matching
     * a given JPSQ like patten.
     * @throws NullPointerException if the name argument is null.
     */
    @Override
    public Set<PhrActor> getActorsForNamePattern(String namePattern) {

        if (namePattern == null) {
            final NullPointerException nullException =
                    new NullPointerException("The namePattern argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query = entityManager.createNamedQuery("selectActorsByNamePattern");
        query.setParameter("name_pattern", namePattern);

        final List<PhrActor> actors = query.getResultList();
        final HashSet<PhrActor> results = new HashSet<PhrActor>();
        results.addAll(actors);

        return results;
    }

    /**
     * Returns all the registered actors packed in to a set.
     * For a big amount of actors this method may take a while.
     * 
     * @return all the registered actors packed in to a set.
     */
    @Override
    public Set<PhrActor> getAllActors() {
        final Query query = entityManager.createNamedQuery("selectAllActors");

        final List<PhrActor> actors = query.getResultList();
        final HashSet<PhrActor> results = new HashSet<PhrActor>();
        results.addAll(actors);

        return results;
    }

    /**
     * Returns the anonymus actor instance. 
     * There is only one anonymus actor instance, repetative call for this method
     * will produce the same instance.
     * 
     * @return the anonymus actor. 
     */
    @Override
    public PhrActor getAnonymusActor() {
        final Query anonymusQuery =
                entityManager.createNamedQuery("selectAnonymousIndividual");
        PhrActor result;
        try {
            result = (PhrActor) anonymusQuery.getSingleResult();
        } catch (NoResultException noResultException) {
            result = new PhrActor(ANONYMUS_NAME);
            entityManager.persist(result);
        } catch (NonUniqueResultException nonUniqueResultException) {
            final ActorException actorException =
                    new ActorException("More Anonymus actor deteceted, data is incosistent.");
            throw actorException;
        }

        return result;
    }

    /**
     * Unregisters a given actor, after this method call the 
     * <code>actorExist</code> for the removed actor will cause return false. 
     * If the actor was not registered (in a previous action) 
     * then this method has no effect.
     * 
     * @param actor the actor to remove, it can not be null.
     * @return the removed actor.
     * @throws NullPointerException if the actor argument is null.
     * @see #removeActors(java.util.Set) 
     * @see #actorExist(at.srfg.kmt.ehealth.phrs.security.model.PhrActor) 
     */
    @Override
    public PhrActor removeActor(PhrActor actor) {
        if (actor == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actor argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Long id = actor.getId();
        if (id == null) {
            return null;
        }

        final PhrActor mergeResult = entityManager.merge(actor);
        entityManager.remove(mergeResult);

        return mergeResult;
    }

    /**
     * Removes all the actors from the given set. If the actor set contains
     * unregistered actors then they will be ignored.
     * 
     * @param actors the actors to remove, it can not be null or empty set.
     * @throws NullPointerException if the actors argument is null.
     * @throws IllegalArgumentException if the actors argument is an empty set.
     */
    @Override
    public void removeActors(Set<PhrActor> actors) {
        if (actors == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actors argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (actors.isEmpty()) {
            final IllegalArgumentException illegalException =
                    new IllegalArgumentException("The actors set can not be empty can not be null.");
            logger.error(illegalException.getMessage(), illegalException);
            throw illegalException;
        }

        logger.debug("Tries to removes actors [{}].", actors);
        for (PhrActor actor : actors) {
            removeActor(actor);
        }
        logger.debug("Actors [{}] was removed,", actors);

    }

    /**
     * Removes all the registered actors after this method call the 
     * <code>areAnyActorsRegistered</code> for the removed actor will cause 
     * return false. 
     */
    @Override
    public void removeAllActors() {
        // FIXME : this is not the most efficent way to remove thigs, it can
        // also be done in a singular bulk operation. 
        final Query allActorsQuery =
                entityManager.createNamedQuery("selectAllActors");
        final List<PhrActor> allActors = allActorsQuery.getResultList();

        for (PhrActor actor : allActors) {
            entityManager.remove(actor);
        }
    }
}
