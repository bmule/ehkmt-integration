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
import javax.persistence.Column;
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
 * This entity is used to define instances for a given (dynamic) class. 
 * An instance can be used to manage and manipulate various properties, this
 * properties are defined in a class.
 * An instance can have various versions - all this version will have the same 
 * URI but a different version.
 * All the instances have a common set of properties, those are :
 * <ul>
 * <li> name - human readable name for the bean instance.
 * <li> uri - used to unique identify an instance.
 * <li> uriVersion - used to identify a certain bean instance version.
 * <li> dynamicProperties - a set that contains all the dynamical properties for
 * the bean instance.
 * <li> dynamicClass - the class for the bean instance.
 * <li> createDate - the date when the bean was created.
 * <li> ownerURI/creatorURI - the URI for the user/entity that create/owns the 
 * bean instance.
 * <li>
 * </ul>
 * 
 * This entity defines the following named querties :
 * <ul>
 * <li> selectDynamicBeanByDynamicClass - it selects all the 
 * <code>DynamicBean</code> that have a certain class, the class is specified 
 * like query argument named "dynamic_class".
 * </ul>
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see DynamicClass
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "selectDynamicBeanByUri",
    query = "SELECT COUNT(db) FROM DynamicBean AS db WHERE db.uri=:bean_uri"),
    
    @NamedQuery(name = "countDynamicBeanByDynamicClass",
    query = "SELECT COUNT(db) FROM DynamicBean AS db WHERE db.dynamicClass=:dynamic_class"),
                        
    @NamedQuery(name = "selectDynamicBeanByDynamicClass",
    query = "SELECT db FROM DynamicBean AS db WHERE db.dynamicClass=:dynamic_class"),
    
    @NamedQuery(name = "selectDynamicBeanByDynamicClassWithProperties",
    query = "SELECT db FROM DynamicBean AS db LEFT JOIN FETCH db.dynamicProperties WHERE db.dynamicClass=:dynamic_class"),
    
    @NamedQuery(name = "selectLastDynamicBeanByDynamicClass",
    query = "SELECT db FROM DynamicBean AS db WHERE db.dynamicClass=:dynamic_class ORDER BY db.createDate ASC"),
    
    @NamedQuery(name = "selectLastDynamicBeanByDynamicClassWithProperties",
    query = "SELECT db FROM DynamicBean AS db LEFT JOIN FETCH db.dynamicProperties WHERE db.dynamicClass=:dynamic_class ORDER BY db.createDate ASC"),
    
    @NamedQuery(name = "selectDynamicBeanByDynamicClassAndVersion",
    query = "SELECT db FROM DynamicBean AS db WHERE db.dynamicClass=:dynamic_class AND db.uriVersion=:uri_version"),
    
    @NamedQuery(name = "selectDynamicBeanByDynamicClassAndVersionWithProperties",
    query = "SELECT db FROM DynamicBean AS db LEFT JOIN FETCH db.dynamicProperties WHERE db.dynamicClass=:dynamic_class AND db.uriVersion=:uri_version"),
    
    @NamedQuery(name = "selectLastDynamicBeanByURI",
    query = "SELECT db FROM DynamicBean AS db WHERE db.uri=:uri"),
    
    @NamedQuery(name = "selectLastDynamicBeanByURIWithProperties",
    query = "SELECT db FROM DynamicBean AS db LEFT JOIN FETCH db.dynamicProperties WHERE db.uri=:uri"),
    
    @NamedQuery(name = "selectDynamicBeanByURIAndVersion",
    query = "SELECT db FROM DynamicBean AS db WHERE db.uri=:uri AND db.uriVersion=:uri_version"),
    
    @NamedQuery(name = "selectDynamicBeanByURIAndVersionWithProperties",
    query = "SELECT db FROM DynamicBean AS db LEFT JOIN FETCH db.dynamicProperties WHERE db.uri=:uri AND db.uriVersion=:uri_version")
})
public class DynamicBean implements Serializable {

    /**                                                                                                                                                                            
     * A version number for this class so that serialization                                                                                                                       
     * can occur without worrying about the underlying class                                                                                                                       
     * changing between serialization and deserialization.                                                                                                                         
     */
    private static final long serialVersionUID = 5165L;

    /**
     * The unique id.
     */
    private Long id;

    /**
     * Human readable name, it does not needs to be unique, it can be null.
     */
    private String name;

    /**
     * Used to identify the last version for this bean.
     */
    private String uri;

    /**
     * The actual version for this bean, it can not be null.
     */
    private Long uriVersion;

    /**
     * All the (dynamic) properties for this bean.
     */
    private Set<DynamicProperty> dynamicProperties;

    /**
     * The class(type) for this bean.
     */
    private DynamicClass dynamicClass;

    /**
     * The date when this bean was created, it can not be null.
     */
    private Date createDate;

    /**
     * The URI for the entity that may own this bean.
     */
    private String ownerURI;

    /**
     * The URI for the entity that create this bean.
     */
    private String creatorURI;

    /**
     * True if this bean can be read.
     */
    private Boolean canRead;

    /**
     * True if this bean can be write (can store information).
     */
    private Boolean canWrite;

    /**
     * True if it can be involved in any kind of activities.
     */
    private Boolean canUse;
    
    /**
     * True if this bean is marked as deleted.
     */
    private Boolean isDeleted;

    /**
     * Builds a <code>DynamicBean</code> instance.
     */
    public DynamicBean() {
        // UNIMPLEMENTED
    }

    /**
     * 
     * @param uri 
     */
    public DynamicBean(String uri) {
        
        if (uri == null) {
            throw new NullPointerException("The URI argumetn must can not be null.");
        }
        
        this.uri = uri;
    }

    public DynamicBean(String uri, String name) {
        if (uri == null) {
            throw new NullPointerException("The URI argumetn must can not be null.");
        }
        
        this.uri = uri;
        this.name = name;
    }

    public DynamicBean(String uri, String name, DynamicClass dynamicClass) {
        this.name = name;
        this.dynamicClass = dynamicClass;
    }
    
    /**
     * Returns the unique id for this class.</br>
     * This value is used exclusive by the database system, it is not 
     * recommendable to build code(logic) that base on this value.
     * 
     * @return  the unique id for this class.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    /**
     * Sets a new value for the unique id for this class.</br>
     * This value is used exclusive by the database system - 
     * <b>Don't use this method</b>. </br>
     * 
     * @param id new value for the unique id for this class
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "dynamicBean", cascade = CascadeType.ALL)
    public Set<DynamicProperty> getDynamicProperties() {
        return dynamicProperties;
    }

    public void setDynamicProperties(Set<DynamicProperty> properties) {
        this.dynamicProperties = properties;
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

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date created) {
        this.createDate = created;
    }

    public String getOwnerURI() {
        return ownerURI;
    }

    public void setOwnerURI(String ownerURI) {
        this.ownerURI = ownerURI;
    }

    public Boolean getCanUse() {
        return canUse;
    }

    public void setCanUse(Boolean canUse) {
        this.canUse = canUse;
    }

    public String getCreatorURI() {
        return creatorURI;
    }

    public void setCreatorURI(String creatorURI) {
        this.creatorURI = creatorURI;
    }

    @Column(nullable = false)
    public Long getUriVersion() {
        return uriVersion;
    }

    public void setUriVersion(Long uriVersion) {
        this.uriVersion = uriVersion;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    

    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer();
        result.append("DynamicBean{" + "uri=");
        result.append(uri);
        result.append(", name=");
        result.append(name);
        result.append(", version=");
        result.append(uriVersion);
        result.append(", dynamicClass=");
        result.append(dynamicClass);
        result.append(", isDeleted=");
        result.append(isDeleted);
        result.append(", created=");
        result.append(createDate);
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
