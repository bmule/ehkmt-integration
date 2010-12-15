/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs.security.model;


import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Generic representation for an user group.
 * A group is a form to relate together users.
 * A group can have none, one or more users.
 *
 * @author Mihai
 */
@Entity
public class Group implements Serializable {

    /**
     * Unique id, is generated by the underlying persistence layer.<br>
     * <b>Note : <b> Don't use this property in any kind of logic,
     * this property is used only by the underly persistence layer.
     */
    private Long id;

    /**
     * The group name.
     */
    private String name;

    /**
     * The group description.
     */
    private String description;

    /**
     * All the user members in this group.
     */
    private Set<User> users;

    /**
     * Builds an Group instance.
     */
    public Group() {
        // UNIMPLEMEMENTED
    }

    /**
     * Returns the description for this Group.
     *
     * @return the description for this Group.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the unique id for this Group instance. <br>
     * <b>Note : <b> Don't use this property in any kind of logic,
     * this property is used only by the underly persistence layer.
     *
     * @return the unique id for this user.
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    /**
     * Returns the name for this Group.
     *
     * @return the name for this Group.
     */
    public String getName() {
        return name;
    }

    public Set<User> getUsers() {
        return users;
    }

    /**
     * Registers a new description for this Group instance.
     *
     *
     * @param description a new description for this Group instance.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Registers a new unique id for this Group instance.<br>
     * <b>Note : <b> Don't set this property by hand (using this
     * method) and don't use this property any kind of logic.
     * This property is used only by the underly persistence layer.
     *
     * @param id the new unique id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Registers a new name for this group.
     * @param name the new name for this group.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Replaces the already existent set of users (for this group)
     * with a new set of users.
     *
     * @param users the new set of users.
     * @see #addUsers(java.util.Set)
     * @see #addUser(at.srfg.kmt.ehealth.phrs.security.model.User)
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }

    /**
     * Appends the actual set of users with a new set of users.
     *
     * @param users the set of users to be appended.
     * @see #addUser(at.srfg.kmt.ehealth.phrs.security.model.User) 
     * @see #setUsers(java.util.Set) 
     */
    public void addUsers(Set<User> users) {
        this.users.addAll(users);
    }

    /**
     * Add a singular user  to the actual user set.
     *
     * @param user the user to add.
     * @see #addUsers(java.util.Set)
     * @see #setUsers(java.util.Set)
     */
    public void addUser(User user) {
        users.add(user);
    }

    public boolean isEmpty() {
        return users == null ? true : users.isEmpty();
    }

    /**
     * Returns a string representation for this Group.
     * The string representation for this Group looks like
     * this :
     * <ul>
     * <li> the String 'Group{name='
     * <li> the name property
     * <li> the String ', description='
     * <li> the description property
     * <li> the String '}'
     * </ul>
     *
     * @return a string representation for this Group.
     */
    @Override
    public String toString() {
        final String result =
                String.format("Group{name=%s, description=%s}", name, description);
        return result;
    }
}
