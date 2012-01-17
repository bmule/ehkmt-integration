/*
 * Project :iCardea
 * File : SSLClient.java
 * Encoding : UTF-8
 * Date : Jan 16, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import com.sun.net.ssl.internal.ssl.Provider;
import java.security.Security;


/**
 *
 * @author mradules
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class SSLClient {
    
    private static final String HANDLER_PKGS = 
            "java.protocol.handler.pkgs";
    private static final String SUN_SSL_PROTOCOL = 
            "com.sun.net.ssl.internal.www.protocol";
    private static final String SSL_STORE_PROTOCOL = 
            "javax.net.ssl.trustStore";
    private static final String SSL_STORE_PASSWORD = 
            "javax.net.ssl.trustStorePassword";

    static void sslSetup(String certPath, String password) {
        System.setProperty(HANDLER_PKGS, SUN_SSL_PROTOCOL);
        
        final Provider provider = new Provider();
        Security.addProvider(provider);
        
        System.setProperty(SSL_STORE_PROTOCOL, certPath);
        System.setProperty(SSL_STORE_PASSWORD, password);
        
        System.out.println("The SSL comunication was enabled");
    }
}
