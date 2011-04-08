/*
 * Project :iCardea
 * File : QueryFactory.java 
 * Encoding : UTF-8
 * Date : Apr 7, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc09ws.impl;


import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.QUPCIN043100UV01;


/**
 *
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class QueryFactory {

    /**
     * The file used to build an empty <code>QUPCIN043100UV01</code> query.
     */
    private final static String PCC9_EMPTY_INPUT_FILE = "PCC-9-Empty-Input.xml";

    /**
     * The file used to build an empty <code>MCCIIN000002UV01</code> acknowledge.
     */
    private final static String PCC9_EMPTY_OUTPUT_FILE = "PCC-9-Empty-Output.xml";

    /**
     * Builds an empty PCC09 (QUPCIN043100UV01) query based on a default template,
     * the default template represents an empty query.
     * 
     * @return an empty PCC09 (QUPCIN043100UV01) query.
     * @throws JAXBException by any XML parsing problem encounter during the 
     * template parsing.
     */
    public static QUPCIN043100UV01 buildQUPCIN043100UV01() throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final InputStream inputStream = getStream(PCC9_EMPTY_INPUT_FILE);

        // this is a Care Record Event Profile Query
        final QUPCIN043100UV01 query =
                (QUPCIN043100UV01) unmarshaller.unmarshal(inputStream);
        return query;
    }

    /**
     * Builds an empty PCC09 (QUPCIN043100UV01) query acknowledge based on a 
     * default template, the default template represents an empty query
     * acknowledge.
     * 
     * @return an empty PCC09 (QUPCIN043100UV01) query.
     * @throws JAXBException by any XML parsing problem encounter during the 
     * template parsing.
     */
    public static MCCIIN000002UV01 buildMCCIIN000002UV01() throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final InputStream inputStream = getStream(PCC9_EMPTY_OUTPUT_FILE);

        final MCCIIN000002UV01 result =
                (MCCIIN000002UV01) unmarshaller.unmarshal(inputStream);

        return result;
    }

    private static InputStream getStream(String name) {
        final ClassLoader classLoader =
                QueryFactory.class.getClassLoader();

        final InputStream inputStream =
                classLoader.getResourceAsStream(name);
        if (inputStream == null) {
            final String msg = String.format("The %s must be placed in the classpath",
                    name);
            throw new IllegalStateException(msg);
        }

        return inputStream;

    }
}
