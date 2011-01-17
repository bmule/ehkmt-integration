/*
 * Project :iCardea
 * File : GroupManagerBean.java
 * Date : Dec 15, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.impl;


import at.srfg.kmt.ehealth.phrs.security.api.GroupManager;
import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
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
 * <code>PhrGroup</code> and related information.
 *
 * @author Mihai
 */
@Stateless
@Local(GroupManager.class)
public class GroupManagerBean implements GroupManager {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.GroupManager</code>.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(GroupManagerBean.class);

    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    /**
     * Registers a  group instance or update and existing one if 
     * the underlying persistence contains already a group with
     * the same name. The method returns true if the group is
     * added and false if the group is updated.
     *
     * @param group the group to add, it can not be null.
     * @return true if the group is added, false if the group is updated.
     * @throws NullPointerException if the <code>group</code> argument is null.
     * @throws GroupException if the underlying persistence layer contains more
     * than one group with the same name. The exception transports the
     * <code>Group</code> that cause the exception, this can be obtained by
     * calling the <code>getGroup</code> method (from the GroupException).
     * @see GroupException
     */
    @Override
    public boolean addGroup(PhrGroup group) {

        if (group == null) {
            final NullPointerException nullException =
                    new NullPointerException("The Group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }


        logger.debug("Tries to add Group [{}]", group);
        final Long id = group.getId();

        if (id == null) {
            entityManager.persist(group);
            logger.debug("The Group [{}] was persisted.", group);
            return true;
        }
        final PhrGroup oldGroup =
                entityManager.find(PhrGroup.class, id);

        if (oldGroup == null) {
            final GroupException groupException =
                    new GroupException("Inconsistent Data Structure.");
            groupException.setGroup(group);
            logger.error(groupException.getMessage(), groupException);
            throw groupException;
        }

        entityManager.merge(group);

        logger.debug("Group [{}] was upadted.", group);
        return false;
    }

    /**
     * Searches a group with the given name.
     *
     * @param name the name for the group.
     * @return the group with the given name.
     * @throws NonUniqueResultException if the underlying persistence
     * later contains more groups with this name (this lead to an inconsistent
     * database model).
     * @throws NullPointerException if the <code>group</code> argument is null.
     * @throws GroupException if the underlying persistence layer contains more
     * than one group with the same name. The exception transports the
     * <code>Group</code> that cause the exception, this can be obtained by
     * calling the <code>getGroup</code> method (from the GroupException).
     * @see GroupException
     */
    private PhrGroup getGroupForNameExactMatch(String name) {
        final Query query = entityManager.createNamedQuery("findGroupForName");
        query.setParameter("name", name);

        try {
            final PhrGroup result = (PhrGroup) query.getSingleResult();

            // mihai : 
            // I call 'result.getUsers().size();' to ensure that
            // that all the users are waking up when the group is retuned.
            // the reason for this is : the OneToMany lazy initialisation works
            // only in the same transtion/session - if the session is done
            // then the lazy initalisayion will fail.
            result.getPhrUsers().size();
            return result;
        } catch (NoResultException exception) {
            logger.debug("No group with the name [{}] found.", name);
            return null;
        }
    }

    /**
     * Proves if the underlying persistence contains a given group.
     *
     * @param group the group which the existence is to be tested.
     * @return true if the underlying persistence contains a given
     * group, false other wise.
     */
    @Override
    public boolean groupExist(PhrGroup group) {

        if (group == null) {
            final NullPointerException nullException =
                    new NullPointerException("The Group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }


        final String name = group.getName();
        try {
            final PhrGroup oldGroup = getGroupForNameExactMatch(name);
            return oldGroup != null;
        } catch (NonUniqueResultException exception) {
            final GroupException gException =
                    new GroupException(exception);
            gException.setGroup(group);
            logger.error("Duplicate group found for {}", group);
            logger.error(gException.getMessage(), exception);
            throw gException;
        }
    }

    /**
     * Removes a specified group. The group to remove must be registered
     * otherwise this has no effect.
     *
     * @param group the group to remove, it can not be null.
     * @return the removed group.
     * @throws NullPointerException if the group argument is null.
     */
    @Override
    public PhrGroup removeGroup(PhrGroup group) {

        // the reason why this method is final is because it is used by the 
        // assignUsersToGroup(...) method.


        if (group == null) {
            final NullPointerException nullException =
                    new NullPointerException("The Group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        logger.debug("Tries to remove group [{}]", group);

        final PhrGroup managedGroup = entityManager.merge(group);
        final Set<PhrUser> users = managedGroup.getPhrUsers();
        if (users == null || users.isEmpty()) {
            entityManager.remove(managedGroup);
            logger.debug("Group [{}] was removed.", managedGroup);
            return managedGroup;
        }

        // removes the group from the user (the owner side).
        for (PhrUser user : users) {
            final Set<PhrGroup> groups = user.getPhrGroups();
            if (groups != null && !groups.isEmpty()) {
                groups.remove(managedGroup);
            }
        }

        entityManager.remove(managedGroup);
        logger.debug("Group [{}] was removed.", managedGroup);
        return managedGroup;
    }

    /**
     * Removes all the registered groups. After this the \
     * <code>getAllGroups</code> method call will return an empty set.
     */
    @Override
    public void removeAllGroups() {
        final Query allGroupsQuery =
                entityManager.createNamedQuery("getAllGroups");

        final List<PhrGroup> groups = allGroupsQuery.getResultList();
        // mihai :
        // this may tahe while for a big number of groups.
        // I try to do it on one fire using a bulk operation but
        // the bulk delete have problems with the OneToMany.
        for (PhrGroup group : groups) {
            entityManager.remove(group);
        }
    }

    /**
     * Return all the registered groups.
     *
     * @return all the registered groups.
     */
    @Override
    public Set<PhrGroup> getAllGroups() {
        final Query query = entityManager.createNamedQuery("getAllGroups");
        final List resultList = query.getResultList();
        final Set<PhrGroup> result = new HashSet<PhrGroup>(resultList);
        return result;
    }

    @Override
    public final void assignUserToGroup(PhrUser user, PhrGroup group) {

        // the reason why this method is final is because it is used by the 
        // assignUsersToGroup(...) method.
        if (user == null) {
            final NullPointerException nullException =
                    new NullPointerException("The user argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (group == null) {
            final NullPointerException nullException =
                    new NullPointerException("The group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(user, group);
        logger.debug("Tries to assign user [{}] to group [{}].", toLog);

        // assign user to group (owner side)
        final PhrUser managedUser = entityManager.merge(user);
        final Set<PhrGroup> phrGroups = managedUser.getPhrGroups();
        final Set<PhrGroup> groups = phrGroups == null
                ? new HashSet<PhrGroup>()
                : phrGroups;

        final PhrGroup managedGroup = entityManager.merge(group);
        groups.add(managedGroup);
        managedUser.setPhrGroups(groups);

        // assign group to user (inverse side)
        final Set<PhrUser> phrUsers = managedGroup.getPhrUsers();
        final Set<PhrUser> users = phrUsers == null
                ? new HashSet<PhrUser>()
                : phrUsers;
        managedGroup.setPhrUsers(users);

        logger.debug("User [{}] was assined to group [{}].", toLog);
    }

    @Override
    public void assignUsersToGroup(Set<PhrUser> users, PhrGroup group) {

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

        if (group == null) {
            final NullPointerException nullException =
                    new NullPointerException("The group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(users, group);
        logger.debug("Tries to assign users [{}] to group [{}].", toLog);

        for (PhrUser user : users) {
            assignUserToGroup(user, group);
        }

        logger.debug("Users [{}] was assined to group [{}].", toLog);
    }

    @Override
    public final void removeUserFromGroup(PhrUser user, PhrGroup group) {

        // the reason why this method is final is because it is used by the 
        // removeUsersFromGroup(...) method.
        if (user == null) {
            final NullPointerException nullException =
                    new NullPointerException("The user argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (group == null) {
            final NullPointerException nullException =
                    new NullPointerException("The group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(user, group);
        logger.debug("Tries to remove user [{}] from group [{}].", toLog);

        final PhrUser managedUser = entityManager.merge(user);
        final Set<PhrGroup> phrGroups = managedUser.getPhrGroups();
        if (phrGroups == null || phrGroups.isEmpty()) {
            logger.debug("There is no relation between the user [{}] and the group [{}], remove user has no effect.", toLog);
            // I leave if there are no relations.
            return;
        }

        // removes the user from the group (inverse)
        final PhrGroup managedGroup = entityManager.merge(group);
        final Set<PhrUser> phrUsers = managedGroup.getPhrUsers();
        phrUsers.remove(managedUser);
        managedGroup.setPhrUsers(phrUsers);

        // removes the group from the user (owner)
        phrGroups.remove(managedGroup);
        managedUser.setPhrGroups(phrGroups);

        logger.debug("User [{}] was removed from group [{}].", toLog);
    }

    @Override
    public void removeUsersFromGroup(Set<PhrUser> users, PhrGroup group) {
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

        if (group == null) {
            final NullPointerException nullException =
                    new NullPointerException("The group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Object[] toLog = Util.forLog(users, group);
        logger.debug("Tries to remove users [{}] from the group [{}].", toLog);

        for (PhrUser user : users) {
            removeUserFromGroup(user, group);
        }

        entityManager.merge(group);
        logger.debug("Users [{}] was removed from the group [{}].", toLog);
    }

    /**
     * Returns a <code>PhrGroup</code> where the name attribute exactly match
     * (case sensitive) the given <code>name</code>. If there is no matching
     * group this method returns null.
     *
     * @param name the name for the group to search, it can not be null.
     * @return a <code>PhrGroup</code> where the name attribute exactly match
     * the given <code>name</code> or null for no match.
     * @throws NullPointerException if the <code>name</code> argument is null.
     */
    @Override
    public PhrGroup getGroupForName(String name) {

        if (name == null) {
            final NullPointerException nullException =
                    new NullPointerException("The name argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final PhrGroup result = getGroupForNameExactMatch(name);
        return result;
    }

    /**
     * Returns a set that contains all the PHRS groups with the name matching
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
    public Set<PhrGroup> getGroupsForNamePattern(String namePattern) {

        if (namePattern == null) {
            final NullPointerException nullException =
                    new NullPointerException("The namePattern argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query = entityManager.createNamedQuery("findGroupForNamePattern");
        final Query queryResult = query.setParameter("namePattern", namePattern);
        final List resultList = queryResult.getResultList();

        final Set<PhrGroup> result = new HashSet<PhrGroup>(resultList);
        return result;
    }
}
