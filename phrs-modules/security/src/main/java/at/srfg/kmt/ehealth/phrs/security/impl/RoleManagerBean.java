/*
 * Project :iCardea
 * File : RoleManagerBean.java
 * Date : Jan 01, 2011
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.api.RoleManager;
import at.srfg.kmt.ehealth.phrs.security.model.Fetcher;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import at.srfg.kmt.ehealth.phrs.util.Util;
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
 * <code>RoleManagerBean</code> and related information.
 *
 * @author Mihai
 */
@Stateless
@Local(RoleManager.class)
public class RoleManagerBean implements RoleManager {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.RoleManagerBean</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(RoleManagerBean.class);

    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    /**
     * The default constructor.
     */
    public RoleManagerBean() {
        // UNIMPLMENTED
    }

    /**
     * Registers a  role instance or update and existing one if
     * the underlying persistence contains already a role with
     * the same name. The method returns true if the role is
     * added and false if the role is updated.
     *
     * @param role the role to add, it can not be null.
     * @return true if the role is added, false if the role is updated.
     * @throws NullPointerException if the <code>role</code> argument is null.
     * @throws RoleException if the underlying persistence layer contains more
     * than one role with the same name. The exception transports the
     * <code>PHRRole</code> that cause the exception, this can be obtained by
     * calling the <code>getRole</code> method (from the RoleException).
     * @see RoleException
     */
    @Override
    public boolean addRole(PhrRole role) {
        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        LOGGER.debug("Tries add role [{}]", role);
        final Long id = role.getId();
        if (id == null) {
            entityManager.persist(role);
            LOGGER.debug("The Role [{}] was persisted.", role);
            return true;
        }

        final PhrRole oldRole = entityManager.find(PhrRole.class, id);
        if (oldRole == null) {
            final RoleException roleException =
                    new RoleException("Inconsistent Data Structure.");
            roleException.setRole(role);
            LOGGER.error(roleException.getMessage(), roleException);
            throw roleException;
        }

        entityManager.merge(role);

        LOGGER.debug("Role [{}] was upadted.", role);
        return false;
    }

    /**
     * Return all the registered roles. <br/>
     * <b>Note :</b> The resulted set of roles contains roles with
     * all the lazy initialized relation <b>not loaded</b>, 
     * any attempt to use/refer the role members (which are 
     * lazy initialized) will ends in to an exception.
     *
     * @return all the registered roles.
     * @see #getAllRoles(at.srfg.kmt.ehealth.phrs.security.model.Fetcher) 
     * @see FetcherFactory#ROLE_FETCHER
     */
    @Override
    public Set<PhrRole> getAllRoles() {
        return getAllRoles(null);
    }

    /**
     * Return all the registered roles. <br/>
     * <b>Note :</b> The set of roles contains roles with
     * all the lazy initialized relation <b>loaded</b> 
     * according with the specified fetcher.
     * If the fetcher is null then this method behavior is the same with the
     * <code>getAllRoles</code> method.
     *
     * @return all the registered roles.
     * @see #getAllRoles()
     * @see FetcherFactory#ROLE_FETCHER
     */
    @Override
    public final Set<PhrRole> getAllRoles(Fetcher<PhrRole> fetcher) {
        // the reason why this method is final is becuase the
        // getAllRoles is uses this method and I wnat to
        // avoid commplication by overwriting

        // FIXME : for big ammoutn of goups go pagging.
        final Query query = entityManager.createNamedQuery("selectAllRoles");
        final List resultList = query.getResultList();
        final Set<PhrRole> roles = new HashSet<PhrRole>(resultList);
        if (fetcher != null) {
            for (PhrRole role : roles) {
                fetcher.fetch(role);
            }
        }
        return roles;
    }

    @Override
    public final PhrRole getRoleForName(String name, Fetcher<PhrRole> fetcher) {
        if (name == null) {
            final NullPointerException nullException =
                    new NullPointerException("The name argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final PhrRole result = getRoleForNameExactMatch(name);

        if (fetcher != null) {
            fetcher.fetch(result);
        }

        return result;

    }

    /**
     * Returns a <code>PhrRole</code> where the name attribute exactly match
     * (case sensitive) the given <code>name</code>. If there is no matching
     * role this method returns null.
     *
     * @param name the name for the role to search, it can not be null.
     * @return a <code>PhrRole</code> where the name attribute exactly match
     * the given <code>name</code> or null for no match.
     * @throws NullPointerException if the <code>name</code> argument is null.
     */
    @Override
    public PhrRole getRoleForName(String name) {
        return getRoleForName(name, null);
    }

    /**
     * Searches a role with the given name.
     *
     * @param name the name for the role.
     * @return the role with the given name.
     * @throws NonUniqueResultException if the underlying persistence
     * later contains more roles with this name (this lead to an inconsistent
     * database model).
     * @throws NullPointerException if the <code>name</code> argument is null.
     */
    private PhrRole getRoleForNameExactMatch(String name) {

        if (name == null) {
            final NullPointerException nullException =
                    new NullPointerException("The name argument ca not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query = entityManager.createNamedQuery("selectRoleForName");
        query.setParameter("name", name);

        try {
            final PhrRole result = (PhrRole) query.getSingleResult();
            return result;
        } catch (NoResultException exception) {
            LOGGER.debug("No role with the name [{}] found.", name);
            return null;
        }
    }

    /**
     * Returns a set that contains all the PHRS roles with the name matching
     * a given JPSQ like patten.<br>
     * The namePattern is a string literal or a string-valued
     * input parameter in which an underscore (_) stands for any single
     * character, a percent (%) character stands for any sequence of
     * characters (including the empty sequence), and all other characters
     * stand for themselves.
     *
     * @param namePattern the name pattern to search, it can not be null.
     * @return a set that contains all the PHRS roles with the name matching
     * a given JPSQ like patten.
     */
    @Override
    public Set<PhrRole> getRolesForNamePattern(String namePattern) {
        return getRolesForNamePattern(namePattern, null);
    }

    @Override
    public final Set<PhrRole> getRolesForNamePattern(String namePattern,
            Fetcher<PhrRole> fetcher) {

        // the reason why this method is final is becuase the
        // removeActorsFromRole is uses this method and I wnat to
        // avoid commplication by overwriting

        if (namePattern == null) {
            final NullPointerException nullException =
                    new NullPointerException("The namePattern argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query =
                entityManager.createNamedQuery("selectRolesForNamePattern");
        final Query queryResult =
                query.setParameter("name_pattern", namePattern);
        final List resultList = queryResult.getResultList();

        final Set<PhrRole> roles = new HashSet<PhrRole>(resultList);
        if (fetcher != null) {
            for (PhrRole role : roles) {
                fetcher.fetch(role);
            }
        }

        return roles;
    }

    /**
     * Proves if the underlying persistence contains a given role.
     *
     * @param role the role which the existence is to be tested.
     * @return true if the underlying persistence contains a given
     * role, false other wise.
     */
    @Override
    public boolean roleExist(PhrRole role) {
        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The Role argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Long id = role.getId();
        if (id == null) {
            return false;
        }

        final PhrRole result = entityManager.find(PhrRole.class, id);
        return result != null;

    }

    /**
     * Removes all the registered roles. After this the \
     * <code>getAllRoles</code> method call will return an empty set.
     */
    @Override
    public void removeAllRoles() {
        final Query allRolesQuery =
                entityManager.createNamedQuery("selectAllRoles");

        final List<PhrRole> roles = allRolesQuery.getResultList();
        // mihai :
        // this may tahe while for a big number of roles.
        // I try to do it on one fire using a bulk operation but
        // the bulk delete have problems with the OneToMany.
        for (PhrRole role : roles) {
            entityManager.remove(role);
        }

        LOGGER.debug("All the PHRS roles are removed.");
    }

    /**
     * Removes a specified role. The role to remove must be registered
     * otherwise this has no effect.
     *
     * @param role the role to remove, it can not be null.
     * @return the removed role or null if the role to remove is not
     * registered.
     * @throws NullPointerException if the role argument is null.
     */
    @Override
    public PhrRole removeRole(PhrRole role) {
        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final PhrRole managedRole = entityManager.merge(role);
        final Set<PhrActor> actors = managedRole.getPhrActors();
        if (actors == null || actors.isEmpty()) {
            entityManager.remove(managedRole);
            LOGGER.debug("Role [{}] was removed.", managedRole);
            return managedRole;
        }

        // removes the role from the actor (the owner side).
        for (PhrActor actor : actors) {
            final Set<PhrRole> roles = actor.getPhrRoles();
            if (roles != null && !roles.isEmpty()) {
                roles.remove(managedRole);
            }
        }

        entityManager.remove(managedRole);
        return managedRole;
    }

    @Override
    public final void assignActorToRole(PhrActor actor, PhrRole role) {

        // the reason why this method is final is becuase the
        // assignActorsToRole is uses this method and I wnat to
        // avoid commplication by overwriting

        if (actor == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actor argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(actor, role);
        LOGGER.debug("Tries to assign actor [{}] to role [{}].", toLog);

        // assign actor to role (owner side)
        final PhrActor managedActor = entityManager.merge(actor);
        final Set<PhrRole> phrRoles = managedActor.getPhrRoles();
        final Set<PhrRole> roles = phrRoles == null
                ? new HashSet<PhrRole>()
                : phrRoles;

        final PhrRole managedRole = entityManager.merge(role);
        roles.add(managedRole);
        managedActor.setPhrRoles(roles);

        // assign role to actor (inverse side)
        final Set<PhrActor> phrActors = managedRole.getPhrActors();
        final Set<PhrActor> actors = phrActors == null
                ? new HashSet<PhrActor>()
                : phrActors;
        managedRole.setPhrActors(actors);

        LOGGER.debug("Actor [{}] was assined to role [{}].", toLog);
    }

    @Override
    public void assignActorsToRole(Set<PhrActor> actors, PhrRole role) {
        if (actors == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actors argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (actors.isEmpty()) {
            final IllegalArgumentException argumentException =
                    new IllegalArgumentException("The actors can not be an empty.");
            LOGGER.error(argumentException.getMessage(), argumentException);
            throw argumentException;
        }

        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(actors, role);
        LOGGER.debug("Tries to assign actors [{}] to role [{}].", toLog);

        for (PhrActor actor : actors) {
            assignActorToRole(actor, role);
        }

        LOGGER.debug("Actors [{}] was assined to role [{}].", toLog);
    }

    @Override
    public final void removeActorFromRole(PhrActor actor, PhrRole role) {

        // the reason why this method is final is becuase the
        // removeActorsFromRole is uses this method and I wnat to
        // avoid commplication by overwriting
        if (actor == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actor argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(actor, role);
        LOGGER.debug("Tries to remove actor [{}] from role [{}].", toLog);

        final PhrActor managedActor = entityManager.merge(actor);
        final Set<PhrRole> phrRoles = managedActor.getPhrRoles();
        if (phrRoles == null || phrRoles.isEmpty()) {
            LOGGER.debug("There is no relation between the actor [{}] and the role [{}], remove actor has no effect.", toLog);
            // I leave if there are no relations.
            return;
        }

        // removes the actor from the role (inverse side).
        final PhrRole managedRole = entityManager.merge(role);
        final Set<PhrActor> phrActors = managedRole.getPhrActors();
        phrActors.remove(managedActor);
        managedRole.setPhrActors(phrActors);

        // removes the role from the actor (owner)
        phrRoles.remove(managedRole);
        managedActor.setPhrRoles(phrRoles);

        LOGGER.debug("Actor [{}] was removed from role [{}].", toLog);
    }

    @Override
    public void removeActorsFromRole(Set<PhrActor> actors, PhrRole role) {
        if (actors == null) {
            final NullPointerException nullException =
                    new NullPointerException("The actors argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (actors.isEmpty()) {
            final IllegalArgumentException argumentException =
                    new IllegalArgumentException("The actors can not be an empty.");
            LOGGER.error(argumentException.getMessage(), argumentException);
            throw argumentException;
        }

        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(actors, role);
        LOGGER.debug("Tries to remove actors [{}] from the role [{}].", toLog);
        for (PhrActor actor : actors) {
            removeActorFromRole(actor, role);
        }
        entityManager.merge(role);
        LOGGER.debug("Actors [{}] was removed from the role [{}].", toLog);
    }
}
