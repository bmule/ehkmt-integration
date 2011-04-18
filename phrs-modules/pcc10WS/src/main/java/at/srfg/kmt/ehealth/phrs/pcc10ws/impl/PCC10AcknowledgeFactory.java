/*
 * Project :iCardea
 * File : PCC10AcknowledgeFactory.java 
 * Encoding : UTF-8
 * Date : Apr 17, 2011
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
import org.hl7.v3.MCCIIN000002UV01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
class PCC10AcknowledgeFactory implements PCC10Factory<MCCIIN000002UV01> {

    /**
     * The Logger instance. All log messages from this class
     * are routed through this member. The Logger name space
     * is <code>at.srfg.kmt.ehealth.phrs.pcc09ws.impl.DefaultPCC10ResponseFactory</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DefaultPCC10RequestFactory.class);

    /**
     * The file used to build an empty <code>MCCIIN000002UV01</code> acknowledge.
     */
    private final static String PCC10_EMPTY_OUTPUT_FILE = "PCC-10-Empty-Output.xml";

    /**
     * Builds a <code>PCC10AcknowledgeFactory</code> instance.
     */
    PCC10AcknowledgeFactory() {
    }

    /**
     * Builds a default PCC10 acknowledge (MCCIIN000002UV01)  based on a 
     * default template. The templates contains an successfully processed request
     * acknowledge.</br>
     * This method can not be overwritten.
     * 
     * @return an empty PCC10 (MCCIIN000002UV01) query.
     * @throws PCC10BuildException by any XML parsing problem encounter during
     * the template parsing.
     */
    @Override
    public MCCIIN000002UV01 build() throws PCC10BuildException {
        final MCCIIN000002UV01 result;
        try {
            final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final InputStream inputStream = getStream(PCC10_EMPTY_OUTPUT_FILE);

            result = (MCCIIN000002UV01) unmarshaller.unmarshal(inputStream);

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
