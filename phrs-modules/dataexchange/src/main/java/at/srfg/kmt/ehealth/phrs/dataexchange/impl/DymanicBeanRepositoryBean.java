/*
 * Project :iCardea
 * File : DymanicBeanRepositoryBean.java
 * Encoding : UTF-8
 * Date : Mar 29, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import at.srfg.kmt.ehealth.phrs.dataexchange.api.DymanicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Stateless
@Local(DymanicBeanRepository.class)
public class DymanicBeanRepositoryBean implements DymanicBeanRepository {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.DymanicBeanRepository</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DymanicBeanRepository.class);
    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    /**
     * Registers the given <code>DynamicBean</code> instance in to the 
     * repository. If the 
     * 
     * @param bean the <code>DynamicBean</code> instance to be register, 
     * it can not be null.
     * @throws NullPointerException if the <code>bean</code> argument is null.
     */
    @Override
    public void add(DynamicBean bean) {
        if (bean == null) {
            final NullPointerException nullException =
                    new NullPointerException("The bean argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final boolean contains = entityManager.contains(bean);

        if (contains) {
            entityManager.merge(bean);
            return;
        }

        final Long id = bean.getId();
        if (id == null) {
            final DynamicClass dynamicClass = bean.getDynamicClass();
            attachtoContext(dynamicClass);
            entityManager.persist(bean);
            return;
        }

        entityManager.merge(bean);
    }

    /**
     * Attached to the current persistent context the given 
     * <code>DynamicClass</code>.
     * 
     * @param dynamicClass the <code>DynamicClass</code> to attach.
     */
    private void attachtoContext(DynamicClass dynamicClass) {
        final boolean contains = entityManager.contains(dynamicClass);
        if (contains) {
            return;
        }

        final Long id = dynamicClass.getId();
        if (id == null) {
            entityManager.persist(dynamicClass);
            return;
        }

        entityManager.find(DynamicClass.class, id);
    }

    /**
     * Proves if the underlying persistence layer contains at least one 
     * dynamic bean with the given class.
     * 
     * @param clazz the  bean type(class) which presence is to be proved, 
     * it can not be null.
     * @return true if the the underlying persistence layer contains at least one 
     * dynamic bean with the given class.
     * @throws NullPointerException if the <code>bean</code> argument is null.
     * @see #containsForClassURI(java.lang.String) 
     */
    @Override
    public final boolean contains(DynamicClass clazz) {
        final Set<DynamicBean> allForClass = getAllForClass(clazz);
        return !allForClass.isEmpty();
    }

    @Override
    public boolean contains(DynamicBean bean) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Proves if the underlying persistence layer contains at least one 
     * dynamic bean with the given class, the class is indentified after its U.
     * 
     * @param clazz the URI for bean type(class) which presence is to be proved, 
     * it can not be null.
     * @return true if the the underlying persistence layer contains at least one 
     * dynamic bean with the given class.
     * @throws NullPointerException if the <code>bean</code> argument is null.
     * @see #containsForClassURI(java.lang.String) 
     */
    @Override
    public boolean containsForClassURI(String classURI) {
        if (classURI == null) {
            final NullPointerException nullException =
                    new NullPointerException("The classURI argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        // mihai : this functionality exist already in the DunaClassRepository
        // but I dont wnat to create a dependedecy
        final Query query =
                entityManager.createNamedQuery("selectDynamicClassByURI");
        query.setParameter("dynamic_class", classURI);

        final DynamicClass clazz;
        try {
            clazz = (DynamicClass) query.getSingleResult();
        } catch (NoResultException noResultException) {
            return false;
        }

        final boolean result = contains(clazz);
        return result;
    }

    /**
     * Returns all the registered beans for a given type. 
     * If there are no registered beans for this type then this method returns 
     * an empty set.
     * 
     * @param clazz the  bean type(class) to search, it can not be null.
     * @return all the registered beans for a given type or an empty set if
     * the underlying persistence layer does not contains any bean from the
     * specified type.
     * @throws NullPointerException if the <code>clazz</code> argument is null.
     * @see #getAllForClass(java.lang.String)     
     */
    @Override
    public final Set<DynamicBean> getAllForClass(DynamicClass clazz) {

        if (clazz == null) {
            final NullPointerException nullException =
                    new NullPointerException("The clazz argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query =
                entityManager.createNamedQuery("selectDynamicBeanByDynamicClass");
        query.setParameter("dynamic_class", clazz);

        final List resultList = query.getResultList();

        final Set<DynamicBean> result = new HashSet<DynamicBean>();
        result.addAll(resultList);
        return result;
    }

    /**
     * Returns all the registered beans for a given type, the type is 
     * indentified after its URI.
     * If there are no registered beans for this type then this method returns 
     * an empty set.
     * 
     * @param clazz the bean type(class) URI to search, it can not be null.
     * @return all the registered beans for a given type or an empty set if
     * the underlying persistence layer does not contains any bean from the
     * specified type.
     * @throws NullPointerException if the <code>classURI</code> argument is null.
     * @see #getForClass(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     */
    @Override
    public Set<DynamicBean> getAllForClass(String classURI) {
        if (classURI == null) {
            final NullPointerException nullException =
                    new NullPointerException("The classURI argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        // mihai : this functionality exist already in the DunaClassRepository
        // but I dont wnat to create a dependedecy
        final Query query =
                entityManager.createNamedQuery("selectDynamicClassByURI");
        query.setParameter("dynamic_class", classURI);


        final DynamicClass clazz;
        try {
            clazz = (DynamicClass) query.getSingleResult();
        } catch (NoResultException noResultException) {
            LOGGER.warn(noResultException.getMessage(), noResultException);
            return new HashSet<DynamicBean>();
        }

        final Set<DynamicBean> result = getAllForClass(clazz);
        return result;

    }

    /**
     * Returns the last registered bean instance for the given type or null if
     * this repository does not contain any beans for the given type (class).
     * 
     * @param clazz the bean class(type) to be search, it can not be null.
     * @return the last registered bean instance for the given type or null if
     * this repository does not contain any beans for the given type (class).
     * @throws NullPointerException if the <code>classURI</code> argument is null.
     * @see #getForClass(java.lang.String) 
     */
    @Override
    public DynamicBean getForClass(DynamicClass clazz) {
        if (clazz == null) {
            final NullPointerException nullException =
                    new NullPointerException("The clazz argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        // selectLastDynamicBeanByDynamicClassWithProperties
        final Query query =
                entityManager.createNamedQuery("selectLastDynamicBeanByDynamicClassWithProperties");
        query.setParameter("dynamic_class", clazz);

        final List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }

        final DynamicBean result = (DynamicBean) resultList.get(0);
        return result;
    }

    /**
     * Returns the last registered bean instance for the given type(URI located)
     * or null if this repository does not contain any beans for the given type
     * (class). The type is identified after its unique id.
     * 
     * @param clazz the bean class(type) to be search, it can not be null.
     * @return the last registered bean instance for the given type or null if
     * this repository does not contain any beans for the given type (class).
     * @throws NullPointerException if the <code>classURI</code> argument is null.
     * @see #getForClass(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     */
    @Override
    public DynamicBean getForClass(String classURI) {
        if (classURI == null) {
            final NullPointerException nullException =
                    new NullPointerException("The clazz argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query =
                entityManager.createNamedQuery("selectDynamicClassByURI");
        query.setParameter("dynamic_class", classURI);

        // mihai : this functionality exist already in the DunaClassRepository
        // but I dont wnat to create a dependedecy
        final DynamicClass clazz;
        try {
            clazz = (DynamicClass) query.getSingleResult();
        } catch (NoResultException noResultException) {
            LOGGER.warn(noResultException.getMessage(), noResultException);
            return null;
        }
        
        final DynamicBean result = getForClass(clazz);
        return result;
    }

    @Override
    public void remove(DynamicBean bean) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAll(DynamicClass clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeLast(DynamicClass clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
