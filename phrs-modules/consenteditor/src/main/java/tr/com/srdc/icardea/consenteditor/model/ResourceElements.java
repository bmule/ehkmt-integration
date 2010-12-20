//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.11.23 at 04:13:06 PM EET 
//


package tr.com.srdc.icardea.consenteditor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
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
 *         &lt;element ref="{model.consenteditor.srdc.com.tr}resourceElement" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="patientId" type="{model.consenteditor.srdc.com.tr}non_empty_string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "resourceElement"
})
@XmlRootElement(name = "resourceElements")
@Entity(name = "ResourceElements")
@Table(name = "RESOURCEELEMENTS")
@Inheritance(strategy = InheritanceType.JOINED)
public class ResourceElements
    implements Serializable, Equals, HashCode
{

    @XmlElement(required = true)
    protected List<ResourceElement> resourceElement;
    @XmlAttribute(name = "patientId", namespace = "model.consenteditor.srdc.com.tr")
    protected String patientId;
    @XmlTransient
    protected Long hjid;

    /**
     * Gets the value of the resourceElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceElement }
     * 
     * 
     */
    @OneToMany(targetEntity = ResourceElement.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "RESOURCEELEMENT_RESOURCEELEM_0")
    public List<ResourceElement> getResourceElement() {
        if (resourceElement == null) {
            resourceElement = new ArrayList<ResourceElement>();
        }
        return this.resourceElement;
    }

    /**
     * 
     * 
     */
    public void setResourceElement(List<ResourceElement> resourceElement) {
        this.resourceElement = resourceElement;
    }

    /**
     * Gets the value of the patientId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Basic
    @Column(name = "PATIENTID", length = 255)
    public String getPatientId() {
        return patientId;
    }

    /**
     * Sets the value of the patientId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatientId(String value) {
        this.patientId = value;
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
        if (!(object instanceof ResourceElements)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final ResourceElements that = ((ResourceElements) object);
        {
            List<ResourceElement> lhsResourceElement;
            lhsResourceElement = this.getResourceElement();
            List<ResourceElement> rhsResourceElement;
            rhsResourceElement = that.getResourceElement();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "resourceElement", lhsResourceElement), LocatorUtils.property(thatLocator, "resourceElement", rhsResourceElement), lhsResourceElement, rhsResourceElement)) {
                return false;
            }
        }
        {
            String lhsPatientId;
            lhsPatientId = this.getPatientId();
            String rhsPatientId;
            rhsPatientId = that.getPatientId();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "patientId", lhsPatientId), LocatorUtils.property(thatLocator, "patientId", rhsPatientId), lhsPatientId, rhsPatientId)) {
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
            List<ResourceElement> theResourceElement;
            theResourceElement = this.getResourceElement();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "resourceElement", theResourceElement), currentHashCode, theResourceElement);
        }
        {
            String thePatientId;
            thePatientId = this.getPatientId();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "patientId", thePatientId), currentHashCode, thePatientId);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

}
