/*
 * Project :iCardea
 * File : DynamicClass.java
 * Encoding : UTF-8
 * Date : Mar 17, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.model;


import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@NamedQueries({@NamedQuery(name="selectDynamicClassByURI", 
                           query="SELECT dc FROM DynamicClass AS dc WHERE dc.uri=:uri"),
               @NamedQuery(name="selectDynamicClassByURIPrefix", 
                           query="SELECT dc FROM DynamicClass AS dc WHERE dc.uri LIKE uri")
             })
@Entity
public class DynamicClass implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    private String uri;
    
    private String name;
    
    private Set<DynamicPropertyType> propertyTypes;
    
    public DynamicClass() {
        // UNIMPLEMENTED
    }

    public DynamicClass(String uri) {
        this.uri = uri;
    }

    public DynamicClass(String uri, String name) {
        this.uri = uri;
        this.name = name;
    }

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

    @OneToMany(mappedBy="dynamicClass")
    public Set<DynamicPropertyType> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(Set<DynamicPropertyType> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }
}
