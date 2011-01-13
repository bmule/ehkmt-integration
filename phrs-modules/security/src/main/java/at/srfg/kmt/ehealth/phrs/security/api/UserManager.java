package at.srfg.kmt.ehealth.phrs.security.api;

import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.Set;


/**
 * Defines the basic operations that can be accomplished with/on users.
 *
 * @author Mihai
 */
public interface UserManager {
    boolean addUser(PhrUser user);
    boolean addUsers(Set<PhrUser> users);
    boolean userExist(PhrUser user);
    PhrUser removeUser(PhrUser user);
    PhrUser removeUsers(Set<PhrUser> users);
    Set<PhrUser> getUsersForName(String name);
    Set<PhrUser> getUsersForNamePattern(String namePattern);
    Set<PhrUser> getUsersForNamePattern(String namePattern, String famillyNamePattern);

    void removeAllUsers();
    PhrUser getAnonymusUser();
}
