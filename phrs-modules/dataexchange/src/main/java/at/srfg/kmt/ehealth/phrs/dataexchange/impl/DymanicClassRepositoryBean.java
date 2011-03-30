/*
 * Project :iCardea
 * File : DymanicClassRepositoryBean.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import at.srfg.kmt.ehealth.phrs.dataexchange.api.DymanicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
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
@Local(DymanicClassRepository.class)
public class DymanicClassRepositoryBean implements DymanicClassRepository {

    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.DymanicClassRepositoryBean</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DymanicClassRepositoryBean.class);

    @Override
    public DynamicClass create() {
        final DynamicClass dynamicClass = new DynamicClass();
        entityManager.persist(dynamicClass);

        return dynamicClass;
    }

    @Override
    public DynamicClass create(String uri) {

        if (uri == null || uri.isEmpty()) {
            final NullPointerException nullException =
                    new NullPointerException("The uri argument can not be null or an empty string.");
            LOGGER.error(nullException.getMessage(), nullException);
        }

        final DynamicClass dynamicClass = new DynamicClass(uri);
        entityManager.persist(dynamicClass);

        return dynamicClass;
    }

    @Override
    public DynamicClass create(String name, String uri) {
        final DynamicClass dynamicClass = new DynamicClass(uri, name);
        entityManager.persist(dynamicClass);

        return dynamicClass;

    }

    @Override
    public boolean exits(String uri) {
        final Query query =
                entityManager.createNamedQuery("selectDynamicClassByURI");
        query.setParameter("uri", uri);
        try {
            final DynamicClass result = (DynamicClass) query.getSingleResult();
        } catch (NoResultException exception) {
            return false;
        }

        return true;
    }

    @Override
    public boolean exits(DynamicClass dynamicClass) {
        final boolean contains = entityManager.contains(dynamicClass);
        if (contains) {
            return true;
        }

        final DynamicClass result =
                entityManager.find(DynamicClass.class, dynamicClass.getId());
        return result != null;
    }

    @Override
    public DynamicClass remove(String uri) {
        final Query query =
                entityManager.createNamedQuery("selectDynamicClassByURI");
        query.setParameter("uri", uri);
        final DynamicClass result = (DynamicClass) query.getSingleResult();

        entityManager.remove(result);
        return result;
    }

    @Override
    public DynamicClass remove(DynamicClass dynamicClass) {
        final boolean contains = entityManager.contains(dynamicClass);
        if (!contains) {
            final DynamicClass managedClass = entityManager.merge(dynamicClass);
            entityManager.remove(managedClass);
            return managedClass;
        }

        final DynamicClass find =
                entityManager.find(DynamicClass.class, dynamicClass.getId());
        entityManager.remove(find);

        return find;
    }

    @Override
    public DynamicClass get(String uri) {
        final Query query =
                entityManager.createNamedQuery("selectDynamicClassByURI");
        query.setParameter("uri", uri);
        final DynamicClass result = (DynamicClass) query.getSingleResult();
        // I do this to be sure that all the lazy init. fields are fetch
        fetch(result);

        return result;
    }

    private void fetch(final DynamicClass result) {
        // I fetch all the lazy relations
        final Set<DynamicPropertyType> propertyTypes = result.getPropertyTypes();
        for (DynamicPropertyType type : propertyTypes) {
            type.getMetadatas().size();
        }
    }

    @Override
    public boolean persist(DynamicClass dynamicClass) {
        final boolean contains = entityManager.contains(dynamicClass);
        final boolean areTypesSaved = persistDynamicPropertyType(dynamicClass);
        if (!areTypesSaved) {
            if (!contains) {
                entityManager.persist(dynamicClass);
            } else {
                entityManager.merge(dynamicClass);
            }
        }

        return !contains;
    }

    private boolean persistDynamicPropertyType(DynamicClass dynamicClass) {
        final Set<DynamicPropertyType> propertyTypes =
                dynamicClass.getPropertyTypes();
        for (DynamicPropertyType propertyType : propertyTypes) {
            final boolean wasPersisted = persistDynamicPropertyMetadatas(propertyType);
            if (!wasPersisted) {
                // The metadata properties also persist the related property 
                // type.
                final boolean contains = entityManager.contains(propertyType);
                if (!contains) {
                    entityManager.persist(propertyType);
                } else {
                    entityManager.merge(propertyType);
                }

            }
        }

        final boolean isEmpty = propertyTypes.isEmpty();
        return !isEmpty;
    }

    private boolean persistDynamicPropertyMetadatas(DynamicPropertyType propertyType) {
        final Set<DynamicPropertyMetadata> metadatas = propertyType.getMetadatas();

        if (metadatas == null || metadatas.isEmpty()) {
            return false;
        }

        for (DynamicPropertyMetadata metadata : metadatas) {
            final boolean contains = entityManager.contains(metadata);
            if (!contains) {
                entityManager.persist(metadata);
            } else {
                entityManager.merge(metadata);
            }
        }

        final boolean isEmpty = metadatas.isEmpty();
        return !isEmpty;
    }

    @Override
    public Set<DynamicClass> get(Query query) {
        final List resultList = query.getResultList();

        final Set<DynamicClass> result = new HashSet<DynamicClass>();
        result.addAll(resultList);
        return result;
    }

    @Override
    public Set<DynamicClass> getForPrefix(String uri) {
        final Query query =
                entityManager.createNamedQuery("selectDynamicClassByURIPrefix");
        query.setParameter("uri", uri + "%");
        final List resultList = query.getResultList();

        final Set<DynamicClass> result = new HashSet<DynamicClass>();
        result.addAll(resultList);
        return result;
    }
}
