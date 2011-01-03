/*
 * Project :iCardea
 * File : JBossENCBuilder.java
 * Date : Dec 22, 2010
 * User : mradules
 */


package at.srfg.kmt.ehealth.phrs.util;


/**
 * It is used to build ENC for JBoss specific environment.
 * The ENC are used for for JNDI lookups, every resource has a unique name
 * addressable via the JNDI lookups. <br>
 * <b>Note : </b> this ENC names produced with this class are <b>only</b>
 * valid for a JBoss environment. <br>
 * This class is not design to be extended.
 *
 * @author Mihai
 */
public final class JBossENCBuilder {

    /**
     * The name for the PHRS enterprise archive.
     */
    static final String EAR_NAME = "phrs-ear-0.1-SNAPSHOT";

    /**
     * The local scope name.
     */
    static final String LOCAL_SCOPE = "local";

    /**
     * The remote scope name.
     */
    static final String REMOTE_SCOPE = "remote";

    /**
     * Don't let anybody to instantiate this class.
     */
    private JBossENCBuilder() {
        // UNIMPLEMENTED
    }

    /**
     * Builds a ENC name local scoped for a given type and
     * enterprise archive name.
     * The returned String follow the syntax :
     * <ul>
     * <li> if the <code>clazz</code> is a interface then :
     * enterprise archive name + / + clazz.getSimpleName() + "Bean/local";
     * <li> if the <code>clazz</code> is a class then :
     * enterprise archive name + / + clazz.getSimpleName() + "/local";
     * <ul>
     *
     * @param earName the enterprise archive name, it can not be null. The
     * enterprise archive name is the file name without the extension.
     * @param clazz the type for the ENC, it can not be null.
     * @return a ENC name local scoped for the given class.
     * @throws NullPointerException if the earName or the clazz argument are null.
     */
    public static String getENCNameLocal(String earName, Class<?> clazz) {

        if (earName == null) {
            throw new NullPointerException("The earName argument can not be null.");
        }

        if (clazz == null) {
            throw new NullPointerException("The clazz argument can not be null.");
        }

        final boolean isInterface = clazz.isInterface();

        final String simpleName = isInterface
                ? clazz.getSimpleName() + "Bean"
                : clazz.getSimpleName();

        final String result = buildENC(earName, simpleName, LOCAL_SCOPE);
        return result;
    }

    /**
     * Builds a ENC name local scoped for the given type for the default
     * PHRS enterprise name (EAR_NAME).
     * The returned String follow the syntax :
     * <ul>
     * <li> if the <code>clazz</code> is a interface then : EAR_NAME + / + clazz.getSimpleName() + "Bean/local";
     * <li> if the <code>clazz</code> is a interface then : EAR_NAME + / + clazz.getSimpleName() + "/local";
     * <ul>
     *
     * @param clazz the type for the ENC, it can not be null.
     * @return a ENC name local scoped for the given class.
     * @throws NullPointerException if the clazz argument is null.
     * @see #EAR_NAME
     */
    public static String getENCNameLocal(Class<?> clazz) {
        return getENCNameLocal(EAR_NAME, clazz);
    }

    /**
     * Builds a ENC name local scoped for the given string, the result ENC name
     * is relative to the default ear name (EAR_NAME).
     * The returned String follow the syntax : EAR_NAME + / + getSimpleName "/local";
     *
     * @param simpleClassName the given string, it can not be null.
     * @return a ENC name local scoped for the given class.
     * @throws NullPointerException if the simpleClassName argument is null.
     * @see JBossENCBuilder#EAR_NAME
     */
    public static String getENCNameLocal(String simpleClassName) {
        if (simpleClassName == null) {
            throw new NullPointerException("The simpleClassName argument can not be null.");
        }

        if (simpleClassName.isEmpty()) {
            throw new IllegalArgumentException("The simpleClassName argument can not be empty string.");
        }

        return buildENC(EAR_NAME, simpleClassName, LOCAL_SCOPE);
    }

    /**
     * Builds a ENC name local scoped for the given string, 
     * the result ENC name is relative to the specified
     * ear (the earName argument).
     * The returned String follow the syntax : earName + / + getSimpleName "/local";

     * @param earName the enterprise archive name - it can not be null.
     * @param simpleClassName the given string, it can not be null.
     * @return a ENC name local scoped for the given class.
     * @throws NullPointerException if the any argument argument is null.
     */
    public static String getENCNameLocal(String earName, String simpleClassName) {

        if (earName == null) {
            throw new NullPointerException("The earName argument can not be null.");
        }

        return buildENC(earName, simpleClassName, LOCAL_SCOPE);
    }

    /**
     * Builds a ENC name remote scoped for the given type,
     * the result ENC name is relative to the specified
     * ear (the earName argument).
     * The returned String follow the syntax :
     * <ul>
     * <li> if the <code>clazz</code> is a interface then : earName + / + clazz.getSimpleName() + "Bean/remote";
     * <li> if the <code>clazz</code> is a interface then : earName + / + clazz.getSimpleName() + "/remote";
     * <ul>
     *
     * @param earName the enterprise archive name, it can not be null.
     * @param clazz the type for the ENC, it can not be null.
     * @return a ENC name remote scoped for the given class relative to
     * the given ear.
     * @throws NullPointerException if any argument is null.
     */
    public static String getENCNameRemote(String earName, Class<?> clazz) {

        if (earName == null) {
            throw new NullPointerException("The earName argument can not be null.");
        }

        if (clazz == null) {
            throw new NullPointerException("The clazz argument can not be null.");
        }

        final boolean isInterface = clazz.isInterface();

        final String simpleName = isInterface
                ? clazz.getSimpleName() + "Bean"
                : clazz.getSimpleName();

        final String result = buildENC(earName, simpleName, REMOTE_SCOPE);
        return result;
    }

    /**
     * Builds a ENC name remote scoped for the given type for the default
     * PHRS enterprise name (EAR_NAME).
     * The returned String follow the syntax :
     * <ul>
     * <li> if the <code>clazz</code> is a interface then : EAR_NAME + / + clazz.getSimpleName() + "Bean/remote";
     * <li> if the <code>clazz</code> is a interface then : EAR_NAME + / + clazz.getSimpleName() + "/remote";
     * <ul>
     *
     * @param clazz the type for the ENC, it can not be null.
     * @return a ENC name local scoped for the given class.
     * @throws NullPointerException if the clazz argument is null.
     * @see #EAR_NAME
     */
    public static String getENCNameRemote(Class<?> clazz) {
        return getENCNameRemote(EAR_NAME, clazz);
    }

    /**
     * Builds a ENC name remote scoped for the given string, the result ENC name
     * is relative to the default ear name (EAR_NAME).
     * The returned String follow the syntax : EAR_NAME + / + getSimpleName "/local";
     *
     * @param simpleClassName the given string, it can not be null.
     * @return a ENC name remote scoped for the given class.
     * @throws NullPointerException if the simpleClassName argument is null.
     * @see JBossENCBuilder#EAR_NAME
     */
    public static String getENCNameRemote(String simpleClassName) {

        if (simpleClassName == null){
            throw new NullPointerException("The simpleClassName argument can not be null.");
        }

        return buildENC(EAR_NAME, simpleClassName, REMOTE_SCOPE);
    }

    /**
     * Builds a ENC name remote scoped for the given string,
     * the result ENC name is relative to the specified
     * ear (the earName argument).
     * The returned String follow the syntax : earName + / + getSimpleName "/remote";

     * @param earName the enterprise archive name - it can not be null.
     * @param simpleClassName the given string, it can not be null.
     * @return a ENC name local scoped for the given class.
     * @throws NullPointerException if the any argument argument is null.
     */
    public static String getENCNameRemote(String earName, String simpleClassName) {

        if (earName == null) {
            throw new NullPointerException("The earName argument can not be null.");
        }

        return buildENC(earName, simpleClassName, REMOTE_SCOPE);
    }

    /**
     * Builds a String with the follow syntax :
     * earName + "/" + simpleClassName + "/" + scope,
     * where the earName, simpleClassName and the scope are the method
     * arguments.
     *
     * @param earName the enterprise archive name, it can not be null.
     * @param simpleClassName the simple class name, it can not be null..
     * @param scope the scope, it can not be null..
     * @return a String with a a given syntax.
     */
    private static String buildENC(String earName, String simpleClassName, String scope) {
        final StringBuffer result = new StringBuffer();
        result.append(earName);

        result.append("/");
        result.append(simpleClassName);

        result.append("/");
        result.append(scope);

        return result.toString();
    }
}
