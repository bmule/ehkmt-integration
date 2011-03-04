/*
 * Project :iCardea
 * File : PhrCategory.java
 * Date : Feb 25, 2011
 * User : mradules
 */

package at.srfg.kmt.ehealth.phrs.security.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *Gives the possibility to describe someone using a controlled vocabulary.
 * 
 * 
 * @author Mihai
 * @version 0.0.1
 * @since 0.0.1
 */
@Entity
public class PhrCategory {

    private Long id;
    private String name;
    private String uri;
    private String description;
    private String locale;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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
    
    
}
