/*
 * Project :iCardea
 * File : DynamicBean.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * This entity defines the following named querties :
 * <ul>
 * <li> selectDynamicBeanByDynamicClass - it selects all the 
 * <code>DynamicBean</code> that have a certain class, the class is specified 
 * like query argument named "dynamic_class".
 * </ul>
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Entity
@NamedQueries({
    @NamedQuery(name="selectDynamicBeanByDynamicClass", 
                query="SELECT db FROM DynamicBean AS db WHERE db.dynamicClass=:dynamic_class"),
    @NamedQuery(name="selectDynamicBeanByDynamicClassWithProperties", 
                query="SELECT db FROM DynamicBean AS db LEFT JOIN FETCH db.properties WHERE db.dynamicClass=:dynamic_class"),
    @NamedQuery(name="selectLastDynamicBeanByDynamicClass", 
                query="SELECT db FROM DynamicBean AS db WHERE db.dynamicClass=:dynamic_class ORDER BY db.created ASC"),
    @NamedQuery(name="selectLastDynamicBeanByDynamicClassWithProperties", 
                query="SELECT db FROM DynamicBean AS db LEFT JOIN FETCH db.properties WHERE db.dynamicClass=:dynamic_class ORDER BY db.created ASC")
}) 
public class DynamicBean implements Serializable {

    private Long id;
    private String name;
    private Set<DynamicProperty> properties;
    private DynamicClass dynamicClass;
    private Date created;
    private String ownerURI;
    private Boolean canRead;
    private Boolean canWrite;

    public DynamicBean() {
        // UNIMPLEMENTED
    }

    public DynamicBean(String name) {
        this.name = name;
    }

    public DynamicBean(String name, DynamicClass dynamicClass) {
        this.name = name;
        this.dynamicClass = dynamicClass;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
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

    @OneToMany(mappedBy="dynamicBean", cascade= CascadeType.ALL)
    public Set<DynamicProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<DynamicProperty> properrties) {
        this.properties = properrties;
    }

    @OneToOne
    public DynamicClass getDynamicClass() {
        return dynamicClass;
    }

    public void setDynamicClass(DynamicClass dynamicClass) {
        this.dynamicClass = dynamicClass;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public Boolean getCanWrite() {
        return canWrite;
    }

    public void setCanWrite(Boolean canWrite) {
        this.canWrite = canWrite;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getOwnerURI() {
        return ownerURI;
    }

    public void setOwnerURI(String ownerURI) {
        this.ownerURI = ownerURI;
    }

    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer();
        result.append("DynamicBean{" + "name="); 
        result.append(name); 
        result.append(", dynamicClass="); 
        result.append(dynamicClass); 
        result.append(", created="); 
        result.append(created); 
        result.append(", ownerURI="); 
        result.append(ownerURI); 
        result.append(", canRead="); 
        result.append(canRead); 
        result.append(", canWrite="); 
        result.append(canWrite); 
        result.append("}"); 
                
        return result.toString();
    }
    
    
    
}
