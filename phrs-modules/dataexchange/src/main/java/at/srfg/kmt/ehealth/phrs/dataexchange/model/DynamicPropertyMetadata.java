/*
 * Project :iCardea
 * File : DynamicPropertyMetadata.java
 * Encoding : UTF-8
 * Date : Mar 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Entity
public class DynamicPropertyMetadata implements Serializable {

    private Long id;
    
    private String name;
    
    private String uri;
    
    private Serializable value;
    
    private DynamicPropertyType propertyType;

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

    @ManyToOne(cascade= CascadeType.ALL)
    public DynamicPropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(DynamicPropertyType propertyType) {
        this.propertyType = propertyType;
    }
}
