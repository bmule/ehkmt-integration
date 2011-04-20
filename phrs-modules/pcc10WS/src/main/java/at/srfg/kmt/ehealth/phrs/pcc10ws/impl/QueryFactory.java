/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Project :iCardea
 * File : QueryFactory.java 
 * Encoding : UTF-8
 * Date : Apr 8, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.pcc10ws.impl;


import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.hl7.v3.*;


/**
 * Use one of the PCC10Factory implementation.
 * 
 * @version 0.1
 * @since 0.1
 * @author Mihai
 */
@Deprecated
final class QueryFactory {

    /**
     * The file used to build an empty <code>MCCIIN000002UV01</code> acknowledge.
     */
    private final static String PCC10_EMPTY_OUTPUT_FILE = "PCC-10-Empty-Output.xml";

    /**
     * Builds an empty PCC10 (MCCIIN000002UV01) answer answer based on a 
     * default template, the default template represents an empty query
     * acknowledge.
     * 
     * @return an empty PCC10 (MCCIIN000002UV01) query.
     * @throws JAXBException by any XML parsing problem encounter during the 
     * template parsing.
     */
    static QUPCIN043200UV01 buildQUPCIN043200UV01() throws JAXBException {
        final QUPCIN043200UV01 result = buildQUPCIN043200UV01(PCC10_EMPTY_OUTPUT_FILE);
        return result;
    }
    
    static QUPCIN043200UV01 buildQUPCIN043200UV01(String fileName) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance("org.hl7.v3");
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final InputStream inputStream = getStream(fileName);
        
        final QUPCIN043200UV01 result =
                (QUPCIN043200UV01) unmarshaller.unmarshal(inputStream);
        
        return result;
    }
    
    private static InputStream getStream(String name) {
        final ClassLoader classLoader =
                QueryFactory.class.getClassLoader();
        
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
