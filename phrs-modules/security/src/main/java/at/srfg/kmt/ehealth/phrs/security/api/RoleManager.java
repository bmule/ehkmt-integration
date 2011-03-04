/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs.security.api;


import at.srfg.kmt.ehealth.phrs.security.model.Fetcher;
import at.srfg.kmt.ehealth.phrs.security.model.PhrRole;
import at.srfg.kmt.ehealth.phrs.security.model.PhrActor;
import java.util.Set;

/**
 * Defines the functionality for the Role Manager service.
 * This service is used to manage and manipulate <code>PhrRole</code>
 * instances.
 *
 * @author Mihai
 */
public interface RoleManager {

    /**
     * Add/register a new role. If the role exists then this method
     * has no effect. The role existence can proved with the
     * <code>roleExist</code> method.
     *
     * @param role the role to add.
     * @return true if the role is successfully register.
     * @see RoleManager#roleExist(at.srfg.kmt.ehealth.phrs.security.model.PhrRole)
     */
    boolean addRole(PhrRole role);

    /**
     * Proves if a role is registered or not.
     *
     * @param role the role that presence is to be tested.
     * @return true if the role is registered false otherwise.
     */
    boolean roleExist(PhrRole role);

    /**
     * Removes a role. It is not defined here how the method must behave
     * if the role is not registered, this will be defined on the
     * implementation.
     *
     * @param role the role to remove.
     * @return the removed role.
     */
    PhrRole removeRole(PhrRole role);

    /**
     * Removes all the roles.
     */
    void removeAllRoles();

    /**
     * Returns a Set that contains all the registered roles.
     *
     * @return a Set that contains all the registered roles.
     */
    Set<PhrRole> getAllRoles();
    
    Set<PhrRole> getAllRoles(Fetcher<PhrRole> fetcher);

    /**
     * Returns a group for a given name, the role name attribute match
     * exactly the specified name.
     *
     * @param name the group name.
     * @return a PHRS group with the name name that match exactly the
     * specified name.
     */
    PhrRole getRoleForName(String name);
    PhrRole getRoleForName(String name, Fetcher<PhrRole> fetcher);

    /**
     * Returns a set of <code>PhrRole</code> where the role name match
     * a certain pattern. The pattern syntax is defined in the implementation.
     *
     * @param namePattern the name pattern.
     * @return a set of <code>PhrRole</code> where the role name match
     * a certain pattern
     */
    Set<PhrRole> getRolesForNamePattern(String namePattern);
    Set<PhrRole> getRolesForNamePattern(String namePattern, Fetcher<PhrRole> fetcher);

    /**
     * Returns a Set that contains all the registered roles.
     *
     * @return a Set that contains all the registered roles.
     */
    void assignActorToRole(PhrActor actor, PhrRole role);

    void assignActorsToRole(Set<PhrActor> actors, PhrRole role);

    void removeActorFromRole(PhrActor actor, PhrRole role);

    void removeActorsFromRole(Set<PhrActor> actors, PhrRole role);
}
