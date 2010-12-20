/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.PhrGroup;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.Set;

/**
 *
 * @author Mihai
 */
public interface GroupManager {
    boolean addGroup(PhrGroup group);
    boolean groupExist(PhrGroup principal);
    PhrGroup removeGroup(PhrGroup principal);
    void removeAllGroups();
    Set<PhrGroup> getAllGroups();

    void assignUserToGroup(PhrUser user, PhrGroup group);
    void assignUsersToGroup(Set<PhrUser> users, PhrGroup group);

    void removeUserFromGroup(PhrUser user, PhrGroup group);
    void removeUsersFromGroup(Set<PhrUser> users, PhrGroup group);
}
