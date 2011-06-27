/*
 * Project :iCardea
 * File : ENCLookup.java
 * Date : Dec 22, 2010
 * User : mradules
 */
package at.srfg.kmt.ehealth.phrs.util;


import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;


/**
 * A lot of methods used for the JNDI lookups.
 * There are two kinds of lookup :
 * <ul>
 * <li> local for the lookups in the same JVM.
 * <li> remote for different JVMs.
 * </ul>
 * By example if you have want to do a lookup for a bean instance
 * from an Servlet and the Servlet is running in the same enterprise
 * container (the same JVM) you may use the local search.
 *
 * @author Mihai
 */
public final class JBossJNDILookup {

    /**
     * Don't let anybody to instantiate this class.
     */
    private JBossJNDILookup() {
        // UNIMPLEMENTED
    }

    /**
     * Searches in the underlying JNDI context a bean with
     * a given type in the local scope. The next code snippet shows
     * the method usage :
     * <pre>
     * GroupManager groupManager = JBossJNDILookup.lookupLocal(GroupManager.class);
     * </pre>
     * this call will look in to the local scope a bean which implements
     * the GroupManager interface. <br>
     * Use this method when you want to do lookups in the same JVM
     * (lookups in the running container).
     *
     * @param <T> the return type.
     * @param beanClass the result class (can be a class or an interface).
     * @return  a bean with a given type in the local scope.
     * @throws NamingException if there is no matching for the given
     * ENC name or by any kind of errors.
     * @throws NullPointerException if the beanClass argument is null.
     */
    public static <T> T lookupLocal(Class<T> beanClass) throws NamingException {

        if (beanClass == null) {
            throw new NullPointerException("The beanClass arguments can not be null.");
        }

        final String name = JBossENCBuilder.getENCNameLocal(beanClass);
        final InitialContext ctx = new InitialContext();
        final Object result = ctx.lookup(name);

        return (T) result;
    }

    /**
     * Searches in the underlying JNDI context a bean with
     * a given type in the remote scope. The next code snippet shows
     * the method usage :
     * <pre>
     * GroupManager groupManager = JBossJNDILookup.lookupLocal(GroupManager.class);
     * </pre>
     * this call will look in to the local scope a bean which implements
     * the GroupManager interface.
     *
     * @param <T> the return type.
     * @param beanClass the result class (can be a class or an interface).
     * @return  a bean with a given type in the remote scope.
     * @throws NamingException if there is no matching for the given
     * ENC name or by any kind of errors.
     * @throws NullPointerException if the beanClass argument is null.
     */
    public static <T> T lookupRemote(Class<T> beanClass) throws NamingException {

        if (beanClass == null) {
            throw new NullPointerException("The beanClass arguments can not be null.");
        }

        final String name = JBossENCBuilder.getENCNameRemote(beanClass);
        final InitialContext ctx = new InitialContext();
        final Object result = ctx.lookup(name);

        return (T) result;
    }

    /**
     * Searches in the underlying JNDI context a bean with
     * a given type in the remote scope. 
     * The context configuration may vary from case to case, for JBoss AS
     * you can use the next configuration :
     * <ul>
     * <li> java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
     * <li> java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
     * <li> java.naming.provider.url=localhost:1099
     * </ul>
     * The next code snippet shows the method usage :
     * <pre>
     *
     * final Properties cnxConfig = new Properties();
     *
     * cnxConfig.put("java.naming.factory.initial",
     *      "org.jnp.interfaces.NamingContextFactory");
     * cnxConfig.put("java.naming.factory.url.pkgs",
     *      "org.jboss.naming:org.jnp.interfaces");
     * cnxConfig.put("java.naming.provider.url", "localhost:1099");
     *
     * final InitialContext ctx = new InitialContext(cnxConfig);
     *
     * GroupManager groupManager = 
     *      JBossJNDILookup.lookupLocal(GroupManager.class, cnxConfig);
     * </pre>
     * this call will look in to the remote scope a bean which implements
     * the GroupManager interface, the context is also configured.
     * 
     * @param <T>
     * @param beanClass
     * @return
     * @throws NamingException if there is no matching for the given
     * ENC name or by any kind of errors.
     * @throws NullPointerException if the beanClass argument is null.
     */
    public static <T> T lookupRemote(Class<T> beanClass, Properties contexProp)
            throws NamingException {


        if (beanClass == null) {
            throw new NullPointerException("The beanClass arguments can not be null.");
        }

        final InitialContext ctx;
        if (contexProp == null) {
            ctx = new InitialContext();
        } else {
            ctx = new InitialContext(contexProp);
        }

        final String name = JBossENCBuilder.getENCNameRemote(beanClass);

        final Object result = ctx.lookup(name);

        return (T) result;
    }

    /**
     * Searches a given resource in the JNDI registry. <br>
     * The encName must follow the underlying JNDI registry rules,
     * for JBoss AS you a name which is follow the syntax :
     * ear file name/Bean name/Scope, where the :
     * <ul>
     * <li> ear file name - the ear file (without the extension)
     * <li> scope - the bean scope (local or remote).
     * </ul>
     * <b>Note : </b> the underlying JNDI InitialContext used by this method
     * is already configured (by the container).
     * The next code snippet show how to use this method :
     * <pre>
     * GroupManager groupManager =
     *      JBossJNDILookup.lookup("phrs-ear-0.1-SNAPSHOT/GroupManagerBean/remote");
     * </pre>
     *
     * in this case the look up will return a remote scoped bean named
     * 'GroupManagerBean' placed in the 'phrs-ear-0.1-SNAPSHOT' ear.
     *
     * @param <T> the return type.
     * @param encName the ECN name, it can not be null.
     * @return the instance corresponding to the given the ENC name.
     * @throws NamingException if there is no matching for the given
     * ENC name or by any kind of errors.
     * @throws NullPointerException if the encName argument is null.
     * @see #lookup(java.lang.String)
     */
    public static <T> T lookup(String encName) throws NamingException {

        if (encName == null) {
            throw new NullPointerException("The encName arguments can not be null.");
        }


        final InitialContext ctx = new InitialContext();



        final Object result = ctx.lookup(encName);

        return (T) result;
    }

    public static String listContext() throws NamingException {
        final InitialContext ctx = new InitialContext();
        final String result = listContext(ctx, "");
        return result;
    }

    public static String listContext(String pathContext) throws NamingException {
        final InitialContext ctx = new InitialContext();
        final String result = listContext(ctx, pathContext);
        return result;
    }

    public static String listContext(Context envCtx, String pathContext) throws NamingException {
        final StringBuilder result = new StringBuilder();
        result.append("[");
        for (NamingEnumeration<NameClassPair> list = envCtx.list(pathContext);
                list.hasMoreElements();) {
            final NameClassPair next = list.next();
            result.append("[Name : ");
            result.append(next.getName());
            result.append(", ");

            result.append("Class Name : ");
            result.append(next.getClassName());
            result.append("], ");
        }
        
        result.delete(result.length() - 2, result.length());
        result.append("]");

        return result.toString();
    }

    /**
     * Searches a given resource in the JNDI registry using a given
     * context configuration. <br>
     * The encName must follow the underlying JNDI registry rules,
     * for JBoss AS you a name which is follow the syntax :
     * ear file name/Bean name/Scope, where the :
     * <ul>
     * <li> ear file name - the ear file (without the extension)
     * <li> scope - the bean scope (local or remote).
     * </ul>
     * The context configuration may vary from case to case, for JBoss AS
     * you can use the next configuration :
     * <ul>
     * <li> java.naming.factory.initial=org.jnp.interfaces.NamingContextFactory
     * <li> java.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces
     * <li> java.naming.provider.url=localhost:1099
     * </ul>
     * If you do lookup search inside a running container (where the JNDI
     * InitialContext is configurated) you don't need to specify an 
     * configuration map - you must use in this case the
     * JBossJNDILookup#lookup(java.lang.String) method. <br>
     * The next code snippet show how to use this method : 
     * <pre>
     * final Properties cnxConfig = new Properties();
     *
     * cnxConfig.put("java.naming.factory.initial", 
     *      "org.jnp.interfaces.NamingContextFactory");
     * cnxConfig.put("java.naming.factory.url.pkgs", 
     *      "org.jboss.naming:org.jnp.interfaces");
     * cnxConfig.put("java.naming.provider.url", "localhost:1099");
     * 
     * final InitialContext ctx = new InitialContext(cnxConfig);
     * 
     * JBossJNDILookup.lookup("phrs-ear-0.1-SNAPSHOT/GroupManagerBean/remote", cnxConfig);
     * </pre>
     *
     * in this case the look up will return a remote scoped bean named
     * 'GroupManagerBean' placed in the 'phrs-ear-0.1-SNAPSHOT' ear.
     * 
     * @param <T> the return type.
     * @param encName the ECN name, it can not be null.
     * @param contexProp the context configuration,
     * if is null then this method has the same effect like the
     * JBossJNDILookup#lookup(java.lang.String) method.
     * @return the instance corresponding to the given the ENC name.
     * @throws NamingException if there is no matching for the given
     * ENC name or by any kind of errors.
     * @throws NullPointerException if the encName argument is null.
     * @see #lookup(java.lang.String) 
     */
    public static <T> T lookup(String encName, Properties contexProp) throws NamingException {

        if (encName == null) {
            throw new NullPointerException("The encName arguments can not be null.");
        }

        final InitialContext ctx;
        if (contexProp == null) {
            ctx = new InitialContext();
        } else {
            ctx = new InitialContext(contexProp);
        }

        final Object result = ctx.lookup(encName);

        return (T) result;
    }
}
