/*
 * Project :iCardea
 * File : Util.java
 * Encoding : UTF-8
 * Date : Dec 22, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.hl7.v3.CD;
import org.hl7.v3.II;
import org.hl7.v3.PN;
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
     * Serialize a
     * <code>org.w3c.dom.Node</code> in to a string, this string represents a
     * well formated XML. This methods can be applied on the SOPA header and
     * SOAP body (because both are extending the
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
     * @throws NullPointerException if the
     * <code>header</code> argument is null.
     */
    static String toString(Node node) throws
            ParserConfigurationException, TransformerConfigurationException,
            TransformerException {

        if (node == null) {
            throw new NullPointerException("The node argument can not be null.");
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

    /**
     * Transforms a
     * <code>org.hl7.v3.II</code> in to a human readable string.
     *
     * @param ii the involved
     * <code>org.hl7.v3.II</code> instance.
     * @return a human readable string for the given instance.
     */
    static String toString(II ii) {
        final StringBuffer result = new StringBuffer();
        final String extension = ii.getExtension();

        if (extension != null) {
            result.append("Extension (UUID) : ");
            result.append(extension);
        }

        return result.toString();
    }

    /**
     * Transforms a
     * <code>org.hl7.v3.CD</code> in to a human readable string.
     *
     * @param cd the involved
     * <code>org.hl7.v3.CD</code> instance.
     * @return a human readable string for the given instance.
     */
    static String toString(CD cd) {
        final StringBuffer result = new StringBuffer();

        if (cd.getCode() != null) {
            result.append("Code : ");
            result.append(cd.getCode());
            result.append(", ");
        }

        if (cd.getCodeSystem() != null) {
            result.append("CodeSystem : ");
            result.append(cd.getCodeSystem());
            result.append(", ");
        }


        if (cd.getCodeSystemName() != null) {
            result.append("CodeSystemName : ");
            result.append(cd.getCodeSystemName());
            result.append(", ");
        }

        if (cd.getCodeSystemVersion() != null) {
            result.append("CodeSystemVersion : ");
            result.append(cd.getCodeSystemVersion());
            result.append(", ");
        }

        final int length = result.length();
        if (length > 2) {
            result.delete(length - 2, length - 1);
        }

        return result.toString();
    }

    /**
     * Transforms a
     * <code>org.hl7.v3.PN</code> in to a human readable string.
     *
     * @param pn the involved
     * <code>org.hl7.v3.PN</code> instance.
     * @return a human readable string for the given instance.
     */
    static String toString(PN pn) {
        final StringBuffer result = new StringBuffer();
        final List<Serializable> content = pn.getContent();
        if (content.isEmpty()) {
            return "No Content Found.";
        }

        for (Serializable item : content) {
            result.append(item);
            result.append(", ");
        }
        final int length = result.length();
        if (length > 2) {
            result.delete(length - 2, length - 1);
        }

        return result.toString();
    }
}
