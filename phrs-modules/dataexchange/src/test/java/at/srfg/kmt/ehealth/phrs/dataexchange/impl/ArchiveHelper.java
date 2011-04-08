/*
 * Project :iCardea
 * File : ArchiveHelper.java 
 * Encoding : UTF-8
 * Date : Apr 2, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.impl;

import java.io.File;



/**
 * Encapsulates the path to the <code>commons-beanutils-1.8.3.jar</code> file.
 * This file is is required during tests, more precisely this file is add (like
 * library) to the dynamic builded ear file(s) required for tests. The maven 
 * copy the file on the path. Maven take care about the file location and coping.<br>
 * The abstract file path is : 
 * ".../target/shrinwrap/commons-beanutils-1.8.3.jar".
 *  
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
class ArchiveHelper {

    /**
     * Don't let anyone to instantiate this class.
     */
    private ArchiveHelper() {
        // UNIMPLEMETNED
    }

    /**
     * Returns the path to the <code>commons-beanutils-1.8.3.jar</code> file.
     * The abstract file path is : 
     * ".../target/shrinwrap/commons-beanutils-1.8.3.jar".
     * 
     * @return the path to the <code>commons-beanutils-1.8.3.jar</code> file.
     */
    static File getCommonsBeanUtils() {
        final StringBuffer filePath = new StringBuffer();
        filePath.append("target");
        filePath.append(File.separatorChar);
        filePath.append("shrinwrap");
        filePath.append(File.separatorChar);
        filePath.append("commons-beanutils-1.8.3.jar");
        final File file = new File(filePath.toString());

        final String msg = String.format("The file %s is can not be located.", filePath);
        if (!file.exists()) {
            throw new AssertionError(msg);
        }

        return file;
    }
}
