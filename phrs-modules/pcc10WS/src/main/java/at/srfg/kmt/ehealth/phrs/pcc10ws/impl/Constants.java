/*
 * Project :iCardea
 * File : Constants.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Contains all the constants used in the PCC10 WS module.
 * The value for some contains can be configurated via a properties file named
 * 'configuration.properties' placed in the classpath.
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class Constants {

    /**
     * This the URL for the web service end point able to serve PCC10 requests.
     * Its value can be configurated with by changing the value associated to
     * the key <b>pcc10EndPoint</b>. Its default value is :
     * <code>http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service</code>.
     * 
     */
    static final String DEFAULT_PCC_10_END_POINT;

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
            } catch (IOException iOException) {
                // I don't care. I use the default
            }
        }
        final String key = "pcc10EndPoint";
        DEFAULT_PCC_10_END_POINT = 
                properties.getProperty(key, 
                "http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service").toString().trim();;
    }
}
