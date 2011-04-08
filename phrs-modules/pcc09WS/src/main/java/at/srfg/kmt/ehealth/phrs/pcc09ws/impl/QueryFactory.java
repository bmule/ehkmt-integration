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
import org.hl7.v3.AcknowledgementType;
import org.hl7.v3.CS;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.MCCIMT000200UV01Acknowledgement;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.QUPCIN043100UV01;


/**
 * Contains a set of common used methods used to create and manipulate 
 * PCC09 queries (QUPCIN043100UV01) and the related acknowledge 
 * (MCCIIN000002UV01).
 * 
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
public class QueryFactory {

    private static final String ALWAYS = "AL";

    /**
     * The file used to build an empty <code>QUPCIN043100UV01</code> query.
     */
    private final static String PCC9_EMPTY_INPUT_FILE = "PCC-9-Empty-Input.xml";

    /**
     * The file used to build an empty <code>MCCIIN000002UV01</code> acknowledge.
     */
    private final static String PCC9_EMPTY_OUTPUT_FILE = "PCC-9-Empty-Output.xml";

    /**
     * The only instance for <code>ObjectFactory</code> - JAXB related.
     */
    private final static ObjectFactory OBJECT_FACTORY = new ObjectFactory();

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

    public static MCCIIN000002UV01 buildMCCIIN000002UV01WithAcceptAckCodeError() throws JAXBException {
        final MCCIIN000002UV01 result = buildMCCIIN000002UV01();
        // Coded Simple Value
        // Coded data in its simplest form, where only the code is not
        // predetermined.
        // CE = Code Error

        final CS cs = new CS();
        cs.setCode("CE");

        result.setAcceptAckCode(cs);

        return result;
    }

    public static void setAcceptAckCodeError(MCCIIN000002UV01 result) {

        final MCCIMT000200UV01Acknowledgement akAcknowledgement =
                OBJECT_FACTORY.createMCCIMT000200UV01Acknowledgement();

        final AcknowledgementType typeCode = AcknowledgementType.CE;
        akAcknowledgement.setTypeCode(typeCode);
        result.getAcknowledgement().add(akAcknowledgement);
    }

    /**
     * Returns true if the specified <code>QUPCIN043100UV01</code> contains an
     * "always" acknowledge code.
     * 
     * @param query the query to prove.
     * @return true if the specified <code>QUPCIN043100UV01</code> contains an
     * "always" acknowledge code.
     * @throws NullPointerException if the <code>query</code> argument is null.
     */
    public static boolean isAcknowledgementAlways(QUPCIN043100UV01 query) {

        if (query == null) {
            throw new NullPointerException("The query can nul be null.");
        }

        final CS acceptAckCodeCS = query.getAcceptAckCode();
        final String acceptAckCode = acceptAckCodeCS.getCode();
        return ALWAYS.equals(acceptAckCode);
    }

}
