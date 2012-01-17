/*
 * Project :iCardea
 * File : PCC10TaskFactory.java
 * Encoding : UTF-8
 * Date : Dec 21, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc10;


import java.util.EnumMap;
import java.util.Map;


/**
 * Used to build PCC10Task instance based on a given arguments lists. <br/>
 * This class is not design to be extended.
 *
 * @author mradules
 * @version 0.1
 * @since 0.1
 */
public final class PCC10TaskFactory {

    /**
     * Don't let anybody to instantiate this class.
     */
    private PCC10TaskFactory() {
        // UNIMPLEMENETD
    }

    /**
     * Builds a
     * <code>PCC10Task</code> instance based on a list of arguments.
     *
     * @param endpointURI the URI for the PCC10 or the point where the result
     * will send.
     * @param patientId the unique patient ID, it can be null but in this case
     * the
     * <code>patientNames</code> argument must be non null.
     * @param careProvisionCode the care provision code, it cn not be null.
     * @return a
     * <code>PCC10Task</code> instance based on a list of arguments.
     * @throws NullPointerException if some of the arguments are null.
     * @see PCC10Task
     */
    public static Runnable buildPCC10TaskForPatientId(String endpointURI,
            String patientId, String careProvisionCode) {
        return buildPCC10Task(endpointURI, patientId, null, careProvisionCode);
    }

    /**
     * Builds a
     * <code>PCC10Task</code> instance based on a list of arguments.
     *
     * @param endpointURI the URI for the PCC10 or the point where the result
     * will send.
     * @param patientId the unique patient ID, it can be null but in this case
     * the
     * <code>patientNames</code> argument must be non null.
     * @param careProvisionCode the care provision code, it cn not be null.
     * @return a
     * <code>PCC10Task</code> instance based on a list of arguments.
     * @throws NullPointerException if some of the arguments are null.
     * @see PCC10Task
     */
    public static Runnable buildPCC10TaskForPatientNames(String endpointURI,
            String patientNames, String careProvisionCode) {
        return buildPCC10Task(endpointURI, null, patientNames, careProvisionCode);
    }

    /**
     * Builds a
     * <code>PCC10Task</code> instance based on a list of arguments.
     *
     * @param endpointURI the URI for the PCC10 or the point where the result
     * will send.
     * @param patientId the unique patient ID, it can be null but in this case
     * the
     * <code>patientNames</code> argument must be non null.
     * @param patientNames the patient names, it can be null but in this case
     * the
     * <code>patientNames</code> argument must be non null.
     * @param careProvisionCode the care provision code, it cn not be null.
     * @return a
     * <code>PCC10Task</code> instance based on a list of arguments.
     * @throws NullPointerException if some of the arguments are null.
     * @see PCC10Task
     */
    public static Runnable buildPCC10Task(String endpointURI, String patientId,
            String patientNames, String careProvisionCode) {

        if (endpointURI == null) {
            new NullPointerException("The endpointURI arguments can not be null.");
        }

        if (patientId == null && patientNames == null) {
            new NullPointerException("The patientNames and patientId arguments can not be null.");
        }

        if (careProvisionCode == null) {
            new NullPointerException("The careProvisionCode arguments can not be null.");
        }

        final Map<PCC10TaskProperty, Object> properties =
                new EnumMap<PCC10TaskProperty, Object>(PCC10TaskProperty.class);

        properties.put(PCC10TaskProperty.END_POINT_URI, endpointURI);
        properties.put(PCC10TaskProperty.PATIENT_ID, patientId);
        properties.put(PCC10TaskProperty.PATIENT_NAME, patientNames);
        properties.put(PCC10TaskProperty.CARE_PROVISION_CODE, careProvisionCode);

        //final PCC10Task result = new PCC10Task(properties);
        return null;
    }
}
