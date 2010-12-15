/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.Group;
import at.srfg.kmt.ehealth.phrs.security.model.User;
import java.util.Set;

/**
 *
 * @author Mihai
 */
public interface GroupManager {
    boolean addGroup(Group group);
    boolean groupExist(Group principal);
    Group removeGroup(Group principal);
    void removeAllGroups();
    Set<Group> getAllGroups();

    void assignUserToGroup(User user, Group group);
    void assignUsersToGroup(Set<User> users, Group group);

    void removeUserFromGroup(User user, Group group);
    void removeUsersFromGroup(Set<User> users, Group group);
}
