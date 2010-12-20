//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.11.23 at 04:13:06 PM EET 
//


package tr.com.srdc.icardea.consenteditor.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{model.consenteditor.srdc.com.tr}non_empty_string"/>
 *         &lt;element name="name" type="{model.consenteditor.srdc.com.tr}non_empty_string"/>
 *         &lt;element name="description" type="{model.consenteditor.srdc.com.tr}non_empty_string" minOccurs="0"/>
 *         &lt;element ref="{model.consenteditor.srdc.com.tr}members" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isUserDefined" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "name",
    "description",
    "members"
})
@XmlRootElement(name = "group")
@Entity(name = "Group")
@Table(name = "GROUP_")
@Inheritance(strategy = InheritanceType.JOINED)
public class Group
    implements Serializable, Equals, HashCode
{

    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String name;
    protected String description;
    protected Members members;
    @XmlAttribute(name = "isUserDefined", namespace = "model.consenteditor.srdc.com.tr")
    protected Boolean isUserDefined;
    @XmlTransient
    protected Long hjid;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "ID", length = 255)
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "NAME_", length = 255)
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "DESCRIPTION", length = 255)
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the members property.
     * 
     * @return
     *     possible object is
     *     {@link Members }
     *     
     */
    @ManyToOne(targetEntity = Members.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "MEMBERS_GROUP__HJID")
    public Members getMembers() {
        return members;
    }

    /**
     * Sets the value of the members property.
     * 
     * @param value
     *     allowed object is
     *     {@link Members }
     *     
     */
    public void setMembers(Members value) {
        this.members = value;
    }

    /**
     * Gets the value of the isUserDefined property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Basic
    @Column(name = "ISUSERDEFINED")
    public boolean isIsUserDefined() {
        if (isUserDefined == null) {
            return false;
        } else {
            return isUserDefined;
        }
    }

    /**
     * Sets the value of the isUserDefined property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsUserDefined(Boolean value) {
        this.isUserDefined = value;
    }

    /**
     * 
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    @Id
    @Column(name = "HJID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getHjid() {
        return hjid;
    }

    /**
     * 
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHjid(Long value) {
        this.hjid = value;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof Group)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Group that = ((Group) object);
        {
            String lhsId;
            lhsId = this.getId();
            String rhsId;
            rhsId = that.getId();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "id", lhsId), LocatorUtils.property(thatLocator, "id", rhsId), lhsId, rhsId)) {
                return false;
            }
        }
        {
            String lhsName;
            lhsName = this.getName();
            String rhsName;
            rhsName = that.getName();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "name", lhsName), LocatorUtils.property(thatLocator, "name", rhsName), lhsName, rhsName)) {
                return false;
            }
        }
        {
            String lhsDescription;
            lhsDescription = this.getDescription();
            String rhsDescription;
            rhsDescription = that.getDescription();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "description", lhsDescription), LocatorUtils.property(thatLocator, "description", rhsDescription), lhsDescription, rhsDescription)) {
                return false;
            }
        }
        {
            Members lhsMembers;
            lhsMembers = this.getMembers();
            Members rhsMembers;
            rhsMembers = that.getMembers();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "members", lhsMembers), LocatorUtils.property(thatLocator, "members", rhsMembers), lhsMembers, rhsMembers)) {
                return false;
            }
        }
        {
            boolean lhsIsUserDefined;
            lhsIsUserDefined = this.isIsUserDefined();
            boolean rhsIsUserDefined;
            rhsIsUserDefined = that.isIsUserDefined();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "isUserDefined", lhsIsUserDefined), LocatorUtils.property(thatLocator, "isUserDefined", rhsIsUserDefined), lhsIsUserDefined, rhsIsUserDefined)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = 1;
        {
            String theId;
            theId = this.getId();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "id", theId), currentHashCode, theId);
        }
        {
            String theName;
            theName = this.getName();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "name", theName), currentHashCode, theName);
        }
        {
            String theDescription;
            theDescription = this.getDescription();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "description", theDescription), currentHashCode, theDescription);
        }
        {
            Members theMembers;
            theMembers = this.getMembers();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "members", theMembers), currentHashCode, theMembers);
        }
        {
            boolean theIsUserDefined;
            theIsUserDefined = this.isIsUserDefined();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "isUserDefined", theIsUserDefined), currentHashCode, theIsUserDefined);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
