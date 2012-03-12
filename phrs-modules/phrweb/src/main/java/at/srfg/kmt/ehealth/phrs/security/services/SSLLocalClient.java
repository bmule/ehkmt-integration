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

    public static void sslSetup(String trustStore, String trustStorePassword,String keyStore, String keyStorePassword) {

        LOGGER.debug("sslSetup trustStore=" + trustStore + " p=" + trustStorePassword+" keyStore "+keyStore+" pass "+keyStorePassword);
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

        final Provider provider = new Provider();
        Security.addProvider(provider);

        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);

        LOGGER.debug("The SSL comunication was enabled");
    }

    public static void sslSetupLocal() {

        final String home=  ResourceBundle.getBundle("icardea").getString("icardea.home");
        final String trustStore=home + ConfigurationService.getInstance().getProperty("javax.net.ssl.trustStore");
        final String keyStore=  home + ConfigurationService.getInstance().getProperty("javax.net.ssl.keyStore");

        final String passTrust=ConfigurationService.getInstance().getProperty("javax.net.ssl.trustStorePassword");
        final String passKey=ConfigurationService.getInstance().getProperty("javax.net.ssl.keyStorePassword");

        LOGGER.debug("sslSetupLocal trustStore {} pass {} keystore {} pass {}",new Object[]{trustStore,passTrust,keyStore,passKey});
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStorePassword", passKey);

        System.setProperty("javax.net.ssl.trustStore",trustStore);

        System.setProperty("javax.net.ssl.trustStorePassword", passTrust);


    }

    /**
     *
     * @param configSettings - this is no longer necessary
     */
    public static void sslSetup(int configSettings) {
       sslSetupLocal();
    }
    public static void sslSetup() {
        sslSetupLocal();
    }
}
