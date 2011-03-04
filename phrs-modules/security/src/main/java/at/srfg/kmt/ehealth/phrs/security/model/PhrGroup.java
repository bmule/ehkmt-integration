/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package at.srfg.kmt.ehealth.phrs.security.model;


import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;


/**
 * A group is a form to relate together actors.
 * A group can have none, one or more actors.
 *
 * @author Mihai
 */

@Entity
@DiscriminatorValue("PhrGroup")
@NamedQueries({
    @NamedQuery(name="selectAllGroups", 
                query="SELECT g FROM PhrGroup AS g"),
    
    @NamedQuery(name="selectGroupByName", 
                query="SELECT g FROM PhrGroup AS g WHERE g.name=:name"),
    
    @NamedQuery(name="selectGroupsByNamePattern", 
                query="SELECT g FROM PhrGroup AS g WHERE g.name LIKE :name_pattern")
    
})
public class PhrGroup extends PhrSocialActor {

    /**
     * All the actors members in this group.
     */
    private Set<PhrActor> members;

    /**
     * Builds an Group instance.
     */
    public PhrGroup() {
        // UNIMPLEMEMENTED
    }

    /**
     * Builds an Group instance for a given name.
     */
    public PhrGroup(String name) {
        super(name);
    }

    @OneToMany(cascade=CascadeType.ALL)
    public Set<PhrActor> getMembers() {
        return members;
    }

    /**
     * Replaces the already existent set of actors (for this group)
     * with a new set of actors.
     *
     * @param actors the new set of actors.
     */
    public void setMembers(Set<PhrActor> members) {
        this.members = members;
    }

    @Transient
    public boolean isGroupEmpty() {
        return members == null ? true : members.isEmpty();
    }
}
