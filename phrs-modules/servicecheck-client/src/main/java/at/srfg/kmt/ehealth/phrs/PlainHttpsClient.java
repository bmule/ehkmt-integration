package at.srfg.kmt.ehealth.phrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.security.cert.Certificate;
import javax.net.ssl.HttpsURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTPS Client to test server functionality in order to find reasons for
 * failures!
 *
 */
public class PlainHttpsClient {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PlainHttpsClient.class);

    public static boolean connect(String host, int port) {

        final String serverURL = "https://" + host + ":" + port;
        return connect(serverURL);
    }
 
    public static boolean connect(String serverURL) {
     
        try {
            return connect(new URL(serverURL));
        } catch (MalformedURLException ex) {
            LOGGER.error("URL {} not valid", serverURL);
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getLocalizedMessage());
            return false;
            
        }

    }
    public static boolean connect(URL serverURL) {
            try {

            HttpsURLConnection conn = (HttpsURLConnection) serverURL.openConnection();


            conn.connect();

            for (Certificate cert : conn.getLocalCertificates()) {
                LOGGER.info("Found local certificate: {}", cert.toString());
            }


            // Create streams to securely send and receive data to the server
            InputStream in2 = conn.getInputStream();
            OutputStream out = conn.getOutputStream();

            // Read from in and write to out...
            // nothing to send or receive...
            // Close the socket
            in2.close();
            out.close();
            LOGGER.info("...OK");
            return true;

        } catch (UnknownHostException ex) {
            LOGGER.error("Host {} unknown", serverURL);
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getLocalizedMessage());
            return false;
        } catch (ConnectException ex) {
            LOGGER.error("TCP connection faild to {}", serverURL);
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getLocalizedMessage());
            return false;
        } catch (IOException ex) {
            LOGGER.error("IOException {} ", ex.getMessage());
            LOGGER.error("...FAIL");
            if (ex.getMessage().contains("NoSuchAlgorithmException")) {
                LOGGER.error("Hint: double-check password");
            }
            LOGGER.trace("{}", ex.getLocalizedMessage());
            return false;
        }
    }

    static boolean socketConnect(String host, int port) {
        try {
            Socket s = new Socket(host, port);
            s.close();
            LOGGER.info("...OK");
        } catch (UnknownHostException ex) {
            LOGGER.error("Host {} unknown", host);
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getLocalizedMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Port unreachable {} ", port);
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getStackTrace());
            return false;
        }catch (IOException ex) {
            LOGGER.error("IOException {} ", ex.getMessage());
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getStackTrace());
            return false;
        }
        return true;
    }

    static boolean sendUDP(String host, int port) {
        try {
            byte[] testmsg = {'a', 'b', 'c'};
            InetAddress ip = InetAddress.getByName(host);
            DatagramPacket dp = new DatagramPacket(testmsg, testmsg.length, ip, port);
            DatagramSocket ds = new DatagramSocket();
            ds.send(dp);
            ds.close();
            LOGGER.info("...OK");
            return true;
        } catch (SocketException ex) {
            LOGGER.error("SocketException {} ", ex.getMessage());
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getStackTrace());
            return false;
        } catch (UnknownHostException ex) {
            LOGGER.error("Host {} unknown", host);
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getLocalizedMessage());
            return false;
        } catch (IOException ex) {
            LOGGER.error("IOException {} ", ex.getMessage());
            LOGGER.error("...FAIL");
            LOGGER.trace("{}", ex.getStackTrace());
            return false;
        }
    }
}
