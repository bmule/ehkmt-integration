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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


/**
 * This entity is used to a class.  A class is used to define types.
 * A type can be defined by a set of properties and an unique URI. The property
 * are used to define properties types, each property type can must have a name,
 * a URI and a type. Each property type can also have none, one or more 
 * metadata(s). <br>
 * This entity defines two named queries : 
 * <ul>
 * <li> selectDynamicClassByURI - search all the <code>DynamicClass</code> where 
 * the URI match exacts a given URI.
 * <li> selectDynamicClassByURIPrefix - search all the <code>DynamicClass</code> where 
 * the URI match starts with a given prefix.
 * <li> selectPropertyForClassAndMedataXXX - search all the properties
 * for a given where where the property has a given metadata.
 * </ul> <br>
 * FIXME : find a better way to avoid the two time join but care about the old
 * Knutism "premature optimization is the root of all evil".
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see DynamicPropertyType
 */
@NamedQueries({
    @NamedQuery(name = "selectDynamicClassByURI",
                query = "SELECT dc FROM DynamicClass AS dc WHERE dc.uri=:uri"),
    @NamedQuery(name = "selectDynamicClassByURIPrefix",
                query = "SELECT dc FROM DynamicClass AS dc WHERE dc.uri LIKE uri"),
    @NamedQuery(name="selectPropertyForClassAndMedataByUri", 
                query="SELECT propType FROM DynamicClass AS dc "
                    + " JOIN dc.propertyTypes AS propType "
                    + " JOIN propType.metadatas AS meta_data "
                    + " WHERE dc.uri=:class_uri AND"
                    +       " meta_data.uri=:meta_data_uri"), 
    @NamedQuery(name="selectPropertyForClassAndMedataByName", 
                query="SELECT propType FROM DynamicClass AS dc "
                    + " JOIN dc.propertyTypes AS propType "
                    + " JOIN propType.metadatas AS meta_data "
                    + " WHERE dc.uri=:class_uri AND"
                    +       " meta_data.name=:meta_data_name")
})
@Entity
public class DynamicClass implements Serializable {

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
     * Unique identifier for this class, it can not be null, it must be 
     * <b>unique</b>.
     */
    private String uri;

    /**
     * Human readable text, it can be null and it does not requires to exists 
     * (it can be null).
     */
    private String name;

    /**
     * The properties for this class.
     */
    private Set<DynamicPropertyType> propertyTypes;

    /**
     * Builds a <code>DynamicClass</code> instance.
     */
    public DynamicClass() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a <code>DynamicClass</code> instance with a given URI.
     * 
     * @param uri the URI for this class, it can not be null.
     * @throws NullPointerException if the <code>uri</code> argumetn is null.
     */
    public DynamicClass(String uri) {

        if (uri == null) {
            throw new NullPointerException("The URI argumetn must can not be null.");
        }

        this.uri = uri;
    }

    /**
     * Builds a <code>DynamicClass</code> for a given URI and name.
     * 
     * @param uri the URI for this class, it can not be null.
     * @param name the name for this class, it can be null.
     * @throws NullPointerException if the <code>uri</code> argumetn is null.
     */
    public DynamicClass(String uri, String name) {

        if (uri == null) {
            throw new NullPointerException("The URI argumetn must can not be null.");
        }

        this.uri = uri;
        this.name = name;
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

    @Column(unique = true, nullable = false)
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @OneToMany(mappedBy = "dynamicClass")
    public Set<DynamicPropertyType> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(Set<DynamicPropertyType> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("DynamicClass{uri=");
        result.append(uri);
        result.append(", name=");
        result.append(name);
        result.append("}");
        
        return result.toString();
    }
}
