/*
 * Project :iCardea
 * File : RoleManagerBean.java
 * Date : Jan 01, 2011
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.api.RoleManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
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
    private static final Logger logger =
            LoggerFactory.getLogger(RoleManagerBean.class);

    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

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
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        logger.debug("Tries add role [{}]", role);
        final String name = role.getName();

        final PhrRole oldRole;
        try {
            oldRole = getRoleForNameExactMatch(name);
        } catch (NonUniqueResultException exception) {
            final RoleException rException =
                    new RoleException(exception);
            rException.setRole(role);
            logger.error("Duplicate role found for {}", role);
            logger.error(rException.getMessage(), exception);
            throw rException;
        }

        if (oldRole == null) {
            entityManager.persist(role);
            logger.debug("Role [{}] was persisted.", role);
            return true;
        }

        final String newName = role.getName();
        if (newName != null && !newName.isEmpty()) {
            oldRole.setName(newName);
        }

        final String newDescription = role.getDescription();
        if (newDescription != null && !newDescription.isEmpty()) {
            oldRole.setDescription(newDescription);
        }

        final Set<PhrUser> newUsers = role.getUsers();
        if (newUsers != null) {
            // FIXME : I think I must remove the old one as well
            oldRole.setUsers(newUsers);
        }

        logger.debug("Role [{}] was upadted.", role);
        return false;
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
        if (name == null) {
            final NullPointerException nullException =
                    new NullPointerException("The name argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final PhrRole result = getRoleForNameExactMatch(name);
        return result;
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
     * @return a set that contains all the PHRS groups with the name matching
     * a given JPSQ like patten.
     */
    @Override
    public Set<PhrRole> getRolesForNamePattern(String namePattern) {
        if (namePattern == null) {
            final NullPointerException nullException =
                    new NullPointerException("The namePattern argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query = entityManager.createNamedQuery("findRoleForNamePattern");
        final Query queryResult = query.setParameter("namePattern", namePattern);
        final List resultList = queryResult.getResultList();

        final Set<PhrRole> result = new HashSet<PhrRole>(resultList);
        return result;
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
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query = entityManager.createNamedQuery("findRoleForName");
        query.setParameter("name", name);

        try {
            final PhrRole result = (PhrRole) query.getSingleResult();

            // mihai : 
            // I call 'result.getUsers().size();' to ensure that
            // that all the users are waking up when the group is retuned.
            // the reason for this is : the OneToMany lazy initialisation works
            // only in the same transtion/session - if the session is done
            // then the lazy initalisayion will fail.
            result.getUsers().size();

            return result;
        } catch (NoResultException exception) {
            logger.debug("No role with the name [{}] found.", name);
            return null;
        }
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
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final String name = role.getName();
        // mihai : I can also do a 'merge' to assign the entity to the
        // actaul context.
        final PhrRole oldRole = getRoleForNameExactMatch(name);
        if (oldRole != null) {
            entityManager.remove(oldRole);
        }

        return oldRole;
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
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final String name = role.getName();
        try {
            final PhrRole oldRole = getRoleForNameExactMatch(name);
            return oldRole != null;
        } catch (NonUniqueResultException exception) {
            final RoleException rException = new RoleException(exception);
            rException.setRole(role);
            logger.error("Duplicate role found for {}", role);
            logger.error(rException.getMessage(), exception);
            throw rException;
        }
    }

    /**
     * Return all the registered roles.
     *
     * @return all the registered roles.
     */
    @Override
    public Set<PhrRole> getAllRoles() {
        // FIXME : for big ammoutn of goups go pagging.
        final Query query = entityManager.createNamedQuery("getAllRoles");
        final List resultList = query.getResultList();
        final Set<PhrRole> result = new HashSet<PhrRole>(resultList);
        return result;
    }

    /**
     * Removes all the registered roles. After this the \
     * <code>getAllRoles</code> method call will return an empty set.
     */
    @Override
    public void removeAllRoles() {
        final Query allRolesQuery =
                entityManager.createNamedQuery("getAllRoles");

        final List<PhrRole> roles = allRolesQuery.getResultList();
        // mihai :
        // this may tahe while for a big number of roles.
        // I try to do it on one fire using a bulk operation but
        // the bulk delete have problems with the OneToMany.
        for (PhrRole role : roles) {
            entityManager.remove(role);
        }

        logger.debug("All the PHRS roles are removed.");
    }

    @Override
    public void assignUserToRole(PhrUser user, PhrRole role) {
        if (user == null) {
            final NullPointerException nullException =
                    new NullPointerException("The user argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(user, role);
        logger.debug("Tries to assign user [{}] to role [{}].", toLog);

        final Set<PhrUser> actualUsers = role.getUsers();
        final Set<PhrUser> users = actualUsers == null
                ? new HashSet<PhrUser>()
                : actualUsers;
        users.add(user);
        role.setUsers(users);

        entityManager.merge(user);
        entityManager.merge(role);

        logger.debug("User [{}] was assined to role [{}].", toLog);
    }

    @Override
    public void assignUsersToRole(Set<PhrUser> users, PhrRole role) {

        if (users == null) {
            final NullPointerException nullException =
                    new NullPointerException("The users argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (users.isEmpty()) {
            final IllegalArgumentException argumentException =
                    new IllegalArgumentException("The users can not be an empty exception.");
            logger.error(argumentException.getMessage(), argumentException);
            throw argumentException;
        }

        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(users, role);
        logger.debug("Tries to assign users [{}] to role [{}].", toLog);


        final Set<PhrUser> actualUsers = role.getUsers();
        final Set<PhrUser> usrs = actualUsers == null
                ? new HashSet<PhrUser>()
                : actualUsers;

        usrs.addAll(users);
        role.setUsers(users);
        for (PhrUser user : users) {
            entityManager.merge(user);
        }
        entityManager.merge(role);

        logger.debug("Users [{}] was assined to role [{}].", toLog);

    }

    @Override
    public void removeUserFromRole(PhrUser user, PhrRole role) {
        if (user == null) {
            final NullPointerException nullException =
                    new NullPointerException("The user argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(user, role);
        logger.debug("Tries to remove user [{}] from role [{}].", toLog);

        final Set<PhrUser> actualUsers = role.getUsers();
        final Set<PhrUser> users = actualUsers == null
                ? new HashSet<PhrUser>()
                : actualUsers;
        users.remove(user);
        role.setUsers(users);

        entityManager.merge(user);
        entityManager.remove(user);

        entityManager.merge(role);

        logger.debug("User [{}] was removed from role [{}].", toLog);
    }

    @Override
    public void removeUsersFromRole(Set<PhrUser> users, PhrRole role) {
        if (users == null) {
            final NullPointerException nullException =
                    new NullPointerException("The users argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (users.isEmpty()) {
            final IllegalArgumentException argumentException =
                    new IllegalArgumentException("The users can not be an empty exception.");
            logger.error(argumentException.getMessage(), argumentException);
            throw argumentException;
        }

        if (role == null) {
            final NullPointerException nullException =
                    new NullPointerException("The role argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(users, role);
        logger.debug("Tries to remove users [{}] from the role [{}].", toLog);

        final Set<PhrUser> roleUsers = role.getUsers();
        roleUsers.removeAll(users);
        role.setUsers(users);

        entityManager.merge(role);
        logger.debug("Users [{}] was removed from the role [{}].", toLog);
    }
}
