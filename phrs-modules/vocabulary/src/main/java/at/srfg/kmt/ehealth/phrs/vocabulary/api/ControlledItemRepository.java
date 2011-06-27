/*
 * Project :iCardea
 * File : ControlledItemRepository.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.vocabulary.api;

import at.srfg.kmt.ehealth.phrs.vocabulary.model.ControlledItem;
import java.util.Set;

/**
 * Defines all the action tat can be accomplish with <code>ControlledItem</code>
 * instances. Those actions are :
 * <ul>
 * <li> register a new item.
 * <li> update an existing item.
 * <li> prove the item existence.
 * <li> relate items in a tagging relation.The tagging implies two parts a 
 * tagged and a tagging; the tagged item gains a tag applied - the tag is the
 * tagging item.
 * <li> query for items.
 * </ul>
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 * @see ControlledItem
 */
public interface ControlledItemRepository {

    /**
     * Registers a new <code>ControlledItem</code> instance.
     * 
     * @param item the instance to register.
     * @return the result depends on the implementation, but it is acceptable 
     * to consider that the result is true if the item is persisted and false if
     * the item is updated.
     */
    boolean add(ControlledItem item);

    /**
     * Proves the existence for a given item.
     * 
     * @param item the item that existence is to be proved.
     * @return true if the specified item exists already.
     */
    boolean contains(ControlledItem item);

    /**
     * Proves if the underlay persistence layer contains a item with a certain
     * code and code system.
     * 
     * @param codeSystem the code system for the item.
     * @param code the code system for the item.
     * @return true if the specified item exists already.
     */
    boolean contains(String codeSystem, String code);

    /**
     * Removes an item. After this the <code>contains</code> must return false 
     * (for the removed item).
     * 
     * @param item the item to remove.
     * @return the removed item.
     */
    ControlledItem remove(ControlledItem item);

    /**
     * Updates the content for an existent <code>ControlledItem</code>.
     * This method is similar to <code>add</code> method for existing
     * (already) persisted items.
     * 
     * @param item the item to update.
     */
    void update(ControlledItem item);

    /**
     * Returns all the <code>ControlledItem</code> that use a certain code system.
     * 
     * @param codeSystem the code system.
     * @return all the <code>ControlledItem</code> that use a certain code system.
     */
    Set<ControlledItem> getByCodeSystem(String codeSystem);

    /**
     * Searches all the controlled items where the preferred label match 
     * <b>exactly</b> a certain value. 
     * It is not defined here how the system must react if no matches are found;
     * it is acceptable to return an empty set if no match are found.
     * 
     * @param prefLabel the value for the preferred label to be search.
     * @return all the controlled items where the preferred label match 
     * <b>exactly</b> a certain value.
     */
    Set<ControlledItem> getByPrefLabel(String prefLabel);

    /**
     * Searches all the controlled items where the preferred label match 
     * starts with a certain prefix. 
     * It is not defined here how the system must react if no matches are found;
     * it is acceptable to return an empty set if no match are found.
     * 
     * @param prefLabelPrefix the value for the preferred label prefix to be search.
     * @return all the controlled items where the preferred label match 
     * starts with a certain prefix. .
     */
    Set<ControlledItem> getByPrefLabelPrefix(String prefLabelPrefix);

    /**
     * Returns all the <code>ControlledItem</code> that use a certain code system.
     * 
     * @param codeSystem
     * @param code
     * @return 
     */
    ControlledItem getByCodeSystemAndCode(String codeSystem, String code);

    /**
     * Proves the existence for a given item, the item is identified after its
     * code system code - code.
     * 
     * @param codeSystemCode the code system for the item that 
     * existence is to be proved.
     * @param code the code for the item that existence is to be proved.
     */
    boolean itemExists(String codeSystemCode, String code);

    /**
     * Returns all the controlled items tagged with a given tag.
     * It is not defined here how the system must react if no matches are found;
     * it is acceptable to return an empty set if no match are found.
     * 
     * @param tag the involved tag.
     * @return all the controlled items tagged with a given tag.
     */
    Set<ControlledItem> getByTag(ControlledItem tag);

    /**
     * Returns all the tags associated with a certain controlled item.
     * It is not defined here how the system must react if no matches are found;
     * it is acceptable to return an empty set if no match are found.
     * 
     * @param controlledItem the involved controlled item.
     * @return all the tags associated with a certain tag.
     */
    Set<ControlledItem> getTags(ControlledItem controlledItem);

    /**
     * Associates a tag to a given controlled item. The tagging relation
     * involves two parts : 
     * <ul>
     * <li> tagged - the item where the tag is applied
     * <li> tagging - the item where the tag is applied
     * </ul>
     * 
     * @param tagged the item where the tag is applied.
     * @param tagging the item where the tag is applied.
     */
    void tag(ControlledItem tagged, ControlledItem tagging);

    /**
     * Proves if the tag relation exits for the given tagged and tagging items.
     * The tagged is the item that get taggged and the tagging in the tag.
     * 
     * @param tagged the item that get tagged.
     * @param tagging the involved tag.
     * @return true if the <code>tagged</code> item is tagged with a certain tag.
     */
    boolean tagExist(ControlledItem tagged, ControlledItem tagging);

    /**
     * Removes a tag relation for a given controlled item. The tagging relation
     * involves two parts : 
     * <ul>
     * <li> tagged - the item where the tag is applied
     * <li> tagging - the item where the tag is applied
     * </ul>
     * 
     * @param tagged the item where the tag is applied.
     * @param tagging the item where the tag is applied.
     */
    void untag(ControlledItem tagged, ControlledItem tagging);

    /**
     * Removes all the tag relation for a given controlled item.
     * 
     * @param tagged 
     */
    void removeAllTags(ControlledItem tagged);
}
