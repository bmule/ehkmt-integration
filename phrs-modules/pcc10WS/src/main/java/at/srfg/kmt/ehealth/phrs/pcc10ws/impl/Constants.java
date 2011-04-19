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
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class Constants {
        /**
     * This the URL for the web service end point able to serve PCC09 requests.
     * 
     */
    static final String DEFAULT_PCC_10_END_POINT;

    static {
        final ClassLoader classLoader = Constants.class.getClassLoader();
        final InputStream stream = 
                classLoader.getResourceAsStream("constants.properites");
        final Properties properties = new Properties();
        if (stream != null) {
            try {
                properties.load(stream);
            } catch (IOException iOException) {
                // I don't care. I use the default
            }
        }
        final String key = "pcc10EndPoint";
        DEFAULT_PCC_10_END_POINT = properties.contains(key) 
                ? properties.getProperty(key).toString() 
                : "http://127.0.0.1:8080/pcc10ws/QUPCAR004030UV_Service";
    }
}
