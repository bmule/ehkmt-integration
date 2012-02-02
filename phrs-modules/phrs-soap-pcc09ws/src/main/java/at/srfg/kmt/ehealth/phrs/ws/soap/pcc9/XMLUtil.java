/*
 * Project :iCardea
 * File : XMLUtil.java
 * Encoding : UTF-8
 * Date : Feb 2, 2012
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Utility class able to extract some specific XML elements specific with the 
 * PCC09/PCC10 messages. <br/>
 * This class is not design to be extend.
 * 
 * @author Mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
final class XMLUtil {
    
    /**
     * The XML Tag Name for the Care Provision Code XML Element.
     */
    private static final String CARE_PROVISION_CODE_TAG_NAME = "careProvisionCode";

    /**
     * The XML Element Attribute Name for the Patient Name XML Element.
     */
    private static final String PATIENTNAME_TAG_NAME = "patientName";

    /**
     * The XML Element Attribute Name for the Patient ID XML Element.
     */
    private static final String PATIENT_ID_TAG_NAME = "patientId";

    /**
     * The XML Tag name for the Value XML Element.
     */
    private static final String VALUE_TAG_NAME = "value";

    /**
     * The XML Tag name for the Parameter List XML Element.
     */
    private static final String PARAMETER_LIST_TAG_NAME = "parameterList";

    /**
     * The XML Element Attribute Name for the Code values.
     */
    private static final String CODE_ATTRIBUTE_NAME = "code";

    /**
     * The XML Element Attribute Name for the Extension values.
     */
    private static final String EXTENSION_ATTRIBUTE_NAME = "extension";

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.XMLUtil</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(XMLUtil.class);

    /**
     * Don"t let anyone to instantiate this class.
     */
    private XMLUtil() {
        // UNIMPLEMENTED
    }

    /**
     * Extracts the Care Provision Code information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Care Provision Code information.
     *
     * @param paramter the
     * <code>org.w3c.dom.Element</code> instance that contains the Care
     * Provision Code information.
     * @return the Care Provision Code information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Care Provision Code information.
     * @throws IllegalStateException if the
     * <code>paramter</code> argument does not contains the right information
     * for the Care Provision Code.
     * @throws NullPointerException if the
     * <code>paramter</code> argument is null.
     */
    static String getCareProvisionCode(Element paramter) {
        if (paramter == null) {
            final NullPointerException exception =
                    new NullPointerException("The parameter argument can not be null");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        // TODO : use XPATH !
        final NodeList careProvisionList =
                paramter.getElementsByTagName(CARE_PROVISION_CODE_TAG_NAME);
        if (careProvisionList.getLength() == 0) {
            return null;
        }
        if (careProvisionList.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount careProvisionCode element. Only one careProvisionCode element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element careProvisionCode = (Element) careProvisionList.item(0);


        final NodeList values = careProvisionCode.getElementsByTagName(VALUE_TAG_NAME);
        if (values.getLength() == 0) {
            return null;
        }
        if (values.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount values element. Only one values element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element value = (Element) values.item(0);
        final String result = value.getAttribute(CODE_ATTRIBUTE_NAME);
        return result;
    }
    
        /**
     * Extracts the Patient ID information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Patient ID information.
     *
     * @param paramter the
     * <code>org.w3c.dom.Element</code> instance that contains the Patient ID
     * information.
     * @return the Patient ID information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Patient ID information.
     * @throws IllegalStateException if the
     * <code>paramter</code> argument does not contains the right information
     * for the Patient ID.
     * @throws NullPointerException if the
     * <code>paramter</code> argument is null.
     */
    static String getPatientId(Element paramter) {
        if (paramter == null) {
            final NullPointerException exception =
                    new NullPointerException("The parameter argument can not be null");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        // TODO : use XPATH !
        final NodeList ids =
                paramter.getElementsByTagName(PATIENT_ID_TAG_NAME);
        if (ids.getLength() == 0) {
            return null;
        }
        if (ids.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount patientId element. Only one patientId element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element careProvisionCode = (Element) ids.item(0);

        final NodeList values = careProvisionCode.getElementsByTagName(VALUE_TAG_NAME);
        if (values.getLength() == 0) {
            return null;
        }
        if (values.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount value element. Only one value element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element value = (Element) values.item(0);
        final String result = value.getAttribute(EXTENSION_ATTRIBUTE_NAME);
        return result;
    }
    
        /**
     * Extracts the Patient Name information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Patient Name information.
     *
     * @param paramter the
     * <code>org.w3c.dom.Element</code> instance that contains the Patient Name
     * information.
     * @return the Patient Name information from the given
     * <code>org.w3c.dom.Element</code> instance or null if the given element
     * does not contains any Patient Name information.
     * @throws IllegalStateException if the
     * <code>paramter</code> argument does not contains the right information
     * for the Patient Name.
     * @throws NullPointerException if the
     * <code>paramter</code> argument is null.
     */
    static String getPatientName(Element paramter) {
        if (paramter == null) {
            final NullPointerException exception =
                    new NullPointerException("The parameter argument can not be null");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }

        // TODO : use XPATH !
        final NodeList names =
                paramter.getElementsByTagName(PATIENTNAME_TAG_NAME);
        if (names.getLength() == 0) {
            return null;
        }
        if (names.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount patientName element. Only one patientName element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element careProvisionCode = (Element) names.item(0);

        final NodeList values =
                careProvisionCode.getElementsByTagName(VALUE_TAG_NAME);
        if (values.getLength() == 0) {
            return null;
        }
        if (values.getLength() != 1) {
            final IllegalStateException exception =
                    new IllegalStateException("Wrong amount values element. Only one values element expected.");
            LOGGER.error(exception.getMessage(), exception);
            throw exception;
        }
        final Element value = (Element) values.item(0);
        final String result = value.getTextContent();
        return result;
    }


}
