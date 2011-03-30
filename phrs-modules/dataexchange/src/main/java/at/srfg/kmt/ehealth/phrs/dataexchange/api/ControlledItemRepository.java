/*
 * Project :iCardea
 * File : ControlledItemRepository.java
 * Encoding : UTF-8
 * Date : Mar 22, 2011
 * User : Mihai Radulescu
 */


package at.srfg.kmt.ehealth.phrs.dataexchange.api;

import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
import java.util.Set;
import javax.persistence.Query;

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
     * Returns all the <code>ControlledItem</code> that use a certain code system.
     * 
     * @param codeSystem
     * @param code
     * @return 
     */
    ControlledItem getByCodeSystemAndCode(String codeSystem, String code);
    Set<ControlledItem> get(Query query);
    Set<ControlledItem> getByTag(ControlledItem tag);
    Set<ControlledItem> getTags(ControlledItem controlledItem);

    void tag(ControlledItem tagged, ControlledItem tagging);
    void untag(ControlledItem tagged, ControlledItem tagging);
    void removeAllTags(ControlledItem tagged);
}
