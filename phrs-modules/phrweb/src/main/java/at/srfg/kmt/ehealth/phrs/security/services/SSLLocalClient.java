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
import java.util.ResourceBundle;

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
        System.out.println("sslSetup certPath=" + certPath + " p=" + password);
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
    public static void sslSetupLocal() {

        ConfigurationService config = ConfigurationService.getInstance();

        String keystoreFilePath = config.getProperty("phr_keystoreFilePath");

        String keystoreFilePassword = config.getProperty("phr_keystoreFilePassword");



        SSLLocalClient.sslSetup(keystoreFilePath, keystoreFilePassword);

    }

    /**
     * Issue sharing one Tomcat server setup, therefore use common shared setup until clear
     * By depending on the consent editor invoker, we are using that local sslsetup
     *
     * @param configSettings
     * 
     * 0= none, handled differently by caller
     * 1= local config
     * 2= partner config sharing with same container
     * 
     * Other - no ssl setup or handled elsewhere
     */
    public static void sslSetup(int configSettings) {
        
        if (configSettings == 1) {
            sslSetupLocal();
        } else if (configSettings == 2) {
            boolean atnatls = new Boolean(ResourceBundle.getBundle("icardea").getString("atna.tls")).booleanValue();
            if (atnatls) {
                // Properties for SSL Security Provider
                System.out.println("SECURE sslSetup 2");
                String protocolProp = "java.protocol.handler.pkgs";
                String sunSSLProtocol = "com.sun.net.ssl.internal.www.protocol";
                String sslStoreProp = "javax.net.ssl.trustStore";
                String certPath = ResourceBundle.getBundle("icardea").getString("icardea.home") + "/icardea-caremanager-ws/src/test/resources/jssecacerts";

                // Enable SSL communication
                System.setProperty(protocolProp, sunSSLProtocol);
                Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
                System.setProperty(sslStoreProp, certPath);
                System.setProperty("javax.net.ssl.trustStorePassword", "srdcpass");
            }
        }
        //otherwise no SSL

    }
}
