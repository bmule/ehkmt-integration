package at.srfg.kmt.ehealth.phrs.security.api;

import java.security.Principal;

/**
 * Defines the basic operations that can be accomplished with/on users.
 *
 * @author Mihai
 */
public interface UserManager {
    boolean addUser(Principal principal);
    boolean userExist(Principal principal);
    Principal removeUser(Principal principal);
    void removeAllUsers();
}
