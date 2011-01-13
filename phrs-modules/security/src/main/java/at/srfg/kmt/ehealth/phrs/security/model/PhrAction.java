/*
 * Project :iCardea
 * File : PhrItem.java
 * Date : Dec 17, 2010
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


/**
 * Defines a generic piece of information.
 * An item can be everything (from an entire documents
 * tree until a singular XML fragment )
 *
 * @author Mihai
 */
@NamedQueries({
    @NamedQuery(name = "findActionForName", query = "SELECT a FROM PhrAction AS a WHERE a.name = :name"),
    @NamedQuery(name = "removeAllActions", query = "DELETE FROM PhrAction AS a"),
    @NamedQuery(name = "removeActionForName", query = "DELETE FROM PhrAction AS a WHERE a.name = :name"),
    @NamedQuery(name = "getAllActions", query = "SELECT a  FROM PhrAction AS a")
})
@Entity
public class PhrAction implements Serializable {
    
    /**
     * A version number for this class so that serialization
     * can occur without worrying about the underlying class
     * changing between serialization and deserialization.
     */
    private static final long serialVersionUID = 5165L;    

    private Long id;
    private String name;
    private String uri;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "PhrAction{" + "name=" + name + "uri=" + uri + '}';
    }
}
