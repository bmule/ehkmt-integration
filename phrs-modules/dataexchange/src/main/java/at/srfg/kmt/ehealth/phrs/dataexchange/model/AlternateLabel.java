/*
 * Project :iCardea
 * File : AlternateLabel.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Entity
public class AlternateLabel implements Serializable {
    
    private Long id;
    private String label;
    private String locale;

    public AlternateLabel() {
    }

    public AlternateLabel(String label) {
        this.label = label;
    }

    public AlternateLabel(String label, String locale) {
        this.label = label;
        this.locale = locale;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
