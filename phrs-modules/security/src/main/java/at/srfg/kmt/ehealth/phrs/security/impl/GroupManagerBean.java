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
    @PersistenceContext(unitName="phrs_storage")
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

        logger.debug("Tries add Group [#0]", group);
        final String name = group.getName();

        final PhrGroup oldGroup;
        try {
            oldGroup = getGroupForName(name);
        } catch (NonUniqueResultException exception) {
            final GroupException gException =
                    new GroupException(exception);
            gException.setGroup(group);
            logger.error("Duplicate group found for #0", group);
            logger.error(gException.getMessage(), exception);
            throw gException;
        }

        if (oldGroup == null) {
            entityManager.persist(group);
            logger.debug("Group [#0] was persisted.", group);
            return true;
        }

        final String newName = group.getName();
        if (newName != null && !newName.isEmpty()) {
            oldGroup.setName(newName);
        }

        final String newDescription = group.getDescription();
        if (newDescription != null && !newDescription.isEmpty()) {
            oldGroup.setDescription(newDescription);
        }

        final Set<PhrUser> newUsers = group.getUsers();
        if (newUsers != null) {
            // FIXME : I think I must remove the old one as well
            oldGroup.setUsers(newUsers);
        }

        logger.debug("Group [#0] was upadted.", group);
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
    private PhrGroup getGroupForName(String name) {
        final Query query = entityManager.createNamedQuery("findGroupForName");
        query.setParameter("name", name);

        try {
            final PhrGroup result = (PhrGroup) query.getSingleResult();
            return result;
        } catch (NoResultException exception) {
            logger.debug("No group with the name [#0] found.", name);
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
            final PhrGroup oldGroup = getGroupForName(name);
            return oldGroup != null;
        } catch (NonUniqueResultException exception) {
            final GroupException gException =
                    new GroupException(exception);
            gException.setGroup(group);
            logger.error("Duplicate group found for #0", group);
            logger.error(gException.getMessage(), exception);
            throw gException;
        }

    }

    @Override
    public PhrGroup removeGroup(PhrGroup group) {
        
        if (group == null) {
            final NullPointerException nullException =
                    new NullPointerException("The Group argument can not be null.");
            logger.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final String name = group.getName();
        // mihai : I can also do a 'merge' to assign the entity to the
        // actaul context.
        final PhrGroup oldGroup = getGroupForName(name);
        if (oldGroup != null) {
            entityManager.remove(oldGroup);
        }

        return oldGroup;
    }

    @Override
    public void removeAllGroups() {
        final Query query = entityManager.createNamedQuery("removeAllGroups");
        query.executeUpdate();
    }

    @Override
    public Set<PhrGroup> getAllGroups() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void assignUserToGroup(PhrUser user, PhrGroup group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void assignUsersToGroup(Set<PhrUser> users, PhrGroup group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUserFromGroup(PhrUser user, PhrGroup group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUsersFromGroup(Set<PhrUser> users, PhrGroup group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
