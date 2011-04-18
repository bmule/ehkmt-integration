/*
 * Project :iCardea
 * File : QueryFactory.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10BuildException;
import at.srfg.kmt.ehealth.phrs.pcc10ws.api.PCC10Factory;
import java.io.InputStream;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import net.sf.json.JSONObject;
import org.hl7.v3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Builds empty PCC10 requests (<code>QUPCIN043200UV01</code>).
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
final class DefaultPCC10RequestFactory implements PCC10Factory<QUPCIN043200UV01> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.DefaultPCC10ResponseFactory</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DefaultPCC10RequestFactory.class);

    /**
     * The file used to build an empty <code>QUPCIN043200UV01</code> acknowledge.
     */
    private final static String PCC10_EMPTY_INPUT_FILE = "PCC-10-Empty-Input.xml";

    /**
     * Builds a <code>DefaultPCC10ResponseFactory</code> instance.
     */
    DefaultPCC10RequestFactory() {
        // UNIMPLEMENTED
    }
    
    /**
     * Builds an empty PCC10 (QUPCIN043200UV01) request based on a 
     * default template, the default template represents an empty query
     * acknowledge. </br>
     * This method can not be overwritten.
     * 
     * @return an empty PCC10 (QUPCIN043200UV01) query.
     * @throws PCC10BuildException by any XML parsing problem encounter during
     * the template parsing.
     */
    @Override
    public final QUPCIN043200UV01 build() throws PCC10BuildException {
        final QUPCIN043200UV01 result;
        try {
            final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final InputStream inputStream = getStream(PCC10_EMPTY_INPUT_FILE);

            result = (QUPCIN043200UV01) unmarshaller.unmarshal(inputStream);

            return result;

        } catch (Exception exception) {
            final PCC10BuildException responseException = 
                    new PCC10BuildException(exception);
            LOGGER.error(exception.getMessage(), responseException);
            throw responseException;
        }

    }

    private InputStream getStream(String name) {
        final ClassLoader classLoader =
                DefaultPCC10RequestFactory.class.getClassLoader();

        final InputStream inputStream =
                classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            final String msg =
                    String.format("The %s must be placed in the classpath", name);
            throw new IllegalStateException(msg);
        }

        return inputStream;
    }
}
