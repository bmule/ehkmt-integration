/*
 * Project :iCardea
 * File : ArchiveHelper.java 
 * Encoding : UTF-8
 * Date : Apr 2, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.vocabulary.rest;

import java.io.File;
import java.util.HashSet;
import java.util.Set;



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
     * ".../target/to_lib/*.jar".
     * 
     * @return the path to the <code>commons-beanutils-1.8.3.jar</code> file.
     */
    static Set<File> getLibs() {
        final StringBuffer libPath = new StringBuffer();
        libPath.append("target");
        libPath.append(File.separatorChar);
        libPath.append("to_lib");
        final File libDir = new File(libPath.toString());
        final File[] files = libDir.listFiles();       
        final Set<File> result = new HashSet<File>();
        for (File file : files) {
            result.add(file);
        }

        return result;
    }
}
