/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.srfg.kmt.ehealth.phrs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
    static String ip;
    static int port;
    static File tsFile;
    static String tsPassword;

    @BeforeClass
    public static void setup() {
        LOGGER.info("TEST SETUP");
        ip = "localhost";
        port = 8089;
        tsPassword = "icardea";
        tsFile = new File("D:\\git-clones\\ehkmt-integration\\phrs-modules\\phrs-soap-pcc09ws\\src\\main\\resources\\srfg-phrs-core-keystore.ks");


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
    public void testPortOpen() throws Exception {
        LOGGER.info("TEST TCP PORT {}:{}", ip, port);
        assert PlainHttpsClient.socketConnect(ip, port);
    }

    /**
     * Test of connect method, of class PlainHttpsClient.
     */
    @Test
    public void testHttpsConnect() throws Exception {
        LOGGER.info("TEST HTTPS SERVER https://{}:{}", ip, port);
        assert PlainHttpsClient.connect(ip, port);
    }

    /**
     * Test of connect method, of class PlainHttpsClient.
     */
    @Test
    public void testUdpReachable() throws Exception {
        LOGGER.info("checking udp port reachabilty: {}:{}", "localhost", 44444);
        assert PlainHttpsClient.sendUDP("localhost", 44444);
    }
}
