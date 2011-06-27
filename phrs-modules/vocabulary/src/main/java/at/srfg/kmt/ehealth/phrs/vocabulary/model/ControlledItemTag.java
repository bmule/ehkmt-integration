/*
 * Project :iCardea
 * File : ControlledItemTag.java
 * Encoding : UTF-8
 * Date : Mar 23, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.vocabulary.model;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;


/**
 * Defines a relation between two controlled items. 
 * One item is used to tag and other one. 
 * The one that get tagged in named <b>tagged</b> and the one tag item is named
 * <b>tagging</b>.
 * This entities it defines the following named queries :
 * <ul>
 * <li> selectControlledItemTagByTagging - returns all controlled item for a given tag. 
 * This query returns one more <code>ControlledItem</code> instances.
 * <li> selectControlledItemTagByTagged - returns tags that are applied on a given controlled item.
 * This query returns one more <code>ControlledItem</code> instances.
 * <li> selectControlledItemTagByTaggedAndTagging - returns the tag relations for
 * a given tagged and tagging arguments. This query returns one 
 * <code>ControlledItemTag</code> instance.
 * </ul>
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Entity
@NamedQueries({@NamedQuery(name="selectControlledItemTagByTagging", 
                           query="SELECT tag.tagged FROM ControlledItemTag AS tag WHERE tag.tagging=:tagging"),
               @NamedQuery(name="selectControlledItemTagByTagged", 
                           query="SELECT tag.tagging FROM ControlledItemTag AS tag WHERE tag.tagged=:tagged"),
               @NamedQuery(name="selectControlledItemTagByTaggedAndTagging", 
                           query="SELECT tag FROM ControlledItemTag AS tag WHERE tag.tagged=:tagged AND tag.tagging=:tagging"),
               @NamedQuery(name="countTagByTaggedAndTagging", 
                           query="SELECT COUNT(tag) FROM ControlledItemTag AS tag WHERE tag.tagged=:tagged AND tag.tagging=:tagging")
})

public class ControlledItemTag implements Serializable {

    private Long id;
    private ControlledItem tagged;
    private ControlledItem tagging;

    public ControlledItemTag() {
    }

    public ControlledItemTag(ControlledItem tagged, ControlledItem tagging) {
        this.tagged = tagged;
        this.tagging = tagging;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    public ControlledItem getTagged() {
        return tagged;
    }

    public void setTagged(ControlledItem tagged) {
        this.tagged = tagged;
    }

    @OneToOne
    public ControlledItem getTagging() {
        return tagging;
    }

    public void setTagging(ControlledItem tagging) {
        this.tagging = tagging;
    }

    @Override
    public String toString() {
        final StringBuffer result = 
                new StringBuffer("ControlledItemTag{tagged=");
        result.append(tagged);
        
        result.append(", tagging=");
        result.append(tagging);
        result.append("}");
        
        return result.toString();
    }
}
