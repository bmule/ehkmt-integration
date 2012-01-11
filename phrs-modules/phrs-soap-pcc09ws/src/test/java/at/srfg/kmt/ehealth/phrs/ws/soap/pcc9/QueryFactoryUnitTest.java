/*
 * Project :iCardea
 * File : QueryFactoryUnitTest.java
 * Encoding : UTF-8
 * Date : Apr 11, 2011
 * User : Mihai Radulescu
 */
package at.srfg.kmt.ehealth.phrs.ws.soap.pcc9;


import at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.Util;
import at.srfg.kmt.ehealth.phrs.ws.soap.pcc9.QueryFactory;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.hl7.v3.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the functionality for the
 * <code>QueryFactory</code> class. <br/> This class can not be extended.
 *
 * @author mihai
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 * @see QueryFactory
 */
public final class QueryFactoryUnitTest {

    /**
     * The Logger instance. All log messages from this class are routed through
     * this member. The Logger name space is
     * <code>at.srfg.kmt.ehealth.phrs.ws.soap.QueryFactoryUnitTest</code>.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QueryFactoryUnitTest.class);
    private final static DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * Builds a
     * <code>QueryFactoryUnitTest</code> instance.
     */
    public QueryFactoryUnitTest() {
        // UNIMPLEMENTED
    }

    /**
     * Builds an empty PCC9 request and proves if the Control Act Process Code
     * (from the new builded PCC9 request) has the right value.
     *
     * @throws JAXBException by any JAXB related exception, if this exception
     * occurs then this test fails.
     */
    @Test
    public void testBuildEmptyPCC9Request() throws JAXBException {
        final QUPCIN043100UV01 request = QueryFactory.buildEmptyPCC9Request();
        assertNotNull("The PCC9 request can not be null.", request);
        final QUPCIN043100UV01QUQIMT020001UV01ControlActProcess controlActProcess =
                request.getControlActProcess();
        final CD code = controlActProcess.getCode();
        final String codeStr = code.getCode();
        final String expectedCode = "QUPC_TE043100UV";
        assertEquals("This PCC9 message must contains the code : " + codeStr,
                expectedCode, codeStr);
    }

    /**
     * Builds a complex PCC9 request based on a list of arguments and after this
     * this it proves is the following request elements are properly stored :
     * <ul>
     * <li> Care provision code.
     * <li> Patient id.
     * <li> Patient Name (name and sun-name)
     * </ul>
     * <br/>
     * The Request contains much more elements but this test it only test the
     * upper listed ones.
     * 
     * @throws JAXBException by any JAXB related error, if this error occurs
     * then this test fails.
     */
    @Test
    public void testBuildComplexPCC9Request() throws JAXBException {

        final String careProvisionCode = "MEDLIST";
        final String careProvisionReason = "iCardea Barcelona Meeting test";
        //String careRecordTimePeriodBegin = "201101010000";
        final String careRecordTimePeriodBegin = getLastMonthDate();
        //String careRecordTimePeriodEnd =   "201101011000";
        final String careRecordTimePeriodEnd = getNowDate();
        final String clinicalStatementTimePeriodBegin = getLastMonthDate();
        //String clinicalStatementTimePeriodBegin = "201001010000";
        final String clinicalStatementTimePeriodEnd = getNowDate();
        //String clinicalStatementTimePeriodEnd = "201005011000";
        final String includeCarePlanAttachment = "true";
        final String maximumHistoryStatements = "30";
        final String patientAdministrativeGender = "M";

        final String patientBirthTime = "197903111010";

        final String patientID = "14920263490";
        final String patientName = "Mopuffus";
        final String patientSurname = "Lumpkins";

        final QUPCIN043100UV01 request =
                QueryFactory.buildPCC9Request(careProvisionCode,
                careProvisionReason,
                careRecordTimePeriodBegin,
                careRecordTimePeriodEnd,
                clinicalStatementTimePeriodBegin,
                clinicalStatementTimePeriodEnd,
                includeCarePlanAttachment,
                maximumHistoryStatements,
                patientAdministrativeGender,
                patientBirthTime,
                patientID,
                patientName,
                patientSurname);
        assertNotNull("The PCC9 request can not be null.", request);

        final QUPCIN043100UV01QUQIMT020001UV01ControlActProcess controlActProcess = request.getControlActProcess();

        final List<QUPCMT040300UV01ParameterList> parameterList = controlActProcess.getQueryByParameter().getValue().getParameterList();

        // The list can have only one element.
        assertEquals("Only one patient was expected", 1, parameterList.size());

        final QUPCMT040300UV01ParameterList param = parameterList.get(0);

        proveCareProvisionCode(param, careProvisionCode);

        provePatientIdentity(param, patientID);

        provePatientName(param, patientName, patientSurname);
    }

    /**
     * Proves if the given
     * <code>org.hl7.v3.QUPCMT040300UV01ParameterList</code> contains a
     * <code>org.hl7.v3.CD</code> with a proper value (the care provision code).
     * In a very simplistic way this method proves the provision code 
     * (presence).
     *
     * @param param the instance to be check.
     * @param expectedCarePCode the expected value for the care provision code.
     */
    private void proveCareProvisionCode(QUPCMT040300UV01ParameterList param,
            String expectedCarePCode) {
        final QUPCMT040300UV01CareProvisionCode provisionCode =
                param.getCareProvisionCode().getValue();
        final CD codeConceptDescriptor = provisionCode.getValue();
        LOGGER.debug("Care Provision Code : " + Util.toString(codeConceptDescriptor));

        final String code = codeConceptDescriptor.getCode();
        assertEquals("Wrong Care Provision Code Value", expectedCarePCode, code);
    }

    /**
     * Proves if the given
     * <code>org.hl7.v3.QUPCMT040300UV01ParameterList</code> contains a
     * <code>org.hl7.v3.II</code> with a proper value (the patient id). In a
     * very simplistic way this method proves the patient id (presence).
     *
     * @param param the instance to be check.
     * @param expectedPatientID the expected value for the patient id.
     */
    private void provePatientIdentity(QUPCMT040300UV01ParameterList param,
            String expectedPatientID) {
        final II intanceId = param.getPatientId().getValue();
        LOGGER.debug("PatientID : " + Util.toString(intanceId));

        final String patientIdExt = intanceId.getExtension();
        assertEquals("Wrong patient ID (extension) value.",
                expectedPatientID, patientIdExt);
    }

    /**
     * Proves if the given
     * <code>org.hl7.v3.QUPCMT040300UV01ParameterList</code> contains a
     * <code>org.hl7.v3.PN</code> with a proper value (the patient name and sur
     * name). In a very simplistic way this method proves the patient name and
     * surname.
     *
     * @param param the instance to be check.
     * @param expectedName the expected value for the patient name.
     * @param expectedSurname the expected value for the patient surname.
     */
    private void provePatientName(QUPCMT040300UV01ParameterList param,
            String expectedName, String expectedSurname) {
        final JAXBElement<QUPCMT040300UV01PatientName> patientNameJAXB =
                param.getPatientName();

        final QUPCMT040300UV01PatientName value = patientNameJAXB.getValue();
        final PN patientName = value.getValue();
        LOGGER.debug("Patient Names : " + Util.toString(patientName));

        final List<Serializable> names = patientName.getContent();
        // the list of names can not be null.
        assertNotNull("The list of names can not be null.", names);

        // I expect two items a name and a surname.
        assertEquals("I expect two items a name and a surname.", 2, names.size());

        final String name = names.get(0).toString();
        assertEquals("Wrong Name", expectedName, name);

        final String sunnam = names.get(1).toString();
        assertEquals("Wrong Surname", expectedSurname, sunnam);
    }

    /**
     * Builds a
     * <code>java.util.Date</code> instance located 31 days in the past and
     * returns it like String formated with the pattern : "yyyyMMddHHmmss".
     *
     * @return a
     * <code>java.util.Date</code> instance located 31 days in the past and
     * returns it like String formated with the pattern : "yyyyMMddHHmmss".
     */
    static String getLastMonthDate() {
        final Date date = new Date();
        // one day has 86400 seconds
        final long oneMonth = 1000 * 86400 * 31;
        final long lastMonth = date.getTime() - oneMonth;
        return DATE_FORMAT.format(lastMonth);
    }

    /**
     * Builds a
     * <code>java.util.Date</code> that caries the current date and returns it
     * like String formated with the pattern : "yyyyMMddHHmmss".
     *
     * @return a
     * <code>java.util.Date</code> that caries the current date and returns it
     * like String formated with the pattern : "yyyyMMddHHmmss".
     */
    static String getNowDate() {
        final Date date = new Date();
        return DATE_FORMAT.format(date);
    }
}
