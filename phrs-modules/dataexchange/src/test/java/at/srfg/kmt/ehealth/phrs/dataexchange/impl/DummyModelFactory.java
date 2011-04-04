/*
 * Project :iCardea
 * File : ModelFactory.java
 * Encoding : UTF-8
 * Date : Mar 18, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;


import at.srfg.kmt.ehealth.phrs.dataexchange.api.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.ControlledItem;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicProperty;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyMetadata;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Used to build several <b>test</b> purposed data models.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class DummyModelFactory {

    /**
     * Used to format/parse date using the "dd.MM.yy" pattern.
     */
    private static final SimpleDateFormat DD_MM_YY_FORMAT =
            new SimpleDateFormat("dd.MM.yy");

    public static final Date DATE;

    public static final String STRING_PROPERTY_NAME = "stringProperty";

    public static final String STRING_PROPERTY_VALUE = "my value";

    public static final String BOOLEAN_PROPERTY_NAME = "booleanProperty";

    public static final Boolean BOOLEAN_PROPERTY_VALUE = Boolean.TRUE;

    public static final String DATE_PROPERTY_NAME = "dateProperty";

    public static final Date DATE_PROPERTY_VALUE;

    public static final Set<String> PROPERTY_NAMES;

    public static final Map<String, Serializable> PROPERTIES;

    public static final Map<String, String> PROPERTY_TYPES;
    
    public static final Map<String, Serializable> TYPES_VALUE;
    
    public static final Map<String, Serializable> PROPERTY_VALUES;

    static {
        try {
            DATE = DD_MM_YY_FORMAT.parse("01.01.1900");
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }

        DATE_PROPERTY_VALUE = DATE;
        PROPERTY_NAMES = new HashSet<String>(3);
        PROPERTY_NAMES.add(STRING_PROPERTY_NAME);
        PROPERTY_NAMES.add(DATE_PROPERTY_NAME);
        PROPERTY_NAMES.add(BOOLEAN_PROPERTY_NAME);

        PROPERTIES = new HashMap<String, Serializable>(3);
        PROPERTIES.put(STRING_PROPERTY_NAME, STRING_PROPERTY_VALUE);
        PROPERTIES.put(BOOLEAN_PROPERTY_NAME, BOOLEAN_PROPERTY_VALUE);
        PROPERTIES.put(DATE_PROPERTY_NAME, DATE_PROPERTY_VALUE);

        PROPERTY_TYPES = new HashMap<String, String>(3);
        PROPERTY_TYPES.put(STRING_PROPERTY_NAME, String.class.getName());
        PROPERTY_TYPES.put(DATE_PROPERTY_NAME, Date.class.getName());
        PROPERTY_TYPES.put(BOOLEAN_PROPERTY_NAME, Boolean.class.getName());
        
        PROPERTY_VALUES = new HashMap<String, Serializable>(3);
        PROPERTY_VALUES.put(STRING_PROPERTY_NAME, STRING_PROPERTY_VALUE);
        PROPERTY_VALUES.put(DATE_PROPERTY_NAME, DATE_PROPERTY_VALUE);
        PROPERTY_VALUES.put(BOOLEAN_PROPERTY_NAME, BOOLEAN_PROPERTY_VALUE);
        
        TYPES_VALUE = new HashMap<String, Serializable>(3);
        TYPES_VALUE.put(String.class.getName(), STRING_PROPERTY_VALUE);
        TYPES_VALUE.put(Date.class.getName(), DATE_PROPERTY_VALUE);
        TYPES_VALUE.put(Boolean.class.getName(), BOOLEAN_PROPERTY_VALUE);
    }

    /**
     * Don't let anybody to instantiate this class.
     * 
     */
    private DummyModelFactory() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a <code>DynamicClass</code> for a given list of parameter.
     * The resulted class will contain only one property and this property will
     * have only one metadata. 
     * 
     * @param className
     * @param classURI
     * @param typeName
     * @param metaName
     * @param metaValue
     * @return 
     */
    static DynamicClass buildDynamicClass(String className, String classURI,
            String typeName, String metaName, String metaValue) {
        final DynamicClass dynamicClass = new DynamicClass(classURI, className);

        final Set<DynamicPropertyType> types =
                new HashSet<DynamicPropertyType>();
        final DynamicPropertyType type =
                buildDynamicPropertyType(typeName, metaName, metaValue);
        type.setDynamicClass(dynamicClass);
        types.add(type);

        dynamicClass.setPropertyTypes(types);

        return dynamicClass;
    }

    static DynamicPropertyType buildDynamicPropertyType(String name,
            String metaName, String metaValue) {
        final DynamicPropertyType type = new DynamicPropertyType();
        type.setName(name);
        type.setType(String.class.getName());

        final DynamicPropertyMetadata metadata =
                buildDynamicPropertyMetadata(metaName, metaValue);
        final Set<DynamicPropertyMetadata> metadatas =
                new HashSet<DynamicPropertyMetadata>();
        metadatas.add(metadata);

        metadata.setPropertyType(type);
        type.setMetadatas(metadatas);

        return type;

    }

    static DynamicPropertyMetadata buildDynamicPropertyMetadata(String name, String value) {
        final DynamicPropertyMetadata metadata = new DynamicPropertyMetadata();
        metadata.setName(name);
        metadata.setValue(value);

        return metadata;

    }

    static String createUniqueString(String prefix) {
        final StringBuffer result = new StringBuffer();
        result.append(prefix);
        result.append(".");
        final UUID uuid = UUID.randomUUID();
        result.append(uuid.toString());
        return result.toString();
    }

    /**
     * Create a map of properties for a <code>DynamicClass</code>.
     * This map contains :
     * <ul>
     * <li> a string property named <i>stringProperty</i>. The Type for this 
     * property is <code>java.lang.String</code>. This property has one meta data
     * named <i>"stringPropertyMetadata"</i> with the value 
     * <i>"stringPropertyMetadata.value"</i>.
     * <li> a date property named <i>dateProperty</i>. The Type for this 
     * property is <code>java.util.Date</code>.This property has one meta data
     * named <i>"datePropertyMetadata"</i> with the value <i>"01.01.1900"</i>.
     * <li> a boolean property named <i>booleanProperty</i>. The Type for this 
     * property is <code>java.lang.Boolean</code>. This property has one meta data
     * named <i>"booleanPropertyMetadata"</i> with the value 
     * <i>"true"</i> (equivalent with Boolean.TRUE).
     * </ul>
     * 
     * @return a map of properties for a <code>DynamicClass</code>.
     */
    static Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> createDefaultModelMap() {
        final Map<DynamicPropertyType, Set<DynamicPropertyMetadata>> result =
                new HashMap<DynamicPropertyType, Set<DynamicPropertyMetadata>>();

        final DynamicPropertyType stringProperty = new DynamicPropertyType();
        stringProperty.setName(STRING_PROPERTY_NAME);
        stringProperty.setType(String.class.getName());

        final DynamicPropertyType dateProperty = new DynamicPropertyType();
        dateProperty.setName("dateProperty");
        dateProperty.setType(Date.class.getName());

        final DynamicPropertyType booleanProperty = new DynamicPropertyType();
        booleanProperty.setName(BOOLEAN_PROPERTY_NAME);
        booleanProperty.setType(Boolean.class.getName());

        final DynamicPropertyMetadata stringPropertyMetadata =
                new DynamicPropertyMetadata();
        stringPropertyMetadata.setName("stringPropertyMetadata");
        stringPropertyMetadata.setValue("stringPropertyMetadata.value");

        final DynamicPropertyMetadata datePropertyMetadata =
                new DynamicPropertyMetadata();
        datePropertyMetadata.setName("datePropertyMetadata");

        datePropertyMetadata.setValue(DATE);

        final DynamicPropertyMetadata booleanPropertyMetadata =
                new DynamicPropertyMetadata();
        booleanPropertyMetadata.setName("booleanPropertyMetadata");
        booleanPropertyMetadata.setValue(true);

        final Set<DynamicPropertyMetadata> stringPropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        stringPropertyMetadatas.add(stringPropertyMetadata);

        final Set<DynamicPropertyMetadata> booleanPropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        booleanPropertyMetadatas.add(booleanPropertyMetadata);

        final Set<DynamicPropertyMetadata> datePropertyMetadatas =
                new HashSet<DynamicPropertyMetadata>();
        datePropertyMetadatas.add(datePropertyMetadata);

        result.put(stringProperty, stringPropertyMetadatas);
        result.put(booleanProperty, booleanPropertyMetadatas);
        result.put(dateProperty, datePropertyMetadatas);

        return result;

    }

    static Set<DynamicPropertyType> createDefaultTypeSet() {
        final Set<DynamicPropertyType> result =
                new HashSet<DynamicPropertyType>();

        final DynamicPropertyType stringProperty = new DynamicPropertyType();
        stringProperty.setName(STRING_PROPERTY_NAME);
        stringProperty.setType(String.class.getName());

        final DynamicPropertyType dateProperty = new DynamicPropertyType();
        dateProperty.setName("dateProperty");
        dateProperty.setType(Date.class.getName());

        final DynamicPropertyType booleanProperty = new DynamicPropertyType();
        booleanProperty.setName("booleanProperty");
        booleanProperty.setType(Boolean.class.getName());

        result.add(dateProperty);
        result.add(stringProperty);
        result.add(booleanProperty);

        return result;
    }

    static ControlledItem createControlledItem() {

        final String code = createUniqueString("code");
        final ControlledItem controlledItem = new ControlledItem(Constants.SNOMED, "SNOMED", code);


        return controlledItem;
    }

    static ControlledItem createControlledItem(String label, String code) {
        final ControlledItem controlledItem = new ControlledItem(Constants.SNOMED, "SNOMED", code);
        controlledItem.setPrefLabel(label);

        return controlledItem;
    }

    static DynamicBean createDynamicBean(DynamicClass dc) {
        final String name = createUniqueString("dynamic.bean.name");
        final String uri = createUniqueString("dynamic.bean.uri");
        return new DynamicBean(uri, name, dc);
    }

    static String getPropertyTypeForName(String propertyName) {
        return PROPERTY_TYPES.get(propertyName);
    }

    static Serializable getPropertyValueForName(String propertyName) {
        return PROPERTY_VALUES.get(propertyName);
    }

    static DynamicBean buildDefaultDynamicBean(DynamicClass clazz) {
        final DynamicBean dynamicBean = createDynamicBean(clazz);

        final Set<DynamicPropertyType> propertyTypes = clazz.getPropertyTypes();
        final Set<DynamicProperty> dynamicProperties =
                new HashSet<DynamicProperty>(propertyTypes.size());

        for (DynamicPropertyType propertyType : propertyTypes) {
            final String propName = propertyType.getName();
            final String propType = propertyType.getType();
            final Serializable propValue = TYPES_VALUE.get(propType);
            final DynamicProperty dynamicProperty =
                    new DynamicProperty(propName, propType, propValue);
            // I set the owner side
            dynamicProperty.setDynamicBean(dynamicBean);
            dynamicProperties.add(dynamicProperty);
        }

        // I set the inverse part
        dynamicBean.setDynamicProperties(dynamicProperties);

        dynamicBean.setCreateDate(DummyModelFactory.DATE);
        dynamicBean.setCanRead(Boolean.TRUE);
        dynamicBean.setCanWrite(Boolean.FALSE);

        final String ownerURI = DummyModelFactory.createUniqueString("mihai.uri");
        dynamicBean.setOwnerURI(ownerURI);

        return dynamicBean;
    }
}
