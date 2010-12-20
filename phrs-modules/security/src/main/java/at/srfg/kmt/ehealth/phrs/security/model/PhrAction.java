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

/**
 * Defines a generic piece of information.
 * An item can be everything (from an entire documents
 * tree until a singular XML fragment )
 *
 * @author Mihai
 */
@Entity
public class PhrAction implements Serializable {

    private Long id;
    private String name;
    private String uri;
    private Serializable original;
    private PhrAction parent;
    private PhrAction next;
    private PhrAction previous;

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

    public PhrAction getNext() {
        return next;
    }

    public void setNext(PhrAction next) {
        this.next = next;
    }

    public PhrAction getParent() {
        return parent;
    }

    public void setParent(PhrAction parent) {
        this.parent = parent;
    }

    public PhrAction getPrevious() {
        return previous;
    }

    public void setPrevious(PhrAction previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        final String result = String.format("PhrItem{name=#0, url=#1, parent=#2}",
                name, 
                uri,
                parent == null ? "no parent" : parent.getName());
        return result;
    }
}
