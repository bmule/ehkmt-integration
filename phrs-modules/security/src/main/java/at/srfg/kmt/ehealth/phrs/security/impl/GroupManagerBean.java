/*
 * Project :iCardea
 * File : GroupManagerBean.java
 * Date : Dec 15, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.impl;

import at.srfg.kmt.ehealth.phrs.security.api.GroupManager;
import at.srfg.kmt.ehealth.phrs.security.model.Group;
import at.srfg.kmt.ehealth.phrs.security.model.User;
import java.util.Set;
import javax.ejb.Local;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Mihai
 */
@Stateless
@Local(GroupManager.class)
public class GroupManagerBean implements GroupManager {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean addGroup(Group group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean groupExist(Group principal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Group removeGroup(Group principal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAllGroups() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Group> getAllGroups() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void assignUserToGroup(User user, Group group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void assignUsersToGroup(Set<User> users, Group group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUserFromGroup(User user, Group group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeUsersFromGroup(Set<User> users, Group group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
