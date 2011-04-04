/*
 * Project :iCardea
 * File : DymanicBeanRepository.java
 * Encoding : UTF-8
 * Date : Mar 29, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;


import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.DynaBean;


/**
 * Defines a set of action that can be accomplished on <code>DynamicBean</code>
 * instances.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface DynamicBeanRepository {

    /**
     * Persists a given <code>DynaBean</code> instance.
     *
     * @param bean the instance to add.
     */
    void add(DynaBean bean);

    /**
     * Removes (from the underlaying persistence layer) a certain bean instance.
     * 
     * @param bean the bean instance to be removed.
     */
    void remove(DynaBean bean);

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

    /**
     * Proves if the this repository contains a given bean.
     * 
     * @param bean the bean which presence is to be proved.
     * @return true if the this repository contains a given bean.
     */
    boolean contains(DynaBean bean);

    /**
     * Proves if this repository contains a bean (with any class) with a
     * bean URI.
     * 
     * @param beanURI the URI for the bean which presence is to be proved.
     */
    boolean contains(String beanURI);

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
    //DynamicBean getForClass(DynamicClass clazz);
    /**
     * Returns the last version for a dynamic bean that has a certain class.
     * If there is no bean for the given class this method can return null.
     * 
     * @param clazz the class for the bean to search.
     * @return the last version for a dynamic bean that has a certain class.
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @see #getForClass(java.lang.String) 
     */
    DynaBean getForClass(DynamicClass clazz) 
            throws DynamicPropertyTypeException, DynaClassException;;

    /**
     * Returns the last version for a dynamic bean that has a certain class, the
     * class is identified after its URI.
     * If there is no bean for the given class this method can return null.
     * 
     * @param clazz the class for the bean to search.
     * @return the last version for a dynamic bean that has a certain class.
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @see #getForClass(java.lang.String) 
     */
    DynaBean getForClass(String classURI) 
            throws DynamicPropertyTypeException, DynaClassException;;

    /**
     * Returns all versions for a dynamic bean that has a certain class.
     * If there is no bean for the given class this method can return an empty
     * set.
     * 
     * @param clazz the class for the bean to search.
     * @return a set that contains all the versions for a dynamic bean that has
     * a certain class.
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @see #getForClass(java.lang.String) 
     */
    Set<DynaBean> getAllForClass(DynamicClass clazz) 
            throws DynamicPropertyTypeException, DynaClassException;

    /**
     * Returns all versions for a dynamic bean that has a certain class, the
     * class is identified after its URI.
     * If there is no bean for the given class this method can return an empty
     * set.
     * 
     * @param clazz the class for the bean to search.
     * @return a set that contains all the versions for a dynamic bean that has
     * a certain class (identified after the URI).
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @see #getAllForClass(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     */
    Set<DynaBean> getAllForClass(String classURI) 
            throws DynamicPropertyTypeException, DynaClassException;

    /**
     * Returns a bean that has a certain URI.
     * 
     * @param beanURI 
     * @return 
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     */
    DynaBean getForURI(String beanURI) 
            throws DynamicPropertyTypeException, DynaClassException;

    /**
     * Returns a certain bean that has a certain class and a certain version.</br>
     * <b>Note : </b> The semantic for version is defined in the implementation.
     * 
     * @param dynamicClass the class for the bean to search.
     * @param version the version.
     * @return a certain bean that has a certain class and a certain version.
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     */
    DynaBean getForClassAndVersion(DynamicClass dynamicClass, long version) 
            throws DynamicPropertyTypeException, DynaClassException;

    /**
     * Returns a certain bean that has a certain class (the class is identify by
     * the class URI).</br>
     * <b>Note : </b> The semantic for version is defined in the implementation.
     * 
     * @param dynamicClass the class URI for the bean to search.
     * @param version the version.
     * @return a certain bean that has a certain class and a certain version
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     */
    DynaBean getForClassAndVersion(String classURI, long version) 
            throws DynamicPropertyTypeException, DynaClassException;

    /**
     * Returns all beans that match the given query by example.
     * The semantic for the <code>qbe</code> argument is 
     * defined in the implementation.
     * 
     * @param query the query by example parameter map.
     * @return all beans that match the given query.
     * @throws DynamicPropertyTypeException if at least one properties involved 
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     * @throws DynaClassException if the specified <code>DynamicClass</code> can
     * not be used from any reason (e.g. class not found in the classpath).
     * in the specified <code>DynamicClass</code> can not be accessed from any
     * reason.
     */
    List<DynaBean> get(Map<String, ?> qbe) 
            throws DynamicPropertyTypeException, DynaClassException;
}
