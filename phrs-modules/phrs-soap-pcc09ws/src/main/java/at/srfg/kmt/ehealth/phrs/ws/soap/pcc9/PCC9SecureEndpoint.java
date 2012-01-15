/*
 * Project :iCardea
 * File : PCC9Endpoint.java
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is en end point able to accept <a
 * href="http://wiki.ihe.net/index.php?title=PCC-9">PCC09</a> transactions.<br/>
 * The usage is simple : build an instance for a given host, port and context
 * path and then use the
 * <code>start</code> method.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class PCC9SecureEndpoint {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.PCC9Endpoint</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PCC9SecureEndpoint.class);

    private final String context;

    private final int port;

    private final String host;

    /**
     * Builds a end point for <a
     * href="http://wiki.ihe.net/index.php?title=PCC-9">PCC09</a> transactions
     * for a given list of arguments.
     *
     * @param host the host i.p. for the end point, it can not be null.
     * @param port the port for the end point,
     * @param path path i.p. for the end point, it can not be null.
     * @throws NullPointerException if the
     * <code>host</code> or the
     * <code>path</code> is null.
     */
    public PCC9SecureEndpoint(String host, int port, String context) {

        if (host == null) {
            final NullPointerException exception =
                    new NullPointerException("The host argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (context == null) {
            final NullPointerException exception =
                    new NullPointerException("The path argument can not be null.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        if (!context.startsWith("/")) {
            this.context = "/" + context;
        } else {
            this.context = context;
        }


        this.port = port;
        this.host = host;
    }

    private String buildURI(String host, int port, String context) {
        final StringBuilder result = new StringBuilder("https://");
        result.append(host);
        result.append(":");
        result.append(port);
        result.append(context);

        return result.toString();
    }

    public void start() throws Exception {
        start(host, port, context, "srfg-phrs-core-keystore.ks", "icardea");
    }

    private void start(String host, int port, String context,
            String keysoreFile, String keyStorePasswd) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        final SSLContext ssl;
        try {
            ssl = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final KeyManagerFactory keyFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        final KeyStore store;
        try {
            store = KeyStore.getInstance("JKS");
        } catch (KeyStoreException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final ClassLoader classLoader = PCC9SecureEndpoint.class.getClassLoader();
        final InputStream stream =
                classLoader.getResourceAsStream(keysoreFile);
        if (stream == null) {
            final String msg =
                    String.format("The key store file named %s can not be located in the classpath.", keysoreFile);
            final NullPointerException exception = new NullPointerException(msg);
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        try {
            store.load(stream, keyStorePasswd.toCharArray());
        } catch (IOException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        } catch (CertificateException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        try {
            keyFactory.init(store, keyStorePasswd.toCharArray());
        } catch (UnrecoverableKeyException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final TrustManagerFactory trustFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

        trustFactory.init(store);
        try {
            ssl.init(keyFactory.getKeyManagers(),
                    trustFactory.getTrustManagers(), new SecureRandom());
        } catch (KeyManagementException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        final HttpsConfigurator configurator = new HttpsConfigurator(ssl);
        final HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(host, port), port);

        httpsServer.setHttpsConfigurator(configurator);

        final HttpContext httpContext = httpsServer.createContext(context);

        httpsServer.start();

        final QUPCAR004040UVWebService webService = new QUPCAR004040UVWebService();
        final String endpointURI = buildURI(host, port, context);
        LOGGER.info("PCC9 Safe endpoint runs on {}", endpointURI);
        final Endpoint endpoint = Endpoint.create(endpointURI, webService);
        endpoint.publish(httpContext);
    }
}
