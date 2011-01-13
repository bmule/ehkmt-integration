package at.srfg.kmt.ehealth.phrs.security.api;


import at.srfg.kmt.ehealth.phrs.security.model.PhrUser;
import java.util.Set;


/**
 * Defines the basic operations that can be accomplished with/on users.
 *
 * @author Mihai
 */
public interface UserManager {

    /**
     * Register a given user.
     * 
     * @param user the user to register.
     * @return true if the operation was successfully.
     */
    boolean addUser(PhrUser user);

    /**
     * Registers more users.
     * 
     * @param users the users to register.
     */
    void addUsers(Set<PhrUser> users);

    /**
     * Proves if a given user is registered or not.
     * 
     * @param user the user which existence is to be proved.
     * @return true if the given user is registered.
     */
    boolean userExist(PhrUser user);

    PhrUser removeUser(PhrUser user);

    PhrUser removeUsers(Set<PhrUser> users);

    Set<PhrUser> getUsersForName(String name);

    Set<PhrUser> getUsersForNamePattern(String namePattern);

    Set<PhrUser> getUsersForNamePattern(String namePattern, String famillyNamePattern);

    void removeAllUsers();

    PhrUser getAnonymusUser();
}
