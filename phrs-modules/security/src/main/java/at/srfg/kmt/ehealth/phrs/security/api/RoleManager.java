/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.Set;

/**
 *
 * @author mradules
 */
public interface RoleManager {
    boolean addRole(PhrRole Role);
    boolean RoleExist(PhrRole principal);
    PhrRole removeRole(PhrRole principal);
    void removeAllRoles();
    Set <PhrRole> getAllRoles();

    void assignRoleToUser(PhrUser user, PhrRole role);
    void assignRolesToUser(Set<PhrUser> users, PhrRole role);

    void removeRoleFromUser(PhrUser user, PhrRole role);
    void removeRolesFromUser(Set<PhrUser> users, PhrRole role);
}
