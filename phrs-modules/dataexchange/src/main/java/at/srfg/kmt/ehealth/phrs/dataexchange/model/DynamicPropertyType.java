/*
 * Project :iCardea
 * File : DynamicProperty.java
 * Encoding : UTF-8
 * Date : Mar 17, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.model;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Entity
public class DynamicPropertyType implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String type;
    private DynamicClass dynamicClass;
    private Set<DynamicPropertyMetadata> metadatas;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade= CascadeType.ALL)
    public DynamicClass getDynamicClass() {
        return dynamicClass;
    }

    public void setDynamicClass(DynamicClass dynamicClass) {
        this.dynamicClass = dynamicClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @OneToMany(mappedBy = "propertyType")
    public Set<DynamicPropertyMetadata> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(Set<DynamicPropertyMetadata> metadatas) {
        this.metadatas = metadatas;
    }
}
