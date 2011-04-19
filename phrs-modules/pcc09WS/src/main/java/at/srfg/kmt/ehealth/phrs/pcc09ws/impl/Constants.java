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
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class Constants {

    /**
     * This the URL for the soap based web service end point able to serve PCC09
     * requests.
     */
    static final String DEFAULT_PCC_09_END_POINT;

    /**
     * This is the end point where the pcc10 transaction can be triggered via a
     * REST based web service.
     */
    static final String PC10_NOTIFY_END_POINT;
    //"http://localhost:8080/pcc10ws/restws/pcc10/notify";

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

        final String wsdlPCC09key = "pcc09EndPoint";
        DEFAULT_PCC_09_END_POINT = properties.contains(wsdlPCC09key)
                ? properties.getProperty(wsdlPCC09key).toString()
                : "http://127.0.0.1:8080/pcc09ws/QUPCAR004040UV_Service";

        final String restPCC10key = "pcc10notifyEndPoint";
        PC10_NOTIFY_END_POINT = properties.contains(restPCC10key)
                ? properties.getProperty(restPCC10key).toString()
                : "http://localhost:8080/pcc10ws/restws/pcc10/notify";

    }
}
