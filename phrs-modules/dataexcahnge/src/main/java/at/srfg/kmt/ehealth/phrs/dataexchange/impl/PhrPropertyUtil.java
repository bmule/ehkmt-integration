/*
 * Project :iCardea
 * File : PhrPropertyUtil.java
 * Date : Dec 15, 2010
 * User : mradules
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import static at.srfg.kmt.ehealth.phrs.dataexchange.impl.PhrBeanConstants.*;

/**
 * A sum of common purposed methods related with the PHRS Bean and its
 * properties.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class PhrPropertyUtil {

    /**
     * Extracts the name from a full name string; 
     * the full name contains the name space and the component name.
     * 
     * @param fullName the full name, form here is the name extracted,
     * it can not be null.
     * @return the name from a full name string.
     * @throws NullPointerExceptionif the <code>fullName</code> argument is null.
     */
    public static String getName(String fullName) {

        if (fullName == null) {
            throw new NullPointerException("The name argumetn can not be null.");
        }

        final int indexOf = fullName.indexOf(NAME_TOKEN);
        if (indexOf < 0) {
            return fullName;
        }

        final String result = fullName.substring(indexOf + NAME_TOKEN.length(), fullName.length());
        return result.trim().isEmpty() ? null : result;
    }

    /**
     * Extracts the name space from a full name string; 
     * the full name contains the name space and the component name.
     * 
     * @param fullName the full name, form here is the name space extracted,
     * it can not be null.
     * @return the name space from a full name string.
     */
    public static String getNamespace(String fullName) {

        if (fullName == null) {
            throw new NullPointerException("The name argumetn can not be null.");
        }

        final int indexOf = fullName.indexOf(NAME_TOKEN);
        if (indexOf < 0) {
            return null;
        }

        final String result = fullName.substring(0, indexOf);
        return result.trim().isEmpty() ? null : result;
    }

    /**
     * Builds a full name from two different parts, the (component) name an the
     * name space.
     * 
     * @param ns the name space, it can be null, if is null then the default
     * name space is used.
     * @param name the name, it can not be null.
     * @return the full name for the tow given parts.
     * @throws NullPointerException if the <code>name</code> argument is null.
     */
    public static String buildName(String ns, String name) {

        if (name == null) {
            throw new NullPointerException("The name argumetn can not be null.");
        }

        final StringBuilder result = new StringBuilder();
        result.append(ns == null ? DEFAULT_NS : ns);
        result.append(NAME_TOKEN);
        result.append(name);

        return result.toString();
    }
}
