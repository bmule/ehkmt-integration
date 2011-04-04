/*
 * Project :iCardea
 * File : DymanicBeanRepositoryBean.java
 * Encoding : UTF-8
 * Date : Mar 29, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;


import at.srfg.kmt.ehealth.phrs.dataexchange.api.Constants;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynaClassException;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicBeanRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicClassRepository;
import at.srfg.kmt.ehealth.phrs.dataexchange.api.DynamicPropertyTypeException;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicBean;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicProperty;
import at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicPropertyType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.beanutils.DynaBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Stateless
@Local(DynamicBeanRepository.class)
public class DynamicBeanRepositoryBean implements DynamicBeanRepository {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.DynamicBeanRepositoryBean</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DynamicBeanRepositoryBean.class);

    /**
     * Used to communicate with the underlying persistence layer.
     */
    @PersistenceContext(unitName = "phrs_storage")
    private EntityManager entityManager;

    /**
     * Used to manage and manipulate bean classes.
     */
    @EJB
    private DynamicClassRepository classRepository;

    /**
     * Registers the given <code>DynaBean</code> instance in to the 
     * repository. </br>
     * The <code>DynaBean</code> instance must correspond to a existing
     * <code>DynamicClass</code> otherwise this method raises an exception.
     * More precisely : the <code>bean</code> name property must exist and
     * it must represent the URI for an already existing <code>DynamicClass</code>
     * instance.
     * 
     * @param bean the <code>DynamicBean</code> instance to be register, 
     * it can not be null.
     * @throws NullPointerException if the <code>bean</code> argument is null.
     * @throws IllegalArgumentException if the <code>bean</code> argument argument
     * has an improper format (e.g. its dynamic class is not defined).
     */
    @Override
    public void add(DynaBean bean) {

        if (bean == null) {
            final NullPointerException nullException =
                    new NullPointerException("The bean argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        // solve the dynamic class property
        final DynamicClass dynamicClass = getDynamicClass(bean);
        final DynamicBean dynamicBean = new DynamicBean();
        dynamicBean.setDynamicClass(dynamicClass);

        // solves the rest of the propeties
        solveDefaultProperties(bean, dynamicBean);

        final Set<DynamicPropertyType> propertyTypes = dynamicClass.getPropertyTypes();
        final Set<DynamicProperty> properties = new HashSet<DynamicProperty>();
        for (DynamicPropertyType dynamicPropertyType : propertyTypes) {
            final String name = dynamicPropertyType.getName();
            final String type = dynamicPropertyType.getType();
            // this line may throw a class cast exception if the content is
            // not srializable
            final Serializable content = (Serializable) bean.get(name);
            final DynamicProperty dynamicProperty =
                    new DynamicProperty(name, type, content);
            properties.add(dynamicProperty);

            // set the owner part.
            dynamicProperty.setDynamicBean(dynamicBean);
        }

        dynamicBean.setDynamicProperties(properties);
        entityManager.persist(dynamicBean);
    }

    /**
     * Obtains the corresponding <code>DynamicClass</code>  for a given 
     * <code>DynaBean</code>. The class information is based on the
     * <code>DynaBean</code>'s class uri property, this property must exist 
     * and it must have an appropriate value otherwise an exception may occur.
     * An appropriate value for the class uri property must be the uri for an
     * already registered dynamic class uri.
     * 
     * @param dynaBean the <code>DynaBean</code> from where the class information
     * will be extracted.
     * @return the corresponding <code>DynamicClass</code>  for a given 
     * <code>DynaBean</code>.
     * @throws IllegalArgumentException if the specified <code>DynaBean</code> 
     * instance does not has a proper class uri property.
     */
    private DynamicClass getDynamicClass(DynaBean dynaBean) {
        final String classURI =
                (String) dynaBean.get(Constants.PHRS_BEAN_CLASS_URI);

        if (classURI == null) {
            final String msg =
                    String.format("The bean %s does not have a correspondonding dyna class. "
                    + "The dyna class's name attributes is null.", dynaBean);
            final IllegalArgumentException illegalException =
                    new IllegalArgumentException(msg);
            LOGGER.error(msg, illegalException);
            throw illegalException;
        }

        final DynamicClass dynamicClass = classRepository.get(classURI);
        if (dynamicClass == null) {
            final String msg =
                    String.format("The bean %s does not have a correspondonding dyna class. "
                    + "The dyna class's name %s indicates to an unregisters DynamicClass.", dynaBean);
            final IllegalArgumentException illegalException =
                    new IllegalArgumentException(msg);
            LOGGER.error(msg, illegalException);
            throw illegalException;
        }

        return dynamicClass;
    }

    /**
     * Extracts the default dynabean properties from an given <code>DynaBean</code>
     * and populates with them an other <code>DynamicBean</code>.
     * 
     * 
     * @param dynaBean the dynabean instance - from there the default properties
     * are obtained.
     * @param dynamicBean the DynamicBean here are the default properties stored.
     */
    private void solveDefaultProperties(DynaBean dynaBean, DynamicBean dynamicBean) {

        final String beanURI;
        if (DynamicUtil.contains(dynaBean, Constants.PHRS_BEAN_URI)) {
            beanURI = (String) dynaBean.get(Constants.PHRS_BEAN_URI);
        } else {
            beanURI = DynamicUtil.createUniqueString("phrsBean");
        }
        final String uriMsg =
                String.format("%s = %s", Constants.PHRS_BEAN_URI, beanURI);
        LOGGER.debug(uriMsg);
        dynamicBean.setUri(beanURI);

        final String beanName;
        if (DynamicUtil.contains(dynaBean, Constants.PHRS_BEAN_NAME)) {
            beanName = (String) dynaBean.get(Constants.PHRS_BEAN_NAME);
        } else {
            beanName = DynamicUtil.createUniqueString("phrsBeanName");
        }
        dynamicBean.setUri(beanName);
        final String beanNameMsg =
                String.format("%s = %s", Constants.PHRS_BEAN_NAME, beanName);
        LOGGER.debug(beanNameMsg);
        
        // I don't care if the user tries to set the verion.
        // the version is managed by the system not by the user.
        final DynamicClass dynamicClass = dynamicBean.getDynamicClass();
        final int lastVersion = getLastVersion(dynamicClass);
        final Long version = Long.valueOf(lastVersion);
        dynamicBean.setUriVersion(version);
        final String beanVersionMsg =
                String.format("%s = %s", Constants.PHRS_BEAN_VERSION, version);
        LOGGER.debug(beanVersionMsg);

        if (DynamicUtil.contains(dynaBean, Constants.PHRS_BEAN_CREATOR)) {
            final String creatorURI =
                    (String) dynaBean.get(Constants.PHRS_BEAN_CREATOR);
            dynamicBean.setCreatorURI(creatorURI);
            final String creatorURIMsg =
                    String.format("%s = %s", Constants.PHRS_BEAN_CREATOR, creatorURI);
            LOGGER.debug(creatorURIMsg);
        }

        if (DynamicUtil.contains(dynaBean, Constants.PHRS_BEAN_OWNER)) {
            final String ownerURI =
                    (String) dynaBean.get(Constants.PHRS_BEAN_OWNER);
            dynamicBean.setCreatorURI(ownerURI);
            final String ownerURIMsg =
                    String.format("%s = %s", Constants.PHRS_BEAN_OWNER, ownerURI);
            LOGGER.debug(ownerURIMsg);
        }

        final Boolean canRead;
        if (DynamicUtil.contains(dynaBean, Constants.PHRS_BEAN_CANREAD)) {
            final String canReadStr =
                    (String) dynaBean.get(Constants.PHRS_BEAN_CANREAD);
            canRead = Boolean.valueOf(uriMsg);
        } else {
            canRead = Boolean.FALSE;
        }
        dynamicBean.setCanRead(canRead);
        final String canReadMsg =
                String.format("%s = %s", Constants.PHRS_BEAN_CANREAD, canRead);
        LOGGER.debug(canReadMsg);


        final boolean canWrite;
        if (DynamicUtil.contains(dynaBean, Constants.PHRS_BEAN_CANWRITE)) {
            final String canUseStr =
                    (String) dynaBean.get(Constants.PHRS_BEAN_CANWRITE);
            canWrite = Boolean.valueOf(canUseStr);
        } else {
            canWrite = false;
        }
        dynamicBean.setCanRead(canWrite);
        final String canWriteMsg =
                String.format("%s = %s", Constants.PHRS_BEAN_CANWRITE, canWrite);
        LOGGER.debug(canWriteMsg);

        final boolean canUse;
        if (DynamicUtil.contains(dynaBean, Constants.PHRS_BEAN_CANUSE)) {
            final String canUseStr =
                    (String) dynaBean.get(Constants.PHRS_BEAN_CANUSE);
            canUse = Boolean.valueOf(canUseStr);

        } else {
            canUse = false;
        }
        dynamicBean.setCanUse(canUse);
        final String canUseMsg =
                String.format("%s = %s", Constants.PHRS_BEAN_CANUSE, canUse);
        LOGGER.debug(canUseMsg);
    }
    
    private int getLastVersion(DynamicClass dynamicClass) {
        final Query query = 
                entityManager.createNamedQuery("countDynamicBeanByDynamicClass");
        query.setParameter("dynamic_class", dynamicClass);
        
        final Long result = (Long) query.getSingleResult();
        return result.intValue();
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
        if (clazz == null) {
            final NullPointerException nullException =
                    new NullPointerException("The clazz argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final long beansCount = countBeansByClass(clazz);
        return beansCount > 0;
    }

    /**
     * Counts all the persisted bean instances that have a certain class.
     * 
     * @param clazz the class (type) for the bean to count.
     * @return total count for all the persisted bean instances that have 
     * a certain class.
     */
    private long countBeansByClass(DynamicClass clazz) {
        final Query query =
                entityManager.createNamedQuery("countDynamicBeanByDynamicClass");
        query.setParameter("dynamic_class", clazz);

        final Long result = (Long) query.getSingleResult();

        return result.longValue();
    }

    /**
     * Returns true if this repository contains the given bean.
     * 
     * @param bean the bean that presence is to be proved.
     * @return true if this repository contains the given bean.
     */
    @Override
    public boolean contains(DynaBean bean) {
        // FIXME : prove the entire bean content !
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns true if this repository contains one bean with the given URI.
     * 
     * @param beanURI the bean URI that presence is to be proved, it can not
     * be null or empty string.
     * @return true if this repository contains one bean with the given URI.
     * @throws NullPointerException if the <code>beanURI</code> argument is null
     * or empty string.
     */
    @Override
    public boolean contains(String beanURI) {

        if (beanURI == null || beanURI.isEmpty()) {
            final NullPointerException nullException =
                    new NullPointerException("The clazz argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }


        final Query query =
                entityManager.createNamedQuery("selectDynamicBeanByUri");
        query.setParameter("bean_uri", beanURI);

        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException exception) {
            final String msg =
                    String.format("No beans with this uri found", beanURI);
            LOGGER.debug(msg);
        }
        return false;
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
     * @throws DynamicPropertyTypeException by the at least one property type
     * can not be located in the classpath. The exception encapsulates the
     * property type that generated the exception.
     * @throws DynaClassException if some bean instance can not be builded using
     * the specified <code>DynaClass</code>. The exception encapsulates the
     * <code>DynaClass</code> instance that generated the exception.
     * @see #getAllForClass(java.lang.String)     
     */
    @Override
    public final Set<DynaBean> getAllForClass(DynamicClass clazz)
            throws DynamicPropertyTypeException, DynaClassException {

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
        

        final Set<DynamicBean> beans = new HashSet<DynamicBean>();
        beans.addAll(resultList);
        
        final Set<DynaBean> dynaBeans = DynamicUtil.getDynaBeans(beans);
        return dynaBeans;
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
     * @throws DynamicPropertyTypeException by the at least one property type
     * can not be located in the classpath. The exception encapsulates the
     * property type that generated the exception.
     * @throws DynaClassException if some bean instance can not be builded using
     * the specified <code>DynaClass</code>. The exception encapsulates the
     * <code>DynaClass</code> instance that generated the exception.
     * @see #getForClass(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     */
    @Override
    public Set<DynaBean> getAllForClass(String classURI)
            throws DynamicPropertyTypeException, DynaClassException {
        if (classURI == null) {
            final NullPointerException nullException =
                    new NullPointerException("The classURI argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final DynamicClass dynamicClass = classRepository.get(classURI);
        if (dynamicClass == null) {
            final String msg =
                    String.format("No regstered dynamic class for uri [%s].", classURI);
            LOGGER.debug(msg);
            return new HashSet<DynaBean>();
        }

        final Set<DynaBean> result = getAllForClass(dynamicClass);
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
     * @throws DynamicPropertyTypeException by the at least one property type
     * can not be located in the classpath. The exception encapsulates the
     * property type that generated the exception.
     * @throws DynaClassException if some bean instance can not be builded using
     * the specified <code>DynaClass</code>. The exception encapsulates the
     * <code>DynaClass</code> instance that generated the exception.
     * @see #getForClass(java.lang.String) 
     */
    @Override
    public DynaBean getForClass(DynamicClass clazz)
            throws DynamicPropertyTypeException, DynaClassException {
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

        final DynamicBean dynamicBean = (DynamicBean) resultList.get(0);
        final DynaBean result = DynamicUtil.get(dynamicBean);
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
     * @throws DynamicPropertyTypeException by the at least one property type
     * can not be located in the classpath. The exception encapsulates the
     * property type that generated the exception.
     * @throws DynaClassException if some bean instance can not be builded using
     * the specified <code>DynaClass</code>. The exception encapsulates the
     * <code>DynaClass</code> instance that generated the exception.
     * @see #getForClass(at.srfg.kmt.ehealth.phrs.dataexchange.model.DynamicClass) 
     */
    @Override
    public DynaBean getForClass(String classURI)
            throws DynamicPropertyTypeException, DynaClassException {
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

        final DynaBean result = getForClass(clazz);
        return result;
    }

    /**
     * Returns a <code>DynaBean</code> instance that match the given URI.
     * If this repository does not contain a <code>DynaBean</code> with the 
     * given URI then this method returns null.
     * 
     * @param beanURI the URI for the bean to search, it can not be null.
     * @return a <code>DynaBean</code> instance that match the given URI.
     * @throws NullPointerException if the <code>beanURI</code> argument is null.
     * @throws DynamicPropertyTypeException by the at least one property type
     * can not be located in the classpath. The exception encapsulates the
     * property type that generated the exception.
     * @throws DynaClassException if some bean instance can not be builded using
     * the specified <code>DynaClass</code>. The exception encapsulates the
     * <code>DynaClass</code> instance that generated the exception.
     */
    @Override
    public DynaBean getForURI(String beanURI)
            throws DynamicPropertyTypeException, DynaClassException {

        if (beanURI == null) {
            final NullPointerException nullException =
                    new NullPointerException("The beanURI argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        final Query query =
                entityManager.createNamedQuery("selectDynamicBeanByUri");
        query.setParameter("bean_uri", beanURI);

        try {
            final DynamicBean dynamicBean = (DynamicBean) query.getSingleResult();
            final DynaBean result = DynamicUtil.get(dynamicBean);
            return result;
        } catch (NoResultException exception) {
            final String msg =
                    String.format("There are no registered beans with this UIR [%s].", beanURI);
            LOGGER.debug(msg);
        }

        return null;
    }

    /**
     * Returns a bean instance for a given class and version.</br>
     * The first version value is 0.
     * 
     * @param dynamicClass the class for the bean to search.
     * @param version the version for the bean to search, 
     * must be grater or equal with 0.
     * @return a bean instance for a given class and version or null if this 
     * repository does not contains any bean that may fulfills the class and the
     * version.
     * @throws NullPointerException if the <code>dynamicClass</code> argument is null.
     * @throws IllegalArgumentException if the <code>version</code>  argument 
     * value is smaller then 0.
     * @throws DynamicPropertyTypeException by the at least one property type
     * can not be located in the classpath. The exception encapsulates the
     * property type that generated the exception.
     * @throws DynaClassException if some bean instance can not be builded using
     * the specified <code>DynaClass</code>. The exception encapsulates the
     * <code>DynaClass</code> instance that generated the exception.
     */
    @Override
    public final DynaBean getForClassAndVersion(DynamicClass dynamicClass, long version)
            throws DynamicPropertyTypeException, DynaClassException {

        if (dynamicClass == null) {
            final NullPointerException nullException =
                    new NullPointerException("The dynamicClass argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (version < 0) {
            final IllegalArgumentException argumentException =
                    new IllegalArgumentException("The dynamicClass version must be greater or equls wiht 0.");
            LOGGER.error(argumentException.getMessage(), argumentException);
            throw argumentException;
        }

        final Query query =
                entityManager.createNamedQuery("selectDynamicBeanByDynamicClassAndVersionWithProperties");
        query.setParameter("dynamic_class", dynamicClass);
        query.setParameter("uri_version", version);

        try {
            final DynamicBean dynamicBean = (DynamicBean) query.getSingleResult();
            final DynaBean result = DynamicUtil.get(dynamicBean);
            return result;
        } catch (NoResultException exception) {
            final String msg =
                    String.format("There are no registered beans with this class [%s] and this version [%s].", dynamicClass, version);
            LOGGER.debug(msg);
        }
        return null;
    }

    /**
     * Returns a bean instance for a given class and version, the class is 
     * identified after it URI.</br>
     * The first version value is 0.
     * 
     * @param classURI the class uri for the bean to search.
     * @param version the version for the bean to search, 
     * must be grater or equal with 0.
     * @return a bean instance for a given class and version or null if this 
     * repository does not contains any bean that may fulfills the class and the
     * version.
     * @throws NullPointerException if the <code>dynamicClass</code> argument is null.
     * @throws IllegalArgumentException if the <code>version</code>  argument 
     * value is smaller then 0.
     * @throws DynamicPropertyTypeException by the at least one property type
     * can not be located in the classpath. The exception encapsulates the
     * property type that generated the exception.
     * @throws DynaClassException if some bean instance can not be builded using
     * the specified <code>DynaClass</code>. The exception encapsulates the
     * <code>DynaClass</code> instance that generated the exception.
     */
    @Override
    public DynaBean getForClassAndVersion(String classURI, long version)
            throws DynamicPropertyTypeException, DynaClassException {

        if (classURI == null) {
            final NullPointerException nullException =
                    new NullPointerException("The classURI argument can not be null.");
            LOGGER.error(nullException.getMessage(), nullException);
            throw nullException;
        }

        if (version < 0) {
            final IllegalArgumentException argumentException =
                    new IllegalArgumentException("The dynamicClass version must be greater or equls wiht 0.");
            LOGGER.error(argumentException.getMessage(), argumentException);
            throw argumentException;
        }

        final DynamicClass dynamicClass = classRepository.get(classURI);
        if (dynamicClass == null) {
            final String msg =
                    String.format("No regstered dynamic class for uri [%s].", classURI);
            LOGGER.debug(msg);
            return null;
        }

        final DynaBean result = getForClassAndVersion(dynamicClass, version);
        return result;
    }

    @Override
    public List<DynaBean> get(java.util.Map<String, ?> qbe)
            throws DynamicPropertyTypeException, DynaClassException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(DynaBean bean) {
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
