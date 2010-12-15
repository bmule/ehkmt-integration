/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.Role;
import at.srfg.kmt.ehealth.phrs.security.model.User;
import java.util.Set;

/**
 *
 * @author mradules
 */
public interface RoleManager {
    boolean addRole(Role Role);
    boolean RoleExist(Role principal);
    Role removeRole(Role principal);
    void removeAllRoles();
    Set <Role> getAllRoles();

    void assignRoleToUser(User user, Role role);
    void assignRolesToUser(Set<User> users, Role role);

    void removeRoleFromUser(User user, Role role);
    void removeRolesFromUser(Set<User> users, Role role);
}
