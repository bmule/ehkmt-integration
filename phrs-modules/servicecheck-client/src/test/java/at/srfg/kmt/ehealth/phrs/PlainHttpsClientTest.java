/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fstrohmeier
 */
public class PlainHttpsClientTest {

    final static Logger LOGGER = LoggerFactory.getLogger(PlainHttpsClientTest.class);
    static File tsFile;
    static String tsPassword;
    static PropertiesConfiguration icardeaConfig;
    static PropertiesConfiguration phrsConfig;
    static Set<URL> testURLs = new HashSet<URL>();

    @BeforeClass
    public static void setup() {
        LOGGER.info("TEST SETUP");


        try {
            icardeaConfig = new PropertiesConfiguration("icardea.properties");
            LOGGER.info("Config input" + icardeaConfig);
            Iterator<String> it = icardeaConfig.getKeys();

            while (it.hasNext()) {
                String key = it.next();
                String value = icardeaConfig.getProperty(key).toString();
                try {
                    testURLs.add(new URL(value));
                    LOGGER.info("-> {} added to test ({})", key, value);
                } catch (MalformedURLException ex) {
                    LOGGER.info("--> {} ignored", key);
                }
            }

            phrsConfig = new PropertiesConfiguration(new File("../phrweb/src/main/resources/phrs.properties"));
            LOGGER.info("Config input" + phrsConfig);
            String pixUrlStr = "https://" + phrsConfig.getString("pix.host") + ":" + phrsConfig.getString("pix.port");
            try {
                testURLs.add(new URL(pixUrlStr));
                LOGGER.info("-> pix.host/pix.port added to test ({})", pixUrlStr);
            } catch (MalformedURLException ex) {
                LOGGER.info("--> pix.host/pix.port ignored");
            }


        } catch (ConfigurationException e) {
            LOGGER.error("ConfigurationService error", e);
        }


        tsPassword = "icardea";
        tsFile = new File(".." + File.separator
                + "phrs-soap-pcc09ws" + File.separator
                + "src" + File.separator
                + "main" + File.separator
                + "resources" + File.separator
                + "srfg-phrs-core-keystore.ks");


        if (System.getProperty("javax.net.ssl.trustStore") == null) {
            System.setProperty("javax.net.ssl.trustStore", tsFile.getAbsolutePath());
            LOGGER.info("TrustStore set to: {}", System.getProperty("javax.net.ssl.trustStore"));
        }
        if (System.getProperty("javax.net.ssl.trustStorePassword") == null) {
            System.setProperty(
                    "javax.net.ssl.trustStorePassword", tsPassword);
            LOGGER.info("TrustStorePassword set to: {}", System.getProperty("javax.net.ssl.trustStorePassword"));
        }
    }

    /**
     * Test of connect method, of class PlainHttpsClient.
     */
    @Test
    public void testTrustStore() {
        LOGGER.info("TESTING TRUSTSTORE");
        if (tsFile == null) {
            LOGGER.warn("No truststore configured!");
            assert false;
        } else {
            LOGGER.info("Found truststore configuration: {}", tsFile);
        }

        if (!tsFile.canRead()) {
            LOGGER.warn("Can't read truststore file {}", tsFile.getAbsoluteFile());
            assert false;
        }

        if (tsPassword == null) {
            LOGGER.warn("No truststore password configured!");
            assert false;
        } else {
            LOGGER.info("Found truststore password config: {}", tsPassword);
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("keytool", "-list", "-keystore", tsFile.getAbsolutePath(), "-storepass", tsPassword, "-v");
            Process p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = in.readLine();
            LOGGER.info("Content of Truststore {}", tsFile.getName());
            while (line != null) {
                if (line.length() > 50) {
                    line = line.substring(0, 50) + "...";
                }
                LOGGER.info("-> {}", line);
                line = in.readLine();
            }
            in.close();
            if (p.exitValue() == 0) {
                LOGGER.info("...OK");
                assert true;
            } else {
                assert false;
            }
        } catch (IOException ex) {
            LOGGER.warn("Can't show truststore: {}", ex.getMessage());
            assert false;
        }
    }

    /**
     * Test of socket connect method, of class PlainHttpsClient.
     */
    @Test
    public void testSocketConnect() throws Exception {
        LOGGER.info("TEST TCP PORTS");
        boolean allOK = true;
        for (URL serverURL : testURLs) {
            String host = serverURL.getHost();
            int port = serverURL.getPort();
            if (port > 0) {
                LOGGER.info("testing {}:{}", host, port);
                allOK = PlainHttpsClient.socketConnect(host, port);
            } else {
                LOGGER.info("skipped testing {}", serverURL);
            }
        }
        assert allOK;
    }

    /**
     * Test of connect method, of class PlainHttpsClient.
     */
    @Test
    public void testHttpsConnect() throws Exception {
        LOGGER.info("TEST HTTPS SERVERS");
        boolean allOK = true;
        for (URL serverURL : testURLs) {
            if (serverURL.getProtocol().equals("https")) {
                LOGGER.info("testing {}", serverURL);
                allOK = PlainHttpsClient.connect(serverURL);
            } else {
                LOGGER.info("skipped testing {}", serverURL);
            }

        }
        assert allOK;
    }

    /**
     * Test of connect method, of class PlainHttpsClient.
     */
    @Test
    public void testSendUDP() throws Exception {
        LOGGER.info("checking udp port reachabilty: {}:{}", "localhost", 44444);
        assert PlainHttpsClient.sendUDP("localhost", 44444);
    }
}
