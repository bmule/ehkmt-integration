/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.Set;

/**
 * Defines the functionality for the Group Manager service.
 * This service is used to manage and manipulate <code>PhrGroup</code>
 * instances.
 *
 * @author Mihai
 * @see PhrGroup
 */
public interface GroupManager {

    /**
     * Add/register a new group. If the group exists then this method
     * has no effect. The group existence can proved with the
     * <code>groupExist</code> method.
     *
     * @param group the group to add.
     * @return true if the group is successfully register.
     * @see GroupManager#groupExist(at.srfg.kmt.ehealth.phrs.security.model.PhrGroup)
     */
    boolean addGroup(PhrGroup group);

    /**
     * Proves if a group is registered or not.
     *
     * @param group the group that presence is to be tested.
     * @return true if the group is registered false otherwise.
     */
    boolean groupExist(PhrGroup group);

    /**
     * Removes a group. It is not defined here how the method must behave
     * if the group is not registered, this will be defined on the
     * implementation.
     *
     * @param group the group to remove.
     * @return the removed group.
     */
    PhrGroup removeGroup(PhrGroup group);

    /**
     * Removes all the groups.
     */
    void removeAllGroups();

    /**
     * Returns a Set that contains all the registered sets.
     *
     * @return a Set that contains all the registered sets.
     */
    Set<PhrGroup> getAllGroups();

    void assignUserToGroup(PhrUser user, PhrGroup group);
    void assignUsersToGroup(Set<PhrUser> users, PhrGroup group);

    void removeUserFromGroup(PhrUser user, PhrGroup group);
    void removeUsersFromGroup(Set<PhrUser> users, PhrGroup group);
}
