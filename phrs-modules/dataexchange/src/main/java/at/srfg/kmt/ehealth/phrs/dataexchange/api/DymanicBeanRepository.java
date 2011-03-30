/*
 * Project :iCardea
 * File : DymanicBeanRepository.java
 * Encoding : UTF-8
 * Date : Mar 29, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;

import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import java.util.Set;

/**
 * Defines a set of action that can be accomplished on <code>DynamicBean</code>
 * instances.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface DymanicBeanRepository {

    /**
     * Persists a given DynamicBean instance.
     *
     * @param bean the instance to add.
     */
    void add(DynamicBean bean);

    /**
     * Removes (from the underlaying persisntece layer) a certain bean instance.
     * 
     * @param bean the bean instance to be removed.
     */
    void remove(DynamicBean bean);

    /**
     * Removes the last bean instance version for a certain class.
     * 
     * @param clazz the class for the bean to be removed.
     */
    void removeLast(DynamicClass clazz);

    /**
     * Removes all the existent bean versions for a given class.
     * 
     * @param clazz the class for the bean to be removed.
     */
    void removeAll(DynamicClass clazz);

    boolean contains(DynamicBean bean);
    
    /**
     * Proves the presence for any bean with a given class.
     * 
     * @param clazz the class for the bean which presence is to be proved.
     * @see #containsForClassURI(java.lang.String) 
     */
    boolean contains(DynamicClass clazz);

    /**
     * Proves the presence for any bean with a given class, the class is 
     * identified after its URI.
     * 
     * @param classURI the class URI for the bean which presence is to be proved.
     * @see #contains(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     */
    boolean containsForClassURI(String classURI);

    /**
     * Returns the last version for a dynamic bean that has a certain class.
     * If there is no bean for the given class this method can return null.
     * 
     * @param clazz the class for the bean to search.
     * @return the last version for a dynamic bean that has a certain class.
     * @see #getForClass(java.lang.String) 
     */
    DynamicBean getForClass(DynamicClass clazz);

    /**
     * Returns the last version for a dynamic bean that has a certain class, the
     * class is identified after its URI.
     * If there is no bean for the given class this method can return null.
     * 
     * @param clazz the class for the bean to search.
     * @return the last version for a dynamic bean that has a certain class.
     * @see #getForClass(java.lang.String) 
     */
    DynamicBean getForClass(String classURI);

    /**
     * Returns all versions for a dynamic bean that has a certain class.
     * If there is no bean for the given class this method can return an empty
     * set.
     * 
     * @param clazz the class for the bean to search.
     * @return a set that contains all the versions for a dynamic bean that has
     * a certain class.
     * @see #getForClass(java.lang.String) 
     */
    Set<DynamicBean> getAllForClass(DynamicClass clazz);

    /**
     * Returns all versions for a dynamic bean that has a certain class, the
     * class is identified after its URI.
     * If there is no bean for the given class this method can return an empty
     * set.
     * 
     * @param clazz the class for the bean to search.
     * @return a set that contains all the versions for a dynamic bean that has
     * a certain class (identified after the URI).
     * @see #getAllForClass(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     */
    Set<DynamicBean> getAllForClass(String classURI);
}
