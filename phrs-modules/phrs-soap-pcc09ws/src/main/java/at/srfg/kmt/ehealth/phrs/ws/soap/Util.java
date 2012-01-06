/*
 * Project :iCardea
 * File : Util.java
 * Encoding : UTF-8
 * Date : Dec 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap;


import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;


/**
 * Contains a set of common purposed methods related with the SOAP message.
 * <br/> This class is not designed to be extend.
 *
 * @author Miahai
 * @version 0.1
 * @since 0.1
 */
final class Util {

    /**
     * Don't let anyone to instantiate this class.
     */
    private Util() {
        // UNIMPLEMENTED
    }

    /**
     * Serialize a <code>org.w3c.dom.Node</code> in to a string, this string
     * represents a well formated XML. This methods can be applied on the
     * SOPA header and SOAP body (because both are extending the 
     * <code>org.w3c.dom.Node</code>) node interface.
     * 
     * 
     * @param node the note to be serialize, it can not be null.
     * @return
     * @throws ParserConfigurationException if the header can not be (XML)
     * parsed.
     * @throws TransformerConfigurationException by any XML transformation 
     * related error.
     * @throws TransformerException by any XML transformation related error.
     * @throws NullPointerException if the <code>header</code> argument is null.
     */
    static String toString(Node node) throws
            ParserConfigurationException, TransformerConfigurationException,
            TransformerException {
        
        if (node == null) {
            throw new  NullPointerException("The node argument can not be null.");
        }

        final StringWriter stringWriter = new StringWriter();
        final StreamResult streamResult = new StreamResult(stringWriter);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.transform(new DOMSource(node), streamResult);

        final String result = stringWriter.toString();
        return result;
    }
}
