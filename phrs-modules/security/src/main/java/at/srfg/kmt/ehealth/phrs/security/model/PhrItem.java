/*
 * Project :iCardea
 * File : PhrItem.java
 * Date : Dec 17, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.security.model;


import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Defines a generic piece of information.
 * An item can be everything (from an entire documents
 * tree until a singular XML fragment )
 *
 * @author Mihai
 */
@Entity
public class PhrItem implements Serializable {

    /**
     * A version number for this class so that serialization
     * can occur without worrying about the underlying class
     * changing between serialization and deserialization.
     */
    private static final long serialVersionUID = 5165L;    

    private Long id;

    private String name;

    private String uri;

    private Serializable original;

    private PhrItem parent;

    private PhrItem next;

    private PhrItem previous;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    public Serializable getOriginal() {
        return original;
    }

    public void setOriginal(Serializable original) {
        this.original = original;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public PhrItem getNext() {
        return next;
    }

    public void setNext(PhrItem next) {
        this.next = next;
    }

    public PhrItem getParent() {
        return parent;
    }

    public void setParent(PhrItem parent) {
        this.parent = parent;
    }

    public PhrItem getPrevious() {
        return previous;
    }

    public void setPrevious(PhrItem previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        final String result = String.format("PhrItem{name=#0, uri=#1, parent=#2}",
                name,
                uri,
                parent == null ? "no parent" : parent.getName());
        return result;
    }
}
