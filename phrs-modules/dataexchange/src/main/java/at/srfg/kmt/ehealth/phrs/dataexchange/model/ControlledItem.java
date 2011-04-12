/*
 * Project :iCardea
 * File : ControlledItem.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
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
 * Defines a controlled item.
 * A controlled item represents an item that belongs in to a controlled vocabulary.
 * It can be described with at least two components : code and code system.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "selectControlledItemByCodeSystem", query="SELECT ci FROM ControlledItem AS ci WHERE ci.codeSystem= :code_system"),
    @NamedQuery(name = "selectControlledItemCodeSystemAndCode", query="SELECT ci FROM ControlledItem AS ci WHERE ci.codeSystem= :code_system AND ci.code= :code"),
    @NamedQuery(name = "selectControlledItemByPrefLabel", 
                query="SELECT ci FROM ControlledItem AS ci WHERE ci.prefLabel=:pref_label"),
    @NamedQuery(name = "selectControlledItemByPrefLabelPrefix", 
                query="SELECT ci FROM ControlledItem AS ci WHERE ci.prefLabel LIKE :pref_label_prefix")
})
public class ControlledItem implements Serializable {

    private Long id;
    private String codeSystem;
    private String codeSystemName;
    private String code;
    private String prefLabel;
    private Set<AlternateLabel> alternativeLabels;

    public ControlledItem() {
    }

    public ControlledItem(String codeSystem, String code) {
        this.codeSystem = codeSystem;
        this.code = code;
    }

    public ControlledItem(String codeSystem, String codeSystemName, String code) {
        this.codeSystem = codeSystem;
        this.codeSystemName = codeSystemName;
        this.code = code;
    }

    public ControlledItem(String codeSystem, String codeSystemName, String code, String prefLabel) {
        this.codeSystem = codeSystem;
        this.codeSystemName = codeSystemName;
        this.code = code;
        this.prefLabel = prefLabel;
    }
    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public void setCodeSystemName(String codeSystemName) {
        this.codeSystemName = codeSystemName;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    @OneToMany
    public Set<AlternateLabel> getAlternativeLabels() {
        return alternativeLabels;
    }

    public void setAlternativeLabels(Set<AlternateLabel> alternativeLabels) {
        this.alternativeLabels = alternativeLabels;
    }

    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer("ControlledItem{codeSystem=");
        result.append(codeSystem);
        result.append(", codeSystemName=");
        result.append(codeSystemName);
        result.append(", code=");
        result.append(code);
        
        result.append(", prefLabel=");
        result.append(prefLabel);
        result.append("}");
        
        return  result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ControlledItem other = (ControlledItem) obj;
        if ((this.codeSystem == null) ? (other.codeSystem != null) : !this.codeSystem.equals(other.codeSystem)) {
            return false;
        }
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        // FIXME : use a right hash here !
        int hash = 7;
        return hash;
    }
}
