/*
 * Project :iCardea
 * File : PhrBeanFactory.java
 * Date : Dec 15, 2010
 * User : mradules
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;


/**
 * Used to build dynamic beans instances. <br>
 * The usage is simple, the next code snippet show this : 
 * <pre>
 * PhrBeanFactory factory = PhrBeanFactory.getInstance();
 * DynaBean bean = factory.buidBean();
 * bean.set("phrs__DisplayName", "blablabla");
 * </pre>
 * This snippet creates a bean instance and set a property.
 *  
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public final class PhrBeanFactory {
    public static final String BEAN_DEF_NAME = "PHRDynaBean";

    /**
     * The only bean instance for this factory.
     */
    private final static PhrBeanFactory THIS = new PhrBeanFactory();
    
    /**
     * Used to build the default properties array.
     */
    private final static PhrBeanPropertiesFactory PROPERTIES_FACTORY =
            DefaultPhrBeanPropertiesFactory.getInstance();

    /**
     * The default set of properties.
     */
    private final static DynaProperty[] PROPERTIES =
            PROPERTIES_FACTORY.getProperties();

    /**
     * Don't let anybody to instantiate class.
     */
    private PhrBeanFactory() {
        // UNIMPLEMENTD
    }

    /**
     * Returns a instance for this factory. 
     * This method returns the same instance, always.
     * 
     * @return returns a instance for this factory. 
     */
    public static PhrBeanFactory getInstance() {
        return THIS;
    }

    /**
     * Builds a dynamic bean class that contains all the default properties.
     * The dynamic bean name has the name <code>BEAN_DEF_NAME</code>.
     * This class can not be used - it represent only the bean type.
     * 
     * @return a dynamic bean class that contains all the default properties.
     * @see #PROPERTIES
     * @see #BEAN_DEF_NAME
     * @see #buidBean() 
     */
    public final DynaClass buidClass() {
        // the reason why this method is final is I want to avoid any 
        // overwitering problems
        final BasicDynaClass result =
                new BasicDynaClass(BEAN_DEF_NAME, null, PROPERTIES);
        return result;
    }

    /**
     * Builds a dynamic bean instance that contains all the default properties.
     * The dynamic bean name has the name <code>BEAN_DEF_NAME</code>.
     * In contrast with the dynamic bean class the bean instance  can be used.
     * 
     * @return a dynamic bean instance that contains all the default properties.
     * @throws IllegalAccessException if at least one bean field can not be 
     * accessed.
     * @throws InstantiationException  if the bean instance can not be created.
     */
    public DynaBean buidBean()
            throws IllegalAccessException, InstantiationException {
        final DynaBean result = buidClass().newInstance();
        return result;
    }

    /**
     * Builds a dynamic bean class, the class contains all the properties
     * provided by a given property factory.
     * The dynamic bean name has the name <code>BEAN_DEF_NAME</code>.
     * This class can not be used - it represent only the bean type.
     *
     * @param factory the factory used to build the dynamic bean properties.
     * @return a dynamic bean class that contains all the properties
     * provided by a given property factory.
     * @throws NullPointerException if the <code>factory</code> argument is null.
     */
    public final DynaClass buidClass(PhrBeanPropertiesFactory factory) {

        if (factory == null) {
            final NullPointerException nullException =
                    new NullPointerException("The factory can not be null.");
            throw nullException;
        }

        final DynaProperty[] properties = factory.getProperties();
        final BasicDynaClass result =
                new BasicDynaClass(BEAN_DEF_NAME, null, properties);
        return result;
    }
    
    /**
     * Builds a dynamic bean instance that contains all the properties provided
     * by a given property factory.
     * The dynamic bean name has the name <code>BEAN_DEF_NAME</code>.
     * In contrast with the dynamic bean class the bean instance  can be used.
     * 
     * @return a dynamic bean instance that contains all the default properties.
     * @throws NullPointerException if the <code>factory</code> argument is null.
     * @throws IllegalAccessException if at least one bean field can not be 
     * accessed.
     * @throws InstantiationException  if the bean instance can not be created.
     */
    public final DynaBean buidBean(PhrBeanPropertiesFactory factory)
            throws IllegalAccessException, InstantiationException {
        final DynaClass resultClass = buidClass(factory);
        final DynaBean result = resultClass.newInstance();
        return result;
    }
}
