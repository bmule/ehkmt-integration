/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.Group;
import at.srfg.kmt.ehealth.phrs.security.model.Role;
import at.srfg.kmt.ehealth.phrs.security.model.User;

/**
 *
 * @author mradules
 */
public interface PermissionManager {

    boolean prove(User user, Action action, Item item);
    boolean prove(User user, Action action);

    boolean prove(Role role, Action action, Item item);
    boolean prove(Role role, Action action);

    boolean prove(Group role, Action action, Item item);
    boolean prove(Group role, Action action);

    Group getAllowdGroupFor(Action action, Item item);
    Role getAllowdRoleFor(Action action, Item item);
}
