/*
 * Project :iCardea
 * File : PatternBasedDataConverter.java 
 * Encoding : UTF-8
 * Date : Apr 15, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.dataexchange.ws;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
class PatternBasedDateConverter implements Converter {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.security.impl.PatternBasedDataConverter</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DynamicBeanRepositoryRestWS.class);

    private final DateFormat dateFormat;

    PatternBasedDateConverter(String pattern) {
        dateFormat = new SimpleDateFormat(pattern);
    }

    @Override
    public Object convert(Class type, Object o) {

            if (o == null) {
                return null;
            }
            
            if ("null".equals(o)) {
                return null;
            }
            
            if (o.toString().trim().isEmpty()) {
                return null;
            }

            if (o instanceof String && type == Date.class) {
                final Date result;
                try {
                    result = dateFormat.parse(o.toString());
                } catch (ParseException ex) {
                    LOGGER.error("The date [{}] can not be converted.", o);
                    LOGGER.error(ex.getMessage(), ex);
                    return null;
                }

                return result;
            }

            return null;


    }
}
