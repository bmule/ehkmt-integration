

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

    /**
     * Remove/ unregisters a given user. It is not defined here what must
     * happen if the user is not registered this will be defined in the
     * implementation.
     * 
     * @param user the user to be removed.
     * @return the removed user.
     */
    PhrUser removeUser(PhrUser user);

    /**
     * Removes/unregisters a given set of users.
     * 
     * @param users the users to remove.
     */
    void removeUsers(Set<PhrUser> users);

    /**
     * Returns a set of <code>PhrUser</code> for a given name, 
     * the group name attribute match exactly the specified name.
     * 
     * @param name the group name.
     * @return a PHRS user with the name name that match exactly the
     * specified name.
     */
    Set<PhrUser> getUsersForName(String name);

    /**
     * Returns a set of <code>PhrUser</code> where the group name match
     * a certain pattern. The pattern syntax is defined in the implementation.
     *
     * @param namePattern the name pattern.
     * @return a set of <code>PhrUser</code> where the user name match
     * a certain pattern
     */
    Set<PhrUser> getUsersForNamePattern(String namePattern);

    /**
     * Returns a set of <code>PhrUser</code> where the group name match
     * a certain pattern. The pattern syntax is defined in the implementation.
     *
     * @param namePattern the name pattern.
     * @return a set of <code>PhrUser</code> where the user name match
     * a certain pattern
     */
    Set<PhrUser> getUsersForNamesPattern(String namePattern, String famillyNamePattern);

    /**
     * Returns all registered users.
     * 
     * @return all the registered users.
     */
    Set<PhrUser> getAllUsers();

    /**
     * Proves if any user is registered.
     * 
     * @return true if there is at least one user registered.
     */
    boolean areAnyUsersRegistered();

    /**
     * Removes/unregisters all the registered users, after this the
     * <code>areAnyUsersRegistered</code> will return false.
     * 
     */
    void removeAllUsers();

    PhrUser getAnonymusUser();
}
