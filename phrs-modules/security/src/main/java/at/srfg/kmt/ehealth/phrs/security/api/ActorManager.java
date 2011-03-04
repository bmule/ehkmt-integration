

package at.srfg.kmt.ehealth.phrs.security.api;


import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import java.util.Set;

/**
 * Defines the basic operations that can be accomplished with/on phrs actors.
 *
 * @author Mihai
 */
public interface ActorManager {

    /**
     * Register a given actor.
     * 
     * @param actual the actor to register.
     * @return true if the operation was successfully.
     */
    boolean addActor(PhrActor actor);

    /**
     * Registers more actors.
     * 
     * @param actors the actors to register.
     */
    void addActors(Set<PhrActor> actors);

    /**
     * Proves if a given actor is registered or not.
     * 
     * @param actor the actor which existence is to be proved.
     * @return true if the given actor is registered.
     */
    boolean actorExist(PhrActor actor);

    /**
     * Remove/ unregisters a given actor. It is not defined here what must
     * happen if the actor is not registered this will be defined in the
     * implementation.
     * 
     * @param actor the actor to be removed.
     * @return the removed actor.
     */
    PhrActor removeActor(PhrActor actor);

    /**
     * Removes/unregisters a given set of actors.
     * 
     * @param actors the actors to remove.
     */
    void removeActors(Set<PhrActor> actors);

    /**
     * Returns a set of <code>PhrActor</code> for a given name, 
     * the group name attribute match exactly the specified name.
     * 
     * @param name the group name.
     * @return a PHRS actor with the name name that match exactly the
     * specified name.
     */
    Set<PhrActor> getActorsForName(String name);

    /**
     * Returns a set of <code>PhrActor</code> where the group name match
     * a certain pattern. The pattern syntax is defined in the implementation.
     *
     * @param namePattern the name pattern.
     * @return a set of <code>PhrActor</code> where the actor name match
     * a certain pattern
     */
    Set<PhrActor> getActorsForNamePattern(String namePattern);

    /**
     * Returns all registered actors.
     * 
     * @return all the registered actors.
     */
    Set<PhrActor> getAllActors();

    /**
     * Proves if any actual is registered.
     * 
     * @return true if there is at least one actual registered.
     */
    boolean areAnyActorsRegistered();

    /**
     * Removes/unregisters all the registered users, after this the
     * <code>areAnyActorsRegistered</code> will return false.
     * 
     */
    void removeAllActors();

    PhrActor getAnonymusActor();
}
