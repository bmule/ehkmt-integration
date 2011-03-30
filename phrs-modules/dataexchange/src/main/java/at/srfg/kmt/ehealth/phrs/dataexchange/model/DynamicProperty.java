/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : DynamicProperty.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
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
public class DynamicProperty implements Serializable {

    private Long id;
    
    private String name;
    
    private String type;
    
    private Serializable content;
    
    private DynamicBean dynamicBean;

    public DynamicProperty() {
    }

    public DynamicProperty(String name, String type, Serializable content) {
        this.name = name;
        this.type = type;
        this.content = content;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Serializable getContent() {
        return content;
    }

    public void setContent(Serializable content) {
        this.content = content;
    }

    @ManyToOne
    public DynamicBean getDynamicBean() {
        return dynamicBean;
    }

    public void setDynamicBean(DynamicBean dynamicBean) {
        this.dynamicBean = dynamicBean;
    }

    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer();
        result.append("DynamicProperty{name=");
        result.append(name);
        result.append(", type=");
        result.append(type);
        result.append(", content=");
        result.append(content);
        result.append("}");
        
        return result.toString();
    }
    
    
}
