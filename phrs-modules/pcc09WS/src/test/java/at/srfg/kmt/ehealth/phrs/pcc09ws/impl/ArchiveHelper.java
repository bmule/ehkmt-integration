/*
 * Project :iCardea
 * File : ArchiveHelper.java 
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;

import java.io.File;



/**
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

    static File getQUPC_AR004040UV_ServiceLib() {
        final StringBuffer filePath = new StringBuffer();
        filePath.append("target");
        filePath.append(File.separatorChar);
        filePath.append("pcc09");
        filePath.append(File.separatorChar);
        filePath.append("QUPC_AR004040UV_Service-0.1.jar");
        final File file = new File(filePath.toString());

        final String msg = String.format("The file %s is can not be located.", filePath);
        if (!file.exists()) {
            throw new AssertionError(msg);
        }

        return file;
    }
}
