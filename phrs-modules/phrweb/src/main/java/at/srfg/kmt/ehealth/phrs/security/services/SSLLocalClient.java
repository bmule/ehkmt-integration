/*
 * Project :iCardea
 * File : SSLClient.java
 * Encoding : UTF-8
 * Date : Jan 16, 2012
 * User : Mihai Radulescu, 
 * config: bmulreni
 */
package at.srfg.kmt.ehealth.phrs.security.services;

import com.sun.net.ssl.internal.ssl.Provider;
import java.security.Security;
import at.srfg.kmt.ehealth.phrs.presentation.services.ConfigurationService;

/**
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class SSLLocalClient {

    private static final String HANDLER_PKGS =
            "java.protocol.handler.pkgs";
    private static final String SUN_SSL_PROTOCOL =
            "com.sun.net.ssl.internal.www.protocol";
    private static final String SSL_STORE_PROTOCOL =
            "javax.net.ssl.trustStore";
    private static final String SSL_STORE_PASSWORD =
            "javax.net.ssl.trustStorePassword";

    public static void sslSetup(String certPath, String password) {
        System.out.println("sslSetup certPath="+certPath+ " p="+password);
        System.setProperty(HANDLER_PKGS, SUN_SSL_PROTOCOL);

        final Provider provider = new Provider();
        Security.addProvider(provider);

        System.setProperty(SSL_STORE_PROTOCOL, certPath);
        System.setProperty(SSL_STORE_PASSWORD, password);

        System.out.println("The SSL comunication was enabled");
    }
    /**
     * Read configuration file
     */
    public static void  sslSetup() {
        
        ConfigurationService config = ConfigurationService.getInstance();

        String keystoreFilePath = config.getProperty("phr_keystoreFilePath");
        
        String keystoreFilePassword = config.getProperty("phr_keystoreFilePassword");
        


        SSLLocalClient.sslSetup(keystoreFilePath, keystoreFilePassword);

    }
}
