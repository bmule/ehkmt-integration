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
import javax.persistence.OneToOne;


/**
 * Defines a generic action that can be accomplished on
 * piece of information from a certain user.
 *
 * @author Mihai
 */
@Entity
public class PhrAction implements Serializable {
    
    /**
     * A version number for this class so that serialization
     * can occur without worrying about the underlying class
     * changing between serialization and deserialization.
     */
    private static final long serialVersionUID = 5165L;    

    /**
     * Unique id, is generated by the underlying persistence layer.<br>
     * <b>Note : <b> Don't use this property in any kind of logic,
     * this property is used only by the underly persistence layer.
     */
    private Long id;
    
    /**
     * The name for this action.
     */
    private String name;
    
    /**
     * The URI for this action.
     */
    private String uri;
    
    /**
     * The actor involved in the action.
     */
    private PhrActor phrActor;

    /**
     * Returns the unique id for this Role instance. <br>
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
    
    @OneToOne
    public PhrActor getPhrActor() {
        return phrActor;
    }

    public void setPhrActor(PhrActor actor) {
        this.phrActor = actor;
    }

    @Override
    public String toString() {
        final String result = String.format("PhrAction{name=%s, user=%s, uri=%s}", name, phrActor, uri);
        return "PhrAction{" + "name=" + name + "uri=" + uri + '}';
    }
}
