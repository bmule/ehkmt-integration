/*
 * Project :iCardea
 * File : Constants.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Contains all the constants used in the PCC09 WS module.
 * The value for some contains can be configurated via a properties file named
 * 'configuration.properties' placed in the classpath.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class Constants {

    /**
     * This the URL for the web service end point able to serve PCC09 requests.
     * The web service behind this end point is a SOAP based web service. </br>
     * Its value can be configurated with by changing the value associated to
     * the key <b>pcc09EndPoint</b>. Its default value is :
     * <code>http://127.0.0.1:8080/pcc09ws/QUPCAR004040UV_Service</code>.
     * 
     */
    static final String DEFAULT_PCC_09_END_POINT;

    /**
     * This the URL for the web service end point able to trigger an  PCC10 
     * transaction. The web service behind this end point is a REST based web
     * service.</br>
     * Its value can be configurated with by changing the value associated to
     * the key <b>pcc10notifyEndPoint</b>. Its default value is :
     * <code>"http://localhost:8080/pcc10ws/restws/pcc10/notify</code>.
     */
    static final String PC10_NOTIFY_END_POINT;
    //"http://localhost:8080/pcc10ws/restws/pcc10/notify";

    /**
     * Used to load the default values for the constants - if this is required.
     */
    static {
        final ClassLoader classLoader = Constants.class.getClassLoader();
        final InputStream stream =
                classLoader.getResourceAsStream("constants.properties");
        final Properties properties = new Properties();
        if (stream != null) {
            try {
                properties.load(stream);
            } catch (IOException ioException) {
                // I don't care. I use the default
            }
        }

        final String wsdlPCC09key = "pcc09EndPoint";
        DEFAULT_PCC_09_END_POINT = properties.getProperty(wsdlPCC09key,
                "http://127.0.0.1:8080/pcc09ws/QUPCAR004040UV_Service").toString().trim();

        final String restPCC10key = "pcc10notifyEndPoint";
        PC10_NOTIFY_END_POINT = properties.getProperty(restPCC10key,
                "http://localhost:8080/pcc10ws/restws/pcc10/notify").toString().trim();
    }
}
