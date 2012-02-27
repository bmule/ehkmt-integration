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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;


final class SSLLocalClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(SSLLocalClient.class);
    private static final String HANDLER_PKGS =
            "java.protocol.handler.pkgs";
    private static final String SUN_SSL_PROTOCOL =
            "com.sun.net.ssl.internal.www.protocol";
    private static final String SSL_STORE_PROTOCOL =
            "javax.net.ssl.trustStore";
    private static final String SSL_STORE_PASSWORD =
            "javax.net.ssl.trustStorePassword";

    public static void sslSetup(String certPath, String password) {
        LOGGER.debug("sslSetup certPath=" + certPath + " p=" + password);
        System.setProperty(HANDLER_PKGS, SUN_SSL_PROTOCOL);

        final Provider provider = new Provider();
        Security.addProvider(provider);

        System.setProperty(SSL_STORE_PROTOCOL, certPath);
        System.setProperty(SSL_STORE_PASSWORD, password);

        LOGGER.debug("The SSL comunication was enabled");
    }

    /**
     javax.net.ssl.trustStore=srfg-phrs-web-keystore.ks
     javax.net.ssl.trustStorePassword=icardea
     */
    public static void sslSetupLocal() {

        ConfigurationService config = ConfigurationService.getInstance();

        String keystoreFilePath = config.getProperty("javax.net.ssl.trustStore","srfg-phrs-web-keystore.ks");

        String keystoreFilePassword = config.getProperty("javax.net.ssl.trustStorePassword","icardea");


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
 /*
        String openIdTest = ConfigurationService.getInstance().getProperty("openid.test.url", "http://kmt23.salzburgresearch.at:4545/idp/u=bob");
        System.out.println("LoginServiceImpl  openid.test.url  " + openIdTest);
        System.out.println("LoginServiceImpl  getEndpointApplicationHome " + LoginUtils.getEndpointApplicationHome());
        System.out.println("LoginServiceImpl  getEndpointLoginPage " + LoginUtils.getEndpointLoginPage());
   */
        if (configSettings < 3) {
            sslSetupLocal();

        } else if (configSettings == 3) {
            // boolean atnatls = new Boolean(ResourceBundle.getBundle("icardea").getString("atna.tls")).booleanValue();
            boolean atnatls = Boolean.parseBoolean(ResourceBundle.getBundle("icardea").getString("atna.tls"));

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
