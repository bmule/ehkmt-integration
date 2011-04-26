/*
 * Project :iCardea
 * File : MetadataRepositoryBean.java
 * Encoding : UTF-8
 * Date : Apr 26, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import at.srfg.kmt.ehealth.phrs.dataexchange.api.MetadataRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local stateless bean that provides a set of methods used to manage or 
 * manipulate metadata.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Stateless
@Local(MetadataRepository.class)
public class MetadataRepositoryBean implements MetadataRepository {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.MetadataRepositoryBean</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MetadataRepositoryBean.class);
    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    /**
     * Builds a <code>MetadataRepositoryBean</code> instance.
     */
    public MetadataRepositoryBean() {
        // UNIMPLEMENTED
    }

    /**
     * Returns all the the properties for a given class (identifiable after its
     * unique URI) that have a certain metatadata, the metadata is identifiable
     * after its name. If there are no matches for the specified parameters 
     * this method returns an empty set.
     * 
     * @param classUri the unique class URI.
     * @param metadataName the name for the metatada.
     * @return all the the properties for a given class (identifiable after its
     * unique URI) that have a certain metatadata, the metadata is identifiable
     * after its name.
     * @throws NullPointerException if the <code>classUri</code> or 
     * <code>metadataName</code> arguments are null.
     * @see #getByMetadataUri(java.lang.String, java.lang.String)
     */
    @Override
    public Set<DynamicPropertyType> getByMetadataName(String classUri, String metadataName) {

        if (classUri == null || metadataName == null) {
            final NullPointerException nullException =
                    new NullPointerException("The classUri or metadataName arguments can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Set<DynamicPropertyType> result =
                new HashSet<DynamicPropertyType>();
        final Query query =
                entityManager.createNamedQuery("selectPropertyForClassAndMedataByName");
        query.setParameter("class_uri", classUri);
        query.setParameter("meta_data_name", metadataName);

        final List<DynamicPropertyType> resultList = query.getResultList();
        result.addAll(resultList);

        return result;
    }

    /**
     * Returns all the the properties for a given class (identifiable after its
     * unique URI) that have a certain metatadata, the metadata is identifiable
     * after its unique URI. If there are no matches for the specified parameters 
     * this method returns an empty set.
     * 
     * @param classUri the unique class URI.
     * @param metadataUri the unique metatada URI.
     * @return all the the properties for a given class (identifiable after its
     * unique URI) that have a certain metatadata, the metadata is identifiable
     * after its name.
     * @throws NullPointerException if the <code>classUri</code> or 
     * <code>metadataName</code> arguments are null.
     * @see #getByMetadataName(java.lang.String, java.lang.String)
     */
    @Override
    public Set<DynamicPropertyType> getByMetadataUri(String classUri, String metadataUri) {

        if (classUri == null || metadataUri == null) {
            final NullPointerException nullException =
                    new NullPointerException("The classUri or metadataUri arguments can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }


        final Set<DynamicPropertyType> result =
                new HashSet<DynamicPropertyType>();
        final Query query =
                entityManager.createNamedQuery("selectPropertyForClassAndMedataByUri");
        query.setParameter("class_uri", classUri);
        query.setParameter("meta_data_name", metadataUri);

        final List<DynamicPropertyType> resultList = query.getResultList();
        result.addAll(resultList);

        return result;
    }
}
