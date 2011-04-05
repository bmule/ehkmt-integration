/*
 * Project :iCardea
 * File : ModelFactory.java
 * Encoding : UTF-8
 * Date : Mar 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.model;


import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class ModelFactory {

    /**
     * Builds a <code>DynamicClass</code> for a given name, uri and parameter map.
     * The parameter map look like this :
     * <ul>
     * <li> the key of the map represents the involved <code>DynamicPropertyType</code>
     * <li> the corresponding value represents a set of <code>DynamicPropertyMetadata</code>
     * applicable on the  the given <code>DynamicPropertyType</code>.
     * </ul>
     * 
     * @param name the human name for the class, it can be null.
     * @param uri the unique id for the class,it can not be null.
     * @param properties the map of properties, , it can be null.
     * @return a <code>DynamicClass</code> for the given set of parameters.
     * @see #buildDynamicClass(java.lang.String, java.lang.String, java.util.Set) 
     */
    public static DynamicClass buildDynamicClass(String name, String uri,
            Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> properties) {

        final DynamicClass dynamicClass = new DynamicClass(uri, name);
        final Set<Entry<DynamicPropertyType, Set<DynamicPropertyMetadata>>> entrySet =
                properties.entrySet();

        for (Entry<DynamicPropertyType, Set<DynamicPropertyMetadata>> entry : entrySet) {
            final DynamicPropertyType propertyType = entry.getKey();
            // set the owner parts
            propertyType.setDynamicClass(dynamicClass);


            final Set<DynamicPropertyMetadata> metadatas = entry.getValue();
            for (DynamicPropertyMetadata metadata : metadatas) {
                // set the owner part
                metadata.setPropertyType(propertyType);
            }
            propertyType.setMetadatas(metadatas);
        }

        final Set<DynamicPropertyType> propertyTypes = properties.keySet();
        dynamicClass.setPropertyTypes(propertyTypes);

        return dynamicClass;
    }

    public static DynamicClass buildDynamicClass(String name, String uri,
            Set<DynamicPropertyType> properties) {

        final DynamicClass dynamicClass = new DynamicClass(uri, name);
        for (DynamicPropertyType propertyType : properties) {
            // set the owner part
            propertyType.setDynamicClass(dynamicClass);
        }
        dynamicClass.setPropertyTypes(properties);

        return dynamicClass;
    }

    /**
     * Builds a unique string (URL like) based on a UUID, the string can have
     * a given prefix. If the prefix is null or empty strings then it will be
     * ignored. <br>
     * The result string follows the following syntax : 
     * <pre>prefix/UUID string</pre>.
     * 
     * @param prefix the prefix for the result string, if is null it will be 
     * ignored.
     * @return a unique string (URL like) based on a UUID..
     * @see #buildUniqueString(java.lang.String) 
     */
    public static String buildUniqueString(String prefix) {
        return buildUniqueString(prefix, null);
    }

    /**
     * Builds a unique string (URL like) based on a UUID, the string can have
     * a given prefix and subfix. If the subfix or the prefix are null or empty 
     * strings then they will be ignored. <br>
     * The result string follows the following syntax : 
     * <pre>prefix/UUID string/subfix</pre>.
     * 
     * @param prefix the prefix for the result string, if is null it will be 
     * ignored.
     * @param subfix the subfix for the result string, if is null it will be 
     * ignored.
     * @return a unique string (URL like) based on a UUID, the string can have
     * a given prefix and subfix.
     * @see #buildUniqueString(java.lang.String) 
     */
    public static String buildUniqueString(String prefix, String subfix) {
        final StringBuffer result = new StringBuffer();
        if (prefix != null && !prefix.isEmpty()) {
            result.append(prefix);
            result.append("/");
        }

        final UUID randomUUID = UUID.randomUUID();
        result.append(randomUUID.toString());

        if (subfix != null && !subfix.isEmpty()) {
            result.append("/");
            result.append(subfix);
        }

        return result.toString();
    }
}
