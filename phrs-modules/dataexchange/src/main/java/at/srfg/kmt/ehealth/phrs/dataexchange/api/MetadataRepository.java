/*
 * Project :iCardea
 * File : DymanicClassRepository.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.api;


import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.util.Set;

/**
 * Defines a sum of metadata related operations.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public interface MetadataRepository {

    /**
     * Returns all the the properties for a given class (identifiable after its
     * unique URI) that have a certain metatadata, the metadata is identifiable
     * after its name. It is not define here how the system must react if there
     * are no matches for the specified parameters, it is acceptable to return
     * an empty set.
     * 
     * @param classUri the unique class URI.
     * @param metadataName the name for the metatada.
     * @return all the the properties for a given class (identifiable after its
     * unique URI) that have a certain metatadata, the metadata is identifiable
     * after its name.
     * @see #getByMetadataUri(java.lang.String, java.lang.String)
     */
    Set<DynamicPropertyType> getByMetadataName(String classUri, String metadataName);

    /**
     * Returns all the the properties for a given class (identifiable after its
     * unique URI) that have a certain metatadata, the metadata is identifiable
     * after its unuqie URI. It is not define here how the system must react if there
     * are no matches for the specified parameters, it is acceptable to return
     * an empty set.
     * 
     * @param classUri the unique class URI.
     * @param metadataUri the unique uri for the metatada.
     * @return all the the properties for a given class (identifiable after its
     * unique URI) that have a certain metatadata, the metadata is identifiable
     * after its name.
     * @see #getByMetadataName(java.lang.String, java.lang.String)
     */
    Set<DynamicPropertyType> getByMetadataUri(String classUri, String metadataUri);
}
